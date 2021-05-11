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
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** Action */
@JsonPropertyOrder({
  Action.JSON_PROPERTY_TYPE,
  Action.JSON_PROPERTY_SELECTOR,
  Action.JSON_PROPERTY_CSS_SELECTOR,
  Action.JSON_PROPERTY_CONTENT
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Action {
  public static final String JSON_PROPERTY_TYPE = "type";
  private String type;

  public static final String JSON_PROPERTY_SELECTOR = "selector";
  private String selector;

  public static final String JSON_PROPERTY_CSS_SELECTOR = "cssSelector";
  private String cssSelector;

  public static final String JSON_PROPERTY_CONTENT = "content";
  private Object content = null;

  public Action type(String type) {

    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Action selector(String selector) {

    this.selector = selector;
    return this;
  }

  /**
   * Get selector
   *
   * @return selector
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_SELECTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public Action cssSelector(String cssSelector) {

    this.cssSelector = cssSelector;
    return this;
  }

  /**
   * Get cssSelector
   *
   * @return cssSelector
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_CSS_SELECTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getCssSelector() {
    return cssSelector;
  }

  public void setCssSelector(String cssSelector) {
    this.cssSelector = cssSelector;
  }

  public Action content(Object content) {

    this.content = content;
    return this;
  }

  /**
   * Get content
   *
   * @return content
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_CONTENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Action action = (Action) o;
    return Objects.equals(this.type, action.type)
        && Objects.equals(this.selector, action.selector)
        && Objects.equals(this.cssSelector, action.cssSelector)
        && Objects.equals(this.content, action.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, selector, cssSelector, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Action {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    selector: ").append(toIndentedString(selector)).append("\n");
    sb.append("    cssSelector: ").append(toIndentedString(cssSelector)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
