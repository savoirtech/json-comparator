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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verify the operation of the JsonComparatorResult.
 *
 * Created by art on 5/11/16.
 */
public class JsonComparatorResultTest {

  /**
   * Verify operation of the isDeep method.
   */
  @Test
  public void testIsDeep() throws Exception {
    JsonComparatorResult result1 = new JsonComparatorResult(false, true, null);
    assertFalse(result1.isDeep());

    JsonComparatorResult result2 = new JsonComparatorResult(true, false, null);
    assertTrue(result2.isDeep());
  }

  /**
   * Verify operation of the isMatch method.
   */
  @Test
  public void testIsMatch() throws Exception {
    JsonComparatorResult result1 = new JsonComparatorResult(false, true, null);
    assertTrue(result1.isMatch());

    JsonComparatorResult result2 = new JsonComparatorResult(true, false, null);
    assertFalse(result2.isMatch());
  }

  /**
   * Verify operation of the getErrorMessage method.
   */
  @Test
  public void testGetErrorMessage() throws Exception {
    JsonComparatorResult result1 = new JsonComparatorResult(true, true, null);
    assertNull(result1.getErrorMessage());

    JsonComparatorResult result2 = new JsonComparatorResult(true, true, "x-error-message-x");
    assertEquals("x-error-message-x", result2.getErrorMessage());
  }
}