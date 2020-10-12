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

/** The content from the activitiesc is returned via the option objects. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Option {
  @JsonProperty("type")
  private OptionType type = null;

  @JsonProperty("content")
  private Object content = null;

  @JsonProperty("eventToken")
  private String eventToken;

  @JsonProperty("responseTokens")
  private Map<String, Object> responseTokens = new HashMap<>();

  public Option type(OptionType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  public OptionType getType() {
    return type;
  }

  public void setType(OptionType type) {
    this.type = type;
  }

  public Option content(Object content) {
    this.content = content;
    return this;
  }

  /**
   * Content that should be applied/displayed/replaced etc, based on the option type. Content can be
   * one of: * html * redirect link * link for a dynamic offer * raw json * one or more actions
   * (json - from offers with templates and visual offers) Actions format is specific for delivery
   * API.
   *
   * @return content
   */
  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

  public Option eventToken(String eventToken) {
    this.eventToken = eventToken;
    return this;
  }

  /**
   * Will be present only in response of a prefetch request. After the content is displayed the
   * event token should be sent via notifications to the edge server so that
   * visit/visitor/impression events could be logged.
   *
   * @return eventToken
   */
  public String getEventToken() {
    return eventToken;
  }

  public void setEventToken(String eventToken) {
    this.eventToken = eventToken;
  }

  public Option responseTokens(Map<String, Object> responseTokens) {
    this.responseTokens = responseTokens;
    return this;
  }

  public Option putResponseTokensItem(String key, Object responseTokensItem) {
    if (this.responseTokens == null) {
      this.responseTokens = new HashMap<>();
    }
    this.responseTokens.put(key, responseTokensItem);
    return this;
  }

  /**
   * List of the response tokens and their values for the given option. Response tokens can be
   * defined via the /v1/responsetokens API. The values for the tokens are computed for every option
   * returned by a activity and represented as a dictionary: * Key - the response token name. *
   * Value - the response token value. The value is usually a string, but it can be a list of string
   * in case of &#39;category affinity&#39; response token.
   *
   * @return responseTokens
   */
  public Map<String, Object> getResponseTokens() {
    return responseTokens;
  }

  public void setResponseTokens(Map<String, Object> responseTokens) {
    this.responseTokens = responseTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Option option = (Option) o;
    return Objects.equals(this.type, option.type)
        && Objects.equals(this.content, option.content)
        && Objects.equals(this.eventToken, option.eventToken)
        && Objects.equals(this.responseTokens, option.responseTokens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, content, eventToken, responseTokens);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Option {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    eventToken: ").append(toIndentedString(eventToken)).append("\n");
    sb.append("    responseTokens: ").append(toIndentedString(responseTokens)).append("\n");
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
