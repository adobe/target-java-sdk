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

import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerState {

    @JsonProperty("request")
    private final DeliveryRequest request;

    @JsonProperty("response")
    private final DeliveryResponse response;

    public ServerState(DeliveryRequest request, DeliveryResponse response) {
        this.request = request;
        this.response = response;
    }

    public DeliveryRequest getRequest() {
        return request;
    }

    public DeliveryResponse getResponse() {
        return response;
    }

}
