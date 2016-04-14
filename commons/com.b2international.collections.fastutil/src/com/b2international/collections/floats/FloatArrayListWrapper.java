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

import it.unimi.dsi.fastutil.floats.FloatArrayList;

/**
 * @since 4.7
 */
public final class FloatArrayListWrapper extends FloatCollectionWrapper implements FloatList {

	private FloatArrayListWrapper(it.unimi.dsi.fastutil.floats.FloatList delegate) {
		super(delegate);
	}
	
	@Override
	protected it.unimi.dsi.fastutil.floats.FloatList delegate() {
		return (it.unimi.dsi.fastutil.floats.FloatList) super.delegate();
	}
	
	@Override
	public void trimToSize() {
		trim(delegate());
	}

	@Override
	public FloatList dup() {
		return create(this);
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
	
	public static FloatList create(FloatCollection collection) {
		if (collection instanceof FloatArrayListWrapper) {
			final it.unimi.dsi.fastutil.floats.FloatList sourceDelegate = ((FloatArrayListWrapper) collection).delegate();
			return wrap(clone(sourceDelegate));
		} else {
			final FloatList result = create(collection.size());
			result.addAll(collection);
			return result;
		}
	}
	
	public static FloatList create(float[] source) {
		return wrap(new it.unimi.dsi.fastutil.floats.FloatArrayList(source));
	}
	
	public static FloatList create(int expectedSize) {
		return wrap(new FloatArrayList(expectedSize));
	}
	
	public static FloatList create() {
		return wrap(new FloatArrayList());
	}

	public static FloatList wrap(it.unimi.dsi.fastutil.floats.FloatList delegate) {
		return new FloatArrayListWrapper(delegate);
	}
	
	// FastUtil helpers
	
	private static it.unimi.dsi.fastutil.floats.FloatList clone(it.unimi.dsi.fastutil.floats.FloatList list) {
		if (list instanceof FloatArrayList) {
			return ((FloatArrayList) list).clone();
		} else {
			throw new UnsupportedOperationException("Unsupported collection implementation: " + list.getClass().getSimpleName());
		}
	}
	
	private static void trim(it.unimi.dsi.fastutil.floats.FloatList list) {
		if (list instanceof FloatArrayList) {
			((FloatArrayList) list).trim();
		} else {
			throw new UnsupportedOperationException("Unsupported list implementation: " + list.getClass().getSimpleName());
		}
	}
	
}
