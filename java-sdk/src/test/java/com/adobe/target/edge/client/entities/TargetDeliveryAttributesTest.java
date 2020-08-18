/*
 * Copyright 2019 Adobe. All rights reserved.
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

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.Attributes;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.ondevice.NotificationDeliveryService;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningEvaluator;
import com.adobe.target.edge.client.ondevice.RuleLoader;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.ondevice.collator.ParamsCollator;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.edge.client.service.VisitorProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryAttributesTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private TargetClient targetJavaClient;
    private OnDeviceDecisioningService localService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() throws IOException, NoSuchFieldException {

        Mockito.lenient().doReturn(getTestDeliveryResponse())
                .when(defaultTargetHttpClient).execute(any(Map.class), any(String.class),
                any(DeliveryRequest.class), any(Class.class));

        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .build();

        VisitorProvider.init(TEST_ORG_ID);

        DefaultTargetService targetService = new DefaultTargetService(clientConfig);
        localService = new OnDeviceDecisioningService(clientConfig, targetService);

        targetJavaClient = TargetClient.create(clientConfig);

        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("localService"), localService);

        RuleLoader testRuleLoader =
                TargetTestDeliveryRequestUtils.getTestRuleLoaderFromFile("DECISIONING_PAYLOAD_ATTRIBUTES.json");
        OnDeviceDecisioningEvaluator evaluator = new OnDeviceDecisioningEvaluator(testRuleLoader);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("ruleLoader"), testRuleLoader);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("onDeviceDecisioningEvaluator"), evaluator);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("deliveryService"), mock(NotificationDeliveryService.class));
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("clusterLocator"), mock(ClusterLocator.class));

        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        OnDeviceDecisioningDetailsExecutor decisionHandler = new OnDeviceDecisioningDetailsExecutor(clientConfig, mapper);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("decisionHandler"), decisionHandler);
        ParamsCollator specificTimeCollator = TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1582818503000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);

    }

    @Test
    void testTargetDeliveryAttributesVisitor1() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874");
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2");
        validateInitialResponse(targetDeliveryRequest, attrs);
        validateResultA(attrs);
    }

    @Test
    void testTargetDeliveryAttributesVisitor2()  {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873");
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2");
        validateInitialResponse(targetDeliveryRequest, attrs);
        assertTrue(attrs.getBoolean("testoffer", "test", false));
        assertEquals("b", attrs.getString("testoffer", "experience"));
        assertEquals(9.99, attrs.getDouble("testoffer", "price", 0d), 0.0001);
        assertEquals("b", attrs.toMboxMap("testoffer").get("experience"));
        assertEquals("b", attrs.toMap().get("testoffer").get("experience"));
        assertEquals(2, attrs.getInteger("testoffer2", "offer", 0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTargetDeliveryAttributesNoRequest() {
        Attributes attrs = targetJavaClient.getAttributes(null, "testoffer", "testoffer2");
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(200, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        assertTrue(attrs.getBoolean("testoffer", "test", false));
        assertTrue(attrs.toMboxMap("testoffer").containsKey("experience"));
        assertTrue(attrs.toMboxMap("testoffer").containsKey("price"));
        assertTrue(attrs.toMboxMap("testoffer2").containsKey("offer"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTargetDeliveryAttributesAllMatches() throws IOException, NoSuchFieldException {
        String mbox = "allmatches";
        ArrayList<String> mboxes = new ArrayList<String>() {{
           add(mbox);
        }};
        ClientConfig clientConfig = ClientConfig.builder()
                .client("targettesting")
                .organizationId(TEST_ORG_ID)
                .onDeviceEnvironment("test")
                .defaultDecisioningMethod(DecisioningMethod.ON_DEVICE)
                .onDeviceAllMatchingRulesMboxes(mboxes)
                .build();
        DefaultTargetService targetService = new DefaultTargetService(clientConfig);
        localService = new OnDeviceDecisioningService(clientConfig, targetService);
        fileRuleLoader("DECISIONING_PAYLOAD_ALL_MATCHES.json", localService);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("localService"), localService);
        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);

        Context context = getLocalContext();
        PrefetchRequest prefetchRequest = getMboxPrefetchLocalRequest(mbox);
        VisitorId visitorId = new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916873");
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .id(visitorId)
                .decisioningMethod(DecisioningMethod.ON_DEVICE)
                .build();

        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest, mbox);
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(200, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        assertEquals(2, attrs.getInteger(mbox, "allmatches", 0));
        assertEquals("a", attrs.getString(mbox, "allmatches1_exp"));
        assertEquals("b", attrs.getString(mbox, "allmatches2_exp"));
    }

    private TargetDeliveryRequest localDeliveryRequest(String visitorIdStr) {
        Context context = getLocalContext();
        PrefetchRequest prefetchRequest = getMboxPrefetchLocalRequest("testoffer");
        ExecuteRequest executeRequest = getMboxExecuteLocalRequest("testoffer2");
        VisitorId visitorId = new VisitorId().tntId(visitorIdStr);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .id(visitorId)
                .decisioningMethod(DecisioningMethod.HYBRID)
                .build();

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        return targetDeliveryRequest;
    }

    @SuppressWarnings("unchecked")
    private void validateInitialResponse(TargetDeliveryRequest targetDeliveryRequest, Attributes attrs) {
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(200, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
    }

    private void validateResultA(Attributes attrs) {
        assertTrue(attrs.getBoolean("testoffer", "test", false));
        assertEquals("a", attrs.getString("testoffer", "experience"));
        assertEquals(12.99, attrs.getDouble("testoffer", "price", 0d), 0.0001);
        assertEquals("a", attrs.toMboxMap("testoffer").get("experience"));
        assertEquals("a", attrs.toMap().get("testoffer").get("experience"));
        assertEquals(1, attrs.getInteger("testoffer2", "offer", 0));
    }

}
