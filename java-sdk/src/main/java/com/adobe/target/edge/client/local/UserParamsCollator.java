package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class UserParamsCollator implements ParamsCollator {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserParamsCollator.class);

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
    private static final Map<String, List<Pattern>> BROWSER_VERSION_PATTERNS =
            Collections.unmodifiableMap(new HashMap<String, List<Pattern>>() {{
                put("chrome", compilePatterns("chrome/(\\d+)", "crios/(\\d+)"));
                put("firefox", compilePatterns("firefox/(\\d+)"));
                put("ie", compilePatterns("msie\\s(\\d+)", "rv:(\\d+)"));
                put("opera", compilePatterns("version/(\\d+)", "opera/(\\d+)", "opera\\s*(\\d+)", "OPR/(\\d+)"));
                put("ipad", compilePatterns("version/(\\d+)"));
                put("iphone", compilePatterns("version/(\\d+)"));
                put("safari", compilePatterns("version/(\\d+)"));
                put("edge", compilePatterns("edge/(\\d+)"));
            }});
    private static final int BROWSER_VERSION_PATTERN_GROUP_INDEX = 1;
    private static final String COMPATIBILITY_TOKEN_START = "(compatible;";
    private static final String COMPATIBILITY_TOKEN_END = ")";
    private static final int COMPATIBILITY_TOKEN_START_LENGTH = COMPATIBILITY_TOKEN_START.length();
    private static final int COMPATIBILITY_TOKEN_END_LENGTH = COMPATIBILITY_TOKEN_END.length();

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest,
                                             RequestDetails requestDetails, Map<String, Object> meta) {
        Map<String, Object> user = new HashMap<>();
        String userAgent = extractUserAgent(deliveryRequest);
        user.put("browserType", parseBrowserType(userAgent));
        user.put("platform", parseBrowserPlatform(userAgent));
        user.put("version", parseBrowserVersion(userAgent));
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

    private String parseBrowserVersion(String userAgent) {
        List<Pattern> patterns = BROWSER_VERSION_PATTERNS.get(parseBrowserType(userAgent));
        if (patterns == null) {
            return UNKNOWN;
        }

        return getMainAndCompatibilitySections(userAgent).stream()
                .map(section -> findBrowserVersion(section, patterns))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
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

    private static List<Pattern> compilePatterns(String... patterns) {
        return Arrays.stream(patterns)
                .map(definition -> Pattern.compile(definition, Pattern.CASE_INSENSITIVE))
                .collect(toList());
    }

    private Optional<String> findBrowserVersion(String userAgent, List<Pattern> patterns) {
        if (StringUtils.isEmpty(userAgent)) {
            return Optional.empty();
        }
        return patterns.stream()
                .map(pattern -> {
                    Matcher matcher = pattern.matcher(userAgent);
                    return matcher.find() ? matcher.group(BROWSER_VERSION_PATTERN_GROUP_INDEX) : null;
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    private List<String> getMainAndCompatibilitySections(String userAgent) {
        int compatibilitySectionStart = userAgent.indexOf(COMPATIBILITY_TOKEN_START);
        if (compatibilitySectionStart == -1) {
            return Collections.singletonList(userAgent);
        }
        int compatibilitySectionEnd = userAgent.indexOf(COMPATIBILITY_TOKEN_END, compatibilitySectionStart);
        if (compatibilitySectionEnd == -1) {
            return Collections.singletonList(userAgent);
        }
        return parseCompatibilitySections(userAgent);
    }

    private List<String> parseCompatibilitySections(String userAgent) {
        ParserState state = new ParserState(userAgent);
        while (state.hasUnprocessedChars()) {
            if (state.atToken(COMPATIBILITY_TOKEN_START)) {
                state.addToStore();
                state.nextLevel();
                state.advance(COMPATIBILITY_TOKEN_START_LENGTH);
            } else if (state.atToken(COMPATIBILITY_TOKEN_END)) {
                state.addToStore();
                state.prevLevel();
                state.advance(COMPATIBILITY_TOKEN_END_LENGTH);
            } else {
                state.next();
            }
        }
        state.addToStore();
        return state.build();
    }

    private static class ParserState {

        private static final int MAX_LEVEL = 5;
        private static final int MIN_LEVEL = 0;

        private final String data;
        private final int dataLength;
        private final List<StringBuilder> levelStore = new ArrayList<>();
        private int levelIndex = 0;
        private int currentCharIndex = 0;
        private int regionStartIndex = 0;

        private ParserState(String data) {
            this.data = data;
            this.dataLength = data.length();
        }

        void advance(int count) {
            regionStartIndex = currentCharIndex + count;
            currentCharIndex = regionStartIndex;
        }

        void next() {
            currentCharIndex++;
        }

        boolean hasUnprocessedChars() {
            return currentCharIndex < dataLength;
        }

        boolean atToken(String token) {
            return data.startsWith(token, currentCharIndex);
        }

        void nextLevel() {
            levelIndex++;
            if (levelIndex > MAX_LEVEL) {
                levelIndex = MAX_LEVEL;
            }
        }

        void prevLevel() {
            levelIndex--;
            if (levelIndex < MIN_LEVEL) {
                levelIndex = MIN_LEVEL;
            }
        }

        void addToStore() {
            ensureStoreHasSpace();
            String currentSubstring = data.substring(regionStartIndex, currentCharIndex);
            levelStore.get(levelIndex).append(currentSubstring);
        }

        private void ensureStoreHasSpace() {
            while (levelStore.size() <= levelIndex) {
                levelStore.add(new StringBuilder());
            }
        }

        List<String> build() {
            return levelStore.stream()
                    .map(StringBuilder::toString)
                    .collect(toList());
        }

    }
}
