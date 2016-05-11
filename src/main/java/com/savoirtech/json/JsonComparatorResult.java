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

package com.savoirtech.json;

/**
 * Result of a JSON comparison.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorResult {
  private final boolean deep;
  private final boolean match;
  private final String errorMessage;

//========================================
// Constructor
//----------------------------------------

  public JsonComparatorResult(boolean deep, boolean match, String errorMessage) {
    this.deep = deep;
    this.match = match;
    this.errorMessage = errorMessage;
  }

//========================================
// Getters
//----------------------------------------

  /**
   * Determine whether the comparison was a deep comparison.  When rules return results, this flag
   * indicates to the comparator whether it needs to walk into objects and arrays.  If the result
   * is a deep comparison, the comparator will not walk into objects and arrays itself.
   *
   * @return true => comparison was deep; false => comparison was shallow.
   */
  public boolean isDeep() {
    return deep;
  }

  /**
   * Determine whether the JSON matched.
   *
   * @return true => the JSON matched; false => the JSON did not match.
   */
  public boolean isMatch() {
    return match;
  }

  /**
   * Obtain a description of failure.
   *
   * @return text describing the failure, if known; null otherwise.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
}
