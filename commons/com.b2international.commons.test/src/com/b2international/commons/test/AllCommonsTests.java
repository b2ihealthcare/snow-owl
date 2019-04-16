/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.test.collect.BitSetTest;
import com.b2international.commons.test.collect.ByteOpenHashSetTest;
import com.b2international.commons.test.collect.EmptyLongListTest;
import com.b2international.commons.test.collect.IntOpenHashSetTest;
import com.b2international.commons.test.collect.LongOpenHashSetTest;
import com.b2international.commons.test.config.ConfigurationFactoryTest;

/**
 * @since 2.5
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	ArabicToRomanNumeralConverterTest.class, 
	BidiMapWithInternalIdTest.class,
	ConcurrentCollectionUtilsTest.class, 
	ConsoleProgressMonitorTest.class, 
	DfsDirectedPathServiceTest.class,
	DirectedGraphTest.class, 
	TopologicalSortTest.class, 
	VersionNumberComparatorTest.class, 
	VersionTest.class, 
	ConfigurationFactoryTest.class, 
	PreorderIteratorTest.class,
	BitSetTest.class,
	ByteOpenHashSetTest.class,
	IntOpenHashSetTest.class,
	LongOpenHashSetTest.class,
	EmptyLongListTest.class
})
public class AllCommonsTests {
	// Empty class body
}
