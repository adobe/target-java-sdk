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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AllocationUtilsTest {

  @Test
  void testAllocationCalc() {
    assertEquals(
        AllocationUtils.calculateAllocation("someClientId", "123456", "ecid123", "salty"), 29.06);

    assertEquals(
        AllocationUtils.calculateAllocation("someClientId", "123456", "tntId123", "salty"), 21.94);

    assertEquals(
        AllocationUtils.calculateAllocation("someClientId", "123456", "tntId123.28_0", "salty"),
        21.94);

    assertEquals(
        AllocationUtils.calculateAllocation("someClientId", "123456", "thirtPartyId123", "salty"),
        73.15);
  }

  @Test
  public void testAllocationInRange() {
    for (long i = 0; i < 10000; i++) {
      double allocation =
          AllocationUtils.calculateAllocation("myTntId99152", "123456", String.valueOf(i), "salty");
      assertTrue(allocation >= 0);
      assertTrue(allocation <= 100);
    }
  }
}
