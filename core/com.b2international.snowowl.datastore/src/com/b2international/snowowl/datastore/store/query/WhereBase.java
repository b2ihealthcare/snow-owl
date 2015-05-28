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
public abstract class WhereBase implements Where {

	private String property;
	private String value;
	
	public WhereBase(String property, String value) {
		this.property = property;
		this.value = value;
	}
	
	@Override
	public String property() {
		return property;
	}

	@Override
	public String value() {
		return value;
	}
	
	@Override
	public final <T> Predicate<T> toPredicate() {
		return new WherePredicate<T>();
	}
	
	protected abstract boolean matches(String actual);
	
	private class WherePredicate<T> implements Predicate<T> {

		@Override
		public boolean apply(T input) {
			final Object value = ReflectionUtils.getGetterValue(input, property()); 
			// TODO add non-string support
			final String actual = String.valueOf(value);
			return matches(actual);
		}

	}

}
