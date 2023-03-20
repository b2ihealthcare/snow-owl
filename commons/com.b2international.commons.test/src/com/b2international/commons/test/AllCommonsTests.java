/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.commons.test.collect.*;
import com.b2international.commons.test.config.ConfigurationFactoryTest;
import com.b2international.commons.test.io.PathUtilsTests;

/**
 * @since 2.5
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	ArabicToRomanNumeralConverterTest.class, 
	VersionNumberComparatorTest.class, 
	VersionTest.class, 
	ConfigurationFactoryTest.class, 
	BitSetTest.class,
	ByteOpenHashSetTest.class,
	IntOpenHashSetTest.class,
	LongOpenHashSetTest.class,
	EmptyLongListTest.class,
	PathUtilsTests.class
})
public class AllCommonsTests {
	// Empty class body
}
