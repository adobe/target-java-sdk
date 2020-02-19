package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.Address;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PageParamsCollator implements ParamsCollator {

    private static final Logger logger = LoggerFactory.getLogger(PageParamsCollator.class);

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> page = new HashMap<>();
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context == null) {
            return page;
        }
        Address address = context.getAddress();
        if (address == null) {
            return page;
        }
        try {
            URL url = new URL(address.getUrl());
            page.put("url", url.toString());
            page.put("url_lc", url.toString().toLowerCase());
            String host = url.getHost();
            page.put("domain", host != null ? host : "");
            page.put("domain_lc", host != null ? host.toLowerCase() : "");
            String subdomain = extractSubDomain(host);
            page.put("subdomain", subdomain != null ? subdomain : "");
            page.put("subdomain_lc", subdomain != null ? subdomain.toLowerCase() : "");
            String topLevelDomain = extractTopLevel(host);
            page.put("topLevelDomain", topLevelDomain != null ? topLevelDomain : "");
            page.put("topLevelDomain_lc", topLevelDomain != null ? topLevelDomain.toLowerCase() : "");
            String path = url.getPath();
            page.put("path", path != null ? path : "");
            page.put("path_lc", path != null ? path.toLowerCase() : "");
            String query = url.getQuery();
            page.put("query", query != null ? query : "");
            page.put("query_lc", query != null ? query.toLowerCase() : "");
            String fragment = url.getRef();
            page.put("fragment", fragment != null ? fragment : "");
            page.put("fragment_lc", fragment != null ? fragment.toLowerCase() : "");
        }
        catch (MalformedURLException ex) {
            logger.warn("URL in context address malformed, skipping", ex);
        }
        return page;
    }

    private String extractTopLevel(String host) {
        // TODO: implement properly
        return host;
    }

    private String extractSubDomain(String host) {
        // TODO: implement properly
        return host;
    }
}
