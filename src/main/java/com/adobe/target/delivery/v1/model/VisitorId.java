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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Object that contains the identifiers for the visitor. If no id is provided in the first request,
 * Target will generate a VisitorId with a tntId. The code that runs on the client side is then
 * responsible for passing this tntId value on all subsequent calls. Validation * Either tntId,
 * thirdPartyId or maketingCloudVisistorId required.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitorId {
  @JsonProperty("tntId")
  private String tntId;

  @JsonProperty("thirdPartyId")
  private String thirdPartyId;

  @JsonProperty("marketingCloudVisitorId")
  private String marketingCloudVisitorId;

  @JsonProperty("customerIds")
  private List<CustomerId> customerIds = new ArrayList<>();

  public VisitorId tntId(String tntId) {
    this.tntId = tntId;
    return this;
  }

  /**
   * Tnt id - an unique identifier (UUID). If no visitor id is provided the TNT id will be generated
   * by the TNT server. The TNT id retunred by the server may also contain the profile location
   * hint, with the same format as for other endpoints (standard/ajax/json/..). Example
   * 32440324234-2343423.23_41, in this example the 23_41 is the profile location hint. Tnt id
   * retuned in the response (containting the profile location hint) should be used in the
   * subsequent requests Validation * Should not contain a &#39;.&#39; (dot) unless the dot delimits
   * the location hint.
   *
   * @return tntId
   */
  public String getTntId() {
    return tntId;
  }

  public void setTntId(String tntId) {
    this.tntId = tntId;
  }

  public VisitorId thirdPartyId(String thirdPartyId) {
    this.thirdPartyId = thirdPartyId;
    return this;
  }

  /**
   * Get thirdPartyId
   *
   * @return thirdPartyId
   */
  public String getThirdPartyId() {
    return thirdPartyId;
  }

  public void setThirdPartyId(String thirdPartyId) {
    this.thirdPartyId = thirdPartyId;
  }

  public VisitorId marketingCloudVisitorId(String marketingCloudVisitorId) {
    this.marketingCloudVisitorId = marketingCloudVisitorId;
    return this;
  }

  /**
   * Get marketingCloudVisitorId
   *
   * @return marketingCloudVisitorId
   */
  public String getMarketingCloudVisitorId() {
    return marketingCloudVisitorId;
  }

  public void setMarketingCloudVisitorId(String marketingCloudVisitorId) {
    this.marketingCloudVisitorId = marketingCloudVisitorId;
  }

  public VisitorId customerIds(List<CustomerId> customerIds) {
    this.customerIds = customerIds;
    return this;
  }

  public VisitorId addCustomerIdsItem(CustomerId customerIdsItem) {
    if (this.customerIds == null) {
      this.customerIds = new ArrayList<>();
    }
    this.customerIds.add(customerIdsItem);
    return this;
  }

  /**
   * Validation * No null elements.
   *
   * @return customerIds
   */
  public List<CustomerId> getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(List<CustomerId> customerIds) {
    this.customerIds = customerIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VisitorId visitorId = (VisitorId) o;
    return Objects.equals(this.tntId, visitorId.tntId)
        && Objects.equals(this.thirdPartyId, visitorId.thirdPartyId)
        && Objects.equals(this.marketingCloudVisitorId, visitorId.marketingCloudVisitorId)
        && Objects.equals(this.customerIds, visitorId.customerIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tntId, thirdPartyId, marketingCloudVisitorId, customerIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VisitorId {\n");
    sb.append("    tntId: ").append(toIndentedString(tntId)).append("\n");
    sb.append("    thirdPartyId: ").append(toIndentedString(thirdPartyId)).append("\n");
    sb.append("    marketingCloudVisitorId: ")
        .append(toIndentedString(marketingCloudVisitorId))
        .append("\n");
    sb.append("    customerIds: ").append(toIndentedString(customerIds)).append("\n");
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
