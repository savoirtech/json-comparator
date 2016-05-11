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

package com.savoirtech.json.model;

import com.google.gson.JsonElement;

/**
 * Full specification of rules and template JSON for a JSON comparator.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparatorSpecification {

  /**
   * Rules to apply to the comparison, each given with a selector defining to which JSON paths
   * each rule applies.
   */
  private JsonComparatorRuleSpecification[] rules;

  /**
   * Template, expected, JSON.  When no rules are defined for a path, the template JSON must
   * match the actual JSON exactly.
   */
  private JsonElement templateJson;

//========================================
// Getters and Setters
//----------------------------------------

  public JsonComparatorRuleSpecification[] getRules() {
    return rules;
  }

  public void setRules(JsonComparatorRuleSpecification[] rules) {
    this.rules = rules;
  }

  public JsonElement getTemplateJson() {
    return templateJson;
  }

  public void setTemplateJson(JsonElement templateJson) {
    this.templateJson = templateJson;
  }
}
