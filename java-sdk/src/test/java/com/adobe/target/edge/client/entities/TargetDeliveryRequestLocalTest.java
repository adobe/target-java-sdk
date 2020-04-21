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
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.local.LocalDecisioningService;
import com.adobe.target.edge.client.local.ParamsCollator;
import com.adobe.target.edge.client.local.RuleLoader;
import com.adobe.target.edge.client.model.ExecutionMode;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.DefaultTargetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryRequestLocalTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
    static final String TEST_RULE_SET = "{\"version\":\"1.0.0\",\"remoteMboxes\":[\"recommendations\"],\"meta\":{\"generatedAt\":\"2020-02-27T23:27:26.445Z\"},\"rules\":{\"mboxes\":{\"testoffer\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632283,\"content\":{\"test\":true,\"experience\":\"a\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632284,\"content\":{\"test\":true,\"experience\":\"b\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer\"}}],\"testoffer2\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632285,\"content\":{\"test\":true,\"offer\":1},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"testoffer2\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"options\":[{\"id\":632286,\"content\":{\"test\":true,\"offer\":2},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"testoffer2\"}}]}}}";

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

        targetJavaClient = TargetClient.create(clientConfig);

        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("localService"), localService);

        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(TEST_RULE_SET);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("ruleLoader"), testRuleLoader);
        ParamsCollator specificTimeCollator = TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1582818503000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeCollator"), specificTimeCollator);
    }

    @Test
    void testTargetDeliveryLocalRequestVisitor1() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.HYBRID);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                "IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                "IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                1, "a");
    }

    @Test
    void testTargetDeliveryLocalRequestVisitor2() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                "IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                "IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                2, "b");
    }

    @Test
    void testTargetDeliveryLocalRequestWrongBrowser() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                null, null, 0, null);
    }

    @Test
    void testTargetDeliveryLocalRequestWrongBrowserVersion() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/71.0");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                null, null, 0, null);
    }

    @Test
    void testTargetDeliveryLocalRequestMBoxAddress() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        Address address = targetDeliveryRequest.getDeliveryRequest().getContext().getAddress();
        targetDeliveryRequest.getDeliveryRequest().getContext().setAddress(new Address().url("https://wrong.com"));
        targetDeliveryRequest.getDeliveryRequest().getExecute().getMboxes().get(0).setAddress(address);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                null,
                "IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                2, null);
    }

    @Test
    void testTargetDeliveryLocalRequestWrongTime() throws NoSuchFieldException {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", ExecutionMode.HYBRID);
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1583625037000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeCollator"), specificTimeCollator);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyLocalServerState(targetDeliveryRequest, targetDeliveryResponse,
                null, null, 0, null);
    }

    @Test
    void testTargetDeliveryAttributesLocalOnlyPartial() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.LOCAL);
        targetDeliveryRequest.getDeliveryRequest().getExecute().addMboxesItem(
                new MboxRequest().index(2).name("recommendations"));
        TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
        assertNotNull(response);
        assertEquals(206, response.getStatus());
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
    }

    @Test
    void testTargetDeliveryAttributesHybridRemote() {
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", ExecutionMode.HYBRID);
        targetDeliveryRequest.getDeliveryRequest().getExecute().addMboxesItem(
                new MboxRequest().index(2).name("recommendations"));
        TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
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

    private void verifyLocalServerState(TargetDeliveryRequest targetDeliveryRequest,
                                        TargetDeliveryResponse targetDeliveryResponse,
                                        String preToken, String execToken, int offer, String experience) {
        DeliveryResponse response = targetDeliveryResponse.getResponse();
        assertNotNull(response);
        PrefetchResponse preResponse = response.getPrefetch();
        assertNotNull(preResponse);
        List<PrefetchMboxResponse> preMboxes = preResponse.getMboxes();
        assertNotNull(preMboxes);
        assertEquals(1, preMboxes.size());
        PrefetchMboxResponse preMboxResponse = preMboxes.get(0);
        assertEquals("testoffer", preMboxResponse.getName());
        assertEquals(1, preMboxResponse.getIndex());
        if (preToken != null) {
            List<Metric> preMetrics = preMboxResponse.getMetrics();
            assertNotNull(preMetrics);
            assertEquals(1, preMetrics.size());
            Metric preMetric = preMetrics.get(0);
            assertEquals(preToken, preMetric.getEventToken());
            assertEquals(MetricType.DISPLAY, preMetric.getType());
            List<Option> preOptions = preMboxResponse.getOptions();
            assertNotNull(preOptions);
            assertEquals(1, preOptions.size());
            Option preOption = preOptions.get(0);
            assertEquals(OptionType.JSON, preOption.getType());
            @SuppressWarnings("unchecked")
            Map<String, Object> preContent = (Map<String, Object>) preOption.getContent();
            assertNotNull(preContent);
            assertEquals(true, preContent.get("test"));
            assertEquals(experience, preContent.get("experience"));
        }
        else {
            List<Metric> preMetrics = preMboxResponse.getMetrics();
            assertNotNull(preMetrics);
            assertEquals(0, preMetrics.size());
            List<Option> preOptions = preMboxResponse.getOptions();
            assertNotNull(preOptions);
            assertEquals(0, preOptions.size());
        }
        ExecuteResponse execResponse = response.getExecute();
        assertNotNull(execResponse);
        List<MboxResponse> mboxes = execResponse.getMboxes();
        assertNotNull(mboxes);
        assertEquals(1, mboxes.size());
        MboxResponse mboxResponse = mboxes.get(0);
        assertEquals("testoffer2", mboxResponse.getName());
        assertEquals(1, mboxResponse.getIndex());
        if (execToken != null) {
            List<Metric> metrics = mboxResponse.getMetrics();
            assertNotNull(metrics);
            assertEquals(1, metrics.size());
            Metric metric = metrics.get(0);
            assertEquals(execToken, metric.getEventToken());
            assertEquals(MetricType.DISPLAY, metric.getType());
            List<Option> options = mboxResponse.getOptions();
            assertNotNull(options);
            assertEquals(1, options.size());
            Option option = options.get(0);
            assertEquals(OptionType.JSON, option.getType());
            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) option.getContent();
            assertNotNull(content);
            assertEquals(true, content.get("test"));
            assertEquals(offer, content.get("offer"));
        }
        else {
            List<Metric> metrics = mboxResponse.getMetrics();
            assertNotNull(metrics);
            assertEquals(0, metrics.size());
            List<Option> options = mboxResponse.getOptions();
            assertNotNull(options);
            assertEquals(0, options.size());
        }
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
    }

}
