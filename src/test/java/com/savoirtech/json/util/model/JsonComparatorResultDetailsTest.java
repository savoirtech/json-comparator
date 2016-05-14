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
import com.google.gson.JsonPrimitive;

import com.savoirtech.json.rules.JsonComparatorCompiledRule;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of json comparator results detail.
 *
 * Created by art on 5/14/16.
 */
public class JsonComparatorResultDetailsTest {

  /**
   * Verify operation of the details object.
   */
  @Test
  public void testDetails() throws Exception {
    JsonElement actualEle = new JsonPrimitive("x-actual-x");
    JsonElement templateEle = new JsonPrimitive("x-template-x");
    JsonComparatorCompiledRule rule = Mockito.mock(JsonComparatorCompiledRule.class);

    JsonComparatorResultDetails
        details =
        new JsonComparatorResultDetails(actualEle, templateEle, rule);

    assertSame(actualEle, details.getActualElement());
    assertSame(templateEle, details.getTemplateElement());
    assertSame(rule, details.getMatchingRule());
  }
}