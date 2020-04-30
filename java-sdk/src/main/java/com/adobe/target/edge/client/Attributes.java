package com.adobe.target.edge.client;

import com.adobe.target.edge.client.model.TargetDeliveryResponse;

import java.util.Map;

public interface Attributes {

    Map<String, Map<String, Object>> toMap();

    Map<String, Object> toMboxMap(String mbox);

    boolean getFeatureBoolean(String mbox, String key);

    String getFeatureString(String mbox, String key);

    int getFeatureInteger(String mbox, String key);

    double getFeatureDouble(String mbox, String key);

    /** Allows access to resulting response to retrieve cookies. */
    TargetDeliveryResponse getResponse();

}
