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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparatorCompiledRule.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorCompiledRuleTest {

  private JsonComparatorCompiledRule compiledRule;

  private JsonComparatorRuleSpecification ruleSpecification;
  private JsonComparatorRule mockRule;
  private RuleChildComparator mockChildComparator;

  private JsonElement templateEle;
  private JsonElement actualEle;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.mockRule = Mockito.mock(JsonComparatorRule.class);
    this.mockChildComparator = Mockito.mock(RuleChildComparator.class);

    this.ruleSpecification = new JsonComparatorRuleSpecification();

    this.templateEle = new JsonPrimitive(1);
    this.actualEle = new JsonPrimitive(1);

    this.compiledRule = new JsonComparatorCompiledRule(this.mockRule, this.ruleSpecification);
  }

  @Test
  public void testGetRule() throws Exception {
    assertSame(this.mockRule, this.compiledRule.getRule());
  }

  @Test
  public void testGetRuleSpecification() throws Exception {
    assertSame(this.ruleSpecification, this.compiledRule.getSpecification());
  }

  /**
   * Verify operation of the compare method on the compiled rule.
   */
  @Test
  public void testCompare() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult testResult = new JsonComparatorResult(true, true, null, null);
    Mockito.when(this.mockRule
                     .compare("x-path-x", this.templateEle, this.actualEle, this.ruleSpecification,
                              this.mockChildComparator)).thenReturn(testResult);

    //
    // Execute
    //
    JsonComparatorResult
        actualResult =
        this.compiledRule
            .compare("x-path-x", this.templateEle, this.actualEle, this.mockChildComparator);

    //
    // Verify
    //
    assertSame(testResult, actualResult);
  }
}
