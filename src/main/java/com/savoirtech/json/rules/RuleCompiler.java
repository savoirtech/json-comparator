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

import com.savoirtech.json.exception.UnknownRuleException;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

/**
 * Compiler of rule specifications into JsonComparatorCompiledRule objects.
 *
 * Created by art on 5/10/16.
 */
public class RuleCompiler {

  private final RuleRegistry registry;
  private JsonComparatorCompiledRuleFactory
      compiledRuleFactory =
      new JsonComparatorCompiledRuleFactory();

  public RuleCompiler(RuleRegistry registry) {
    this.registry = registry;
  }

//========================================
// Getters and Setters
//----------------------------------------

  public JsonComparatorCompiledRuleFactory getCompiledRuleFactory() {
    return compiledRuleFactory;
  }

  public void setCompiledRuleFactory(
      JsonComparatorCompiledRuleFactory compiledRuleFactory) {
    this.compiledRuleFactory = compiledRuleFactory;
  }

//========================================
// Public API
//----------------------------------------

  public JsonComparatorCompiledRule compile(JsonComparatorRuleSpecification ruleSpecification) {
    String action = ruleSpecification.getAction();

    JsonComparatorRule result = this.registry.lookupRule(action);

    if (result == null) {
      throw new UnknownRuleException(action);
    }

    return this.compiledRuleFactory.create(result, ruleSpecification);
  }
}
