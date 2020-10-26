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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Object common for prefetch, execute and notifications in order to specify the request details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDetails {
  @JsonProperty("address")
  private Address address = null;

  @JsonProperty("parameters")
  private Map<String, String> parameters = new HashMap<>();

  @JsonProperty("profileParameters")
  private Map<String, String> profileParameters = new HashMap<>();

  @JsonProperty("order")
  private Order order = null;

  @JsonProperty("product")
  private Product product = null;

  public RequestDetails address(Address address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   *
   * @return address
   */
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public RequestDetails parameters(Map<String, String> parameters) {
    this.parameters = parameters;
    return this;
  }

  public RequestDetails putParametersItem(String key, String parametersItem) {
    if (this.parameters == null) {
      this.parameters = new HashMap<>();
    }
    this.parameters.put(key, parametersItem);
    return this;
  }

  /**
   * Parameters map. Same object is reused for mbox or profile parameters with slight validation
   * differences. Following names are not allowed for mbox parameters: &#39;orderId&#39;,
   * &#39;orderTotal&#39;, productPurchasedIds&#39; Validation (for both mbox and profile
   * parameters): * Max 50 parameters limit. * Parameter name should not be blank. * Parameter name
   * max length 128. * Parameter name should not start with &#39;profile.&#39; * Parameter value
   * length max 5000.
   *
   * @return parameters
   */
  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public RequestDetails profileParameters(Map<String, String> profileParameters) {
    this.profileParameters = profileParameters;
    return this;
  }

  public RequestDetails putProfileParametersItem(String key, String profileParametersItem) {
    if (this.profileParameters == null) {
      this.profileParameters = new HashMap<>();
    }
    this.profileParameters.put(key, profileParametersItem);
    return this;
  }

  /**
   * Parameters map. Same object is reused for mbox or profile parameters with slight validation
   * differences. Following names are not allowed for mbox parameters: &#39;orderId&#39;,
   * &#39;orderTotal&#39;, productPurchasedIds&#39; Validation (for both mbox and profile
   * parameters): * Max 50 parameters limit. * Parameter name should not be blank. * Parameter name
   * max length 128. * Parameter name should not start with &#39;profile.&#39; * Parameter value
   * length max 5000.
   *
   * @return profileParameters
   */
  public Map<String, String> getProfileParameters() {
    return profileParameters;
  }

  public void setProfileParameters(Map<String, String> profileParameters) {
    this.profileParameters = profileParameters;
  }

  public RequestDetails order(Order order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   *
   * @return order
   */
  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public RequestDetails product(Product product) {
    this.product = product;
    return this;
  }

  /**
   * Get product
   *
   * @return product
   */
  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestDetails requestDetails = (RequestDetails) o;
    return Objects.equals(this.address, requestDetails.address)
        && Objects.equals(this.parameters, requestDetails.parameters)
        && Objects.equals(this.profileParameters, requestDetails.profileParameters)
        && Objects.equals(this.order, requestDetails.order)
        && Objects.equals(this.product, requestDetails.product);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, parameters, profileParameters, order, product);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequestDetails {\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    profileParameters: ").append(toIndentedString(profileParameters)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    product: ").append(toIndentedString(product)).append("\n");
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
