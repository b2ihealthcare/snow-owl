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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * A place that stores information about what will go into your next commit.
 * 
 * @since 6.5
 * @see RevisionIndex#prepareCommit()
 */
public final class StagingArea {

	private final DefaultRevisionIndex index;

	private final Map<String, Object> newDocuments = newHashMap();
	private final Map<String, Object> changedDocuments = newHashMap();
	private final Map<String, Object> removedDocuments = newHashMap();
	
	StagingArea(DefaultRevisionIndex index) {
		this.index = index;
	}
	
	/**
	 * Commits the changes so far staged to the staging area. 
	 * @param commitId
	 * @param branchPath
	 * @param timestamp
	 * @param author
	 * @param commitComment
	 * @return
	 */
	public Commit commit(String commitId, String branchPath, long timestamp, String author, String commitComment) {
		return index.write(branchPath, timestamp, writer -> {
			Commit.Builder commit = Commit.builder()
				.id(commitId)
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
						newComponents.put(rev.getContainerId(), rev.getId());
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
						changedComponents.put(rev.getContainerId(), rev.getId());
					}
				}
			}
			
			// collect changes and register them by container ID
			final Map<String, CommitChange.Builder> changesByContainer = newHashMap();
			ImmutableSet.<String>builder()
				.addAll(newComponents.keySet())
				.addAll(changedComponents.keySet())
				.addAll(removedComponents.keySet())
				.build()
				.forEach(containerId -> {
					if (!changesByContainer.containsKey(containerId)) {
						changesByContainer.put(containerId, CommitChange.builder(containerId));
					}
					changesByContainer
						.get(containerId)
						.newComponents(newComponents.get(containerId))
						.changedComponents(changedComponents.get(containerId))
						.removedComponents(removedComponents.get(containerId));
				});
			
			Commit commitDoc = commit
					.changes(changesByContainer.values().stream().map(CommitChange.Builder::build).collect(Collectors.toList()))
					.build();
			writer.put(commitDoc.getId(), commitDoc);
			writer.commit();
			return commitDoc;
		});
	}
	
	public StagingArea stageNew(String key, Object newDocument) {
		newDocuments.put(key, newDocument);
		return this;
	}
	
	public StagingArea stageNew(Revision newRevision) {
		return stageNew(newRevision.getId(), newRevision);
	}
	
	public StagingArea stageChange(Revision changedRevision) {
		changedDocuments.put(changedRevision.getId(), changedRevision);
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
