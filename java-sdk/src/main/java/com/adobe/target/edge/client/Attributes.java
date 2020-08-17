package com.adobe.target.edge.client;

import com.adobe.target.edge.client.model.TargetDeliveryResponse;

import java.util.Map;

public interface Attributes {

    Map<String, Map<String, Object>> toMap();

    Map<String, Object> toMboxMap(String mbox);

    Boolean getBoolean(String mbox, String key, Boolean defaultValue);

    String getString(String mbox, String key);

    Integer getInteger(String mbox, String key, Integer defaultValue);

    Double getDouble(String mbox, String key, Double defaultValue);

    /** Allows access to resulting response to retrieve cookies. */
    TargetDeliveryResponse getResponse();

}
