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

/** The properties of the products */
@ApiModel(description = "The properties of the products")
@JsonPropertyOrder({Product.JSON_PROPERTY_ID, Product.JSON_PROPERTY_CATEGORY_ID})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Product {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_CATEGORY_ID = "categoryId";
  private String categoryId;

  public Product id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Product id. Should not be blank.
   *
   * @return id
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Product id. Should not be blank.")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Product categoryId(String categoryId) {

    this.categoryId = categoryId;
    return this;
  }

  /**
   * Category id. Should not be blank.
   *
   * @return categoryId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Category id. Should not be blank.")
  @JsonProperty(JSON_PROPERTY_CATEGORY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product product = (Product) o;
    return Objects.equals(this.id, product.id)
        && Objects.equals(this.categoryId, product.categoryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, categoryId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Product {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    categoryId: ").append(toIndentedString(categoryId)).append("\n");
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
