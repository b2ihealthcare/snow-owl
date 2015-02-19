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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;

import org.junit.Test;

import com.b2international.commons.arrays.BidiMapWithInternalId;

public class BidiMapWithInternalIdTest {

	public static String TEST_KEY = "3";
	public static String TEST_VALUE = "three";
	public static String[] keys = {"0", "1", "2", TEST_KEY, "4", "5"};
	public static String[] values = {"zero", "one", "two", TEST_VALUE, "four", "five"};
	
	@Test
	public void testRemove() {
		
		Map<String,String> referenceMap = createReferenceMap();
		BidiMapWithInternalId<String,String> map = createMap();
		int id = map.getInternalId(TEST_KEY);
		
		map.remove(TEST_KEY);
		referenceMap.remove(TEST_KEY);
		
		Assert.assertNull("Entry still exists", map.get(TEST_KEY));
		Assert.assertTrue("Entry still exists", !map.get(id).equals(values[id]));
		
		assertEquals(referenceMap, map);
	}
	
	@Test
	public void testRemoveFromElements() {
		
		Map<String,String> referenceMap = createReferenceMap();
		BidiMapWithInternalId<String,String> map = createMap();
		int id = map.getInternalId(TEST_KEY);
		
		Iterator<String> iterator = map.getElements().iterator();
		
		String valueToRemove = iterator.next();
		iterator.remove();
		
		iterator.next();
		iterator.next();
		String value = iterator.next();
		
		Assert.assertEquals("Wrong value", TEST_VALUE, value);
		
		iterator.remove();
		
		// Update reference map accordingly
		referenceMap.values().remove(valueToRemove);
		referenceMap.remove(TEST_KEY);
		
		Assert.assertNull("Entry still exists", map.get(TEST_KEY));
		Assert.assertTrue("Entry still exists", !map.get(id).equals(values[id]));
		
		assertEquals(referenceMap, map);
	}
	
	protected void assertEquals(Map<String, String> expected, BidiMapWithInternalId<String, String> actual) {
		Assert.assertEquals("Map size", expected.size(), actual.size());
		for(Entry<String, String> entry: expected.entrySet()) {
			Assert.assertEquals("Entry mismatch", entry.getValue(), actual.get(entry.getKey()));
		}
	}
	
	protected BidiMapWithInternalId<String,String> createMap() {
		
		BidiMapWithInternalId<String, String> map = new BidiMapWithInternalId<String, String>(keys.length);
		for(int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		
		return map;
	}
	
	protected Map<String,String> createReferenceMap() {
		Map<String, String> referenceMap = new HashMap<String, String>(keys.length);
		for(int i = 0; i < keys.length; i++) {
			referenceMap.put(keys[i], values[i]);
		}
		return referenceMap;
	}

}