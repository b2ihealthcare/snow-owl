/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * @since 4.6
 */
public interface SortBy {

	public <T> Ordering<T> toOrdering();
	
	public static final SortBy INDEX_ORDER = new SortBy() { 
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> Ordering<T> toOrdering() {
			return (Ordering<T>) Ordering.allEqual();
		}
	};
	
	public static final class SortByField implements SortBy {
		
		private final String property;
		
		private final boolean numeric;
		
		private final boolean ascending;

		public SortByField(String property, boolean numeric, boolean ascending) {
			this.property = property;
			this.numeric = numeric;
			this.ascending = ascending;
		}

		public String property() {
			return property;
		}
		
		public boolean isNumeric() {
			return numeric;
		}
		
		public boolean isAscending() {
			return ascending;
		}
		
		@Override
		public <T> Ordering<T> toOrdering() {
			final Ordering<T> orderingOnField = Ordering.natural().onResultOf(new Function<T, Comparable<?>>() {
				public Comparable<?> apply(Object input) { 
					return (Comparable<?>) ReflectionUtils.getGetterValue(input, property()); 
				};
			});
			
			return ascending ? orderingOnField : orderingOnField.reverse();
		}
	}
}
