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
package com.b2international.collections.ints;

/**
 * @since 4.7
 */
class IntListIteratorWrapper extends IntIteratorWrapper<it.unimi.dsi.fastutil.ints.IntListIterator> implements IntListIterator {

	IntListIteratorWrapper(it.unimi.dsi.fastutil.ints.IntListIterator delegate) {
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
	public int previous() {
		return delegate.previousInt();
	}

	@Override
	public void add(int value) {
		delegate.add(value);
	}

	@Override
	public void set(int value) {
		delegate.set(value);
	}
}
