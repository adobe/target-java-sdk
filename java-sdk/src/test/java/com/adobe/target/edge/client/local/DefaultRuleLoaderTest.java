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
package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRuleLoaderTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
    static final String TEST_RULE_SET = "{\"version\":\"1.0.0\",\"meta\":{\"generatedAt\":\"2020-02-27T23:27:26.445Z\"},\"rules\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632283,\"content\":{\"test\":true,\"experience\":\"a\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mboxes\":[\"testoffer\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632285,\"content\":{\"test\":true,\"offer\":1},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mboxes\":[\"testoffer2\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632284,\"content\":{\"test\":true,\"experience\":\"b\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mboxes\":[\"testoffer\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632286,\"content\":{\"test\":true,\"offer\":2},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mboxes\":[\"testoffer2\"],\"views\":[]}}]}";
    static final String TEST_RULE_SET_HIGHER_VERSION = "{\"version\":\"2.0.0\",\"meta\":{\"generatedAt\":\"2020-02-27T23:27:26.445Z\"},\"rules\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632283,\"content\":{\"test\":true,\"experience\":\"a\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mboxes\":[\"testoffer\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632285,\"content\":{\"test\":true,\"offer\":1},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRGqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":0,\"type\":\"ab\",\"mboxes\":[\"testoffer2\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632284,\"content\":{\"test\":true,\"experience\":\"b\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mboxes\":[\"testoffer\"],\"views\":[]}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"and\":[{\"and\":[{\"==\":[\"bar\",{\"var\":\"mbox.foo\"}]},{\"==\":[\"buz\",{\"substr\":[{\"var\":\"mbox.baz_lc\"},0,3]}]}]},{\"and\":[{\"or\":[{\"<=\":[1582790400000,{\"var\":\"current_timestamp\"},4736217600000]}]},{\"or\":[{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]},{\"==\":[{\"var\":\"current_day\"},\"7\"]}]},{\"<=\":[\"0900\",{\"var\":\"current_time\"},\"1745\"]}]},{\"and\":[{\"or\":[{\"==\":[{\"var\":\"current_day\"},\"1\"]},{\"==\":[{\"var\":\"current_day\"},\"2\"]},{\"==\":[{\"var\":\"current_day\"},\"3\"]},{\"==\":[{\"var\":\"current_day\"},\"4\"]},{\"==\":[{\"var\":\"current_day\"},\"5\"]}]},{\"<=\":[\"0515\",{\"var\":\"current_time\"},\"1940\"]}]}]}]},{\"and\":[{\"==\":[{\"var\":\"user.browserType\"},\"firefox\"]},{\"and\":[{\">=\":[{\"var\":\"user.browserVersion\"},72]},{\"!=\":[{\"var\":\"user.browserType\"},\"ipad\"]}]}]},{\"and\":[{\"in\":[\"foo\",{\"var\":\"page.query\"}]},{\"==\":[\".jpg\",{\"substr\":[{\"var\":\"page.path\"},-4]}]},{\"==\":[\"ref1\",{\"var\":\"page.fragment_lc\"}]}]}]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"id\":632286,\"content\":{\"test\":true,\"offer\":2},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"IbG2Jz2xmHaqX7Ml/YRxRJNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"testoffer2\"}]},\"meta\":{\"activityId\":334694,\"experienceId\":1,\"type\":\"ab\",\"mboxes\":[\"testoffer2\"],\"views\":[]}}]}";

    TargetExceptionHandler exceptionHandler;

    ClientConfig clientConfig;

    @BeforeEach
    void init() {
        exceptionHandler = spy(new TargetExceptionHandler() {
            @Override
            public void handleException(TargetClientException e) {

            }
        });

        clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .localEnvironment("production")
                .exceptionHandler(exceptionHandler)
                .build();
    }

    static HttpResponse<LocalDecisioningRuleSet> getTestResponse(final String ruleSet, final String etag, final int status) {
        return new HttpResponse<LocalDecisioningRuleSet>() {
            @Override
            public int getStatus() {
                return status;
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public Headers getHeaders() {
                Headers headers = new Headers();
                if (etag != null) {
                    headers.add("ETag", etag);
                }
                return headers;
            }

            @Override
            public LocalDecisioningRuleSet getBody() {
                if (ruleSet == null) {
                    return null;
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
                try {
                    return mapper.readValue(ruleSet, new TypeReference<LocalDecisioningRuleSet>() {});
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Optional<UnirestParsingException> getParsingError() {
                return Optional.empty();
            }

            @Override
            public <V> V mapBody(Function<LocalDecisioningRuleSet, V> func) {
                return null;
            }

            @Override
            public <V> HttpResponse<V> map(Function<LocalDecisioningRuleSet, V> func) {
                return null;
            }

            @Override
            public HttpResponse<LocalDecisioningRuleSet> ifSuccess(Consumer<HttpResponse<LocalDecisioningRuleSet>> consumer) {
                return null;
            }

            @Override
            public HttpResponse<LocalDecisioningRuleSet> ifFailure(Consumer<HttpResponse<LocalDecisioningRuleSet>> consumer) {
                return null;
            }

            @Override
            public <E> HttpResponse<LocalDecisioningRuleSet> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
                return null;
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public <E> E mapError(Class<? extends E> errorClass) {
                return null;
            }
        };
    }

    @Test
    void testDefaultRuleLoader() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        String etag = "5b1cf3c050e1a0d16934922bf19ba6ea";
        Mockito.doReturn(getTestResponse(TEST_RULE_SET, etag, HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any(ClientConfig.class));

        defaultRuleLoader.start(clientConfig);
        verify(defaultRuleLoader, timeout(1000)).setLatestRules(any(LocalDecisioningRuleSet.class));
        verify(defaultRuleLoader, timeout(1000)).setLatestETag(eq(etag));
        LocalDecisioningRuleSet rules = defaultRuleLoader.getLatestRules();
        assertNotNull(rules);

        Mockito.doReturn(getTestResponse(TEST_RULE_SET, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_NOT_MODIFIED))
                .when(defaultRuleLoader).executeRequest(any(ClientConfig.class));

        defaultRuleLoader.refresh();
        verify(exceptionHandler, never()).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
    }

    @Test
    void testDefaultRuleLoaderNullResponse() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(getTestResponse(null, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any(ClientConfig.class));

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
    }

    @Test
    void testDefaultRuleLoaderInvalidVersion() {

        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(getTestResponse(TEST_RULE_SET_HIGHER_VERSION, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any(ClientConfig.class));

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
    }

    @Test
    void testDefaultRuleLoaderInvalidStatus() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(getTestResponse(TEST_RULE_SET, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_NOT_FOUND))
                .when(defaultRuleLoader).executeRequest(any(ClientConfig.class));

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
    }
}
