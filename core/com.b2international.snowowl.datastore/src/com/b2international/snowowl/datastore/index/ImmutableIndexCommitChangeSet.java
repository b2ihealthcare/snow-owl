/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionWriter;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @since 5.0
 */
public final class ImmutableIndexCommitChangeSet implements IndexCommitChangeSet {

	private final Map<String, Object> rawMappings;
	private final Multimap<Class<?>, String> rawDeletions;
	private final Map<Long, Revision> revisionMappings;
	private final Multimap<Class<? extends Revision>, Long> revisionDeletions;
	private final Collection<ComponentIdentifier> newComponents;
	private final Collection<ComponentIdentifier> changedComponents;
	private final Collection<ComponentIdentifier> deletedComponents;

	private ImmutableIndexCommitChangeSet(final Map<String, Object> rawMappings, 
			final Multimap<Class<?>, String> rawDeletions, 
			final Map<Long, Revision> revisionMappings,
			final Multimap<Class<? extends Revision>, Long> revisionDeletions,
			final Collection<ComponentIdentifier> newComponents,
			final Collection<ComponentIdentifier> changedComponents,
			final Collection<ComponentIdentifier> deletedComponents) {
		this.rawMappings = rawMappings;
		this.rawDeletions = rawDeletions;
		this.revisionMappings = revisionMappings;
		this.revisionDeletions = revisionDeletions;
		this.newComponents = newComponents;
		this.changedComponents = changedComponents;
		this.deletedComponents = deletedComponents;
	}
	
	@Override
	public boolean isEmpty() {
		return rawMappings.isEmpty() 
				&& rawDeletions.isEmpty()
				&& revisionMappings.isEmpty()
				&& revisionDeletions.isEmpty();
	}
	
	@Override
	public Collection<ComponentIdentifier> getNewComponents() {
		return newComponents;
	}
	
	@Override
	public Collection<ComponentIdentifier> getChangedComponents() {
		return changedComponents;
	}
	
	@Override
	public Collection<ComponentIdentifier> getDeletedComponents() {
		return deletedComponents;
	}
	
	@Override
	public Map<String, Object> getRawMappings() {
		return rawMappings;
	}
	
	@Override
	public Multimap<Class<?>, String> getRawDeletions() {
		return rawDeletions;
	}
	
	@Override
	public Map<Long, Revision> getRevisionMappings() {
		return revisionMappings;
	}
	
	@Override
	public Multimap<Class<? extends Revision>, Long> getRevisionDeletions() {
		return revisionDeletions;
	}
	
	@Override
	public IndexCommitChangeSet merge(IndexCommitChangeSet indexCommitChangeSet) {
		return builder().from(this).from(indexCommitChangeSet).build();
	}
	
	/**
	 * Apply this {@link ImmutableIndexCommitChangeSet} on the given {@link RevisionWriter index transaction}.
	 * 
	 * @param staging
	 * @throws IOException
	 */
	@Override
	public void apply(StagingArea staging) {
		for (final Class<?> type : rawDeletions.keySet()) {
			rawDeletions.get(type).forEach(key -> staging.stageRemove(type, key));
		}
		
		for (Entry<String, Object> doc : rawMappings.entrySet()) {
			staging.stageNew(doc.getKey(), doc);
		}

		for (Class<? extends Revision> type : revisionDeletions.keySet()) {
			staging.stageRemoveAll(type, revisionDeletions.get(type));
		}

		for (Entry<Long, Revision> doc : revisionMappings.entrySet()) {
			staging.stageNew(doc.getKey(), doc.getValue());
		}
	}
	
	/**
	 * Returns a human-readable description of this changes containing the number of add/updated and deleted documents. 
	 * @return
	 */
	@Override
	public String getDescription() {
		return String.format("Indexed %d documents, deleted %d documents.", rawMappings.size() + revisionMappings.size(), rawDeletions.values().size() + revisionDeletions.values().size());
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

		private final ImmutableMap.Builder<String, Object> rawMappings = ImmutableMap.builder();
		private final ImmutableMultimap.Builder<Class<?>, String> rawDeletions = ImmutableMultimap.builder();
		private final ImmutableMap.Builder<Long, Revision> revisionMappings = ImmutableMap.builder();
		private final ImmutableMultimap.Builder<Class<? extends Revision>, Long> revisionDeletions = ImmutableMultimap.builder();
		private final ImmutableSet.Builder<ComponentIdentifier> newComponents = ImmutableSet.builder();
		private final ImmutableSet.Builder<ComponentIdentifier> changedComponents = ImmutableSet.builder();
		private final ImmutableSet.Builder<ComponentIdentifier> deletedComponents = ImmutableSet.builder();

		private Builder() {
		}
		
		public Builder from(IndexCommitChangeSet indexCommitChangeSet) {
			this.rawMappings.putAll(indexCommitChangeSet.getRawMappings());
			this.rawDeletions.putAll(indexCommitChangeSet.getRawDeletions());
			this.revisionMappings.putAll(indexCommitChangeSet.getRevisionMappings());
			this.revisionDeletions.putAll(indexCommitChangeSet.getRevisionDeletions());
			this.newComponents.addAll(indexCommitChangeSet.getNewComponents());
			this.changedComponents.addAll(indexCommitChangeSet.getChangedComponents());
			this.deletedComponents.addAll(indexCommitChangeSet.getDeletedComponents());
			return this;
		}
		
		public Builder putNewComponents(ComponentIdentifier newComponent) {
			this.newComponents.add(newComponent);
			return this;
		}
		
		public Builder putChangedComponents(ComponentIdentifier changedComponent) {
			this.changedComponents.add(changedComponent);
			return this;
		}
		
		public Builder putDeletedComponents(ComponentIdentifier deletedComponent) {
			this.deletedComponents.add(deletedComponent);
			return this;
		}
		
		public Builder putRawMappings(String key, Object value) {
			this.rawMappings.put(key, value);
			return this;
		}
		
		public Builder putRawDeletions(Class<?> type, String key) {
			this.rawDeletions.put(type, key);
			return this;
		}
		
		public Builder putRawDeletions(Class<?> type, Iterable<? extends String> keys) {
			this.rawDeletions.putAll(type, keys);
			return this;
		}
		
		public Builder putRevisionMappings(Long key, Revision value) {
			this.revisionMappings.put(key, value);
			return this;
		}
		
		public Builder putRevisionMappings(Map<? extends Long, ? extends Revision> mappings) {
			this.revisionMappings.putAll(mappings);
			return this;
		}
		
		public Builder putRevisionDeletions(Multimap<? extends Class<? extends Revision>, ? extends Long> deletions) {
			this.revisionDeletions.putAll(deletions);
			return this;
		}
		
		public Builder putRevisionDeletions(Class<? extends Revision> type, Long storageKey) {
			this.revisionDeletions.put(type, storageKey);
			return this;
		}
		
		public Builder putRevisionDeletions(Class<? extends Revision> type, Iterable<? extends Long> storageKeys) {
			this.revisionDeletions.putAll(type, storageKeys);
			return this;
		}

		public IndexCommitChangeSet build() {
			return new ImmutableIndexCommitChangeSet(rawMappings.build(), 
					rawDeletions.build(), 
					revisionMappings.build(), 
					revisionDeletions.build(),
					newComponents.build(),
					changedComponents.build(),
					deletedComponents.build());
		}

	}

}
