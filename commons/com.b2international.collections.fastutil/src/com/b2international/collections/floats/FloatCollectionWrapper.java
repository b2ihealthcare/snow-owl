/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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

/**
 * @since 4.7
 */
public class FloatCollectionWrapper extends AbstractFloatCollection implements FloatCollection {

	private final it.unimi.dsi.fastutil.floats.FloatCollection delegate;

	protected FloatCollectionWrapper(it.unimi.dsi.fastutil.floats.FloatCollection delegate) {
		this.delegate = delegate;
	}
	
	protected it.unimi.dsi.fastutil.floats.FloatCollection delegate() {
		return delegate;
	}
	
	@Override
	public final FloatIterator iterator() {
		return new FloatIteratorWrapper<>(delegate.iterator());
	}
	
	@Override
	public final void clear() {
		delegate.clear();
	}

	@Override
	public final boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public final int size() {
		return delegate.size();
	}
	
	@Override
	public final boolean add(float value) {
		return delegate.add(value);
	}

	@Override
	public final boolean addAll(FloatCollection collection) {
		if (collection instanceof FloatCollectionWrapper) {
			return delegate.addAll(((FloatCollectionWrapper) collection).delegate);
		} else {
			return super.addAll(collection);
		}
	}

	@Override
	public final boolean contains(float value) {
		return delegate.contains(value);
	}
	
	@Override
	public final boolean containsAll(FloatCollection collection) {
		if (collection instanceof FloatCollectionWrapper) {
			return delegate.containsAll(((FloatCollectionWrapper) collection).delegate);
		} else {
			return super.containsAll(collection);
		}
	}

	@Override
	public final boolean remove(float value) {
		return delegate.rem(value);
	}

	@Override
	public final boolean removeAll(FloatCollection collection) {
		if (collection instanceof FloatCollectionWrapper) {
			return delegate.removeAll(((FloatCollectionWrapper) collection).delegate);
		} else {
			return super.removeAll(collection);
		}
	}

	@Override
	public final boolean retainAll(FloatCollection collection) {
		if (collection instanceof FloatCollectionWrapper) {
			return delegate.retainAll(((FloatCollectionWrapper) collection).delegate);
		} else {
			return super.retainAll(collection);
		}
	}

	@Override
	public final float[] toArray() {
		return delegate.toFloatArray();
	}
	
	@Override
	public void trimToSize() {
		throw new UnsupportedOperationException("Unsupported collection implementation: " + delegate.getClass().getSimpleName());
	}
	
	public static FloatCollection wrap(it.unimi.dsi.fastutil.floats.FloatCollection collection) {
		return new FloatCollectionWrapper(collection);
	}
}
