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
		ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .build();
		assertFalse(clientConfig.isProxyEnabled());
		assertNull(clientConfig.getProxy());
	}
	
	@Test
    void testProxyConfigSetWithNoAuthentication() {
		ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .proxy(new ClientProxyConfig(PROXY_HOST, PROXY_PORT))
                .build();
		assertTrue(clientConfig.isProxyEnabled());
		ClientProxyConfig proxy = clientConfig.getProxy();
		assertNotNull(proxy);
		assertEquals(PROXY_HOST, proxy.getHost());
		assertEquals(PROXY_PORT, proxy.getPort());
		assertNull(proxy.getUsername());
		assertNull(proxy.getPassword());
		assertFalse(proxy.isAuthProxy());
	}
	
	@Test
    void testProxyConfigSetWithAuthentication() {
		ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .proxy(new ClientProxyConfig(PROXY_HOST, PROXY_PORT, PROXY_USERNAME, PROXY_PASSWORD))
                .build();
		assertTrue(clientConfig.isProxyEnabled());
		ClientProxyConfig proxy = clientConfig.getProxy();
		assertNotNull(proxy);
		assertEquals(PROXY_HOST, proxy.getHost());
		assertEquals(PROXY_PORT, proxy.getPort());
		assertEquals(PROXY_USERNAME, proxy.getUsername());
		assertEquals(PROXY_PASSWORD, proxy.getPassword());
		assertTrue(proxy.isAuthProxy());
	}
}
