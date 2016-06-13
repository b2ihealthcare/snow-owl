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
package com.b2international.index.revision;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.b2international.index.Writer;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public class DefaultRevisionWriter implements RevisionWriter {

	private final String branchPath;
	private final long commitTimestamp;
	private final Writer index;
	private final RevisionSearcher searcher;
	
	private final Map<Class<? extends Revision>, Collection<Long>> revisionUpdates = newHashMap();

	public DefaultRevisionWriter(String branchPath, long commitTimestamp, Writer index, RevisionSearcher searcher) {
		this.branchPath = branchPath;
		this.commitTimestamp = commitTimestamp;
		this.index = index;
		this.searcher = searcher;
	}

	@Override
	public void put(long storageKey, Revision object) throws IOException {
		if (revisionUpdates.containsKey(object.getClass())) {
			revisionUpdates.get(object.getClass()).add(storageKey);
		} else {
			revisionUpdates.put(object.getClass(), newHashSet(storageKey));
		}
		object.setBranchPath(branchPath);
		object.setCommitTimestamp(commitTimestamp);
		object.setStorageKey(storageKey);
		index.put(generateRevisionId(), object);
	}

	@Override
	public void putAll(Map<Long, Revision> revisionsByStorageKey) throws IOException {
		for (Entry<Long, Revision> doc : revisionsByStorageKey.entrySet()) {
			put(doc.getKey(), doc.getValue());
		}
	}

	@Override
	public void remove(Class<? extends Revision> type, long storageKey) throws IOException {
		remove(type, Collections.singleton(storageKey));
	}
	
	@Override
	public void remove(Class<? extends Revision> type, Collection<Long> storageKeys) throws IOException {
		removeAll(ImmutableMap.<Class<? extends Revision>, Collection<Long>>of(type, storageKeys));
	}

	@Override
	public void removeAll(Map<Class<? extends Revision>, Collection<Long>> storageKeysByType) throws IOException {
		final Map<Class<? extends Revision>, Query<? extends Revision>> queriesByType = newHashMap();
		for (Class<? extends Revision> type : storageKeysByType.keySet()) {
			final Collection<Long> storageKeysToUpdate = storageKeysByType.get(type);
			if (!storageKeysToUpdate.isEmpty()) {
				final Expression where = Expressions.matchAnyLong(Revision.STORAGE_KEY, storageKeysToUpdate);
				queriesByType.put(type, Query.builder(type).selectAll().where(where).limit(Integer.MAX_VALUE).build());
			}
		}
		for (Entry<Class<? extends Revision>, Query<? extends Revision>> entry : queriesByType.entrySet()) {
			final Iterable<? extends Revision> revisionsToUpdate = searcher.search(entry.getValue());
			final Map<String, Object> revisionUpdates = newHashMap();
			for (Revision rev : revisionsToUpdate) {
				final Set<ReplacedIn> set = newHashSet();
				final Collection<ReplacedIn> prevReplacedIns = rev.getReplacedIns();
				set.addAll(prevReplacedIns);
				set.add(new ReplacedIn(branchPath, commitTimestamp));
				rev.setReplacedIns(set);
				revisionUpdates.put(rev._id(), rev);
			}
			index.putAll(revisionUpdates);
		}
	}

	@Override
	public void commit() throws IOException {
		// before commit, mark all previous revisions as replaced
		removeAll(revisionUpdates);
		index.commit();
	}

	@Override
	public String branch() {
		return branchPath;
	}
	
	@Override
	public RevisionSearcher searcher() {
		return searcher;
	}
	
	private String generateRevisionId() {
		return UUID.randomUUID().toString();
	}

}
