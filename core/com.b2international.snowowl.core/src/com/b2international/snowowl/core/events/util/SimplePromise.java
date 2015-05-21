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
package com.b2international.snowowl.core.events.util;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.commons.collections.Procedure;

/**
 * @since 4.1
 */
public class SimplePromise<T> implements Promise<T> {

	private LinkedList<Procedure<T>> thens = newLinkedList();
	private LinkedList<Procedure<Throwable>> fails = newLinkedList();
	private AtomicBoolean completed = new AtomicBoolean(false);

	@Override
	public Promise<T> then(Procedure<T> then) {
		thens.add(then);
		return this;
	}

	@Override
	public Promise<T> fail(Procedure<Throwable> fail) {
		fails.add(fail);
		return this;
	}

	/**
	 * Resolves the promise by sending the given result object to all then listeners.
	 * 
	 * @param t - the resolution of this promise
	 */
	protected void resolve(T t) {
		if (completed.compareAndSet(false, true)) {
			for (Procedure<T> then : thens) {
				then.apply(t);
			}
		}
	}

	/**
	 * Rejects the promise by sending the {@link Throwable} to all failure listeners.
	 * 
	 * @param throwable
	 */
	protected void reject(Throwable throwable) {
		if (completed.compareAndSet(false, true)) {
			for (Procedure<Throwable> fail : fails) {
				fail.apply(throwable);
			}
		}
	}

}
