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

public class LocalDecisioningServicesManager {

    public static class LocalDecisioningServices {
        private NotificationDeliveryService notificationDeliveryService;
        private RuleLoader ruleLoader;
        private ClusterLocator clusterLocator;

        private void setNotificationDeliveryService(NotificationDeliveryService notificationDeliveryService) {
            this.notificationDeliveryService = notificationDeliveryService;
        }

        private void setRuleLoader(RuleLoader ruleLoader) {
            this.ruleLoader = ruleLoader;
        }

        private void setClusterLocator(ClusterLocator clusterLocator) {
            this.clusterLocator = clusterLocator;
        }

        public NotificationDeliveryService getNotificationDeliveryService() {
            return notificationDeliveryService;
        }

        public RuleLoader getRuleLoader() {
            return ruleLoader;
        }

        public ClusterLocator getClusterLocator() {
            return clusterLocator;
        }
    }

    private static final LocalDecisioningServicesManager sInstance = new LocalDecisioningServicesManager();

    private final Map<String, LocalDecisioningServices> servicesMap = new HashMap<>();

    public static LocalDecisioningServicesManager getInstance() {
        return sInstance;
    }

    public LocalDecisioningServices getServices(ClientConfig clientConfig, TargetService targetService) {
        String serviceKey = clientConfig.getClient();
        LocalDecisioningServices services = servicesMap.get(serviceKey);
        if (services != null) {
            return services;
        }
        synchronized (servicesMap) {
            services = servicesMap.get(serviceKey);
            if (services != null) {
                return services;
            }
            services = new LocalDecisioningServices();
            services.setNotificationDeliveryService(new NotificationDeliveryService(targetService));
            services.setRuleLoader(new DefaultRuleLoader());
            services.setClusterLocator(new ClusterLocator());
            servicesMap.put(serviceKey, services);
            return services;
        }
    }

}
