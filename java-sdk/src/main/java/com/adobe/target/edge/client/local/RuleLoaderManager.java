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
