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

public class AllocationUtils {
  private static final int TOTAL_BUCKETS = 10_000;
  private static final int MAX_PERCENTAGE = 100;

  public static String getDeviceId(
      String clientId, String activityId, String visitorId, String salt) {
    int index = visitorId.indexOf(".");
    if (index > 0) {
      visitorId = visitorId.substring(0, index);
    }

    return clientId + "." + activityId + "." + visitorId + "." + salt;
  }

  public static double calculateAllocation(
      String clientId, String activityId, String visitorId, String salt) {
    return calculateAllocation(getDeviceId(clientId, activityId, visitorId, salt));
  }

  public static double calculateAllocation(String deviceId) {
    int hashValue = HashingUtils.hashUnencodedChars(deviceId);

    int hashFixedBucket = Math.abs(hashValue) % TOTAL_BUCKETS;
    float allocationValue = ((float) hashFixedBucket / TOTAL_BUCKETS) * MAX_PERCENTAGE;

    return (double) Math.round(allocationValue * 100) / 100; // two decimal places
  }
}
