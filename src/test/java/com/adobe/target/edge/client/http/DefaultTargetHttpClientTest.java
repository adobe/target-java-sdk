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
package com.adobe.target.edge.client.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.ClientProxyConfig;
import kong.unirest.Proxy;
import kong.unirest.UnirestInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultTargetHttpClientTest {
  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
  static final String PROXY_HOST = "localhost";
  static final Integer PROXY_PORT = 3128;
  static final String PROXY_USERNAME = "test-user-name";
  static final String PROXY_PASSWORD = "test-password";

  @Test
  void testProxyConfigNotSet() {
    ClientConfig clientConfig =
        ClientConfig.builder().client("emeaprod4").organizationId(TEST_ORG_ID).build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNull(unirestProxy);
    targetClient.close();
  }

  @Test
  void testProxyConfigSetWithNoAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .client("emeaprod4")
            .organizationId(TEST_ORG_ID)
            .proxyConfig(new ClientProxyConfig(PROXY_HOST, PROXY_PORT))
            .build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNotNull(unirestProxy);
    assertEquals(PROXY_HOST, unirestProxy.getHost());
    assertEquals(PROXY_PORT, unirestProxy.getPort());
    assertNull(unirestProxy.getUsername());
    assertNull(unirestProxy.getPassword());
    targetClient.close();
  }

  @Test
  void testProxyConfigSetWithAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .client("emeaprod4")
            .organizationId(TEST_ORG_ID)
            .proxyConfig(
                new ClientProxyConfig(PROXY_HOST, PROXY_PORT, PROXY_USERNAME, PROXY_PASSWORD))
            .build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNotNull(unirestProxy);
    assertEquals(PROXY_HOST, unirestProxy.getHost());
    assertEquals(PROXY_PORT, unirestProxy.getPort());
    assertEquals(PROXY_USERNAME, unirestProxy.getUsername());
    assertEquals(PROXY_PASSWORD, unirestProxy.getPassword());
    targetClient.close();
  }
}
