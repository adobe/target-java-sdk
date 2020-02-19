package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class UserParamsCollator implements ParamsCollator {

    private static final String UNKNOWN = "unknown";
    private static final Map<String, Predicate<String>> BROWSER_TYPE_MATCHER =
            Collections.unmodifiableMap(new LinkedHashMap<String, Predicate<String>>() {{
                put("chrome", browser -> (browser.contains("Chrome") || browser.contains("CriOS")) &&
                        !browser.contains("OPR") && !browser.contains("Edge/"));
                put("firefox", browser -> browser.contains("Firefox"));
                put("ie", browser -> browser.contains("MSIE") || browser.contains("Trident"));
                put("opera", browser -> browser.contains("Opera") || browser.contains("OPR"));
                put("ipad", browser -> browser.contains("iPad"));
                put("iphone", browser -> browser.contains("iPhone"));
                put("safari", browser -> browser.contains("Safari") && !browser.contains("Chrome") && !browser.contains("OPR") &&
                        !browser.contains("CriOS"));
                put("edge", browser -> browser.contains("Edge/"));
            }});
    private static final Map<String, String> BROWSER_PLATFORMS_MAPPING =
            Collections.unmodifiableMap(new LinkedHashMap<String, String>() {{
                put("Windows", "windows");
                put( "Macintosh", "mac");
                put("Mac OS", "mac");
                put("Linux", "linux");
            }});

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest) {
        Map<String, Object> user = new HashMap<>();
        String userAgent = extractUserAgent(deliveryRequest);
        user.put("browserType", parseBrowserType(userAgent));
        user.put("platform", parseBrowserPlatform(userAgent));
        return user;
    }

    private String parseBrowserType(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return UNKNOWN;
        }

        return BROWSER_TYPE_MATCHER.entrySet().stream()
                .filter(browserType -> browserType.getValue().test(userAgent))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(UNKNOWN);
    }

    private static String parseBrowserPlatform(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return UNKNOWN;
        }

        return BROWSER_PLATFORMS_MAPPING.entrySet().stream()
                .filter(it -> userAgent.contains(it.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(UNKNOWN);
    }

    private String extractUserAgent(TargetDeliveryRequest deliveryRequest) {
        Context context = deliveryRequest.getDeliveryRequest().getContext();
        if (context == null) {
            return null;
        }
        String userAgent = context.getUserAgent();
        if (StringUtils.isEmpty(userAgent)) {
            return null;
        }
        return userAgent;
    }

    private List<Pattern> compilePatterns(String... patterns) {
        return Arrays.stream(patterns)
                .map(definition -> Pattern.compile(definition, Pattern.CASE_INSENSITIVE))
                .collect(toList());
    }
}
