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
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
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
import java.util.concurrent.CompletableFuture;
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

  private static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private TargetClient targetJavaClient;
  private ClientConfig clientConfig;
  private ClusterLocator clusterLocator;
  private OnDeviceDecisioningService localService;
  private TelemetryService telemetryServiceSpy;

  @SuppressWarnings("unchecked")
  void setup(boolean telemetryEnabled, DecisioningMethod decisioningMethod, String clientName)
      throws NoSuchFieldException {
    VisitorProvider.init(TEST_ORG_ID);
    clientConfig =
        ClientConfig.builder()
            .client(clientName)
            .organizationId(TEST_ORG_ID)
            .defaultDecisioningMethod(decisioningMethod)
            .telemetryEnabled(telemetryEnabled)
            .build();

    telemetryServiceSpy = spy(new TelemetryService(clientConfig));
    DefaultTargetService targetService =
        new DefaultTargetService(clientConfig, telemetryServiceSpy);
    clusterLocator = new ClusterLocator();
    NotificationService notificationService =
        new NotificationService(targetService, clientConfig, clusterLocator);
    FieldSetter.setField(
        targetService,
        targetService.getClass().getDeclaredField("targetHttpClient"),
        defaultTargetHttpClient);
    Mockito.lenient()
        .doReturn(CompletableFuture.completedFuture(getTestDeliveryResponse()))
        .when(defaultTargetHttpClient)
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
    Mockito.lenient()
        .doReturn(getTestDeliveryResponse())
        .when(defaultTargetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));
    localService = new OnDeviceDecisioningService(clientConfig, targetService, telemetryServiceSpy);
    ObjectMapper mapper = new JacksonObjectMapper().getMapper();
    OnDeviceDecisioningDetailsExecutor decisionHandler =
        new OnDeviceDecisioningDetailsExecutor(clientConfig, mapper);
    targetJavaClient = TargetClient.create(clientConfig);

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

  /**
   * First call is for location hint, it goes directly inside executeRequestAsync(). Telemetry
   * Service gets called. second time telemetry data gets added in OnDeviceDecisioningService &
   * third time inside targetService.executeNotificationAsync()
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetryForODD() throws NoSuchFieldException, IOException {
    setup(true, DecisioningMethod.ON_DEVICE, "testTelemetryForODD");

    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();

    targetJavaClient.getOffers(targetDeliveryRequest);

    verify(telemetryServiceSpy, times(2)).getTelemetry();
    verify(telemetryServiceSpy, times(3))
        .addTelemetry(
            any(TargetDeliveryRequest.class),
            any(TimingTool.class),
            any(TargetDeliveryResponse.class));
  }

  /**
   * This test is executed on-device for this Hybrid request.
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetryForHybrid() throws NoSuchFieldException, IOException {
    setup(true, DecisioningMethod.HYBRID, "testTelemetryForHybrid");

    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.HYBRID)
            .build();

    targetJavaClient.getOffers(targetDeliveryRequest);

    verify(telemetryServiceSpy, times(2)).getTelemetry();
    verify(telemetryServiceSpy, times(3))
        .addTelemetry(
            any(TargetDeliveryRequest.class),
            any(TimingTool.class),
            any(TargetDeliveryResponse.class));
  }

  /**
   * Test case to call get offers for server side, in first request we capture the telemetry & in
   * next call we send it with any getOffers() call or sendNotifications() call
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testTelemetryForServerSide() throws NoSuchFieldException {
    setup(true, DecisioningMethod.SERVER_SIDE, "testTelemetryForServerSide");
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

    TargetDeliveryResponse targetDeliveryResponse1 =
        targetJavaClient.getOffers(targetDeliveryRequest);
    assertNull(targetDeliveryResponse1.getRequest().getTelemetry());

    // In next call we see telemetry data added to the deliveryRequest
    TargetDeliveryResponse targetDeliveryResponse2 =
        targetJavaClient.getOffers(targetDeliveryRequest);

    verify(telemetryServiceSpy, atLeast(2)).getTelemetry();
    verify(telemetryServiceSpy, times(2))
        .addTelemetry(
            any(TargetDeliveryRequest.class),
            any(TimingTool.class),
            any(TargetDeliveryResponse.class));
    assertEquals(1, telemetryServiceSpy.getTelemetry().getEntries().size());
    assertNotNull(targetDeliveryResponse2);
    assertNotNull(targetDeliveryResponse2.getRequest());
    assertNotNull(targetDeliveryResponse2.getRequest().getTelemetry());
    assertEquals(1, targetDeliveryResponse2.getRequest().getTelemetry().getEntries().size());
  }

  /**
   * Test case for server-side decisioning that calls getOffers() once and then sendNotifications()
   * once. This is to verify (and document the use case) telemetry is being added to any request
   * that goes to Delivery API, regardless of which method is being called on TargetClient by the
   * user.
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testTelemetryForServerSideSendNotification() throws NoSuchFieldException {
    setup(true, DecisioningMethod.SERVER_SIDE, "testTelemetryForServerSideSendNotification");
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

    TargetDeliveryResponse targetDeliveryResponse1 =
        targetJavaClient.getOffers(targetDeliveryRequest);
    assertNull(targetDeliveryResponse1.getRequest().getTelemetry());

    targetJavaClient.sendNotifications(targetDeliveryRequest);

    verify(telemetryServiceSpy, atLeast(2)).getTelemetry();
    verify(telemetryServiceSpy, times(2))
        .addTelemetry(
            any(TargetDeliveryRequest.class),
            any(TimingTool.class),
            any(TargetDeliveryResponse.class));
    assertEquals(1, telemetryServiceSpy.getTelemetry().getEntries().size());
  }

  /**
   * Test case with Hybrid decisioning that gets executed server-side. We are verifying telemetry
   * call for Hybrid decisioning -> server-side execution mode.
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testTelemetryForHybridServerSideCall() throws NoSuchFieldException {
    setup(true, DecisioningMethod.HYBRID, "testTelemetryForHybridServerSideCall");
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
            .decisioningMethod(DecisioningMethod.HYBRID)
            .build();

    targetJavaClient.getOffers(targetDeliveryRequest);
    verify(telemetryServiceSpy, atLeast(2)).getTelemetry();
    verify(telemetryServiceSpy, times(2))
        .addTelemetry(
            any(TargetDeliveryRequest.class),
            any(TimingTool.class),
            any(TargetDeliveryResponse.class));
    assertEquals(1, telemetryServiceSpy.getTelemetry().getEntries().size());
  }

  /**
   * Check telemetryEntry in execute call
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetrySentOnExecute() throws NoSuchFieldException, IOException {
    setup(true, DecisioningMethod.ON_DEVICE, "testTelemetrySentOnExecute");

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

    Telemetry telemetry = telemetryServiceSpy.getTelemetry();

    assertNotNull(telemetry);

    assertEquals(telemetry.getEntries().size(), 2);
    TelemetryEntry telemetryEntry = telemetry.getEntries().get(1);

    assertTrue(telemetryEntry.getTimestamp() > timestamp);
    assertTrue(telemetryEntry.getExecution() > 0);
    assertTrue(telemetryEntry.getRequestId().length() > 0);
    assertEquals(telemetryEntry.getFeatures().getDecisioningMethod(), "on-device");
  }
  /**
   * Check telemetryEntry in prefetch call
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetrySentOnPrefetch() throws NoSuchFieldException, IOException {
    setup(true, DecisioningMethod.ON_DEVICE, "testTelemetrySentOnPrefetch");
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

    Telemetry telemetry = telemetryServiceSpy.getTelemetry();
    assertNotNull(telemetry);
    TelemetryEntry telemetryEntry = telemetry.getEntries().get(1);

    assertTrue(telemetryEntry.getTimestamp() > timestamp);
    assertTrue(telemetryEntry.getExecution() > 0);
    assertTrue(telemetryEntry.getRequestId().length() > 0);
    assertEquals(telemetryEntry.getFeatures().getDecisioningMethod(), "on-device");
  }

  /**
   * When telemetryEnabled flag is set to false verify we don't store telemetry data
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetryNotSentPrefetch() throws NoSuchFieldException, IOException {
    setup(false, DecisioningMethod.ON_DEVICE, "testTelemetryNotSentPrefetch");
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

  /**
   * When telemetryEnabled flag is set to false verify we don't store telemetry data
   *
   * @throws NoSuchFieldException
   * @throws IOException
   */
  @Test
  void testTelemetryNotSentExecute() throws NoSuchFieldException, IOException {
    setup(false, DecisioningMethod.ON_DEVICE, "testTelemetryNotSentExecute");
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

  /**
   * Verifying all telemetry features which gets added for server side decisioning
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testAddTelemetryForServerSide() throws NoSuchFieldException {
    setup(true, DecisioningMethod.SERVER_SIDE, "testAddTelemetryForServerSide");
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
    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);
    TelemetryEntry telemetryEntry = telemetryServiceSpy.getTelemetry().getEntries().get(0);
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

  /**
   * Test to verify telemetryEntry has correct executionMode For ODD & status 200 it should be local
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testExecutionModeOnDeviceWhenStatusOK() throws NoSuchFieldException {
    setup(true, DecisioningMethod.ON_DEVICE, "testExecutionModeOnDeviceWhenStatusOK");
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

    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);
    TelemetryEntry telemetryEntry = telemetryServiceSpy.getTelemetry().getEntries().get(1);
    assert telemetryEntry != null;
    assertEquals(ExecutionMode.LOCAL, telemetryEntry.getMode());
  }

  /**
   * Test to verify telemetryEntry has correct executionMode For hybrid & status 200 we should have
   * mode as local
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testExecutionModeHybridWhenStatusOK() throws NoSuchFieldException {
    setup(true, DecisioningMethod.HYBRID, "testExecutionModeHybridWhenStatusOK");
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
            .decisioningMethod(DecisioningMethod.HYBRID)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 200, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);
    TelemetryEntry telemetryEntry = telemetryServiceSpy.getTelemetry().getEntries().get(1);
    assert telemetryEntry != null;
    assertEquals(ExecutionMode.LOCAL, telemetryEntry.getMode());
  }

  /**
   * Test to verify telemetryEntry has correct executionMode With partial content 206 status &
   * hybrid our mode should be edge
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testExecutionModeHybridWithPartialContent() throws NoSuchFieldException {
    setup(true, DecisioningMethod.HYBRID, "testExecutionModeHybridWithPartialContent");
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
            .decisioningMethod(DecisioningMethod.HYBRID)
            .build();

    DeliveryResponse deliveryResponse = new DeliveryResponse();
    deliveryResponse.setClient("SUMMIT_TEST2021");

    TargetDeliveryResponse targetDeliveryResponse =
        new TargetDeliveryResponse(targetDeliveryRequest, deliveryResponse, 206, "test call");
    targetDeliveryResponse.getResponse().setRequestId("testID");

    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);
    TelemetryEntry telemetryEntry = telemetryServiceSpy.getTelemetry().getEntries().get(1);
    assert telemetryEntry != null;
    assertEquals(ExecutionMode.EDGE, telemetryEntry.getMode());
  }

  /**
   * Test to verify telemetryEntry has correct executionMode For partial content & ODD it should be
   * edge
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testExecutionModeOnDeviceWithPartialContent() throws NoSuchFieldException {
    setup(true, DecisioningMethod.ON_DEVICE, "testExecutionModeOnDeviceWithPartialContent");
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

    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);
    Telemetry telemetry = telemetryServiceSpy.getTelemetry();

    assertEquals(ExecutionMode.EDGE, telemetry.getEntries().get(1).getMode());
  }

  /**
   * Verify if telemetry data is added correctly
   *
   * @throws NoSuchFieldException
   */
  @Test
  void testAddTelemetry() throws NoSuchFieldException {
    setup(true, DecisioningMethod.SERVER_SIDE, "testAddTelemetry");
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
    telemetryServiceSpy.getTelemetry();

    assertEquals(0, telemetryServiceSpy.getTelemetry().getEntries().size());

    telemetryServiceSpy.addTelemetry(targetDeliveryRequest, timer, targetDeliveryResponse);

    assertEquals(1, telemetryServiceSpy.getTelemetry().getEntries().size());
  }
}
