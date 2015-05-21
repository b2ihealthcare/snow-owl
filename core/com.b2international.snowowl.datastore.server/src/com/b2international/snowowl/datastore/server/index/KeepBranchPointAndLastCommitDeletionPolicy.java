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
import java.util.List;
import java.util.ListIterator;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;

/**
 * Customized {@link IndexDeletionPolicy index deletion policy} for keeping only
 * the most recent commit from all commits without any user data assigned
 * and the most recent ones per versions. Other index commits will
 * be purged on index writer open and commit events.
 *
 */
public class KeepBranchPointAndLastCommitDeletionPolicy extends IndexDeletionPolicy {

	@Override
	public void onInit(final List<? extends IndexCommit> commits) throws IOException {
		onCommit(commits);
	}

	@Override
	public void onCommit(final List<? extends IndexCommit> commits) throws IOException {

		final ListIterator<? extends IndexCommit> commitIterator = commits.listIterator(commits.size());
		boolean latestFound = false;

		while (commitIterator.hasPrevious()) {
			final IndexCommit commit = commitIterator.previous();

			if (!commit.getUserData().isEmpty()) {
				continue;
			}

			if (!latestFound) {
				latestFound = true;
			} else {
				commit.delete();
			}
		}
	}
}
