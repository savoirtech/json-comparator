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

import com.jayway.jsonpath.Configuration;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

/**
 *
 * Created by art on 5/9/16.
 */
public class JsonComparisonProcessorFactory {

  private final Configuration jsonPathConfiguration;

  public JsonComparisonProcessorFactory(Configuration jsonPathConfiguration) {
    this.jsonPathConfiguration = jsonPathConfiguration;
  }

  public JsonComparisonProcessor createProcessor(JsonElement templateJson,
                                                 JsonComparatorRuleSpecification[] rules,
                                                 JsonElement actualJson) {

    return new JsonComparisonProcessor(this.jsonPathConfiguration, templateJson, rules, actualJson);
  }
}
