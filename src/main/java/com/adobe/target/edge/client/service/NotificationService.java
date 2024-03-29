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
package com.adobe.target.edge.client.service;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.Notification;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.ondevice.ClusterLocator;
import java.util.List;
import java.util.UUID;

public class NotificationService {

  private final TargetService targetService;
  private final ClientConfig clientConfig;
  private final ClusterLocator clusterLocator;

  public NotificationService(
      TargetService targetService, ClientConfig clientConfig, ClusterLocator clusterLocator) {
    this.targetService = targetService;
    this.clientConfig = clientConfig;
    this.clusterLocator = clusterLocator;
  }

  public void sendNotification(TargetDeliveryRequest targetDeliveryRequest) {
    this.targetService.executeNotificationAsync(targetDeliveryRequest);
  }

  public void buildNotifications(
      TargetDeliveryRequest targetDeliveryRequest,
      TargetDeliveryResponse targetDeliveryResponse,
      List<Notification> notifications) {

    boolean noNotifications = notifications == null || notifications.isEmpty();
    if (noNotifications) {
      return;
    }
    DeliveryRequest deliveryRequest = targetDeliveryRequest.getDeliveryRequest();
    setBeaconToFalse(deliveryRequest);
    String locationHint =
        targetDeliveryRequest.getLocationHint() != null
            ? targetDeliveryRequest.getLocationHint()
            : this.clusterLocator.getLocationHint();
    TargetDeliveryRequest notificationRequest =
        TargetDeliveryRequest.builder()
            .locationHint(locationHint)
            .sessionId(targetDeliveryRequest.getSessionId())
            .visitor(targetDeliveryRequest.getVisitor())
            .decisioningMethod(DecisioningMethod.SERVER_SIDE)
            .requestId(UUID.randomUUID().toString())
            .impressionId(UUID.randomUUID().toString())
            .id(
                deliveryRequest.getId() != null
                    ? deliveryRequest.getId()
                    : targetDeliveryResponse.getResponse().getId())
            .experienceCloud(deliveryRequest.getExperienceCloud())
            .context(deliveryRequest.getContext())
            .environmentId(deliveryRequest.getEnvironmentId())
            .qaMode(deliveryRequest.getQaMode())
            .property(deliveryRequest.getProperty())
            .notifications(notifications)
            .trace(deliveryRequest.getTrace())
            .build();
    this.sendNotification(notificationRequest);
  }

  public static void setBeaconToFalse(DeliveryRequest deliveryRequest) {
    Context context = deliveryRequest.getContext();
    if (context.getBeacon() == null || context.getBeacon()) {
      context.setBeacon(false);
    }
  }
}
