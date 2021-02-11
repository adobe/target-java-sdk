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

import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.delivery.v1.model.ExecuteResponse;
import com.adobe.target.delivery.v1.model.MboxResponse;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.OptionType;
import com.adobe.target.delivery.v1.model.PageLoadResponse;
import com.adobe.target.delivery.v1.model.PrefetchResponse;
import com.adobe.target.edge.client.Attributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetAttributesResponse implements Attributes {

  private static final String GLOBAL_MBOX = "target-global-mbox";

  private final TargetDeliveryResponse response;
  private final Map<String, Map<String, Object>> content;

  public TargetAttributesResponse(TargetDeliveryResponse response) {
    this.response = response;
    this.content = toMap(response);
  }

  @Override
  public Boolean getBoolean(String mbox, String key, Boolean defaultValue) {
    Map<String, Object> map = toMboxMap(mbox);

    if (map == null) {
      return defaultValue;
    }

    Object value = map.get(key);

    if (value instanceof Boolean) {
      return ((Boolean) value);
    }

    return defaultValue;
  }

  @Override
  public String getString(String mbox, String key) {
    Map<String, Object> map = toMboxMap(mbox);

    if (map == null) {
      return null;
    }

    Object value = map.get(key);

    if (value != null) {
      return value.toString();
    }

    return null;
  }

  @Override
  public Integer getInteger(String mbox, String key, Integer defaultValue) {
    Map<String, Object> map = toMboxMap(mbox);

    if (map == null) {
      return defaultValue;
    }

    Object value = map.get(key);

    if (value instanceof Number) {
      return ((Number) value).intValue();
    } else if (value instanceof String) {
      return Double.valueOf((String) value).intValue();
    }

    return defaultValue;
  }

  @Override
  public Double getDouble(String mbox, String key, Double defaultValue) {
    Map<String, Object> map = toMboxMap(mbox);

    if (map == null) {
      return defaultValue;
    }

    Object value = map.get(key);

    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    } else if (value instanceof String) {
      return Double.parseDouble((String) value);
    }

    return defaultValue;
  }

  @Override
  public TargetDeliveryResponse getResponse() {
    return response;
  }

  @Override
  public Map<String, Object> toMboxMap(String mbox) {
    return this.content.get(mbox);
  }

  @Override
  public Map<String, Map<String, Object>> toMap() {
    return this.content;
  }

  private static Map<String, Map<String, Object>> toMap(TargetDeliveryResponse targetResponse) {
    if (targetResponse == null) {
      return null;
    }

    Map<String, Map<String, Object>> result = new HashMap<>();
    String globalMbox = getGlobalMbox(targetResponse);
    DeliveryResponse response = targetResponse.getResponse();
    PrefetchResponse prefetchResponse = response.getPrefetch();
    ExecuteResponse executeResponse = response.getExecute();

    processPrefetch(result, prefetchResponse, globalMbox);
    processExecute(result, executeResponse, globalMbox);

    return toReadOnlyMap(result);
  }

  private static String getGlobalMbox(TargetDeliveryResponse response) {
    String globalMbox = response.getResponseStatus().getGlobalMbox();

    return globalMbox == null ? GLOBAL_MBOX : globalMbox;
  }

  private static void processPrefetch(
      Map<String, Map<String, Object>> accumulator, PrefetchResponse response, String globalMbox) {
    if (response == null) {
      return;
    }

    processPageLoad(accumulator, response.getPageLoad(), globalMbox);
    processMboxes(accumulator, response.getMboxes());
  }

  private static void processExecute(
      Map<String, Map<String, Object>> accumulator, ExecuteResponse response, String globalMbox) {
    if (response == null) {
      return;
    }

    processPageLoad(accumulator, response.getPageLoad(), globalMbox);
    processMboxes(accumulator, response.getMboxes());
  }

  private static void processPageLoad(
      Map<String, Map<String, Object>> accumulator, PageLoadResponse response, String globalMbox) {
    if (response == null) {
      return;
    }

    addOptions(accumulator, response.getOptions(), globalMbox);
  }

  private static <T extends MboxResponse> void processMboxes(
      Map<String, Map<String, Object>> accumulator, List<T> mboxes) {
    if (mboxes == null) {
      return;
    }

    for (int i = mboxes.size() - 1; i >= 0; i--) {
      MboxResponse resp = mboxes.get(i);
      String mbox = resp.getName();
      List<Option> options = resp.getOptions();

      addOptions(accumulator, options, mbox);
    }
  }

  private static void addOptions(
      Map<String, Map<String, Object>> accumulator, List<Option> options, String mbox) {
    Map<String, Object> mboxContent = accumulator.computeIfAbsent(mbox, k -> new HashMap<>());

    for (int j = options.size() - 1; j >= 0; j--) {
      Option option = options.get(j);

      if (isJsonOption(option)) {
        Object contentMap = option.getContent();
        @SuppressWarnings("unchecked")
        Map<String, Object> contentObj = (Map<String, Object>) contentMap;
        mboxContent.putAll(contentObj);
      }
    }
  }

  private static boolean isJsonOption(Option option) {
    Object contentMap = option.getContent();
    return option.getType() == OptionType.JSON && contentMap instanceof Map;
  }

  private static Map<String, Map<String, Object>> toReadOnlyMap(
      Map<String, Map<String, Object>> map) {
    Map<String, Map<String, Object>> result = new HashMap<>(map.size());

    map.keySet().forEach(key -> result.put(key, Collections.unmodifiableMap(map.get(key))));

    return Collections.unmodifiableMap(result);
  }
}
