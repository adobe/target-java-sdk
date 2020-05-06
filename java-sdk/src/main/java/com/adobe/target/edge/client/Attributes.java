package com.adobe.target.edge.client;

import com.adobe.target.edge.client.model.TargetDeliveryResponse;

import java.util.Map;

public interface Attributes {

    Map<String, Map<String, Object>> toMap();

    Map<String, Object> toMboxMap(String mbox);

    boolean getBoolean(String mbox, String key);

    String getString(String mbox, String key);

    int getInteger(String mbox, String key);

    double getDouble(String mbox, String key);

    /** Allows access to resulting response to retrieve cookies. */
    TargetDeliveryResponse getResponse();

}
