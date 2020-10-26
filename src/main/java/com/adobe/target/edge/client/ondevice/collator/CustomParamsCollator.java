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
package com.adobe.target.edge.client.ondevice.collator;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import java.util.*;

public class CustomParamsCollator implements ParamsCollator {

  protected static final String LOWER_CASE_POSTFIX = "_lc";

  public Map<String, Object> collateParams(
      TargetDeliveryRequest deliveryRequest, RequestDetails requestDetails) {
    Map<String, Object> custom = new HashMap<>();
    addAllParameters(custom, requestDetails);
    return custom;
  }

  private void addAllParameters(Map<String, Object> custom, RequestDetails details) {
    if (details != null) {
      Map<String, String> params = details.getParameters();
      if (params != null) {
        custom.putAll(params);
        params.forEach((key, value) -> custom.put(key + LOWER_CASE_POSTFIX, value.toLowerCase()));
      }
    }
  }
}
