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

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.local.LocalDecisioningService;
import com.adobe.target.edge.client.model.ExecutionMode;
import com.adobe.target.edge.client.model.TargetAttributesResponse;
import com.adobe.target.edge.client.service.TargetRequestException;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DefaultTargetClient implements TargetClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);
    private final TargetService targetService;
    private final LocalDecisioningService localService;

    DefaultTargetClient(ClientConfig clientConfig) {
        this.targetService = new DefaultTargetService(clientConfig);
        this.localService = new LocalDecisioningService(clientConfig, this.targetService);
        VisitorProvider.init(clientConfig.getOrganizationId());
    }

    @Override
    public TargetDeliveryResponse getOffers(TargetDeliveryRequest request) {
        try {
            Objects.requireNonNull(request, "ClientConfig instance cannot be null");
            TargetDeliveryResponse targetDeliveryResponse;
            if (request.getExecutionMode() == ExecutionMode.LOCAL ||
                    (request.getExecutionMode() == ExecutionMode.HYBRID &&
                    localService.evaluateLocalExecution(request).isAllLocal())) {
                targetDeliveryResponse = localService.executeRequest(request);
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
            if (request.getExecutionMode() == ExecutionMode.LOCAL ||
                    (request.getExecutionMode() == ExecutionMode.HYBRID &&
                    localService.evaluateLocalExecution(request).isAllLocal())) {
                targetDeliveryResponse = CompletableFuture.completedFuture(localService.executeRequest(request));
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
    public Attributes getAttributes(TargetDeliveryRequest targetRequest, String ...mboxes) {
        TargetDeliveryResponse response = getOffers(addMBoxesToRequest(targetRequest, mboxes));
        return new TargetAttributesResponse(response);
    }

    @Override
    public CompletableFuture<Attributes> getAttributesAsync(TargetDeliveryRequest targetRequest, String ...mboxes) {
        CompletableFuture<TargetDeliveryResponse> completableResponse = getOffersAsync(addMBoxesToRequest(targetRequest, mboxes));
        return completableResponse.thenApply(TargetAttributesResponse::new);
    }

    @Override
    public void close() {
        try {
            targetService.close();
            localService.stop();
        } catch (Exception e) {
            logger.error("Could not close TargetClient: {}", e.getMessage());
        }
    }

    private static TargetDeliveryRequest addMBoxesToRequest(TargetDeliveryRequest targetRequest, String... mboxes) {
        if (targetRequest == null || targetRequest.getDeliveryRequest() == null) {
            targetRequest = TargetDeliveryRequest.builder().executionMode(ExecutionMode.HYBRID).build();
        }
        int idx = 0;
        Set<String> existingMBoxNames = new HashSet<>();
        DeliveryRequest deliveryRequest = targetRequest.getDeliveryRequest();
        PrefetchRequest prefetchRequest = deliveryRequest.getPrefetch();
        if (prefetchRequest != null && prefetchRequest.getMboxes() != null) {
            for (MboxRequest mb : prefetchRequest.getMboxes()) {
                existingMBoxNames.add(mb.getName());
            }
        }
        ExecuteRequest executeRequest = deliveryRequest.getExecute();
        if (executeRequest == null) {
            executeRequest = new ExecuteRequest();
            targetRequest.getDeliveryRequest().setExecute(executeRequest);
        }
        List<MboxRequest> existingMboxes = executeRequest.getMboxes();
        if (existingMboxes != null) {
            for (MboxRequest mb : existingMboxes) {
                if (mb.getIndex() >= idx) {
                    idx = mb.getIndex() + 1;
                }
                existingMBoxNames.add(mb.getName());
            }
        }
        for (String mbox : mboxes) {
            if (!existingMBoxNames.contains(mbox)) {
                executeRequest.addMboxesItem(new MboxRequest().index(idx++).name(mbox));
            }
        }
        return targetRequest;
    }
}
