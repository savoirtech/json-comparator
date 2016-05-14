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

import com.google.gson.Gson;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.savoirtech.json.processor.JsonComparisonProcessorFactory;
import com.savoirtech.json.util.JsonComparatorUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparatorBuilder.
 *
 * Created by art on 5/11/16.
 */
public class JsonComparatorBuilderTest {

  private JsonComparatorBuilder builder;

  private Gson gson;
  private Configuration mockJsonPathConfiguration;
  private JsonComparisonProcessorFactory mockProcessorFactory;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    builder = new JsonComparatorBuilder();

    this.gson = new Gson();
    this.mockJsonPathConfiguration = Mockito.mock(Configuration.class);
    this.mockProcessorFactory = Mockito.mock(JsonComparisonProcessorFactory.class);
  }

  /**
   * Verify operation of the witGson() fluent builder method.
   */
  @Test
  public void testWithGson() throws Exception {
    //
    // Execute
    //
    JsonComparatorBuilder result = this.builder.withGson(this.gson);
    JsonComparator comparator = this.builder.build();
    JsonComparatorUtil util = this.builder.buildUtil();

    //
    // Verify
    //
    assertSame(result, this.builder);
    assertSame(this.gson, comparator.getGson());
    assertSame(this.gson, util.getGson());
  }

  /**
   * Verify operation of the withJsonPathConfiguration() fluent builder method.
   */
  @Test
  public void testWithJsonPathConfiguration() throws Exception {
    //
    // Execute
    //
    JsonComparatorBuilder result;
    JsonComparator comparator;

    result = this.builder.withJsonPathConfiguration(this.mockJsonPathConfiguration);
    comparator = this.builder.build();
    JsonComparatorUtil util = this.builder.buildUtil();

    //
    // Verify
    //
    assertSame(result, this.builder);

    Configuration
        actualJsonPathConfig =
        (Configuration) Whitebox.getInternalState(comparator.getJsonComparisonProcessorFactory(),
                                                  "jsonPathConfiguration");

    assertSame(this.mockJsonPathConfiguration, actualJsonPathConfig);
    assertSame(this.mockJsonPathConfiguration, util.getJsonPathConfiguration());
  }

  /**
   * Verify operation of the withJsonComparisonProcessorFactory fluent builder method.
   */
  @Test
  public void testWithJsonComparisonProcessorFactory() throws Exception {
    //
    // Setup test data and interactions
    //

    //
    // Execute
    //
    JsonComparatorBuilder result;
    JsonComparator comparator;

    result = this.builder.withJsonComparisonProcessorFactory(this.mockProcessorFactory);
    comparator = this.builder.build();


    //
    // Verify
    //
    assertSame(result, this.builder);
    assertSame(this.mockProcessorFactory, comparator.getJsonComparisonProcessorFactory());
  }

  /**
   * Verify operation of the build method using all default values.
   */
  @Test
  public void testDefaultBuild() throws Exception {
    //
    // Setup test data and interactions
    //

    //
    // Execute
    //
    JsonComparator comparator = this.builder.build();
    JsonComparatorUtil util = this.builder.buildUtil();

    //
    // Verify
    //
    Configuration
        actualJsonPathConfig =
        (Configuration) Whitebox.getInternalState(comparator.getJsonComparisonProcessorFactory(),
                                                  "jsonPathConfiguration");

    assertTrue(actualJsonPathConfig.jsonProvider() instanceof GsonJsonProvider);
    assertTrue(actualJsonPathConfig.containsOption(Option.AS_PATH_LIST));
    assertTrue(actualJsonPathConfig.containsOption(Option.ALWAYS_RETURN_LIST));
    assertNotSame(this.gson, comparator.getGson());

    assertSame(actualJsonPathConfig, util.getJsonPathConfiguration());
  }
}