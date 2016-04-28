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
class LongListIteratorWrapper extends LongIteratorWrapper<it.unimi.dsi.fastutil.longs.LongListIterator> implements LongListIterator {

	LongListIteratorWrapper(it.unimi.dsi.fastutil.longs.LongListIterator delegate) {
		super(delegate);
	}

	@Override
	public boolean hasPrevious() {
		return delegate.hasPrevious();
	}

	@Override
	public int nextIndex() {
		return delegate.nextIndex();
	}

	@Override
	public int previousIndex() {
		return delegate.previousIndex();
	}

	@Override
	public long previous() {
		return delegate.previousLong();
	}

	@Override
	public void add(long value) {
		delegate.add(value);
	}

	@Override
	public void set(long value) {
		delegate.set(value);
	}
}
