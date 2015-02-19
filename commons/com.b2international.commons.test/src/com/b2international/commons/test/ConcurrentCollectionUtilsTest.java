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

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

public class ConcurrentCollectionUtilsTest {

	@Test
	public void testFilter() {
		UnmodifiableIterator<Integer> sourceIterator = Iterators.forArray(
				new Integer[] {-9,-8,-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7,8,9});
		UnmodifiableIterator<Integer> expectedFilteredIterator = Iterators.forArray(
				new Integer[] {1,2,3,4,5,6,7,8,9});
		Predicate<Integer> positiveIntegerPredicate = new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return input > 0;
			}
		};
		Iterator<Integer> filteredIterator = ConcurrentCollectionUtils.filter(sourceIterator, positiveIntegerPredicate);
		assertIteratorsEqual(expectedFilteredIterator, filteredIterator);
	}
	
	@Test
	public void testTransform() {
		UnmodifiableIterator<Integer> sourceIterator = Iterators.forArray(
				new Integer[] {0,1,2,3,4,5,6,7,8,9});
		UnmodifiableIterator<String> expectedTransformedIterator = Iterators.forArray(
				new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
		Function<Integer, String> toStringFunction = new Function<Integer, String>() {
			@Override
			public String apply(Integer input) {
				return input.toString();
			}
		};
		Iterator<String> transformedIterator = ConcurrentCollectionUtils.transform(sourceIterator, toStringFunction);
		assertIteratorsEqual(expectedTransformedIterator, transformedIterator);
	}
	
	private <T> void assertIteratorsEqual(Iterator<T> expected, Iterator<T> actual) {
		int expectedSize = Iterators.size(expected);
		int actualSize = Iterators.size(actual);
		if (expectedSize != actualSize)
			Assert.fail(String.format("Iterator element counts don't match. Expected: %d, actual: %d.", expectedSize, actualSize));
		while (expected.hasNext()) {
			T expectedElement = (T) expected.next();
			while (actual.hasNext()) {
				T actualElement = (T) actual.next();
				if (actualElement.equals(expectedElement))
					Assert.fail(String.format("Iterator elements don't match. Expected: %d, actual: %d.", expectedElement, actualElement));
			}
		}
	}
}