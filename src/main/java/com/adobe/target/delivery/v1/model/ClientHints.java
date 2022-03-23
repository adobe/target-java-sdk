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

/** Client hints data. Used in place of userAgent if provided. */
public class ClientHints {
  @JsonProperty("mobile")
  private Boolean mobile;

  @JsonProperty("model")
  private String model;

  @JsonProperty("platform")
  private String platform;

  @JsonProperty("platformVersion")
  private String platformVersion;

  @JsonProperty("browserUAWithMajorVersion")
  private String browserUAWithMajorVersion;

  @JsonProperty("browserUAWithFullVersion")
  private String browserUAWithFullVersion;

  @JsonProperty("architecture")
  private String architecture;

  @JsonProperty("bitness")
  private String bitness;

  public ClientHints mobile(Boolean mobile) {
    this.mobile = mobile;
    return this;
  }

  /**
   * Sec-CH-UA-Mobile (low entropy)
   *
   * @return mobile
   */
  public Boolean getMobile() {
    return mobile;
  }

  public void setMobile(Boolean mobile) {
    this.mobile = mobile;
  }

  public ClientHints model(String model) {
    this.model = model;
    return this;
  }

  /**
   * Sec-CH-UA-Model
   *
   * @return model
   */
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public ClientHints platform(String platform) {
    this.platform = platform;
    return this;
  }

  /**
   * Sec-CH-UA-Platform (low entropy)
   *
   * @return platform
   */
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public ClientHints platformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
    return this;
  }

  /**
   * Sec-CH-UA-Platform-Version
   *
   * @return platformVersion
   */
  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  public ClientHints browserUAWithMajorVersion(String browserUAWithMajorVersion) {
    this.browserUAWithMajorVersion = browserUAWithMajorVersion;
    return this;
  }

  /**
   * Sec-CH-UA (low entropy)
   *
   * @return browserUAWithMajorVersion
   */
  public String getBrowserUAWithMajorVersion() {
    return browserUAWithMajorVersion;
  }

  public void setBrowserUAWithMajorVersion(String browserUAWithMajorVersion) {
    this.browserUAWithMajorVersion = browserUAWithMajorVersion;
  }

  public ClientHints browserUAWithFullVersion(String browserUAWithFullVersion) {
    this.browserUAWithFullVersion = browserUAWithFullVersion;
    return this;
  }

  /**
   * Sec-CH-UA-Full-Version-List
   *
   * @return browserUAWithFullVersion
   */
  public String getBrowserUAWithFullVersion() {
    return browserUAWithFullVersion;
  }

  public void setBrowserUAWithFullVersion(String browserUAWithFullVersion) {
    this.browserUAWithFullVersion = browserUAWithFullVersion;
  }

  public ClientHints architecture(String architecture) {
    this.architecture = architecture;
    return this;
  }

  /**
   * Sec-CH-UA-Arch
   *
   * @return architecture
   */
  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture(String architecture) {
    this.architecture = architecture;
  }

  public ClientHints bitness(String bitness) {
    this.bitness = bitness;
    return this;
  }

  /**
   * Sec-CH-UA-Bitness
   *
   * @return bitness
   */
  public String getBitness() {
    return bitness;
  }

  public void setBitness(String bitness) {
    this.bitness = bitness;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientHints clientHints = (ClientHints) o;
    return Objects.equals(this.mobile, clientHints.mobile)
        && Objects.equals(this.model, clientHints.model)
        && Objects.equals(this.platform, clientHints.platform)
        && Objects.equals(this.platformVersion, clientHints.platformVersion)
        && Objects.equals(this.browserUAWithMajorVersion, clientHints.browserUAWithMajorVersion)
        && Objects.equals(this.browserUAWithFullVersion, clientHints.browserUAWithFullVersion)
        && Objects.equals(this.architecture, clientHints.architecture)
        && Objects.equals(this.bitness, clientHints.bitness);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        mobile,
        model,
        platform,
        platformVersion,
        browserUAWithMajorVersion,
        browserUAWithFullVersion,
        architecture,
        bitness);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientHints {\n");
    sb.append("    mobile: ").append(toIndentedString(mobile)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    platform: ").append(toIndentedString(platform)).append("\n");
    sb.append("    platformVersion: ").append(toIndentedString(platformVersion)).append("\n");
    sb.append("    browserUAWithMajorVersion: ")
        .append(toIndentedString(browserUAWithMajorVersion))
        .append("\n");
    sb.append("    browserUAWithFullVersion: ")
        .append(toIndentedString(browserUAWithFullVersion))
        .append("\n");
    sb.append("    architecture: ").append(toIndentedString(architecture)).append("\n");
    sb.append("    bitness: ").append(toIndentedString(bitness)).append("\n");
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
