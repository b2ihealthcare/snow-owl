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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getLast;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;

import com.b2international.commons.collections.BackwardListIterator;
import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

/**
 * Wraps a Lucene {@link Directory} instance, adding extra functionality primarily for manipulating 
 * index commits contained in the directory.
 */
public class IndexDirectory {

	private static final IndexCommitFunction<Integer> GET_SEGMENT_COUNTER = new IndexCommitFunction<Integer>() {
		@Override 
		public Integer apply(final IndexCommit commit) {
			try {
				final SegmentInfos segmentInfos = new SegmentInfos();
				segmentInfos.read(commit.getDirectory(), commit.getSegmentsFileName());
				return segmentInfos.counter;
			} catch (final IOException e) {
				throw new SnowowlRuntimeException("Failed to extract segment counter from index commit: " + commit);
			}
		}

		@Override 
		public Integer getDefault() {
			return 0;
		}
	};

	private static final IndexCommitFunction<IndexCommit> GET_INDEX_COMMIT = new IndexCommitFunction<IndexCommit>() {
		@Override 
		public IndexCommit apply(final IndexCommit commit) {
			return commit;
		}

		@Override 
		public IndexCommit getDefault() {
			return null;
		}
	};

	private final Directory directory;

	public IndexDirectory(final Directory directory) {
		checkNotNull(directory, "Directory may not be null.");
		this.directory = directory;
	}

	/**Returns with the last {@link IndexCommit index commit} from the underlying {@link Directory directory}. Never {@code null}.*/
	public IndexCommit getLastIndexCommit() {
		try {
			return getLast(listCommits());
		} catch (final IOException e) {
			throw new IndexException("Error while getting HEAD commit from " + directory, e);
		}
	}

	public IndexCommit getLastBaseIndexCommit(final IBranchPath logicalBranchPath) {
		return getLastBaseIndexCommitForKeyValue(IndexUtils.INDEX_BRANCH_PATH_KEY, logicalBranchPath.getPath());
	}

	public IndexCommit getLastBaseIndexCommit(final BranchPath physicalBranchPath) {
		return getLastBaseIndexCommitForKeyValue(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, physicalBranchPath.path());
	}

	private IndexCommit getLastBaseIndexCommitForKeyValue(final String key, final String branchPathAsString) {
		try {
			return getValueFromIndexCommit(key, Predicates.equalTo(branchPathAsString), GET_INDEX_COMMIT);
		} catch (final IOException e) {
			throw new IndexException("Couldn't get index commit for CDO branch path: '" + branchPathAsString + "'.", e);
		}
	}

	public int getSegmentInfoCounter() {
		try {
			return getValueFromIndexCommit(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, Predicates.<String>notNull(), GET_SEGMENT_COUNTER);
		} catch (final IOException e) {
			throw new IndexException("Couldn't get segment generation counter for directory.", e);
		}
	}

	private <T> T getValueFromIndexCommit(final String userDataKey, 
			final Predicate<String> userDataPredicate, 
			final IndexCommitFunction<T> indexCommitFunction) throws IOException {

		checkNotNull(userDataKey, "userDataKey");
		checkNotNull(userDataPredicate, "userDataPredicate");
		checkNotNull(indexCommitFunction, "indexCommitFunction");

		// Check most recent index commits first
		final Iterator<IndexCommit> commitItr = new BackwardListIterator<IndexCommit>(listCommits());
		while (commitItr.hasNext()) {
			final IndexCommit commit = commitItr.next();
			final Map<String, String> userData = commit.getUserData();
			final String value = userData.get(userDataKey);
			if (userDataPredicate.apply(value)) {
				return indexCommitFunction.apply(commit);
			}
		}

		return indexCommitFunction.getDefault();
	}

	public List<IndexCommit> listCommits() throws IOException {
		return DirectoryReader.listCommits(directory);
	}

	public List<String> listFiles() throws IOException {
		return ImmutableList.copyOf(directory.listAll());
	}

	public boolean indexExists() throws IOException {
		return DirectoryReader.indexExists(directory);
	}

	public IndexWriter createIndexWriter(final IndexWriterConfig config) throws IOException {
		return new IndexWriter(directory, config);
	}

	public SearcherManager createSearcherManager() throws IOException {
		return new SearcherManager(directory, null);
	}

	public void close() throws IOException {
		directory.close();
	}
}
