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
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.Option;
import com.adobe.target.delivery.v1.model.OptionType;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CampaignMacroReplacer {
  private static final String OPTIONS = "options";
  private static final String CONTENT = "content";

  private static final String MACRO_PATTERN_REGEX = "\\$\\{([a-zA-Z0-9_.]*?)\\}";

  private final Map<String, String> MACRO_NAME_REPLACEMENTS =
      new HashMap<String, String>() {
        {
          put("campaign", "activity");
          put("recipe", "experience");
        }
      };

  private final List<String> MACRO_NAME_REMOVALS = Arrays.asList("mbox");

  private final Map<String, Object> consequence;
  private final OnDeviceDecisioningRule rule;
  private final HashMap<String, Object> requestDetails;
  private final ObjectMapper mapper;
  private final Map<String, String> requestParameters;

  public CampaignMacroReplacer(
      OnDeviceDecisioningRule rule,
      Map<String, Object> consequence,
      RequestDetails details,
      ObjectMapper mapper) {

    this.rule = rule;
    this.consequence = consequence;

    this.requestDetails = new HashMap<>();

    this.requestParameters = details.getParameters();

    if (details instanceof MboxRequest) {
      this.requestDetails.put("name", ((MboxRequest) details).getName());
      this.requestDetails.put("index", ((MboxRequest) details).getIndex());
    }
    this.mapper = mapper;
  }

  public List<Option> getOptions() {
    Object options = this.consequence.get(OPTIONS);

    List<Option> optionsList =
        this.mapper.convertValue(options, new TypeReference<List<Option>>() {});

    for (Option option : optionsList) {
      if (option.getType() == OptionType.HTML && option.getContent() instanceof String) {
        option.setContent(this.addCampaignMacroValues((String) option.getContent()));
      }

      if (option.getType() == OptionType.ACTIONS && option.getContent() instanceof List) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> actions = (List<Map<String, Object>>) option.getContent();
        for (Map<String, Object> action : actions) {
          if (action.get(CONTENT) instanceof String) {
            action.put(CONTENT, this.addCampaignMacroValues((String) action.get(CONTENT)));
          }
        }
      }
    }

    return optionsList;
  }

  private String addCampaignMacroValues(String htmlContent) {
    Pattern pattern =
        Pattern.compile(MACRO_PATTERN_REGEX, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(htmlContent);
    StringBuffer buffer = new StringBuffer(htmlContent.length());

    while (matcher.find()) {
      String macroKey = sanitizedMacroKey(matcher.group(1));
      String macroValue = this.getMacroValue(macroKey, "${" + matcher.group(1) + "}");

      matcher.appendReplacement(buffer, Matcher.quoteReplacement(macroValue));
    }

    matcher.appendTail(buffer);

    return buffer.toString();
  }

  private String sanitizedMacroKey(String macroKey) {
    for (String legacyKey : MACRO_NAME_REPLACEMENTS.keySet()) {
      macroKey = macroKey.replaceAll(legacyKey, MACRO_NAME_REPLACEMENTS.get(legacyKey));
    }

    List<String> keySegments = Arrays.asList(macroKey.split("\\."));
    if (keySegments.size() > 2) {
      keySegments = keySegments.subList(keySegments.size() - 2, keySegments.size());
    }
    return keySegments.stream()
        .filter(part -> !MACRO_NAME_REMOVALS.contains(part))
        .collect(Collectors.joining("."));
  }

  private String getMacroValue(String key, String defaultValue) {
    // first look for the key in rule meta
    Map<String, Object> meta = this.rule.getMeta();
    if (meta.containsKey(key)) {
      return String.valueOf(meta.get(key));
    }

    // then look in the request detail
    if (this.requestDetails.containsKey(key)) {
      return String.valueOf(this.requestDetails.get(key));
    }

    // finally look for the key in request parameters
    if (this.requestParameters.containsKey(key)) {
      return requestParameters.get(key);
    }

    return defaultValue;
  }
}
