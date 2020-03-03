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
