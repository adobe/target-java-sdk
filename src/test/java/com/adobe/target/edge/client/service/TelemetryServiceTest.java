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
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.fileRuleLoader;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getContext;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getMboxExecuteRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getPrefetchViewsRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getTestDeliveryResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.adobe.target.delivery.v1.model.ChannelType;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.ExecutionMode;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.Notification;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.Property;
import com.adobe.target.delivery.v1.model.Telemetry;
import com.adobe.target.delivery.v1.model.TelemetryEntry;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.utils.TimingTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelemetryServiceTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private TargetClient targetJavaClient;
  private static ClientConfig clientConfig;
  private DefaultTargetService targetService;
  private ClusterLocator clusterLocator;
  private OnDeviceDecisioningService localService;
  private NotificationService notificationService;
  private static TelemetryService telemetryService;

  @SuppressWarnings("unchecked")
  void setup(boolean telemetryEnabled) throws NoSuchFieldException {

    Mockito.lenient()
        .doReturn(getTestDeliveryResponse())
        .when(defaultTargetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
    clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .telemetryEnabled(telemetryEnabled)
            .build();
    telemetryService = new TelemetryService(clientConfig);
    targetService = new DefaultTargetService(clientConfig);
    clusterLocator = new ClusterLocator();
    notificationService = new NotificationService(targetService, clientConfig, clusterLocator);
    localService = new OnDeviceDecisioningService(clientConfig, targetService);
    ObjectMapper mapper = new JacksonObjectMapper().getMapper();
    OnDeviceDecisioningDetailsExecutor decisionHandler =
        new OnDeviceDecisioningDetailsExecutor(clientConfig, mapper);
    targetJavaClient = TargetClient.create(clientConfig);

    FieldSetter.setField(
        targetService,
        targetService.getClass().getDeclaredField("targetHttpClient"),
        defaultTargetHttpClient);
    FieldSetter.setField(
        targetJavaClient,
        targetJavaClient.getClass().getDeclaredField("targetService"),
        targetService);
    FieldSetter.setField(
        targetJavaClient,
        targetJavaClient.getClass().getDeclaredField("localService"),
        localService);

    FieldSetter.setField(
        localService, localService.getClass().getDeclaredField("decisionHandler"), decisionHandler);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        notificationService);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("clusterLocator"),
        mock(ClusterLocator.class));
  }

  @Test
  void testTelemetrySentOnExecute() throws NoSuchFieldException, IOException {
    setup(true);
    long timestamp = System.currentTimeMillis();
    TargetService targetServiceMock = mock(TargetService.class, RETURNS_DEFAULTS);
    NotificationService notificationService =
        new NotificationService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        notificationService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .prefetch(
                new PrefetchRequest()
                    .addMboxesItem(new MboxRequest().index(0).name("TEST_PREFETCH")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);

    ArgumentCaptor<TargetDeliveryRequest> captor =
        ArgumentCaptor.forClass(TargetDeliveryRequest.class);

    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(captor.capture());

    Telemetry telemetry = targetDeliveryRequest.getDeliveryRequest().getTelemetry();

    assertNotNull(telemetry);

    assertEquals(telemetry.getEntries().size(), 1);
    TelemetryEntry telemetryEntry = telemetry.getEntries().get(0);

    assertTrue(telemetryEntry.getTimestamp() > timestamp);
    assertTrue(telemetryEntry.getExecution() > 0);
    assertTrue(telemetryEntry.getRequestId().length() > 0);
    assertEquals(telemetryEntry.getFeatures().getDecisioningMethod(), "on-device");
  }

  @Test
  void testTelemetrySentOnPrefetch() throws NoSuchFieldException, IOException {
    setup(true);
    long timestamp = System.currentTimeMillis();
    TargetService targetServiceMock = mock(TargetService.class, RETURNS_DEFAULTS);
    NotificationService notificationService =
        new NotificationService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        notificationService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .prefetch(
                new PrefetchRequest()
                    .addMboxesItem(new MboxRequest().index(0).name("TEST_PREFETCH")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);

    ArgumentCaptor<TargetDeliveryRequest> captor =
        ArgumentCaptor.forClass(TargetDeliveryRequest.class);

    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(captor.capture());

    Telemetry telemetry = captor.getValue().getDeliveryRequest().getTelemetry();

    assertNotNull(telemetry);
    TelemetryEntry telemetryEntry = telemetry.getEntries().get(0);

    assertTrue(telemetryEntry.getTimestamp() > timestamp);
    assertTrue(telemetryEntry.getExecution() > 0);
    assertTrue(telemetryEntry.getRequestId().length() > 0);
    assertEquals(telemetryEntry.getFeatures().getDecisioningMethod(), "on-device");
  }

  @Test
  void testTelemetryNotSentPrefetch() throws NoSuchFieldException, IOException {
    setup(false);
    TargetService targetServiceMock = mock(TargetService.class, RETURNS_DEFAULTS);
    NotificationService notificationService =
        new NotificationService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        notificationService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .prefetch(
                new PrefetchRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);
    verify(targetServiceMock, never()).executeNotificationAsync(any());
  }

  @Test
  void testTelemetryNotSentExecute() throws NoSuchFieldException, IOException {
    setup(false);
    TargetService targetServiceMock = mock(TargetService.class, RETURNS_DEFAULTS);
    NotificationService notificationService =
        new NotificationService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        notificationService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .prefetch(
                new PrefetchRequest()
                    .addMboxesItem(new MboxRequest().index(0).name("TEST_PREFETCH")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);

    ArgumentCaptor<TargetDeliveryRequest> captor =
        ArgumentCaptor.forClass(TargetDeliveryRequest.class);
    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(captor.capture());

    Telemetry telemetry = captor.getValue().getDeliveryRequest().getTelemetry();
    List<Notification> notifications = captor.getValue().getDeliveryRequest().getNotifications();

    assertNull(telemetry);
    assertEquals(notifications.size(), 1);
  }

  @Test
  void testDecisioningMethod() {
    List<String> childKeys =
        Arrays.stream(com.adobe.target.edge.client.model.DecisioningMethod.values())
            .map(Enum::name)
            .collect(Collectors.toList());
    List<String> parentKeys =
        Arrays.stream(com.adobe.target.delivery.v1.model.DecisioningMethod.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    assertEquals(childKeys, parentKeys);

    for (int i = 0; i < childKeys.size(); i++) {
      assertEquals(
          com.adobe.target.delivery.v1.model.DecisioningMethod.valueOf(parentKeys.get(i))
              .toString(),
          com.adobe.target.edge.client.model.DecisioningMethod.valueOf(childKeys.get(i))
              .toString());
    }
  }

  @Test
  void testCreateTelemetryForServerSide() throws NoSuchFieldException {
    setup(true);
    TimingTool timer = new TimingTool();
    timer.timeStart(TIMING_EXECUTE_REQUEST);

    Context context = getContext();
    PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
    ExecuteRequest executeRequest = getMboxExecuteRequest();
    String nonDefaultToken = "non-default-token";

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .property(new Property().token(nonDefaultToken))
            .decisioningMethod(DecisioningMethod.SERVER_SIDE)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 200, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    TelemetryEntry telemetryEntry =
        telemetryService.createTelemetryEntry(
            targetDeliveryRequest, targetDeliveryResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));

    assertNotNull(telemetryEntry);
    assertEquals(3, telemetryEntry.getFeatures().getExecuteMboxCount());
    assertEquals(1, telemetryEntry.getFeatures().getPrefetchViewCount());
    assertEquals(true, telemetryEntry.getFeatures().getExecutePageLoad());
    assertEquals(true, telemetryEntry.getFeatures().getPrefetchPageLoad());
    assertEquals(3, telemetryEntry.getFeatures().getPrefetchMboxCount());
    assertEquals("testID", telemetryEntry.getRequestId());
    assertEquals(ExecutionMode.EDGE, telemetryEntry.getMode());
    assertEquals(
        DecisioningMethod.SERVER_SIDE.toString(),
        telemetryEntry.getFeatures().getDecisioningMethod());
  }

  @Test
  void testExecutionModeOnDeviceWhenStatusOK() throws NoSuchFieldException {
    setup(true);
    TimingTool timer = new TimingTool();
    timer.timeStart(TIMING_EXECUTE_REQUEST);

    Context context = getContext();
    PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
    ExecuteRequest executeRequest = getMboxExecuteRequest();
    String nonDefaultToken = "non-default-token";

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .property(new Property().token(nonDefaultToken))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 200, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    TelemetryEntry telemetryEntry =
        telemetryService.createTelemetryEntry(
            targetDeliveryRequest, targetDeliveryResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));

    assert telemetryEntry != null;
    assertEquals(ExecutionMode.LOCAL, telemetryEntry.getMode());
  }

  @Test
  void testExecutionModeOnDeviceWithPartialContent() throws NoSuchFieldException {
    setup(true);
    TimingTool timer = new TimingTool();
    timer.timeStart(TIMING_EXECUTE_REQUEST);

    Context context = getContext();
    PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
    ExecuteRequest executeRequest = getMboxExecuteRequest();

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 206, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    TelemetryEntry telemetryEntry =
        telemetryService.createTelemetryEntry(
            targetDeliveryRequest, targetDeliveryResponse, timer.timeEnd(TIMING_EXECUTE_REQUEST));

    assert telemetryEntry != null;
    assertEquals(ExecutionMode.EDGE, telemetryEntry.getMode());
  }

  @Test
  void testAddTelemetry() throws NoSuchFieldException {
    setup(true);
    TimingTool timer = new TimingTool();
    timer.timeStart(TIMING_EXECUTE_REQUEST);

    Context context = getContext();
    PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
    ExecuteRequest executeRequest = getMboxExecuteRequest();
    String nonDefaultToken = "non-default-token";

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .property(new Property().token(nonDefaultToken))
            .decisioningMethod(DecisioningMethod.SERVER_SIDE)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 200, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    //   empty the  in memory stored telemetry
    telemetryService.getTelemetry();

    assertEquals(0, telemetryService.getTelemetry().getEntries().size());

    telemetryService.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);

    assertEquals(1, telemetryService.getTelemetry().getEntries().size());
  }
}
