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
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
    private final JsonLogic jsonLogic;
    private final RuleLoader ruleLoader;
    private final NotificationDeliveryService deliveryService;

    private final ParamsCollator timeCollator = new TimeParamsCollator();
    private final ParamsCollator userCollator = new UserParamsCollator();
    private final ParamsCollator pageCollator = new PageParamsCollator();
    private final ParamsCollator prevPageCollator = new PageParamsCollator(true);
    private final ParamsCollator customCollator = new CustomParamsCollator();

    public LocalDecisioningService(ClientConfig clientConfig, TargetService targetService) {
        this.clientConfig = clientConfig;
        this.jsonLogic = new JsonLogic();
        this.ruleLoader = RuleLoaderManager.getInstance().getLoader(clientConfig);
        this.ruleLoader.start(clientConfig);
        this.deliveryService = NotificationDeliveryManager.getInstance().getService(clientConfig, targetService);
        this.deliveryService.start(clientConfig);
    }

    public void stop() {
        this.ruleLoader.stop();
        this.deliveryService.stop();
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
        Map<String, Object> meta = ruleSet.getMeta();
        if (meta == null) {
            return new LocalExecutionResult(false,
                    "Local-decisioning rule set does not contain proper information", null);
        }
        @SuppressWarnings("unchecked")
        List<String> allRemoteMboxes = (List<String>)meta.get("remoteMboxes");
        if (allRemoteMboxes == null || allRemoteMboxes.size() == 0) {
            return new LocalExecutionResult(true, null, null);
        }
        Set<String> remoteSet = new HashSet<>(allRemoteMboxes);
        Set<String> remoteMboxes = new HashSet<>();
        for (String mboxName : allMboxNames(deliveryRequest, ruleSet)) {
            if (remoteSet.contains(mboxName)) {
                remoteMboxes.add(mboxName);
            }
        }
        if (remoteMboxes.size() > 0) {
            return new LocalExecutionResult(false,
                    String.format("mboxes %s have remote activities", remoteMboxes),
                    remoteMboxes.toArray(new String[0]));
        }
        else {
            return new LocalExecutionResult(true, null, null);
        }
    }

    public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest) {
        DeliveryRequest devRequest = deliveryRequest.getDeliveryRequest();
        String requestId = devRequest.getRequestId();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        LocalDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
        if (ruleSet == null) {
            DeliveryResponse deliveryResponse = new DeliveryResponse()
                    .client(clientConfig.getClient())
                    .requestId(requestId)
                    .id(devRequest.getId())
                    .status(500);
            return new TargetDeliveryResponse(deliveryRequest, deliveryResponse,
                    500, "Local-decisioning rules not available");
        }
        List<RequestDetails> prefetchRequests = new ArrayList<>();
        List<RequestDetails> executeRequests = new ArrayList<>();
        if (deliveryRequest.getDeliveryRequest().getPrefetch() != null) {
            prefetchRequests.addAll(new ArrayList<>(devRequest.getPrefetch().getMboxes()));
            prefetchRequests.addAll(new ArrayList<>(devRequest.getPrefetch().getViews()));
            if (devRequest.getPrefetch().getPageLoad() != null) {
                prefetchRequests.add(devRequest.getPrefetch().getPageLoad());
            }
        }
        if (deliveryRequest.getDeliveryRequest().getExecute() != null) {
            executeRequests.addAll(new ArrayList<>(devRequest.getExecute().getMboxes()));
            if (devRequest.getExecute().getPageLoad() != null) {
                executeRequests.add(devRequest.getExecute().getPageLoad());
            }
        }
        PrefetchResponse prefetchResponse = new PrefetchResponse();
        ExecuteResponse executeResponse = new ExecuteResponse();
        LocalExecutionResult localResult = evaluateLocalExecution(deliveryRequest);
        int status = localResult.isAllLocal() ? 200 : 206;
        DeliveryResponse deliveryResponse = new DeliveryResponse()
                .client(clientConfig.getClient())
                .requestId(requestId)
                .id(devRequest.getId())
                .status(status);
        TargetDeliveryResponse targetResponse = new TargetDeliveryResponse(deliveryRequest,
                deliveryResponse, status,
                localResult.isAllLocal() ? "Local-decisioning response" : localResult.getReason());
        targetResponse.getResponseStatus().setRemoteMboxes(localResult.getRemoteMBoxes());
        String visitorId = getOrCreateVisitorId(deliveryRequest, targetResponse);
        for (RequestDetails details : prefetchRequests) {
            boolean handled = false;
            List<LocalDecisioningRule> rules = detailsRules(details, ruleSet);
            if (rules != null) {
                for (LocalDecisioningRule rule : rules) {
                    Map<String, Object> resultMap = executeRule(deliveryRequest,
                      details, visitorId, rule);
                    handled |= handleResult(resultMap, details, deliveryRequest, prefetchResponse, null);
                    if (handled && details instanceof MboxRequest) {
                        break;
                    }
                }
            }
            if (!handled) {
                unhandledResponse(details, prefetchResponse, null);
            }
            deliveryResponse.setPrefetch(prefetchResponse);
        }
        for (RequestDetails details : executeRequests) {
            boolean handled = false;
            List<LocalDecisioningRule> rules = detailsRules(details, ruleSet);
            if (rules != null) {
                for (LocalDecisioningRule rule : rules) {
                    Map<String, Object> resultMap = executeRule(deliveryRequest,
                      details, visitorId, rule);
                    handled |= handleResult(resultMap, details, deliveryRequest, null, executeResponse);
                    if (handled && details instanceof MboxRequest) {
                        break;
                    }
                }
            }
            if (!handled) {
                unhandledResponse(details, null, executeResponse);
            }
            deliveryResponse.setExecute(executeResponse);
        }
        if (this.clientConfig.isLogRequests()) {
            logger.debug(targetResponse.toString());
        }
        return targetResponse;
    }

    private Map<String, Object> executeRule(TargetDeliveryRequest deliveryRequest,
                                            RequestDetails details,
                                            String visitorId,
                                            LocalDecisioningRule rule) {
        Map<String, Object> condition = rule.getCondition();
        Map<String, Object> data = new HashMap<>();
        data.put("allocation", computeAllocation(visitorId, rule));
        data.putAll(timeCollator.collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("user", userCollator.collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("page", pageCollator.collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("referring", prevPageCollator.collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("mbox", customCollator.collateParams(deliveryRequest, details, rule.getMeta()));
        logger.trace("data={}", data);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String expression = mapper.writeValueAsString(condition);
            logger.trace("expression={}", expression);
            return ((Boolean) jsonLogic.apply(expression, data)) ? rule.getConsequence() : null;
        }
        catch (Exception e) {
            String message = "Hit exception while evaluating local-decisioning rule";
            logger.warn(message, e);
            TargetExceptionHandler handler = this.clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message, e));
            }
            return null;
        }
    }

    private boolean handleResult(Map<String, Object> consequence,
                                 RequestDetails details,
                                 TargetDeliveryRequest deliveryRequest,
                                 PrefetchResponse prefetchResponse,
                                 ExecuteResponse executeResponse) {
        logger.trace("consequence={}", consequence);
        if (consequence == null || consequence.isEmpty()) {
            return false;
        }
        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        if (details instanceof ViewRequest) {
            View view = mapper.convertValue(consequence, new TypeReference<View>() {
            });
            if (prefetchResponse != null) {
                List<View> views = prefetchResponse.getViews();
                if (views == null) {
                    views = new ArrayList<>();
                    prefetchResponse.setViews(views);
                }
                views.add(view);
                return true;
            }
        }
        else if (details instanceof MboxRequest) {
            MboxRequest mbox = (MboxRequest)details;
            List<Option> options = mapper.convertValue(consequence.get("options"),
                    new TypeReference<List<Option>>() {
            });
            List<Metric> metrics = mapper.convertValue(consequence.get("metrics"),
                    new TypeReference<List<Metric>>() {
            });
            if (prefetchResponse != null) {
                PrefetchMboxResponse prefetchMboxResponse = new PrefetchMboxResponse();
                prefetchMboxResponse.setName(mbox.getName());
                prefetchMboxResponse.setIndex(mbox.getIndex());
                prefetchMboxResponse.setOptions(options);
                prefetchMboxResponse.setMetrics(metrics);
                prefetchResponse.addMboxesItem(prefetchMboxResponse);
            }
            else if (executeResponse != null) {
                MboxResponse mboxResponse = new MboxResponse();
                mboxResponse.setName(mbox.getName());
                mboxResponse.setIndex(mbox.getIndex());
                mboxResponse.setOptions(options);
                mboxResponse.setMetrics(metrics);
                executeResponse.addMboxesItem(mboxResponse);
                submitNotifications(deliveryRequest, details, metrics);
            }
            return true;
        }
        else {
            List<Option> options = mapper.convertValue(consequence.get("options"),
              new TypeReference<List<Option>>() {
              });
            List<Option> allOptions = new ArrayList<>(options);
            List<Metric> metrics = mapper.convertValue(consequence.get("metrics"),
              new TypeReference<List<Metric>>() {
              });
            List<Metric> allMetrics = new ArrayList<>(metrics);
            if (executeResponse != null) {
                submitNotifications(deliveryRequest, details, metrics);
            }
            if (prefetchResponse != null) {
                PageLoadResponse pageLoad = prefetchResponse.getPageLoad();
                if (pageLoad == null) {
                    pageLoad = new PageLoadResponse();
                    prefetchResponse.setPageLoad(pageLoad);
                }
                final PageLoadResponse finalPageLoad = pageLoad;
                allOptions.forEach(finalPageLoad::addOptionsItem);
                allMetrics.forEach(finalPageLoad::addMetricsItem);
            }
            else if (executeResponse != null) {
                PageLoadResponse pageLoad = executeResponse.getPageLoad();
                if (pageLoad == null) {
                    pageLoad = new PageLoadResponse();
                    executeResponse.setPageLoad(pageLoad);
                }
                final PageLoadResponse finalPageLoad = pageLoad;
                allOptions.forEach(finalPageLoad::addOptionsItem);
                allMetrics.forEach(finalPageLoad::addMetricsItem);
            }
            return true;
        }
        return false;
    }

    private void unhandledResponse(RequestDetails details,
                                   PrefetchResponse prefetchResponse,
                                   ExecuteResponse executeResponse) {
        if (details instanceof ViewRequest) {
            prefetchResponse.addViewsItem(new View());
        }
        else if (details instanceof MboxRequest) {
            MboxRequest request = (MboxRequest)details;
            if (prefetchResponse != null) {
                PrefetchMboxResponse response = new PrefetchMboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                prefetchResponse.addMboxesItem(response);
            }
            else {
                MboxResponse response = new MboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                executeResponse.addMboxesItem(response);
            }
        }
        else {
            if (prefetchResponse != null) {
                prefetchResponse.setPageLoad(new PageLoadResponse());
            }
            else {
                executeResponse.setPageLoad(new PageLoadResponse());
            }
        }
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
            vid = UUID.randomUUID().toString();
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

    private double computeAllocation(String vid, LocalDecisioningRule rule) {
        String client = this.clientConfig.getClient();
        String activityId = rule.getMeta().get("activityId").toString();
        int index = vid.indexOf(".");
        if (index > 0) {
            vid = vid.substring(0, index);
        }
        String input = client + "." + activityId + "." + vid;
        int output = MurmurHash.hash32(input);
        return ((Math.abs(output) % 10000) / 10000D) * 100D;
    }

    private void submitNotifications(TargetDeliveryRequest deliveryRequest,
                                     RequestDetails details, List<Metric> metrics) {
        for (Metric metric : metrics) {
            Notification notification = new Notification();
            notification.setId(UUID.randomUUID().toString());
            notification.setImpressionId(UUID.randomUUID().toString());
            notification.setType(metric.getType());
            notification.setTimestamp(System.currentTimeMillis());
            notification.setTokens(Collections.singletonList(metric.getEventToken()));
            if (details instanceof ViewRequest) {
                ViewRequest vr = (ViewRequest)details;
                NotificationView view = new NotificationView();
                view.setName(vr.getName());
                view.setKey(vr.getKey());
                notification.setView(view);
            }
            else if (details instanceof MboxRequest) {
                MboxRequest mboxRequest = (MboxRequest)details;
                NotificationMbox mbox = new NotificationMbox();
                mbox.setName(mboxRequest.getName());
                notification.setMbox(mbox);
            }
            DeliveryRequest dreq = deliveryRequest.getDeliveryRequest();
            TargetDeliveryRequest notifRequest = TargetDeliveryRequest
                    .builder()
                    .locationHint(deliveryRequest.getLocationHint())
                    .sessionId(deliveryRequest.getSessionId())
                    .visitor(deliveryRequest.getVisitor())
                    .executionMode(ExecutionMode.REMOTE)
                    .requestId(UUID.randomUUID().toString())
                    .impressionId(UUID.randomUUID().toString())
                    .id(dreq.getId())
                    .experienceCloud(dreq.getExperienceCloud())
                    .context(dreq.getContext())
                    .prefetch(dreq.getPrefetch())
                    .execute(dreq.getExecute())
                    .environmentId(dreq.getEnvironmentId())
                    .qaMode(dreq.getQaMode())
                    .property(dreq.getProperty())
                    .notifications(Collections.singletonList(notification))
                    .trace(dreq.getTrace())
                    .build();
            this.deliveryService.sendNotification(notifRequest);
        }
    }

    private List<String> allMboxNames(TargetDeliveryRequest request, LocalDecisioningRuleSet ruleSet) {
        List<String> mboxNames = new ArrayList<>();
        if (request == null || ruleSet == null) {
            return mboxNames;
        }
        String globalMbox = globalMbox(ruleSet);
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

    private List<LocalDecisioningRule> detailsRules(RequestDetails details, LocalDecisioningRuleSet ruleSet) {
        if (details instanceof ViewRequest) {
            return ruleSet.getViewRules(((ViewRequest) details).getName());
        }
        else if (details instanceof MboxRequest) {
            return ruleSet.getMboxRules(((MboxRequest) details).getName());
        }
        else {
            return ruleSet.getMboxRules(globalMbox(ruleSet));
        }
    }

    private String globalMbox(LocalDecisioningRuleSet ruleSet) {
        String def = "target-global-mbox";
        if (ruleSet == null || ruleSet.getMeta() == null) {
            return def;
        }
        return (String)ruleSet.getMeta().getOrDefault("globalMbox", def);
    }
}
