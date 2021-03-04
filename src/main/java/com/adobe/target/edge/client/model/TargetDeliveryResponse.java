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
package com.adobe.target.edge.client.model;

import com.adobe.experiencecloud.ecid.visitor.VisitorState;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.DeliveryResponse;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;

public class TargetDeliveryResponse {

  private final TargetDeliveryRequest request;
  private final DeliveryResponse response;
  private final ResponseStatus status;

  public TargetDeliveryResponse(
      TargetDeliveryRequest request, DeliveryResponse response, int status, String message) {
    this.request = request;
    this.response = response;
    this.status = new ResponseStatus(status, message);
  }

  public DeliveryRequest getRequest() {
    return request.getDeliveryRequest();
  }

  public DeliveryResponse getResponse() {
    return response;
  }

  public List<TargetCookie> getCookies() {
    if (response == null
        || !(response.getStatus() == HttpStatus.SC_OK
            || response.getStatus() == HttpStatus.SC_PARTIAL_CONTENT)) {
      return Collections.EMPTY_LIST;
    }
    List<TargetCookie> requestCookies = new ArrayList<>();
    CookieUtils.createTargetCookie(request.getSessionId(), response.getId().getTntId())
        .ifPresent(targetCookie -> requestCookies.add(targetCookie));
    CookieUtils.createClusterCookie(response.getId().getTntId())
        .ifPresent(clusterCookie -> requestCookies.add(clusterCookie));
    return requestCookies;
  }

  public Map<String, VisitorState> getVisitorState() {
    if (request.getVisitor() == null) {
      return Collections.emptyMap();
    }
    return request.getVisitor().getState();
  }

  public int getStatus() {
    return status.getStatus();
  }

  public String getMessage() {
    return status.getMessage();
  }

  public ResponseStatus getResponseStatus() {
    return status;
  }

  @JsonIgnore
  public ServerState getServerState() {
    return new ServerState(request.getDeliveryRequest(), response);
  }

  @Override
  public String toString() {
    return "TargetDeliveryResponse{"
        + "request="
        + request
        + ", response="
        + response
        + ", status="
        + status
        + '}';
  }
}
