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

package com.savoirtech.json.rules.impl;

import com.google.gson.JsonElement;

import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.JsonComparatorRule;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.rules.RuleChildComparator;

/**
 * Rule that compares a value against a regular expression.  The value should be a JSON primitive,
 * although objects and arrays are converted to string form and compared.
 *
 * Created by art on 5/10/16.
 */
public class RegexMatchingRule implements JsonComparatorRule {

  @Override
  public JsonComparatorResult compare(String path, JsonElement templateElement,
                                          JsonElement actualElement,
                                          JsonComparatorRuleSpecification specification,
                                          RuleChildComparator childComparator) {

    boolean matches;
    String errorMessage = null;

    String value = this.getStringForComparison(actualElement);

    matches = value.matches(specification.getPattern());

    if (!matches) {
      errorMessage =
          "value at path " + path + " does not match '" + specification.getPattern() + "': value="
          + value;
    }

    return new JsonComparatorResult(false, matches, errorMessage);
  }


//========================================
// Internal Methods
//========================================

  private String getStringForComparison(JsonElement ele) {
    if (ele.isJsonPrimitive()) {
      return ele.getAsString();
    }

    return ele.toString();
  }
}
