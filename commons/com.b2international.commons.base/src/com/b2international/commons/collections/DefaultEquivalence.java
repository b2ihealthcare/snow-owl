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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Suppliers.memoize;

import com.google.common.base.Equivalence;
import com.google.common.base.Supplier;

/**
 * Type safe default equals {@link Equivalence} implementation.
 * Falls back to the predefined implementation of {@link #equals(Object)} and {@link #hashCode()} of
 * the investigated objects.
 *
 */
public final class DefaultEquivalence extends Equivalence<Object> {

	/**
	 * Returns with the shared default equivalence instance. 
	 */
	@SuppressWarnings("unchecked")
	public static <E> Equivalence<E> getDefaultEquivalence() {
		return (Equivalence<E>) EQUIVALENCE_SUPPLIER.get();
	}  
	
	private static final Supplier<DefaultEquivalence> EQUIVALENCE_SUPPLIER = memoize(new Supplier<DefaultEquivalence>() {
		@Override
		public DefaultEquivalence get() {
			return new DefaultEquivalence();
		}
	});
	
	private DefaultEquivalence() { /*private*/ }
	
	@Override
	protected boolean doEquivalent(final Object a, final Object b) {
		return equal(a, b);
	}

	@Override
	protected int doHash(final Object t) {
		return null != t ? t.hashCode() : 0;
	}

}