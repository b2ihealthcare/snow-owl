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
package com.b2international.index.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * @since 7.16.2
 */
public final class ArrayDiff {

	private final List<JsonNode> addedItems;
	private final List<JsonNode> removedItems;

	public ArrayDiff(List<JsonNode> addedItems, List<JsonNode> removedItems) {
		this.addedItems = Collections3.toImmutableList(addedItems);
		this.removedItems = Collections3.toImmutableList(removedItems);
	}
	
	public List<JsonNode> getAddedItems() {
		return addedItems;
	}
	
	public List<JsonNode> getRemovedItems() {
		return removedItems;
	}
	
	@Override
	public String toString() {
		return String.format("added:%s,removed:%s", addedItems, removedItems);
	}
	
	public static final ArrayDiff diff(ArrayNode source, ArrayNode target) {
		Preconditions.checkNotNull(source, "source may not be null");
		Preconditions.checkNotNull(target, "target may not be null");
		
		Set<JsonNode> sourceItems = Sets.newHashSet(source.iterator());
		Set<JsonNode> targetItems = Sets.newHashSet(target.iterator());
		Set<JsonNode> sameItems = Set.copyOf(Sets.intersection(sourceItems, targetItems));
		
		// removing intersection from source items results in the removed items
		sourceItems.removeAll(sameItems);
		// removing intersection from target items results in the added items
		targetItems.removeAll(sameItems);
		
		// keep order of items as defined in the input arrays
		return new ArrayDiff(
			StreamSupport.stream(target.spliterator(), false).filter(targetItems::contains).collect(Collectors.toUnmodifiableList()),
			StreamSupport.stream(source.spliterator(), false).filter(sourceItems::contains).collect(Collectors.toUnmodifiableList())
		);
	}
	
}
