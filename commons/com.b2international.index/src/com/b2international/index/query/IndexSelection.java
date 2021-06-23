/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.mapping.DocumentMapping;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 8.0
 */
public final class IndexSelection<T> {

	private final Class<T> select;
	private final List<Class<?>> from;
	private final Class<?> parentScope;

	public IndexSelection(Class<T> select, List<Class<?>> from, Class<?> parentScope) {
		this.select = select;
		this.from = from;
		this.parentScope = parentScope;
		if (parentScope != null && from.size() > 1) {
			throw new BadRequestException("Querying multiple indexes '%s' with parent scope '%s' is not supported yet.", from, parentScope);
		}
	}
	
	public Class<T> getSelect() {
		return select;
	}
	
	public List<Class<?>> getFrom() {
		return from;
	}
	
	public List<String> getFromDocumentTypes() {
		return getFrom().stream().map(DocumentMapping::getType).collect(Collectors.toList());
	}
	
	public Class<?> getParentScope() {
		return parentScope;
	}
	
	public String getParentScopeDocumentType() {
		return parentScope != null ? DocumentMapping.getType(parentScope) : null;
	}
	
	public static <T> Builder<T> builder(Class<T> select) {
		return new Builder<T>(select);
	}
	
	/**
	 * @since 8.0
	 * @param <T>
	 */
	public static final class Builder<T> {
		
		private Class<T> select;
		private List<Class<?>> from;
		private Class<?> parentScope;
		
		public Builder(Class<T> select) {
			this.select = select;
			this.from = List.of(select);
		}
		
		public Builder<T> from(Class<?> from, Class<?>...froms) {
			com.google.common.collect.ImmutableList.Builder<Class<?>> types = ImmutableList.<Class<?>>builder()
					.add(from);
			if (froms != null) {
				types.add(froms);
			}
			return from(types.build());
		}
		
		public Builder<T> from(List<Class<?>> from) {
			this.from = from;
			return this;
		}
		
		public Builder<T> withParentScope(Class<?> parentScope) {
			this.parentScope = parentScope;
			return this;
		}
		
		public IndexSelection<T> build() {
			return new IndexSelection<>(select, from, parentScope);
		}

	}

	public String toSelectString() {
		if (from.size() == 1) {
			return select == Iterables.getOnlyElement(from) ? "*" : select.toString();
		} else {
			return select.toString();
		}
	}

}
