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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.RuleChildComparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the RegexMatchingRule.
 *
 * Created by art on 5/10/16.
 */
public class RegexMatchingRuleTest {

  private RegexMatchingRule rule;

  private RuleChildComparator mockChildComparator;

  private JsonComparatorRuleSpecification ruleSpecification;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.rule = new RegexMatchingRule();

    this.mockChildComparator = Mockito.mock(RuleChildComparator.class);

    this.ruleSpecification = new JsonComparatorRuleSpecification();
    this.ruleSpecification.setAction("match");
  }

  /**
   * Verify operation of the regex rule's comparison.
   */
  @Test
  public void testCompareMatches() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement templateEle = new JsonPrimitive("x-expected-x");
    JsonElement actualEle = new JsonPrimitive("x-actual-x");
    this.ruleSpecification.setPattern(".*actual.*");

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule.compare("x-path-x", templateEle, actualEle, this.ruleSpecification,
                          this.mockChildComparator);

    //
    // Verify
    //
    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorMessage());
    assertFalse(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockChildComparator);
  }

  /**
   * Verify operation of the regex rule's comparison when there is a mismatch.
   */
  @Test
  public void testCompareMismatch() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement templateEle = new JsonPrimitive("x-expected-x");
    JsonElement actualEle = new JsonPrimitive("x-actual-x");
    this.ruleSpecification.setPattern(".*nomatch.*");

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule.compare("x-path-x", templateEle, actualEle, this.ruleSpecification,
                          this.mockChildComparator);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("value at path x-path-x does not match '.*nomatch.*': value=x-actual-x",
                 result.getErrorMessage());
    assertFalse(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockChildComparator);
  }

  @Test
  public void testCompareNonPrimitive() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement templateEle = new JsonPrimitive("x-expected-x");
    JsonObject actualEle = new JsonObject();
    actualEle.add("field1", new JsonPrimitive("x-actual-x"));
    this.ruleSpecification.setPattern(".*actual.*");

    //
    // Execute
    //
    JsonComparatorResult
        result =
        this.rule.compare("x-path-x", templateEle, actualEle, this.ruleSpecification,
                          this.mockChildComparator);

    //
    // Verify
    //
    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorMessage());
    assertFalse(result.isDeep());
    Mockito.verifyZeroInteractions(this.mockChildComparator);
  }
}
