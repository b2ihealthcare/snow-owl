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
package com.b2international.snowowl.datastore.store.query;

import com.b2international.commons.ReflectionUtils;
import com.google.common.base.Predicate;

/**
 * @since 4.1
 */
public abstract class WhereBase<T> implements Where<T> {

	private String property;
	private T value;
	
	public WhereBase(String property, T value) {
		this.property = property;
		this.value = value;
	}
	
	@Override
	public String property() {
		return property;
	}

	@Override
	public T value() {
		return value;
	}
	
	@Override
	public final Predicate<T> toPredicate() {
		return new WherePredicate();
	}
	
	protected abstract boolean matches(T actual);
	
	private class WherePredicate implements Predicate<T> {

		@SuppressWarnings("unchecked")
		@Override
		public boolean apply(T input) {
			final Object actual = ReflectionUtils.getGetterValue(input, property());
			if (value instanceof String) {
				return matches((T) String.valueOf(actual));
			} else {
				return matches((T) actual);
			}
		}

	}

}
