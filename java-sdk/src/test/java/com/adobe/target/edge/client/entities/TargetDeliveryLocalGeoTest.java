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
import com.adobe.target.delivery.v1.model.Geo;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.OptionType;
import com.adobe.target.delivery.v1.model.PrefetchMboxResponse;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.PrefetchResponse;
import com.adobe.target.delivery.v1.model.VisitorId;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.ondevice.NotificationDeliveryService;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.ondevice.client.geo.GeoClient;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TargetDeliveryLocalGeoTest {

    public static final String GEO_TEST_FILE = "DECISIONING_ARTIFACT_GEO.json";
    public static final String GEO_TEST_FAKE_FILE = "DECISIONING_ARTIFACT_GEO_FAKE.json";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private GeoClient mockGeoClient;
    private TargetClient targetJavaClient;
    private OnDeviceDecisioningService localService;

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
        localService = new OnDeviceDecisioningService(clientConfig, targetService);
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
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("deliveryService"), mock(NotificationDeliveryService.class));
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("clusterLocator"), mock(ClusterLocator.class));

        fileRuleLoader(GEO_TEST_FILE, localService);

        Geo geoResult = new Geo()
                .city("san francisco")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.24f);
        mockGeoClient = Mockito.spy(new GeoClient() {
            @Override
            public void start(ClientConfig clientConfig) {
            }

            @Override
            public Geo lookupGeo(String ip) {
                return geoResult;
            }

            @Override
            public void close() {
            }
        });
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("geoClient"), mockGeoClient);
    }

    @Test
    void testTargetDeliveryLocalRequestGeoPasses() {
        Geo sf = new Geo()
                .ipAddress("127.0.0.1")
                .city("san francisco")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(1, options.size());
        Option option = options.get(0);
        assertNotNull(option);
        assertEquals(OptionType.JSON, option.getType());
        assertNotNull(option.getContent());
        assertTrue(option.getContent() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>)option.getContent();
        assertEquals(true, content.get("geo"));
        assertEquals("geo.b", content.get("exp"));
        verify(mockGeoClient, atMostOnce()).start(any());
        verify(mockGeoClient, never()).lookupGeo(any());
        Map<String, Object> responseTokens = option.getResponseTokens();
        assertEquals("SANFRANCISCO", responseTokens.get("geo.city"));
        assertEquals("CA", responseTokens.get("geo.state"));
        assertEquals("US", responseTokens.get("geo.country"));
        assertEquals(37.74f, responseTokens.get("geo.latitude"));
        assertEquals(-122.24f, responseTokens.get("geo.longitude"));
    }

    @Test
    void testTargetDeliveryLocalRequestGeoIP() {
        String ip = "127.0.0.1";
        Geo ipGeo = new Geo().ipAddress(ip);
        List<Option> options = optionsForGeo(ipGeo);
        assertNotNull(options);
        assertEquals(1, options.size());
        Option option = options.get(0);
        assertNotNull(option);
        assertEquals(OptionType.JSON, option.getType());
        assertNotNull(option.getContent());
        assertTrue(option.getContent() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>)option.getContent();
        assertEquals(true, content.get("geo"));
        assertEquals("geo.b", content.get("exp"));
        verify(mockGeoClient, atMostOnce()).start(any());
        verify(mockGeoClient, atLeastOnce()).lookupGeo(eq(ip));
    }

    @Test
    void testTargetDeliveryLocalRequestGeoNoIPLookup() throws IOException, NoSuchFieldException {
        fileRuleLoader(GEO_TEST_FAKE_FILE, localService);
        String ip = "127.0.0.1";
        Geo ipGeo = new Geo().ipAddress(ip);
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(new Context().geo(ipGeo).address(new Address().url("https://test.com")))
                .prefetch(new PrefetchRequest().addMboxesItem(new MboxRequest().name("geo").index(0)))
                .id(new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916874"))
                .decisioningMethod(DecisioningMethod.ON_DEVICE)
                .build();
        TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        verify(mockGeoClient, atMostOnce()).start(any());
        verify(mockGeoClient, never()).lookupGeo(eq(ip));
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsLatitude() {
        Geo sf = new Geo()
                .city("san francisco")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.14f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsLongitude() {
        Geo sf = new Geo()
                .city("san francisco")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.84f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsCountry() {
        Geo sf = new Geo()
                .city("san francisco")
                .stateCode("ca")
                .countryCode("mx")
                .latitude(37.74f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsRegion() {
        Geo sf = new Geo()
                .city("san francisco")
                .stateCode("nv")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsCity() {
        Geo sf = new Geo()
                .city("sacramento")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(sf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testTargetDeliveryLocalRequestGeoFailsSSF()  {
        Geo ssf = new Geo()
                .city("south san francisco")
                .stateCode("ca")
                .countryCode("us")
                .latitude(37.74f)
                .longitude(-122.24f);
        List<Option> options = optionsForGeo(ssf);
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @SuppressWarnings("unchecked")
    private List<Option> optionsForGeo(Geo geo) {
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(new Context().geo(geo).address(new Address().url("https://test.com")))
                .prefetch(new PrefetchRequest().addMboxesItem(new MboxRequest().name("geo").index(0)))
                .id(new VisitorId().tntId("38734fba-262c-4722-b4a3-ac0a93916874"))
                .decisioningMethod(DecisioningMethod.HYBRID)
                .build();
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
        assertEquals("geo", mboxResponse.getName());
        return mboxResponse.getOptions();
    }
}
