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

package com.savoirtech.json.processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.jayway.jsonpath.Configuration;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Verify operation of the json comparison processor factory.
 *
 * Created by art on 5/11/16.
 */
public class JsonComparisonProcessorFactoryTest {

  private JsonComparisonProcessorFactory factory;

  private Configuration jsonPathConfiguration;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.jsonPathConfiguration = Mockito.mock(Configuration.class);

    this.factory = new JsonComparisonProcessorFactory(this.jsonPathConfiguration);
  }

  /**
   * Verify operation of the createProcessor method.
   */
  @Test
  public void testCreateProcessor() throws Exception {
    JsonComparatorRuleSpecification[] rules = new JsonComparatorRuleSpecification[0];

    JsonElement templateEle1 = new JsonPrimitive("x-template-ele1-x");
    JsonElement actualEle1 = new JsonPrimitive("x-actual-ele1-x");
    JsonComparisonProcessor processor1a;
    JsonComparisonProcessor processor1b;
    processor1a = this.factory.createProcessor(templateEle1, rules, actualEle1);
    processor1b = this.factory.createProcessor(templateEle1, rules, actualEle1);

    assertNotNull(processor1a);
    assertNotNull(processor1b);
    assertNotSame(processor1a, processor1b);

    JsonElement templateEle2 = new JsonPrimitive("x-template-ele2-x");
    JsonElement actualEle2 = new JsonPrimitive("x-actual-ele2-x");
    JsonComparisonProcessor processor2;
    processor2 = this.factory.createProcessor(templateEle2, rules, actualEle2);
    assertNotNull(processor2);
    assertNotSame(processor1a, processor2);
    assertNotSame(processor1b, processor2);
  }
}