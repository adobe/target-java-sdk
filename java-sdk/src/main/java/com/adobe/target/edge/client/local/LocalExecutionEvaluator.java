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

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.ViewRequest;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.LocalExecutionEvaluation;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalExecutionEvaluator {

    private final RuleLoader ruleLoader;

    public LocalExecutionEvaluator(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    /**
     * Use to determine if the given request can be fully executed locally or not and why.
     *
     * @param deliveryRequest request to examine
     * @return LocalExecutionResult
     */
    public LocalExecutionEvaluation evaluateLocalExecution(TargetDeliveryRequest deliveryRequest) {
        if (deliveryRequest == null) {
            return new LocalExecutionEvaluation(false,
                    "Given request cannot be null", null, null);
        }

        LocalDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
        if (ruleSet == null) {
            return new LocalExecutionEvaluation(false,
                    "Local-decisioning rule set not yet available", null, null);
        }

        List<String> requestMboxNames = allMboxNames(deliveryRequest, ruleSet);
        Set<String> localMboxSet = new HashSet<>(ruleSet.getLocalMboxes());
        Set<String> remoteMboxes = new HashSet<>();
        for (String mboxName : requestMboxNames) {
            if (!localMboxSet.contains(mboxName)) {
                remoteMboxes.add(mboxName);
            }
        }

        List<String> requestViews = allViewNames(deliveryRequest);
        Set<String> localViewSet = new HashSet<>(ruleSet.getLocalViews());
        Set<String> remoteViews = new HashSet<>();
        if (allViews(requestViews)) {
            remoteViews.addAll(ruleSet.getRemoteViews());
        }
        else {
            for (String viewName : requestViews) {
                if (!localViewSet.contains(viewName)) {
                    remoteViews.add(viewName);
                }
            }
        }

        if (!remoteMboxes.isEmpty() || !remoteViews.isEmpty()) {
            StringBuilder reason = new StringBuilder("remote activities in: ");
            boolean haveRemoteMboxes = !remoteMboxes.isEmpty();
            if (haveRemoteMboxes) {
                reason.append(String.format("mboxes %s", remoteMboxes));
            }
            if (!remoteViews.isEmpty()) {
                if (haveRemoteMboxes) {
                    reason.append(", ");
                }
                reason.append(String.format("views %s", remoteViews));
            }
            return new LocalExecutionEvaluation(false,
                    reason.toString(),
                    new ArrayList<>(remoteMboxes),
                    new ArrayList<>(remoteViews));
        }
        else {
            return new LocalExecutionEvaluation(true, null, null, null);
        }

    }

    private List<String> allMboxNames(TargetDeliveryRequest request, LocalDecisioningRuleSet ruleSet) {
        if (request == null || ruleSet == null) {
            return Collections.emptyList();
        }
        List<String> mboxNames = new ArrayList<>();
        String globalMbox = ruleSet.getGlobalMbox();
        PrefetchRequest prefetch = request.getDeliveryRequest().getPrefetch();
        if (prefetch != null) {
            if (prefetch.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(prefetch.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        ExecuteRequest execute = request.getDeliveryRequest().getExecute();
        if (execute != null) {
            if (execute.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(execute.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        return mboxNames;
    }

    private List<String> allViewNames(TargetDeliveryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        List<String> viewNames = new ArrayList<>();
        PrefetchRequest prefetch = request.getDeliveryRequest().getPrefetch();
        if (prefetch != null) {
            List<ViewRequest> views = prefetch.getViews();
            if (views != null) {
                for (ViewRequest viewRequest : views) {
                    viewNames.add(viewRequest.getName());
                }
            }
        }
        return viewNames;
    }

    private boolean allViews(List<String> viewNames) {
        return viewNames.size() == 1 && viewNames.get(0) == null;
    }

}
