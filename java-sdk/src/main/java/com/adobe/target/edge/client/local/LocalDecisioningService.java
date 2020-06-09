/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.*;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.adobe.target.edge.client.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LocalDecisioningService {

    public static class LocalExecutionResult {
        private boolean allLocal;
        private String reason;
        private String[] remoteMBoxes;

        public LocalExecutionResult(boolean allLocal, String reason, String[] remoteMBoxes) {
            this.allLocal = allLocal;
            this.reason = reason;
            this.remoteMBoxes = remoteMBoxes;
        }

        public boolean isAllLocal() {
            return allLocal;
        }

        public String getReason() {
            return reason;
        }

        public String[] getRemoteMBoxes() {
            return remoteMBoxes;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(LocalDecisioningService.class);

    private final ClientConfig clientConfig;
    private final ObjectMapper mapper;
    private final LocalDecisionHandler decisionHandler;
    private final RuleLoader ruleLoader;
    private final NotificationDeliveryService deliveryService;
    private final ClusterLocator clusterLocator;

    public LocalDecisioningService(ClientConfig clientConfig, TargetService targetService) {
        this.mapper = new JacksonObjectMapper().getMapper();
        this.clientConfig = clientConfig;
        LocalDecisioningServicesManager.LocalDecisioningServices services =
                LocalDecisioningServicesManager.getInstance().getServices(clientConfig, targetService);
        this.ruleLoader = services.getRuleLoader();
        this.ruleLoader.start(clientConfig);
        this.deliveryService = services.getNotificationDeliveryService();
        this.deliveryService.start(clientConfig);
        this.clusterLocator = services.getClusterLocator();
        this.clusterLocator.start(clientConfig, targetService);
        this.decisionHandler = new LocalDecisionHandler(clientConfig, mapper);
    }

    public void stop() {
        this.ruleLoader.stop();
        this.deliveryService.stop();
        this.clusterLocator.stop();
    }

    public void refreshRules() {
        this.ruleLoader.refresh();
    }

    /**
     * Use to determine if the given request can be fully executed locally or not and why.
     *
     * @param deliveryRequest request to examine
     * @return LocalExecutionResult
     */
    public LocalExecutionResult evaluateLocalExecution(TargetDeliveryRequest deliveryRequest) {
        if (deliveryRequest == null) {
            return new LocalExecutionResult(false,
                    "Given request cannot be null", null);
        }
        LocalDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
        if (ruleSet == null) {
            return new LocalExecutionResult(false,
                    "Local-decisioning rule set not yet available", null);
        }
        List<String> allRemoteMboxes = ruleSet.getRemoteMboxes();
        if (allRemoteMboxes == null || allRemoteMboxes.isEmpty()) {
            return new LocalExecutionResult(true, null, null);
        }
        Set<String> remoteSet = new HashSet<>(allRemoteMboxes);
        Set<String> remoteMboxes = new HashSet<>();
        for (String mboxName : allMboxNames(deliveryRequest, ruleSet)) {
            if (remoteSet.contains(mboxName)) {
                remoteMboxes.add(mboxName);
            }
        }
        if (!remoteMboxes.isEmpty()) {
            return new LocalExecutionResult(false,
                    String.format("mboxes %s have remote activities", remoteMboxes),
                    remoteMboxes.toArray(new String[0]));
        }
        else {
            return new LocalExecutionResult(true, null, null);
        }
    }

    public CompletableFuture<TargetDeliveryResponse> executeRequestAsync(TargetDeliveryRequest deliveryRequest) {
        return CompletableFuture.supplyAsync(() -> executeRequest(deliveryRequest));
    }

    public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest) {
        DeliveryRequest delivRequest = deliveryRequest.getDeliveryRequest();
        String requestId = delivRequest.getRequestId();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        LocalDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
        if (ruleSet == null) {
            DeliveryResponse deliveryResponse = new DeliveryResponse()
                    .client(clientConfig.getClient())
                    .requestId(requestId)
                    .id(delivRequest.getId())
                    .status(HttpStatus.SC_SERVICE_UNAVAILABLE);
            return new TargetDeliveryResponse(deliveryRequest, deliveryResponse,
                    HttpStatus.SC_SERVICE_UNAVAILABLE, "Local-decisioning rules not available");
        }
        List<RequestDetails> prefetchRequests = new ArrayList<>();
        List<RequestDetails> executeRequests = new ArrayList<>();
        if (delivRequest.getPrefetch() != null) {
            prefetchRequests.addAll(new ArrayList<>(delivRequest.getPrefetch().getMboxes()));
            prefetchRequests.addAll(new ArrayList<>(delivRequest.getPrefetch().getViews()));
            if (delivRequest.getPrefetch().getPageLoad() != null) {
                prefetchRequests.add(delivRequest.getPrefetch().getPageLoad());
            }
        }
        if (delivRequest.getExecute() != null) {
            executeRequests.addAll(new ArrayList<>(delivRequest.getExecute().getMboxes()));
            if (delivRequest.getExecute().getPageLoad() != null) {
                executeRequests.add(delivRequest.getExecute().getPageLoad());
            }
        }
        PrefetchResponse prefetchResponse = new PrefetchResponse();
        ExecuteResponse executeResponse = new ExecuteResponse();
        LocalExecutionResult localResult = evaluateLocalExecution(deliveryRequest);
        int status = localResult.isAllLocal() ? HttpStatus.SC_OK : HttpStatus.SC_PARTIAL_CONTENT;
        DeliveryResponse deliveryResponse = new DeliveryResponse()
                .client(clientConfig.getClient())
                .requestId(requestId)
                .id(delivRequest.getId())
                .status(status);
        TargetDeliveryResponse targetResponse = new TargetDeliveryResponse(deliveryRequest,
                deliveryResponse, status,
                localResult.isAllLocal() ? "Local-decisioning response" : localResult.getReason());
        targetResponse.getResponseStatus().setRemoteMboxes(localResult.getRemoteMBoxes());
        String visitorId = getOrCreateVisitorId(deliveryRequest, targetResponse);
        List<Notification> notifications = new ArrayList<>();
        TraceHandler traceHandler = null;
        if (delivRequest.getTrace() != null) {
            traceHandler = new TraceHandler(this.clientConfig, this.ruleLoader, this.mapper,
                    ruleSet, deliveryRequest);
        }
        for (RequestDetails details : prefetchRequests) {
            this.decisionHandler.handleDetails(deliveryRequest, traceHandler, ruleSet,
                    visitorId, details, prefetchResponse, null, null);
        }
        deliveryResponse.setPrefetch(prefetchResponse);
        for (RequestDetails details : executeRequests) {
            this.decisionHandler.handleDetails(deliveryRequest, traceHandler, ruleSet,
                    visitorId, details, null, executeResponse, notifications);
        }
        deliveryResponse.setExecute(executeResponse);
        sendNotifications(deliveryRequest, targetResponse, notifications);
        if (this.clientConfig.isLogRequests()) {
            logger.debug(targetResponse.toString());
        }
        return targetResponse;
    }

    private String getOrCreateVisitorId(TargetDeliveryRequest deliveryRequest,
                                        TargetDeliveryResponse targetResponse) {
        String vid = null;
        VisitorId visitorId = deliveryRequest.getDeliveryRequest().getId();
        if (visitorId != null) {
            vid = StringUtils.firstNonBlank(
                visitorId.getThirdPartyId(),
                firstAuthenticatedCustomerId(visitorId),
                visitorId.getMarketingCloudVisitorId(),
                visitorId.getTntId()
            );
        }
        // If no vid found in request, check response in case we have already
        // set our own tntId there in an earlier call
        if (vid == null && targetResponse.getResponse().getId() != null) {
            vid = targetResponse.getResponse().getId().getTntId();
        }
        // If vid still null, create new tntId and use that and set it in the response
        if (vid == null) {
            vid = generateTntId();
            if (visitorId == null) {
                visitorId = new VisitorId().tntId(vid);
            }
            else {
                visitorId.setTntId(vid);
            }
            targetResponse.getResponse().setId(visitorId);
        }
        return vid;
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
            if (StringUtils.isNotEmpty(customerId.getId()) &&
                    AuthenticatedState.AUTHENTICATED.equals(customerId.getAuthenticatedState())) {
                return customerId.getId();
            }
        }
        return null;
    }

    private void sendNotifications(TargetDeliveryRequest deliveryRequest,
            TargetDeliveryResponse deliveryResponse, List<Notification> notifications) {
        DeliveryRequest dreq = deliveryRequest.getDeliveryRequest();
        String locationHint = deliveryRequest.getLocationHint() != null ?
                                      deliveryRequest.getLocationHint() :
                                      this.clusterLocator.getLocationHint();
        TargetDeliveryRequest notifRequest = TargetDeliveryRequest
                .builder()
                 .locationHint(locationHint)
                 .sessionId(deliveryRequest.getSessionId())
                 .visitor(deliveryRequest.getVisitor())
                 .executionMode(ExecutionMode.REMOTE)
                 .requestId(UUID.randomUUID().toString())
                 .impressionId(UUID.randomUUID().toString())
                 .id(dreq.getId() != null ? dreq.getId() : deliveryResponse.getResponse().getId())
                 .experienceCloud(dreq.getExperienceCloud())
                 .context(dreq.getContext())
                 .environmentId(dreq.getEnvironmentId())
                 .qaMode(dreq.getQaMode())
                 .property(dreq.getProperty())
                 .notifications(notifications)
                 .trace(dreq.getTrace())
                 .build();
        this.deliveryService.sendNotification(notifRequest);
    }

    private List<String> allMboxNames(TargetDeliveryRequest request, LocalDecisioningRuleSet ruleSet) {
        List<String> mboxNames = new ArrayList<>();
        if (request == null || ruleSet == null) {
            return mboxNames;
        }
        String globalMbox = ruleSet.getGlobalMbox();
        PrefetchRequest prefetch = request.getDeliveryRequest().getPrefetch();
        if (prefetch != null) {
            if (prefetch.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(prefetch.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        ExecuteRequest execute = request.getDeliveryRequest().getExecute();
        if (execute != null) {
            if (execute.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(execute.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        return mboxNames;
    }
}
