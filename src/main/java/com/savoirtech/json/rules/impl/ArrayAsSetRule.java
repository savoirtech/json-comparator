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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.JsonComparatorRule;
import com.savoirtech.json.JsonComparatorResult;
import com.savoirtech.json.rules.RuleChildComparator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Rule that compares JSON array entries as sets instead of ordered arrays.  In other words, the
 * ordering of elements may change, but every actual element must match one expected element,
 * and visa versa.
 *
 * Created by art on 5/10/16.
 */
public class ArrayAsSetRule implements JsonComparatorRule {

//========================================
// Public API
//----------------------------------------

  @Override
  public JsonComparatorResult compare(String path, JsonElement templateElement,
                                      JsonElement actualElement,
                                      JsonComparatorRuleSpecification specification,
                                      RuleChildComparator childComparator) {

    JsonComparatorResult result;

    if (actualElement.isJsonArray()) {
      if (templateElement.isJsonArray()) {
        result =
            this.compareArraysAsSets(path, templateElement.getAsJsonArray(),
                                     actualElement.getAsJsonArray(), childComparator);
      } else {
        result =
            new JsonComparatorResult(true, false,
                                     "set rule on non-array template element at path " + path, path);
      }
    } else {
      result =
          new JsonComparatorResult(true, false,
                                   "set rule on non-array element at path " + path, path);
    }

    return result;
  }

//========================================
// Internal Methods
//----------------------------------------

  private JsonComparatorResult compareArraysAsSets(String path, JsonArray expectedArray,
                                                   JsonArray actualArray,
                                                   RuleChildComparator childComparator) {

    boolean matches = true;
    String errorMessage = null;
    String errorPath = null;

    // First simply check the size; if they don't match, the sets cannot be equivalent.
    if (expectedArray.size() != actualArray.size()) {
      errorMessage =
          "set comparison: sizes do not match at path " + path +
          ": expectedCount=" + expectedArray.size() + "; actualCount=" + actualArray.size();

      return new JsonComparatorResult(true, false, errorMessage, path);
    }


    //
    // For each actual value, find an expected value that matches.  Then remove the expected value
    //  from the remaining set of expected values, so each is only matched once.
    //
    Set<JsonElement> remainingSet = new HashSet<JsonElement>();
    expectedArray.iterator().forEachRemaining(remainingSet::add);

    Iterator<JsonElement> actualElementIterator = actualArray.iterator();
    int position = 0;

    while ((matches) && (!remainingSet.isEmpty())) {
      JsonElement nextActual = actualElementIterator.next();

      //
      // Check whether this current actual element matches any in the remaining template set.
      //
      String accessor = "[" + position + "]";
      String childPath = path + accessor;

      JsonElement matchingEle = this.compareOneSetEle(childPath, nextActual, remainingSet, childComparator);

      //
      // If matched, remove the matched element from the remaining set so it won't be matched again.
      //  Otherwise, the comparison is a failure.
      //
      if (matchingEle != null) {
        remainingSet.remove(matchingEle);
      } else {
        matches = false;
        errorMessage = "set comparison: failed to find match for path " + childPath;
        errorPath = childPath;
      }

      position++;
    }

    return new JsonComparatorResult(true, matches, errorMessage, errorPath);
  }

  /**
   * Compare one element expected in a set against all the possible expected values.
   *
   * @param childPath
   * @param actual
   * @param remainingSet
   * @param childComparator
   * @return
   */
  private JsonElement compareOneSetEle(String childPath, JsonElement actual, Set<JsonElement> remainingSet, RuleChildComparator childComparator) {
    boolean matches = false;

    //
    // Loop until either an expected element matches the actual one given, or no more expected
    //  elements exist.
    //
    Iterator<JsonElement> remainingIterator = remainingSet.iterator();
    JsonElement matchingEle = null;

    while ((!matches) && (remainingIterator.hasNext())) {
      JsonElement expectedChild = remainingIterator.next();

      //
      // Compare these elements using the child comparator, which will continue to perform a
      //  deep comparison of each.
      //
      JsonComparatorResult childResult = childComparator.compare(childPath, expectedChild, actual);

      matches = childResult.isMatch();

      // If it matched, remove this expected child from the remaining set.
      if (childResult.isMatch()) {
        matchingEle = expectedChild;
      }
    }

    return matchingEle;
  }
}
