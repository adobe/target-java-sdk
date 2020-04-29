package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.LocalDecisioningRule;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LocalDecisionHandler {

    private static final Logger logger = LoggerFactory.getLogger(LocalDecisionHandler.class);


    private final ClientConfig clientConfig;
    private final ObjectMapper mapper;

    private final JsonLogic jsonLogic = new JsonLogic();
    private final ParamsCollator timeCollator = new TimeParamsCollator();
    private final ParamsCollator userCollator = new UserParamsCollator();
    private final ParamsCollator pageCollator = new PageParamsCollator();
    private final ParamsCollator prevPageCollator = new PageParamsCollator(true);
    private final ParamsCollator customCollator = new CustomParamsCollator();

    public LocalDecisionHandler(ClientConfig clientConfig, ObjectMapper mapper) {
        this.clientConfig = clientConfig;
        this.mapper = mapper;
    }

    public void handleDetails(TargetDeliveryRequest deliveryRequest,
            TraceHandler traceHandler,
            LocalDecisioningRuleSet ruleSet,
            String visitorId,
            RequestDetails details,
            PrefetchResponse prefetchResponse,
            ExecuteResponse executeResponse,
            List<Notification> notifications) {
        if (traceHandler != null) {
            traceHandler.updateRequest(deliveryRequest, details,
                    executeResponse != null);
        }
        List<LocalDecisioningRule> rules = detailsRules(details, ruleSet);
        boolean handled = false;
        if (rules != null) {
            for (LocalDecisioningRule rule : rules) {
                Map<String, Object> resultMap = executeRule(deliveryRequest,
                        details, visitorId, rule, traceHandler);
                handled |= handleResult(resultMap, rule, details, prefetchResponse,
                        executeResponse, notifications, traceHandler);
                if (handled && details instanceof MboxRequest) {
                    break;
                }
            }
        }
        if (!handled) {
            unhandledResponse(details, prefetchResponse, executeResponse, traceHandler);
        }
    }

    private Map<String, Object> executeRule(TargetDeliveryRequest deliveryRequest,
            RequestDetails details,
            String visitorId,
            LocalDecisioningRule rule,
            TraceHandler traceHandler) {
        Map<String, Object> condition = rule.getCondition();
        Map<String, Object> context = new HashMap<>();
        context.put("allocation", computeAllocation(visitorId, rule));
        context.putAll(timeCollator.collateParams(deliveryRequest, details));
        context.put("user", userCollator.collateParams(deliveryRequest, details));
        context.put("page", pageCollator.collateParams(deliveryRequest, details));
        context.put("referring", prevPageCollator.collateParams(deliveryRequest, details));
        context.put("mbox", customCollator.collateParams(deliveryRequest, details));
        logger.trace("details={}, context={}", details, context);
        try {
            String expression = this.mapper.writeValueAsString(condition);
            logger.trace("expression={}", expression);
            boolean matched = JsonLogic.truthy(jsonLogic.apply(expression, context));
            if (traceHandler != null) {
                traceHandler.addCampaign(rule, context, matched);
            }
            return matched ? rule.getConsequence() : null;
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
            LocalDecisioningRule rule,
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
                views.add(view);
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
                mboxResponse.setOptions(options);
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
                    options.forEach(pageLoad::addOptionsItem);
                    metrics.forEach(pageLoad::addMetricsItem);
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
            if (prefetchResponse != null) {
                PrefetchMboxResponse response = new PrefetchMboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                response.setTrace(trace);
                prefetchResponse.addMboxesItem(response);
            }
            else {
                MboxResponse response = new MboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                response.setTrace(trace);
                executeResponse.addMboxesItem(response);
            }
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

    private List<LocalDecisioningRule> detailsRules(RequestDetails details, LocalDecisioningRuleSet ruleSet) {
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

    private Map<String, Object> currentTrace(TraceHandler traceHandler) {
        if (traceHandler == null) {
            return null;
        }
        return traceHandler.getCurrentTrace();
    }
}
