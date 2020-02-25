package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.Map;

public interface ParamsCollator {

    Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest,
                                      RequestDetails requestDetails, Map<String, Object> meta);

}
