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
package com.adobe.target.edge.client.ondevice.collator;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageParamsCollator implements ParamsCollator {

  private static final Logger logger = LoggerFactory.getLogger(PageParamsCollator.class);

  protected static final String PAGE_URL = "url";
  protected static final String PAGE_URL_LOWER = "url_lc";
  protected static final String PAGE_DOMAIN = "domain";
  protected static final String PAGE_DOMAIN_LOWER = "domain_lc";
  protected static final String PAGE_SUBDOMAIN = "subdomain";
  protected static final String PAGE_SUBDOMAIN_LOWER = "subdomain_lc";
  protected static final String PAGE_TOP_LEVEL_DOMAIN = "topLevelDomain";
  protected static final String PAGE_TOP_LEVEL_DOMAIN_LOWER = "topLevelDomain_lc";
  protected static final String PAGE_PATH = "path";
  protected static final String PAGE_PATH_LOWER = "path_lc";
  protected static final String PAGE_QUERY = "query";
  protected static final String PAGE_QUERY_LOWER = "query_lc";
  protected static final String PAGE_FRAGMENT = "fragment";
  protected static final String PAGE_FRAGMENT_LOWER = "fragment_lc";

  private boolean referring = false;

  public PageParamsCollator() {}

  public PageParamsCollator(boolean referring) {
    this.referring = referring;
  }

  public Map<String, Object> collateParams(
      TargetDeliveryRequest deliveryRequest, RequestDetails requestDetails) {
    Map<String, Object> page = new HashMap<>();
    Context context = deliveryRequest.getDeliveryRequest().getContext();
    if (context == null) {
      return page;
    }
    Address address =
        requestDetails.getAddress() != null ? requestDetails.getAddress() : context.getAddress();
    if (address == null) {
      return page;
    }
    try {
      String urlToUse = this.referring ? address.getReferringUrl() : address.getUrl();
      if (StringUtils.isEmpty(urlToUse)) {
        return page;
      }
      URL url = new URL(urlToUse);
      page.put(PAGE_URL, url.toString());
      page.put(PAGE_URL_LOWER, url.toString().toLowerCase());
      String host = url.getHost();
      page.put(PAGE_DOMAIN, strOrBlank(host));
      page.put(PAGE_DOMAIN_LOWER, strLowerOrBlank(host));
      String subdomain = extractSubDomain(host);
      page.put(PAGE_SUBDOMAIN, strOrBlank(subdomain));
      page.put(PAGE_SUBDOMAIN_LOWER, strLowerOrBlank(subdomain));
      String topLevelDomain = extractTopLevel(host);
      page.put(PAGE_TOP_LEVEL_DOMAIN, strOrBlank(topLevelDomain));
      page.put(PAGE_TOP_LEVEL_DOMAIN_LOWER, strLowerOrBlank(topLevelDomain));
      String path = url.getPath();
      page.put(PAGE_PATH, strOrBlank(path));
      page.put(PAGE_PATH_LOWER, strLowerOrBlank(path));
      String query = url.getQuery();
      page.put(PAGE_QUERY, strOrBlank(query));
      page.put(PAGE_QUERY_LOWER, strLowerOrBlank(query));
      String fragment = url.getRef();
      page.put(PAGE_FRAGMENT, strOrBlank(fragment));
      page.put(PAGE_FRAGMENT_LOWER, strLowerOrBlank(fragment));
    } catch (MalformedURLException ex) {
      logger.warn("URL in context address malformed, skipping", ex);
    }
    return page;
  }

  private String extractTopLevel(String host) {
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
    String[] parts = host.split("\\.");
    if (parts.length < 3) {
      return "";
    }
    return parts[0];
  }

  private String strOrBlank(String str) {
    return str != null ? str : "";
  }

  private String strLowerOrBlank(String str) {
    return str != null ? str.toLowerCase() : "";
  }
}
