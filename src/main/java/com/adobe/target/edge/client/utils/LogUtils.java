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
package com.adobe.target.edge.client.utils;

import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LogUtils {
  public static String getRequestDetails(TargetDeliveryRequest targetDeliveryRequest) {
    DeliveryRequest deliveryRequest = targetDeliveryRequest.getDeliveryRequest();

    StringBuilder builder = new StringBuilder();
    builder.append("(");

    Map<String, String> details = new HashMap<>();

    details.put("sessionId", targetDeliveryRequest.getSessionId());
    details.put("requestId", deliveryRequest.getRequestId());

    Iterator<Map.Entry<String, String>> entrySet = details.entrySet().iterator();

    while (entrySet.hasNext()) {
      Map.Entry<String, String> entry = entrySet.next();
      builder.append(entry.getKey() + ":" + entry.getValue());
      if (entrySet.hasNext()) {
        builder.append(", ");
      }
    }

    builder.append(")");

    return builder.toString();
  }
}
