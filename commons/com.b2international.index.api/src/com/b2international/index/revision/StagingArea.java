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
	private final Multimap<Class<?>, String> removedDocuments = HashMultimap.create();
	
	StagingArea(DefaultRevisionIndex index) {
		this.index = index;
	}
	
	/**
	 * Commits the changes so far staged to the staging area. 
	 * @param commitId
	 * @param branchPath
	 * @param timestamp
	 * @param userId
	 * @param commitComment
	 * @return
	 */
	public Commit commit(String commitId, String branchPath, long timestamp, String userId, String commitComment) {
		return index.write(branchPath, timestamp, writer -> {
			Commit.Builder commit = Commit.builder()
				.id(commitId)
				.userId(userId)
				.branch(branchPath)
				.comment(commitComment)
				.timestamp(timestamp);
			
			// apply removals first
			for (Class<?> type : removedDocuments.keySet()) {
				final ImmutableSet<String> deletedDocIds = ImmutableSet.copyOf(removedDocuments.get(type));
				writer.remove(type, deletedDocIds);
//				commit.deletedComponents(deletedDocIds);
			}
			
			// then new documents and revisions
			for (Entry<String, Object> doc : newDocuments.entrySet()) {
				if (!removedDocuments.containsValue(doc.getKey())) {
					writer.put(doc.getKey(), doc.getValue());
				}
			}
//			commit.newComponents(newDocuments.keySet());
			
			Commit commitDoc = commit.build();
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

	public StagingArea stageRemove(Class<?> type, String key) {
		removedDocuments.put(type, key);
		return this;
	}

}
