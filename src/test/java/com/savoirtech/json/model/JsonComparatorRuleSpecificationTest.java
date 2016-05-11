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

package com.savoirtech.json.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparatorRuleSpecification.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorRuleSpecificationTest {

  private JsonComparatorRuleSpecification ruleSpecification;

  private JsonComparatorSelector mockSelector1;
  private JsonComparatorSelector mockSelector2;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.ruleSpecification = new JsonComparatorRuleSpecification();

    this.mockSelector1 = Mockito.mock(JsonComparatorSelector.class);
    this.mockSelector2 = Mockito.mock(JsonComparatorSelector.class);
  }

  /**
   * Verify operation of the getter an setter of the selector.
   */
  @Test
  public void testGetSetSelector() throws Exception {
    assertNull(this.ruleSpecification.getSelector());

    this.ruleSpecification.setSelector(this.mockSelector1);
    assertSame(this.mockSelector1, this.ruleSpecification.getSelector());
  }

  /**
   * Verify operation of the getter and setter for action.
   */
  @Test
  public void testSetAction() throws Exception {
    String testAction = "x-action-x";

    assertNull(this.ruleSpecification.getAction());

    this.ruleSpecification.setAction(testAction);
    assertSame(testAction, this.ruleSpecification.getAction());
  }

  /**
   * Verify operation of the getter and setter for pattern.
   */
  @Test
  public void testSetPattern() throws Exception {
    String testPattern = "x-pattern-x";

    assertNull(this.ruleSpecification.getPattern());

    this.ruleSpecification.setPattern(testPattern);
    assertSame(testPattern, this.ruleSpecification.getPattern());
  }

  /**
   * Verify operation of the equals method.
   */
  @Test
  public void testEquals() throws Exception {
    //
    // Setup test data and interactions
    //
    String testAction1 = "x-action1-x";
    String testAction2 = "x-action2-x";
    String testPattern1 = "x-pattern1-x";
    String testPattern2 = "x-pattern2-x";

    JsonComparatorRuleSpecification matchRule1;
    JsonComparatorRuleSpecification matchRule2;
    JsonComparatorRuleSpecification matchRule3;
    JsonComparatorRuleSpecification matchRule4;

    matchRule1 = this.createRuleSpecification(testAction1, testPattern1, this.mockSelector1);
    matchRule2 = this.createRuleSpecification(testAction2, testPattern1, this.mockSelector1);
    matchRule3 = this.createRuleSpecification(testAction1, testPattern2, this.mockSelector1);
    matchRule4 = this.createRuleSpecification(testAction1, testPattern1, this.mockSelector2);

    this.ruleSpecification.setAction(testAction1);
    this.ruleSpecification.setPattern(testPattern1);
    this.ruleSpecification.setSelector(this.mockSelector1);

    //
    // Execute and Verify
    //
    assertTrue(this.ruleSpecification.equals(matchRule1));
    assertTrue(matchRule1.equals(this.ruleSpecification));

    assertFalse(this.ruleSpecification.equals(matchRule2));
    assertFalse(matchRule2.equals(this.ruleSpecification));

    assertFalse(this.ruleSpecification.equals(matchRule3));
    assertFalse(matchRule3.equals(this.ruleSpecification));

    assertFalse(this.ruleSpecification.equals(matchRule4));
    assertFalse(matchRule4.equals(this.ruleSpecification));

    assertFalse(this.ruleSpecification.equals(null));
    assertFalse(this.ruleSpecification.equals("wrong-class"));
    assertTrue(this.ruleSpecification.equals(this.ruleSpecification));
  }

  /**
   * Verify operation of the hashCode method.
   */
  @Test
  public void testHashCode() throws Exception {
    this.ruleSpecification.setAction("x-action-x");
    this.ruleSpecification.setPattern("x-pattern-x");
    this.ruleSpecification.setSelector(this.mockSelector1);

    int expected = "x-pattern-x".hashCode() + 31 * (
          "x-action-x".hashCode() + 31 * (
              this.mockSelector1.hashCode()
              )
        );

    assertEquals(expected, this.ruleSpecification.hashCode());

    this.ruleSpecification.setSelector(null);
    this.ruleSpecification.setAction(null);
    assertEquals("x-pattern-x".hashCode(), this.ruleSpecification.hashCode());

    this.ruleSpecification.setPattern(null);
    this.ruleSpecification.setAction("x-action-x");
    assertEquals(31 * ( "x-action-x".hashCode() ), this.ruleSpecification.hashCode());

    this.ruleSpecification.setAction(null);
    this.ruleSpecification.setSelector(this.mockSelector1);
    assertEquals(31 * 31 * ( this.mockSelector1.hashCode() ), this.ruleSpecification.hashCode());
  }

//========================================
// Internal Methods
//----------------------------------------

  /**
   * Create a rule specification with the given action, pattern, and selector.
   */
  private JsonComparatorRuleSpecification createRuleSpecification(String action, String pattern, JsonComparatorSelector selector) {
    JsonComparatorRuleSpecification result = new JsonComparatorRuleSpecification();

    result.setAction(action);
    result.setPattern(pattern);
    result.setSelector(selector);

    return result;
  }
}