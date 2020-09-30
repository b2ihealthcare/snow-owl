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

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.List;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
/**
 * @since 7.8
 */
public final class ConceptMapCompareResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int totalAdded;
	private final int totalRemoved;
	private final int totalChanged;
	private final int limit;
	
	private final List<ConceptMapMapping> addedMembers;
	private final List<ConceptMapMapping> removedMembers;
	private final Multimap<ConceptMapMapping, ConceptMapMapping> changedMembers;
	
	public ConceptMapCompareResult(List<ConceptMapMapping> addedMembers, List<ConceptMapMapping> removedMembers, Multimap<ConceptMapMapping, ConceptMapMapping> changedMembers, int limit) {
		
		this.addedMembers = addedMembers.stream().limit(limit).collect(toList());
		this.removedMembers = removedMembers.stream().limit(limit).collect(toList());
		
		Builder<ConceptMapMapping, ConceptMapMapping> limitedMembers = ImmutableMultimap.builder();
		changedMembers.entries().stream().limit(limit).forEach(limitedMembers::put);
		this.changedMembers = limitedMembers.build();
		
		this.totalAdded = addedMembers.size();
		this.totalRemoved = removedMembers.size();
		this.totalChanged = changedMembers.values().size();
		this.limit = limit;
		
	}
	
	public List<ConceptMapMapping> getAddedMembers() {
		return addedMembers;
	}
	
	public List<ConceptMapMapping> getRemovedMembers() {
		return removedMembers;
	}
	
	public Multimap<ConceptMapMapping, ConceptMapMapping> getChangedMembers() {
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
	
	public int getLimit() {
		return limit;
	}
	
}
