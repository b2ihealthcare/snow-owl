/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.index.mapping.DocumentMapping;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @since 7.0
 */
public final class RevisionBranchChangeSet {

	private final DefaultRevisionIndex index;
	private final RevisionBranchRef ref;
	private final Multimap<Class<? extends Revision>, String> newRevisionIdsByType = HashMultimap.create();
	private final Multimap<Class<? extends Revision>, String> changedRevisionIdsByType = HashMultimap.create();
	private final Multimap<Class<? extends Revision>, String> removedRevisionIdsByType = HashMultimap.create();
	private final Map<ObjectId, ObjectId> containersRequiredForNewAndChangedRevisions = newHashMap();
	
	RevisionBranchChangeSet(DefaultRevisionIndex index, RevisionBranchRef ref, RevisionCompare compare) {
		this.index = index;
		this.ref = ref;
		compare.getDetails().forEach(detail -> {
			// add all objects to the tx
			if (detail.isAdd()) {
				Class<?> revType = DocumentMapping.getClass(detail.getComponent().type());
				if (Revision.class.isAssignableFrom(revType)) {
					newRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
					// XXX since there is a new detail for the component, it cannot be a changed revision, remove if it were added due to the next few statement 
					changedRevisionIdsByType.remove((Class<? extends Revision>) revType, detail.getComponent().id());
					if (!detail.getObject().isRoot()) {
						Class<?> objectType = DocumentMapping.getClass(detail.getObject().type());
						containersRequiredForNewAndChangedRevisions.put(detail.getComponent(), detail.getObject());
						// only register as changed container, if the container is not registered as a new revision
						if (!newRevisionIdsByType.containsEntry((Class<? extends Revision>) objectType, detail.getObject().id())) {
							changedRevisionIdsByType.put((Class<? extends Revision>) objectType, detail.getObject().id());
						}
					}
				}
			} else if (detail.isChange()) {
				if (detail.isPropertyChange()) {
					Class<?> revType = DocumentMapping.getClass(detail.getObject().type());
					if (Revision.class.isAssignableFrom(revType)) {
						changedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getObject().id());
					}
				} else {
					Class<?> revType = DocumentMapping.getClass(detail.getComponent().type());
					if (Revision.class.isAssignableFrom(revType)) {
						changedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
					}
				}
			} else if (detail.isRemove()) {
				Class<?> revType = DocumentMapping.getClass(detail.getComponent().type());
				if (Revision.class.isAssignableFrom(revType)) {
					removedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
					// XXX since there is a remove detail for the component, it cannot be a changed revision, remove if it were added due to the next few statement 
					changedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
					if (!detail.getObject().isRoot()) {
						Class<?> objectType = DocumentMapping.getClass(detail.getObject().type());						
						changedRevisionIdsByType.put((Class<? extends Revision>) objectType, detail.getObject().id());
						// only register as removed container, if the container is not registered as a removed revision
						if (!removedRevisionIdsByType.containsEntry((Class<? extends Revision>) objectType, detail.getObject().id())) {
							changedRevisionIdsByType.put((Class<? extends Revision>) objectType, detail.getObject().id());
						}
					}
				}
			} else {
				throw new UnsupportedOperationException("Unsupported diff operation: " + detail.getOp());
			}
		});
	}

	public Collection<Class<? extends Revision>> getAddedTypes() {
		return ImmutableSet.copyOf(newRevisionIdsByType.keySet());
	}
	
	public Collection<Class<? extends Revision>> getChangedTypes() {
		return ImmutableSet.copyOf(changedRevisionIdsByType.keySet());
	}
	
	public Collection<Class<? extends Revision>> getRemovedTypes() {
		return ImmutableSet.copyOf(removedRevisionIdsByType.keySet());
	}

	public Set<String> getAddedIds(Class<? extends Revision> type) {
		return ImmutableSet.copyOf(newRevisionIdsByType.get(type));
	}
	
	public Set<String> getChangedIds(Class<? extends Revision> type) {
		return ImmutableSet.copyOf(changedRevisionIdsByType.get(type));
	}
	
	public Set<String> getRemovedIds(Class<? extends Revision> type) {
		return ImmutableSet.copyOf(removedRevisionIdsByType.get(type));
	}

	public ObjectId getContainerId(ObjectId revisionId) {
		return containersRequiredForNewAndChangedRevisions.get(revisionId);
	}

	public boolean isRemoved(ObjectId revisionId) {
		return removedRevisionIdsByType.containsEntry(DocumentMapping.getClass(revisionId.type()), revisionId.id());
	}

	public Set<String> getAddedIds() {
		return ImmutableSet.copyOf(newRevisionIdsByType.values());
	}
	
	public Set<String> getChangedIds() {
		return ImmutableSet.copyOf(changedRevisionIdsByType.values());
	}
	
	public Set<String> getRemovedIds() {
		return ImmutableSet.copyOf(removedRevisionIdsByType.values());
	}

	public void removeChanged(Class<? extends Revision> type, String id) {
		changedRevisionIdsByType.remove(type, id);
	}

	public <T> T read(RevisionIndexRead<T> read) {
		return index.read(ref, read);
	}

}
