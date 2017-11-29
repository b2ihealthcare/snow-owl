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
package com.b2international.snowowl.core.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Represents a collection resource in the RESTful API.
 * 
 * @since 4.0
 */
public class CollectionResource<T> implements Serializable, Iterable<T> {

	private static final long serialVersionUID = -840552452105348114L;
	
	private final List<T> items;

	protected CollectionResource(List<T> items) {
		this.items = items == null ? Collections.<T> emptyList() : items;
	}

	/**
	 * @return <tt>true</tt> if this {@link CollectionResource} contains no elements
	 * @since 5.8 
	 */
	@JsonIgnore
	public final boolean isEmpty() {
		return getItems().isEmpty(); 
	}
	
	/**
	 * @return the items associated in the collection.
	 */
	public final List<T> getItems() {
		return items;
	}
	
	@Override
	public Iterator<T> iterator() {
		return items.iterator();
	}

	/**
	 * Creates a new {@link CollectionResource} for the given items.
	 * 
	 * @param items
	 * @return
	 */
	public static <T> CollectionResource<T> of(List<T> items) {
		return new CollectionResource<T>(items);
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
		return of(ImmutableList.copyOf(items));
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(CollectionResource.class).add("items", getItems()).toString();
	}
	
	/**
	 * @return an {@link Optional} item in this collection resource
	 */
	@JsonIgnore
	public Optional<T> first() {
		return Optional.ofNullable(Iterables.getFirst(getItems(), null));
	}
	
	/**
	 * Returns a sequential {@code Stream} with this collection resource as its source.
	 * @return a {@link Stream} over the items of this collection resource
	 * @since 5.12
	 */
	@JsonIgnore
	public Stream<T> stream() {
		return getItems().stream();
	}

}