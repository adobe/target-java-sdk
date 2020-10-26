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
package com.adobe.target.edge.client.http;

import java.util.function.Consumer;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingMetricConsumer implements Consumer<TargetMetricContext> {
  private static final Logger logger = LoggerFactory.getLogger(TargetMetrics.class);

  @Override
  public void accept(TargetMetricContext targetMetricContext) {

    if (targetMetricContext.getStatus() == HttpStatus.SC_OK) {
      logger.debug(
          "Target Request Status: {} Time: {}ms Url: {} ",
          targetMetricContext.getUrl(),
          targetMetricContext.getStatus(),
          targetMetricContext.getExecutionTime());
      return;
    }

    logger.error(
        "Target Request Status: Status: {} Message: {} Time: {}ms Url: {} ",
        targetMetricContext.getUrl(),
        targetMetricContext.getStatus(),
        targetMetricContext.getStatusMessage(),
        targetMetricContext.getExecutionTime());
  }
}
