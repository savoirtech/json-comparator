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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.model.JsonComparatorSpecification;
import com.savoirtech.json.processor.RuleProcessor;
import com.savoirtech.json.rules.JsonComparatorCompiledRule;
import com.savoirtech.json.util.model.JsonComparatorResultDetails;

/**
 * Utilities for json comparator.  Use JsonComparatorBuilder.buildUtil() to construct properly
 * initialized instances.
 *
 * Created by art on 5/13/16.
 */
public class JsonComparatorUtil {

  private Gson gson;

  private Configuration jsonPathConfiguration;

//========================================
// Getters and Setters
//----------------------------------------

  public Gson getGson() {
    return gson;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public Configuration getJsonPathConfiguration() {
    return jsonPathConfiguration;
  }

  public void setJsonPathConfiguration(Configuration jsonPathConfiguration) {
    this.jsonPathConfiguration = jsonPathConfiguration;
  }

//========================================
// Public API
//----------------------------------------

  public JsonComparatorResultDetails extractFailureDetails(String comparisonSpec,
                                                           String actualJsonString,
                                                           JsonComparatorResult comparisonResult) {

    if (comparisonResult.getErrorPath() == null) {
      return null;
    }

    JsonComparatorSpecification comparatorSpecification = this.compileSpecification(comparisonSpec);
    JsonElement expectedJsonEle = comparatorSpecification.getTemplateJson();

    JsonParser parser = new JsonParser();
    JsonElement actualJsonEle = parser.parse(actualJsonString);

    JsonPath path = JsonPath.compile(comparisonResult.getErrorPath());

    Configuration jsonPathConfig = Configuration.builder()
        .jsonProvider(new GsonJsonProvider(this.gson))
        .build();

    //
    // Read and return actual
    //
    JsonElement actualEle = path.read(actualJsonEle, jsonPathConfig);

    //
    // Read and return expected
    //
    JsonElement templateEle = path.read(expectedJsonEle, jsonPathConfig);

    //
    // Find the rule that applies, if any, and return that as well
    //
    JsonComparatorCompiledRule
        rule =
        this.locateApplicableRule(comparatorSpecification.getRules(), actualJsonEle,
                                  comparisonResult.getErrorPath());

    return new JsonComparatorResultDetails(actualEle, templateEle, rule);
  }

//========================================
// Internal Methods
//----------------------------------------

  private JsonComparatorCompiledRule locateApplicableRule(JsonComparatorRuleSpecification[] rules,
                                                          JsonElement actualJson, String path) {

    RuleProcessor ruleProcessor = new RuleProcessor(jsonPathConfiguration, rules, actualJson);

    ruleProcessor.init();

    return ruleProcessor.findMatchingRule(path);
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
