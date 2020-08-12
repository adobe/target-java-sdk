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
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningHandler;
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
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRuleLoaderTest {

    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
    static final String TEST_RULE_SET = "{\"version\":\"1.0.0\",\"meta\":{\"generatedAt\":\"2020-03-17T22:29:29.115Z\",\"remoteMboxes\":[\"recommendations\"],\"globalMbox\":\"target-global-mbox\"},\"rules\":{\"mboxes\":{\"product\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"<=\":[1580371200000,{\"var\":\"current_timestamp\"},1600585200000]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"content\":{\"product\":\"default\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"3eLgpLF+APtuSsE47wxq/mqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"product\"}]},\"meta\":{\"activityId\":317586,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"product\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"<=\":[1580371200000,{\"var\":\"current_timestamp\"},1600585200000]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"content\":{\"product\":\"new_layout\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"3eLgpLF+APtuSsE47wxq/pNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"product\"}]},\"meta\":{\"activityId\":317586,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"product\"}}]},\"views\":{}}}";
    static final String TEST_RULE_SET_HIGHER_VERSION = "{\"version\":\"2.0.0\",\"meta\":{\"generatedAt\":\"2020-03-17T22:29:29.115Z\",\"remoteMboxes\":[\"recommendations\"],\"globalMbox\":\"target-global-mbox\"},\"rules\":{\"mboxes\":{\"product\":[{\"condition\":{\"and\":[{\"<\":[0,{\"var\":\"allocation\"},50]},{\"<=\":[1580371200000,{\"var\":\"current_timestamp\"},1600585200000]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"content\":{\"product\":\"default\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"3eLgpLF+APtuSsE47wxq/mqipfsIHvVzTQxHolz2IpSCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"product\"}]},\"meta\":{\"activityId\":317586,\"experienceId\":0,\"type\":\"ab\",\"mbox\":\"product\"}},{\"condition\":{\"and\":[{\"<\":[50,{\"var\":\"allocation\"},100]},{\"<=\":[1580371200000,{\"var\":\"current_timestamp\"},1600585200000]}]},\"consequence\":{\"mboxes\":[{\"options\":[{\"content\":{\"product\":\"new_layout\"},\"type\":\"json\"}],\"metrics\":[{\"type\":\"display\",\"eventToken\":\"3eLgpLF+APtuSsE47wxq/pNWHtnQtQrJfmRrQugEa2qCnQ9Y9OaLL2gsdrWQTvE54PwSz67rmXWmSnkXpSSS2Q==\"}],\"name\":\"product\"}]},\"meta\":{\"activityId\":317586,\"experienceId\":1,\"type\":\"ab\",\"mbox\":\"product\"}}]},\"views\":{}}}";

    private TargetExceptionHandler exceptionHandler;
    private OnDeviceDecisioningHandler executionHandler;
    private ClientConfig clientConfig;

    @BeforeEach
    void init() {
        exceptionHandler = spy(new TargetExceptionHandler() {
            @Override
            public void handleException(TargetClientException e) {

            }
        });

        executionHandler = spy(new OnDeviceDecisioningHandler() {
            @Override
            public void onDeviceDecisioningReady() {

            }

            @Override
            public void artifactDownloadSucceeded(byte[] artifactData) {

            }

            @Override
            public void artifactDownloadFailed(TargetClientException e) {

            }
        });

        clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .onDeviceEnvironment("production")
                .defaultDecisioningMethod(DecisioningMethod.ON_DEVICE)
                .exceptionHandler(exceptionHandler)
                .onDeviceDecisioningHandler(executionHandler)
                .build();
    }

    static HttpResponse<OnDeviceDecisioningRuleSet> getTestResponse(final String ruleSet, final String etag, final int status) {
        return new HttpResponse<OnDeviceDecisioningRuleSet>() {
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
            public OnDeviceDecisioningRuleSet getBody() {
                if (ruleSet == null) {
                    return null;
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
                try {
                    return mapper.readValue(ruleSet, new TypeReference<OnDeviceDecisioningRuleSet>() {
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Optional<UnirestParsingException> getParsingError() {
                return Optional.empty();
            }

            @Override
            public <V> V mapBody(Function<OnDeviceDecisioningRuleSet, V> func) {
                return null;
            }

            @Override
            public <V> HttpResponse<V> map(Function<OnDeviceDecisioningRuleSet, V> func) {
                return null;
            }

            @Override
            public HttpResponse<OnDeviceDecisioningRuleSet> ifSuccess(Consumer<HttpResponse<OnDeviceDecisioningRuleSet>> consumer) {
                return null;
            }

            @Override
            public HttpResponse<OnDeviceDecisioningRuleSet> ifFailure(Consumer<HttpResponse<OnDeviceDecisioningRuleSet>> consumer) {
                return null;
            }

            @Override
            public <E> HttpResponse<OnDeviceDecisioningRuleSet> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
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
        Mockito.doReturn(null)
                .when(defaultRuleLoader).generateRequest(any(ClientConfig.class));
        Mockito.doReturn(getTestResponse(TEST_RULE_SET, etag, HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any());

        defaultRuleLoader.start(clientConfig);
        verify(defaultRuleLoader, timeout(1000)).setLatestRules(any(OnDeviceDecisioningRuleSet.class));
        verify(defaultRuleLoader, timeout(1000)).setLatestETag(eq(etag));
        verify(executionHandler, timeout(1000)).onDeviceDecisioningReady();
        verify(executionHandler, timeout(1000)).artifactDownloadSucceeded(any());
        verify(executionHandler, never()).artifactDownloadFailed(any());
        OnDeviceDecisioningRuleSet rules = defaultRuleLoader.getLatestRules();
        assertNotNull(rules);
        defaultRuleLoader.stop();

        // do it again, make sure starting works again after a stop
        reset(executionHandler);
        defaultRuleLoader.start(clientConfig);
        verify(defaultRuleLoader, timeout(1000)).setLatestRules(any(OnDeviceDecisioningRuleSet.class));
        verify(defaultRuleLoader, timeout(1000)).setLatestETag(eq(etag));
        verify(executionHandler, timeout(1000)).onDeviceDecisioningReady();
        verify(executionHandler, timeout(1000)).artifactDownloadSucceeded(any());
        verify(executionHandler, never()).artifactDownloadFailed(any());
        rules = defaultRuleLoader.getLatestRules();
        assertNotNull(rules);

        Mockito.doReturn(getTestResponse(TEST_RULE_SET, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_NOT_MODIFIED))
                .when(defaultRuleLoader).executeRequest(any());

        defaultRuleLoader.refresh();
        verify(exceptionHandler, never()).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
     }

    @Test
    void testDefaultRuleLoaderNullResponse() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(null)
                .when(defaultRuleLoader).generateRequest(any(ClientConfig.class));
        Mockito.doReturn(getTestResponse(null, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any());

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        verify(executionHandler, never()).onDeviceDecisioningReady();
        verify(executionHandler, never()).artifactDownloadSucceeded(any());
        verify(executionHandler, timeout(1000)).artifactDownloadFailed(any());
        defaultRuleLoader.stop();
    }

    @Test
    void testDefaultRuleLoaderInvalidVersion() {

        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(null)
                .when(defaultRuleLoader).generateRequest(any(ClientConfig.class));
        Mockito.doReturn(getTestResponse(TEST_RULE_SET_HIGHER_VERSION, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any());

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        verify(executionHandler, never()).onDeviceDecisioningReady();
        verify(executionHandler, never()).artifactDownloadSucceeded(any());
        verify(executionHandler, timeout(1000)).artifactDownloadFailed(any());
        defaultRuleLoader.stop();
    }

    @Test
    void testDefaultRuleLoaderInvalidStatus() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        Mockito.doReturn(null)
                .when(defaultRuleLoader).generateRequest(any(ClientConfig.class));
        Mockito.doReturn(getTestResponse(TEST_RULE_SET, "5b1cf3c050e1a0d16934922bf19ba6ea", HttpStatus.SC_NOT_FOUND))
                .when(defaultRuleLoader).executeRequest(any());

        defaultRuleLoader.start(clientConfig);
        verify(exceptionHandler, timeout(1000)).handleException(any(TargetClientException.class));
        verify(executionHandler, never()).onDeviceDecisioningReady();
        verify(executionHandler, never()).artifactDownloadSucceeded(any());
        verify(executionHandler, timeout(1000)).artifactDownloadFailed(any());
        defaultRuleLoader.stop();
    }

    @Test
    void testRuleLoaderArtifactPayload() {
        DefaultRuleLoader defaultRuleLoader = mock(DefaultRuleLoader.class, CALLS_REAL_METHODS);

        String etag = "5b1cf3c050e1a0d16934922bf19ba6ea";
        Mockito.doReturn(null)
                .when(defaultRuleLoader).generateRequest(any(ClientConfig.class));
        Mockito.doReturn(getTestResponse(TEST_RULE_SET, etag, HttpStatus.SC_OK))
                .when(defaultRuleLoader).executeRequest(any());

        ClientConfig payloadClientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .onDeviceEnvironment("production")
                .defaultDecisioningMethod(DecisioningMethod.ON_DEVICE)
                .exceptionHandler(exceptionHandler)
                .onDeviceDecisioningHandler(executionHandler)
                .onDeviceArtifactPayload(TEST_RULE_SET.getBytes(StandardCharsets.UTF_8))
                .build();

        defaultRuleLoader.start(payloadClientConfig);
        verify(defaultRuleLoader, timeout(1000)).setLatestRules(any(OnDeviceDecisioningRuleSet.class));
        verify(executionHandler, timeout(1000)).onDeviceDecisioningReady();
        verify(executionHandler, never()).artifactDownloadSucceeded(any());
        verify(executionHandler, never()).artifactDownloadFailed(any());
        OnDeviceDecisioningRuleSet rules = defaultRuleLoader.getLatestRules();
        assertNotNull(rules);

        defaultRuleLoader.refresh();
        verify(exceptionHandler, never()).handleException(any(TargetClientException.class));
        defaultRuleLoader.stop();
    }
}
