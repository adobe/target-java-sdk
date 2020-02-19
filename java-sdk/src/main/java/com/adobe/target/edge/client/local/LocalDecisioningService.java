package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.LocalDecisioningRule;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalDecisioningService {

    private static final Logger logger = LoggerFactory.getLogger(LocalDecisioningService.class);

    private final ClientConfig clientConfig;
    private final JsonLogic jsonLogic;

    public LocalDecisioningService(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.jsonLogic = new JsonLogic();
    }

    public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest, LocalDecisioningRuleSet ruleSet) {
        Map<String, MboxRequest> executeMboxes = new HashMap<>();
        if (deliveryRequest.getDeliveryRequest().getExecute() != null) {
            executeMboxes.putAll(deliveryRequest.getDeliveryRequest().getExecute().getMboxes().stream().collect(Collectors.toMap(MboxRequest::getName, Function.identity())));
        }
        Map<String, MboxRequest> prefetchMboxes = new HashMap<>();
        if (deliveryRequest.getDeliveryRequest().getPrefetch() != null) {
            prefetchMboxes.putAll(deliveryRequest.getDeliveryRequest().getPrefetch().getMboxes().stream().collect(Collectors.toMap(MboxRequest::getName, Function.identity())));
        }
        PrefetchResponse prefetchResponse = new PrefetchResponse();
        ExecuteResponse executeResponse = new ExecuteResponse();
        String requestId = deliveryRequest.getDeliveryRequest().getRequestId();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        DeliveryResponse deliveryResponse = new DeliveryResponse()
                .client(clientConfig.getClient())
                .requestId(requestId)
                .id(deliveryRequest.getDeliveryRequest().getId())
                .status(200);
        TargetDeliveryResponse targetResponse = new TargetDeliveryResponse(deliveryRequest, deliveryResponse, 200, "Local-decisioning response");
        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        List<LocalDecisioningRule> rules = ruleSet.getRules();
        if (rules != null) {
            for (LocalDecisioningRule rule : rules) {
                Map<String, Object> resultMap = executeRule(deliveryRequest, targetResponse, rule);
                logger.info("resultMap=" + resultMap);
                if (resultMap == null) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> mboxArray = (List<Map<String, Object>>) resultMap.get("mboxes");
                if (mboxArray != null) {
                    for (Map<String, Object> mboxMap : mboxArray) {
                        String mbox = (String) mboxMap.get("name");
                        List<Option> options = mapper.convertValue(mboxMap.get("options"), new TypeReference<List<Option>>() {
                        });
                        if (executeMboxes.containsKey(mbox)) {
                            MboxRequest mboxRequest = executeMboxes.get(mbox);
                            MboxResponse mboxResponse = new MboxResponse()
                                    .name(mbox)
                                    .index(mboxRequest.getIndex())
                                    .options(options);
                            executeResponse.addMboxesItem(mboxResponse);
                            deliveryResponse.setExecute(executeResponse);
                        }
                        if (prefetchMboxes.containsKey(mbox)) {
                            MboxRequest mboxRequest = prefetchMboxes.get(mbox);
                            PrefetchMboxResponse prefetchMboxResponse = new PrefetchMboxResponse();
                            prefetchMboxResponse.setName(mbox);
                            prefetchMboxResponse.setIndex(mboxRequest.getIndex());
                            prefetchMboxResponse.setOptions(options);
                            prefetchResponse.addMboxesItem(prefetchMboxResponse);
                            deliveryResponse.setPrefetch(prefetchResponse);
                        }
                    }
                }
            }
        }
        return targetResponse;
    }

    private Map<String, Object> executeRule(TargetDeliveryRequest deliveryRequest, TargetDeliveryResponse targetResponse, LocalDecisioningRule rule) {
        Map<String, Object> condition = rule.getCondition();
        Map<String, Object> data = new HashMap<>();
        String vid = getOrCreateVisitorId(deliveryRequest, targetResponse);
        data.put("allocation", computeAllocation(vid, rule));
        addTimeParams(data);
        data.put("user", new UserParamsCollator().collateParams(deliveryRequest));
        data.put("page", new PageParamsCollator().collateParams(deliveryRequest));
        data.put("mbox", new CustomParamsCollator().collateParams(deliveryRequest));
        logger.info("data="+data);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String expression = mapper.writeValueAsString(condition);
            return ((Boolean) jsonLogic.apply(expression, data)) ? rule.getConsequence() : null;
        }
        catch (Exception e) {
            logger.warn("Hit exception while evaluating local-decisioning rule", e);
            return null;
        }
    }

    private String getOrCreateVisitorId(TargetDeliveryRequest deliveryRequest, TargetDeliveryResponse targetResponse) {
        String vid = null;
        VisitorId visitorId = deliveryRequest.getDeliveryRequest().getId();
        if (visitorId != null) {
            vid = visitorId.getTntId();
        }
        else {
            visitorId = targetResponse.getResponse().getId();
            if (visitorId != null) {
                vid = visitorId.getTntId();
            }
        }
        if (vid == null) {
            vid = UUID.randomUUID().toString();
            visitorId = new VisitorId().tntId(vid);
            targetResponse.getResponse().setId(visitorId);
        }
        return vid;
    }

    private void addTimeParams(Map<String, Object> data) {
        long now = System.currentTimeMillis();
        data.put("current_timestamp", now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("u");
        data.put("current_day", dayFormat.format(now));
        SimpleDateFormat hourFormat = new SimpleDateFormat("H");
        data.put("current_hour", hourFormat.format(now));
        SimpleDateFormat minuteFormat = new SimpleDateFormat("m");
        data.put("current_minute", minuteFormat.format(now));
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

}
