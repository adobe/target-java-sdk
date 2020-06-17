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
package com.adobe.target.edge.client.local.client.geo;

import com.adobe.target.delivery.v1.model.Geo;
import com.adobe.target.edge.client.ClientConfig;
import kong.unirest.GetRequest;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DefaultGeoClientTest {

    @Test
    void testDefaultGeoClient() {
        String domain = "test.com";
        ClientConfig clientConfig = ClientConfig.builder()
                .client("testclient")
                .organizationId("testOrgId")
                .localConfigHostname(domain)
                .build();
        DefaultGeoClient geoClient = spy(DefaultGeoClient.class);
        geoClient.start(clientConfig);

        String ip = "127.0.0.1";
        String url = "https://" + domain + DefaultGeoClient.GEO_PATH;
        String city = "SAN FRANCISCO";
        String regionCode = "CA";
        String countryCode = "US";
        float latitude = 37.74f;
        float longitude = -122.24f;

        GetRequest request = mock(GetRequest.class);
        @SuppressWarnings("rawtypes")
        HttpResponse response = mock(HttpResponse.class);
        Headers headers = new Headers();
        headers.add(DefaultGeoClient.GEO_HEADER_CITY, city);
        headers.add(DefaultGeoClient.GEO_HEADER_REGION, regionCode);
        headers.add(DefaultGeoClient.GEO_HEADER_COUNTRY, countryCode);
        headers.add(DefaultGeoClient.GEO_HEADER_LATITUDE, String.valueOf(latitude));
        headers.add(DefaultGeoClient.GEO_HEADER_LONGITUDE, String.valueOf(longitude));

        Mockito.doReturn(response)
                .when(request).asEmpty();
        Mockito.doReturn(headers)
                .when(response).getHeaders();
        Mockito.doReturn(request)
                .when(geoClient).geoRequest(any(), any(), any());

        Geo geoResponse = geoClient.lookupGeo(ip);

        verify(geoClient).geoRequest(eq(url), eq(DefaultGeoClient.GEO_IP_HEADER), eq(ip));
        assertEquals(city, geoResponse.getCity());
        assertEquals(regionCode, geoResponse.getStateCode());
        assertEquals(countryCode, geoResponse.getCountryCode());
        assertEquals(latitude, geoResponse.getLatitude(), 0.01);
        assertEquals(longitude, geoResponse.getLongitude(), 0.01);
    }
}
