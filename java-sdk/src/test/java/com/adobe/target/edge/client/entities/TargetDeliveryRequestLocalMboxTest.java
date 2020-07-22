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
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.ondevice.collator.ParamsCollator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryRequestLocalMboxTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private TargetClient targetJavaClient;

    private OnDeviceDecisioningService localService;

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
        ParamsCollator specificTimeCollator = TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1582818503000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);
    }

    @Test
    void testTargetDeliveryLocalRequestVisitor1() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_ADDRESS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.HYBRID,
                        "offer2");
        targetDeliveryRequest.getDeliveryRequest().getContext().setAddress(
                new Address().url("https://test.com?foo=bar"));
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "offer2");
        Map<String, Object> expectedContent = new HashMap<String, Object>() {{
            put("baz", 1);
        }};
        verifyJSONContent(prefetchOptions, expectedContent,
                "mWtD0yDAXMnesyQOa7/jS2qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
        Option option = prefetchOptions.get(0);
        Map<String, Object> responseTokens = option.getResponseTokens();
        assertEquals(333312, responseTokens.get("activity.id"));
        assertEquals("Form Based Activity - offer2 - Feb 19 2020, 10:34", responseTokens.get("activity.name"));
        assertEquals(0, responseTokens.get("experience.id"));
        assertEquals("Experience A", responseTokens.get("experience.name"));
        assertEquals(630815, responseTokens.get("offer.id"));
        assertEquals(2, responseTokens.get("option.id"));
        assertEquals("Offer2", responseTokens.get("option.name"));
        assertEquals("on-device", responseTokens.get("activity.decisioningMethod"));
    }

    @Test
    void testTargetDeliveryLocalRequestVisitor2() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_ADDRESS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("58734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.HYBRID,
                        "offer2");
        targetDeliveryRequest.getDeliveryRequest().getContext().setAddress(
                new Address().url("https://test.com?foo=bar"));
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "offer2");
        Map<String, Object> expectedContent = new HashMap<String, Object>() {{
            put("baz", 2);
        }};
        verifyJSONContent(prefetchOptions, expectedContent,
                "mWtD0yDAXMnesyQOa7/jS5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestAddressMbox() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_ADDRESS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("58734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.HYBRID,
                        "offer2");
        targetDeliveryRequest.getDeliveryRequest().getContext().setAddress(
                new Address().url("https://test.com"));
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getMboxes().get(0).setAddress(
                new Address().url("https://test.com?foo=bar"));
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "offer2");
        Map<String, Object> expectedContent = new HashMap<String, Object>() {{
            put("baz", 2);
        }};
        verifyJSONContent(prefetchOptions, expectedContent,
                "mWtD0yDAXMnesyQOa7/jS5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestWrongURL() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_ADDRESS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("58734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.HYBRID,
                        "offer2");
        targetDeliveryRequest.getDeliveryRequest().getContext().setAddress(
                new Address().url("https://test.com?foo=baz"));
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
            extractOptions(targetDeliveryRequest, targetDeliveryResponse, "offer2");
        assertEquals(0, prefetchOptions.size());
    }

    @Test
    void testTargetDeliveryLocalRequestBrowserChrome() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_BROWSER.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "browser-mbox");
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "browser-mbox");
        verifyHTMLContent(prefetchOptions, "<h1>it's chrome</h1>",
                "B8C2FP2IuBgmeJcDfXHjGpZBXFCzaoRRABbzIA9EnZOCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestWrongBrowserFirefox() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_BROWSER.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "browser-mbox");
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/71.0");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "browser-mbox");
        verifyHTMLContent(prefetchOptions, "<h1>it's firefox</h1>",
                "B8C2FP2IuBgmeJcDfXHjGpNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestBrowserNoMatch() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_BROWSER.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "browser-mbox");
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "browser-mbox");
        verifyHTMLContent(prefetchOptions, "<h1>not firefox, safari or chrome</h1>",
                "B8C2FP2IuBgmeJcDfXHjGmqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestTimeRange() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_TIMEFRAME.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "daterange-mbox");
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1582830000000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "daterange-mbox");
        verifyHTMLContent(prefetchOptions, "<strong>date range 1 (feb 27-29)</strong>",
                "wQY/V1IOYec8T4fAT5ww7unJlneZxJu5VqGhXCosHhWCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestTimeRange2() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_TIMEFRAME.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "daterange-mbox");
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1583348400000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "daterange-mbox");
        verifyHTMLContent(prefetchOptions, "<strong>date range 2 (mar 2 - 6)</strong>",
                "wQY/V1IOYec8T4fAT5ww7pNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestFriday() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_TIMEFRAME.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "daterange-mbox");
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1583521200000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "daterange-mbox");
        verifyHTMLContent(prefetchOptions, "<strong>it's friday</strong>",
                "wQY/V1IOYec8T4fAT5ww7hB3JWElmEno9qwHyGr0QvSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestOutTimeRange() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_TIMEFRAME.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916873", DecisioningMethod.HYBRID,
                        "daterange-mbox");
        ParamsCollator specificTimeCollator =
                TargetTestDeliveryRequestUtils.getSpecificTimeCollator(1590516000000L);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("timeParamsCollator"), specificTimeCollator);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "daterange-mbox");
        verifyHTMLContent(prefetchOptions, "<strong>default result</strong>",
                "wQY/V1IOYec8T4fAT5ww7mqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestParams() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PARAMS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("338e3c1e51f7416a8e1ccba4f81acea0.28_0", DecisioningMethod.HYBRID,
                        "redundant-mbox");
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getMboxes().get(0).setParameters(
                new HashMap<String, String>() {{
                    put("foo", "bar");
                }}
        );
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "redundant-mbox");
        Map<String, Object> expectedContent = new HashMap<String, Object>() {{
            put("foo", "bar");
            put("isFooBar", true);
            put("experience", "B");
        }};
        verifyJSONContent(prefetchOptions, expectedContent,
                "Zhwxeqy1O2r9Ske1YDA9bJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestParamsMismatch() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PARAMS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("338e3c1e51f7416a8e1ccba4f81acea0.28_0", DecisioningMethod.HYBRID,
                        "redundant-mbox");
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getMboxes().get(0).setParameters(
                new HashMap<String, String>() {{
                    put("foo", "bart");
                }}
        );
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "redundant-mbox");
        assertNotNull(prefetchOptions);
        assertEquals(0, prefetchOptions.size());
    }

    @Test
    void testTargetDeliveryLocalRequestPriority() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PRIORITIES.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("338e3c1e51f7416a8e1ccba4f81acea0.28_0", DecisioningMethod.HYBRID,
                        "kitty");
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/71.0");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        assertEquals(1, targetDeliveryResponse.getResponse().getPrefetch().getMboxes().size());
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "kitty");
       verifyHTMLContent(prefetchOptions, "<div>kitty high with targeting: Firefox</div>",
                "/DhjxnVDh9heBZ0MrYFLF2qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestPriority2() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PRIORITIES.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("238e3c1e51f7416a8e1ccba4f81acea0.28_0", DecisioningMethod.HYBRID,
                        "kitty");
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        assertEquals(1, targetDeliveryResponse.getResponse().getPrefetch().getMboxes().size());
        List<Option> prefetchOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "kitty");
        verifyHTMLContent(prefetchOptions, "<div>kitty high A</div>",
                "ruhwp7VESR7F74TJL2DV5WqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==");
    }

    @Test
    void testTargetDeliveryLocalRequestPageload() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_GLOBAL_MBOX.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.ON_DEVICE,
                        null);
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/71.0");
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        prefetchRequest.setPageLoad(new RequestDetails());
        targetDeliveryRequest.getDeliveryRequest().setPrefetch(prefetchRequest);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions = targetDeliveryResponse.getResponse().getPrefetch().getPageLoad().getOptions();
        assertNotNull(prefetchOptions);
        assertEquals(2, prefetchOptions.size());
        int matches = 0;
        for (Option option : prefetchOptions) {
            assertEquals(OptionType.HTML, option.getType());
            String preContent = (String) option.getContent();
            if (preContent.equals("<div>Firetime</div>")) {
                assertEquals("9FNM3ikASssS+sVoFXNulJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                        option.getEventToken());
                matches++;
            }
            else if (preContent.equals("<div>mouse</div>")) {
                assertEquals("5C2cbrGD+bQ5qOATNGy1AZNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                        option.getEventToken());
                matches++;
            }
            else {
                throw new IllegalStateException("unexpected content");
            }
        }
        assertEquals(2, matches);
    }

    @Test
    void testTargetDeliveryLocalRequestPageload2() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_GLOBAL_MBOX.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.ON_DEVICE,
                        null);
        targetDeliveryRequest.getDeliveryRequest().getContext().setUserAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        RequestDetails preload = new RequestDetails();
        preload.setParameters(
                new HashMap<String, String>() {{
                    put("foo", "bar");
                }}
        );
        prefetchRequest.setPageLoad(preload);
        targetDeliveryRequest.getDeliveryRequest().setPrefetch(prefetchRequest);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> prefetchOptions = targetDeliveryResponse.getResponse().getPrefetch().getPageLoad().getOptions();
        assertNotNull(prefetchOptions);
        assertEquals(3, prefetchOptions.size());
        int matches = 0;
        for (Option option : prefetchOptions) {
            assertEquals(OptionType.HTML, option.getType());
            String preContent = (String) option.getContent();
            if (preContent.equals("<div>Chrometastic</div>")) {
                assertEquals("9FNM3ikASssS+sVoFXNulGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                        option.getEventToken());
                matches++;
            }
            else if (preContent.equals("<div>foo=bar experience A</div>")) {
                assertEquals("0L1rCkDps3F+UEAm1B9A4GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                        option.getEventToken());
                matches++;
            }
            else if (preContent.equals("<div>mouse</div>")) {
                assertEquals("5C2cbrGD+bQ5qOATNGy1AZNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==",
                        option.getEventToken());
                matches++;
            }
            else {
                throw new IllegalStateException("unexpected content");
            }
        }
        assertEquals(3, matches);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTargetDeliveryAttributesLocalOnlyPartial() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_RECOMMENDATIONS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.ON_DEVICE,
                        "daterange-mbox");
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().addMboxesItem(
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
    @SuppressWarnings("unchecked")
    void testTargetDeliveryAttributesHybridRemote() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_RECOMMENDATIONS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874", DecisioningMethod.HYBRID,
                        "daterange-mbox");
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().addMboxesItem(
                new MboxRequest().index(2).name("recommendations"));
        TargetDeliveryResponse response = targetJavaClient.getOffers(targetDeliveryRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
    }

    private TargetDeliveryRequest localDeliveryRequest(String visitorIdStr,
            DecisioningMethod mode,
            String prefetchMbox) {
        Context context = getLocalContext();
        PrefetchRequest prefetchRequest = null;
        if (prefetchMbox != null) {
            prefetchRequest = getMboxPrefetchLocalRequest(prefetchMbox);
        }
        VisitorId visitorId = new VisitorId().tntId(visitorIdStr);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .id(visitorId)
                .decisioningMethod(mode)
                .build();

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        return targetDeliveryRequest;
    }

    @SuppressWarnings("unchecked")
    private List<Option> extractOptions(TargetDeliveryRequest targetDeliveryRequest,
            TargetDeliveryResponse targetDeliveryResponse,
            String prefetchMbox) {
        DeliveryResponse response = targetDeliveryResponse.getResponse();
        assertNotNull(response);
        PrefetchResponse preResponse = response.getPrefetch();
        assertNotNull(preResponse);
        List<PrefetchMboxResponse> preMboxes = preResponse.getMboxes();
        assertNotNull(preMboxes);
        List<Option> prefetchOptions = null;
        if (prefetchMbox != null) {
            assertEquals(1, preMboxes.size());
            PrefetchMboxResponse preMboxResponse = preMboxes.get(0);
            assertEquals(prefetchMbox, preMboxResponse.getName());
            assertEquals(1, preMboxResponse.getIndex());
            prefetchOptions = preMboxResponse.getOptions();
        } else {
            assertEquals(0, preMboxes.size());
        }
        ExecuteResponse execResponse = response.getExecute();
        assertNotNull(execResponse);
        List<MboxResponse> mboxes = execResponse.getMboxes();
        assertNotNull(mboxes);
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        return prefetchOptions;
    }

    private void verifyHTMLContent(List<Option> options, String expectedContent, String eventToken) {
        assertNotNull(options);
        assertEquals(1, options.size());
        Option option = options.get(0);
        assertEquals(OptionType.HTML, option.getType());
        assertEquals(eventToken, option.getEventToken());
        String preContent = (String) option.getContent();
        assertEquals(expectedContent, preContent);
    }

    @SuppressWarnings("unchecked")
    private void verifyJSONContent(List<Option> options, Map<String, Object> expectedContent, String eventToken) {
        assertNotNull(options);
        assertEquals(1, options.size());
        Option option = options.get(0);
        assertEquals(OptionType.JSON, option.getType());
        assertEquals(eventToken, option.getEventToken());
        Map<String, Object> preContent = (Map<String, Object>) option.getContent();
        for (Map.Entry<String, Object> entry : preContent.entrySet()) {
            assertEquals(expectedContent.get(entry.getKey()), entry.getValue());
        }
    }

}
