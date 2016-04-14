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
package com.b2international.collections.floats;

import com.b2international.collections.FloatCollection;
import com.b2international.collections.FloatIterator;

/**
 * @since 4.7
 */
public abstract class AbstractFloatCollection implements FloatCollection {

	@Override
	public boolean addAll(FloatCollection collection) {
		boolean changed = false;
		
		final FloatIterator itr = collection.iterator();
		while (itr.hasNext()) {
			changed |= add(itr.next());
		}
		
		return changed;
	}

	@Override
	public boolean contains(float value) {
		final FloatIterator itr = iterator();
		while (itr.hasNext()) {
			float current = itr.next();
			if (current == value) {
				return true;
			}
		}
	
		return false;
	}

	@Override
	public boolean containsAll(FloatCollection collection) {
		final FloatIterator itr;
		final FloatCollection other;
		
		if (size() < collection.size()) {
			itr = this.iterator();
			other = collection;
		} else {
			itr = collection.iterator();
			other = this;
		}
		
		while (itr.hasNext()) {
			if (!other.contains(itr.next())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean remove(float value) {
		boolean changed = false;
		
		final FloatIterator itr = iterator();
		while (itr.hasNext()) {
			float current = itr.next();
			if (current == value) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean removeAll(FloatCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	@Override
	public boolean retainAll(FloatCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	private boolean retainOrRemoveAll(FloatCollection collection, boolean retain) {
		boolean changed = false;
		
		final FloatIterator itr = iterator();
		while (itr.hasNext()) {
			float current = itr.next();
			if (collection.contains(current) ^ retain) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public float[] toArray() {
		final float[] result = new float[size()];
		final FloatIterator itr = iterator();
		int i = 0;
		
		while (itr.hasNext()) {
			result[i++] = itr.next();
		}
		
		return result;
	}

	@Override
	public void clear() {
		final FloatIterator itr = iterator();
		while (itr.hasNext()) {
			itr.next();
			itr.remove();
		}
	}

	@Override
	public boolean isEmpty() {
		final FloatIterator itr = iterator();
		return !itr.hasNext();
	}

	@Override
	public int size() {
		int size = 0;
		final FloatIterator itr = iterator();
		
		while (itr.hasNext()) {
			itr.next();
			size++;
		}
		
		return size;
	}
	
}
