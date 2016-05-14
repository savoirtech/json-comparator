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

package com.savoirtech.json.util.model;

import com.google.gson.JsonElement;

import com.savoirtech.json.rules.JsonComparatorCompiledRule;

/**
 * Holder of detailed information related to the result of a json comparison.
 *
 * Created by art on 5/13/16.
 */
public class JsonComparatorResultDetails {
  private final JsonElement actualElement;
  private final JsonElement templateElement;
  private final JsonComparatorCompiledRule matchingRule;

  public JsonComparatorResultDetails(JsonElement actualElement,
                                     JsonElement templateElement,
                                     JsonComparatorCompiledRule matchingRule) {
    this.actualElement = actualElement;
    this.templateElement = templateElement;
    this.matchingRule = matchingRule;
  }

  public JsonElement getActualElement() {
    return actualElement;
  }

  public JsonElement getTemplateElement() {
    return templateElement;
  }

  public JsonComparatorCompiledRule getMatchingRule() {
    return matchingRule;
  }
}
