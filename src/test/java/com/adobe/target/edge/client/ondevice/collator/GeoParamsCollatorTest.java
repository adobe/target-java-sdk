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
 */
package com.adobe.target.edge.client.ondevice.collator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.Geo;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GeoParamsCollatorTest {

  @Test
  public void testCollator() {
    VisitorProvider.init("testOrgId");
    RequestDetails pageLoad = new RequestDetails();
    Geo geo = new Geo();
    geo.setCity("san francisco");
    geo.setStateCode("ca");
    geo.setCountryCode("us");
    geo.setLatitude(37.74f);
    geo.setLongitude(-122.24f);
    TargetDeliveryRequest request =
        TargetDeliveryRequest.builder()
            .execute(new ExecuteRequest().pageLoad(pageLoad))
            .context(new Context().geo(geo))
            .build();
    GeoParamsCollator collator = new GeoParamsCollator();
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("SANFRANCISCO", result.get(GeoParamsCollator.GEO_CITY));
    assertEquals("CA", result.get(GeoParamsCollator.GEO_REGION));
    assertEquals("US", result.get(GeoParamsCollator.GEO_COUNTRY));
    assertEquals(37.74f, (Float) result.get(GeoParamsCollator.GEO_LATITUDE), 0.01);
    assertEquals(-122.24f, (Float) result.get(GeoParamsCollator.GEO_LONGITUDE), 0.01);
  }
}
