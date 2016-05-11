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

package com.savoirtech.json.rules;

import com.google.gson.JsonElement;

import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

/**
 * Compiled rule that maintains the rule processor, which is typically a singleton in practice,
 * together with the rule specification, which varies with every use.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorCompiledRule {

  private final JsonComparatorRule rule;
  private final JsonComparatorRuleSpecification specification;

  public JsonComparatorCompiledRule(JsonComparatorRule rule,
                                    JsonComparatorRuleSpecification specification) {
    this.rule = rule;
    this.specification = specification;
  }

  public JsonComparatorRule getRule() {
    return rule;
  }

  public JsonComparatorRuleSpecification getSpecification() {
    return specification;
  }

  public JsonComparatorResult compare(String path, JsonElement templateEle,
                                          JsonElement actualEle,
                                          RuleChildComparator childComparator) {

    return this.rule.compare(path, templateEle, actualEle, this.specification, childComparator);
  }
}
