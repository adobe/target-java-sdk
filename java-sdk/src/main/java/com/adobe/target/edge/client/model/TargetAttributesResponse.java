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
package com.adobe.target.edge.client.model;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.Attributes;

import java.util.*;

public class TargetAttributesResponse implements Attributes {

    private final TargetDeliveryResponse response;
    private final Map<String, Map<String, Object>> content = new HashMap<>();
    private volatile boolean contentCreated = false;

    public TargetAttributesResponse(TargetDeliveryResponse response) {
        this.response = response;
    }

    @Override
    public Boolean getBoolean(String mbox, String key, Boolean defaultValue) {
        Map<String, Object> map = toMboxMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return ((Boolean) value);
            }
        }
        return defaultValue;
    }

    @Override
    public String getString(String mbox, String key) {
        Map<String, Object> map = toMboxMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    @Override
    public Integer getInteger(String mbox, String key, Integer defaultValue) {
        Map<String, Object> map = toMboxMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            else if (value instanceof String) {
                return Double.valueOf((String)value).intValue();
            }
        }
        return defaultValue;
    }

    @Override
    public Double getDouble(String mbox, String key, Double defaultValue) {
        Map<String, Object> map = toMboxMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            else if (value instanceof String) {
                return Double.parseDouble((String)value);
            }
        }
        return defaultValue;
    }

    @Override
    public TargetDeliveryResponse getResponse() {
        return response;
    }

    @Override
    public Map<String, Object> toMboxMap(String mbox) {
        Map<String, Object> mboxContent = this.content.get(mbox);
        if (mboxContent != null) {
            return mboxContent;
        }
        return toMap().get(mbox);
    }

    @Override
    public Map<String, Map<String, Object>> toMap() {
        if (this.response == null) {
            return null;
        }
        if (this.contentCreated) {
            return this.content;
        }
        synchronized (this.content) {
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
            for (int i = allMboxes.size() - 1; i >= 0; i--) {
                MboxResponse resp = allMboxes.get(i);
                String mbox = resp.getName();
                Map<String, Object> mboxContent = this.content.computeIfAbsent(mbox, k -> new HashMap<>());
                List<Option> options = resp.getOptions();
                for (Option option : options) {
                    Object contentMap = option.getContent();
                    if (option.getType() == OptionType.JSON && contentMap instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contentObj = (Map<String, Object>) contentMap;
                        mboxContent.putAll(contentObj);
                    }
                }
            }
            this.contentCreated = true;
        }
        return this.content;
    }
}
