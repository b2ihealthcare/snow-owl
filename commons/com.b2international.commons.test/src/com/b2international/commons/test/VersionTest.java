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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.commons.Version;
import com.google.common.collect.ImmutableList;

/**
 * Unit test for {@link Version}.
 * 
 */
public class VersionTest {

	private static final String VALID_VERSION_STRING = "1.22.333.4444";
	private static final String INVALID_VERSION_STRING = "1.2-test";

	@Test
	public void testParseVersion() {
		Version parsedVersion = Version.parseVersion(VALID_VERSION_STRING);
		// assertions
		Version expectedVersion = new Version(ImmutableList.of(1, 22, 333, 4444));
		assertEquals(expectedVersion, parsedVersion);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testParseInvalidVersion() {
		Version.parseVersion(INVALID_VERSION_STRING);
	}
	
	@Test
	public void testIncrement() {
		Version version = new Version(ImmutableList.of(1,2,3));
		Version incrementedVersion = version.increment();
		// assertions
		Version expectedIncrementedVersion = new Version(ImmutableList.of(1,2,4));
		assertEquals(expectedIncrementedVersion, incrementedVersion);
	}
	
	@Test
	public void testCompareTo() {
		Version version1 = new Version(ImmutableList.of(1));
		Version version1_1 = new Version(ImmutableList.of(1,1));
		Version version2_1 = new Version(ImmutableList.of(2,1));
		
		// assertions
		assertTrue(version1.compareTo(version1) == 0);
		assertTrue(version1.compareTo(version1_1) < 0);
		assertTrue(version1.compareTo(version2_1) < 0);
		assertTrue(version1_1.compareTo(version2_1) < 0);
		assertTrue(version1_1.compareTo(version1) > 0);
		assertTrue(version2_1.compareTo(version1) > 0);
		assertTrue(version2_1.compareTo(version1_1) > 0);
	}
}