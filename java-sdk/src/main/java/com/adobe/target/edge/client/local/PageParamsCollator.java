package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PageParamsCollator implements ParamsCollator {

    private static final Logger logger = LoggerFactory.getLogger(PageParamsCollator.class);

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest, Map<String, Object> meta) {
        Map<String, Object> page = new HashMap<>();
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context == null) {
            return page;
        }
        Address address = context.getAddress();
        @SuppressWarnings("unchecked")
        List<String> mboxes = (List<String>)meta.get("mboxes");
        if (mboxes != null && mboxes.size() > 0) {
            Set<String> mboxSet = new HashSet<>(mboxes);
            PrefetchRequest prequest = deliveryRequest.getDeliveryRequest().getPrefetch();
            if (prequest != null) {
                List<MboxRequest> requests = prequest.getMboxes();
                if (requests != null) {
                    for (MboxRequest request : requests) {
                        if (mboxSet.contains(request.getName())) {
                            if (request.getAddress() != null) {
                                address = request.getAddress();
                            }
                        }
                    }
                }
            }
            ExecuteRequest erequest = deliveryRequest.getDeliveryRequest().getExecute();
            if (erequest != null) {
                List<MboxRequest> requests = erequest.getMboxes();
                if (requests != null) {
                    for (MboxRequest request : requests) {
                        if (mboxSet.contains(request.getName())) {
                            if (request.getAddress() != null) {
                                address = request.getAddress();
                            }
                        }
                    }
                }
            }
        }
        if (address == null) {
            return page;
        }
        try {
            URL url = new URL(address.getUrl());
            page.put("url", url.toString());
            page.put("url_lc", url.toString().toLowerCase());
            String host = url.getHost();
            page.put("domain", strOrBlank(host));
            page.put("domain_lc", strLowerOrBlank(host));
            String subdomain = extractSubDomain(host);
            page.put("subdomain", strOrBlank(subdomain));
            page.put("subdomain_lc", strLowerOrBlank(subdomain));
            String topLevelDomain = extractTopLevel(host);
            page.put("topLevelDomain", strOrBlank(topLevelDomain));
            page.put("topLevelDomain_lc", strLowerOrBlank(topLevelDomain));
            String path = url.getPath();
            page.put("path", strOrBlank(path));
            page.put("path_lc", strLowerOrBlank(path));
            String query = url.getQuery();
            page.put("query", strOrBlank(query));
            page.put("query_lc", strLowerOrBlank(query));
            String fragment = url.getRef();
            page.put("fragment", strOrBlank(fragment));
            page.put("fragment_lc", strLowerOrBlank(fragment));
        }
        catch (MalformedURLException ex) {
            logger.warn("URL in context address malformed, skipping", ex);
        }
        return page;
    }

    private String extractTopLevel(String host) {
        // TODO: implement properly
        if (host == null) {
            return "";
        }
        int idx = host.lastIndexOf('.');
        if (idx >= 0 && host.length() > 1) {
            return host.substring(idx + 1);
        }
        return host;
    }

    private String extractSubDomain(String host) {
        // TODO: implement properly
        if (host == null) {
            return "";
        }
        if (host.toLowerCase().startsWith("www.")) {
            host = host.substring(4);
        }
        return host;
    }

    private String strOrBlank(String str) {
        return str != null ? str : "";
    }

    private String strLowerOrBlank(String str) {
        return str != null ? str.toLowerCase() : "";
    }
}
