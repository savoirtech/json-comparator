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

import com.savoirtech.json.model.JsonComparatorRuleSpecification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the factory of JsonComparatorCompiledRule objects.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorCompiledRuleFactoryTest {

  private JsonComparatorCompiledRuleFactory factory;

  private JsonComparatorRule mockRule;
  private JsonComparatorRuleSpecification mockRuleSpecification;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.factory = new JsonComparatorCompiledRuleFactory();

    this.mockRule = Mockito.mock(JsonComparatorRule.class);
    this.mockRuleSpecification = Mockito.mock(JsonComparatorRuleSpecification.class);
  }

  /**
   * Verify operation of the factory's create method.
   */
  @Test
  public void testCreate() throws Exception {
    //
    // Setup test data and interactions
    //

    //
    // Execute
    //
    JsonComparatorCompiledRule compiledRule1;
    JsonComparatorCompiledRule compiledRule2;
    compiledRule1 = this.factory.create(this.mockRule, this.mockRuleSpecification);
    compiledRule2 = this.factory.create(this.mockRule, this.mockRuleSpecification);

    //
    // Verify
    //
    assertSame(this.mockRule, compiledRule1.getRule());
    assertSame(this.mockRuleSpecification, compiledRule1.getSpecification());
    assertSame(this.mockRule, compiledRule2.getRule());
    assertSame(this.mockRuleSpecification, compiledRule2.getSpecification());
    assertNotSame(compiledRule1, compiledRule2);
  }
}
