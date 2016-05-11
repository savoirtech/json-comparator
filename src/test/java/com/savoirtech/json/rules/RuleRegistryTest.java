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

package com.savoirtech.json.rules;

import com.savoirtech.json.rules.impl.ArrayAsSetRule;
import com.savoirtech.json.rules.impl.RegexMatchingRule;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the Rule Registry.
 *
 * Created by art on 5/10/16.
 */
public class RuleRegistryTest {

  private RuleRegistry ruleRegistry;

  private JsonComparatorRule mockRule1;
  private JsonComparatorRule mockRule2;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.ruleRegistry = new RuleRegistry();

    this.mockRule1 = Mockito.mock(JsonComparatorRule.class);
    this.mockRule2 = Mockito.mock(JsonComparatorRule.class);
  }

  /**
   * Verify operation of the initBuildInRules method.
   */
  @Test
  public void testInitBuiltInRules() throws Exception {
    //
    // Execute
    //
    this.ruleRegistry.initBuiltInRules();
    JsonComparatorRule matchesRule = this.ruleRegistry.lookupRule("matches");
    JsonComparatorRule setRule = this.ruleRegistry.lookupRule("set");

    //
    // Verify
    //
    assertTrue(matchesRule instanceof RegexMatchingRule);
    assertTrue(setRule instanceof ArrayAsSetRule);
  }

  /**
   * Verify operation of the registerRule and lookupRule methods.
   */
  @Test
  public void testRegisterAndLookupRules() throws Exception {
    assertNull(this.ruleRegistry.lookupRule("x-action1-x"));
    assertNull(this.ruleRegistry.lookupRule("x-action2-x"));

    this.ruleRegistry.registerRule("x-action1-x", this.mockRule1);
    assertSame(this.mockRule1, this.ruleRegistry.lookupRule("x-action1-x"));
    assertNull(this.ruleRegistry.lookupRule("x-action2-x"));

    this.ruleRegistry.registerRule("x-action2-x", this.mockRule2);
    assertSame(this.mockRule2, this.ruleRegistry.lookupRule("x-action2-x"));
    assertSame(this.mockRule1, this.ruleRegistry.lookupRule("x-action1-x"));
  }
}