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
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.ExecuteResponse;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.OptionType;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.PrefetchResponse;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.delivery.v1.model.View;
import com.adobe.target.delivery.v1.model.ViewRequest;
import com.adobe.target.delivery.v1.model.VisitorId;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import com.adobe.target.edge.client.ondevice.NotificationDeliveryService;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningDetailsExecutor;
import com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService;
import com.adobe.target.edge.client.ondevice.collator.ParamsCollator;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.fileRuleLoader;
import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.getLocalContext;
import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.getTestDeliveryResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryRequestLocalViewTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";

    static final class SelectorContent {
        private String type;
        private String selector;
        private String cssSelector;
        private Object content;
        private String eventToken;

        public SelectorContent(String type, String selector, String cssSelector, Object content, String eventToken) {
            this.type = type;
            this.selector = selector;
            this.cssSelector = cssSelector;
            this.content = content;
            this.eventToken = eventToken;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void assertMatches(Option option) {
            if (option.getType() == OptionType.ACTIONS) {
                assertEquals(eventToken, option.getEventToken());
                assertTrue(option.getContent() instanceof List);
                List contentList = (List) option.getContent();
                assertEquals(1, contentList.size());
                Map<String, String> optionContent = (Map<String, String>) contentList.get(0);
                assertEquals(type, optionContent.get("type"));
                assertEquals(selector, optionContent.get("selector"));
                assertEquals(cssSelector, optionContent.get("cssSelector"));
                assertEquals(content, optionContent.get("content"));
            }
            else if (option.getType() == null) {
                assertEquals(eventToken, option.getEventToken());
            }
            else {
                throw new IllegalArgumentException("unexpected option type");
            }
        }
    }

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
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("deliveryService"), mock(NotificationDeliveryService.class));
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("clusterLocator"), mock(ClusterLocator.class));
    }

    @Test
    void testTargetDeliveryLocalNamedViews() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_VIEWS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874",
                        Collections.emptyList(), false);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> contactOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "contact", false);
        List<SelectorContent> contactSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<span id=\"action_insert_15889715194712006\">Please do not contact us.</span>",
                    "DKyEy8B6J+arIj6GhXNW/AreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(127,0,0,1)");
                        put("priority", "important");
                    }},
                    "DKyEy8B6J+arIj6GhXNW/AreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(contactOptions, contactSelectors);

        List<Option> homeOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "home", false);
        List<SelectorContent> homeSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<p id=\"action_insert_15889689998702412\">experience A</p>",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "nobody home - exp A",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "Home - Experience B",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "experience B! Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut rhoncus, magna et dignissim ullamcorper, magna ipsum pharetra velit, vel egestas magna leo interdum urna. Etiam purus massa, accumsan in elit sit amet, posuere maximus augue. Donec non velit sit amet ipsum feugiat aliquet id in metus. Integer a auctor nisl. Donec ac lacinia eros. Proin nisl magna, bibendum ut tellus vitae, mattis laoreet lacus. Pellentesque mauris lorem, scelerisque quis nisi ac, vulputate tincidunt est. Maecenas ex justo, ultrices non neque sed, fermentum maximus diam. Vestibulum at facilisis magna. Ut eu tristique lectus. Proin gravida leo eu fermentum ullamcorper. Suspendisse gravida nibh vitae ultricies ultricies. Donec fermentum, metus id tincidunt dapibus, tellus lacus tristique felis, non posuere nibh ligula sed est.",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(homeOptions, homeSelectors);
    }

    @Test
    void testTargetDeliveryLocalNamedViewsJason() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_VIEWS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874",
                        Collections.emptyList(), false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getViews().get(0).setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> contactOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "contact", false);
        List<SelectorContent> contactSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertBefore",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<div id=\"action_insert_15889714592501888\">Please call Jason immediately</div>",
                    "DKyEy8B6J+arIj6GhXNW/GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(255,255,170,1)");
                        put("priority", "important");
                    }},
                    "DKyEy8B6J+arIj6GhXNW/GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(contactOptions, contactSelectors);

        List<Option> homeOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "home", false);
        List<SelectorContent> homeSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<p id=\"action_insert_15889690271122446\">jason = correct</p>",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "jason home - exp A",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<p id=\"action_insert_15889689998702412\">experience A</p>",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "nobody home - exp A",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "Home - Experience B",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "experience B! Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut rhoncus, magna et dignissim ullamcorper, magna ipsum pharetra velit, vel egestas magna leo interdum urna. Etiam purus massa, accumsan in elit sit amet, posuere maximus augue. Donec non velit sit amet ipsum feugiat aliquet id in metus. Integer a auctor nisl. Donec ac lacinia eros. Proin nisl magna, bibendum ut tellus vitae, mattis laoreet lacus. Pellentesque mauris lorem, scelerisque quis nisi ac, vulputate tincidunt est. Maecenas ex justo, ultrices non neque sed, fermentum maximus diam. Vestibulum at facilisis magna. Ut eu tristique lectus. Proin gravida leo eu fermentum ullamcorper. Suspendisse gravida nibh vitae ultricies ultricies. Donec fermentum, metus id tincidunt dapibus, tellus lacus tristique felis, non posuere nibh ligula sed est.",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(homeOptions, homeSelectors);
    }

    @Test
    void testTargetDeliveryLocalNamedViewsGreg() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_VIEWS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874",
                        Collections.emptyList(), false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("greg", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getViews().get(0).setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> contactOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "contact", false);
        List<SelectorContent> contactSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertBefore",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<span id=\"action_insert_15889714897491922\">Please email Greg immediately</span>",
                    "DKyEy8B6J+arIj6GhXNW/JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(170,255,255,1)");
                        put("priority", "important");
                    }},
                    "DKyEy8B6J+arIj6GhXNW/JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(contactOptions, contactSelectors);

        List<Option> homeOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "home", false);
        List<SelectorContent> homeSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<p id=\"action_insert_15889690455422475\">greg = correct</p>",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "greg home - exp A",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertAfter",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<p id=\"action_insert_15889689998702412\">experience A</p>",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "nobody home - exp A",
                    "ob3/yMzSVllQx2v2P7122GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > H3:nth-of-type(1)",
                    "#spa-content > H3:nth-of-type(1)",
                    "Home - Experience B",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "experience B! Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut rhoncus, magna et dignissim ullamcorper, magna ipsum pharetra velit, vel egestas magna leo interdum urna. Etiam purus massa, accumsan in elit sit amet, posuere maximus augue. Donec non velit sit amet ipsum feugiat aliquet id in metus. Integer a auctor nisl. Donec ac lacinia eros. Proin nisl magna, bibendum ut tellus vitae, mattis laoreet lacus. Pellentesque mauris lorem, scelerisque quis nisi ac, vulputate tincidunt est. Maecenas ex justo, ultrices non neque sed, fermentum maximus diam. Vestibulum at facilisis magna. Ut eu tristique lectus. Proin gravida leo eu fermentum ullamcorper. Suspendisse gravida nibh vitae ultricies ultricies. Donec fermentum, metus id tincidunt dapibus, tellus lacus tristique felis, non posuere nibh ligula sed est.",
                    "XQ6HqnRfrxl3ausAjtQJj5NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(homeOptions, homeSelectors);
    }

    @Test
    void testTargetDeliveryLocalNamedViewsBoth() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_VIEWS.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916874",
                        Collections.emptyList(), false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
            put("greg", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getViews().get(0).setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> contactOptions =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, "contact", false);
        List<SelectorContent> contactSelectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertBefore",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    "<div id=\"action_insert_15889714592501888\">Please call Jason immediately</div>",
                    "DKyEy8B6J+arIj6GhXNW/GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#spa-content > P:nth-of-type(1)",
                    "#spa-content > P:nth-of-type(1)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(255,255,170,1)");
                        put("priority", "important");
                    }},
                    "DKyEy8B6J+arIj6GhXNW/GqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(contactOptions, contactSelectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewAB() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_AB.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        Map<String, String> params = new HashMap<String, String>() {{
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getPageLoad().setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("insertBefore",
                    "HTML > BODY > DIV.offer:eq(0) > IMG:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(2) > IMG:nth-of-type(1)",
                    "<p id=\"action_insert_15887183492726231\">experience A</p>",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertAfter",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "<p id=\"action_insert_15882853393943012\">Life moves pretty fast. If you don’t stop and look around once in a while, you could miss it.</p>",
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#action_insert_15882853393943012",
                    "#action_insert_15882853393943012",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(86,255,86,1)");
                        put("priority", "important");
                    }},
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewABJason() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_AB.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getPageLoad().setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "jason is correct",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(170,255,255,1)");
                        put("priority", "important");
                    }},
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertBefore",
                    "HTML > BODY > DIV.offer:eq(0) > IMG:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(2) > IMG:nth-of-type(1)",
                    "<p id=\"action_insert_15887183492726231\">experience A</p>",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertAfter",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "<p id=\"action_insert_15882853393943012\">Life moves pretty fast. If you don’t stop and look around once in a while, you could miss it.</p>",
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#action_insert_15882853393943012",
                    "#action_insert_15882853393943012",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(86,255,86,1)");
                        put("priority", "important");
                    }},
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

     @Test
    void testTargetDeliveryLocalPageLoadViewABBoth() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_AB.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
            put("greg", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getPageLoad().setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    "greg is correct",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(127,255,0,1)");
                        put("priority", "important");
                    }},
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "jason is correct",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(170,255,255,1)");
                        put("priority", "important");
                    }},
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertBefore",
                    "HTML > BODY > DIV.offer:eq(0) > IMG:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(2) > IMG:nth-of-type(1)",
                    "<p id=\"action_insert_15887183492726231\">experience A</p>",
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "/SXLXTYTqMY5xlnwaQZrEWqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("insertAfter",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "<p id=\"action_insert_15882853393943012\">Life moves pretty fast. If you don’t stop and look around once in a while, you could miss it.</p>",
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setStyle",
                    "#action_insert_15882853393943012",
                    "#action_insert_15882853393943012",
                    new HashMap<String, String>() {{
                        put("background-color", "rgba(86,255,86,1)");
                        put("priority", "important");
                    }},
                    "6Na6eWan1u0HrN32JDT54JNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewXTBoth() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_XT.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
            put("greg", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getPageLoad().setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(1)",
                    "greg is correct",
                    "39UdigzDfmb97ogXP1PN62qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "Hello greg",
                    "39UdigzDfmb97ogXP1PN62qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN62qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN62qipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewXTJason() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_XT.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        Map<String, String> params = new HashMap<String, String>() {{
            put("jason", "correct");
        }};
        targetDeliveryRequest.getDeliveryRequest().getPrefetch().getPageLoad().setParameters(params);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN65NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "Hello jason",
                    "39UdigzDfmb97ogXP1PN65NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(2)",
                    "jason is correct",
                    "39UdigzDfmb97ogXP1PN65NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN65NWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewXTNone() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_XT.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, false);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, false);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN6wreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "Hello everyone",
                    "39UdigzDfmb97ogXP1PN6wreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    "39UdigzDfmb97ogXP1PN6wreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(3)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(3)",
                    "all visitors",
                    "39UdigzDfmb97ogXP1PN6wreqXMfVUcUx0s/BHR5kCKCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q=="));
        }};
        verifyContent(options, selectors);
    }

    @Test
    void testTargetDeliveryLocalPageLoadViewXTExecute() throws IOException, NoSuchFieldException {
        fileRuleLoader("DECISIONING_PAYLOAD_PAGELOAD_VEC_XT.json", localService);
        TargetDeliveryRequest targetDeliveryRequest =
                localDeliveryRequest("38734fba-262c-4722-b4a3-ac0a93916877", null, true);
        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        List<Option> options =
                extractOptions(targetDeliveryRequest, targetDeliveryResponse, null, true);
        List<SelectorContent> selectors = new ArrayList<SelectorContent>() {{
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    null));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "HTML > BODY > DIV:nth-of-type(1) > H1:nth-of-type(1)",
                    "Hello everyone",
                    null));
            add(new SelectorContent(null,
                    null,
                    null,
                    null,
                    null));
            add(new SelectorContent("setHtml",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(3)",
                    "HTML > BODY > UL:nth-of-type(1) > LI:nth-of-type(3)",
                    "all visitors",
                    null));
        }};
        verifyContent(options, selectors);
    }

    private TargetDeliveryRequest localDeliveryRequest(String visitorIdStr,
            List<String> views,
            boolean execute) {
        Context context = getLocalContext();
        context.setAddress(new Address().url("http://local-target-test/"));
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        if (views != null) {
            if (views.isEmpty()) {
                prefetchRequest.addViewsItem(new ViewRequest());
            } else {
                for (String viewName : views) {
                    prefetchRequest.addViewsItem(new ViewRequest().name(viewName));
                }
            }
        }
        else {
            prefetchRequest.setPageLoad(new RequestDetails());
        }
        ExecuteRequest executeRequest = null;
        if (execute) {
            executeRequest = new ExecuteRequest();
            executeRequest.setPageLoad(new RequestDetails());
        }
        VisitorId visitorId = new VisitorId().tntId(visitorIdStr);

        TargetDeliveryRequestBuilder targetDeliveryRequestBuilder = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .id(visitorId)
                .decisioningMethod(DecisioningMethod.ON_DEVICE);
        if (execute) {
            targetDeliveryRequestBuilder.execute(executeRequest);
        }
        TargetDeliveryRequest targetDeliveryRequest = targetDeliveryRequestBuilder.build();

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        return targetDeliveryRequest;
    }

    @SuppressWarnings("unchecked")
    private List<Option> extractOptions(TargetDeliveryRequest targetDeliveryRequest,
            TargetDeliveryResponse targetDeliveryResponse,
            String viewName,
            boolean execute) {
        DeliveryResponse response = targetDeliveryResponse.getResponse();
        assertNotNull(response);
        List<Option> options;
        if (execute) {
            ExecuteResponse executeResponse = response.getExecute();
            assertNotNull(executeResponse);
            options = executeResponse.getPageLoad().getOptions();
        }
        else {
            PrefetchResponse preResponse = response.getPrefetch();
            assertNotNull(preResponse);
            if (viewName != null) {
                List<View> views = preResponse.getViews();
                assertNotNull(views);
                options = new ArrayList<>();
                for (View view : views) {
                    if (view.getName().equals(viewName)) {
                        options.addAll(view.getOptions());
                    }
                }
            } else {
                options = preResponse.getPageLoad().getOptions();
            }
        }
        verify(defaultTargetHttpClient, never()).execute(any(Map.class), any(String.class),
                eq(targetDeliveryRequest), any(Class.class));
        verify(defaultTargetHttpClient, atMostOnce()).execute(any(Map.class), any(String.class),
                any(TargetDeliveryRequest.class), any(Class.class));
        return options;
    }

    private void verifyContent(List<Option> options, List<SelectorContent> expectedContent) {
        assertNotNull(options);
        assertEquals(expectedContent.size(), options.size());
        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);
            SelectorContent content = expectedContent.get(i);
            content.assertMatches(option);
        }
    }

}
