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
package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.service.TargetService;

import java.util.HashMap;
import java.util.Map;

public class NotificationDeliveryManager {

    private static final NotificationDeliveryManager sInstance = new NotificationDeliveryManager();

    private final Map<String, NotificationDeliveryService> serviceMap = new HashMap<>();

    public static NotificationDeliveryManager getInstance() {
        return sInstance;
    }

    public NotificationDeliveryService getService(ClientConfig clientConfig, TargetService targetService) {
        String serviceKey = clientConfig.getClient();
        NotificationDeliveryService service = serviceMap.get(serviceKey);
        if (service != null) {
            return service;
        }
        synchronized (serviceMap) {
            service = serviceMap.get(serviceKey);
            if (service != null) {
                return service;
            }
            service = new NotificationDeliveryService(targetService);
            serviceMap.put(serviceKey, service);
            return service;
        }
    }

}
