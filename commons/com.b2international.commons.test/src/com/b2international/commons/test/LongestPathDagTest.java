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

import static com.google.common.collect.Lists.newArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.commons.graph.GraphUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Test for calculating the longest path in DAGs.
 *
 */
public class LongestPathDagTest {

	@Test
	public void testLongestPathFromRoot() {
		Multimap<String, String> multimap = HashMultimap.<String, String>create();
		multimap.put("A", "B");
		multimap.put("A", "C");
		
		multimap.put("B", "G");
		multimap.put("B", "E");
		
		multimap.put("C", "D");
		
		multimap.put("D", "E");
		
		multimap.put("E", "G");
		multimap.put("E", "F");
		
		multimap.put("F", "H");
		
		
		Assert.assertEquals(newArrayList("A", "C", "D", "E", "F", "H"), GraphUtils.getLongestPath(multimap));
	}
	
	@Test
	public void testLongestPathFromRootIncorrectOrder() {
		Multimap<String, String> multimap = HashMultimap.<String, String>create();
		multimap.put("A", "B");
		multimap.put("A", "C");
		
		multimap.put("B", "G");
		multimap.put("B", "E");
		
		multimap.put("C", "D");
		
		multimap.put("D", "E");
		
		multimap.put("E", "G");
		multimap.put("E", "F");
		
		multimap.put("F", "H");
		
		
		Assert.assertNotEquals(newArrayList("C", "A", "D", "E", "F", "H"), GraphUtils.getLongestPath(multimap));
	}
	
}