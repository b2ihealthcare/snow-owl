/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.b2international.index.mapping.DocumentMapping;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public abstract class SortBy {
	
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
	
	/**
	 * @since 5.0
	 */
	public static final class SortByField extends SortBy {
		private final String field;
		private final Order order;

		private SortByField(String field, Order order) {
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
	
	/**
	 * @since 6.3
	 */
	public static final class SortByScript extends SortBy {

		private final Order order;
		private final String name;
		private final Map<String, Object> arguments;

		private SortByScript(String name, Map<String, Object> arguments, Order order) {
			this.name = name;
			this.arguments = arguments;
			this.order = order;
		}
		
		public Order getOrder() {
			return order;
		}
		
		public String getName() {
			return name;
		}
		
		public Map<String, Object> getArguments() {
			return arguments;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(name, arguments, order);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }
			
			SortByScript other = (SortByScript) obj;
			return Objects.equals(name, other.name) 
					&& Objects.equals(arguments, other.arguments)
					&& Objects.equals(order, other.order); 
		}
		
		@Override
		public String toString() {
			return name + " " + arguments + " " + order;
		}
		
	}
	
	/**
	 * @since 5.0
	 */
	public static final class MultiSortBy extends SortBy {
		private final List<SortBy> items;

		private MultiSortBy(List<SortBy> items) {
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
		private final List<SortBy> sorts = newArrayList();
		
		public Builder sortByField(String field, Order order) {
			sorts.add(field(field, order));
			return this;
		}
		
		public Builder sortByScript(String script, Map<String, Object> arguments, Order order) {
			sorts.add(script(script, arguments, order));
			return this;
		}
		
		public SortBy build() {
			if (sorts.isEmpty()) {
				return DOC_ID;
			} else if (sorts.size() == 1) {
				return Iterables.getOnlyElement(sorts);
			} else {
				return new MultiSortBy(sorts);
			}
		}
	}
	
	/**
	 * Creates and returns a new {@link SortBy} instance that sorts matches by the given field in the given order.
	 * @param field - the field to use for sort
	 * @param order - the order to use when sorting matches
	 * @return
	 */
	public static SortBy field(String field, Order order) {
		return new SortByField(field, order);
	}
	
	/**
	 * @param script
	 * @param arguments
	 * @param order
	 * @return
	 */
	public static SortBy script(String script, Map<String, Object> arguments, Order order) {
		return new SortByScript(script, arguments, order);
	}

	public static Builder builder() {
		return new Builder();
	}

}