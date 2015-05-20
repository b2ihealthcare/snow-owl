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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getLast;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DelimiterStopAnalyzer;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.NullSearcherManager;
import com.b2international.snowowl.datastore.server.internal.lucene.index.FilteringMergePolicy;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

public class IndexBranchService implements Closeable {
	
	private static final IndexCommitFunction<Integer> GET_SEGMENT_COUNTER_FUNCTION = new IndexCommitFunction<Integer>() {
		@Override public Integer apply(final IndexCommit commit) {
			checkNotNull(commit, "commit");
			final SegmentInfos segmentInfos = new SegmentInfos();
			try {
				segmentInfos.read(commit.getDirectory(), commit.getSegmentsFileName());
				return segmentInfos.counter;
			} catch (final IOException e) {
				throw new SnowowlRuntimeException("Failed to extract segment counter from index commit: " + commit);
			}
		}
		
		@Override public Integer getDefault() {
			return Integer.valueOf(0);
		};
	};

	private static final IndexCommitFunction<IndexCommit> GET_INDEX_COMMIT_FUNCTION = new IndexCommitFunction<IndexCommit>() {
		@Override public IndexCommit apply(final IndexCommit commit) {
			return commit;
		}
		
		@Override public IndexCommit getDefault() {
			return null; //intentionally null
		}
	};

	private volatile boolean closed;

	private Directory directory;
	@Nullable private IndexWriter indexWriter;
	private ReferenceManager<IndexSearcher> manager;
	private FilteringMergePolicy filteringMergePolicy;
	private final IndexPostProcessingConfiguration configuration;
	private final IDirectoryManager directoryManager;
	private final boolean readOnly;
	private boolean firstStartupAtMain = false;
	
	public IndexBranchService(final IBranchPath branchPath, final IDirectoryManager directoryManager, @Nullable final IndexBranchService baseService) throws IOException {

		this.directoryManager = checkNotNull(directoryManager, "directoryManager");
		long timestamp = IIndexPostProcessingConfiguration.DEFAULT_TIMESTAMP;
		final AtomicBoolean requiresPostProcessing = new AtomicBoolean(false);
		
		directory = directoryManager.createDirectory(branchPath, baseService);
		readOnly = isBasePath(branchPath);
		
		//not main and there are no indexes, we have to 'do' nothing 
		if (!isMain(branchPath) && !DirectoryReader.indexExists(directory)) {

			Preconditions.checkNotNull(baseService, "Base branch index service argument cannot be null.");
			IndexCommit commit = baseService.getIndexCommit(branchPath);
			final int baseSegmentInfoCounter = getSegmentInfoCounter(baseService.getDirectory());
		
			if (null == commit) {
				if (null != baseService) {
					commit = baseService.getIndexCommit(branchPath.getParent());
				}
			}
			
			if (null == commit) {
			
				indexWriter = null;
				manager = NullSearcherManager.getInstance();
			
			} else {
				
				if (!readOnly) {
					timestamp = Long.parseLong(commit.getUserData().get(IndexUtils.INDEX_BASE_TIMESTAMP_KEY));
				}
				requiresPostProcessing.compareAndSet(false, !readOnly);
				
				indexWriter = readOnly ? null : createIndexWriter(isMain(branchPath));
				filteringMergePolicy.setMinSegmentCount(baseSegmentInfoCounter);
				manager = createSearcherManager();
				
			}
			
		} else {
			
			indexWriter = createIndexWriter(isMain(branchPath));
			manager = createSearcherManager();
			
		}
		
		//keep segment file even it contains only deletions
		forceKeepFullyDeletedSegments(indexWriter);
		
		configuration = new IndexPostProcessingConfiguration();
		configuration.setBranchPath(branchPath);
		configuration.setTimestamp(timestamp);
		configuration.setRequiresPostProcessing(requiresPostProcessing);
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
		
		// Close components in reverse order.
		if (null != manager) {
			try {
				manager.close();
			} catch (final IOException e) {
				try {
					manager.close();
				} catch (final IOException e1) {
					//intentionally ignored
				}
				throw new IndexException("Error while closing index searcher manager. [" + configuration.getBranchPath() + "]", e);
			}
		}
		
		try {
			indexWriter.rollback();
		} catch (final IOException ignored) {
			// This should be a closeQuietly(...) call, but we want to roll back changes instead of committing them
		}

		if (null != directory) {
			try {
				directory.close();
			} catch (final IOException e) {
				try {
					directory.close();
				} catch (final IOException e1) {
					//intentionally ignored
				}
				throw new IndexException("Error while closing index directory. [" + configuration.getBranchPath().getPath() + "]", e);
			}
		}

		
		manager = null;
		indexWriter = null;
		directory = null;
	}
	
	public IDirectoryManager getDirectoryManager() {
		return directoryManager;
	}
	
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}
	
	public void addDocument(final Document document) throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.addDocument(document);
		}
	}

	public void deleteDocuments(final Query query) throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.deleteDocuments(query);
		}
	}
	
	public void deleteDocuments(final Term term) throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.deleteDocuments(term);
		}
	}

	public void updateDocument(final Term term, final Document document) throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.updateDocument(term, document);
		}
	}

	public void commit() throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.setCommitData(Collections.<String, String>emptyMap()); //override snapshot commit data
			indexWriter.commit();
			manager.maybeRefreshBlocking();
		}
	}

	public void optimize() throws IOException {
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			indexWriter.forceMerge(1);
			commit();
		}
	}

	public void rollback() throws IOException {
		checkClosed();
		checkReadOnly();
		
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
		checkClosed();
		checkReadOnly();
		if (null != indexWriter) {
			deleteDocuments(new MatchAllDocsQuery());
		}
	}

	public IndexCommit snapshot() throws IOException {
		checkClosed();
		checkReadOnly();
		
		final SnapshotDeletionPolicy snapshotDeletionPolicy = getSnapshotDeletionPolicy(indexWriter);
		final IndexCommit commit = snapshotDeletionPolicy.snapshot();
		return commit;
	}

	public void releaseSnapshot(final IndexCommit commit) throws IOException {
		checkClosed();
		checkReadOnly();
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
		
		final Iterator<IndexCommit> itr = new BackwardListIterator<IndexCommit>(DirectoryReader.listCommits(directory));
		
		while (itr.hasNext()) {
			
			final IndexCommit $ = itr.next();
			final Map<String, String> userData = $.getUserData();
			
			if (null != userData) {
				
				final String value = userData.get(IndexUtils.INDEX_BRANCH_PATH_KEY);
				if (null != value) {
					return true; 
				}
				
			}
		}
		
		return false;
		
	}
	
	/**Returns with the last {@link IndexCommit index commit} from the underlying {@link Directory directory}. Never {@code null}.*/
	public IndexCommit getHeadIndexCommit() {
		try {
			return getLast(DirectoryReader.listCommits(directory));
		} catch (final IOException e) {
			throw new IndexException("Error while getting HEAD commit from " + directory, e);
		}
	}
	
	@Nullable public IndexCommit getIndexCommit(final IBranchPath branchPath) throws IOException {
		return getIndexCommit(directory, checkNotNull(branchPath, "branchPath"));
	}
	
	void createIndexCommit(final IBranchPath branchPath, final int[] cdoBranchPath, final long baseTimestamp) throws IOException {
		checkClosed();
		checkReadOnly();
		checkState(cdoBranchPath[0] == 0, "CDO ID sequence did not start with 0.");
		
		final Map<String, String> userData = Maps.newHashMap();
		userData.put(IndexUtils.INDEX_BRANCH_PATH_KEY, branchPath.getPath());
		userData.put(IndexUtils.INDEX_BASE_TIMESTAMP_KEY, String.valueOf(baseTimestamp));
		userData.put(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, Ints.join("/", cdoBranchPath));
		
		indexWriter.setCommitData(userData);
		indexWriter.commit(); //intentionally not via #commit (reader should not be reopen, commit data should be specified)
		updateMergePolicy();
	}

	public Directory getDirectory() {
		return directory;
	}

	public IIndexPostProcessingConfiguration getPostProcessingConfiguration() {
		return configuration;
	}

	@Nullable public static IndexCommit getIndexCommit(final Directory directory, final IBranchPath branchPath) {
		try {
			return getValueFromIndexCommit(directory, IndexUtils.INDEX_BRANCH_PATH_KEY, new Predicate<String>() {
				@Override public boolean apply(final String pathString) {
					return checkNotNull(branchPath, "branchPath").getPath().equals(pathString);
				}
			}, GET_INDEX_COMMIT_FUNCTION);
		} catch (final IOException e) {
			throw new IndexException("Cannot get index commit for branch path: '" + branchPath + "'.", e);
		}
	}

	private void updateMergePolicy() throws IOException {
		filteringMergePolicy.setMinSegmentCount(getSegmentInfoCounter(directory));
	}

	private static int getSegmentInfoCounter(final Directory _directory) throws IOException {
		return getValueFromIndexCommit(_directory, IndexUtils.INDEX_BRANCH_PATH_KEY, Predicates.<String>notNull(), GET_SEGMENT_COUNTER_FUNCTION);
	}
	
	private static <T> T getValueFromIndexCommit(final Directory directory, final String userDataKey, final Predicate<String> expectedValuePredicate, final IndexCommitFunction<T> getValueFunction) throws IOException {

		checkNotNull(userDataKey, "userDataKey");
		checkNotNull(expectedValuePredicate, "expectedValuePredicate");
		checkNotNull(getValueFunction, "getValueFunction");
		
		final Iterator<IndexCommit> itr = new BackwardListIterator<IndexCommit>(DirectoryReader.listCommits(directory));
		while (itr.hasNext()) {
			final IndexCommit $ = itr.next();
			final Map<String, String> userData = $.getUserData();
			if (null != userData) {
				final String value = userData.get(userDataKey);
				if (expectedValuePredicate.apply(value)) {
					return getValueFunction.apply($);
				}
			}
		}
		
		return getValueFunction.getDefault();
	}

	private IndexWriter createIndexWriter(final boolean commitIfEmpty) throws IOException {
		
		final Analyzer analyzer = new DelimiterStopAnalyzer();
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		final IndexDeletionPolicy wrappedDeletionPolicy = new KeepBranchPointAndLastCommitDeletionPolicy();
		final SnapshotDeletionPolicy deletionPolicy = new SnapshotDeletionPolicy(wrappedDeletionPolicy);
		config.setIndexDeletionPolicy(deletionPolicy);

		filteringMergePolicy = new FilteringMergePolicy(new LogByteSizeMergePolicy());
		config.setMergePolicy(filteringMergePolicy);
		config.setMergeScheduler(config.getMergeScheduler());
		indexWriter = new IndexWriter(directory, config);
		
		// create empty index, if it doesn't exist yet
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

	private void checkClosed() {
		if (closed) {
			throw new AlreadyClosedException("This branch-specific index service is already closed.");
		}
	}
	
	private void checkReadOnly() {
		if (readOnly) {
			throw new UnsupportedOperationException("Index branch service is read only.");
		}
	}

	private void forceKeepFullyDeletedSegments(final IndexWriter writer) {
		if (!readOnly) {
			ReflectionUtils.setField(IndexWriter.class, writer, "keepFullyDeletedSegments", true);
		}
	}
	
	private SnapshotDeletionPolicy getSnapshotDeletionPolicy(final IndexWriter indexWriter) {
		
		final IndexDeletionPolicy policy = indexWriter.getConfig().getIndexDeletionPolicy();
	
		if (!(policy instanceof SnapshotDeletionPolicy)) {
			throw new IllegalStateException("IndexWriter has no SnapshotDeletionPolicy configured.");
		}
	
		return (SnapshotDeletionPolicy) policy;
	}

	
}