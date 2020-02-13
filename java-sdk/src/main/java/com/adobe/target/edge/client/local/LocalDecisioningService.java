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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        data.put("user", userParams(deliveryRequest));
        data.put("page", pageParams(deliveryRequest));
        data.put("mbox", customParams(deliveryRequest));
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

    private Map<String, Object> pageParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> page = new HashMap<>();
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context == null) {
            return page;
        }
        Address address = context.getAddress();
        if (address == null) {
            return page;
        }
        try {
            URL url = new URL(address.getUrl());
            page.put("url", url.toString());
            page.put("url_lc", url.toString().toLowerCase());
            String host = url.getHost();
            page.put("domain", host != null ? host : "");
            page.put("domain_lc", host != null ? host.toLowerCase() : "");
            String path = url.getPath();
            page.put("path", path != null ? path : "");
            page.put("path_lc", path != null ? path.toLowerCase() : "");
            String query = url.getQuery();
            page.put("query", query != null ? query : "");
            page.put("query_lc", query != null ? query.toLowerCase() : "");
            String fragment = url.getRef();
            page.put("fragment", fragment != null ? fragment : "");
            page.put("fragment_lc", fragment != null ? fragment.toLowerCase() : "");
        }
        catch (MalformedURLException ex) {
            logger.warn("URL in context address malformed, skipping", ex);
        }
        return page;
    }

    private Map<String, Object> userParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> user = new HashMap<>();
        user.put("browserType", parseBrowserType(deliveryRequest));
        return user;
    }

    private Map<String, Object> customParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> custom = new HashMap<>();
        return custom;
    }

    private String parseBrowserType(TargetDeliveryRequest deliveryRequest) {
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context == null) {
            return "";
        }
        String userAgent = context.getUserAgent();
        if (userAgent == null) {
            return "";
        }
        if (userAgent.toLowerCase().contains("ipad")) {
            return "ipad";
        }
        else if (userAgent.toLowerCase().contains("iphone")) {
            return "iphone";
        }
        else if (userAgent.toLowerCase().contains("safari")) {
            return "safari";
        }
        else if (userAgent.toLowerCase().contains("chrome")) {
            return "chrome";
        }
        else if (userAgent.toLowerCase().contains("firefox")) {
            return "firefox";
        }
        else if (userAgent.toLowerCase().contains("msie")) {
            return "msie";
        }
        else if (userAgent.toLowerCase().contains("edge")) {
            return "edge";
        }
        else if (userAgent.toLowerCase().contains("opera")) {
            return "opera";
        }
        return "unknown";
    }
}
