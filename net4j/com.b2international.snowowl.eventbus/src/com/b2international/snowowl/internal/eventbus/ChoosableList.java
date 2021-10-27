/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * A thread-safe list of container that accepts unique elements only, and allows
 * selecting a random element from itself.
 * 
 * @since 8.0 (in class EventBus since 3.1)
 * @param <T>
 */
public class ChoosableList<T> implements Iterable<T> {

	private final CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<>();
	private final AtomicInteger pos = new AtomicInteger(0);

	public T choose() {
		while (true) {
			final int size = list.size();
			if (size == 0) {
				return null;
			}
			
			final int index = pos.getAndUpdate(oldIndex -> {
				if (oldIndex >= size - 1) {
					return 0;
				} else {
					return oldIndex + 1;
				}
			});
			
			try {
				return list.get(index);
			} catch (final IndexOutOfBoundsException e) {
				/*
				 * Guards against cases where the list is modified after a "snapshot" of the
				 * list's size was taken above
				 */
				pos.set(0);
			}
		}
	}

	public boolean add(final T handler) {
		return list.addIfAbsent(handler);
	}

	public boolean removeIf(final Predicate<? super T> filter) {
		return list.removeIf(filter);
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
