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
package com.adobe.target.sample.service;

import com.adobe.experiencecloud.ecid.visitor.CustomerState;
import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.adobe.target.sample.util.TargetRequestUtils.*;

/**
 * Common code for making target calls.
 */
@Service
public class TargetClientService {

    private final TargetClient targetJavaClient;

    public TargetClientService(TargetClient targetJavaClient) {
        this.targetJavaClient = targetJavaClient;
    }

    public TargetDeliveryResponse getPageLoadTargetDeliveryResponse(HttpServletRequest request,
                                                                    HttpServletResponse response) {

        Context context = getContext(request);

        ExecuteRequest executeRequest = new ExecuteRequest();
        RequestDetails pageLoad = new RequestDetails();
        executeRequest.pageLoad(pageLoad);

        List<TargetCookie> targetCookies = getTargetCookies(request.getCookies());

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .execute(executeRequest)
                .cookies(targetCookies)
                .build();
        TargetDeliveryResponse serverState = targetJavaClient.getOffers(targetDeliveryRequest);
        setCookies(serverState.getCookies(), response);
        return serverState;
    }

    public TargetDeliveryResponse getMboxTargetDeliveryResponse(List<MboxRequest> executeMboxes,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        Context context = getContext(request);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setMboxes(executeMboxes);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .execute(executeRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .build();
        try {
            TargetDeliveryResponse serverState = targetJavaClient.getOffers(targetDeliveryRequest);
            setCookies(serverState.getCookies(), response);
            return serverState;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public TargetDeliveryResponse getMboxTargetDeliveryResponse(List<MboxRequest> executeMboxes,
                                                                List<MboxRequest> prefetchMboxes,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        Context context = getContext(request);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setMboxes(executeMboxes);
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        prefetchRequest.setMboxes(prefetchMboxes);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .execute(executeRequest)
                .prefetch(prefetchRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .build();
        try {
            TargetDeliveryResponse serverState = targetJavaClient.getOffers(targetDeliveryRequest);
            setCookies(serverState.getCookies(), response);
            return serverState;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public TargetDeliveryResponse prefetchViewsTargetDeliveryResponse(HttpServletRequest request,
                                                                      HttpServletResponse response) {
        TargetDeliveryRequest targetDeliveryRequest = getTargetDeliveryRequest(request);

        TargetDeliveryResponse serverState = targetJavaClient.getOffers(targetDeliveryRequest);
        setCookies(serverState.getCookies(), response);
        return serverState;
    }

    public CompletableFuture<TargetDeliveryResponse> prefetchViewsTargetDeliveryResponseAsync(HttpServletRequest request, HttpServletResponse response) {
        TargetDeliveryRequest targetDeliveryRequest = getTargetDeliveryRequest(request);
        CompletableFuture<TargetDeliveryResponse> deliveryResponseAsync =
                targetJavaClient.getOffersAsync(targetDeliveryRequest)
                        .thenApply(serverState -> {
                            setCookies(serverState.getCookies(), response);
                            return serverState;
                        });

        return deliveryResponseAsync;
    }

    public ResponseStatus sendNotifications(HttpServletRequest request, List<Notification> notifications) {
        Context context = getContext(request);

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .notifications(notifications)
                .cookies(getTargetCookies(request.getCookies()))
                .build();

        ResponseStatus status = targetJavaClient.sendNotifications(targetDeliveryRequest);
        return status;
    }

    private TargetDeliveryRequest getTargetDeliveryRequest(HttpServletRequest request) {
        Context context = getContext(request);
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        ViewRequest requestDetails = new ViewRequest();
        prefetchRequest.setViews(Arrays.asList(requestDetails));

        Map<String, CustomerState> customerIds = new HashMap<>();
        customerIds.put("userid", CustomerState.authenticated("67312378756723456"));
        customerIds.put("puuid", CustomerState.unknown("550e8400-e29b-41d4-a716-446655440000"));

        return TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .customerIds(customerIds)
                .build();
    }

}
