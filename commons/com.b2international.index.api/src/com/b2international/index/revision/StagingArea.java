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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * A place that stores information about what will go into your next commit.
 * 
 * @since 6.6
 * @see RevisionIndex#prepareCommit()
 */
public final class StagingArea {

	private final DefaultRevisionIndex index;
	private final String branchPath;

	private Map<String, Object> newDocuments;
	private Map<String, Object> changedDocuments;
	private Map<String, Object> removedDocuments;
	
	StagingArea(DefaultRevisionIndex index, String branchPath) {
		this.index = index;
		this.branchPath = branchPath;
		reset();
	}
	
	/**
	 * Commits the changes so far staged to the staging area.
	 *  
	 * @param timestamp
	 * @param author
	 * @param commitComment
	 * @return
	 */
	public Commit commit(long timestamp, String author, String commitComment) {
		return commit(null, timestamp, author, commitComment);
	}
	
	/**
	 * Commits the changes so far staged to the staging area.
	 *  
	 * @param commitGroupId - can be used to connect multiple commits and consider them as a single commit
	 * @param timestamp - long timestamp when the commit happened
	 * @param author - the author of the changes
	 * @param commitComment - short text about the changes
	 * @return
	 */
	public Commit commit(String commitGroupId, long timestamp, String author, String commitComment) {
		return index.write(branchPath, timestamp, writer -> {
			Commit.Builder commit = Commit.builder()
					.id(UUID.randomUUID().toString())
					.groupId(commitGroupId)
					.author(author)
					.branch(branchPath)
					.comment(commitComment)
					.timestamp(timestamp);

			final Multimap<String, String> newComponents = HashMultimap.create();
			final Multimap<String, String> changedComponents = HashMultimap.create();
			final Multimap<String, String> removedComponents = HashMultimap.create();
			final Multimap<Class<?>, String> deletedIdsByType = HashMultimap.create();
			
			removedDocuments.forEach((key, value) -> {
				deletedIdsByType.put(value.getClass(), key);
				if (value instanceof Revision) {
					removedComponents.put(((Revision) value).getContainerId(), key);
				}
			});
			
			
			// apply removals first
			for (Class<?> type : deletedIdsByType.keySet()) {
				final ImmutableSet<String> deletedDocIds = ImmutableSet.copyOf(deletedIdsByType.get(type));
				writer.remove(type, deletedDocIds);
			}
			
			// then new documents and revisions
			for (Entry<String, Object> doc : newDocuments.entrySet()) {
				if (!removedDocuments.containsKey(doc.getKey())) {
					Object document = doc.getValue();
					writer.put(doc.getKey(), document);
					if (document instanceof Revision) {
						Revision rev = (Revision) document;
						newComponents.put(checkNotNull(rev.getContainerId(), "Missing containerId for revision: " + rev), rev.getId());
					}
				}
			}
			
			// and changed documents
			for (Entry<String, Object> doc : changedDocuments.entrySet()) {
				if (!removedDocuments.containsKey(doc.getKey())) {
					Object document = doc.getValue();
					writer.put(doc.getKey(), document);
					if (document instanceof Revision) {
						Revision rev = (Revision) document;
						changedComponents.put(checkNotNull(rev.getContainerId(), "Missing containerId for revision: " + rev), rev.getId());
					}
				}
			}
			
			// collect changes and register them by container ID
			final List<CommitChange> changes = ImmutableSet.<String>builder()
				.addAll(newComponents.keySet())
				.addAll(changedComponents.keySet())
				.addAll(removedComponents.keySet())
				.build()
				.stream()
				.map(containerId -> {
					return CommitChange.builder(containerId)
						.newComponents(newComponents.get(containerId))
						.changedComponents(changedComponents.get(containerId))
						.removedComponents(removedComponents.get(containerId))
						.build();
				})
				.collect(Collectors.toList());
			
			Commit commitDoc = commit.changes(changes).build();
			writer.put(commitDoc.getId(), commitDoc);
			writer.commit();
			reset();
			return commitDoc;
		});
	}

	/**
	 * Reset staging area to empty.
	 */
	private void reset() {
		newDocuments = newHashMap();
		changedDocuments = newHashMap();
		removedDocuments = newHashMap();
	}

	public StagingArea stageNew(Revision newRevision) {
		return stageNew(newRevision.getId(), newRevision);
	}
	
	public StagingArea stageNew(String key, Object newDocument) {
		newDocuments.put(key, newDocument);
		return this;
	}
	
	public StagingArea stageChange(Revision changedRevision) {
		return stageChange(changedRevision.getId(), changedRevision);
	}
	
	public StagingArea stageChange(String key, Object changed) {
		changedDocuments.put(key, changed);
		return this;
	}
	
	public StagingArea stageRemove(Revision removedRevision) {
		return stageRemove(removedRevision.getId(), removedRevision);
	}

	public StagingArea stageRemove(String key, Object removed) {
		removedDocuments.put(key, removed);
		return this;
	}

}
