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
package com.adobe.target.edge.client.exception;

import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.LogUtils;

public class TargetRequestException extends RuntimeException {
  private TargetDeliveryRequest request;

  public TargetRequestException(
      String message, Throwable cause, TargetDeliveryRequest deliveryRequest) {
    super(message, cause);

    request = deliveryRequest;
  }

  public TargetRequestException(String message) {
    super(message);
  }

  public TargetDeliveryRequest getRequest() {
    return request;
  }

  public void setRequest(TargetDeliveryRequest deliveryRequest) {
    request = deliveryRequest;
  }

  @Override
  public String getMessage() {
    if (request == null) {
      return super.getMessage();
    }

    return super.getMessage() + " " + LogUtils.getRequestDetails(request);
  }
}
