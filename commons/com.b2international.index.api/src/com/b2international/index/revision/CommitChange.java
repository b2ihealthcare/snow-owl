/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Objects;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.6
 */
@Doc(nested = false, index = true)
public final class CommitChange {

	public static Builder builder(String containerId) {
		return new Builder(containerId);
	}
	
	/**
	 * @since 6.6
	 */
	public static final class Builder {
		
		private final String containerId;
		private Iterable<String> newComponents;
		private Iterable<String> changedComponents;
		private Iterable<String> removedComponents;
		
		Builder(String containerId) {
			this.containerId = containerId;
		}
		
		public Builder newComponents(Iterable<String> newComponents) {
			this.newComponents = newComponents;
			return this;
		}
		
		public Builder changedComponents(Iterable<String> changedComponents) {
			this.changedComponents = changedComponents;
			return this;
		}
		
		public Builder removedComponents(Iterable<String> removedComponents) {
			this.removedComponents = removedComponents;
			return this;
		}
		
		public CommitChange build() {
			return new CommitChange(containerId, newComponents, changedComponents, removedComponents);
		}
		
	}
	
	private final String containerId;
	private final Set<String> newComponents;
	private final Set<String> changedComponents;
	private final Set<String> removedComponents;

	@JsonCreator
	private CommitChange(
			@JsonProperty("containerId") String containerId, 
			@JsonProperty("newComponents") Iterable<String> newComponents, 
			@JsonProperty("changedComponents") Iterable<String> changedComponents, 
			@JsonProperty("removedComponents") Iterable<String> removedComponents) {
		this.containerId = containerId;
		this.newComponents = Collections3.toImmutableSet(newComponents);
		this.changedComponents = Collections3.toImmutableSet(changedComponents);
		this.removedComponents = Collections3.toImmutableSet(removedComponents);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(containerId, newComponents, changedComponents, removedComponents);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CommitChange other = (CommitChange) obj;
		return Objects.equals(containerId, other.containerId)
				&& Objects.equals(newComponents, other.newComponents)
				&& Objects.equals(changedComponents, other.changedComponents)
				&& Objects.equals(removedComponents, other.removedComponents);
	}
	
	/**
	 * @return the identifier of the container component that is related to the actual changes (mainly a component that can show the history of multiple minor components)
	 */
	public String getContainerId() {
		return containerId;
	}
	
	/**
	 * @return a set of identifiers that are marked as NEW in the corresponding commit.
	 */
	public Set<String> getNewComponents() {
		return newComponents;
	}
	
	/**
	 * @return a set of identifiers that are marked as CHANGED in the corresponding commit.
	 */
	public Set<String> getChangedComponents() {
		return changedComponents;
	}
	
	/**
	 * @return a set of identifiers that are marked as REMOVED in the corresponding commit.
	 */
	public Set<String> getRemovedComponents() {
		return removedComponents;
	}

}
