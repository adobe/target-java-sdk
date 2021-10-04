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
package com.adobe.target.edge.client.entities;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.service.NotificationDeliveryService;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.edge.client.service.TargetService;
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
class TargetTelemetryTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private TargetClient targetJavaClient;
  private ClientConfig clientConfig;
  private DefaultTargetService targetService;
  private ClusterLocator clusterLocator;
  private OnDeviceDecisioningService localService;
  private NotificationDeliveryService notificationDeliveryService;

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

    targetService = new DefaultTargetService(clientConfig);
    clusterLocator = new ClusterLocator();
    notificationDeliveryService = new NotificationDeliveryService(targetService, clientConfig, clusterLocator);

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
        localService.getClass().getDeclaredField("deliveryService"),
        notificationDeliveryService);
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
    NotificationDeliveryService notificationDeliveryService = new NotificationDeliveryService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("deliveryService"),
      notificationDeliveryService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);

    ArgumentCaptor<TargetDeliveryRequest> captor =
        ArgumentCaptor.forClass(TargetDeliveryRequest.class);
    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(captor.capture());

    Telemetry telemetry = captor.getValue().getDeliveryRequest().getTelemetry();

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
    NotificationDeliveryService notificationDeliveryService = new NotificationDeliveryService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("deliveryService"),
      notificationDeliveryService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .prefetch(
                new PrefetchRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);

    ArgumentCaptor<TargetDeliveryRequest> captor =
        ArgumentCaptor.forClass(TargetDeliveryRequest.class);
    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(captor.capture());

    Telemetry telemetry = captor.getValue().getDeliveryRequest().getTelemetry();

    assertNotNull(telemetry);

    assertEquals(telemetry.getEntries().size(), 1);

    TelemetryEntry telemetryEntry = telemetry.getEntries().get(0);

    assertTrue(telemetryEntry.getTimestamp() > timestamp);
    assertTrue(telemetryEntry.getExecution() > 0);
    assertTrue(telemetryEntry.getRequestId().length() > 0);
    assertEquals(telemetryEntry.getFeatures().getDecisioningMethod(), "on-device");
  }

  @Test
  void testTelemetryNotSentPrefetch() throws NoSuchFieldException, IOException {
    setup(false);

    NotificationDeliveryService mockNotificationDeliveryService =
        mock(NotificationDeliveryService.class, RETURNS_DEFAULTS);
    TargetService targetServiceMock = mock(TargetService.class, RETURNS_DEFAULTS);
    NotificationDeliveryService notificationDeliveryService = new NotificationDeliveryService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("deliveryService"),
      notificationDeliveryService);
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
    NotificationDeliveryService notificationDeliveryService = new NotificationDeliveryService(targetServiceMock, clientConfig, clusterLocator);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("deliveryService"),
      notificationDeliveryService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .execute(
                new ExecuteRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
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
    List<String> childKeys = Arrays.stream(com.adobe.target.edge.client.model.DecisioningMethod.values()).map(val -> val.name()).collect(Collectors.toList());
    List<String> parentKeys = Arrays.stream(com.adobe.target.delivery.v1.model.DecisioningMethod.values()).map(val -> val.name()).collect(Collectors.toList());

    assertEquals(childKeys, parentKeys);

    for (int i = 0; i < childKeys.size(); i++) {
        assertEquals(com.adobe.target.delivery.v1.model.DecisioningMethod.valueOf(parentKeys.get(i)).toString(), com.adobe.target.edge.client.model.DecisioningMethod.valueOf(childKeys.get(i)).toString());
    }
  }
}
