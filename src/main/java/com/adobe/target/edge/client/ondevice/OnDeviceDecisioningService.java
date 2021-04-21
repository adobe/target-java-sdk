/*
 * Copyright 2021 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.model.*;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningEvaluation;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.ondevice.client.geo.DefaultGeoClient;
import com.adobe.target.edge.client.ondevice.client.geo.GeoClient;
import com.adobe.target.edge.client.ondevice.collator.CustomParamsCollator;
import com.adobe.target.edge.client.ondevice.collator.GeoParamsCollator;
import com.adobe.target.edge.client.ondevice.collator.PageParamsCollator;
import com.adobe.target.edge.client.ondevice.collator.ParamsCollator;
import com.adobe.target.edge.client.ondevice.collator.TimeParamsCollator;
import com.adobe.target.edge.client.ondevice.collator.UserParamsCollator;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.adobe.target.edge.client.utils.StringUtils;
import com.adobe.target.edge.client.utils.TimingTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnDeviceDecisioningService {

  private static final Logger logger = LoggerFactory.getLogger(OnDeviceDecisioningService.class);

  public static final String CONTEXT_KEY_USER = "user";
  public static final String CONTEXT_KEY_GEO = "geo";
  public static final String CONTEXT_KEY_PAGE = "page";
  public static final String CONTEXT_KEY_REFERRING = "referring";
  public static final String CONTEXT_KEY_CUSTOM = "mbox";
  public static final String TIMING_EXECUTE_REQUEST = "timing_execute_request";

  private static final Map<String, ParamsCollator> REQUEST_PARAMS_COLLATORS =
      new HashMap<String, ParamsCollator>() {
        {
          put(CONTEXT_KEY_USER, new UserParamsCollator());
          put(CONTEXT_KEY_GEO, new GeoParamsCollator());
        }
      };

  private static final Map<String, ParamsCollator> DETAILS_PARAMS_COLLATORS =
      new HashMap<String, ParamsCollator>() {
        {
          put(CONTEXT_KEY_PAGE, new PageParamsCollator());
          put(CONTEXT_KEY_REFERRING, new PageParamsCollator(true));
          put(CONTEXT_KEY_CUSTOM, new CustomParamsCollator());
        }
      };

  private final ParamsCollator timeParamsCollator = new TimeParamsCollator();
  private final ClientConfig clientConfig;
  private final ObjectMapper mapper;
  private final RuleLoader ruleLoader;
  private final NotificationDeliveryService deliveryService;
  private final ClusterLocator clusterLocator;
  private final OnDeviceDecisioningDetailsExecutor decisionHandler;
  private final OnDeviceDecisioningEvaluator onDeviceDecisioningEvaluator;
  private final GeoClient geoClient;

  public OnDeviceDecisioningService(ClientConfig clientConfig, TargetService targetService) {
    this.mapper = new JacksonObjectMapper().getMapper();
    this.clientConfig = clientConfig;
    OnDeviceDecisioningServicesManager.OnDeviceDecisioningServices services =
        OnDeviceDecisioningServicesManager.getInstance().getServices(clientConfig, targetService);
    this.ruleLoader = services.getRuleLoader();
    this.ruleLoader.start(clientConfig);
    this.deliveryService = services.getNotificationDeliveryService();
    this.clusterLocator = services.getClusterLocator();
    this.clusterLocator.start(clientConfig, targetService);
    this.decisionHandler = new OnDeviceDecisioningDetailsExecutor(clientConfig, mapper);
    this.onDeviceDecisioningEvaluator = new OnDeviceDecisioningEvaluator(this.ruleLoader);
    this.geoClient = new DefaultGeoClient();
    this.geoClient.start(clientConfig);
  }

  public void stop() {
    this.ruleLoader.stop();
    this.clusterLocator.stop();
  }

  public void refreshRules() {
    this.ruleLoader.refresh();
  }

  public OnDeviceDecisioningEvaluation evaluateLocalExecution(
      TargetDeliveryRequest deliveryRequest) {
    return this.onDeviceDecisioningEvaluator.evaluateLocalExecution(deliveryRequest);
  }

  public CompletableFuture<TargetDeliveryResponse> executeRequestAsync(
      TargetDeliveryRequest deliveryRequest) {
    return CompletableFuture.supplyAsync(() -> executeRequest(deliveryRequest));
  }

  public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest) {
    TimingTool timer = new TimingTool();
    timer.timeStart(TIMING_EXECUTE_REQUEST);

    DeliveryRequest delivRequest = deliveryRequest.getDeliveryRequest();
    String requestId = delivRequest.getRequestId();
    if (requestId == null) {
      requestId = UUID.randomUUID().toString();
    }

    OnDeviceDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
    if (ruleSet == null) {
      DeliveryResponse deliveryResponse =
          new DeliveryResponse()
              .client(clientConfig.getClient())
              .requestId(requestId)
              .id(delivRequest.getId())
              .status(HttpStatus.SC_SERVICE_UNAVAILABLE);
      return new TargetDeliveryResponse(
          deliveryRequest,
          deliveryResponse,
          HttpStatus.SC_SERVICE_UNAVAILABLE,
          "Local-decisioning rules not available");
    }

    Map<String, Object> requestContext =
        new HashMap<>(timeParamsCollator.collateParams(deliveryRequest, null));
    geoLookupIfNeeded(deliveryRequest, ruleSet.isGeoTargetingEnabled());
    collateParams(requestContext, REQUEST_PARAMS_COLLATORS, deliveryRequest, null);

    Telemetry telemetry = new Telemetry();

    TraceHandler traceHandler = null;
    if (delivRequest.getTrace() != null) {
      traceHandler =
          new TraceHandler(
              this.clientConfig, this.ruleLoader, this.mapper, ruleSet, deliveryRequest);
    }
    Set<String> responseTokens = new HashSet<>(ruleSet.getResponseTokens());

    TargetDeliveryResponse targetResponse = buildDeliveryResponse(deliveryRequest, requestId);
    String visitorId = getOrCreateVisitorId(deliveryRequest, targetResponse);

    List<RequestDetails> prefetchRequests = detailsFromPrefetch(delivRequest);
    handleDetails(
        prefetchRequests,
        requestContext,
        deliveryRequest,
        visitorId,
        responseTokens,
        traceHandler,
        ruleSet,
        targetResponse.getResponse().getPrefetch(),
        null,
        null);

    List<RequestDetails> executeRequests = detailsFromExecute(delivRequest);
    List<Notification> notifications = new ArrayList<>();

    handleDetails(
        executeRequests,
        requestContext,
        deliveryRequest,
        visitorId,
        responseTokens,
        traceHandler,
        ruleSet,
        null,
        targetResponse.getResponse().getExecute(),
        notifications);

    TelemetryEntry telemetryEntry =
        createTelemetryEntry(
            deliveryRequest, targetResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));
    telemetry.addTelemetryEntry(telemetryEntry);

    sendNotifications(deliveryRequest, targetResponse, notifications, telemetry);

    if (this.clientConfig.isLogRequests()) {
      logger.debug(targetResponse.toString());
    }

    return targetResponse;
  }

  private List<RequestDetails> detailsFromPrefetch(DeliveryRequest deliveryRequest) {
    if (deliveryRequest.getPrefetch() != null) {
      List<RequestDetails> prefetchRequests = new ArrayList<>();
      prefetchRequests.addAll(new ArrayList<>(deliveryRequest.getPrefetch().getMboxes()));
      prefetchRequests.addAll(new ArrayList<>(deliveryRequest.getPrefetch().getViews()));
      if (deliveryRequest.getPrefetch().getPageLoad() != null) {
        prefetchRequests.add(deliveryRequest.getPrefetch().getPageLoad());
      }
      return prefetchRequests;
    }
    return Collections.emptyList();
  }

  private List<RequestDetails> detailsFromExecute(DeliveryRequest deliveryRequest) {
    if (deliveryRequest.getExecute() != null) {
      List<RequestDetails> executeRequests =
          new ArrayList<>(new ArrayList<>(deliveryRequest.getExecute().getMboxes()));
      if (deliveryRequest.getExecute().getPageLoad() != null) {
        executeRequests.add(deliveryRequest.getExecute().getPageLoad());
      }
      return executeRequests;
    }
    return Collections.emptyList();
  }

  private TargetDeliveryResponse buildDeliveryResponse(
      TargetDeliveryRequest deliveryRequest, String requestId) {
    OnDeviceDecisioningEvaluation localEvaluation = evaluateLocalExecution(deliveryRequest);
    int status = localEvaluation.isAllLocal() ? HttpStatus.SC_OK : HttpStatus.SC_PARTIAL_CONTENT;
    DeliveryResponse deliveryResponse =
        new DeliveryResponse()
            .client(clientConfig.getClient())
            .requestId(requestId)
            .id(deliveryRequest.getDeliveryRequest().getId())
            .status(status);
    PrefetchResponse prefetchResponse = new PrefetchResponse();
    ExecuteResponse executeResponse = new ExecuteResponse();
    deliveryResponse.setPrefetch(prefetchResponse);
    deliveryResponse.setExecute(executeResponse);
    TargetDeliveryResponse targetResponse =
        new TargetDeliveryResponse(
            deliveryRequest,
            deliveryResponse,
            status,
            localEvaluation.isAllLocal()
                ? "Local-decisioning response"
                : localEvaluation.getReason());
    ResponseStatus responseStatus = targetResponse.getResponseStatus();
    responseStatus.setGlobalMbox(localEvaluation.getGlobalMbox());
    responseStatus.setRemoteMboxes(localEvaluation.getRemoteMBoxes());
    responseStatus.setRemoteViews(localEvaluation.getRemoteViews());
    return targetResponse;
  }

  private String getOrCreateVisitorId(
      TargetDeliveryRequest deliveryRequest, TargetDeliveryResponse targetResponse) {
    String vid = null;
    VisitorId visitorId = deliveryRequest.getDeliveryRequest().getId();
    if (visitorId != null) {
      vid =
          StringUtils.firstNonBlank(
              visitorId.getMarketingCloudVisitorId(),
              removeLocationHint(visitorId.getTntId()),
              visitorId.getThirdPartyId());
    }
    // If no vid found in request, check response in case we have already
    // set our own tntId there in an earlier call
    if (vid == null && targetResponse.getResponse().getId() != null) {
      vid = removeLocationHint(targetResponse.getResponse().getId().getTntId());
    }
    if (vid != null) {
      return vid;
    }
    // If vid still null, create new tntId and use that and set it in the response
    String newTntId = generateTntId();
    if (visitorId == null) {
      visitorId = new VisitorId().tntId(newTntId);
    } else {
      visitorId.setTntId(newTntId);
    }
    targetResponse.getResponse().setId(visitorId);
    return removeLocationHint(newTntId);
  }

  private static String removeLocationHint(String tntId) {
    if (StringUtils.isEmpty(tntId)) {
      return tntId;
    }
    int index = tntId.indexOf(".");
    return index <= 0 ? tntId : tntId.substring(0, index);
  }

  private String generateTntId() {
    String tntId = UUID.randomUUID().toString();
    String locationHint = this.clusterLocator.getLocationHint();
    if (locationHint != null) {
      tntId += "." + CookieUtils.locationHintToNodeDetails(locationHint);
    }
    return tntId;
  }

  private String firstAuthenticatedCustomerId(VisitorId visitorId) {
    if (visitorId == null) {
      return null;
    }
    List<CustomerId> customerIds = visitorId.getCustomerIds();
    if (customerIds == null) {
      return null;
    }
    for (CustomerId customerId : customerIds) {
      if (StringUtils.isNotEmpty(customerId.getId())
          && AuthenticatedState.AUTHENTICATED.equals(customerId.getAuthenticatedState())) {
        return customerId.getId();
      }
    }
    return null;
  }

  private void geoLookupIfNeeded(TargetDeliveryRequest deliveryRequest, boolean doGeoLookup) {
    if (!doGeoLookup) {
      return;
    }
    Context context = deliveryRequest.getDeliveryRequest().getContext();
    if (context != null) {
      Geo geo = context.getGeo();
      if (geo != null) {
        if (StringUtils.isNotEmpty(geo.getIpAddress())
            && StringUtils.isEmpty(geo.getCity())
            && StringUtils.isEmpty(geo.getStateCode())
            && StringUtils.isEmpty(geo.getCountryCode())
            && geo.getLatitude() == null
            && geo.getLongitude() == null) {
          Geo resolvedGeo = this.geoClient.lookupGeo(geo.getIpAddress());
          deliveryRequest.getDeliveryRequest().getContext().setGeo(resolvedGeo);
        }
      }
    }
  }

  private void collateParams(
      Map<String, Object> localContext,
      Map<String, ParamsCollator> paramsCollator,
      TargetDeliveryRequest deliveryRequest,
      RequestDetails requestDetails) {
    for (Map.Entry<String, ParamsCollator> entry : paramsCollator.entrySet()) {
      localContext.put(
          entry.getKey(), entry.getValue().collateParams(deliveryRequest, requestDetails));
    }
  }

  private void handleDetails(
      List<RequestDetails> detailsList,
      Map<String, Object> requestContext,
      TargetDeliveryRequest deliveryRequest,
      String visitorId,
      Set<String> responseTokens,
      TraceHandler traceHandler,
      OnDeviceDecisioningRuleSet ruleSet,
      PrefetchResponse prefetchResponse,
      ExecuteResponse executeResponse,
      List<Notification> notifications) {
    for (RequestDetails details : detailsList) {
      Map<String, Object> detailsContext = new HashMap<>(requestContext);
      collateParams(detailsContext, DETAILS_PARAMS_COLLATORS, deliveryRequest, details);
      this.decisionHandler.executeDetails(
          deliveryRequest,
          detailsContext,
          visitorId,
          responseTokens,
          traceHandler,
          ruleSet,
          details,
          prefetchResponse,
          executeResponse,
          notifications);
    }
  }

  private TelemetryEntry createTelemetryEntry(
      TargetDeliveryRequest targetDeliveryRequest,
      TargetDeliveryResponse targetDeliveryResponse,
      double executionTime) {
    TelemetryFeatures telemetryFeatures =
        new TelemetryFeatures().decisioningMethod(getDecisioningMethod(targetDeliveryRequest));

    return new TelemetryEntry()
        .requestId(targetDeliveryResponse.getResponse().getRequestId())
        .features(telemetryFeatures)
        .execution(executionTime)
        .timestamp(System.currentTimeMillis());
  }

  private void sendNotifications(
      TargetDeliveryRequest deliveryRequest,
      TargetDeliveryResponse deliveryResponse,
      List<Notification> notifications,
      Telemetry telemetry) {
    boolean noNotifications = notifications == null || notifications.isEmpty();
    boolean noTelemetry =
        !clientConfig.isTelemetryEnabled()
            || (telemetry == null || telemetry.getEntries().isEmpty());

    if (noNotifications && noTelemetry) {
      return;
    }

    DeliveryRequest dreq = deliveryRequest.getDeliveryRequest();
    String locationHint =
        deliveryRequest.getLocationHint() != null
            ? deliveryRequest.getLocationHint()
            : this.clusterLocator.getLocationHint();
    TargetDeliveryRequest notifRequest =
        TargetDeliveryRequest.builder()
            .locationHint(locationHint)
            .sessionId(deliveryRequest.getSessionId())
            .visitor(deliveryRequest.getVisitor())
            .decisioningMethod(DecisioningMethod.SERVER_SIDE)
            .requestId(UUID.randomUUID().toString())
            .impressionId(UUID.randomUUID().toString())
            .id(dreq.getId() != null ? dreq.getId() : deliveryResponse.getResponse().getId())
            .experienceCloud(dreq.getExperienceCloud())
            .context(dreq.getContext())
            .environmentId(dreq.getEnvironmentId())
            .qaMode(dreq.getQaMode())
            .property(dreq.getProperty())
            .notifications(notifications)
            .telemetry(clientConfig.isTelemetryEnabled() ? telemetry : null)
            .trace(dreq.getTrace())
            .build();
    this.deliveryService.sendNotification(notifRequest);
  }

  private DecisioningMethod getDecisioningMethod(TargetDeliveryRequest request) {
    DecisioningMethod requestDecisioning = request.getDecisioningMethod();

    if (requestDecisioning != null) {
      return requestDecisioning;
    }

    DecisioningMethod configDecisioning = clientConfig.getDefaultDecisioningMethod();

    if (configDecisioning != null) {
      return configDecisioning;
    }

    return DecisioningMethod.SERVER_SIDE;
  }
}
