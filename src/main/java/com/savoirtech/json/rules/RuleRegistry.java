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

import com.savoirtech.json.rules.impl.ArrayAsSetRule;
import com.savoirtech.json.rules.impl.RegexMatchingRule;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of rules, keyed by action, available for use by the comparator.
 *
 * Created by art on 5/10/16.
 */
public class RuleRegistry {
  private final Map<String, JsonComparatorRule> rules = new HashMap<>();

  public void initBuiltInRules() {
    this.registerRule("matches", new RegexMatchingRule());
    this.registerRule("set", new ArrayAsSetRule());
  }

  public void registerRule(String action, JsonComparatorRule rule) {
    this.rules.put(action, rule);
  }

  public JsonComparatorRule lookupRule(String action) {
    return this.rules.get(action);
  }
}
