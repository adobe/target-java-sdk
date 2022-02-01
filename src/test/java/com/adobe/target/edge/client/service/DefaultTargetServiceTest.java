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

import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getLocalContext;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getMboxExecuteLocalRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getMboxPrefetchLocalRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getTestDeliveryResponse;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.MetricType;
import com.adobe.target.delivery.v1.model.Notification;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.Telemetry;
import com.adobe.target.delivery.v1.model.TelemetryEntry;
import com.adobe.target.delivery.v1.model.VisitorId;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.ResponseWrapper;
import com.adobe.target.edge.client.http.TargetHttpClient;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultTargetServiceTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

  @Mock private TargetHttpClient targetHttpClient;
  @Mock private TelemetryService telemetryService;

  private DefaultTargetService targetService;

  @BeforeEach
  void init() throws NoSuchFieldException {
    ClientConfig clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).telemetryEnabled(true).build();
    TelemetryService telemetryService = new TelemetryService(clientConfig);
    targetService = new DefaultTargetService(clientConfig, telemetryService);
    TargetClient targetJavaClient = TargetClient.create(clientConfig);

    FieldSetter.setField(
        targetService,
        targetService.getClass().getDeclaredField("targetHttpClient"),
        targetHttpClient);
    FieldSetter.setField(
        targetJavaClient,
        targetJavaClient.getClass().getDeclaredField("targetService"),
        targetService);
  }

  /**
   * since callDeliveryApi() is private, we can check if the targetHttpClient inside
   * callDeliveryApi() is called from our test method
   */
  @Test
  public void testExecuteRequestCallToDeliveryApi() {

    ResponseWrapper<DeliveryResponse> mockedResponseWrapper = getTestDeliveryResponse();
    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(mockedResponseWrapper)
        .when(targetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    TargetDeliveryRequest targetDeliveryRequestMock = getDeliveryRequest();
    targetService.executeRequest(targetDeliveryRequestMock);

    verify(targetHttpClient, times(1))
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
  }

  /**
   * since callDeliveryApiAsync() is private, we can check if the targetHttpClient inside
   * callDeliveryApiAsync() is called from our test method
   */
  @Test
  public void testExecuteRequestAsyncCallToDeliveryApi() {

    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(CompletableFuture.completedFuture(getTestDeliveryResponse()))
        .when(targetHttpClient)
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    targetService.executeRequestAsync(getDeliveryRequest());

    verify(targetHttpClient, times(1))
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
  }

  /**
   * executeNotification(args) method also calls private method callDeliveryApi(), we can check if
   * the targetHttpClient inside callDeliveryApi() is called within the method.
   */
  @Test
  public void testExecuteNotification() {

    ResponseWrapper<DeliveryResponse> mockedResponseWrapper = getTestDeliveryResponse();
    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(mockedResponseWrapper)
        .when(targetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    TargetDeliveryRequest targetDeliveryRequestMock = getDeliveryRequest();
    targetService.executeNotification(targetDeliveryRequestMock);

    verify(targetHttpClient, times(1))
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
  }

  /**
   * If a user sends a notifications request with Context.beacon = true, we should always set it to
   * false for them. Beacon does not make sense to use with a server-side SDK.
   */
  @Test
  public void testExecuteNotificationWithBeaconTrue() {

    ResponseWrapper<DeliveryResponse> mockedResponseWrapper = getTestDeliveryResponse();
    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(mockedResponseWrapper)
        .when(targetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    TargetDeliveryRequest targetDeliveryRequestMock = getDeliveryRequest();
    targetDeliveryRequestMock.getDeliveryRequest().getContext().setBeacon(Boolean.valueOf(true));
    targetService.executeNotification(targetDeliveryRequestMock);

    ArgumentCaptor<DeliveryRequest> captor = ArgumentCaptor.forClass(DeliveryRequest.class);

    verify(targetHttpClient, times(1))
        .execute(any(Map.class), any(String.class), captor.capture(), any(Class.class));
    assertFalse(captor.getValue().getContext().getBeacon());
  }

  /**
   * If a user sends a notifications request with Context.beacon = null, we should set it to false
   * to avoid NPE. Beacon does not make sense to use with a server-side SDK.
   */
  @Test
  public void testExecuteNotificationWithBeaconNull() {

    ResponseWrapper<DeliveryResponse> mockedResponseWrapper = getTestDeliveryResponse();
    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(mockedResponseWrapper)
        .when(targetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    TargetDeliveryRequest targetDeliveryRequestMock = getDeliveryRequest();
    targetDeliveryRequestMock.getDeliveryRequest().getContext().setBeacon(null);
    targetService.executeNotification(targetDeliveryRequestMock);

    ArgumentCaptor<DeliveryRequest> captor = ArgumentCaptor.forClass(DeliveryRequest.class);

    verify(targetHttpClient, times(1))
        .execute(any(Map.class), any(String.class), captor.capture(), any(Class.class));
    assertFalse(captor.getValue().getContext().getBeacon());
  }

  /**
   * executeNotificationAsync(args) method also calls private method callDeliveryApiAsync(), we can
   * check if the targetHttpClient inside callDeliveryApiAsync() is called within the method.
   */
  @Test
  public void testExecuteNotificationAsync() {
    getMockedTelemetry();
    Mockito.lenient()
        .doReturn(CompletableFuture.completedFuture(getTestDeliveryResponse()))
        .when(targetHttpClient)
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    targetService.executeNotificationAsync(getDeliveryRequest());

    verify(targetHttpClient, times(1))
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
  }

  private void getMockedTelemetry() {
    Telemetry telemetryMock = new Telemetry();
    TelemetryEntry telemetryEntryMock = new TelemetryEntry();
    telemetryEntryMock.setRequestId("test123");
    telemetryEntryMock.setTimestamp(System.currentTimeMillis());
    telemetryMock.addEntriesItem(telemetryEntryMock);
    Mockito.lenient().when(telemetryService.getTelemetry()).thenReturn(telemetryMock);
  }

  private TargetDeliveryRequest getDeliveryRequest() {
    Context context = getLocalContext();
    PrefetchRequest prefetchRequest = getMboxPrefetchLocalRequest("testoffer");
    ExecuteRequest executeRequest = getMboxExecuteLocalRequest("testoffer2");
    VisitorId visitorId = new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916873");
    Notification notification = new Notification();
    notification.setId(UUID.randomUUID().toString());
    notification.setImpressionId(UUID.randomUUID().toString());
    notification.setType(MetricType.DISPLAY);
    notification.setTimestamp(System.currentTimeMillis());
    notification.setTokens(
        Collections.singletonList(
            "IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .id(visitorId)
            .notifications(Collections.singletonList(notification))
            .build();
    return targetDeliveryRequest;
  }
}
