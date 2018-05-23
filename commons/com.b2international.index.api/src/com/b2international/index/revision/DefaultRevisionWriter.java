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
import java.util.Set;
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
	
	private final Map<Class<?>, Set<String>> revisionUpdates = newHashMap();

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
	public void put(String key, Object object) {
		if (object instanceof Revision) {
			Revision rev = (Revision) object;
			final Class<? extends Revision> type = rev.getClass();
			if (!revisionUpdates.containsKey(type)) {
				revisionUpdates.put(type, Sets.<String>newHashSet());
			}
			final Collection<String> revisionsToUpdate = revisionUpdates.get(type);
			// prevent duplicated revisions
			checkArgument(!revisionsToUpdate.contains(key), "duplicate revision %s", key);
			revisionsToUpdate.add(key);
			
			rev.setCreated(created);
			index.put(generateRevisionId(), rev);
		} else {
			index.put(key, object);
		}
	}

	@Override
	public <T> void putAll(Map<String, T> objectsByKey) {
		objectsByKey.forEach(this::put);
	}
	
	@Override
	public <T> void bulkUpdate(BulkUpdate<T> update) {
		index.bulkUpdate(update);
	}

	@Override
	public void remove(Class<?> type, String key) {
		remove(type, Collections.singleton(key));
	}
	
	@Override
	public void remove(Class<?> type, Set<String> keysToRemove) {
		removeAll(ImmutableMap.of(type, keysToRemove));
	}
	
	@Override
	public void removeAll(Map<Class<?>, Set<String>> keysByType) {
		final String oldRevised = revised.toIpAddress();
		final String newRevised = created.toIpAddress();
		for (Class<?> type : keysByType.keySet()) {
			final Set<String> keysToUpdate = keysByType.get(type);
			if (Revision.class.isAssignableFrom(type)) {
				if (!keysToUpdate.isEmpty()) {
					final Expression filter = Expressions.builder()
							.filter(Expressions.matchAny(Revision.Fields.ID, keysToUpdate))
							.filter(Revision.toRevisionFilter(branch.segments()))
							.build();
					final BulkUpdate<Revision> update = new BulkUpdate<Revision>((Class<? extends Revision>) type, filter, DocumentMapping._ID, Revision.UPDATE_REVISED, ImmutableMap.of("oldRevised", oldRevised, "newRevised", newRevised));
					index.bulkUpdate(update);
				}
			} else {
				index.remove(type, keysToUpdate);
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
	
	private String generateRevisionId() {
		return UUID.randomUUID().toString();
	}

}
