/*
 * Copyright 2012 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.google.common.collect.ListMultimap;
/**
 * @since 7.8
 */
public class ConceptCompareSetResult <T> {
	Set<T> addedMembers;
	Set<T> removedMembers;
	ListMultimap<T, T> changedMembers;
	
	public ConceptCompareSetResult(Set<T> addedMembers, Set<T> removedMembers, ListMultimap<T, T> changedMembers) {
		this.addedMembers = addedMembers;
		this.removedMembers = removedMembers;
		this.changedMembers = changedMembers;
	}
	
	public Set<T> getAddedMembers() {
		return addedMembers;
	}
	
	public Set<T> getRemovedMembers() {
		return removedMembers;
	}
	
	public ListMultimap<T, T> getChangedMembers() {
		return changedMembers;
	}

}
