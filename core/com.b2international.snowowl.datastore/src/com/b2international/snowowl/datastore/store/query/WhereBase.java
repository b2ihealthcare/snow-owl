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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.xtext.util.Strings;

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
		return new WherePredicate<T>(this);
	}
	
	protected abstract boolean matches(String actual);
	
	private class WherePredicate<T> implements Predicate<T> {

		private Where where;

		public WherePredicate(Where where) {
			this.where = checkNotNull(where);
		}
		
		private Method findGetter(Class<?> type, String property) {
			try {
				return type.getMethod(property);
			} catch (NoSuchMethodException | SecurityException e) {
				if (!property.startsWith("get")) {
					return findGetter(type, "get".concat(Strings.toFirstUpper(property)));
				}
				throw new QueryException("Could not find applicable getter method: ", property, e);
			}
		}

		@Override
		public boolean apply(T input) {
			final Method getter = findGetter(input.getClass(), where.property());
			try {
				if (getter != null) {
					final String actual = String.valueOf(getter.invoke(input));
					return matches(actual);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return false;
		}

	}

}
