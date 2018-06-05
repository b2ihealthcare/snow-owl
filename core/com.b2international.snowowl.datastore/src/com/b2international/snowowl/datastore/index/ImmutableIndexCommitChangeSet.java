/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ComponentIdentifier;

/**
 * @since 5.0
 */
public final class ImmutableIndexCommitChangeSet implements IndexCommitChangeSet {

	private final String commitId;
	private final Map<ComponentIdentifier, Object> newObjects;
	private final Map<ComponentIdentifier, Object> changedObjects;
	private final Map<ComponentIdentifier, Object> removedObjects;

	private ImmutableIndexCommitChangeSet(
			final String commitId,
			final Map<ComponentIdentifier, Object> newObjects,
			final Map<ComponentIdentifier, Object> changedObjects,
			final Map<ComponentIdentifier, Object> removedObjects) {
		this.commitId = commitId;
		this.newObjects = newObjects;
		this.changedObjects = changedObjects;
		this.removedObjects = removedObjects;
	}
	
	@Override
	public boolean isEmpty() {
		return newObjects.isEmpty() && changedObjects.isEmpty() && removedObjects.isEmpty();
	}
	
	@Override
	public String getCommitId() {
		return commitId;
	}

	@Override
	public Map<ComponentIdentifier, Object> getNewObjects() {
		return newObjects;
	}
	
	@Override
	public Map<ComponentIdentifier, Object> getChangedObjects() {
		return changedObjects;
	}
	
	@Override
	public Map<ComponentIdentifier, Object> getRemovedObjects() {
		return removedObjects;
	}
	
	@Override
	public IndexCommitChangeSet merge(IndexCommitChangeSet indexCommitChangeSet) {
		return builder().from(this).from(indexCommitChangeSet).build();
	}
	
	/**
	 * Apply this {@link ImmutableIndexCommitChangeSet} on the given {@link StagingArea staging area}.
	 * 
	 * @param staging
	 */
	@Override
	public void apply(StagingArea staging) {
		newObjects.forEach((id, object) -> {
			staging.stageNew(id.getComponentId(), object);
		});
		changedObjects.forEach((id, object) -> {
			staging.stageChange(id.getComponentId(), object);
		});
		removedObjects.forEach((id, object) -> {
			staging.stageRemove(id.getComponentId(), object);
		});
	}
	
	/**
	 * Returns a human-readable description of this changes containing the number of add/updated and deleted documents. 
	 * @return
	 */
	@Override
	public String getDescription() {
		return String.format("Index Changes[+%d, ~%d, -%d]", newObjects.size(), changedObjects.size(), removedObjects.size());
	}
	
	/**
	 * Create a new {@link Builder} instance.
	 * 
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * Builder to simplify construction of {@link ImmutableIndexCommitChangeSet} objects.
	 * 
	 * @since 5.0
	 */
	public static class Builder {

		private final Map<ComponentIdentifier, Object> newObjects = newHashMap();
		private final Map<ComponentIdentifier, Object> changedObjects = newHashMap();
		private final Map<ComponentIdentifier, Object> removedObjects = newHashMap();
		private String commitId;

		private Builder() {
		}
		
		public Builder from(IndexCommitChangeSet from) {
			if (commitId == null) {
				commitId = from.getCommitId();
			}
			this.newObjects.putAll(from.getNewObjects());
			this.changedObjects.putAll(from.getChangedObjects());
			this.removedObjects.putAll(from.getRemovedObjects());
			return this;
		}
		
		public Builder putNewObject(ComponentIdentifier newComponentId, Object newComponent) {
			Object prev = this.newObjects.put(newComponentId, newComponent);
			if (prev != null) {
				throw new IllegalArgumentException("Multiple entries with same key: " + newComponentId + "=" + newComponent);
			}
			return this;
		}
		
		public Builder putChangedObject(ComponentIdentifier changedComponentId, Object changedComponent) {
			Object prev = this.changedObjects.put(changedComponentId, changedComponent);
			if (prev != null) {
				throw new IllegalArgumentException("Multiple entries with same key: " + changedComponentId + "=" + changedComponent);
			}
			return this;
		}
		
		public Builder putRemovedComponent(ComponentIdentifier removedComponentId, Object removedComponent) {
			Object prev = this.removedObjects.put(removedComponentId, removedComponent);
			if (prev != null) {
				throw new IllegalArgumentException("Multiple entries with same key: " + removedComponentId + "=" + removedComponent);
			}
			return this;
		}
		
		public Builder commitId(String commitId) {
			this.commitId = commitId;
			return this;
		}
		
		public IndexCommitChangeSet build() {
			return new ImmutableIndexCommitChangeSet(commitId, newObjects, changedObjects, removedObjects);
		}

	}

}
