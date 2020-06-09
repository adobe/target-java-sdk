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
package com.adobe.target.edge.client.local.collator;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.Geo;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.HashMap;
import java.util.Map;

public class GeoParamsCollator implements ParamsCollator {

    private static final String GEO_LATITUDE = "latitude";
    private static final String GEO_LONGITUDE = "longitude";
    private static final String GEO_CITY = "city";
    private static final String GEO_REGION = "region";
    private static final String GEO_COUNTRY = "country";

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest,
                                             RequestDetails requestDetails) {
        Map<String, Object> params = new HashMap<>();
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context != null) {
            Geo geo = context.getGeo();
            updateGeoParams(params, geo);
        }
        return params;
    }

    public void updateGeoParams(Map<String, Object> params, Geo geo) {
        if (geo != null) {
            Float latitude = geo.getLatitude();
            if (latitude != null) {
                params.put(GEO_LATITUDE, latitude);
            }
            Float longitude = geo.getLongitude();
            if (longitude != null) {
                params.put(GEO_LONGITUDE, longitude);
            }
            String city = geo.getCity();
            if (city != null) {
                params.put(GEO_CITY, city.toUpperCase().replace(" ", ""));
            }
            String region = geo.getState();
            if (region != null) {
                params.put(GEO_REGION, region.toUpperCase());
            }
            String country = geo.getCountry();
            if (country != null) {
                params.put(GEO_COUNTRY, country.toUpperCase());
            }
        }
    }

}
