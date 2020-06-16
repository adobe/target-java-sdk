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
package com.adobe.target.edge.client.model.local;

import java.util.List;

public class LocalExecutionEvaluation {

    private boolean allLocal;
    private String reason;
    private List<String> remoteMBoxes;
    private List<String> remoteViews;

    public LocalExecutionEvaluation(boolean allLocal, String reason, List<String> remoteMBoxes, List<String> remoteViews) {
        this.allLocal = allLocal;
        this.reason = reason;
        this.remoteMBoxes = remoteMBoxes;
        this.remoteViews = remoteViews;
    }

    public boolean isAllLocal() {
        return allLocal;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getRemoteMBoxes() {
        return remoteMBoxes;
    }

    public List<String> getRemoteViews() {
        return remoteViews;
    }

}
