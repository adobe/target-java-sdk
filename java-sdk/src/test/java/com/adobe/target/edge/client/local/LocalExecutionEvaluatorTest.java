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
import com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.local.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.local.LocalExecutionEvaluation;
import com.adobe.target.edge.client.service.VisitorProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalExecutionEvaluatorTest {

    private LocalExecutionEvaluator evaluator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        VisitorProvider.init("testOrgId");
        objectMapper = new JacksonObjectMapper().getMapper();
    }

    @Test
    public void testNullRequest() throws JsonProcessingException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(null);
        assertFalse(evaluation.isAllLocal());
        assertNull(evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testNoRuleSet() {
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(null);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder().build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        assertNull(evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testAllLocalNoRemoteMbox() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> localMboxes = new ArrayList<>();
        localMboxes.add("test");
        localMboxes.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localMboxes"), localMboxes);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteMboxes"), new ArrayList<String>());
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .execute(new ExecuteRequest()
                        .addMboxesItem(new MboxRequest().name("test"))
                        .addMboxesItem(new MboxRequest().name("test2")))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertTrue(evaluation.isAllLocal());
        assertNull(evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testLocalAndRemoteMbox() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> mboxes = new ArrayList<>();
        mboxes.add("test");
        mboxes.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localMboxes"), mboxes);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteMboxes"), mboxes);
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .execute(new ExecuteRequest()
                        .addMboxesItem(new MboxRequest().name("test").index(0))
                        .addMboxesItem(new MboxRequest().name("test2").index(1)))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        List<String> remoteMboxes = evaluation.getRemoteMBoxes();
        remoteMboxes.sort(Comparator.naturalOrder());
        assertEquals(mboxes, remoteMboxes);
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testUnknownMbox() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> mboxes = new ArrayList<>();
        mboxes.add("test");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localMboxes"), mboxes);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteMboxes"), new ArrayList<>());
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .execute(new ExecuteRequest()
                        .addMboxesItem(new MboxRequest().name("test"))
                        .addMboxesItem(new MboxRequest().name("test2")))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        List<String> remoteMboxes = new ArrayList<>();
        remoteMboxes.add("test2");
        assertEquals(remoteMboxes, evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testAllLocalNoRemoteView() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> localViews = new ArrayList<>();
        localViews.add("test");
        localViews.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localViews"), localViews);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteViews"), new ArrayList<String>());
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .prefetch(new PrefetchRequest()
                        .addViewsItem(new ViewRequest().name("test"))
                        .addViewsItem(new ViewRequest().name("test2")))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertTrue(evaluation.isAllLocal());
        assertNull(evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testAllLocalNoRemoteAllViews() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> localViews = new ArrayList<>();
        localViews.add("test");
        localViews.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localViews"), localViews);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteViews"), new ArrayList<String>());
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .prefetch(new PrefetchRequest()
                        .addViewsItem(new ViewRequest()))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertTrue(evaluation.isAllLocal());
        assertNull(evaluation.getRemoteMBoxes());
        assertNull(evaluation.getRemoteViews());
    }

    @Test
    public void testLocalAndRemoteView() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> views = new ArrayList<>();
        views.add("test");
        views.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localViews"), views);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteViews"), views);
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .prefetch(new PrefetchRequest()
                        .addViewsItem(new ViewRequest().name("test"))
                        .addViewsItem(new ViewRequest().name("test2")))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        List<String> remoteViews = evaluation.getRemoteViews();
        remoteViews.sort(Comparator.naturalOrder());
        assertEquals(views, remoteViews);
        assertNull(evaluation.getRemoteMBoxes());
    }

    @Test
    public void testRemoteAllViews() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> views = new ArrayList<>();
        views.add("test");
        views.add("test2");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localViews"), new ArrayList<>());
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteViews"), views);
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .prefetch(new PrefetchRequest()
                        .addViewsItem(new ViewRequest()))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        List<String> remoteViews = evaluation.getRemoteViews();
        remoteViews.sort(Comparator.naturalOrder());
        assertEquals(views, remoteViews);
        assertNull(evaluation.getRemoteMBoxes());
    }

    @Test
    public void testUnknownView() throws JsonProcessingException, NoSuchFieldException {
        LocalDecisioningRuleSet ruleSet = new LocalDecisioningRuleSet();
        List<String> views = new ArrayList<>();
        views.add("test");
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("localViews"), views);
        FieldSetter.setField(ruleSet, ruleSet.getClass()
                .getDeclaredField("remoteViews"), new ArrayList<>());
        String serializedRuleSet = objectMapper.writeValueAsString(ruleSet);
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoader(serializedRuleSet);
        evaluator = new LocalExecutionEvaluator(testRuleLoader);
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .prefetch(new PrefetchRequest()
                        .addViewsItem(new ViewRequest().name("test"))
                        .addViewsItem(new ViewRequest().name("test2")))
                .build();
        LocalExecutionEvaluation evaluation = evaluator.evaluateLocalExecution(request);
        assertFalse(evaluation.isAllLocal());
        List<String> remoteViews = new ArrayList<>();
        remoteViews.add("test2");
        assertEquals(remoteViews, evaluation.getRemoteViews());
        assertNull(evaluation.getRemoteMBoxes());
    }
}
