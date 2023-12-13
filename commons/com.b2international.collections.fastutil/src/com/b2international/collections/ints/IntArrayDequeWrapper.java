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

import java.util.NoSuchElementException;

/**
 * @since 4.7
 */
public class IntArrayDequeWrapper extends IntArrayListWrapper implements IntDeque {

	public static IntDeque create(int[] source) {
		return new IntArrayDequeWrapper(new it.unimi.dsi.fastutil.ints.IntArrayList(source));
	}

	private IntArrayDequeWrapper(it.unimi.dsi.fastutil.ints.IntArrayList delegate) {
		super(delegate);
	}

	@Override
	public void addLast(int value) {
		delegate().add(delegate().size(), value);
	}

	@Override
	public int getLast() {
		if (!delegate().isEmpty()) {
			return delegate().getInt(delegate().size() - 1);
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public int removeLast() {
		if (!delegate().isEmpty()) {
			return delegate().removeInt(delegate().size() - 1);
		} else {
			throw new NoSuchElementException();
		}
	}
}
