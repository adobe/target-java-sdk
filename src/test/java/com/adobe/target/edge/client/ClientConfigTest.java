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
package com.adobe.target.edge.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientConfigTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
  static final String PROXY_HOST = "localhost";
  static final Integer PROXY_PORT = 3128;
  static final String PROXY_USERNAME = "test-user-name";
  static final String PROXY_PASSWORD = "test-password";

  @Test
  void testProxyConfigNotSet() {
    ClientConfig clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).build();
    assertFalse(clientConfig.isProxyEnabled());
    assertNull(clientConfig.getProxyConfig());
  }

  @Test
  void testProxyConfigSetWithNoAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .proxyConfig(new ClientProxyConfig(PROXY_HOST, PROXY_PORT))
            .build();
    assertTrue(clientConfig.isProxyEnabled());
    ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
    assertNotNull(proxyConfig);
    assertEquals(PROXY_HOST, proxyConfig.getHost());
    assertEquals(PROXY_PORT, proxyConfig.getPort());
    assertNull(proxyConfig.getUsername());
    assertNull(proxyConfig.getPassword());
    assertFalse(proxyConfig.isAuthProxy());
  }

  @Test
  void testProxyConfigSetWithAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .proxyConfig(
                new ClientProxyConfig(PROXY_HOST, PROXY_PORT, PROXY_USERNAME, PROXY_PASSWORD))
            .build();
    assertTrue(clientConfig.isProxyEnabled());
    ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
    assertNotNull(proxyConfig);
    assertEquals(PROXY_HOST, proxyConfig.getHost());
    assertEquals(PROXY_PORT, proxyConfig.getPort());
    assertEquals(PROXY_USERNAME, proxyConfig.getUsername());
    assertEquals(PROXY_PASSWORD, proxyConfig.getPassword());
    assertTrue(proxyConfig.isAuthProxy());
  }
}
