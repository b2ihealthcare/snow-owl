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
	
	private final List<ConceptMapMapping> addedMembers;
	private final List<ConceptMapMapping> removedMembers;
	private final ListMultimap<ConceptMapMapping, ConceptMapMapping> changedMembers;
	private final List<ConceptMapMapping> unChangedMembers;
	
	public ConceptMapCompareResult(List<ConceptMapMapping> addedMembers, List<ConceptMapMapping> removedMembers, ListMultimap<ConceptMapMapping, ConceptMapMapping> changedMembers, List<ConceptMapMapping> unChangedMembers) {
		this.addedMembers = ImmutableList.copyOf(addedMembers);
		this.removedMembers = ImmutableList.copyOf(removedMembers);
		this.changedMembers = ImmutableListMultimap.copyOf(changedMembers);
		this.unChangedMembers = ImmutableList.copyOf(unChangedMembers);
	}
	
	public List<ConceptMapMapping> getAddedMembers() {
		return addedMembers;
	}
	
	public List<ConceptMapMapping> getRemovedMembers() {
		return removedMembers;
	}
	
	public List<ConceptMapMapping> getUnChangedMembers() {
		return unChangedMembers;
	}
	
	public ListMultimap<ConceptMapMapping, ConceptMapMapping> getChangedMembers() {
		return changedMembers;
	}

}
