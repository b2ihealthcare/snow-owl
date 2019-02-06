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
package com.b2international.collections.longs;

/**
 * @since 4.7
 */
public abstract class AbstractLongList extends AbstractLongCollection implements LongList {
	
	@Override
	public void trimToSize() {
		// Default implementation does nothing
	}
	
	@Override
	public long get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		
		LongIterator itr = iterator();
		long value = itr.next();
		
		for (int i = 0; i < index; i++) {
			value = itr.next();
		}
		
		return value;
	}
	
	@Override
	public long removeLong(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}

		LongIterator itr = iterator();
		long value = itr.next();
		
		for (int i = 0; i < index; i++) {
			value = itr.next();
		}
		
		itr.remove();
		return value;
	}
	
	@Override
	public long set(int index, long value) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}

		LongListIterator itr = listIterator(index);
		long existingValue = itr.next();
		itr.set(value);
		return existingValue;
	}
	
	@Override
	public LongIterator iterator() {
		return listIterator();
	}
	
	@Override
	public LongListIterator listIterator(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		
		LongListIterator itr = listIterator();
		for (int i = 0; i < index; i++) {
			itr.next();
		}
		
		return itr;
	}
	
	@Override
	public boolean equals(Object obj) {
		return equals(this, obj);
	}
	
	@Override
	public int hashCode() {
        return hashCode(this);
	}
	
	public static boolean equals(LongList obj1, Object obj2) {
		if (obj1 == obj2) { return true; }
		if (!(obj2 instanceof LongList)) { return false; }
		
		LongList other = (LongList) obj2;
		if (obj1.size() != other.size()) { return false; }
		
		LongIterator itr1 = obj1.iterator();
		LongIterator itr2 = other.iterator();
		
		while (itr1.hasNext() && itr2.hasNext()) {
			long value1 = itr1.next();
			long value2 = itr2.next();
			
			if (value1 != value2) {
				return false;
			}
		}
		
		return true;
	}
}
