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
 * Geo data. If not specified, and Geo is enabled for the client, it will be resolved via user&#39;s
 * IP.
 */
@ApiModel(
    description =
        "Geo data. If not specified, and Geo is enabled for the client, it will be resolved via user's IP. ")
@JsonPropertyOrder({
  Geo.JSON_PROPERTY_IP_ADDRESS,
  Geo.JSON_PROPERTY_LATITUDE,
  Geo.JSON_PROPERTY_LONGITUDE,
  Geo.JSON_PROPERTY_COUNTRY_CODE,
  Geo.JSON_PROPERTY_STATE_CODE,
  Geo.JSON_PROPERTY_CITY,
  Geo.JSON_PROPERTY_ZIP
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class Geo {
  public static final String JSON_PROPERTY_IP_ADDRESS = "ipAddress";
  private String ipAddress;

  public static final String JSON_PROPERTY_LATITUDE = "latitude";
  private Float latitude;

  public static final String JSON_PROPERTY_LONGITUDE = "longitude";
  private Float longitude;

  public static final String JSON_PROPERTY_COUNTRY_CODE = "countryCode";
  private String countryCode;

  public static final String JSON_PROPERTY_STATE_CODE = "stateCode";
  private String stateCode;

  public static final String JSON_PROPERTY_CITY = "city";
  private String city;

  public static final String JSON_PROPERTY_ZIP = "zip";
  private String zip;

  public Geo ipAddress(String ipAddress) {

    this.ipAddress = ipAddress;
    return this;
  }

  /**
   * IPv4 or IPv6 address for Geo resolution
   *
   * @return ipAddress
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "IPv4 or IPv6 address for Geo resolution")
  @JsonProperty(JSON_PROPERTY_IP_ADDRESS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public Geo latitude(Float latitude) {

    this.latitude = latitude;
    return this;
  }

  /**
   * Latitude
   *
   * @return latitude
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Latitude")
  @JsonProperty(JSON_PROPERTY_LATITUDE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Float getLatitude() {
    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public Geo longitude(Float longitude) {

    this.longitude = longitude;
    return this;
  }

  /**
   * Longitude
   *
   * @return longitude
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Longitude")
  @JsonProperty(JSON_PROPERTY_LONGITUDE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Float getLongitude() {
    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }

  public Geo countryCode(String countryCode) {

    this.countryCode = countryCode;
    return this;
  }

  /**
   * Country code in ISO 3166-1 alpha-2 format
   *
   * @return countryCode
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Country code in ISO 3166-1 alpha-2 format")
  @JsonProperty(JSON_PROPERTY_COUNTRY_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public Geo stateCode(String stateCode) {

    this.stateCode = stateCode;
    return this;
  }

  /**
   * Alphanumeric characters representing the subdivision part from ISO 3166-2
   *
   * @return stateCode
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value = "Alphanumeric characters representing the subdivision part from ISO 3166-2")
  @JsonProperty(JSON_PROPERTY_STATE_CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public Geo city(String city) {

    this.city = city;
    return this;
  }

  /**
   * City
   *
   * @return city
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "City")
  @JsonProperty(JSON_PROPERTY_CITY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Geo zip(String zip) {

    this.zip = zip;
    return this;
  }

  /**
   * Zip/Postal Code
   *
   * @return zip
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Zip/Postal Code")
  @JsonProperty(JSON_PROPERTY_ZIP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Geo geo = (Geo) o;
    return Objects.equals(this.ipAddress, geo.ipAddress)
        && Objects.equals(this.latitude, geo.latitude)
        && Objects.equals(this.longitude, geo.longitude)
        && Objects.equals(this.countryCode, geo.countryCode)
        && Objects.equals(this.stateCode, geo.stateCode)
        && Objects.equals(this.city, geo.city)
        && Objects.equals(this.zip, geo.zip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ipAddress, latitude, longitude, countryCode, stateCode, city, zip);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Geo {\n");
    sb.append("    ipAddress: ").append(toIndentedString(ipAddress)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    stateCode: ").append(toIndentedString(stateCode)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
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
