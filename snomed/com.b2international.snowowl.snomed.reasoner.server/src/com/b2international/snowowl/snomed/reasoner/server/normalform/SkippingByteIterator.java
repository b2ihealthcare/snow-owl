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
package com.b2international.snowowl.snomed.reasoner.server.normalform;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SkippingByteIterator implements Iterator<Byte> {

	private byte current;
	
	public SkippingByteIterator() {
		this((byte)1);
	}
	
	public SkippingByteIterator(byte startValue) {
		current = startValue;
	}
	
	// Be sure to add skip values before the iterator arrives at them
	public void skip(byte valueToSkip) {
		current = (byte) Math.max(current, valueToSkip + 1);
	}
	
	@Override
	public boolean hasNext() {
		return current < Byte.MAX_VALUE;
	}

	@Override
	public Byte next() {
		return nextByte();
	}
	
	public byte nextByte() {

		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		// Post-increment so that we'll start inspecting the next available value on the next call
		return current++;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}