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
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRule;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class OnDeviceDecisioningDetailsExecutor {

    private static final Logger logger = LoggerFactory.getLogger(OnDeviceDecisioningDetailsExecutor.class);

    private final ClientConfig clientConfig;
    private final ObjectMapper mapper;
    private final OnDeviceDecisioningRuleExecutor ruleExecutor;

    public OnDeviceDecisioningDetailsExecutor(ClientConfig clientConfig, ObjectMapper mapper) {
        this.clientConfig = clientConfig;
        this.mapper = mapper;
        this.ruleExecutor = new OnDeviceDecisioningRuleExecutor(clientConfig, mapper);
    }

    public void executeDetails(TargetDeliveryRequest deliveryRequest,
            Set<String> onDeviceAllMatchingRulesMboxes,
            Map<String, Object> localContext,
            String visitorId,
            Set<String> responseTokens,
            TraceHandler traceHandler,
            OnDeviceDecisioningRuleSet ruleSet,
            RequestDetails details,
            PrefetchResponse prefetchResponse,
            ExecuteResponse executeResponse,
            List<Notification> notifications) {
        if (traceHandler != null) {
            traceHandler.updateRequest(deliveryRequest, details,
                    executeResponse != null);
        }
        List<OnDeviceDecisioningRule> rules = detailsRules(details, ruleSet);
        String propertyToken = requestPropertyToken(deliveryRequest);
        boolean handledAtLeastOnce = false;
        Set<String> skipKeySet = new HashSet<>();
        if (rules != null) {
            for (OnDeviceDecisioningRule rule : rules) {
                if (propertyTokenMismatch(rule.getPropertyTokens(), propertyToken)) {
                    continue;
                }
                String ruleKey = rule.getRuleKey();
                if (ruleKey != null && skipKeySet.contains(ruleKey)) {
                    continue;
                }
                Map<String, Object> consequence = this.ruleExecutor.executeRule(localContext,
                        details, visitorId, rule, responseTokens, traceHandler);
                boolean handled = handleResult(consequence, rule, details, prefetchResponse,
                        executeResponse, notifications, traceHandler);
                if (handled) {
                    handledAtLeastOnce = true;
                    if (details instanceof MboxRequest) {
                        if (!onDeviceAllMatchingRulesMboxes.contains(((MboxRequest) details).getName())) {
                            break;
                        }
                    }
                    else if (ruleKey != null) {
                        skipKeySet.add(ruleKey);
                    }
                }
            }
        }
        if (!handledAtLeastOnce) {
            unhandledResponse(details, prefetchResponse, executeResponse, traceHandler);
        }
    }

    private boolean handleResult(Map<String, Object> consequence,
            OnDeviceDecisioningRule rule,
            RequestDetails details,
            PrefetchResponse prefetchResponse,
            ExecuteResponse executeResponse,
            List<Notification> notifications,
            TraceHandler traceHandler) {
        logger.trace("consequence={}", consequence);
        if (consequence == null || consequence.isEmpty()) {
            return false;
        }
        if (details instanceof ViewRequest) {
            View view = this.mapper.convertValue(consequence, new TypeReference<View>() {
            });
            view.setTrace(currentTrace(traceHandler));
            if (prefetchResponse != null) {
                List<View> views = prefetchResponse.getViews();
                if (views == null) {
                    views = new ArrayList<>();
                    prefetchResponse.setViews(views);
                }
                View foundView = null;
                for (View existing : views) {
                    if (existing.getName().equals(view.getName())) {
                        foundView = existing;
                        break;
                    }
                }
                if (foundView == null) {
                    views.add(view);
                }
                else {
                    if (view.getOptions() != null) {
                        List<Option> foundOptions = foundView.getOptions();
                        if (foundOptions == null) {
                            foundOptions = new ArrayList<>();
                            foundView.setOptions(foundOptions);
                        }
                        foundOptions.addAll(view.getOptions());
                    }
                    if (view.getMetrics() != null) {
                        List<Metric> foundMetrics = foundView.getMetrics();
                        if (foundMetrics == null) {
                            foundMetrics = new ArrayList<>();
                            foundView.setMetrics(foundMetrics);
                        }
                        foundMetrics.addAll(view.getMetrics());
                    }
                }
                return true;
            }
            return false;
        }
        else {
            List<Option> options = this.mapper.convertValue(consequence.get("options"),
                    new TypeReference<List<Option>>() {
                    });
            List<Metric> metrics = this.mapper.convertValue(consequence.get("metrics"),
                    new TypeReference<List<Metric>>() {
                    });
            if (executeResponse != null) {
                Notification notification = createNotification(details, options);
                if (traceHandler != null) {
                    traceHandler.addNotification(rule, notification);
                }
                notifications.add(notification);
            }
            if (details instanceof MboxRequest) {
                MboxRequest mbox = (MboxRequest) details;
                MboxResponse mboxResponse;
                if (prefetchResponse != null) {
                    mboxResponse = new PrefetchMboxResponse();
                }
                else {
                    mboxResponse = new MboxResponse();
                }
                mboxResponse.setName(mbox.getName());
                mboxResponse.setIndex(mbox.getIndex());
                for (Option option : options) {
                    if (executeResponse != null) {
                        option.setEventToken(null);
                    }
                    mboxResponse.addOptionsItem(option);
                }
                mboxResponse.setMetrics(metrics);
                mboxResponse.setTrace(currentTrace(traceHandler));
                if (prefetchResponse != null) {
                    prefetchResponse.addMboxesItem((PrefetchMboxResponse) mboxResponse);
                    return true;
                }
                if (executeResponse != null) {
                    executeResponse.addMboxesItem(mboxResponse);
                    return true;
                }
                return false;
            }
            else {
                PageLoadResponse pageLoad = null;
                if (prefetchResponse != null) {
                    pageLoad = prefetchResponse.getPageLoad();
                    if (pageLoad == null) {
                        pageLoad = new PageLoadResponse();
                        prefetchResponse.setPageLoad(pageLoad);
                    }
                } else if (executeResponse != null) {
                    pageLoad = executeResponse.getPageLoad();
                    if (pageLoad == null) {
                        pageLoad = new PageLoadResponse();
                        executeResponse.setPageLoad(pageLoad);
                    }
                }
                if (pageLoad != null) {
                    pageLoad.setTrace(currentTrace(traceHandler));
                    for (Option option : options) {
                        if (executeResponse != null) {
                            option.setEventToken(null);
                        }
                        pageLoad.addOptionsItem(option);
                    }
                    for (Metric metric : metrics) {
                        if (pageLoad.getMetrics() == null ||
                                !pageLoad.getMetrics().contains(metric)) {
                            pageLoad.addMetricsItem(metric);
                        }
                    }
                    return true;
                }
                return false;
            }
        }
    }

    private void unhandledResponse(RequestDetails details,
            PrefetchResponse prefetchResponse,
            ExecuteResponse executeResponse,
            TraceHandler traceHandler) {
        Map<String, Object> trace = null;
        if (traceHandler != null) {
            trace = new HashMap<>(traceHandler.getCurrentTrace());
        }
        if (details instanceof ViewRequest) {
            View view = new View();
            view.setTrace(trace);
            prefetchResponse.addViewsItem(view);
        }
        else if (details instanceof MboxRequest) {
            MboxRequest request = (MboxRequest)details;
            MboxResponse response;
            if (prefetchResponse != null) {
                response = new PrefetchMboxResponse();
                prefetchResponse.addMboxesItem((PrefetchMboxResponse)response);
            }
            else {
                response = new MboxResponse();
                executeResponse.addMboxesItem(response);
            }
            response.setIndex(request.getIndex());
            response.setName(request.getName());
            response.setTrace(trace);
        }
        else {
            PageLoadResponse response = new PageLoadResponse();
            response.setTrace(trace);
            if (prefetchResponse != null) {
                prefetchResponse.setPageLoad(response);
            }
            else {
                executeResponse.setPageLoad(response);
            }
        }
    }

    private Notification createNotification(RequestDetails details, List<Option> options) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setImpressionId(UUID.randomUUID().toString());
        notification.setType(MetricType.DISPLAY);
        notification.setTimestamp(System.currentTimeMillis());
        notification.setTokens(options.stream().map(Option::getEventToken).collect(Collectors.toList()));
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
        return notification;
    }

    private List<OnDeviceDecisioningRule> detailsRules(RequestDetails details, OnDeviceDecisioningRuleSet ruleSet) {
        if (details instanceof ViewRequest) {
            ViewRequest request = (ViewRequest) details;
            String name = request.getName();
            if (name != null) {
                return ruleSet.getRules().getViews().get(name);
            }
            else {
                return ruleSet.getRules().getViews().values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }
        else if (details instanceof MboxRequest) {
            return ruleSet.getRules().getMboxes().get(((MboxRequest) details).getName());
        }
        else {
            return ruleSet.getRules().getMboxes().get(ruleSet.getGlobalMbox());
        }
    }

    private String requestPropertyToken(TargetDeliveryRequest deliveryRequest) {
        Property property = deliveryRequest.getDeliveryRequest().getProperty();
        if (property == null) {
            return null;
        }
        return property.getToken();
    }

    private boolean propertyTokenMismatch(List<String> rulePropertyTokens, String propertyToken) {
        if (StringUtils.isEmpty(propertyToken)) {
            return false;
        }
        if (rulePropertyTokens == null || rulePropertyTokens.isEmpty()) {
            return false;
        }
        return !rulePropertyTokens.contains(propertyToken);
    }

    private Map<String, Object> currentTrace(TraceHandler traceHandler) {
        if (traceHandler == null) {
            return null;
        }
        return traceHandler.getCurrentTrace();
    }

}
