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

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.ClientProxyConfig;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultTargetHttpClient implements TargetHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private ObjectMapper serializer = new JacksonObjectMapper();

    public DefaultTargetHttpClient(ClientConfig clientConfig) {
        unirestInstance.config()
                .socketTimeout(clientConfig.getSocketTimeout())
                .connectTimeout(clientConfig.getConnectTimeout())
                .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
                .automaticRetries(clientConfig.isEnabledRetries())
                .enableCookieManagement(false)
                .setObjectMapper(getObjectMapper())
                .setDefaultHeader("Accept", "application/json");

        if (clientConfig.isLogRequestStatus()) {
            unirestInstance.config().instrumentWith(new TargetMetrics(new LoggingMetricConsumer()));
        }

        if (clientConfig.getRequestInterceptor() != null) {
            unirestInstance.config().addInterceptor(clientConfig.getRequestInterceptor());
        }
        
        if (clientConfig.isProxyEnabled()) {
            ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
            if(proxyConfig.isAuthProxy()) {
                unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getUsername(), proxyConfig.getPassword());
            } else {
                unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort());
            }
        }
    }

    private ObjectMapper getObjectMapper() {
        logger.debug("using json serializer: {}", serializer.getClass().getSimpleName());
        return serializer;
    }

    @Override
    public void addDefaultHeader(String key, String value) {
        unirestInstance.config().setDefaultHeader(key, value);
    }

    @Override
    public <T, R> HttpResponse<R> execute(Map<String, Object> queryParams, String url, T request, Class<R> response) {
        return unirestInstance.post(url)
                .queryString(queryParams)
                .body(request)
                .asObject(response);
    }

    @Override
    public <T, R> CompletableFuture<HttpResponse<R>> executeAsync(Map<String, Object> queryParams, String url,
                                                                  T request, Class<R> response) {
        return unirestInstance.post(url)
                .queryString(queryParams)
                .body(request)
                .asObjectAsync(response);
    }

    @Override
    public void close() {
        unirestInstance.shutDown();
    }

    UnirestInstance getUnirestInstance() {
    	return unirestInstance;
    }
}
