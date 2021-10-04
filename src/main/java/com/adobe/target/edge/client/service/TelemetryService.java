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

  public TelemetryEntry createTelemetryEntry(TargetDeliveryRequest targetDeliveryRequest, TargetDeliveryResponse targetDeliveryResponse,
                                              double executionTime) {

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
