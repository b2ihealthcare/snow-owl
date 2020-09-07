/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.io.Serializable;
import java.util.List;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
/**
 * @since 7.8
 */
public final class ConceptMapCompareResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_LIMIT_PER_COMPARE_CATEGORY = 5000;
	
	private final int totalAdded;
	private final int totalRemoved;
	private final int totalChanged;
	
	private final List<ConceptMapMapping> addedMembers;
	private final List<ConceptMapMapping> removedMembers;
	private final ListMultimap<ConceptMapMapping, ConceptMapMapping> changedMembers;
	
	public ConceptMapCompareResult(List<ConceptMapMapping> addedMembers, List<ConceptMapMapping> removedMembers, ListMultimap<ConceptMapMapping, ConceptMapMapping> changedMembers, int limit) {
		this.addedMembers = ImmutableList.copyOf(limit < addedMembers.size() ? addedMembers.subList(0, limit) : addedMembers);
		this.removedMembers = ImmutableList.copyOf(limit < removedMembers.size() ? removedMembers.subList(0, limit) : removedMembers);
		
		ImmutableListMultimap.Builder<ConceptMapMapping, ConceptMapMapping> builder = ImmutableListMultimap.builder();
		int noOfChangedMappings = 0;
		for (ConceptMapMapping key : changedMembers.keySet()) {
			if (noOfChangedMappings < DEFAULT_LIMIT_PER_COMPARE_CATEGORY) {
				List<ConceptMapMapping> memberChanges = changedMembers.get(key);
				builder.putAll(key, memberChanges);
				noOfChangedMappings += memberChanges.size();
			}
		}
		this.changedMembers = builder.build();
		
		this.totalAdded = addedMembers.size();
		this.totalRemoved = removedMembers.size();
		this.totalChanged = changedMembers.values().size();
	}
	
	public List<ConceptMapMapping> getAddedMembers() {
		return addedMembers;
	}
	
	public List<ConceptMapMapping> getRemovedMembers() {
		return removedMembers;
	}
	
	public ListMultimap<ConceptMapMapping, ConceptMapMapping> getChangedMembers() {
		return changedMembers;
	}
	
	public int getTotalAdded() {
		return totalAdded;
	}
	
	public int getTotalRemoved() {
		return totalRemoved;
	}
	
	public int getTotalChanged() {
		return totalChanged;
	}
	
}
