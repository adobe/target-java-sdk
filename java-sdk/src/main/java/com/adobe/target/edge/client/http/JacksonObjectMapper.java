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
 */
package com.adobe.target.edge.client.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import kong.unirest.GenericType;
import kong.unirest.UnirestException;

public class JacksonObjectMapper implements kong.unirest.ObjectMapper {

  private final ObjectMapper objectMapper;

  public JacksonObjectMapper() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.objectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
  }

  @Override
  public <T> T readValue(String value, Class<T> valueType) {
    try {
      return objectMapper.readValue(value, valueType);
    } catch (IOException e) {
      throw new UnirestException(e);
    }
  }

  @Override
  public <T> T readValue(String value, GenericType<T> genericType) {
    try {
      return objectMapper.readValue(value, objectMapper.constructType(genericType.getType()));
    } catch (IOException e) {
      throw new UnirestException(e);
    }
  }

  @Override
  public String writeValue(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new UnirestException(e);
    }
  }

  public ObjectMapper getMapper() {
    return this.objectMapper;
  }
}
