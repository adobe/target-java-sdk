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

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.Geo;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class GeoParamsCollator implements ParamsCollator {

  public static final Map<String, Object> DEFAULT_GEO_PARAMS = new HashMap<String, Object>(){
    {
      put(GEO_LATITUDE, null);
      put(GEO_LONGITUDE, null);
      put(GEO_CITY, "");
      put(GEO_REGION, "");
      put(GEO_COUNTRY, "");
    }
  };

  protected static final String GEO_LATITUDE = "latitude";
  protected static final String GEO_LONGITUDE = "longitude";
  protected static final String GEO_CITY = "city";
  protected static final String GEO_REGION = "region";
  protected static final String GEO_COUNTRY = "country";


  public Map<String, Object> collateParams(
      TargetDeliveryRequest deliveryRequest, RequestDetails requestDetails) {
    Map<String, Object> params = new HashMap<>();
    Context context = deliveryRequest.getDeliveryRequest().getContext();
    if (context == null || context.getGeo() == null) {
      params.putAll(DEFAULT_GEO_PARAMS);
      return params;
    }

    Geo geo = context.getGeo();
    params.put(GEO_LATITUDE, geo.getLatitude());
    params.put(GEO_LONGITUDE, geo.getLongitude());
    params.put(GEO_CITY, StringUtils.isEmpty(geo.getCity()) ? "" : geo.getCity().toUpperCase().replace(" ", ""));
    params.put(GEO_REGION, StringUtils.isEmpty(geo.getStateCode()) ? "" : geo.getStateCode().toUpperCase());
    params.put(GEO_COUNTRY, StringUtils.isEmpty(geo.getCountryCode()) ? "" : geo.getCountryCode().toUpperCase());

    return params;
  }
}
