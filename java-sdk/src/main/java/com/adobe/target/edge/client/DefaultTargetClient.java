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

import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.local.DefaultRuleLoader;
import com.adobe.target.edge.client.local.LocalDecisioningService;
import com.adobe.target.edge.client.local.RuleLoader;
import com.adobe.target.edge.client.model.ExecutionMode;
import com.adobe.target.edge.client.service.TargetRequestException;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import com.adobe.target.edge.client.utils.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultTargetClient implements TargetClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);
    private final TargetService targetService;
    private final RuleLoader ruleLoader;
    private final LocalDecisioningService localService;

    DefaultTargetClient(ClientConfig clientConfig) {
        this.targetService = new DefaultTargetService(clientConfig);
        this.ruleLoader = new DefaultRuleLoader();
        this.ruleLoader.start(clientConfig);
        this.localService = new LocalDecisioningService(clientConfig);
        VisitorProvider.init(clientConfig.getOrganizationId());
    }

    @Override
    public TargetDeliveryResponse getOffers(TargetDeliveryRequest request) {
        try {
            Objects.requireNonNull(request, "ClientConfig instance cannot be null");
            TargetDeliveryResponse targetDeliveryResponse;
            if (request.getExecutionMode() == ExecutionMode.LOCAL) {
                targetDeliveryResponse = localService.executeRequest(request, this.ruleLoader.getLatestRules());
            }
            else {
                targetDeliveryResponse = targetService.executeRequest(request);
            }
            return targetDeliveryResponse;
        } catch (Exception e) {
            throw new TargetRequestException(e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<TargetDeliveryResponse> getOffersAsync(TargetDeliveryRequest request) {
        try {
            Objects.requireNonNull(request, "ClientConfig instance cannot be null");
            CompletableFuture<TargetDeliveryResponse> targetDeliveryResponse;
            if (request.getExecutionMode() == ExecutionMode.LOCAL) {
                targetDeliveryResponse = CompletableFuture.completedFuture(localService.executeRequest(request, this.ruleLoader.getLatestRules()));
            }
            else {
                targetDeliveryResponse = targetService.executeRequestAsync(request);
            }
            return targetDeliveryResponse;
        } catch (Exception e) {
            throw new TargetRequestException(e.getMessage(), e);
        }
    }

    @Override
    public ResponseStatus sendNotifications(TargetDeliveryRequest request) {
        return targetService.executeNotification(request);
    }

    @Override
    public CompletableFuture<ResponseStatus> sendNotificationsAsync(TargetDeliveryRequest request) {
        return targetService.executeNotificationAsync(request);
    }

    @Override
    public void close() {
        try {
            targetService.close();
            ruleLoader.stop();
        } catch (Exception e) {
            logger.error("Could not close TargetClient: {}", e.getMessage());
        }
    }
}
