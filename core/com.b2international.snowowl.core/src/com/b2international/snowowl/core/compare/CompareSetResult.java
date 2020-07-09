/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.compare;

import java.util.Set;

import com.google.common.collect.ListMultimap;
/**
 * @since 7.8
 */
public class CompareSetResult <T> {
	Set<T> addedMembers;
	Set<T> removedMembers;
	ListMultimap<T, T> changedMembers;
	
	public CompareSetResult(Set<T> addedMembers, Set<T> removedMembers, ListMultimap<T, T> changedMembers) {
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
