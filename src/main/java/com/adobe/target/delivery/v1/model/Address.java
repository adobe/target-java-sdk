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

/** The address object. It indicates the current URL and the referring URL. */
public class Address {
  @JsonProperty("url")
  private String url;

  @JsonProperty("referringUrl")
  private String referringUrl;

  public Address url(String url) {
    this.url = url;
    return this;
  }

  /**
   * URL
   *
   * @return url
   */
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Address referringUrl(String referringUrl) {
    this.referringUrl = referringUrl;
    return this;
  }

  /**
   * referral URL
   *
   * @return referringUrl
   */
  public String getReferringUrl() {
    return referringUrl;
  }

  public void setReferringUrl(String referringUrl) {
    this.referringUrl = referringUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Address address = (Address) o;
    return Objects.equals(this.url, address.url)
        && Objects.equals(this.referringUrl, address.referringUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, referringUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Address {\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    referringUrl: ").append(toIndentedString(referringUrl)).append("\n");
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
