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
package com.b2international.index.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.b2international.index.mapping.DocumentMapping;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public class SortBy {
	
	public static enum Order {
		ASC, 
		DESC;
	}

	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final String FIELD_SCORE = "_score";
	
	/**
	 * Singleton representing document sort based on their natural occurrence. 
	 */
	public static final SortBy DOC_ID = SortBy.field(DocumentMapping._ID, Order.ASC);
	
	/**
	 * Singleton representing document sort based on their score in decreasing order (higher score first).
	 */
	public static final SortBy SCORE = SortBy.field(FIELD_SCORE, Order.DESC);
	
	public static final class SortByField extends SortBy {
		private final String field;
		private final Order order;

		public SortByField(String field, Order order) {
			this.field = checkNotNull(field, "field");
			this.order = checkNotNull(order, "order");
		}

		public String getField() {
			return field;
		}
		
		public Order getOrder() {
			return order;
		}

		@Override
		public int hashCode() {
			return Objects.hash(field, order);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }
			
			SortByField other = (SortByField) obj;
			if (!Objects.equals(field, other.field)) { return false; }
			if (order != other.order) { return false; }
			return true;
		}

		@Override
		public String toString() {
			return field + " " + order;
		}
	}
	
	public static final class MultiSortBy extends SortBy {
		private final List<SortBy> items;

		public MultiSortBy(List<SortBy> items) {
			this.items = ImmutableList.copyOf(checkNotNull(items, "items"));
		}
		
		public List<SortBy> getItems() {
			return items;
		}

		@Override
		public int hashCode() {
			return 31 + items.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }
			
			MultiSortBy other = (MultiSortBy) obj;
			return items.equals(other.items);
		}

		@Override
		public String toString() {
			return Joiner.on(", ").join(items);
		}
	}
	
	public static final class Builder {
		private final Map<String, Order> sortOrderMap = Maps.newHashMap();
		
		public Builder add(String field, Order order) {
			sortOrderMap.put(field, order);
			return this;
		}
		
		public SortBy build() {
			if (sortOrderMap.isEmpty()) {
				return DOC_ID;
			} else if (sortOrderMap.size() == 1) {
				Entry<String, Order> onlyElement = Iterables.getOnlyElement(sortOrderMap.entrySet());
				return new SortByField(onlyElement.getKey(), onlyElement.getValue());
			} else {
				Iterable<SortByField> sortByFieldIterable = Iterables.transform(sortOrderMap.entrySet(), new Function<Entry<String, Order>, SortByField>() {
					@Override
					public SortByField apply(Entry<String, Order> input) {
						return new SortByField(input.getKey(), input.getValue());
					}
				});
				return new MultiSortBy(ImmutableList.<SortBy>copyOf(sortByFieldIterable));
			}
		}
	}
	
	public static SortBy field(String field, Order order) {
		return new SortByField(field, order);
	}
	
	public static Builder builder() {
		return new Builder();
	}
}