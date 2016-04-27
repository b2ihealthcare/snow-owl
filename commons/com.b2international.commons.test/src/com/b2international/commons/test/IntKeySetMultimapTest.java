/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.collections.ints.IntIterator;
import com.b2international.commons.collect.IntKeySetMultimap;

/**
 * Test for {@link IntKeySetMultimap}.
 */
public class IntKeySetMultimapTest {

	@Test
	public void putAndCheckDistinctValues() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.put(2, "Two_1");
		multimap.put(2, "Two_2");
		multimap.put(2, "Two_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_2");
		multimap.put(3, "Three_3");
		assertTrue(multimap.get(1).size() == 1);
		assertTrue(multimap.get(2).size() == 2);
		assertTrue(multimap.get(3).size() == 3);
	}
	
	@Test
	public void checkNotExistingValues() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		assertTrue(multimap.get(-1).isEmpty());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void checkViewOfValues() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.get(1).add("Must fail");
	}
	
	@Test
	public void putAndCheckValues() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.put(2, "Two_1");
		multimap.put(2, "Two_2");
		multimap.put(2, "Two_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_2");
		multimap.put(3, "Three_3");
		assertTrue(multimap.values().size() == 3);
	}
	
	@Test
	public void putAndCheckFlatValues() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.put(2, "Two_1");
		multimap.put(2, "Two_2");
		multimap.put(2, "Two_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_2");
		multimap.put(3, "Three_3");
		assertTrue(newHashSet(concat(multimap.values())).size() == 6);
	}
	
	@Test
	public void putAndCheckSize() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.put(2, "Two_1");
		multimap.put(2, "Two_2");
		multimap.put(2, "Two_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_2");
		multimap.put(3, "Three_3");
		assertTrue(multimap.size() == 3);
	}
	
	@Test
	public void putAndCheckIterator() {
		final IntKeySetMultimap<String> multimap = new IntKeySetMultimap<>();
		multimap.put(1, "One_1");
		multimap.put(2, "Two_1");
		multimap.put(2, "Two_2");
		multimap.put(2, "Two_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_1");
		multimap.put(3, "Three_2");
		multimap.put(3, "Three_3");

		final IntIterator keys = multimap.keySet().iterator();
		while (keys.hasNext()) {
			keys.next();
			keys.remove();
		}
		
		assertEquals(0, multimap.size());
		assertTrue(multimap.isEmpty());
	}
	
}