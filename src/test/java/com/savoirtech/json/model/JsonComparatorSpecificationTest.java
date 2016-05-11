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
import com.google.gson.JsonPrimitive;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparatorSpecification.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorSpecificationTest {

  private JsonComparatorSpecification specification;

  private JsonComparatorRuleSpecification[] rules;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.specification = new JsonComparatorSpecification();

    this.rules = new JsonComparatorRuleSpecification[0];
  }

  /**
   * Verify operation of the getter and setter for rules.
   */
  @Test
  public void testGetSetRules() throws Exception {
    assertNull(this.specification.getRules());

    this.specification.setRules(this.rules);
    assertSame(this.rules, this.specification.getRules());
  }

  /**
   * Verify operation of the getter and setter for the template json.
   */
  @Test
  public void testGetSetTemplateJson() throws Exception {
    assertNull(this.specification.getTemplateJson());

    JsonElement templateJson = new JsonPrimitive("x-template-x");
    this.specification.setTemplateJson(templateJson);

    assertSame(templateJson, this.specification.getTemplateJson());
  }
}