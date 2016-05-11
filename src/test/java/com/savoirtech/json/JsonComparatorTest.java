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

package com.savoirtech.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.model.JsonComparatorSelector;
import com.savoirtech.json.model.JsonComparatorSpecification;
import com.savoirtech.json.processor.JsonComparisonProcessor;
import com.savoirtech.json.processor.JsonComparisonProcessorFactory;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparator.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorTest {

  private JsonComparator jsonComparator;

  private Gson testGson;             // Not mockable
  private JsonElement templateJson;  // Not mockable

  private JsonComparisonProcessorFactory mockProcessorFactory;
  private JsonComparisonProcessor mockProcessor;

  private String testComparisonSpec;
  private JsonComparatorSelector testSelector;
  private JsonComparatorRuleSpecification[] testRules;

  private String actualJson;
  private JsonElement actualJsonElement;


  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.jsonComparator = new JsonComparator();   // Using the protected constructor here

    this.testGson = new Gson();
    this.templateJson = new JsonPrimitive("x-template-json-x");

    this.mockProcessorFactory = Mockito.mock(JsonComparisonProcessorFactory.class);
    this.mockProcessor = Mockito.mock(JsonComparisonProcessor.class);

    this.testSelector = new JsonComparatorSelector();
    this.testSelector.setPath("x-selector-path-x");

    this.actualJson = "{ \"x-field-x\": \"x-actual-value-x\" }";
    this.actualJsonElement = this.compileJson(this.actualJson);

    // Order matters here:
    this.prepareTestComparatorSpec();
    this.testComparisonSpec = this.createTestComparatorSpecString();
  }

  /**
   * Verify operation of the getter and setter for Gson.
   */
  @Test
  public void testGetSetGson() throws Exception {
    assertNotNull(this.jsonComparator.getGson());
    assertNotSame(this.testGson, this.jsonComparator.getGson());

    this.jsonComparator.setGson(this.testGson);

    assertSame(this.testGson, this.jsonComparator.getGson());
  }

  /**
   * Verify operation of the getter and setter of the JsonComparisonProcessorFactory.
   */
  @Test
  public void testSetJsonComparisonProcessorFactory() throws Exception {
    assertNull(this.jsonComparator.getJsonComparisonProcessorFactory());

    this.jsonComparator.setJsonComparisonProcessorFactory(this.mockProcessorFactory);
    assertSame(this.mockProcessorFactory, this.jsonComparator.getJsonComparisonProcessorFactory());
  }

  /**
   * Verify operation of the compare method.
   */
  @Test
  public void testCompare() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult testResult = Mockito.mock(JsonComparatorResult.class);

    Mockito.when(this.mockProcessorFactory
                     .createProcessor(Mockito.eq(this.templateJson), Mockito.argThat(this.createRulesMatcher(this.testRules)),
                                      Mockito.eq(this.actualJsonElement)))
        .thenReturn(this.mockProcessor);

    Mockito.when(this.mockProcessor.executeComparison()).thenReturn(testResult);

    this.jsonComparator.setJsonComparisonProcessorFactory(this.mockProcessorFactory);

    //
    // Execute
    //
    JsonComparatorResult result;
    result = this.jsonComparator.compare(this.testComparisonSpec, this.actualJson);

    //
    // Verify
    //
    assertSame(testResult, result);
  }

  /**
   * Verify operation of the compare method when the actual JSON is null but the expected JSON is
   * not null.
   */
  @Test
  public void testCompareActualNullExpectedNonNull() throws Exception {
    //
    // Execute
    //
    JsonComparatorResult result;
    result = this.jsonComparator.compare(this.testComparisonSpec, null);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("actual json is null; template json is not", result.getErrorMessage());
    assertFalse(result.isDeep());
  }

  /**
   * Verify operation of the compare method when the expected JSON is null but the actual JSON is
   * not null.
   */
  @Test
  public void testCompareActualNonNullExpectedNull() throws Exception {
    //
    // Setup test data and interactions
    //
    this.templateJson = null;
    String testComparisonSpec = createTestComparatorSpecString();

    //
    // Execute
    //
    JsonComparatorResult result;
    result = this.jsonComparator.compare(testComparisonSpec, this.actualJson);

    //
    // Verify
    //
    assertFalse(result.isMatch());
    assertEquals("template json is null; actual json is not", result.getErrorMessage());
    assertFalse(result.isDeep());
  }

  /**
   * Verify operation of the compare method when the expected and actual JSON are both null.
   */
  @Test
  public void testCompareActualNullExpectedNull() throws Exception {
    //
    // Setup test data and interactions
    //
    this.templateJson = null;
    String testComparisonSpec = createTestComparatorSpecString();

    //
    // Execute
    //
    JsonComparatorResult result;
    result = this.jsonComparator.compare(testComparisonSpec, null);

    //
    // Verify
    //
    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorMessage());
    assertTrue(result.isDeep());
  }

//========================================
// Internal Methods
//----------------------------------------

  /**
   * Create a test comparator string using the test data for the comparator specification.  Note
   * that this is required because Gson objects cannot be mocked.
   *
   * @return string with the serialized json for the test comparison specification.
   */
  private String createTestComparatorSpecString() {
    JsonComparatorSpecification comparatorSpecification = new JsonComparatorSpecification();
    comparatorSpecification.setRules(this.testRules);
    comparatorSpecification.setTemplateJson(this.templateJson);

    return this.testGson.toJson(comparatorSpecification);
  }

  /**
   * Prepare test data for the comparator specification.
   */
  private void prepareTestComparatorSpec() {
    JsonComparatorRuleSpecification spec = new JsonComparatorRuleSpecification();
    spec.setAction("x-action-x");
    spec.setPattern("x-pattern-x");
    spec.setSelector(this.testSelector);

    this.testRules = new JsonComparatorRuleSpecification[]{spec};
  }

  /**
   * Compile a json string into a JsonElement.  Note this is required for the test since Gson
   * objects cannot be mocked.
   *
   * @param jsonString string containing serialized json content to parse.
   * @return JsonElement for the root of the parsed json.
   */
  private JsonElement compileJson(String jsonString) {
    JsonParser parser = new JsonParser();
    JsonElement result = parser.parse(jsonString);

    return result;
  }

  /**
   * Create a matcher for an array of json comparator rule specifications.
   *
   * @param expected an array containing the expected specifications.
   * @return the matcher that will validate an array of rules matches the expected one given.
   */
  private Matcher<JsonComparatorRuleSpecification[]> createRulesMatcher(
      JsonComparatorRuleSpecification[] expected) {
    Matcher<JsonComparatorRuleSpecification[]>
        result =
        new ArgumentMatcher<JsonComparatorRuleSpecification[]>() {
          @Override
          public boolean matches(Object argument) {
            if (argument instanceof JsonComparatorRuleSpecification[]) {
              JsonComparatorRuleSpecification[]
                  actual =
                  (JsonComparatorRuleSpecification[]) argument;

              // Make sure the actual array contains the right number of entries.
              if (actual.length == expected.length) {
                //
                // Iterate over expected and actual values and compare them.
                //
                Iterator<JsonComparatorRuleSpecification>
                    expectedIter =
                    Arrays.asList(expected).iterator();
                Iterator<JsonComparatorRuleSpecification>
                    actualIter =
                    Arrays.asList(actual).iterator();

                while (expectedIter.hasNext()) {
                  // If the current value is not equal, the comparison is a failure.
                  if (!(expectedIter.next().equals(actualIter.next()))) {
                    return false;
                  }
                }

                return true;
              }
            }
            return false;
          }
        };

    return result;
  }
}
