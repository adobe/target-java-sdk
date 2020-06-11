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
import com.adobe.target.edge.client.ClientProxyConfig;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

public class DefaultGeoClient implements GeoClient {

    public static final String GEO_PATH = "/v1/geo";

    public static final String GEO_HEADER_CITY = "x-geo-city";
    public static final String GEO_HEADER_REGION = "x-geo-region-code";
    public static final String GEO_HEADER_COUNTRY = "x-geo-country-code";
    public static final String GEO_HEADER_LATITUDE = "x-geo-latitude";
    public static final String GEO_HEADER_LONGITUDE = "x-geo-longitude";

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private final String geoUrl;

    public DefaultGeoClient(ClientConfig clientConfig) {

        this.geoUrl = "https://" + clientConfig.getLocalConfigHostname() + GEO_PATH;

        unirestInstance.config()
                .socketTimeout(clientConfig.getSocketTimeout())
                .connectTimeout(clientConfig.getConnectTimeout())
                .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
                .automaticRetries(clientConfig.isEnabledRetries())
                .enableCookieManagement(false);

        if (clientConfig.isProxyEnabled()) {
            ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
            if(proxyConfig.isAuthProxy()) {
                unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort(), proxyConfig.getUsername(), proxyConfig.getPassword());
            } else {
                unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort());
            }
        }

    }

    @Override
    @SuppressWarnings("rawtypes")
    public Geo lookupGeo(String ip) {
        HttpResponse response = unirestInstance
                .get(this.geoUrl)
                .header("X-Forwarded-For", ip)
                .asEmpty();
        return headersToGeo(response.getHeaders());
    }

    @Override
    public void close() throws Exception {
        unirestInstance.shutDown();
    }

    private Geo headersToGeo(Headers headers) {
        Geo geo = new Geo();
        geo.setCity(headers.getFirst(GEO_HEADER_CITY));
        geo.setStateCode(headers.getFirst(GEO_HEADER_REGION));
        geo.setCountryCode(headers.getFirst(GEO_HEADER_COUNTRY));
        geo.setLatitude(parseFloat(headers.getFirst(GEO_HEADER_LATITUDE)));
        geo.setLongitude(parseFloat(headers.getFirst(GEO_HEADER_LONGITUDE)));
        return geo;
    }

    private Float parseFloat(String floatStr) {
        if (floatStr == null) {
            return null;
        }
        try {
            return Float.parseFloat(floatStr);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
