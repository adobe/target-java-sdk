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
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * This object will contain all the options for the selectors set for the current view, from the active activities,
 * in case the context and targeting conditions from the request have been matched.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class View {
    @JsonProperty("name")
    private String name;

    @JsonProperty("key")
    private String key;

    @JsonProperty("options")
    private List<Option> options = new ArrayList<>();

    @JsonProperty("metrics")
    private List<Metric> metrics = new ArrayList<>();

    @JsonProperty("analytics")
    private AnalyticsResponse analytics = null;

    @JsonProperty("state")
    private String state;

    @JsonProperty("trace")
    private Map<String, Object> trace = new HashMap<>();

    public View name(String name) {
        this.name = name;
        return this;
    }

    /**
     * View Name - Unique view name. If the activity has a metric with a view with this name it will be matched,
     * providing the Key matches as well or is null and view and metric targeting is matched.
     *
     * @return name
     **/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View key(String key) {
        this.key = key;
        return this;
    }

    /**
     * View Key - An optional encoded String identifier used in advanced scenarios, such as View fingerprinting. Same
     * matching conditions as for View Name.
     *
     * @return key
     **/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public View options(List<Option> options) {
        this.options = options;
        return this;
    }

    public View addOptionsItem(Option optionsItem) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(optionsItem);
        return this;
    }

    /**
     * The prefetched content (options) to be displayed for the current view.
     *
     * @return options
     **/

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public View metrics(List<Metric> metrics) {
        this.metrics = metrics;
        return this;
    }

    public View addMetricsItem(Metric metricsItem) {
        if (this.metrics == null) {
            this.metrics = new ArrayList<>();
        }
        this.metrics.add(metricsItem);
        return this;
    }

    /**
     * Click track metrics for the current view.
     *
     * @return metrics
     **/

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public View analytics(AnalyticsResponse analytics) {
        this.analytics = analytics;
        return this;
    }

    /**
     * Get analytics
     *
     * @return analytics
     **/

    public AnalyticsResponse getAnalytics() {
        return analytics;
    }

    public void setAnalytics(AnalyticsResponse analytics) {
        this.analytics = analytics;
    }

    public View state(String state) {
        this.state = state;
        return this;
    }

    /**
     * View state token that must be sent back with display notification for the view.
     *
     * @return state
     **/

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public View trace(Map<String, Object> trace) {
        this.trace = trace;
        return this;
    }

    public View putTraceItem(String key, Object traceItem) {
        if (this.trace == null) {
            this.trace = new HashMap<>();
        }
        this.trace.put(key, traceItem);
        return this;
    }

    /**
     * The object containing all trace data for the request, only present if the trace token was provided in the
     * request.
     *
     * @return trace
     **/

    public Map<String, Object> getTrace() {
        return trace;
    }

    public void setTrace(Map<String, Object> trace) {
        this.trace = trace;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        View view = (View) o;
        return Objects.equals(this.name, view.name) &&
                Objects.equals(this.key, view.key) &&
                Objects.equals(this.options, view.options) &&
                Objects.equals(this.metrics, view.metrics) &&
                Objects.equals(this.analytics, view.analytics) &&
                Objects.equals(this.state, view.state) &&
                Objects.equals(this.trace, view.trace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, options, metrics, analytics, state, trace);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class View {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    key: ").append(toIndentedString(key)).append("\n");
        sb.append("    options: ").append(toIndentedString(options)).append("\n");
        sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
        sb.append("    analytics: ").append(toIndentedString(analytics)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    trace: ").append(toIndentedString(trace)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

