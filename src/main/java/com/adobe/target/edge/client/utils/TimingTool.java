/*
 * Copyright 2021 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.utils;

import java.util.HashMap;
import java.util.Map;

public class TimingTool {

  private Map<String, Long> startTimes = new HashMap<>();
  private Map<String, Double> timings = new HashMap<>();

  public String timeStart(String id) {
    if (!startTimes.containsKey(id)) {
      startTimes.put(id, System.nanoTime());
    }
    return id;
  }

  public double timeEnd(String id) {
    if (!startTimes.containsKey(id)) {
      return -1;
    }
    double timing = ((System.nanoTime() - startTimes.get(id)) / 1000000D);
    timings.put(id, timing);
    return timing;
  }

  public Map<String, Double> getTimings() {
    return timings;
  }

  public double getTiming(String id) {
    return timings.get(id);
  }

  public void reset() {
    startTimes = new HashMap<>();
    timings = new HashMap<>();
  }
}
