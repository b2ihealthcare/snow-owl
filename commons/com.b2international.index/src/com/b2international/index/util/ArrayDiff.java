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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 7.16.2
 */
public final class ArrayDiff {

	private final List<JsonNode> addedItems;
	private final Map<String, ArrayItemDiff> changedItemsById;
	private final List<JsonNode> removedItems;

	public ArrayDiff(List<JsonNode> addedItems, Map<String, ArrayItemDiff> changedItemsById, List<JsonNode> removedItems) {
		this.addedItems = Collections3.toImmutableList(addedItems);
		this.changedItemsById = changedItemsById == null ? Map.of() : Map.copyOf(changedItemsById);
		this.removedItems = Collections3.toImmutableList(removedItems);
	}
	
	public List<JsonNode> getAddedItems() {
		return addedItems;
	}
	
	public Map<String, ArrayItemDiff> getChangedItemsById() {
		return changedItemsById;
	}
	
	public List<JsonNode> getRemovedItems() {
		return removedItems;
	}
	
	@Override
	public String toString() {
		return String.format("added:%s, changed:%s, removed:%s", addedItems, changedItemsById, removedItems);
	}
	
	public static final ArrayDiff diff(ArrayNode source, ArrayNode target, Function<JsonNode, String> itemIdFunction) {
		Preconditions.checkNotNull(source, "source may not be null");
		Preconditions.checkNotNull(target, "target may not be null");
		
		if (itemIdFunction != null) {
			Map<String, JsonNode> sourceItemsByIdField = new LinkedHashMap<>();
			source.forEach(item -> sourceItemsByIdField.put(itemIdFunction.apply(item), item));
			Map<String, JsonNode> targetItemsByIdField = new LinkedHashMap<>();
			target.forEach(item -> targetItemsByIdField.put(itemIdFunction.apply(item), item));
			
			ImmutableList.Builder<JsonNode> addedItems = ImmutableList.builder();
			ImmutableMap.Builder<String, ArrayItemDiff> changedItems = ImmutableMap.builder();
			ImmutableList.Builder<JsonNode> removedItems = ImmutableList.builder();
			
			for (String id : Sets.union(sourceItemsByIdField.keySet(), targetItemsByIdField.keySet())) {
				JsonNode sourceItem = sourceItemsByIdField.get(id);
				JsonNode targetItem = targetItemsByIdField.get(id);
				if (sourceItem == null && targetItem != null) {
					addedItems.add(targetItem);
				} else if (sourceItem != null && targetItem == null) {
					removedItems.add(sourceItem);
				} else if (sourceItem != null && targetItem != null) {
					changedItems.put(id, new ArrayItemDiff(id, sourceItem, targetItem));
				} else {
					// should not happen, but if in a parallel universe it does, then nothing to do
				}
			}
			
			return new ArrayDiff(addedItems.build(), changedItems.build(), removedItems.build());
		} else {
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
				Map.of(),
				StreamSupport.stream(source.spliterator(), false).filter(sourceItems::contains).collect(Collectors.toUnmodifiableList())
			);
		}
	}
	
	public static final class ArrayItemDiff {
		
		private final String id;
		private final JsonNode fromValue;
		private final JsonNode value;
		
		private JsonDiff diff;
		
		public ArrayItemDiff(String id, JsonNode fromValue, JsonNode value) {
			this.id = id;
			this.fromValue = fromValue;
			this.value = value;
		}
		
		public JsonDiff diff() {
			if (diff == null) {
				diff = JsonDiff.diff(fromValue, value);
			}
			return diff;
		}
		
		public String getId() {
			return id;
		}
		
		public JsonNode getFromValue() {
			return fromValue;
		}
		
		public JsonNode getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return String.format("%s -> %s", fromValue, value);
		}
		
	}
	
}
