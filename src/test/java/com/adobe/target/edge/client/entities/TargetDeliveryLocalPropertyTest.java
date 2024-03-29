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

import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.fileRuleLoader;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getTestDeliveryResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryRequestBuilder;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.edge.client.service.NotificationService;
import com.adobe.target.edge.client.service.TelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TargetDeliveryLocalPropertyTest {

  public static final String PROPERTY_TEST_FILE = "DECISIONING_ARTIFACT_PROPERTY.json";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private TargetClient targetJavaClient;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void init() throws IOException, NoSuchFieldException {

    Mockito.lenient()
        .doReturn(getTestDeliveryResponse())
        .when(defaultTargetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    ClientConfig clientConfig = ClientConfig.builder().organizationId("org").build();
    TelemetryService telemetryService = new TelemetryService(clientConfig);
    DefaultTargetService targetService = new DefaultTargetService(clientConfig, telemetryService);
    OnDeviceDecisioningService localService =
        new OnDeviceDecisioningService(clientConfig, targetService, telemetryService);
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
        mock(NotificationService.class));
    FieldSetter.setField(
        localService,
        localService.getClass().getDeclaredField("clusterLocator"),
        mock(ClusterLocator.class));

    fileRuleLoader(PROPERTY_TEST_FILE, localService);
  }

  @Test
  void testTargetDeliveryLocalRequestNoPropertyPasses() {
    List<Option> options = optionsForProperty(null, true);
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testTargetDeliveryLocalRequestNullPropertyPasses() {
    List<Option> options = optionsForProperty(null, false);
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testTargetDeliveryLocalRequestBlankPropertyPasses() {
    List<Option> options = optionsForProperty("", false);
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testTargetDeliveryLocalRequestPropertyPasses() {
    List<Option> options = optionsForProperty("a4d17d78-cb39-6171-b12d-a222a62ebe49", false);
    verifyPropertyResult(options);
  }

  @Test
  void testTargetDeliveryLocalRequestPropertyFails() {
    List<Option> options = optionsForProperty("f4d17d78-cb39-6171-b12d-a222a62ebe49", false);
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @SuppressWarnings("unchecked")
  private List<Option> optionsForProperty(String propertyToken, boolean noProperty) {
    TargetDeliveryRequestBuilder builder =
        TargetDeliveryRequest.builder()
            .context(new Context().address(new Address().url("https://test.com")))
            .prefetch(
                new PrefetchRequest()
                    .addMboxesItem(new MboxRequest().name("superfluous-mbox").index(0)))
            .id(new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916874"))
            .decisioningMethod(DecisioningMethod.HYBRID);
    if (!noProperty) {
      builder.property(new Property().token(propertyToken));
    }
    TargetDeliveryRequest targetDeliveryRequest = builder.build();
    TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
    assertNotNull(response);
    assertEquals(200, response.getStatus());
    verify(defaultTargetHttpClient, never())
        .execute(any(Map.class), any(String.class), eq(targetDeliveryRequest), any(Class.class));
    DeliveryResponse deliveryResponse = response.getResponse();
    assertNotNull(deliveryResponse);
    PrefetchResponse prefetchResponse = deliveryResponse.getPrefetch();
    assertNotNull(prefetchResponse);
    List<PrefetchMboxResponse> mboxResponses = prefetchResponse.getMboxes();
    assertNotNull(mboxResponses);
    assertEquals(1, mboxResponses.size());
    PrefetchMboxResponse mboxResponse = mboxResponses.get(0);
    assertNotNull(mboxResponse);
    assertEquals(0, mboxResponse.getIndex());
    assertEquals("superfluous-mbox", mboxResponse.getName());
    return mboxResponse.getOptions();
  }

  private void verifyPropertyResult(List<Option> options) {
    assertNotNull(options);
    assertEquals(1, options.size());
    Option option = options.get(0);
    assertNotNull(option);
    assertEquals(OptionType.JSON, option.getType());
    assertNotNull(option.getContent());
    assertTrue(option.getContent() instanceof Map);
    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) option.getContent();
    assertEquals(true, content.get("doMagic"));
    assertEquals(150, content.get("importantValue"));
  }
}
