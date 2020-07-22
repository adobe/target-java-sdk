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

import com.adobe.target.delivery.v1.model.ChannelType;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.utils.CookieUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import static org.apache.http.HttpStatus.SC_OK;

public class ClusterLocator {

    private static final int MAX_RETRIES = 10;

    private boolean running;
    private int retries;
    private String locationHint;
    private CompletableFuture<TargetDeliveryResponse> future = null;
    private Timer timer = null;

    public void start(final ClientConfig clientConfig, final TargetService targetService) {
        if (!clientConfig.isOnDeviceDecisioningEnabled()) {
            return;
        }
        if (this.running) {
            return;
        }
        this.running = true;
        this.retries = 0;
        this.timer = new Timer(this.getClass().getCanonicalName());
        executeRequest(targetService);
    }

    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.timer.cancel();
        if (this.future != null) {
            this.future.cancel(true);
        }
    }

    public String getLocationHint() {
        return this.locationHint;
    }

    private void executeRequest(final TargetService targetService) {
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .context(new Context().channel(ChannelType.WEB))
                .build();
        future = targetService.executeRequestAsync(request);
        future.thenAcceptAsync(response -> {
            if (!this.running) { return; }
            if (response != null &&
                    response.getStatus() == SC_OK &&
                    response.getResponse() != null &&
                    response.getResponse().getId() != null &&
                    response.getResponse().getId().getTntId() != null) {
                String tntId = response.getResponse().getId().getTntId();
                this.locationHint = CookieUtils.locationHintFromTntId(tntId);
            }
            else if (retries++ < MAX_RETRIES) {
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        executeRequest(targetService);
                    }
                }, retries * 10000);
            }
        });
    }

}
