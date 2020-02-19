package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomParamsCollator implements ParamsCollator {

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> custom = new HashMap<>();
        PrefetchRequest prequest = deliveryRequest.getDeliveryRequest().getPrefetch();
        if (prequest != null) {
            addAllParameters(custom, prequest.getPageLoad());
            List<MboxRequest> requests = prequest.getMboxes();
            if (requests != null) {
                for (MboxRequest request : requests) {
                    addAllParameters(custom, request);
                }
            }
        }
        ExecuteRequest erequest = deliveryRequest.getDeliveryRequest().getExecute();
        if (erequest != null) {
            addAllParameters(custom, erequest.getPageLoad());
            List<MboxRequest> requests = erequest.getMboxes();
            if (requests != null) {
                for (MboxRequest request : requests) {
                    addAllParameters(custom, request);
                }
            }
        }
        return custom;
    }

    private void addAllParameters(Map<String, Object> custom, RequestDetails details) {
        if (details != null) {
            Map<String, String> params = details.getParameters();
            if (params != null) {
                custom.putAll(params);
            }
        }
    }

}
