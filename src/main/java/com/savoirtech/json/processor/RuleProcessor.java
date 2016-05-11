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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.savoirtech.json.model.JsonComparatorRuleSpecification;
import com.savoirtech.json.rules.JsonComparatorCompiledRule;
import com.savoirtech.json.rules.RuleCompiler;
import com.savoirtech.json.rules.RuleRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor of rules that compiles the needed information from rule specifications and answers the
 * question of whether a specific path matches any of the rules.
 *
 * Note that this class acts as a bridge between the JsonPath implementation and the comparator: the
 * JsonPath implementation does not have a means to ask, "does -this- path match the JsonPath?", but
 * instead only provides a means to locate values or paths from actual JSON given a JsonPath.  So
 * the JsonPath values are matched to the actual JSON in order to locate the paths to which each
 * rule will apply, and the results are cached so the comparator can find them during its walk of
 * the JSON.
 *
 * Created by art on 5/10/16.
 */
public class RuleProcessor {

  private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(RuleProcessor.class);

  private Logger log = DEFAULT_LOGGER;

  private final Configuration jsonPathConfiguration;

  private final JsonComparatorRuleSpecification[] rules;
  private final Map<String, JsonComparatorCompiledRule> rulePathMap;

  private final JsonElement actualJson;

  private RuleCompiler ruleCompiler;

  public RuleProcessor(Configuration jsonPathConfiguration,
                       JsonComparatorRuleSpecification[] rules, JsonElement actualJson) {

    this.jsonPathConfiguration = jsonPathConfiguration;
    this.rules = rules;
    this.actualJson = actualJson;

    this.rulePathMap = new HashMap<>();

    RuleRegistry ruleRegistry = new RuleRegistry();
    ruleRegistry.initBuiltInRules();
    this.ruleCompiler = new RuleCompiler(ruleRegistry);
  }

//========================================
//  Getters and Setters
//========================================

  public Logger getLog() {
    return log;
  }

  public void setLog(Logger log) {
    this.log = log;
  }

  public RuleCompiler getRuleCompiler() {
    return ruleCompiler;
  }

  public void setRuleCompiler(RuleCompiler ruleCompiler) {
    this.ruleCompiler = ruleCompiler;
  }

//========================================
// API Methods
//========================================

  public void init() {
    this.compileRules();
  }

  public JsonComparatorCompiledRule findMatchingRule(String path) {
    return this.rulePathMap.get(path);
  }

//========================================
// Internal Methods
//========================================

  private void compileRules() {
    this.log.debug("compiling rules");

    if (this.rules != null) {
      for (JsonComparatorRuleSpecification oneRuleSpecification : this.rules) {
        String rulePathSelector = oneRuleSpecification.getSelector().getPath();

        this.log.debug("compiling rule for path selector {}", rulePathSelector);

        JsonPath rulePath = JsonPath.compile(rulePathSelector);

        try {
          //
          // Find all of the paths matched by the jsonPath from the actual JSON; note that it would
          //  be preferable to simply ask jsonPath, "does the current path match?" while walking
          //  the actual json, but there is no such operation.
          //
          JsonArray paths = rulePath.read(this.actualJson, this.jsonPathConfiguration);

          // Compile the rule and save it in the map as the rule for each matched path
          JsonComparatorCompiledRule compiledRule = this.ruleCompiler.compile(oneRuleSpecification);
          paths.forEach((path) -> this.rulePathMap.put(path.getAsString(), compiledRule));

          this.log.trace("rule for path selector {} matched {}", rulePathSelector, paths);
        } catch (PathNotFoundException exc) {
          //
          // No paths matched.
          //
          this.log.trace("rule for path selector {} did not match any paths", rulePathSelector);
        }
      }
    }

    this.log.debug("done compiling rules");
  }
}
