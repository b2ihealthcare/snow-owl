/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents a collection resource in the RESTful API.
 * 
 * @since 8.0
 */
@JsonInclude(Include.NON_NULL)
public class ListCollectionResource<T> implements CollectionResource<T> {

	private static final long serialVersionUID = 1L;
	
	private final List<T> items;

	protected ListCollectionResource(List<T> items) {
		this.items = items == null ? Collections.<T> emptyList() : items;
	}

	@Override
	public final List<T> getItems() {
		return items;
	}
	
	/**
	 * Creates a new {@link CollectionResource} for the given items.
	 * 
	 * @param items
	 * @return
	 */
	public static <T> CollectionResource<T> of(List<T> items) {
		return new ListCollectionResource<T>(items);
	}
	
	/**
	 * Creates a new {@link CollectionResource} for the given items.
	 * 
	 * @param items
	 * @return
	 */
	@JsonCreator
	public static <T> CollectionResource<T> of(@JsonProperty("items") Collection<T> items) {
		if (items instanceof List) {
			return of((List<T>)items);
		}
		return of(List.copyOf(items));
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(ListCollectionResource.class).add("items", getItems()).toString();
	}
	
}