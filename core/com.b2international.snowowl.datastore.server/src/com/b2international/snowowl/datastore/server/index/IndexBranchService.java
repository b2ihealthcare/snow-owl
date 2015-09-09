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

import static com.b2international.snowowl.datastore.BranchPathUtils.isBasePath;
import static com.b2international.snowowl.datastore.BranchPathUtils.isMain;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getLast;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.collections.BackwardListIterator;
import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DelimiterStopAnalyzer;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.NullSearcherManager;
import com.b2international.snowowl.datastore.server.internal.lucene.index.FilteringMergePolicy;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

public class IndexBranchService implements Closeable {

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

	private static final IndexCommitFunction<Boolean> COMMIT_EXISTS = new IndexCommitFunction<Boolean>() {
		@Override 
		public Boolean apply(final IndexCommit commit) {
			return true;
		}

		@Override 
		public Boolean getDefault() {
			return false;
		}
	};

	private volatile boolean closed;

	private Directory directory;
	private IndexWriter indexWriter;
	private ReferenceManager<IndexSearcher> manager;
	private boolean firstStartupAtMain;

	private final IDirectoryManager directoryManager;
	private final BranchPath branchPath;
	private final FilteringMergePolicy mergePolicy;
	private final boolean readOnly;

	public IndexBranchService(final IBranchPath branchPath, final BranchPath cdoBranchPath, final IDirectoryManager directoryManager) throws IOException {

		this.directoryManager = checkNotNull(directoryManager, "directoryManager");
		this.branchPath = cdoBranchPath;
		this.mergePolicy = new FilteringMergePolicy(new LogByteSizeMergePolicy());
		this.readOnly = isBasePath(branchPath);
		this.directory = directoryManager.openDirectory(cdoBranchPath, readOnly);

		if (!isMain(branchPath) && !DirectoryReader.indexExists(directory)) {

			if (!readOnly) {
				final BranchPath baseCdoBranchPath = cdoBranchPath.parent();
				try (final Directory baseDirectory = directoryManager.openDirectory(baseCdoBranchPath, true)) {

					final IndexCommit commit = getIndexCommit(baseDirectory, cdoBranchPath);
					if (commit == null) {
						this.indexWriter = null;
						this.manager = NullSearcherManager.getInstance();
					} else {
						this.indexWriter = createIndexWriter(false);
						this.mergePolicy.setMinSegmentCount(getSegmentInfoCounter(baseDirectory));
						this.manager = createSearcherManager();
					}
				}
			} else {
				this.indexWriter = null;
				this.manager = createSearcherManager();
			}

		} else {
			this.indexWriter = createIndexWriter(isMain(branchPath));
			this.manager = createSearcherManager();
		}
	}

	public ReferenceManager<IndexSearcher> getManager() {
		return manager;
	}

	@Override
	public void close() {

		if (closed) {
			return;
		}

		closed = true;
		IOException caught = null;

		// Close components in reverse order.
		if (null != manager) {
			try {
				manager.close();
			} catch (final IOException e) {
				caught = e;
			} finally {
				manager = null;
			}
		}

		if (null != indexWriter) {
			try {
				indexWriter.rollback();
			} catch (final IOException e) {
				if (caught != null) {
					caught.addSuppressed(e);
				} else {
					caught = e;
				}
			} finally {
				indexWriter = null;
			}
		}

		if (null != directory) {
			try {
				directory.close();
			} catch (final IOException e) {
				if (caught != null) {
					caught.addSuppressed(e);
				} else {
					caught = e;
				}
			} finally {
				directory = null;
			}
		}

		if (caught != null) {
			throw new IndexException("Error while closing index branch service. [" + branchPath.path() + "]", caught);
		}
	}

	public IDirectoryManager getDirectoryManager() {
		return directoryManager;
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public void addDocument(final Document document) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.addDocument(document);
		}
	}

	public void deleteDocuments(final Query query) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.deleteDocuments(query);
		}
	}

	public void deleteDocuments(final Term term) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.deleteDocuments(term);
		}
	}

	public void updateDocument(final Term term, final Document document) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.updateDocument(term, document);
		}
	}

	public void commit() throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.setCommitData(Collections.<String, String>emptyMap()); //override snapshot commit data
			indexWriter.commit();
			manager.maybeRefreshBlocking();
		}
	}

	public void optimize() throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.forceMerge(1);
			commit();
		}
	}

	public void rollback() throws IOException {
		ensureOpen();
		ensureWritable();

		/* 
		 * Roll back the IndexWriter, and recreate it to be able to move forward.
		 * The SearcherManager is not closed, as it is still reading the most recent IndexCommit, which remains valid. 
		 */
		if (null != indexWriter) {
			indexWriter.rollback();
			indexWriter = createIndexWriter(false);
		}
	}

	public void deleteAll() throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			deleteDocuments(new MatchAllDocsQuery());
		}
	}

	public IndexCommit snapshot() throws IOException {
		ensureOpen();
		ensureWritable();

		final SnapshotDeletionPolicy snapshotDeletionPolicy = getSnapshotDeletionPolicy(indexWriter);
		final IndexCommit commit = snapshotDeletionPolicy.snapshot();
		return commit;
	}

	public void releaseSnapshot(final IndexCommit commit) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			final SnapshotDeletionPolicy snapshotDeletionPolicy = getSnapshotDeletionPolicy(indexWriter);
			snapshotDeletionPolicy.release(commit);
		}
	}

	public boolean hasDocuments() throws IOException {

		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = manager.acquire();
			return indexSearcher.getIndexReader().numDocs() > 0;
		} finally {
			if (null != indexSearcher) {
				manager.release(indexSearcher);
			}
		}
	}

	/**
	 * Returns {@code true} if the current index branch service has at least one index commit
	 * representing a snapshot. In other words, at least one task/branch has been created on the current index.
	 * Otherwise returns with {@code false}.
	 */
	public boolean hasSnapshotIndexCommit() throws IOException {
		return getValueFromIndexCommit(directory, IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, Predicates.<String>notNull(), COMMIT_EXISTS);
	}

	/**Returns with the last {@link IndexCommit index commit} from the underlying {@link Directory directory}. Never {@code null}.*/
	public IndexCommit getHeadIndexCommit() {
		try {
			return getLast(DirectoryReader.listCommits(directory));
		} catch (final IOException e) {
			throw new IndexException("Error while getting HEAD commit from " + directory, e);
		}
	}

	@Nullable public IndexCommit getIndexCommit(final BranchPath branchPath) throws IOException {
		return getIndexCommit(directory, checkNotNull(branchPath, "branchPath"));
	}

	void createIndexCommit(final BranchPath branchPath) throws IOException {
		ensureOpen();
		ensureWritable();

		final Map<String, String> userData = ImmutableMap.<String, String>builder()
				.put(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, branchPath.path())
				.build();

		indexWriter.setCommitData(userData);
		indexWriter.commit(); //intentionally not via #commit (reader should not be reopen, commit data should be specified)
		updateMergePolicy();
	}

	public Directory getDirectory() {
		return directory;
	}

	public List<String> listFiles() throws IOException {
		return directoryManager.listFiles(branchPath);
	}

	private void updateMergePolicy() throws IOException {
		mergePolicy.setMinSegmentCount(getSegmentInfoCounter(directory));
	}

	public static IndexCommit getIndexCommit(final Directory directory, final BranchPath branchPath) {
		final String branchPathAsString = branchPath.path();
		
		try {
			return getValueFromIndexCommit(directory, IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, Predicates.equalTo(branchPathAsString), GET_INDEX_COMMIT);
		} catch (final IOException e) {
			throw new IndexException("Couldn't get index commit for CDO branch path: '" + branchPathAsString + "'.", e);
		}
	}

	private static int getSegmentInfoCounter(final Directory _directory) throws IOException {
		try {
			return getValueFromIndexCommit(_directory, IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, Predicates.<String>notNull(), GET_SEGMENT_COUNTER);
		} catch (final IOException e) {
			throw new IndexException("Couldn't get segment generation counter for directory.", e);
		}
	}

	private static <T> T getValueFromIndexCommit(final Directory directory, 
			final String userDataKey, 
			final Predicate<String> userDataPredicate, 
			final IndexCommitFunction<T> indexCommitFunction) throws IOException {

		checkNotNull(userDataKey, "userDataKey");
		checkNotNull(userDataPredicate, "userDataPredicate");
		checkNotNull(indexCommitFunction, "indexCommitFunction");

		// Check most recent index commits first
		final Iterator<IndexCommit> commitItr = new BackwardListIterator<IndexCommit>(DirectoryReader.listCommits(directory));
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

	private IndexWriter createIndexWriter(final boolean commitIfEmpty) throws IOException {
		final Analyzer analyzer = new DelimiterStopAnalyzer();
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		final IndexDeletionPolicy innerPolicy = new KeepBranchPointAndLastCommitDeletionPolicy();
		final SnapshotDeletionPolicy outerPolicy = new SnapshotDeletionPolicy(innerPolicy);
		config.setIndexDeletionPolicy(outerPolicy);
		config.setMergePolicy(mergePolicy);
		indexWriter = new IndexWriter(directory, config);

		// Merges may trim fully deleted segments, which interferes with the overlay directories
		ReflectionUtils.setField(IndexWriter.class, indexWriter, "keepFullyDeletedSegments", true);

		// Create empty index, if it doesn't exist yet
		if (commitIfEmpty && !DirectoryReader.indexExists(directory)) {
			firstStartupAtMain = true;
			indexWriter.commit();
		}

		updateMergePolicy();
		return indexWriter;
	}

	public boolean isFirstStartupAtMain() {
		return firstStartupAtMain;
	}

	private SearcherManager createSearcherManager() throws IOException {
		return new SearcherManager(directory, null);
	}

	private void ensureOpen() {
		if (closed) {
			throw new AlreadyClosedException("Index branch service is already closed.");
		}
	}

	private void ensureWritable() {
		if (readOnly) {
			throw new UnsupportedOperationException("Index branch service is read-only.");
		}
	}

	private SnapshotDeletionPolicy getSnapshotDeletionPolicy(final IndexWriter indexWriter) {
		final IndexDeletionPolicy policy = indexWriter.getConfig().getIndexDeletionPolicy();

		if (!(policy instanceof SnapshotDeletionPolicy)) {
			throw new IllegalStateException("IndexWriter has no SnapshotDeletionPolicy configured.");
		} else {
			return (SnapshotDeletionPolicy) policy;
		}
	}
}
