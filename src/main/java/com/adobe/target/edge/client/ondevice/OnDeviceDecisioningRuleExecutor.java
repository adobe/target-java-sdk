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

import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRule;
import com.adobe.target.edge.client.ondevice.collator.GeoParamsCollator;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.adobe.target.edge.client.utils.AllocationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnDeviceDecisioningRuleExecutor {

  private static final Logger logger =
      LoggerFactory.getLogger(OnDeviceDecisioningRuleExecutor.class);

  private static final String ALLOCATION = "allocation";
  private static final String CAMPAIGN_BUCKET_SALT = "0";
  private static final String OPTIONS = "options";
  private static final String RESPONSE_TOKEN_EXECUTION_TYPE = "activity.decisioningMethod";

  private final ClientConfig clientConfig;
  private final ObjectMapper mapper;

  private final JsonLogic jsonLogic = new JsonLogic();

  public OnDeviceDecisioningRuleExecutor(ClientConfig clientConfig, ObjectMapper mapper) {
    this.clientConfig = clientConfig;
    this.mapper = mapper;
  }

  public Map<String, Object> executeRule(
      Map<String, Object> localContext,
      RequestDetails details,
      String visitorId,
      OnDeviceDecisioningRule rule,
      Set<String> responseTokens,
      TraceHandler traceHandler) {
    localContext.put(ALLOCATION, computeAllocation(visitorId, rule, null));
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
        return replaceCampaignMacros(
            rule, consequenceWithResponseTokens(responseTokens, rule, localContext), details);
      }
      return null;
    } catch (Exception e) {
      String message = "Hit exception while evaluating local-decisioning rule";
      logger.warn(message, e);
      TargetExceptionHandler handler = this.clientConfig.getExceptionHandler();
      if (handler != null) {
        handler.handleException(new TargetClientException(message, e));
      }
      return null;
    }
  }

  private double computeAllocation(String visitorId, OnDeviceDecisioningRule rule, String salt) {
    return AllocationUtils.calculateAllocation(
        this.clientConfig.getClient(),
        rule.getActivityId(),
        visitorId,
        salt == null ? CAMPAIGN_BUCKET_SALT : salt);
  }

  private Map<String, Object> consequenceWithResponseTokens(
      Set<String> responseTokenKeys,
      OnDeviceDecisioningRule rule,
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

    List<Option> optionsList =
        this.mapper.convertValue(options, new TypeReference<List<Option>>() {});
    if (optionsList == null || optionsList.isEmpty()) {
      return consequence;
    }

    Option option = optionsList.get(0);
    Map<String, Object> responseTokens = option.getResponseTokens();
    responseTokens.put(RESPONSE_TOKEN_EXECUTION_TYPE, "on-device");
    @SuppressWarnings("unchecked")
    Map<String, Object> geoContext =
        (Map<String, Object>) localContext.get(OnDeviceDecisioningService.CONTEXT_KEY_GEO);
    if (geoContext != null) {
      for (Map.Entry<String, Object> geoEntry : geoContext.entrySet()) {
        String key = geoEntry.getKey();
        String tokenKey;
        if (key.equals("region")) {
          tokenKey = OnDeviceDecisioningService.CONTEXT_KEY_GEO + ".state";
        } else {
          tokenKey = OnDeviceDecisioningService.CONTEXT_KEY_GEO + "." + key;
        }
        if (responseTokenKeys.contains(tokenKey)
            && geoEntry.getValue() != GeoParamsCollator.DEFAULT_GEO_PARAMS.get(geoEntry.getKey())) {
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

  private Map<String, Object> replaceCampaignMacros(
      OnDeviceDecisioningRule rule, Map<String, Object> consequence, RequestDetails details) {
    if (consequence == null || consequence.get(OPTIONS) == null) {
      return consequence;
    }
    CampaignMacroReplacer campaignMacroReplacer =
        new CampaignMacroReplacer(rule, consequence, details, this.mapper);
    return updateOptions(consequence, campaignMacroReplacer.getOptions());
  }

  private Map<String, Object> updateOptions(Map<String, Object> consequence, List<Option> options) {
    consequence.put(OPTIONS, this.mapper.convertValue(options, List.class));
    return consequence;
  }
}
