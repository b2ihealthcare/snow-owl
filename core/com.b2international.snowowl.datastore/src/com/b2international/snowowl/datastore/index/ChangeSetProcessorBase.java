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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import com.b2international.index.revision.Revision;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public abstract class ChangeSetProcessorBase implements ChangeSetProcessor {

	private final String description;
	private final Collection<Revision> newMappings = newArrayList();
	private final Collection<Revision> changedMappings = newArrayList();
	private final Multimap<Class<? extends Revision>, String> deletions = HashMultimap.create();

	protected ChangeSetProcessorBase(String description) {
		this.description = description;
	}
	
	@Override
	public final String description() {
		return description;
	}
	
	protected final void indexNewRevision(Revision revision) {
		newMappings.add(revision);
	}
	
	protected final void indexChangedRevision(Revision revision) {
		changedMappings.add(revision);
	}
	
	protected final void deleteRevisions(Class<? extends Revision> type, Collection<String> keys) {
		deletions.putAll(type, keys);
	}
	
	@Override
	public final Collection<Revision> getNewMappings() {
		return newMappings;
	}
	
	@Override
	public final Collection<Revision> getChangedMappings() {
		return changedMappings;
	}
	
	@Override
	public final Multimap<Class<? extends Revision>, String> getDeletions() {
		return deletions;
	}
	
}