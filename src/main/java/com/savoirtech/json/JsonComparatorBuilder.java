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
import com.google.gson.GsonBuilder;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.savoirtech.json.processor.JsonComparisonProcessorFactory;

/**
 * Build a JsonComparator using the fluent builder pattern.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorBuilder {

  private Gson gson;
  private Configuration jsonPathConfiguration;
  private JsonComparisonProcessorFactory jsonComparisonProcessorFactory;

//========================================
// Fluent Methods
//----------------------------------------

  public JsonComparatorBuilder withGson(Gson gson) {
    this.gson = gson;
    return this;
  }

  public JsonComparatorBuilder withJsonPathConfiguration(Configuration jsonPathConfiguration) {
    this.jsonPathConfiguration = jsonPathConfiguration;
    return this;
  }

  public JsonComparatorBuilder withJsonComparisonProcessorFactory(
      JsonComparisonProcessorFactory factory) {
    this.jsonComparisonProcessorFactory = factory;
    return this;
  }

//========================================
// Builder
//----------------------------------------

  public JsonComparator build() {
    JsonComparator result = new JsonComparator();

    if (this.gson == null) {
      this.gson = new GsonBuilder().create();
    }

    if (this.jsonPathConfiguration == null) {
      this.jsonPathConfiguration = buildDefaultConfiguration();
    }

    if (this.jsonComparisonProcessorFactory == null) {
      this.jsonComparisonProcessorFactory =
          new JsonComparisonProcessorFactory(this.jsonPathConfiguration);
    }

    result.setGson(this.gson);
    result.setJsonComparisonProcessorFactory(this.jsonComparisonProcessorFactory);

    return result;
  }

  private Configuration buildDefaultConfiguration() {
    //
    // Prepare the configuration to use with the jsonPath library.
    //
    return Configuration.builder().jsonProvider(
        new GsonJsonProvider(gson))
        .options(
            Option.AS_PATH_LIST,
            Option.ALWAYS_RETURN_LIST)
        .build();
  }
}
