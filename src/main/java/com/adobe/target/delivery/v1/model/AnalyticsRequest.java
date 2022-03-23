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
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Integration with Adobe Analytics (A4T) */
public class AnalyticsRequest {
  @JsonProperty("supplementalDataId")
  private String supplementalDataId;

  @JsonProperty("logging")
  private LoggingType logging;

  @JsonProperty("trackingServer")
  private String trackingServer;

  @JsonProperty("trackingServerSecure")
  private String trackingServerSecure;

  public AnalyticsRequest supplementalDataId(String supplementalDataId) {
    this.supplementalDataId = supplementalDataId;
    return this;
  }

  /**
   * Supplemental data id, used for **server side** integrations. Format &lt;16 hexadecimal
   * digits&gt;-&lt;16 hexadecimal digits&gt;
   *
   * @return supplementalDataId
   */
  public String getSupplementalDataId() {
    return supplementalDataId;
  }

  public void setSupplementalDataId(String supplementalDataId) {
    this.supplementalDataId = supplementalDataId;
  }

  public AnalyticsRequest logging(LoggingType logging) {
    this.logging = logging;
    return this;
  }

  /**
   * Get logging
   *
   * @return logging
   */
  public LoggingType getLogging() {
    return logging;
  }

  public void setLogging(LoggingType logging) {
    this.logging = logging;
  }

  public AnalyticsRequest trackingServer(String trackingServer) {
    this.trackingServer = trackingServer;
    return this;
  }

  /**
   * tracking server domain (should not include http://)
   *
   * @return trackingServer
   */
  public String getTrackingServer() {
    return trackingServer;
  }

  public void setTrackingServer(String trackingServer) {
    this.trackingServer = trackingServer;
  }

  public AnalyticsRequest trackingServerSecure(String trackingServerSecure) {
    this.trackingServerSecure = trackingServerSecure;
    return this;
  }

  /**
   * secure tracking server domain (should not include https://)
   *
   * @return trackingServerSecure
   */
  public String getTrackingServerSecure() {
    return trackingServerSecure;
  }

  public void setTrackingServerSecure(String trackingServerSecure) {
    this.trackingServerSecure = trackingServerSecure;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnalyticsRequest analyticsRequest = (AnalyticsRequest) o;
    return Objects.equals(this.supplementalDataId, analyticsRequest.supplementalDataId)
        && Objects.equals(this.logging, analyticsRequest.logging)
        && Objects.equals(this.trackingServer, analyticsRequest.trackingServer)
        && Objects.equals(this.trackingServerSecure, analyticsRequest.trackingServerSecure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supplementalDataId, logging, trackingServer, trackingServerSecure);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnalyticsRequest {\n");
    sb.append("    supplementalDataId: ").append(toIndentedString(supplementalDataId)).append("\n");
    sb.append("    logging: ").append(toIndentedString(logging)).append("\n");
    sb.append("    trackingServer: ").append(toIndentedString(trackingServer)).append("\n");
    sb.append("    trackingServerSecure: ")
        .append(toIndentedString(trackingServerSecure))
        .append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
