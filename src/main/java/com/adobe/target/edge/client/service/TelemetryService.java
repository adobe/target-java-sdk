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

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.ExecutionMode;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.Telemetry;
import com.adobe.target.delivery.v1.model.TelemetryEntry;
import com.adobe.target.delivery.v1.model.TelemetryFeatures;
import com.adobe.target.delivery.v1.model.TelemetryRequest;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.utils.MathUtils;
import com.adobe.target.edge.client.utils.TimingTool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TelemetryService {

  private final ClientConfig clientConfig;
  private final ConcurrentLinkedQueue<TelemetryEntry> storedTelemetries =
      new ConcurrentLinkedQueue<>();
  private static final int STATUS_OK = 200;
  private static final int DECIMAL_PLACE = 2;

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

  public void addTelemetry(
      TargetDeliveryRequest deliveryRequest,
      TimingTool timer,
      TargetDeliveryResponse targetDeliveryResponse,
      double parsingTime,
      long responseSize) {
    TelemetryEntry telemetryEntry =
        createTelemetryEntry(
            deliveryRequest, targetDeliveryResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));
    if (telemetryEntry != null) {
      telemetryEntry.setParsing(parsingTime);
      TelemetryRequest telemetryRequest = new TelemetryRequest();
      telemetryRequest.setResponseSize(responseSize);
      telemetryEntry.setRequest(telemetryRequest);
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

  private TelemetryEntry createTelemetryEntry(
      TargetDeliveryRequest targetDeliveryRequest,
      TargetDeliveryResponse targetDeliveryResponse,
      double executionTime) {
    if (!clientConfig.isTelemetryEnabled()) {
      return null;
    }

    TelemetryFeatures telemetryFeatures = buildTelemetryFeatures(targetDeliveryRequest);

    int status = targetDeliveryResponse.getStatus();
    ExecutionMode executionMode = getMode(targetDeliveryRequest, status);

    return new TelemetryEntry()
        .requestId(targetDeliveryResponse.getResponse().getRequestId())
        .mode(executionMode)
        .features(telemetryFeatures)
        .execution(MathUtils.roundDouble(executionTime, DECIMAL_PLACE))
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

  private int executeMboxCount(TargetDeliveryRequest request) {
    int executeMboxCount = 0;
    ExecuteRequest executeRequest = request.getDeliveryRequest().getExecute();
    if (executeRequest != null) {
      executeMboxCount = executeRequest.getMboxes().size();
    }
    return executeMboxCount;
  }

  private Boolean isExecutePageLoad(TargetDeliveryRequest request) {

    boolean isExecutePageLoad = false;
    ExecuteRequest executeRequest = request.getDeliveryRequest().getExecute();
    if (executeRequest != null) {
      isExecutePageLoad = executeRequest.getPageLoad() != null;
    }
    return isExecutePageLoad;
  }

  private Integer prefetchMboxCount(TargetDeliveryRequest request) {
    int prefetchMboxCount = 0;
    PrefetchRequest prefetchRequest = request.getDeliveryRequest().getPrefetch();
    if (prefetchRequest != null) {
      prefetchMboxCount = prefetchRequest.getMboxes().size();
    }
    return prefetchMboxCount;
  }

  private Boolean isPrefetchPageLoad(TargetDeliveryRequest request) {
    boolean prefetchPageLoad = false;
    PrefetchRequest prefetchRequest = request.getDeliveryRequest().getPrefetch();
    if (prefetchRequest != null) {
      prefetchPageLoad = prefetchRequest.getPageLoad() != null;
    }
    return prefetchPageLoad;
  }

  private Integer prefetchViewCount(TargetDeliveryRequest request) {
    int prefetchViewCount = 0;
    if (request.getDeliveryRequest().getPrefetch() != null) {
      prefetchViewCount = request.getDeliveryRequest().getPrefetch().getViews().size();
    }
    return prefetchViewCount;
  }

  private ExecutionMode getMode(TargetDeliveryRequest request, int status) {

    if (status == STATUS_OK
        && (getDecisioningMethod(request).equals(DecisioningMethod.ON_DEVICE)
            || getDecisioningMethod(request).equals(DecisioningMethod.HYBRID))) {
      return ExecutionMode.LOCAL;
    }
    return ExecutionMode.EDGE;
  }

  private TelemetryFeatures buildTelemetryFeatures(TargetDeliveryRequest targetDeliveryRequest) {

    com.adobe.target.delivery.v1.model.DecisioningMethod decisioningMethod =
        com.adobe.target.delivery.v1.model.DecisioningMethod.valueOf(
            getDecisioningMethod(targetDeliveryRequest).name());

    TelemetryFeatures telemetryFeatures = new TelemetryFeatures();
    telemetryFeatures.setDecisioningMethod(decisioningMethod);

    int countOfExecutedMbox = executeMboxCount(targetDeliveryRequest);
    int countOfPrefetchedMbox = prefetchMboxCount(targetDeliveryRequest);
    int countOfPrefetchedView = prefetchViewCount(targetDeliveryRequest);
    boolean isItExecutePageLoad = isExecutePageLoad(targetDeliveryRequest);
    boolean isItPrefetchedPageLoad = isPrefetchPageLoad(targetDeliveryRequest);

    if (countOfExecutedMbox != 0) {
      telemetryFeatures.setExecuteMboxCount(countOfExecutedMbox);
    }
    if (countOfPrefetchedMbox != 0) {
      telemetryFeatures.setPrefetchMboxCount(countOfPrefetchedMbox);
    }
    if (countOfPrefetchedView != 0) {
      telemetryFeatures.setPrefetchViewCount(countOfPrefetchedView);
    }
    if (isItExecutePageLoad) {
      telemetryFeatures.setExecutePageLoad(true);
    }
    if (isItPrefetchedPageLoad) {
      telemetryFeatures.setPrefetchPageLoad(true);
    }

    return telemetryFeatures;
  }
}
