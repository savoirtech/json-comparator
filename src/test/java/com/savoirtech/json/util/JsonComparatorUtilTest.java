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

package com.savoirtech.json.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.model.JsonComparatorSelector;
import com.savoirtech.json.model.JsonComparatorSpecification;
import com.savoirtech.json.util.model.JsonComparatorResultDetails;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by art on 5/14/16.
 */
public class JsonComparatorUtilTest {

  private JsonComparatorUtil util;

  private Gson testGson;
  private Configuration testJsonPathConfiguration;

  private JsonElement templateJson;
  private JsonElement actualJson;
  private String testComparisonSpec;
  private JsonComparatorSelector testSelector;
  private JsonComparatorRuleSpecification[] testRules;


  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.util = new JsonComparatorUtil();

    this.testGson = new Gson();   // Not mockable
    this.testJsonPathConfiguration =
        Configuration.builder().jsonProvider(new GsonJsonProvider(this.testGson)).build();

    // Can't mock gson, so these tests are more complex...
    this.templateJson = new JsonParser().parse("[ 1, 2, 4 ]");
    this.actualJson = new JsonParser().parse("[ 1, 2, 3 ]");

    this.testSelector = new JsonComparatorSelector();
    this.testSelector.setPath("x-selector-path-x");

    // Order matters here:
    this.prepareTestComparatorSpec();
    this.testComparisonSpec = this.createTestComparatorSpecString();
  }

  /**
   * Verify operation of the getter and setter for gson.
   */
  @Test
  public void testGetSetGson() throws Exception {
    assertNull(this.util.getGson());

    this.util.setGson(this.testGson);
    assertSame(this.testGson, this.util.getGson());
  }

  /**
   * Verify operation of the getter and setter for JSON path configuration.
   */
  @Test
  public void testSetJsonPathConfiguration() throws Exception {
    assertNull(this.util.getJsonPathConfiguration());

    this.util.setJsonPathConfiguration(this.testJsonPathConfiguration);
    assertSame(this.testJsonPathConfiguration, this.util.getJsonPathConfiguration());
  }

  /**
   * Verify operation of the extractFailureDetails method.
   */
  @Test
  public void testExtractFailureDetails() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult
        comparatorResult =
        new JsonComparatorResult(true, false, "x-error-x", "$[2]");
    this.util.setGson(this.testGson);
    this.util.setJsonPathConfiguration(this.testJsonPathConfiguration);

    //
    // Execute
    //
    JsonComparatorResultDetails
        details =
        this.util.extractFailureDetails(this.testComparisonSpec, this.actualJson.toString(),
                                        comparatorResult);

    //
    // Verify
    //
    assertEquals(new JsonPrimitive(3), details.getActualElement());
    assertEquals(new JsonPrimitive(4), details.getTemplateElement());
  }

  /**
   * Verify operation of the extractFailureDetails method when the error path is null.
   */
  @Test
  public void testExtractFailureDetailsNullErrorPath() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorResult
        comparatorResult =
        new JsonComparatorResult(true, true, null, null);
    this.util.setGson(this.testGson);
    this.util.setJsonPathConfiguration(this.testJsonPathConfiguration);

    //
    // Execute
    //
    JsonComparatorResultDetails
        details =
        this.util.extractFailureDetails(this.testComparisonSpec, this.actualJson.toString(),
                                        comparatorResult);

    //
    // Verify
    //
    assertNull(details);
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

}