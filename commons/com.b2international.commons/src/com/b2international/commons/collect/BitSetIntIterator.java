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
package com.b2international.commons.collect;

import java.util.BitSet;

import com.b2international.collections.ints.IntIterator;

public class BitSetIntIterator implements IntIterator {

	private int fromIndex = -1;
	private int setBitsFound = 0;
	private final BitSet bitSet;
	private final int cardinality;

	public BitSetIntIterator(final BitSet bitSet) {
		this.bitSet = bitSet;
		this.cardinality = bitSet.cardinality();
	}

	@Override
	public boolean hasNext() {
		return setBitsFound < cardinality;
	}

	@Override
	public int next() {
		fromIndex = bitSet.nextSetBit(fromIndex + 1);

		if (-1 == fromIndex) {
			throw new IllegalStateException("No more set bits left");
		}

		++setBitsFound;
		return fromIndex;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}