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
import com.adobe.target.edge.client.local.LocalDecisionHandler;
import com.adobe.target.edge.client.local.LocalDecisioningService;
import com.adobe.target.edge.client.local.ParamsCollator;
import com.adobe.target.edge.client.local.RuleLoader;
import com.adobe.target.edge.client.model.ExecutionMode;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryAttributesTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
    static final String TEST_RULE_SET = "{\"version\":\"1.0.0\",\"remoteMboxes\":[\"recommendations\"],\"meta\":{\"generatedAt\":\"2020-02-27T23:27:26.445Z\"},\"rules\":{\"mboxes\":{\"testoffer\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632283,\"content\":{\"test\":true,\"experience\":\"a\",\"price\":12.99},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632284,\"content\":{\"test\":true,\"experience\":\"b\",\"price\":9.99},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer\"}}],\"testoffer2\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632285,\"content\":{\"test\":true,\"offer\":1},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer2\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632286,\"content\":{\"test\":true,\"offer\":2},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer2\"}}]}}}";
    static final String TEST_RULE_SET_NO_AUDIENCE = "{\"version\":\"1.0.0\",\"remoteMboxes\":[\"recommendations\"],\"meta\":{\"generatedAt\":\"2020-02-27T23:27:26.445Z\"},\"rules\":{\"mboxes\":{\"testoffer\":[{\"condition\":{\"<\":[0,{\"var\":\"allocation\"},50]},\"consequence\":{\"options\":[{\"id\":632283,\"content\":{\"test\":true,\"experience\":\"a\",\"price\":12.99},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer\",\"views\":[]}},{\"condition\":{\"<\":[50,{\"var\":\"allocation\"},100]},\"consequence\":{\"options\":[{\"id\":632284,\"content\":{\"test\":true,\"experience\":\"b\",\"price\":9.99},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer\"}}],\"testoffer2\":[{\"condition\":{\"<\":[0,{\"var\":\"allocation\"},50]},\"consequence\":{\"options\":[{\"id\":632285,\"content\":{\"test\":true,\"offer\":1},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer2\"}},{\"condition\":{\"<\":[50,{\"var\":\"allocation\"},100]},\"consequence\":{\"options\":[{\"id\":632286,\"content\":{\"test\":true,\"offer\":2},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer2\"}}]}}}";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private TargetClient targetJavaClient;

    private LocalDecisioningService localService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() throws NoSuchFieldException {

        Mockito.lenient().doReturn(getTestDeliveryResponse())
                .when(defaultTargetHttpClient).execute(any(Map.class), any(String.class),
                any(DeliveryRequest.class), any(Class.class));

        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .build();

        DefaultTargetService targetService = new DefaultTargetService(clientConfig);
        localService = new LocalDecisioningService(clientConfig, targetService);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(TEST_RULE_SET);
        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        LocalDecisionHandler decisionHandler = new LocalDecisionHandler(clientConfig, mapper);

        targetJavaClient = TargetClient.create(clientConfig);

        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("localService"), localService);

        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("ruleLoader"), testRuleLoader);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("decisionHandler"), decisionHandler);
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1582818503000L);
        FieldSetter.setField(decisionHandler, decisionHandler.getClass()
                .getDeclaredField("timeCollator"), specificTimeCollator);
    }

    @Test
    void testTargetDeliveryAttributesVisitor1() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.HYBRID);
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2");
        validateInitialResponse(targetDeliveryRequest, attrs);
        validateResultA(attrs);
    }

    @Test
    void testTargetDeliveryAttributesVisitor2() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2");
        validateInitialResponse(targetDeliveryRequest, attrs);
        assertTrue(attrs.getFeatureBoolean("testoffer", "test"));
        assertEquals("b", attrs.getFeatureString("testoffer", "experience"));
        assertEquals(9.99, attrs.getFeatureDouble("testoffer", "price"), 0.0001);
        assertEquals("b", attrs.toMap("testoffer").get("experience"));
        assertEquals(2, attrs.getFeatureInteger("testoffer2", "offer"));
    }

    @Test
    void testTargetDeliveryAttributesLocalOnly() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.LOCAL);
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2", "recommendations");
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(206, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        validateResultA(attrs);
    }

    @Test
    void testTargetDeliveryAttributesHybridRemote() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.HYBRID);
        Attributes attrs = targetJavaClient.getAttributes(targetDeliveryRequest,
                "testoffer", "testoffer2", "recommendations");
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(200, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
    }

    @Test
    void testTargetDeliveryAttributesNoRequest() throws NoSuchFieldException {
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(TEST_RULE_SET_NO_AUDIENCE);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("ruleLoader"), testRuleLoader);
        Attributes attrs = targetJavaClient.getAttributes(null, "testoffer", "testoffer2");
        assertNotNull(attrs);
        assertNotNull(attrs.getResponse());
        assertEquals(200, attrs.getResponse().getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        assertTrue(attrs.getFeatureBoolean("testoffer", "test"));
        assertTrue(attrs.toMap("testoffer").containsKey("experience"));
        assertTrue(attrs.toMap("testoffer").containsKey("price"));
        assertTrue(attrs.toMap("testoffer2").containsKey("offer"));
    }

    private TargetDeliveryRequest localDeliveryRequest(String visitorIdStr, ExecutionMode mode) {
        Context context = getLocalContext();
        PrefetchRequest prefetchRequest = getMboxPrefetchLocalRequest();
        ExecuteRequest executeRequest = getMboxExecuteLocalRequest();
        VisitorId visitorId = new VisitorId().tntId(visitorIdStr);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .id(visitorId)
                .executionMode(mode)
                .build();

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        return targetDeliveryRequest;
    }

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
        assertTrue(attrs.getFeatureBoolean("testoffer", "test"));
        assertEquals("a", attrs.getFeatureString("testoffer", "experience"));
        assertEquals(12.99, attrs.getFeatureDouble("testoffer", "price"), 0.0001);
        assertEquals("a", attrs.toMap("testoffer").get("experience"));
        assertEquals(1, attrs.getFeatureInteger("testoffer2", "offer"));
    }

}
