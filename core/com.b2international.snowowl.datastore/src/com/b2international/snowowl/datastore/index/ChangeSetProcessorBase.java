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
import java.util.Collections;
import java.util.Map;

import com.b2international.index.revision.Revision;

/**
 * @since 4.3
 */
public abstract class ChangeSetProcessorBase implements ChangeSetProcessor {

	private final String description;
	
	private Map<String, RevisionDocument> newMappings;
	private Map<String, RevisionDocumentChange> changedMappings;
	private Collection<RevisionDocument> deletions;

	protected ChangeSetProcessorBase(String description) {
		this.description = description;
	}
	
	@Override
	public final String description() {
		return description;
	}
	
	protected final void stageNew(RevisionDocument revision) {
		if (newMappings == null) {
			newMappings = newHashMap();
		}
		Revision prev = newMappings.put(revision.getId(), revision);
		if (prev != null) {
			throw new IllegalArgumentException("Multiple entries with same key: " + revision.getId() + "=" + revision);
		}
	}
	
	protected final void stageChange(RevisionDocument oldRevision, RevisionDocument newRevision) {
		if (changedMappings == null) {
			changedMappings = newHashMap();
		}
		RevisionDocumentChange prev = changedMappings.put(newRevision.getId(), new RevisionDocumentChange(oldRevision, newRevision));
		if (prev != null) {
			throw new IllegalArgumentException("Multiple entries with same key: " + newRevision.getId() + "=" + prev);
		}
	}
	
	protected final void stageRemove(RevisionDocument revision) {
		deletions.add(revision);
	}
	
	@Override
	public final Map<String, RevisionDocument> getNewMappings() {
		return newMappings == null ? Collections.emptyMap() : newMappings;
	}
	
	@Override
	public final Map<String, RevisionDocumentChange> getChangedMappings() {
		return changedMappings == null ? Collections.emptyMap() : changedMappings;
	}
	
	@Override
	public final Collection<RevisionDocument> getDeletions() {
		return deletions == null ? Collections.emptyList() : deletions;
	}
	
}