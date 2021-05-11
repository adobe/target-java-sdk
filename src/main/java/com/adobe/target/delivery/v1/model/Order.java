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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Object that describes the order details. */
@ApiModel(description = "Object that describes the order details.")
@JsonPropertyOrder({
  Order.JSON_PROPERTY_ID,
  Order.JSON_PROPERTY_TOTAL,
  Order.JSON_PROPERTY_PURCHASED_PRODUCT_IDS,
  Order.JSON_PROPERTY_TIME,
  Order.JSON_PROPERTY_EXPERIENCE_LOCAL_ID,
  Order.JSON_PROPERTY_DUPLICATE,
  Order.JSON_PROPERTY_OUTLIER
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Order {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_TOTAL = "total";
  private BigDecimal total;

  public static final String JSON_PROPERTY_PURCHASED_PRODUCT_IDS = "purchasedProductIds";
  private List<String> purchasedProductIds = null;

  public static final String JSON_PROPERTY_TIME = "time";
  private OffsetDateTime time = null;

  public static final String JSON_PROPERTY_EXPERIENCE_LOCAL_ID = "experienceLocalId";
  private Integer experienceLocalId;

  public static final String JSON_PROPERTY_DUPLICATE = "duplicate";
  private Boolean duplicate;

  public static final String JSON_PROPERTY_OUTLIER = "outlier";
  private Boolean outlier;

  public Order id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Order Id.
   *
   * @return id
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Order Id.")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Order total(BigDecimal total) {

    this.total = total;
    return this;
  }

  /**
   * Order Total. The amount of money in the current order. minimum: 0
   *
   * @return total
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Order Total. The amount of money in the current order. ")
  @JsonProperty(JSON_PROPERTY_TOTAL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public Order purchasedProductIds(List<String> purchasedProductIds) {

    this.purchasedProductIds = purchasedProductIds;
    return this;
  }

  public Order addPurchasedProductIdsItem(String purchasedProductIdsItem) {
    if (this.purchasedProductIds == null) {
      this.purchasedProductIds = new ArrayList<>();
    }
    this.purchasedProductIds.add(purchasedProductIdsItem);
    return this;
  }

  /**
   * Order&#39;s product ids. Validation * No blank values allowed. * Each product Id max length 50.
   * * Product ids, separated by commas and concatenated, total length should not exceed 250.
   *
   * @return purchasedProductIds
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Order's product ids. Validation   * No blank values allowed.   * Each product Id max length 50.   * Product ids, separated by commas and concatenated, total length should not exceed 250. ")
  @JsonProperty(JSON_PROPERTY_PURCHASED_PRODUCT_IDS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<String> getPurchasedProductIds() {
    return purchasedProductIds;
  }

  public void setPurchasedProductIds(List<String> purchasedProductIds) {
    this.purchasedProductIds = purchasedProductIds;
  }

  public Order time(OffsetDateTime time) {

    this.time = time;
    return this;
  }

  /**
   * Time in the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals) format
   *
   * @return time
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Time in the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601#Time_intervals) format ")
  @JsonProperty(JSON_PROPERTY_TIME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public Order experienceLocalId(Integer experienceLocalId) {

    this.experienceLocalId = experienceLocalId;
    return this;
  }

  /**
   * Id used to track the experience across POST/PUT requests minimum: 0 maximum: 2147483647
   *
   * @return experienceLocalId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Id used to track the experience across POST/PUT requests")
  @JsonProperty(JSON_PROPERTY_EXPERIENCE_LOCAL_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getExperienceLocalId() {
    return experienceLocalId;
  }

  public void setExperienceLocalId(Integer experienceLocalId) {
    this.experienceLocalId = experienceLocalId;
  }

  public Order duplicate(Boolean duplicate) {

    this.duplicate = duplicate;
    return this;
  }

  /**
   * Whether or not the order is a duplicate
   *
   * @return duplicate
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Whether or not the order is a duplicate")
  @JsonProperty(JSON_PROPERTY_DUPLICATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Boolean getDuplicate() {
    return duplicate;
  }

  public void setDuplicate(Boolean duplicate) {
    this.duplicate = duplicate;
  }

  public Order outlier(Boolean outlier) {

    this.outlier = outlier;
    return this;
  }

  /**
   * Whether or not the order is abnormally different from the rest in volume
   *
   * @return outlier
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value = "Whether or not the order is abnormally different from the rest in volume")
  @JsonProperty(JSON_PROPERTY_OUTLIER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Boolean getOutlier() {
    return outlier;
  }

  public void setOutlier(Boolean outlier) {
    this.outlier = outlier;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(this.id, order.id)
        && Objects.equals(this.total, order.total)
        && Objects.equals(this.purchasedProductIds, order.purchasedProductIds)
        && Objects.equals(this.time, order.time)
        && Objects.equals(this.experienceLocalId, order.experienceLocalId)
        && Objects.equals(this.duplicate, order.duplicate)
        && Objects.equals(this.outlier, order.outlier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, total, purchasedProductIds, time, experienceLocalId, duplicate, outlier);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Order {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    purchasedProductIds: ")
        .append(toIndentedString(purchasedProductIds))
        .append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    experienceLocalId: ").append(toIndentedString(experienceLocalId)).append("\n");
    sb.append("    duplicate: ").append(toIndentedString(duplicate)).append("\n");
    sb.append("    outlier: ").append(toIndentedString(outlier)).append("\n");
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
