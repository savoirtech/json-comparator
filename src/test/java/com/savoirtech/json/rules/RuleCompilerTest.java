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

import com.savoirtech.json.exception.UnknownRuleException;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by art on 5/10/16.
 */
public class RuleCompilerTest {

  private RuleCompiler ruleCompiler;

  private RuleRegistry mockRuleRegistry;
  private JsonComparatorRuleSpecification mockRuleSpecification;
  private JsonComparatorRule mockRule;
  private JsonComparatorCompiledRuleFactory mockCompiledRuleFactory;
  private JsonComparatorCompiledRule mockCompiledRule;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.mockRuleRegistry = Mockito.mock(RuleRegistry.class);
    this.mockRuleSpecification = Mockito.mock(JsonComparatorRuleSpecification.class);
    this.mockRule = Mockito.mock(JsonComparatorRule.class);
    this.mockCompiledRuleFactory = Mockito.mock(JsonComparatorCompiledRuleFactory.class);
    this.mockCompiledRule = Mockito.mock(JsonComparatorCompiledRule.class);

    this.ruleCompiler = new RuleCompiler(this.mockRuleRegistry);

    Mockito.when(this.mockCompiledRuleFactory.create(this.mockRule, this.mockRuleSpecification))
        .thenReturn(this.mockCompiledRule);
  }

  /**
   * Verify operation of the getter and setter for the compiled rule factory.
   *
   * @throws Exception
   */
  @Test
  public void testGetSetCompiledRuleFactory() throws Exception {
    assertNotNull(this.ruleCompiler.getCompiledRuleFactory());
    assertNotSame(this.mockCompiledRuleFactory, this.ruleCompiler.getCompiledRuleFactory());

    this.ruleCompiler.setCompiledRuleFactory(this.mockCompiledRuleFactory);

    assertSame(this.mockCompiledRuleFactory, this.ruleCompiler.getCompiledRuleFactory());
  }

  /**
   * Verify operation of the compile method.
   */
  @Test
  public void testCompile() throws Exception {
    //
    // Setup test data and interactions
    //
    this.ruleCompiler.setCompiledRuleFactory(this.mockCompiledRuleFactory);
    Mockito.when(this.mockRuleSpecification.getAction()).thenReturn("x-action-x");
    Mockito.when(this.mockRuleRegistry.lookupRule("x-action-x")).thenReturn(this.mockRule);

    //
    // Execute
    //
    JsonComparatorCompiledRule compiledRule;
    compiledRule = this.ruleCompiler.compile(this.mockRuleSpecification);

    //
    // Verify
    //
    assertSame(this.mockCompiledRule, compiledRule);
  }

  /**
   * Verify operation of the compile method when an invalid action is applied.
   */
  @Test
  public void testCompileUnknownAction() throws Exception {
    //
    // Setup test data and interactions
    //
    this.ruleCompiler.setCompiledRuleFactory(this.mockCompiledRuleFactory);
    Mockito.when(this.mockRuleSpecification.getAction()).thenReturn("x-action-x");

    //
    // Execute
    //
    try {
      this.ruleCompiler.compile(this.mockRuleSpecification);
      fail("missing expected exception");
    } catch (UnknownRuleException urExc) {
      assertEquals(new UnknownRuleException("x-action-x").getMessage(), urExc.getMessage());
    }
  }
}
