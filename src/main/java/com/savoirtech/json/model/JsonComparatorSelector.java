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
 * Selector for a JSON comparator rule.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparatorSelector {

  /**
   * Path, or path pattern, within the JSON to which the rule applies.
   */
  private String path;

//========================================
// Getters and Setters
//----------------------------------------

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
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

    JsonComparatorSelector that = (JsonComparatorSelector) o;

    return !(path != null ? !path.equals(that.path) : that.path != null);

  }

  @Override
  public int hashCode() {
    return path != null ? path.hashCode() : 0;
  }
}
