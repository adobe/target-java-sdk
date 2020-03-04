package com.adobe.target.edge.client.model;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.Attributes;

import java.util.*;

public class TargetAttributesResponse implements Attributes {

    private TargetDeliveryResponse response;
    private String[] mboxes;

    private Map<String, Object> content;

    public TargetAttributesResponse(TargetDeliveryResponse response, String ...mboxes) {
        this.response = response;
        this.mboxes = mboxes;
    }

    @Override
    public boolean getFeatureBoolean(String key) {
        Map<String, Object> map = toMap();
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return ((Boolean) value);
            }
        }
        return false;
    }

    @Override
    public String getFeatureString(String key) {
        Map<String, Object> map = toMap();
        if (map != null) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    @Override
    public int getFeatureInteger(String key) {
        Map<String, Object> map = toMap();
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            else if (value instanceof String) {
                return Double.valueOf((String)value).intValue();
            }
        }
        return 0;
    }

    @Override
    public double getFeatureDouble(String key) {
        Map<String, Object> map = toMap();
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            else if (value instanceof String) {
                return Double.parseDouble((String)value);
            }
        }
        return 0d;
    }

    @Override
    public TargetDeliveryResponse getResponse() {
        return response;
    }

    @Override
    public Map<String, Object> toMap() {
        if (this.content != null) {
            return content;
        }
        if (this.response == null || this.mboxes == null) {
            return null;
        }
        Set<String> mboxNames = new HashSet<>(Arrays.asList(this.mboxes));
        List<MboxResponse> allMboxes = new ArrayList<>();
        DeliveryResponse response = this.response.getResponse();
        PrefetchResponse prefetchResponse = response.getPrefetch();
        if (prefetchResponse != null) {
            List<PrefetchMboxResponse> prefectchMboxes = prefetchResponse.getMboxes();
            allMboxes.addAll(prefectchMboxes);
        }
        ExecuteResponse executeResponse = response.getExecute();
        if (executeResponse != null) {
            List<MboxResponse> executeMboxes = executeResponse.getMboxes();
            allMboxes.addAll(executeMboxes);
        }
        Map<String, Object> finalContent = new HashMap<>();
        for (MboxResponse resp : allMboxes) {
            if (resp.getName() == null || !mboxNames.contains(resp.getName())) {
                continue;
            }
            List<Option> options = resp.getOptions();
            for (Option option : options) {
                Object contentMap = option.getContent();
                if (option.getType() == OptionType.JSON && contentMap instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> contentObj = (Map<String, Object>) contentMap;
                    finalContent.putAll(contentObj);
                }
            }
        }
        this.content = finalContent;
        return finalContent;
    }
}
