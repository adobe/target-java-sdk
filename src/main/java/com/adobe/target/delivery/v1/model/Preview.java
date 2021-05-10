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
package Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * Use this object to enable the Preview mode in the request. Use the Preview mode to test the look
 * and feel of your site for various location and offer combinations.
 */
@ApiModel(
    description =
        "Use this object to enable the Preview mode in the request. Use the Preview mode to test the look and feel of your site for various location and offer combinations. ")
@JsonPropertyOrder({Preview.JSON_PROPERTY_TOKEN})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class Preview {
  public static final String JSON_PROPERTY_TOKEN = "token";
  private String token;

  public Preview token(String token) {

    this.token = token;
    return this;
  }

  /**
   * The token for the Preview mode. Validation * Verify that the token belongs to the client
   * provided in request.
   *
   * @return token
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The token for the Preview mode. Validation   * Verify that the token belongs to the client provided in request. ")
  @JsonProperty(JSON_PROPERTY_TOKEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Preview preview = (Preview) o;
    return Objects.equals(this.token, preview.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Preview {\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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
