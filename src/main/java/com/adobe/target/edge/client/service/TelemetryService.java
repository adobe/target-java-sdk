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

import static com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService.TIMING_EXECUTE_REQUEST;

import com.adobe.target.delivery.v1.model.Telemetry;
import com.adobe.target.delivery.v1.model.TelemetryEntry;
import com.adobe.target.delivery.v1.model.TelemetryFeatures;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.utils.TimingTool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TelemetryService {

  public ClientConfig clientConfig;
  public final ConcurrentLinkedQueue<TelemetryEntry> storedTelemetries =
      new ConcurrentLinkedQueue<>();

  public TelemetryService(ClientConfig clientConfig) {
    this.clientConfig = clientConfig;
  }

  public void addTelemetry(
      TargetDeliveryRequest deliveryRequest,
      TimingTool timer,
      TargetDeliveryResponse targetDeliveryResponse) {
    TelemetryEntry telemetryEntry =
        createTelemetryEntry(
            deliveryRequest, targetDeliveryResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));
    if (telemetryEntry != null) {
      storedTelemetries.add(telemetryEntry);
    }
  }

  public Telemetry getTelemetry() {
    List<TelemetryEntry> telemetryEntryList = new ArrayList<>();
    TelemetryEntry telemetryEntry;
    while ((telemetryEntry = storedTelemetries.poll()) != null) {
      telemetryEntryList.add(telemetryEntry);
    }
    return new Telemetry().entries(telemetryEntryList);
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
