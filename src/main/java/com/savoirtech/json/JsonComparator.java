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

import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.model.JsonComparatorSpecification;
import com.savoirtech.json.processor.JsonComparisonProcessor;
import com.savoirtech.json.processor.JsonComparisonProcessorFactory;

/**
 * Comparator of two JSON documents which supports rules to allow expected variations in the
 * results.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparator {

  private Gson gson = new GsonBuilder().create();

  private JsonComparisonProcessorFactory jsonComparisonProcessorFactory;

//========================================
// Constructor
//----------------------------------------

  /**
   * Use JsonComparatorBuilder to construct JsonComparator.
   */
  protected JsonComparator() {
  }

//========================================
// Getters and Setters
//----------------------------------------

  public Gson getGson() {
    return gson;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public JsonComparisonProcessorFactory getJsonComparisonProcessorFactory() {
    return jsonComparisonProcessorFactory;
  }

  public void setJsonComparisonProcessorFactory(
      JsonComparisonProcessorFactory jsonComparisonProcessorFactory) {
    this.jsonComparisonProcessorFactory = jsonComparisonProcessorFactory;
  }

//========================================
// Public API
//----------------------------------------

  /**
   * Compare the actual JSON given to the comparison specification given.
   *
   * @param comparisonSpec specification containing template JSON and rules for comparison.
   * @param actualJson     the actual JSON to compare.
   * @return result indicating whether the JSON is a match, and a description of any failure.
   */
  public JsonComparatorResult compare(String comparisonSpec, String actualJson) {
    JsonComparatorResult result;

    JsonComparatorSpecification comparatorSpecification = this.compileSpecification(comparisonSpec);

    JsonElement templateJson = comparatorSpecification.getTemplateJson();

    //
    // Validate the template and actual json are not null.  If both are null, accept the result.
    //
    if (templateJson == null) {
      if (actualJson != null) {
        return new JsonComparatorResult(false, false, "template json is null; actual json is not",
                                        "$");
      } else {
        // Expected and actual json are null; accept them as-is
        return new JsonComparatorResult(true, true, null, null);
      }
    } else if (actualJson == null) {
      return new JsonComparatorResult(false, false, "actual json is null; template json is not",
                                      "$");
    }

    //
    // Execute the comparison now and return the result.
    //
    result = this.compareJson(templateJson, comparatorSpecification.getRules(), actualJson);

    return result;
  }

//========================================
// INTERNALS
//========================================

  /**
   * Compare the actual JSON given to the template JSON and rules.
   *
   * @param templateJson     template JSON against which to compare the actual JSON.
   * @param rules            rules that customize the comparison process.
   * @param actualJsonString actual JSON to compare.
   * @return result indicating whether the actual JSON matches, and a description of any failure.
   */
  private JsonComparatorResult compareJson(JsonElement templateJson,
                                           JsonComparatorRuleSpecification[] rules,
                                           String actualJsonString) {

    JsonParser parser = new JsonParser();
    JsonElement actualJsonEle = parser.parse(actualJsonString);

    JsonComparisonProcessor
        processor =
        this.jsonComparisonProcessorFactory.createProcessor(templateJson, rules, actualJsonEle);

    JsonComparatorResult result = processor.executeComparison();

    return result;
  }

  /**
   * Compile the comparison specification given.
   *
   * @param comparisonSpec comparison specification, in string format.
   * @return compiled result of the specification.
   */
  private JsonComparatorSpecification compileSpecification(String comparisonSpec) {
    return this.gson.fromJson(comparisonSpec, JsonComparatorSpecification.class);
  }
}
