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
package com.b2international.commons.collections;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;

/**
 * Abstract implementation of a mutable supplier.
 * @see ISupplier2
 * @see #from
 * @param <F> - from type
 * @param <T> - the provided type
 */
public abstract class MutableSupplier<F, T> implements ISupplier2<F, T> {

	/**The {@code from} variable for creating {@code T} instance.*/
	protected final AtomicReference<F> from;
	
	/**Creates a new {@link MutableSupplier} with the initial {@code from} argument.*/
	public MutableSupplier(final AtomicReference<F> from) {
		this.from = Preconditions.checkNotNull(from, "From argument cannot be null.");
	}
	
	/**
	 * Delegates into {@link #get(F)}.
	 * <p>
	 * {@inheritDoc}
	 */
	/* (non-Javadoc)
	 * @see com.google.common.base.Supplier#get()
	 */
	@Override
	public final T get() {
		return get(getValue());
	}
	
	/**
	 * Sets the new value on the underling reference.
	 * @param newValue the new value.
	 */
	public void set(final F newValue) {
		from.set(newValue);
	}
	
	/**
	 * Returns with the current {@code from} value of the current {@link MutableSupplier supplier}.
	 * <br> Can be {@code null}.
	 * @return the current value.
	 */
	public F getValue() {
		return from.get();
	}

}