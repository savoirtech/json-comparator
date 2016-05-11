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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verify operation of the JsonComparatorSelector.
 *
 * Created by art on 5/10/16.
 */
public class JsonComparatorSelectorTest {

  private JsonComparatorSelector selector;

  /**
   * Setup common test data and interactions.
   */
  @Before
  public void setupTest() throws Exception {
    this.selector = new JsonComparatorSelector();
  }

  /**
   * Verify operation of the getter and setter for path.
   */
  @Test
  public void testGetSetPath() throws Exception {
    assertNull(this.selector.getPath());

    this.selector.setPath("x-path-x");
    assertEquals("x-path-x", this.selector.getPath());
  }

  /**
   * Verify operation of the equals method on selectors with equivalent paths.
   */
  @Test
  public void testEqualsPositive() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorSelector otherSelector = new JsonComparatorSelector();
    otherSelector.setPath("x-path-x");

    //
    // Execute
    //
    this.selector.setPath("x-path-x");
    boolean result = this.selector.equals(otherSelector);

    //
    // Verify
    //
    assertTrue(result);
  }

  /**
   * Verify operation of the equals method with selector paths that are not equivalent.
   */
  @Test
  public void testEqualsNegativePathDifference() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorSelector otherSelector = new JsonComparatorSelector();
    otherSelector.setPath("x-path2-x");

    //
    // Execute
    //
    this.selector.setPath("x-path1-x");
    boolean result = this.selector.equals(otherSelector);

    //
    // Verify
    //
    assertFalse(result);
  }

  /**
   * Verify operation of the equals method when the other path is null and "this" one is not.
   */
  @Test
  public void testEqualsNegativeOtherPathNull() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorSelector otherSelector = new JsonComparatorSelector();
    otherSelector.setPath(null);

    //
    // Execute
    //
    this.selector.setPath("x-path1-x");
    boolean result = this.selector.equals(otherSelector);

    //
    // Verify
    //
    assertFalse(result);
  }

  /**
   * Verify operation of the equals method when "this" path is null and the other is not.
   */
  @Test
  public void testEqualsNegativeThisPathNull() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorSelector otherSelector = new JsonComparatorSelector();
    otherSelector.setPath("x-path-x");

    //
    // Execute
    //
    this.selector.setPath(null);
    boolean result = this.selector.equals(otherSelector);

    //
    // Verify
    //
    assertFalse(result);
  }

  /**
   * Verify operation of the equals method when both paths are null.
   */
  @Test
  public void testEqualsPositiveNullPaths() throws Exception {
    //
    // Setup test data and interactions
    //
    JsonComparatorSelector otherSelector = new JsonComparatorSelector();
    otherSelector.setPath(null);

    //
    // Execute
    //
    this.selector.setPath(null);
    boolean result = this.selector.equals(otherSelector);

    //
    // Verify
    //
    assertTrue(result);
  }

  /**
   * Verify operation of the equals method when the other object is of the wrong class.
   */
  @Test
  public void testEqualsNegativeWrongClass() throws Exception {
    //
    // Execute
    //
    this.selector.setPath(null);
    boolean result = this.selector.equals("something-else");

    //
    // Verify
    //
    assertFalse(result);
  }

  /**
   * Verify operation of the equals method when the other object is null.
   */
  @Test
  public void testEqualsNegativeNullOther() throws Exception {
    //
    // Execute
    //
    this.selector.setPath(null);
    boolean result = this.selector.equals(null);

    //
    // Verify
    //
    assertFalse(result);
  }

  /**
   * Verify operation of the equals method when the other object is the same one as "this".
   */
  @Test
  public void testEqualsPositiveSameObject() throws Exception {
    //
    // Execute
    //
    this.selector.setPath(null);
    boolean result = this.selector.equals(this.selector);

    //
    // Verify
    //
    assertTrue(result);
  }

  /**
   * Verify operation of the hashCode method.
   */
  @Test
  public void testHashCode() throws Exception {
    this.selector.setPath(null);
    assertEquals(0, this.selector.hashCode());

    this.selector.setPath("x-path-x");
    assertEquals("x-path-x".hashCode(), this.selector.hashCode());
  }
}
