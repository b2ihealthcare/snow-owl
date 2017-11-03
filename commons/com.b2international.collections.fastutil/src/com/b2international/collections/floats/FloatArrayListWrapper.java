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

import it.unimi.dsi.fastutil.floats.FloatArrayList;

/**
 * @since 4.7
 */
public final class FloatArrayListWrapper extends FloatCollectionWrapper implements FloatList {

	private FloatArrayListWrapper(it.unimi.dsi.fastutil.floats.FloatList delegate) {
		super(delegate);
	}
	
	@Override
	public int hashCode() {
		return AbstractFloatCollection.hashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!(obj instanceof FloatList)) { return false; }
		
		FloatList other = (FloatList) obj;
		if (size() != other.size()) { return false; }
		return AbstractFloatCollection.elementsEqual(iterator(), other.iterator());
	}
	
	@Override
	protected it.unimi.dsi.fastutil.floats.FloatList delegate() {
		return (it.unimi.dsi.fastutil.floats.FloatList) super.delegate();
	}
	
	@Override
	public void trimToSize() {
		if (delegate() instanceof FloatArrayList) {
			((FloatArrayList) delegate()).trim();
		} else {
			super.trimToSize();
		}
	}

	@Override
	public float get(int index) {
		return delegate().getFloat(index);
	}

	@Override
	public FloatListIterator listIterator() {
		return new FloatListIteratorWrapper(delegate().listIterator());
	}

	@Override
	public FloatListIterator listIterator(int startIndex) {
		return new FloatListIteratorWrapper(delegate().listIterator(startIndex));
	}

	@Override
	public float set(int index, float value) {
		return delegate().set(index, value);
	}
	
	@Override
	public float removeFloat(int index) {
		return delegate().removeFloat(index);
	}
	
	public static FloatList create(FloatCollection collection) {
		if (collection instanceof FloatArrayListWrapper) {
			final it.unimi.dsi.fastutil.floats.FloatList sourceDelegate = ((FloatArrayListWrapper) collection).delegate();
			return new FloatArrayListWrapper(clone(sourceDelegate));
		} else {
			final FloatList result = createWithExpectedSize(collection.size());
			result.addAll(collection);
			return result;
		}
	}
	
	public static FloatList create(float[] source) {
		return new FloatArrayListWrapper(new it.unimi.dsi.fastutil.floats.FloatArrayList(source));
	}
	
	public static FloatList createWithExpectedSize(int expectedSize) {
		return new FloatArrayListWrapper(new FloatArrayList(expectedSize));
	}
	
	public static FloatList create() {
		return new FloatArrayListWrapper(new FloatArrayList());
	}

	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.floats.FloatList clone(it.unimi.dsi.fastutil.floats.FloatList list) {
		if (list instanceof FloatArrayList) {
			return ((FloatArrayList) list).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported list implementation: " + list.getClass().getSimpleName());
		}
	}
	
}
