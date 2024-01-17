/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons.test;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.VersionNumberComparator;

public class VersionNumberComparatorTest {

	private VersionNumberComparator comparator;

	@Before
	public void init() {
		comparator = new VersionNumberComparator();
	}
	
	@Test
	public void test1() {
		int result = comparator.compare("0.0.1", "0.0.2");
		assertTrue(result < 0);
	}
	
	@Test
	public void test2() {
		int result = comparator.compare("0.0.1", "0.0.1");
		assertTrue(result == 0);
	}
	
	@Test
	public void test3() {
		int result = comparator.compare("0.0.2", "0.0.1");
		assertTrue(result > 0);
	}
	
	@Test
	public void test4() {
		int result = comparator.compare("0.1", "0.1.1");
		assertTrue(result < 0);
	}
	
	@Test
	public void test5() {
		int result = comparator.compare("1.0.0", "2.0.0");
		assertTrue(result < 0);
	}
	
	@Test
	public void test6() {
		int result = comparator.compare("1.0.0", "1.0.2.3.4.5");
		assertTrue(result < 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidInput1() {
		comparator.compare("a", "0.1.2");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidInput2() {
		comparator.compare("0.1.2", "a");
	}
	
}