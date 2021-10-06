/*
 * Copyright 2021 Adobe. All rights reserved.
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
import com.adobe.target.edge.client.service.NotificationDeliveryService;
import com.adobe.target.edge.client.service.TargetService;
import java.util.HashMap;
import java.util.Map;

public class OnDeviceDecisioningServicesManager {

  public static class OnDeviceDecisioningServices {
    private NotificationDeliveryService notificationDeliveryService;
    private RuleLoader ruleLoader;
    private ClusterLocator clusterLocator;

    private void setNotificationDeliveryService(
        NotificationDeliveryService notificationDeliveryService) {
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

  private static final OnDeviceDecisioningServicesManager sInstance =
      new OnDeviceDecisioningServicesManager();

  private final Map<String, OnDeviceDecisioningServices> servicesMap = new HashMap<>();

  public static OnDeviceDecisioningServicesManager getInstance() {
    return sInstance;
  }

  public OnDeviceDecisioningServices getServices(
      ClientConfig clientConfig, TargetService targetService) {
    String serviceKey = clientConfig.getClient();
    OnDeviceDecisioningServices services = servicesMap.get(serviceKey);
    if (services != null) {
      return services;
    }
    synchronized (servicesMap) {
      services = servicesMap.get(serviceKey);
      if (services != null) {
        return services;
      }
      services = new OnDeviceDecisioningServices();
      ClusterLocator clusterLocator = new ClusterLocator();
      services.setNotificationDeliveryService(new NotificationDeliveryService(targetService, clientConfig, clusterLocator));
      services.setRuleLoader(new DefaultRuleLoader());
      services.setClusterLocator(clusterLocator);
      servicesMap.put(serviceKey, services);
      return services;
    }
  }
}
