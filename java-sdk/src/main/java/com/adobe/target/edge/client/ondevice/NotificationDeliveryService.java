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

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.adobe.target.edge.client.service.TargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class NotificationDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDeliveryService.class);

    private final TargetService targetService;
    private ThreadPoolExecutor executor;

    public NotificationDeliveryService(TargetService targetService) {
        this.targetService = targetService;
    }

    public void sendNotification(final TargetDeliveryRequest deliveryRequest) {
        this.executor.execute(() ->
                NotificationDeliveryService.this.targetService.executeNotification(deliveryRequest));
    }

    public void start(ClientConfig clientConfig) {
        this.executor = new ThreadPoolExecutor(2,
                50,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2000),
                (r, executor) -> {
                    RejectedExecutionException e = new RejectedExecutionException("On-device-decisioning notification queue full");
                    TargetExceptionHandler handler = clientConfig.getExceptionHandler();
                    if (handler != null) {
                        handler.handleException(new TargetClientException("On-device-decisioning notification rejected", e));
                    }
                    throw e;
                });
    }

    public void stop() {
        try {
            this.executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            logger.info("Caught InterruptedException while shutting down NotificationDeliveryService: " + e);
        }
    }

}
