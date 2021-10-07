/*
 * Copyright 2021 Adobe. All rights reserved.
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

import com.adobe.target.delivery.v1.model.TelemetryEntry;
import com.adobe.target.delivery.v1.model.TelemetryFeatures;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;

public class TelemetryService {
  public ClientConfig clientConfig;

  public TelemetryService(ClientConfig clientConfig) {
    this.clientConfig = clientConfig;
  }

  public TelemetryEntry createTelemetryEntry(
      TargetDeliveryRequest targetDeliveryRequest,
      TargetDeliveryResponse targetDeliveryResponse,
      double executionTime) {
    if (!clientConfig.isTelemetryEnabled()) {
      return null;
    }
    com.adobe.target.delivery.v1.model.DecisioningMethod decisioningMethod =
        com.adobe.target.delivery.v1.model.DecisioningMethod.valueOf(
            getDecisioningMethod(targetDeliveryRequest).name());
    TelemetryFeatures telemetryFeatures =
        new TelemetryFeatures().decisioningMethod(decisioningMethod);

    return new TelemetryEntry()
        .requestId(targetDeliveryResponse.getResponse().getRequestId())
        .features(telemetryFeatures)
        .execution(executionTime)
        .timestamp(System.currentTimeMillis());
  }

  private DecisioningMethod getDecisioningMethod(TargetDeliveryRequest request) {
    DecisioningMethod requestDecisioning = request.getDecisioningMethod();

    if (requestDecisioning != null) {
      return requestDecisioning;
    }

    DecisioningMethod configDecisioning = clientConfig.getDefaultDecisioningMethod();

    if (configDecisioning != null) {
      return configDecisioning;
    }

    return DecisioningMethod.SERVER_SIDE;
  }
}
