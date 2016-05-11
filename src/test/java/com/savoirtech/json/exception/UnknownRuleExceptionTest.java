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

package com.savoirtech.json.exception;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Verify the operation of the UnknownRuleException.
 *
 * Created by art on 5/11/16.
 */
public class UnknownRuleExceptionTest {

  @Test
  public void testUnknownRuleExceptionMessage() {
    Exception exc = new UnknownRuleException("x-rule-x");
    assertEquals("unknown rule action \"x-rule-x\"", exc.getMessage());
  }
}