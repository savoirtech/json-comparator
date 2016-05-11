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

import com.jayway.jsonpath.Configuration;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.JsonComparatorCompiledRule;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.rules.RuleChildComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stateful processor of a single comparison.
 *
 * Created by art on 5/9/16.
 */
public class JsonComparisonProcessor {

  private static final Logger
      DEFAULT_LOGGER =
      LoggerFactory.getLogger(JsonComparisonProcessor.class);

  private Logger log = DEFAULT_LOGGER;


  /**
   * Template, or expected, JSON for the comparison.
   */
  private final JsonElement templateJson;

  /**
   * Actual JSON being compared.
   */
  private final JsonElement actualJson;

  /**
   * Processor of rules for this comparison, responsible for compiling the rules and determining
   * which rule applies, if any, for each JSON path.
   */
  private RuleProcessor ruleProcessor;

  /**
   * Child comparator for use by rules when performing their own deep comparisons.
   */
  private final MyChildRuleComparator childRuleComparator = new MyChildRuleComparator();

//========================================
// Constructor
//----------------------------------------

  /**
   * Construct a comparison processor using the given json path configuration and rules in order to
   * compare the template json given to the actual json given.
   *
   * @param jsonPathConfiguration configuration to use with JsonPath.
   * @param templateJson          template of the expected JSON.
   * @param rules                 rules to apply to the actual JSON while comparing to the template
   *                              JSON.
   * @param actualJson            actual JSON to compare.
   */
  public JsonComparisonProcessor(Configuration jsonPathConfiguration, JsonElement templateJson,
                                 JsonComparatorRuleSpecification[] rules, JsonElement actualJson) {

    this.templateJson = templateJson;
    this.actualJson = actualJson;

    this.ruleProcessor = new RuleProcessor(jsonPathConfiguration, rules, actualJson);
  }

//========================================
// Getters and Setters
//----------------------------------------

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

  public RuleProcessor getRuleProcessor() {
    return ruleProcessor;
  }

  public void setRuleProcessor(RuleProcessor ruleProcessor) {
    this.ruleProcessor = ruleProcessor;
  }

//========================================
// Public API
//----------------------------------------

  /**
   * Execute the comparison of the JSON and return the results.
   *
   * @return result indicating whether the JSON was a match and providing an error description for
   * failures.
   */
  public JsonComparatorResult executeComparison() {
    this.ruleProcessor.init();

    return this.walkAndCompare("$", this.templateJson, this.actualJson);
  }

//========================================
// Internal Methods
//----------------------------------------

  /**
   * Walk the JSON and compare the actual JSON to the template JSON, applying rules as-needed.
   *
   * @param path        current path to the JSON elements given.
   * @param templateEle the template, or expected, JSON at this path.
   * @param actualEle   the actual JSON at this path.
   * @return result indicating whether there is a match, and providing a description when there is a
   * mismatch.
   */
  private JsonComparatorResult walkAndCompare(String path, JsonElement templateEle,
                                              JsonElement actualEle) {

    JsonComparatorResult result;

    // Find the rule that applies, if any
    JsonComparatorCompiledRule rule = this.ruleProcessor.findMatchingRule(path);

    if (rule != null) {
      result = rule.compare(path, templateEle, actualEle, this.childRuleComparator);
    } else {
      result = this.shallowCompareJsonElements(path, templateEle, actualEle);
    }

    // Make sure contents of objects and arrays are walked, as needed
    if ((result.isMatch()) && (!result.isDeep())) {
      if (actualEle.isJsonObject()) {
        result =
            this.walkJsonObjectFields(path, templateEle.getAsJsonObject(),
                                      actualEle.getAsJsonObject());
      } else if (actualEle.isJsonArray()) {
        result =
            this.walkJsonArray(path, templateEle.getAsJsonArray(), actualEle.getAsJsonArray());
      }
    }

    return result;
  }

  /**
   * Performs a minimal, shallow comparison of the two given JSON elements.
   */
  private JsonComparatorResult shallowCompareJsonElements(String path, JsonElement expected,
                                                          JsonElement actual) {
    JsonComparatorResult result;

    if (expected.isJsonObject()) {
      if (actual.isJsonObject()) {
        return new JsonComparatorResult(false, true, null);
      } else {
        return new JsonComparatorResult(false, false,
                                        "actual json at path " + path
                                        + " is not an object, but an object is expected");
      }
    } else if (expected.isJsonArray()) {
      if (actual.isJsonArray()) {
        return new JsonComparatorResult(false, true, null);
      } else {
        return new JsonComparatorResult(false, false,
                                        "actual json at path " + path
                                        + " is not an array, but an array is expected");
      }
    } else {
      if (expected.equals(actual)) {
        return new JsonComparatorResult(false, true, null);
      } else {
        return new JsonComparatorResult(false, false,
                                        "primitive mismatch at path " + path + ": actual=" + actual
                                        + "; expected=" + expected);
      }
    }
  }

  /**
   * Walk all of the fields within the JSON objects given, comparing each.
   */
  private JsonComparatorResult walkJsonObjectFields(String pathToObject, JsonObject templateObj,
                                                    JsonObject actualObj) {

    boolean match = true;
    String errorMessage = null;

    //
    // Make sure the set of fields in both objects match.  If not, there's no need to continue to
    //  perform a deep comparison.
    //

    if (this.jsonObjectFieldSetsMatch(templateObj, actualObj)) {
      //
      // Iterate over all of the fields in the objects and compare each.
      //
      Iterator<Map.Entry<String, JsonElement>> entryIterator = actualObj.entrySet().iterator();

      while ((match) && (entryIterator.hasNext())) {
        Map.Entry<String, JsonElement> entry = entryIterator.next();

        String fieldPath = pathToObject + "['" + entry.getKey() + "']";

        JsonElement templateFieldEle = templateObj.get(entry.getKey());

        // Perform a deep comparison of the field values.
        JsonComparatorResult
            fieldResult =
            this.walkAndCompare(fieldPath, templateFieldEle, entry.getValue());

        match = fieldResult.isMatch();
        errorMessage = fieldResult.getErrorMessage();
      }
    } else {
      match = false;
      errorMessage = "object field sets do not match: path='" + pathToObject + "'";
    }

    return new JsonComparatorResult(true, match, errorMessage);
  }

  /**
   * Walk all of the fields within the JSON arrays given, comparing each.
   *
   * @param pathToArray path to the array elements being compared.
   * @param templateArr template, or expected, array.
   * @param actualArr   actual array.
   * @return result of the comparison indicating whether the JSON matches, and providing a cause
   * description when they do no match.
   */
  private JsonComparatorResult walkJsonArray(String pathToArray, JsonArray templateArr,
                                             JsonArray actualArr) {

    boolean match = true;
    String errorMessage = null;

    //
    // Make sure the arrays are the same size; otherwise, there's no need to check the contents.
    //
    if (templateArr.size() == actualArr.size()) {
      //
      // Loop over the array elements and compare each.
      //
      Iterator<JsonElement> actualArrayIterator = actualArr.iterator();
      Iterator<JsonElement> templateArrayIterator = templateArr.iterator();
      int position = 0;

      while ((match) && (actualArrayIterator.hasNext())) {
        JsonElement templateArrayEle = templateArrayIterator.next();
        JsonElement actualArrayEle = actualArrayIterator.next();

        String valuePath = pathToArray + "[" + position + "]";

        // Perform a deep comparison of the array entries.
        JsonComparatorResult
            childResult =
            this.walkAndCompare(valuePath, templateArrayEle, actualArrayEle);

        match = childResult.isMatch();
        errorMessage = childResult.getErrorMessage();

        position++;
      }
    } else {
      match = false;
      errorMessage =
          "array size mismatch: path='" + pathToArray + "'; actualSize=" + actualArr.size()
          + "; expectedSize=" + templateArr.size();
    }

    return new JsonComparatorResult(true, match, errorMessage);
  }

  /**
   * Determine whether the set of field names in the two given JSON objects are the same.
   */
  private boolean jsonObjectFieldSetsMatch(JsonObject first, JsonObject second) {
    Set<String>
        firstFieldNames =
        first.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());

    Set<String>
        secondFieldNames =
        second.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());

    return (firstFieldNames.equals(secondFieldNames));
  }

//========================================
// Internal Classes
//----------------------------------------

  /**
   * Comparator for use by rules when they need to perform deep comparisons.  This comparator allows
   * rules to continue to be applied without forcing every rule implementation to handle rules
   * themselves.
   */
  private class MyChildRuleComparator implements RuleChildComparator {

    @Override
    public JsonComparatorResult compare(String path, JsonElement templateEle,
                                        JsonElement actualEle) {

      return walkAndCompare(path, templateEle, actualEle);
    }
  }
}
