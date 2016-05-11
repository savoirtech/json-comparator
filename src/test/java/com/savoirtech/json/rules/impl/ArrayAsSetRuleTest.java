/*
 *  Copyright (c) 2016 Savoir Technologies
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.savoirtech.json.rules.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.RuleChildComparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by art on 5/10/16.
 */
public class ArrayAsSetRuleTest {

  private ArrayAsSetRule rule;

  private RuleChildComparator mockChildComparator;
  private JsonComparatorRuleSpecification mockRuleSpecification;

  private JsonComparatorResult passResult;
  private JsonComparatorResult failResult;
  private JsonArray templateArray;
  private JsonArray actualArray;


  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.rule = new ArrayAsSetRule();

    this.mockChildComparator = Mockito.mock(RuleChildComparator.class);
    this.mockRuleSpecification = Mockito.mock(JsonComparatorRuleSpecification.class);

    this.passResult = new JsonComparatorResult(true, true, null);
    this.failResult = new JsonComparatorResult(true, false, "x-fail-message-x");
    this.templateArray = new JsonArray();
    this.actualArray = new JsonArray();

    Mockito.when(this.mockChildComparator
                     .compare(Mockito.anyString(), Mockito.any(JsonElement.class),
                              Mockito.any(JsonElement.class))).thenReturn(this.failResult);
  }

  /**
   * Verify operation of set rule comparison on a match.
   */
  @Test
  public void testCompareMatches() throws Exception {
    //
    // Setup test data and interactions
    //
    this.populateJsonArray(this.templateArray, 1, 2, 3, 4);
    this.populateJsonArray(this.actualArray, 4, 2, 3, 1);

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", this.templateArray, this.actualArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

  /**
   * Verify operation of set rule comparison on a mismatch.
   */
  @Test
  public void testCompareMismatch() throws Exception {
    //
    // Setup test data and interactions
    //
    this.populateJsonArray(this.templateArray, 1, 2, 3, 4);
    this.populateJsonArray(this.actualArray, 4, 2, 3, 0);

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", this.templateArray, this.actualArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("set comparison: failed to find match for path x-path-x[3]",
                 result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

  /**
   * Verify operation of set rule comparison on a mismatch of set size.
   */
  @Test
  public void testCompareMismatchSetSize() throws Exception {
    //
    // Setup test data and interactions
    //
    this.populateJsonArray(this.templateArray, 1, 2, 3, 4);
    this.populateJsonArray(this.actualArray, 4, 2, 3);

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", this.templateArray, this.actualArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals(
        "set comparison: sizes do not match at path x-path-x: expectedCount=4; actualCount=3",
        result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

  /**
   * Verify operation of set rule comparison on a mismatch of set elements when the actual array
   * duplicates values in the expected set.
   */
  @Test
  public void testCompareMismatchDuplicates() throws Exception {
    //
    // Setup test data and interactions
    //
    this.populateJsonArray(this.templateArray, 10, 20, 30, 40, 50, 60);
    this.populateJsonArray(this.actualArray, 10, 20, 30, 10, 20, 30);

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", this.templateArray, this.actualArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals(
        "set comparison: failed to find match for path x-path-x[3]",
        result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

  @Test
  public void testExpectedArrayVsActualNonArray() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement nonArray = new JsonPrimitive("x-string-x");

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", this.templateArray, nonArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("set rule on non-array element at path x-path-x", result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

  @Test
  public void testExpectedNonArrayVsActualArray() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement nonArray = new JsonPrimitive("x-string-x");

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule
            .compare("x-path-x", nonArray, this.actualArray, this.mockRuleSpecification,
                     this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("set rule on non-array template element at path x-path-x",
                 result.getErrorMessage());
    assertTrue(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockRuleSpecification);
  }

//========================================
// Internal Methods
//----------------------------------------

  private void populateJsonArray(JsonArray array, int... values) {
    for (int oneValue : values) {
      JsonPrimitive jsonValue = new JsonPrimitive(oneValue);
      array.add(jsonValue);

      Mockito.when(this.mockChildComparator
                       .compare(Mockito.anyString(), Mockito.eq(jsonValue), Mockito.eq(jsonValue)))
          .thenReturn(this.passResult);
    }
  }
}
