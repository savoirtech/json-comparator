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

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Integrated test of the JsonComparator.
 *
 * Created by art on 5/4/16.
 */
public class JsonComparatorIT {

  private JsonComparator comparator;

  @Before
  public void setupTest() {
    this.comparator = new JsonComparatorBuilder().build();
  }

  @Test
  public void testCompare01() throws Exception {
    String actual01 = IOUtils.toString(JsonComparatorIT.class.getResourceAsStream("spec01.json"));
    String rules01 = IOUtils.toString(JsonComparatorIT.class.getResourceAsStream("rules01.json"));

    JsonComparatorResult result = this.comparator.compare(rules01, actual01);

    assertTrue("expect match; error=" + result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorPath());
  }

  @Test
  public void testReadmeExample01() {

    JsonComparator comparator = new JsonComparatorBuilder().build();

    String comparisonSpec = "{ \"templateJson\": [ 1, 2, 3 ] }";
    String actualJson = "[ 1, 2, 3 ]";

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertTrue(result.isMatch());

    // Same thing, except include the error message on mismatches:
    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorPath());
  }

  @Test
  public void testReadmeExample02() {
    JsonComparator comparator = new JsonComparatorBuilder().build();

    String
        comparisonSpec =
        "{ \"rules\": [ { \"selector\": { \"path\": \"$[2]\" }, \"action\": \"matches\", \"pattern\": \"[3-5]\" } ], \"templateJson\": [ 1, 2, 3 ] }";
    String actualJson = "[ 1, 2, 4 ]";

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertTrue(result.getErrorMessage(), result.isMatch());
    assertNull(result.getErrorPath());
  }

  @Test
  public void testFailureCase() {
    JsonComparator comparator = new JsonComparatorBuilder().build();

    String
        comparisonSpec =
        "{ \"rules\": [ { \"selector\": { \"path\": \"$[2]\" }, \"action\": \"matches\", \"pattern\": \"[3-5]\" } ], \"templateJson\": [ 2, 4, 6 ] }";
    String actualJson = "[ 2, 4, 6 ]";

    JsonComparatorResult result = comparator.compare(comparisonSpec, actualJson);

    assertFalse(result.isMatch());
    assertEquals("value at path $[2] does not match '[3-5]': value=6", result.getErrorMessage());
    assertEquals("$[2]", result.getErrorPath());
  }
}
