package com.adobe.target.edge.client.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.ClientProxyConfig;

import kong.unirest.Proxy;
import kong.unirest.UnirestInstance;

@ExtendWith(MockitoExtension.class)
public class DefaultTargetHttpClientTest {
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
        DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
        UnirestInstance unirestInstance= targetClient.getUnirestInstance();
        Proxy unirestProxy = unirestInstance.config().getProxy();
        assertNull(unirestProxy);
        targetClient.close();
    }
    
    @Test
    void testProxyConfigSetWithNoAuthentication() {
        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .proxyConfig(new ClientProxyConfig(PROXY_HOST, PROXY_PORT))
                .build();
        DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
        UnirestInstance unirestInstance= targetClient.getUnirestInstance();
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
        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .proxyConfig(new ClientProxyConfig(PROXY_HOST, PROXY_PORT, PROXY_USERNAME, PROXY_PASSWORD))
                .build();
        DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
        UnirestInstance unirestInstance= targetClient.getUnirestInstance();
        Proxy unirestProxy = unirestInstance.config().getProxy();
        assertNotNull(unirestProxy);
        assertEquals(PROXY_HOST, unirestProxy.getHost());
        assertEquals(PROXY_PORT, unirestProxy.getPort());
        assertEquals(PROXY_USERNAME, unirestProxy.getUsername());
        assertEquals(PROXY_PASSWORD, unirestProxy.getPassword());
        targetClient.close();
    }
}
