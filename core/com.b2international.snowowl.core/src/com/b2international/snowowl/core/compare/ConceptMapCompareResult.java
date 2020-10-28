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
/**
 * @since 7.8
 */
public final class ConceptMapCompareResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int totalAdded;
	private final int totalRemoved;
	private final int totalChanged;
	private final int totalUnchanged;
	private final int limit;
	
	private final List<ConceptMapMapping> addedMembers;
	private final List<ConceptMapMapping> removedMembers;
	
	private final List<ConceptMapMapping> changedMembers;
	private final List<ConceptMapMapping> unchangedMembers;
	
	public ConceptMapCompareResult(List<ConceptMapMapping> addedMembers, List<ConceptMapMapping> removedMembers, List<ConceptMapMapping> changedMembers, List<ConceptMapMapping> unchangedMembers,
			int totalAdded, int totalRemoved, int totalChanged, int totalUnchanged, int limit) {
		
		this.addedMembers = List.copyOf(addedMembers);
		this.removedMembers = List.copyOf(removedMembers);
		
		this.changedMembers = List.copyOf(changedMembers);
		this.unchangedMembers = List.copyOf(unchangedMembers);
		
		this.totalAdded = totalAdded;
		this.totalRemoved = totalRemoved;
		this.totalChanged = totalChanged;
		this.totalUnchanged = totalUnchanged;
		this.limit = limit;
	}
	
	public List<ConceptMapMapping> getAddedMembers() {
		return addedMembers;
	}
	
	public List<ConceptMapMapping> getRemovedMembers() {
		return removedMembers;
	}
	
	public List<ConceptMapMapping> getUnchangedMembers() {
		return unchangedMembers;
	}
	
	public List<ConceptMapMapping> getChangedMembers() {
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
	
	public int getTotalUnchanged() {
		return totalUnchanged;
	}
	
	public int getLimit() {
		return limit;
	}
	
}
