/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.compare;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.9
 */
public final class CompareResult implements Serializable {

	public static final class Builder {
		
		private final String baseBranch;
		private final String compareBranch;
		private final long compareHeadTimestamp;
		private final ImmutableSet.Builder<ComponentIdentifier> newComponents = ImmutableSet.builder();
		private final ImmutableSet.Builder<ComponentIdentifier> changedComponents = ImmutableSet.builder();
		private final ImmutableSet.Builder<ComponentIdentifier> deletedComponents = ImmutableSet.builder();
		
		private Builder(String baseBranch, String compareBranch, long compareHeadTimestamp) {
			this.baseBranch = baseBranch;
			this.compareBranch = compareBranch;
			this.compareHeadTimestamp = compareHeadTimestamp;
		}
		
		public Builder putNewComponent(ComponentIdentifier identifier) {
			this.newComponents.add(identifier);
			return this;
		}
		
		public Builder putChangedComponent(ComponentIdentifier identifier) {
			this.changedComponents.add(identifier);
			return this;
		}
		
		public Builder putDeletedComponent(ComponentIdentifier identifier) {
			this.deletedComponents.add(identifier);
			return this;
		}
		
		public CompareResult build() {
			return new CompareResult(baseBranch, compareBranch, compareHeadTimestamp, newComponents.build(), changedComponents.build(), deletedComponents.build());
		}

	}

	private final String baseBranch;
	private final String compareBranch;
	private final long compareHeadTimestamp;
	private final Collection<ComponentIdentifier> newComponents;
	private final Collection<ComponentIdentifier> changedComponents;
	private final Collection<ComponentIdentifier> deletedComponents;
	
	private CompareResult(
			String baseBranch, 
			String compareBranch, 
			long compareHeadTimestamp,
			Collection<ComponentIdentifier> newComponents,
			Collection<ComponentIdentifier> changedComponents,
			Collection<ComponentIdentifier> deletedComponents) {
		this.baseBranch = baseBranch;
		this.compareBranch = compareBranch;
		this.compareHeadTimestamp = compareHeadTimestamp;
		this.newComponents = newComponents;
		this.changedComponents = changedComponents;
		this.deletedComponents = deletedComponents;
	}
	
	public String getBaseBranch() {
		return baseBranch;
	}
	
	public String getCompareBranch() {
		return compareBranch;
	}
	
	public long getCompareHeadTimestamp() {
		return compareHeadTimestamp;
	}
	
	public Collection<ComponentIdentifier> getNewComponents() {
		return newComponents;
	}
	
	public Collection<ComponentIdentifier> getChangedComponents() {
		return changedComponents;
	}
	
	public Collection<ComponentIdentifier> getDeletedComponents() {
		return deletedComponents;
	}

	public static Builder builder(String baseBranch, String compareBranch, long compareHeadTimestamp) {
		return new Builder(baseBranch, compareBranch, compareHeadTimestamp);
	}
	
}
