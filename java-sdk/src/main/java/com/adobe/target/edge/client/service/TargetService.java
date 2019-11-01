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
package com.adobe.target.edge.client.service;

import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.concurrent.CompletableFuture;

public interface TargetService extends AutoCloseable {

    TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest);

    CompletableFuture<TargetDeliveryResponse> executeRequestAsync(TargetDeliveryRequest deliveryRequest);

    ResponseStatus executeNotification(TargetDeliveryRequest deliveryRequest);

    CompletableFuture<ResponseStatus> executeNotificationAsync(TargetDeliveryRequest deliveryRequest);

}
