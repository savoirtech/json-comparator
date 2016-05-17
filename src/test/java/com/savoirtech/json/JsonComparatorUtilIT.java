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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.util.JsonComparatorUtil;
import com.savoirtech.json.util.model.JsonComparatorResultDetails;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Integrated test of the JsonComparator.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparatorUtilIT {

  private JsonComparator comparator;

  @Before
  public void setupTest() {
    this.comparator = new JsonComparatorBuilder().build();
  }

  /**
   * Verify operation of extractFailureDetails when there is a simple primitive mismatch.
   */
  @Test
  public void testExtractFailureDetails() {
    String comparisonSpec = "{ \"rules\": [], \"templateJson\": [ 2, 4, 6 ] }";
    String actualJson = "[ 2, 4, 8 ]";

    JsonComparatorResultDetails
        details =
        this.runUtilTest(comparisonSpec, actualJson, new JsonPrimitive(8), new JsonPrimitive(6),
                         null);

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertNull(details.getMatchingRule());
  }

  @Test
  public void testExtractRuleFailureDetails() {
    String
        comparisonSpec =
        "{ \"rules\": [ { \"selector\": { \"path\": \"$[2]\" }, \"action\": \"matches\", \"pattern\": \"[3-5]\" } ], \"templateJson\": [ 2, 4, 6 ] }";
    String actualJson = "[ 2, 4, 6 ]";

    JsonComparatorResultDetails
        details =
        this.runUtilTest(comparisonSpec, actualJson, new JsonPrimitive(6), new JsonPrimitive(6),
                         null);

    assertNotNull(details.getMatchingRule());
  }

  @Test
  public void testExtractRuleFailureDetailsWithNullsFields() {
    Gson serializeNullsGson = new GsonBuilder().serializeNulls().create();

    String expectedJson = "{ \"field1\": 1, \"field2\": 2 }";

    String
        comparisonSpec =
        "{ \"rules\": [], \"templateJson\": " + expectedJson + " }";
    String actualJson = "{ \"field1\": null, \"field3\": 3 }";

    JsonElement expectedJsonEle = new JsonParser().parse(expectedJson);
    JsonElement actualJsonEle = new JsonParser().parse(actualJson);

    JsonComparatorResultDetails
        details =
        this.runUtilTest(comparisonSpec, actualJson, actualJsonEle, expectedJsonEle,
                         serializeNullsGson);
  }

//========================================
// Internal Methods
//----------------------------------------

  private JsonComparatorResultDetails runUtilTest(String comparisonSpec, String actualJsonString,
                                                  JsonElement expectedActualJsonEle,
                                                  JsonElement expectedTemplateJsonEle,
                                                  Gson optionalGson) {

    JsonComparatorBuilder builder = new JsonComparatorBuilder();

    if (optionalGson != null) {
      builder.withGson(optionalGson);
    }

    JsonComparator comparator = builder.build();
    JsonComparatorUtil util = builder.buildUtil();

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJsonString);

    assertFalse(result.isMatch());

    JsonComparatorResultDetails
        details =
        util.extractFailureDetails(comparisonSpec, actualJsonString, result);

    assertEquals(expectedActualJsonEle, details.getActualElement());
    assertEquals(expectedTemplateJsonEle, details.getTemplateElement());

    return details;
  }
}
