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

import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private TargetClient targetJavaClient;
  private ClientConfig clientConfig;
  private DefaultTargetService targetService;
  private ClusterLocator clusterLocator;
  private OnDeviceDecisioningService localService;
  private NotificationService notificationService;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void init() throws NoSuchFieldException {

    Mockito.lenient()
        .doReturn(CompletableFuture.completedFuture(getTestDeliveryResponse()))
        .when(defaultTargetHttpClient)
        .executeAsync(
            any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).telemetryEnabled(false).build();

    TelemetryService telemetryService = new TelemetryService(clientConfig);
    targetService = new DefaultTargetService(clientConfig, telemetryService);
    clusterLocator = new ClusterLocator();
    notificationService = new NotificationService(targetService, clientConfig, clusterLocator);

    localService = new OnDeviceDecisioningService(clientConfig, targetService, telemetryService);
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
  @SuppressWarnings("unchecked")
  void testNotificationDeliveryService() {
    TargetDeliveryRequest localDeliveryRequest = localDeliveryRequest();
    notificationService.sendNotification(localDeliveryRequest);
    verify(defaultTargetHttpClient, timeout(1000))
        .executeAsync(
            any(Map.class),
            any(String.class),
            eq(localDeliveryRequest.getDeliveryRequest()),
            any(Class.class));
  }

  @Test
  void testNotificationDeliveryServiceCalled() throws NoSuchFieldException, IOException {
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
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);
    verify(targetServiceMock, timeout(1000)).executeNotificationAsync(any());
  }

  @Test
  void testNotificationDeliveryServiceNotCalled() throws NoSuchFieldException, IOException {
    NotificationService mockNotificationService = mock(NotificationService.class, RETURNS_DEFAULTS);
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("notificationService"),
        mockNotificationService);
    fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(new Context().channel(ChannelType.WEB))
            .prefetch(
                new PrefetchRequest().addMboxesItem(new MboxRequest().index(0).name("allmatches")))
            .decisioningMethod(DecisioningMethod.ON_DEVICE)
            .build();
    targetJavaClient.getOffers(targetDeliveryRequest);
    verify(mockNotificationService, never()).sendNotification(any());
  }

  private TargetDeliveryRequest localDeliveryRequest() {
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

    assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
    assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
    assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
    return targetDeliveryRequest;
  }
}
