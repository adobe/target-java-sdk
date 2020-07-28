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
package com.adobe.target.edge.client.service;

import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.http.TargetHttpClient;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.adobe.target.edge.client.utils.StringUtils;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.adobe.target.edge.client.utils.TargetConstants.SDK_VERSION;
import static org.apache.http.HttpStatus.SC_OK;

public class DefaultTargetService implements TargetService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);
    public static final String SDK_USER_KEY = "X-EXC-SDK";
    public static final String SDK_USER_VALUE = "AdobeTargetJava";
    public static final String SDK_VERSION_KEY = "X-EXC-SDK-Version";
    public static final String SESSION_ID = "sessionId";
    public static final String CLIENT = "client";
    private final TargetHttpClient targetHttpClient;
    private final ClientConfig clientConfig;
    private String stickyLocationHint;

    public DefaultTargetService(ClientConfig clientConfig) {
        TargetHttpClient targetHttpClient = new DefaultTargetHttpClient(clientConfig);
        if (clientConfig.isLogRequests()) {
            this.targetHttpClient = TargetHttpClient.createLoggingHttpClient(targetHttpClient);
        } else {
            this.targetHttpClient = targetHttpClient;
        }
        this.targetHttpClient.addDefaultHeader(SDK_USER_KEY, SDK_USER_VALUE);
        this.targetHttpClient.addDefaultHeader(SDK_VERSION_KEY, SDK_VERSION);
        this.clientConfig = clientConfig;
    }

    @Override
    public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest) {
        String url = clientConfig.getUrl(getBestLocationHint(deliveryRequest));
        HttpResponse<DeliveryResponse> response = targetHttpClient.execute(getQueryParams(deliveryRequest),
                url, deliveryRequest.getDeliveryRequest(), DeliveryResponse.class);
        return getTargetDeliveryResponse(deliveryRequest, response);
    }

    @Override
    public CompletableFuture<TargetDeliveryResponse> executeRequestAsync(TargetDeliveryRequest deliveryRequest) {
        String url = clientConfig.getUrl(getBestLocationHint(deliveryRequest));
        CompletableFuture<HttpResponse<DeliveryResponse>> responseCompletableFuture =
                targetHttpClient.executeAsync(getQueryParams(deliveryRequest), url,
                        deliveryRequest.getDeliveryRequest(), DeliveryResponse.class);
        return responseCompletableFuture.thenApply(response -> getTargetDeliveryResponse(deliveryRequest, response));
    }

    @Override
    public ResponseStatus executeNotification(TargetDeliveryRequest deliveryRequest) {
        String url = clientConfig.getUrl(getBestLocationHint(deliveryRequest));
        HttpResponse<DeliveryResponse> response = targetHttpClient.execute(getQueryParams(deliveryRequest),
                url, deliveryRequest.getDeliveryRequest(), DeliveryResponse.class);
        updateStickyLocationHint(retrieveDeliveryResponse(response));
        return new ResponseStatus(response.getStatus(), response.getStatusText());
    }

    @Override
    public CompletableFuture<ResponseStatus> executeNotificationAsync(TargetDeliveryRequest deliveryRequest) {
        String url = clientConfig.getUrl(getBestLocationHint(deliveryRequest));
        CompletableFuture<HttpResponse<DeliveryResponse>> responseCompletableFuture =
                targetHttpClient.executeAsync(getQueryParams(deliveryRequest), url,
                        deliveryRequest.getDeliveryRequest(), DeliveryResponse.class);
        return responseCompletableFuture.thenApply(response -> {
            updateStickyLocationHint(retrieveDeliveryResponse(response));
            return new ResponseStatus(response.getStatus(), response.getStatusText());
        });
    }

    private Map<String, Object> getQueryParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(SESSION_ID, deliveryRequest.getSessionId());
        queryParams.put(CLIENT, clientConfig.getClient());
        return queryParams;
    }

    private TargetDeliveryResponse getTargetDeliveryResponse(TargetDeliveryRequest deliveryRequest,
                                                             HttpResponse<DeliveryResponse> response) {
        DeliveryResponse deliveryResponse = retrieveDeliveryResponse(response);
        updateStickyLocationHint(deliveryResponse);
        return new TargetDeliveryResponse(deliveryRequest, deliveryResponse, response.getStatus(),
                response.getStatusText());
    }

    private DeliveryResponse retrieveDeliveryResponse(HttpResponse<DeliveryResponse> response) {
        DeliveryResponse deliveryResponse = response.getBody();
        if (deliveryResponse == null) {
            Optional<UnirestParsingException> parsingError = response.getParsingError();
            logger.error("Error parsing delivery response: {}", parsingError.get().getOriginalBody());
        }
        return deliveryResponse;
    }

    private void updateStickyLocationHint(DeliveryResponse deliveryResponse) {
        if (!clientConfig.isOnDeviceDecisioningEnabled() ||
                StringUtils.isNotEmpty(stickyLocationHint)) {
            return;
        }
        if (deliveryResponse != null &&
                deliveryResponse.getStatus() == SC_OK &&
                deliveryResponse.getId() != null &&
                deliveryResponse.getId().getTntId() != null) {
            String tntId = deliveryResponse.getId().getTntId();
            this.stickyLocationHint = CookieUtils.locationHintFromTntId(tntId);
        }
    }

    @Override
    public void close() throws Exception {
        targetHttpClient.close();
    }

    private String getBestLocationHint(TargetDeliveryRequest deliveryRequest) {
        if (deliveryRequest.getLocationHint() != null) {
            return deliveryRequest.getLocationHint();
        }
        return stickyLocationHint;
    }

    private void addRemoteResponseToken(List<Option> options) {
        if (options != null) {
            for (Option option : options) {
                Map<String, Object> responseTokens = option.getResponseTokens();
                responseTokens.put("activity.decisioningMethod", "server-side");
            }
        }
    }
}
