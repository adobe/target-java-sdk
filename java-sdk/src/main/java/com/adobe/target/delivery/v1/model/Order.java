/*
 * Copyright 2019 Adobe. All rights reserved.
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Object that describes the order details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    @JsonProperty("id")
    private String id;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("purchasedProductIds")
    private List<String> purchasedProductIds = new ArrayList<>();

    @JsonProperty("time")
    private OffsetDateTime time;

    @JsonProperty("experienceLocalId")
    private Integer experienceLocalId;

    @JsonProperty("duplicate")
    private Boolean duplicate;

    @JsonProperty("outlier")
    private Boolean outlier;

    public Order id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Order Id.
     *
     * @return id
     **/

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
     * Order Total. The amount of money in the current order.
     * minimum: 0
     *
     * @return total
     **/

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
     * Order&#39;s product ids. Validation   * No blank values allowed.   * Each product Id max length 50.   * Product
     * ids, separated by commas and concatenated, total length should not exceed 250.
     *
     * @return purchasedProductIds
     **/

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
     * Time in the [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) format
     *
     * @return time
     **/

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
     * Id used to track the experience across POST/PUT requests
     * minimum: 0
     * maximum: 2147483647
     *
     * @return experienceLocalId
     **/

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
     **/

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
     **/

    public Boolean getOutlier() {
        return outlier;
    }

    public void setOutlier(Boolean outlier) {
        this.outlier = outlier;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(this.id, order.id) &&
                Objects.equals(this.total, order.total) &&
                Objects.equals(this.purchasedProductIds, order.purchasedProductIds) &&
                Objects.equals(this.time, order.time) &&
                Objects.equals(this.experienceLocalId, order.experienceLocalId) &&
                Objects.equals(this.duplicate, order.duplicate) &&
                Objects.equals(this.outlier, order.outlier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, total, purchasedProductIds, time, experienceLocalId, duplicate, outlier);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    purchasedProductIds: ").append(toIndentedString(purchasedProductIds)).append("\n");
        sb.append("    time: ").append(toIndentedString(time)).append("\n");
        sb.append("    experienceLocalId: ").append(toIndentedString(experienceLocalId)).append("\n");
        sb.append("    duplicate: ").append(toIndentedString(duplicate)).append("\n");
        sb.append("    outlier: ").append(toIndentedString(outlier)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

