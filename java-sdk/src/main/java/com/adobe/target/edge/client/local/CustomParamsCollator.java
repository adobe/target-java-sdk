package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.*;

public class CustomParamsCollator implements ParamsCollator {

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest,
                                             RequestDetails requestDetails, Map<String, Object> meta) {
        Map<String, Object> custom = new HashMap<>();
        if (requestDetails instanceof ViewRequest) {
            @SuppressWarnings("unchecked")
            List<String> views = (List<String>)meta.get("views");
            if (views != null && views.size() > 0 && requestDetails.getParameters() != null) {
                addAllParameters(custom, requestDetails);
            }
        }
        else if (requestDetails instanceof MboxRequest) {
            @SuppressWarnings("unchecked")
            List<String> mboxes = (List<String>)meta.get("mboxes");
            if (mboxes != null && mboxes.size() > 0) {
                Set<String> mboxSet = new HashSet<>(mboxes);
                if (mboxSet.contains(((MboxRequest) requestDetails).getName()) &&
                        requestDetails.getParameters() != null) {
                    addAllParameters(custom, requestDetails);
                }
            }
        }
        else { // pageLoad
            if (requestDetails.getParameters() != null) {
                addAllParameters(custom, requestDetails);
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
