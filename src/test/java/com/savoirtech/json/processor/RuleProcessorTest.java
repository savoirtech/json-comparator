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

package com.savoirtech.json.processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.model.JsonComparatorSelector;
import com.savoirtech.json.rules.JsonComparatorCompiledRule;
import com.savoirtech.json.rules.RuleCompiler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.Assert.*;

/**
 * Verify operation of the rule processor.
 *
 * Created by art on 5/11/16.
 */
public class RuleProcessorTest {

  private RuleProcessor ruleProcessor;

  private Logger mockLogger;

  private Configuration jsonPathConfiguration;
  private RuleCompiler mockRuleCompiler;
  private JsonComparatorCompiledRule mockCompiledRule1;
  private JsonComparatorCompiledRule mockCompiledRule2;
  private JsonComparatorCompiledRule mockCompiledRule3;

  private JsonComparatorRuleSpecification[] rules;
  private JsonElement actualEle;

  // { "name": "Joe", "dog": { "breed": "German Shepherd", "name": "Max" } }

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.jsonPathConfiguration =
        Configuration.builder()
            .jsonProvider(new GsonJsonProvider())
            .options(Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST)
            .build();

    this.mockRuleCompiler = Mockito.mock(RuleCompiler.class);
    this.mockCompiledRule1 = Mockito.mock(JsonComparatorCompiledRule.class);
    this.mockCompiledRule2 = Mockito.mock(JsonComparatorCompiledRule.class);
    this.mockCompiledRule3 = Mockito.mock(JsonComparatorCompiledRule.class);

    this.rules = new JsonComparatorRuleSpecification[3];

    this.rules[0] =
        this.createRuleSpecification("$['dog']['breed']", "x-breed-action-x", "x-breed-pattern-x");

    this.rules[1] =
        this.createRuleSpecification("$..['name']", "x-name-action-x", "x-name-pattern-x");

    this.rules[2] =
        this.createRuleSpecification("$..['no-such-element']", "x-no-such-element-action-x",
                                     "x-no-such-element-pattern-x");

    this.actualEle =
        new JsonParser().parse(
            "{ \"name\": \"Joe\", \"dog\": { \"breed\": \"German Shepherd\", \"name\": \"Max\" } }");

    this.mockLogger = Mockito.mock(Logger.class);

    this.ruleProcessor =
        new RuleProcessor(this.jsonPathConfiguration, this.rules, this.actualEle);
  }

  /**
   * Verify operation of the getter and setter for the log.
   */
  @Test
  public void testGetSetLog() throws Exception {
    assertNotNull(this.ruleProcessor.getLog());
    assertNotSame(this.mockLogger, this.ruleProcessor.getLog());

    this.ruleProcessor.setLog(this.mockLogger);
    assertSame(this.mockLogger, this.ruleProcessor.getLog());
  }

  /**
   * Verify operation of the getter and setter for the rule compiler.
   */
  @Test
  public void testGetSetRuleCompiler() throws Exception {
    assertNotNull(this.ruleProcessor.getRuleCompiler());
    assertNotSame(this.mockRuleCompiler, this.ruleProcessor.getRuleCompiler());

    this.ruleProcessor.setRuleCompiler(this.mockRuleCompiler);
    assertSame(this.mockRuleCompiler, this.ruleProcessor.getRuleCompiler());
  }

  /**
   * Verify operation of the init method.
   */
  @Test
  public void testInitAndFindMatchingRules() throws Exception {
    //
    // Setup test data and interactions
    //
    this.ruleProcessor.setRuleCompiler(this.mockRuleCompiler);
    this.ruleProcessor.setLog(this.mockLogger);

    Mockito.when(this.mockRuleCompiler.compile(this.rules[0])).thenReturn(this.mockCompiledRule1);
    Mockito.when(this.mockRuleCompiler.compile(this.rules[1])).thenReturn(this.mockCompiledRule2);
    Mockito.when(this.mockRuleCompiler.compile(this.rules[2])).thenReturn(this.mockCompiledRule3);

    //
    // Execute
    //
    JsonComparatorCompiledRule ruleMatch1;
    JsonComparatorCompiledRule ruleMatch2;
    JsonComparatorCompiledRule ruleMatch3;
    this.ruleProcessor.init();
    ruleMatch1 = this.ruleProcessor.findMatchingRule("$['dog']['breed']");
    ruleMatch2 = this.ruleProcessor.findMatchingRule("$['name']");
    ruleMatch3 = this.ruleProcessor.findMatchingRule("$['dog']['name']");

    //
    // Verify
    //
    assertSame(this.mockCompiledRule1, ruleMatch1);
    assertSame(this.mockCompiledRule2, ruleMatch2);
    assertSame(this.mockCompiledRule2, ruleMatch3);
    Mockito.verify(this.mockLogger).trace("rule for path selector {} did not match any paths",
                                          this.rules[2].getSelector().getPath());
  }

//========================================
// Internal Methods
//----------------------------------------

  private JsonComparatorRuleSpecification createRuleSpecification(String path, String action,
                                                                  String pattern) {

    JsonComparatorRuleSpecification result = new JsonComparatorRuleSpecification();

    result.setAction(action);
    result.setPattern(pattern);
    JsonComparatorSelector selector = new JsonComparatorSelector();
    selector.setPath(path);
    result.setSelector(selector);

    return result;
  }
}