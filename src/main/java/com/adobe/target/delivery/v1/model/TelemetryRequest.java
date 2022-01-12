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

/** Telemetry Request */
public class TelemetryRequest {
  @JsonProperty("dns")
  private Double dns;

  @JsonProperty("tls")
  private Double tls;

  @JsonProperty("timeToFirstByte")
  private Double timeToFirstByte;

  @JsonProperty("download")
  private Double download;

  @JsonProperty("responseSize")
  private Long responseSize;

  public TelemetryRequest dns(Double dns) {
    this.dns = dns;
    return this;
  }

  /**
   * DNS resolution time, in milliseconds elapsed since UNIX epoch.
   *
   * @return dns
   */
  public Double getDns() {
    return dns;
  }

  public void setDns(Double dns) {
    this.dns = dns;
  }

  public TelemetryRequest tls(Double tls) {
    this.tls = tls;
    return this;
  }

  /**
   * TLS handshake time, in milliseconds elapsed since UNIX epoch.
   *
   * @return tls
   */
  public Double getTls() {
    return tls;
  }

  public void setTls(Double tls) {
    this.tls = tls;
  }

  public TelemetryRequest timeToFirstByte(Double timeToFirstByte) {
    this.timeToFirstByte = timeToFirstByte;
    return this;
  }

  /**
   * Time to first byte, in milliseconds elapsed since UNIX epoch.
   *
   * @return timeToFirstByte
   */
  public Double getTimeToFirstByte() {
    return timeToFirstByte;
  }

  public void setTimeToFirstByte(Double timeToFirstByte) {
    this.timeToFirstByte = timeToFirstByte;
  }

  public TelemetryRequest download(Double download) {
    this.download = download;
    return this;
  }

  /**
   * Download time, in milliseconds elapsed since UNIX epoch.
   *
   * @return download
   */
  public Double getDownload() {
    return download;
  }

  public void setDownload(Double download) {
    this.download = download;
  }

  public TelemetryRequest responseSize(Long responseSize) {
    this.responseSize = responseSize;
    return this;
  }

  /**
   * Response size
   *
   * @return responseSize
   */
  public Long getResponseSize() {
    return responseSize;
  }

  public void setResponseSize(Long responseSize) {
    this.responseSize = responseSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelemetryRequest telemetryRequest = (TelemetryRequest) o;
    return Objects.equals(this.dns, telemetryRequest.dns)
        && Objects.equals(this.tls, telemetryRequest.tls)
        && Objects.equals(this.timeToFirstByte, telemetryRequest.timeToFirstByte)
        && Objects.equals(this.download, telemetryRequest.download)
        && Objects.equals(this.responseSize, telemetryRequest.responseSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dns, tls, timeToFirstByte, download, responseSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TelemetryRequest {\n");
    sb.append("    dns: ").append(toIndentedString(dns)).append("\n");
    sb.append("    tls: ").append(toIndentedString(tls)).append("\n");
    sb.append("    timeToFirstByte: ").append(toIndentedString(timeToFirstByte)).append("\n");
    sb.append("    download: ").append(toIndentedString(download)).append("\n");
    sb.append("    responseSize: ").append(toIndentedString(responseSize)).append("\n");
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
