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

import java.util.Collection;
import java.util.Map;

import com.b2international.index.revision.Revision;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public abstract class ChangeSetProcessorBase implements ChangeSetProcessor {

	private final String description;
	private final Map<String, RevisionDocument> newMappings = newHashMap();
	private final Map<String, RevisionDocumentChange> changedMappings = newHashMap();
	private final Multimap<Class<? extends Revision>, String> deletions = HashMultimap.create();

	protected ChangeSetProcessorBase(String description) {
		this.description = description;
	}
	
	@Override
	public final String description() {
		return description;
	}
	
	protected final void indexNewRevision(RevisionDocument revision) {
		Revision prev = newMappings.put(revision.getId(), revision);
		if (prev != null) {
			throw new IllegalArgumentException("Multiple entries with same key: " + revision.getId() + "=" + revision);
		}
	}
	
	protected final void indexChangedRevision(RevisionDocument oldRevision, RevisionDocument newRevision) {
		RevisionDocumentChange prev = changedMappings.put(newRevision.getId(), new RevisionDocumentChange(oldRevision, newRevision));
		if (prev != null) {
			throw new IllegalArgumentException("Multiple entries with same key: " + newRevision.getId() + "=" + prev);
		}
	}
	
	protected final void deleteRevisions(Class<? extends Revision> type, Collection<String> keys) {
		deletions.putAll(type, keys);
	}
	
	@Override
	public final Map<String, RevisionDocument> getNewMappings() {
		return newMappings;
	}
	
	@Override
	public final Map<String, RevisionDocumentChange> getChangedMappings() {
		return changedMappings;
	}
	
	@Override
	public final Multimap<Class<? extends Revision>, String> getDeletions() {
		return deletions;
	}
	
}