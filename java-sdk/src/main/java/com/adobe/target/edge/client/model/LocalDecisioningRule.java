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
package com.adobe.target.edge.client.model;

import java.util.Map;

public class LocalDecisioningRule {

    private Object condition;
    private Map<String, Object> consequence;
    private Map<String, Object> meta;

    public LocalDecisioningRule() {
    }

    public Object getCondition() {
        return condition;
    }

    public void setCondition(Object condition) {
        this.condition = condition;
    }

    public Map<String, Object> getConsequence() {
        return consequence;
    }

    public void setConsequence(Map<String, Object> consequence) {
        this.consequence = consequence;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "LocalDecisioningRule{" +
                "condition=" + condition +
                ", consequence=" + consequence +
                ", meta=" + meta +
                '}';
    }
}
