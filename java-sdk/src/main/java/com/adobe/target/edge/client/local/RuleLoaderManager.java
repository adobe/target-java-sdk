package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;

import java.util.HashMap;
import java.util.Map;

public class RuleLoaderManager {

    private static final RuleLoaderManager sInstance = new RuleLoaderManager();

    private final Map<String, RuleLoader> loaderMap = new HashMap<>();

    public static RuleLoaderManager getInstance() {
        return sInstance;
    }

    public RuleLoader getLoader(ClientConfig clientConfig) {
        String serviceKey = clientConfig.getClient();
        RuleLoader loader = loaderMap.get(serviceKey);
        if (loader != null) {
            return loader;
        }
        synchronized (loaderMap) {
            loader = loaderMap.get(serviceKey);
            if (loader != null) {
                return loader;
            }
            loader = new DefaultRuleLoader();
            loaderMap.put(serviceKey, loader);
            return loader;
        }
    }

}
