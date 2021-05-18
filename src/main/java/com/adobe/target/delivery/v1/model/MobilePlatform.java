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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** Mobile Platform should be specified when the channel is Mobile */
@ApiModel(description = "Mobile Platform should be specified when the channel is Mobile")
@JsonPropertyOrder({
  MobilePlatform.JSON_PROPERTY_DEVICE_NAME,
  MobilePlatform.JSON_PROPERTY_DEVICE_TYPE,
  MobilePlatform.JSON_PROPERTY_PLATFORM_TYPE,
  MobilePlatform.JSON_PROPERTY_VERSION
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class MobilePlatform {
  public static final String JSON_PROPERTY_DEVICE_NAME = "deviceName";
  private String deviceName;

  public static final String JSON_PROPERTY_DEVICE_TYPE = "deviceType";
  private DeviceType deviceType;

  public static final String JSON_PROPERTY_PLATFORM_TYPE = "platformType";
  private MobilePlatformType platformType;

  public static final String JSON_PROPERTY_VERSION = "version";
  private String version;

  public MobilePlatform deviceName(String deviceName) {

    this.deviceName = deviceName;
    return this;
  }

  /**
   * Optional field, added to help with device detection using device atlas. This is equivalent of
   * a.DeviceName field passed in from Mobile SDK
   *
   * @return deviceName
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Optional field, added to help with device detection using device atlas. This is equivalent of a.DeviceName field passed in from Mobile SDK ")
  @JsonProperty(JSON_PROPERTY_DEVICE_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public MobilePlatform deviceType(DeviceType deviceType) {

    this.deviceType = deviceType;
    return this;
  }

  /**
   * Get deviceType
   *
   * @return deviceType
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_DEVICE_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public DeviceType getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(DeviceType deviceType) {
    this.deviceType = deviceType;
  }

  public MobilePlatform platformType(MobilePlatformType platformType) {

    this.platformType = platformType;
    return this;
  }

  /**
   * Get platformType
   *
   * @return platformType
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_PLATFORM_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public MobilePlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(MobilePlatformType platformType) {
    this.platformType = platformType;
  }

  public MobilePlatform version(String version) {

    this.version = version;
    return this;
  }

  /**
   * If not specified - all activities with any platformVersion will be evaluated. If specified -
   * only activities with the same platformVersion will be evaluated.
   *
   * @return version
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "If not specified - all activities with any platformVersion will be evaluated. If specified - only activities with the same platformVersion will be evaluated. ")
  @JsonProperty(JSON_PROPERTY_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MobilePlatform mobilePlatform = (MobilePlatform) o;
    return Objects.equals(this.deviceName, mobilePlatform.deviceName)
        && Objects.equals(this.deviceType, mobilePlatform.deviceType)
        && Objects.equals(this.platformType, mobilePlatform.platformType)
        && Objects.equals(this.version, mobilePlatform.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deviceName, deviceType, platformType, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MobilePlatform {\n");
    sb.append("    deviceName: ").append(toIndentedString(deviceName)).append("\n");
    sb.append("    deviceType: ").append(toIndentedString(deviceType)).append("\n");
    sb.append("    platformType: ").append(toIndentedString(platformType)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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
