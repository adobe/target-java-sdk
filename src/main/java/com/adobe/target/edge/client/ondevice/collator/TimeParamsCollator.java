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
package com.adobe.target.edge.client.ondevice.collator;

import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeParamsCollator implements ParamsCollator {

  protected static final String CURRENT_TIMESTAMP = "current_timestamp";
  protected static final String CURRENT_DAY = "current_day";
  protected static final String CURRENT_TIME = "current_time";

  public Map<String, Object> collateParams(
      TargetDeliveryRequest deliveryRequest, RequestDetails requestDetails) {
    Map<String, Object> time = new HashMap<>();
    long now = currentTimestamp();
    Date nowDate = new Date(now);
    time.put(CURRENT_TIMESTAMP, now);
    SimpleDateFormat dayFormat = new SimpleDateFormat("u");
    dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    time.put(CURRENT_DAY, dayFormat.format(nowDate));
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    time.put(CURRENT_TIME, timeFormat.format(nowDate));
    return time;
  }

  protected long currentTimestamp() {
    return System.currentTimeMillis();
  }
}
