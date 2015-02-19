/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.BackwardListIterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Customized {@link IndexDeletionPolicy index deletion policy} for keeping only 
 * the most recent commit from all commits without any user data assigned 
 * and the most recent ones per versions. Other index commits will 
 * be purged on index writer open and commit events.
 *
 */
public class KeepOnlyMostRecentVersionAndMasterCommitDeletionPolicy extends IndexDeletionPolicy {

	/**Artificial version value representing the HEAD of the master index.*/
	private static final String UNSIGNED_VERSION_VALUE = "unspecified_version_value";
	
	/**The key of the version.*/
	private final String versionKey;

	public KeepOnlyMostRecentVersionAndMasterCommitDeletionPolicy(final String versionKey) {
		Preconditions.checkNotNull(versionKey, "Version key argument cannot be null.");
		this.versionKey = versionKey;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.index.IndexDeletionPolicy#onInit(java.util.List)
	 */
	@Override
	public void onInit(final List<? extends IndexCommit> commits) throws IOException {
		onCommit(commits);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.index.IndexDeletionPolicy#onCommit(java.util.List)
	 */
	@Override
	public void onCommit(final List<? extends IndexCommit> commits) throws IOException {
		
		//sanity check
		if (CompareUtils.isEmpty(commits)) {
			
			return;
			
		}
		
		//initialize a backward iterator 
		final Iterator<IndexCommit> itr = new BackwardListIterator<IndexCommit>(commits);

		//set for storing visited versions
		final Set<String> visitedVersionValues = Sets.newHashSetWithExpectedSize(commits.size() / 2);
		
		//iterate from backward, if we meet with a new value for the version key we just keep iterating
		//but if we meet with an already visited key, we delete it since, it represents a historical revision
		while (itr.hasNext()) {
		
			final IndexCommit commit = itr.next();
			final String versionValue = getVersionValue(commit);
			
			//already a visited element
			if (!visitedVersionValues.add(versionValue)) {
				
				commit.delete();
				itr.remove();
				
			}
			
		}
		
	}

	/**
	 * Returns with the version extracted from the user data associated with the given index commit.
	 * Basically this method return with the value of the {@link Entry} associated with the key given
	 * as {@link #versionKey}. This method will return with {@link #UNSIGNED_VERSION_VALUE} either
	 * the user data is {@code null} or empty, or the entry does not exists with the {@link #versionKey} key. 
	 * @throws IOException if low level exception occurred.  
	 */
	private String getVersionValue(final IndexCommit commit) throws IOException {
		Preconditions.checkNotNull(commit, "Index commit argument cannot be null.");
		final Map<String, String> userData = commit.getUserData();
		if (CompareUtils.isEmpty(userData)) {
			return UNSIGNED_VERSION_VALUE;
		}
		final String $ = userData.get(versionKey);
		return null == $ ? UNSIGNED_VERSION_VALUE : $;
	}

}