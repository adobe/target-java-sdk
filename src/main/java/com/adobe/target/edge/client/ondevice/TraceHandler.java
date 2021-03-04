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
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRule;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class TraceHandler {

  private static final String MATCHED_IDS_KEY = "matchedSegmentIds";
  private static final String UNMATCHED_IDS_KEY = "unmatchedSegmentIds";
  private static final String MATCHED_RULES_KEY = "matchedRuleConditions";
  private static final String UNMATCHED_RULES_KEY = "unmatchedRuleConditions";

  public static final String ACTIVITY_ID = "activity.id";
  public static final String ACTIVITY_NAME = "activity.name";
  public static final String ACTIVITY_TYPE = "activity.type";
  public static final String EXPERIENCE_ID = "experience.id";
  public static final String AUDIENCE_IDS = "audience.ids";
  public static final String OFFER_ID = "offer.id";

  private final TimeZone utc = TimeZone.getTimeZone("UTC");

  private final ObjectMapper mapper;
  private final OnDeviceDecisioningRuleSet ruleSet;

  private final Map<String, Object> trace;
  private final Map<Number, Map<String, Object>> campaigns;
  private final Map<Number, Map<String, Object>> evaluatedTargets;

  public TraceHandler(
      ClientConfig clientConfig,
      RuleLoader ruleLoader,
      ObjectMapper mapper,
      OnDeviceDecisioningRuleSet ruleSet,
      TargetDeliveryRequest request) {
    this.mapper = mapper;
    this.ruleSet = ruleSet;
    this.trace = new HashMap<>();
    this.trace.put("clientCode", clientConfig.getClient());
    this.trace.put("artifact", this.artifactTrace(ruleLoader, ruleSet));
    this.trace.put("profile", this.profileTrace(request.getDeliveryRequest().getId()));
    this.campaigns = new HashMap<>();
    this.evaluatedTargets = new HashMap<>();
  }

  public void updateRequest(
      TargetDeliveryRequest request, RequestDetails details, boolean execute) {
    this.trace.put("request", this.requestTrace(request, details, execute));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void addCampaign(
      OnDeviceDecisioningRule rule, Map<String, Object> context, boolean matched) {
    Map<String, Object> meta = rule.getMeta();
    Number activityId = (Number) meta.get(ACTIVITY_ID);
    if (activityId == null) {
      return;
    }
    if (matched && !this.campaigns.containsKey(activityId)) {
      Map<String, Object> campaign = this.campaignTrace(rule);
      campaign.put("branchId", meta.get(EXPERIENCE_ID));
      campaign.put("offers", meta.get(OFFER_ID));
      this.campaigns.put(activityId, campaign);
    }
    Map<String, Object> target = this.evaluatedTargets.get(activityId);
    if (target == null) {
      target = this.campaignTargetTrace(rule, context);
      this.evaluatedTargets.put(activityId, target);
    }
    List audienceIds = (List) meta.get(AUDIENCE_IDS);
    List ids;
    List rules;
    if (matched) {
      ids = (List) target.get(MATCHED_IDS_KEY);
      rules = (List) target.get(MATCHED_RULES_KEY);
    } else {
      ids = (List) target.get(UNMATCHED_IDS_KEY);
      rules = (List) target.get(UNMATCHED_RULES_KEY);
    }
    ids.addAll(audienceIds);
    rules.add(rule.getCondition());
  }

  public void addNotification(OnDeviceDecisioningRule rule, Notification notification) {
    Map<String, Object> meta = rule.getMeta();
    Number activityId = (Number) meta.get(ACTIVITY_ID);
    if (activityId == null) {
      return;
    }
    Map<String, Object> campaign = this.campaigns.get(activityId);
    if (campaign == null) {
      return;
    }
    @SuppressWarnings("rawtypes")
    Map notificationMap = mapper.convertValue(notification, Map.class);
    campaign.put("notifications", Collections.singletonList(notificationMap));
  }

  public Map<String, Object> getCurrentTrace() {
    Map<String, Object> currentTrace = new HashMap<>(this.trace);
    currentTrace.put("campaigns", campaigns.values());
    currentTrace.put("evaluatedCampaignTargets", this.evaluatedTargets.values());
    return currentTrace;
  }

  private Map<String, Object> artifactTrace(
      RuleLoader ruleLoader, OnDeviceDecisioningRuleSet ruleSet) {
    Map<String, Object> artifacts = new HashMap<>(ruleSet.getMeta());
    artifacts.put("artifactVersion", ruleSet.getVersion());
    artifacts.put("pollingInterval", ruleLoader.getPollingInterval());
    artifacts.put("artifactRetrievalCount", ruleLoader.getNumFetches());
    artifacts.put("artifactLocation", ruleLoader.getLocation());
    artifacts.put("pollingHalted", false);
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    format.setTimeZone(this.utc);
    artifacts.put("artifactLastRetrieved", format.format(ruleLoader.getLastFetch()));
    return artifacts;
  }

  private Map<String, Object> profileTrace(VisitorId visitorId) {
    Map<String, Object> profile = new HashMap<>();
    if (visitorId != null) {
      String tntId = visitorId.getTntId();
      if (tntId != null) {
        Map<String, Object> visitorIdMap = new HashMap<>();
        int idx = tntId.lastIndexOf(".");
        if (idx >= 0 && idx < tntId.length() - 1) {
          visitorIdMap.put("tntId", tntId.substring(0, idx));
          visitorIdMap.put("profileLocation", tntId.substring(idx + 1));
        } else {
          visitorIdMap.put("tntId", tntId);
        }
        String mcid = visitorId.getMarketingCloudVisitorId();
        if (mcid != null) {
          visitorIdMap.put("marketingCloudVisitorId", mcid);
        }
        String thirdParty = visitorId.getThirdPartyId();
        if (thirdParty != null) {
          visitorIdMap.put("thirdPartyId", thirdParty);
        }
        List<CustomerId> customerIds = visitorId.getCustomerIds();
        if (customerIds != null) {
          visitorIdMap.put("customerIds", mapper.convertValue(customerIds, Map.class));
        }
        profile.put("visitorId", visitorIdMap);
      }
    }
    return profile;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Map<String, Object> requestTrace(
      TargetDeliveryRequest request, RequestDetails details, boolean execute) {
    Map<String, Object> req = new HashMap<>();
    req.put("sessionId", request.getSessionId());
    req.put("environmentId", request.getDeliveryRequest().getEnvironmentId());
    if (details instanceof ViewRequest) {
      req.put("view", mapper.convertValue(details, Map.class));
    } else {
      Map mbox = mapper.convertValue(details, Map.class);
      if (!mbox.containsKey("name")) {
        mbox.put("name", this.ruleSet.getGlobalMbox());
      }
      mbox.put("type", execute ? "execute" : "prefetch");
      req.put("mbox", mbox);
    }
    Address address = details.getAddress();
    if (address != null) {
      String urlStr = address.getUrl();
      req.put("pageURL", address.getUrl());
      try {
        URL url = new URL(urlStr);
        req.put("host", url.getHost());
      } catch (MalformedURLException e) {
        // ignore
      }
    }
    return req;
  }

  private Map<String, Object> campaignTargetTrace(
      OnDeviceDecisioningRule rule, Map<String, Object> context) {
    Map<String, Object> target = campaignTrace(rule);
    target.put("context", context);
    target.put(MATCHED_IDS_KEY, new ArrayList<>());
    target.put(UNMATCHED_IDS_KEY, new ArrayList<>());
    target.put(MATCHED_RULES_KEY, new ArrayList<>());
    target.put(UNMATCHED_RULES_KEY, new ArrayList<>());
    return target;
  }

  private Map<String, Object> campaignTrace(OnDeviceDecisioningRule rule) {
    Map<String, Object> campaign = new HashMap<>();
    Map<String, Object> meta = rule.getMeta();
    campaign.put("id", meta.get(ACTIVITY_ID));
    campaign.put("activityName", meta.get(ACTIVITY_NAME));
    campaign.put("activityType", meta.get(ACTIVITY_TYPE));
    return campaign;
  }
}
