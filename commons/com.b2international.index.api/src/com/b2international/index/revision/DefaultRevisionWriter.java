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
package com.b2international.index.revision;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Writer;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public class DefaultRevisionWriter implements RevisionWriter {

	private final RevisionBranchRef branch;
	
	private final Writer index;
	private final RevisionSearcher searcher;
	
	private final RevisionBranchPoint created;
	private final RevisionBranchPoint revised;
	
	private final Map<Class<? extends Revision>, Collection<Long>> revisionUpdates = newHashMap();

	public DefaultRevisionWriter(
			final RevisionBranchRef branch,
			long commitTimestamp,
			Writer index, 
			RevisionSearcher searcher) {
		this.branch = branch;
		this.index = index;
		this.searcher = searcher;
		this.created = new RevisionBranchPoint(branch.branchId(), commitTimestamp);
		this.revised = new RevisionBranchPoint(branch.branchId(), Long.MAX_VALUE);
	}

	@Override
	public void put(long storageKey, Revision object) {
		checkArgument(storageKey > 0, "StorageKey cannot be negative or zero");
		if (!revisionUpdates.containsKey(object.getClass())) {
			revisionUpdates.put(object.getClass(), Sets.<Long>newHashSet());
		}
		final Collection<Long> revisionsToUpdate = revisionUpdates.get(object.getClass());
		// prevent duplicated revisions
		checkArgument(!revisionsToUpdate.contains(storageKey), "duplicate revision %s", storageKey);
		revisionsToUpdate.add(storageKey);
		
		object.setStorageKey(storageKey);
		object.setCreated(created);
		index.put(generateRevisionId(), object);
	}

	@Override
	public void putAll(Map<Long, Revision> revisionsByStorageKey) {
		for (Entry<Long, Revision> doc : revisionsByStorageKey.entrySet()) {
			put(doc.getKey(), doc.getValue());
		}
	}

	@Override
	public void remove(Class<? extends Revision> type, long storageKey) {
		remove(type, Collections.singleton(storageKey));
	}
	
	@Override
	public void remove(Class<? extends Revision> type, Collection<Long> storageKeys) {
		removeAll(ImmutableMap.<Class<? extends Revision>, Collection<Long>>of(type, storageKeys));
	}

	@Override
	public void removeAll(Map<Class<? extends Revision>, Collection<Long>> storageKeysByType) {
		final String oldRevised = revised.toIpAddress();
		final String newRevised = created.toIpAddress();
		for (Class<? extends Revision> type : storageKeysByType.keySet()) {
			final Collection<Long> storageKeysToUpdate = storageKeysByType.get(type);
			if (!storageKeysToUpdate.isEmpty()) {
				final Expression filter = Expressions.builder()
							.filter(Expressions.matchAnyLong(Revision.STORAGE_KEY, storageKeysToUpdate))
							.filter(Revision.toRevisionFilter(branch.segments()))
							.build();
				final BulkUpdate<Revision> update = new BulkUpdate<Revision>(type, filter, DocumentMapping._ID, Revision.UPDATE_REVISED, ImmutableMap.of("oldRevised", oldRevised, "newRevised", newRevised));
				index.bulkUpdate(update);
			}
		}
	}

	@Override
	public void commit() throws IOException {
		// before commit, mark all previous revisions as replaced
		removeAll(revisionUpdates);
		index.bulkUpdate(
			new BulkUpdate<>(
				RevisionBranch.class, 
				DocumentMapping.matchId(branch()), 
				DocumentMapping._ID, 
				RevisionBranch.Scripts.WITH_HEADTIMESTAMP, 
				ImmutableMap.of("headTimestamp", created.getTimestamp())
			)
		);
		index.commit();
	}

	@Override
	public String branch() {
		return branch.path();
	}
	
	@Override
	public RevisionSearcher searcher() {
		return searcher;
	}
	
	@Override
	public Writer writer() {
		return index;
	}
	
	private String generateRevisionId() {
		return UUID.randomUUID().toString();
	}

}
