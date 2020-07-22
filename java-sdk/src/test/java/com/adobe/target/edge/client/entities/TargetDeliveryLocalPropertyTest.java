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

import com.adobe.target.delivery.v1.model.Address;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.OptionType;
import com.adobe.target.delivery.v1.model.PrefetchMboxResponse;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.PrefetchResponse;
import com.adobe.target.delivery.v1.model.Property;
import com.adobe.target.delivery.v1.model.VisitorId;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryRequestBuilder;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.fileRuleLoader;
import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.getTestDeliveryResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TargetDeliveryLocalPropertyTest {

    public static final String PROPERTY_TEST_FILE = "DECISIONING_ARTIFACT_PROPERTY.json";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private TargetClient targetJavaClient;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() throws IOException, NoSuchFieldException {

        Mockito.lenient().doReturn(getTestDeliveryResponse())
                .when(defaultTargetHttpClient).execute(any(Map.class), any(String.class),
                any(DeliveryRequest.class), any(Class.class));

        ClientConfig clientConfig = ClientConfig.builder()
                .client("adobesummit2018")
                .organizationId("org")
                .build();

        DefaultTargetService targetService = new DefaultTargetService(clientConfig);
        OnDeviceDecisioningService localService = new OnDeviceDecisioningService(clientConfig, targetService);
        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        OnDeviceDecisioningDetailsExecutor decisionHandler = new OnDeviceDecisioningDetailsExecutor(clientConfig, mapper);

        targetJavaClient = TargetClient.create(clientConfig);

        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("localService"), localService);

        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("decisionHandler"), decisionHandler);

        fileRuleLoader(PROPERTY_TEST_FILE, localService);
    }

    @Test
    void testTargetDeliveryLocalRequestNoPropertyPasses() {
        List<Option> options = optionsForProperty(null, true);
        verifyPropertyResult(options);
    }


    @Test
    void testTargetDeliveryLocalRequestNullPropertyPasses() {
        List<Option> options = optionsForProperty(null, false);
        verifyPropertyResult(options);
    }

    @Test
    void testTargetDeliveryLocalRequestBlankPropertyPasses() {
        List<Option> options = optionsForProperty("", false);
        verifyPropertyResult(options);
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
        TargetDeliveryRequestBuilder builder = TargetDeliveryRequest.builder()
                .context(new Context().address(new Address().url("https://test.com")))
                .prefetch(new PrefetchRequest().addMboxesItem(new MboxRequest().name("superfluous-mbox").index(0)))
                .id(new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916874"))
                .decisioningMethod(DecisioningMethod.HYBRID);
        if (!noProperty) {
            builder.property(new Property().token(propertyToken));
        }
        TargetDeliveryRequest targetDeliveryRequest = builder.build();
        TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
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
        Map<String, Object> content = (Map<String, Object>)option.getContent();
        assertEquals(false, content.get("doMagic"));
        assertEquals(75, content.get("importantValue"));
    }
}
