package com.adobe.target.edge.client;

import com.adobe.target.edge.client.model.TargetDeliveryResponse;

import java.util.Map;

public interface Attributes {

    Map<String, Object> toMap();

    boolean getFeatureBoolean(String key);

    String getFeatureString(String key);

    int getFeatureInteger(String key);

    double getFeatureDouble(String key);

    /** Allows access to resulting response to retrieve cookies. */
    TargetDeliveryResponse getResponse();

}
