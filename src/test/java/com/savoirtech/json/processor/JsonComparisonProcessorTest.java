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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.jayway.jsonpath.Configuration;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.JsonComparatorCompiledRule;
import com.savoirtech.json.rules.RuleChildComparator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparisonProcessor.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparisonProcessorTest {

  private JsonComparisonProcessor processor;

  private Logger mockLog;
  private Configuration mockJsonPathConfiguration;
  private RuleProcessor mockRuleProcessor;
  private JsonComparatorCompiledRule mockCompiledRule;
  private JsonComparatorRuleSpecification[] rules;

  private JsonElement templateJson;   // Not mockable
  private JsonElement actualJson;     // Not mockable

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.mockLog = Mockito.mock(Logger.class);
    this.mockJsonPathConfiguration = Mockito.mock(Configuration.class);
    this.mockRuleProcessor = Mockito.mock(RuleProcessor.class);
    this.mockCompiledRule = Mockito.mock(JsonComparatorCompiledRule.class);

    this.rules = new JsonComparatorRuleSpecification[0];
    this.templateJson = new JsonPrimitive("x-template-x");
    this.actualJson = new JsonPrimitive("x-actual-x");

    this.processor =
        new JsonComparisonProcessor(this.mockJsonPathConfiguration, this.templateJson, this.rules,
                                    this.actualJson);
  }

  /**
   * Verify operation of the getter and setter for the Log.
   */
  @Test
  public void testGetSetLog() throws Exception {
    assertNotNull(this.processor.getLog());
    assertNotSame(this.mockLog, this.processor.getLog());

    this.processor.setLog(this.mockLog);
    assertSame(this.mockLog, this.processor.getLog());
  }

  /**
   * Verify operation of the getter and setter for the rule processor.
   */
  @Test
  public void testGetSetRuleProcessor() throws Exception {
    assertNotNull(this.processor.getRuleProcessor());
    assertNotSame(this.mockRuleProcessor, this.processor.getRuleProcessor());

    this.processor.setRuleProcessor(this.mockRuleProcessor);
    assertSame(this.mockRuleProcessor, this.processor.getRuleProcessor());
  }

  /**
   * Verify operation of the executeComparison method on mismatched primitive values only.
   */
  @Test
  public void testExecuteNegativeComparisonPrimitives() throws Exception {
    //
    // Execute and Verify
    //
    this.testComparison(this.templateJson, this.actualJson, false,
                        "primitive mismatch at path $: actual=\"x-actual-x\"; expected=\"x-template-x\"");
  }

  /**
   * Verify operation of the executeComparison method on matched primitive values only.
   */
  @Test
  public void testExecutePositiveComparisonPrimitives() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonElement templateValue1 = new JsonPrimitive(13);
    JsonElement actualValue1 = new JsonPrimitive(13);

    //
    // Execute and Verify
    //
    this.testComparison(templateValue1, actualValue1, true, null);
  }

  /**
   * Verify operation of the executeComparison method on matching objects.
   */
  @Test
  public void testExecuteComparisonObjects() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonObject templateObject = new JsonObject();
    templateObject.add("x-field1-x", new JsonPrimitive("x-value1-x"));

    JsonObject actualObject = new JsonObject();
    actualObject.add("x-field1-x", new JsonPrimitive("x-value1-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateObject, actualObject, true, null);
  }

  /**
   * Verify operation of the executeComparison method on mismatched objects.
   */
  @Test
  public void testExecuteComparisonMismatchedObjects() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonObject templateObject = new JsonObject();
    templateObject.add("x-field1-x", new JsonPrimitive("x-value1-x"));

    JsonObject actualObject = new JsonObject();
    actualObject.add("x-field1-x", new JsonPrimitive("x-value2-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateObject, actualObject, false,
                        "primitive mismatch at path $['x-field1-x']: actual=\"x-value2-x\"; expected=\"x-value1-x\"");
  }

  /**
   * Verify operation of the executeComparison method on objects with different sets of fields.
   */
  @Test
  public void testExecuteComparisonObjectsDifferentFieldSets() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonObject templateObject = new JsonObject();
    templateObject.add("x-field1-x", new JsonPrimitive("x-value1-x"));
    templateObject.add("x-field2-x", new JsonPrimitive("x-value2-x"));

    JsonObject actualObject = new JsonObject();
    actualObject.add("x-field1-x", new JsonPrimitive("x-value1-x"));
    actualObject.add("x-field3-x", new JsonPrimitive("x-value3-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateObject, actualObject, false,
                        "object field sets do not match: path='$'");
  }

  /**
   * Verify operation of the executeComparison method on matching arrays.
   */
  @Test
  public void testExecuteComparisonArrays() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonArray templateArray = new JsonArray();
    templateArray.add(new JsonPrimitive("x-value1-x"));

    JsonArray actualArray = new JsonArray();
    actualArray.add(new JsonPrimitive("x-value1-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateArray, actualArray, true, null);
  }

  /**
   * Verify operation of the executeComparison method on arrays of different sizes.
   */
  @Test
  public void testExecuteComparisonArraysDifferentSizes() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonArray templateArray = new JsonArray();
    templateArray.add(new JsonPrimitive("x-value1-x"));
    templateArray.add(new JsonPrimitive("x-value2-x"));

    JsonArray actualArray = new JsonArray();
    actualArray.add(new JsonPrimitive("x-value1-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateArray, actualArray, false,
                        "array size mismatch: path='$'; actualSize=1; expectedSize=2");
  }

  /**
   * Verify operation of the executeComparison method on mismatched arrays of the same size.
   */
  @Test
  public void testExecuteComparisonMismatchedArrays() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonArray templateArray = new JsonArray();
    templateArray.add(new JsonPrimitive("x-value1-x"));

    JsonArray actualArray = new JsonArray();
    actualArray.add(new JsonPrimitive("x-value2-x"));

    //
    // Execute and Verify
    //
    this.testComparison(templateArray, actualArray, false,
                        "primitive mismatch at path $[0]: actual=\"x-value2-x\"; expected=\"x-value1-x\"");
  }

  /**
   * Verify the operation of the executeComparison method when comparing via a rule which returns a
   * match.
   *
   * @throws Exception
   */
  @Test
  public void testExecuteComparisonWithMatchingRule() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult testResult = new JsonComparatorResult(true, true, null);
    Mockito.when(this.mockRuleProcessor.findMatchingRule("$")).thenReturn(this.mockCompiledRule);
    Mockito.when(this.mockCompiledRule
                     .compare(Mockito.eq("$"), Mockito.same(this.templateJson),
                              Mockito.same(this.actualJson),
                              Mockito.any(RuleChildComparator.class)))
        .thenReturn(testResult);

    //
    // Execute and Verify
    //
    this.testComparison(this.templateJson, this.actualJson, true, null);
  }

  /**
   * Verify the operation of the executeComparison method when comparing via a rule which returns a
   * failure.
   *
   * @throws Exception
   */
  @Test
  public void testExecuteComparisonFailureWithMatchingRule() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult testResult = new JsonComparatorResult(true, false, "x-error-message-x");
    Mockito.when(this.mockRuleProcessor.findMatchingRule("$")).thenReturn(this.mockCompiledRule);
    Mockito.when(this.mockCompiledRule
                     .compare(Mockito.eq("$"), Mockito.same(this.templateJson),
                              Mockito.same(this.actualJson),
                              Mockito.any(RuleChildComparator.class)))
        .thenReturn(testResult);

    //
    // Execute and Verify
    //
    this.testComparison(this.templateJson, this.actualJson, false, "x-error-message-x");
  }

  /**
   * Verify the operation of the executeComparison method when comparing an object against a
   * primitive value.
   *
   * @throws Exception
   */
  @Test
  public void testExecuteComparisonObjectVsPrimitive() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonObject templateObject = new JsonObject();
    JsonElement actualEle = new JsonPrimitive("x-primitive-x");

    //
    // Execute and Verify
    //
    this.testComparison(templateObject, actualEle, false,
                        "actual json at path $ is not an object, but an object is expected");
  }

  /**
   * Verify the operation of the executeComparison method when comparing an array against a
   * primitive value.
   *
   * @throws Exception
   */
  @Test
  public void testExecuteComparisonArrayVsPrimitive() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonArray templateArray = new JsonArray();
    JsonElement actualEle = new JsonPrimitive("x-primitive-x");

    //
    // Execute and Verify
    //
    this.testComparison(templateArray, actualEle, false,
                        "actual json at path $ is not an array, but an array is expected");
  }

  /**
   * Test a comparison that goes through the child adapter of the processor.
   *
   * @throws Exception
   */
  @Test
  public void testCompareWithChildAdapter() throws Exception {
    //
    // Setup test data and interactions
    //

    // Use a rule, and setup an "Answer" for the rule that calls into the child adapter
    Mockito.when(this.mockRuleProcessor.findMatchingRule("$")).thenReturn(this.mockCompiledRule);
    Mockito.when(this.mockCompiledRule
                     .compare(Mockito.eq("$"), Mockito.same(this.templateJson),
                              Mockito.same(this.actualJson),
                              Mockito.any(RuleChildComparator.class)))
        .thenAnswer(invocation -> {
          RuleChildComparator childComparator;
          childComparator = (RuleChildComparator) invocation.getArguments()[3];

          // Call into the child adapter now and return the actual result
          return childComparator.compare("x-sub-path-x",
                                         (JsonElement) invocation.getArguments()[1],
                                         (JsonElement) invocation.getArguments()[2]);
        });

    //
    // Execute and Verify
    //
    this.testComparison(this.templateJson, this.actualJson, false,
                        "primitive mismatch at path x-sub-path-x: actual=\"x-actual-x\"; expected=\"x-template-x\"");
  }

//========================================
// Internal Methods
//----------------------------------------

  /**
   * Execute the test with a new processor configured for the given template and actual JSON
   * elements and verify the result match the expected values.
   *
   * @param templateEle template JSON for the comparison.
   * @param actualEle actual JSON for the comparison.
   * @param expectMatch true = a match is expected; false = a mismatch is expected.
   * @param expectedErrorMessage error message expected.
   * @throws Exception
   */
  private void testComparison(JsonElement templateEle, JsonElement actualEle, boolean expectMatch,
                              String expectedErrorMessage) throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparisonProcessor processor1;
    processor1 =
        new JsonComparisonProcessor(this.mockJsonPathConfiguration, templateEle, this.rules,
                                    actualEle);
    processor1.setRuleProcessor(this.mockRuleProcessor);

    //
    // Execute
    //
    JsonComparatorResult result;
    result = processor1.executeComparison();

    //
    // Verify
    //
    Mockito.verify(this.mockRuleProcessor).init();
    assertEquals(expectMatch, result.isMatch());
    assertEquals(expectedErrorMessage, result.getErrorMessage());
  }

}