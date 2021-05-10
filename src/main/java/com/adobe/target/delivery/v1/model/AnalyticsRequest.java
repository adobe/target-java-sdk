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
package Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** Integration with Adobe Analytics (A4T) */
@ApiModel(description = "Integration with Adobe Analytics (A4T)")
@JsonPropertyOrder({
  AnalyticsRequest.JSON_PROPERTY_SUPPLEMENTAL_DATA_ID,
  AnalyticsRequest.JSON_PROPERTY_LOGGING,
  AnalyticsRequest.JSON_PROPERTY_TRACKING_SERVER,
  AnalyticsRequest.JSON_PROPERTY_TRACKING_SERVER_SECURE
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class AnalyticsRequest {
  public static final String JSON_PROPERTY_SUPPLEMENTAL_DATA_ID = "supplementalDataId";
  private String supplementalDataId;

  public static final String JSON_PROPERTY_LOGGING = "logging";
  private LoggingType logging;

  public static final String JSON_PROPERTY_TRACKING_SERVER = "trackingServer";
  private String trackingServer;

  public static final String JSON_PROPERTY_TRACKING_SERVER_SECURE = "trackingServerSecure";
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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Supplemental data id, used for **server side** integrations. Format <16 hexadecimal digits>-<16 hexadecimal digits> ")
  @JsonProperty(JSON_PROPERTY_SUPPLEMENTAL_DATA_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_LOGGING)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
   * tracking server
   *
   * @return trackingServer
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "tracking server")
  @JsonProperty(JSON_PROPERTY_TRACKING_SERVER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
   * secure tracking server
   *
   * @return trackingServerSecure
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "secure tracking server")
  @JsonProperty(JSON_PROPERTY_TRACKING_SERVER_SECURE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getTrackingServerSecure() {
    return trackingServerSecure;
  }

  public void setTrackingServerSecure(String trackingServerSecure) {
    this.trackingServerSecure = trackingServerSecure;
  }

  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
