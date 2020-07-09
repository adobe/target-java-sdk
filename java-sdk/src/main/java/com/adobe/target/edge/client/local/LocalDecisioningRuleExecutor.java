package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.local.LocalDecisioningRule;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalDecisioningRuleExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LocalDecisioningRuleExecutor.class);

    private static final String ALLOCATION = "allocation";
    private static final String OPTIONS = "options";
    private static final String RT_EXECUTION_TYPE = "activity.executionType";

    private final ClientConfig clientConfig;
    private final ObjectMapper mapper;

    private final JsonLogic jsonLogic = new JsonLogic();

    public LocalDecisioningRuleExecutor(ClientConfig clientConfig, ObjectMapper mapper) {
        this.clientConfig = clientConfig;
        this.mapper = mapper;
    }

    public Map<String, Object> executeRule(Map<String, Object> localContext,
            RequestDetails details,
            String visitorId,
            LocalDecisioningRule rule,
            Set<String> responseTokens,
            TraceHandler traceHandler) {
        localContext.put(ALLOCATION, computeAllocation(visitorId, rule));
        Object condition = rule.getCondition();
        logger.trace("details={}, context={}", details, localContext);
        try {
            String expression = this.mapper.writeValueAsString(condition);
            logger.trace("expression={}", expression);
            boolean matched = JsonLogic.truthy(jsonLogic.apply(expression, localContext));
            if (traceHandler != null) {
                traceHandler.addCampaign(rule, localContext, matched);
            }
            if (matched) {
                return consequenceWithResponseTokens(responseTokens, rule, localContext);
            }
            return null;
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

    private double computeAllocation(String vid, LocalDecisioningRule rule) {
        String client = this.clientConfig.getClient();
        String seed = rule.getActivityId();
        int index = vid.indexOf(".");
        if (index > 0) {
            vid = vid.substring(0, index);
        }
        String input = client + "." + seed + "." + vid;
        int output = MurmurHash.hash32(input);
        return ((Math.abs(output) % 10000) / 10000D) * 100D;
    }

    private Map<String, Object> consequenceWithResponseTokens(Set<String> responseTokenKeys,
            LocalDecisioningRule rule,
            Map<String, Object> localContext) {
        Map<String, Object> consequence = rule.getConsequence();
        if (consequence == null) {
            return null;
        }

        if (responseTokenKeys.isEmpty()) {
            return consequence;
        }

        Object options = consequence.get(OPTIONS);
        if (options == null) {
            return consequence;
        }

        List<Option> optionsList = this.mapper.convertValue(options,
                new TypeReference<List<Option>>() {
                });
        if (optionsList == null || optionsList.isEmpty()) {
            return consequence;
        }

        Option option = optionsList.get(0);
        Map<String, Object> responseTokens = option.getResponseTokens();
        responseTokens.put(RT_EXECUTION_TYPE, "client-side");
        @SuppressWarnings("unchecked")
        Map<String, Object> geoContext =
                (Map<String, Object>)localContext.get(LocalDecisioningService.CONTEXT_KEY_GEO);
        if (geoContext != null) {
            for (Map.Entry<String, Object> geoEntry : geoContext.entrySet()) {
                String key = geoEntry.getKey();
                String tokenKey;
                if (key.equals("region")) {
                    tokenKey = LocalDecisioningService.CONTEXT_KEY_GEO + ".state";
                }
                else {
                    tokenKey = LocalDecisioningService.CONTEXT_KEY_GEO + "." + key;
                }
                if (responseTokenKeys.contains(tokenKey)) {
                    responseTokens.put(tokenKey, geoEntry.getValue());
                }
            }
        }

        Map<String, Object> meta = rule.getMeta();
        if (meta == null) {
            return updateOptions(consequence, optionsList);
        }
        for (Map.Entry<String, Object> metaEntry : meta.entrySet()) {
            String key = metaEntry.getKey();
            if (responseTokenKeys.contains(key)) {
                responseTokens.put(key, metaEntry.getValue());
            }
        }
        return updateOptions(consequence, optionsList);
    }

    private Map<String, Object> updateOptions(Map<String, Object> consequence,
            List<Option> options) {
        consequence.put(OPTIONS, this.mapper.convertValue(options, List.class));
        return consequence;
    }
}
