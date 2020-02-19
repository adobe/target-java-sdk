package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.*;

public class CustomParamsCollator implements ParamsCollator {

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest, Map<String, Object> meta) {
        Map<String, Object> custom = new HashMap<>();
        @SuppressWarnings("unchecked")
        List<String> mboxes = (List<String>)meta.get("mboxes");
        if (mboxes != null && mboxes.size() > 0) {
            Set<String> mboxSet = new HashSet<>(mboxes);
            PrefetchRequest prequest = deliveryRequest.getDeliveryRequest().getPrefetch();
            if (prequest != null) {
                List<MboxRequest> requests = prequest.getMboxes();
                if (requests != null) {
                    for (MboxRequest request : requests) {
                        if (mboxSet.contains(request.getName())) {
                            addAllParameters(custom, request);
                        }
                    }
                }
            }
            ExecuteRequest erequest = deliveryRequest.getDeliveryRequest().getExecute();
            if (erequest != null) {
                List<MboxRequest> requests = erequest.getMboxes();
                if (requests != null) {
                    for (MboxRequest request : requests) {
                        if (mboxSet.contains(request.getName())) {
                            addAllParameters(custom, request);
                        }
                    }
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
                params.forEach((key, value) -> custom.put(key + "_lc", value.toLowerCase()));
            }
        }
    }

}
