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

/**
 * Specification of a rule as input to the comparator.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparatorRuleSpecification {
  private JsonComparatorSelector selector;

  /**
   * Action to take when this rule matches.  Must exist in the rules registry.
   */
  private String action;

  /**
   * Pattern to apply for rules which use patterns, such as the regex matching rule.
   */
  private String pattern;

//========================================
// Getters and Setters
//----------------------------------------

  public JsonComparatorSelector getSelector() {
    return selector;
  }

  public void setSelector(JsonComparatorSelector selector) {
    this.selector = selector;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

//========================================
// Equals and Hash Code
//----------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    JsonComparatorRuleSpecification that = (JsonComparatorRuleSpecification) o;

    if (selector != null ? !selector.equals(that.selector) : that.selector != null) {
      return false;
    }
    if (action != null ? !action.equals(that.action) : that.action != null) {
      return false;
    }
    return !(pattern != null ? !pattern.equals(that.pattern) : that.pattern != null);

  }

  @Override
  public int hashCode() {
    int result = selector != null ? selector.hashCode() : 0;
    result = 31 * result + (action != null ? action.hashCode() : 0);
    result = 31 * result + (pattern != null ? pattern.hashCode() : 0);
    return result;
  }
}
