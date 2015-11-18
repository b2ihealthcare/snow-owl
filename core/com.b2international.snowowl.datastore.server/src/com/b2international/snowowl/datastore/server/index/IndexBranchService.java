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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ReflectionUtils;
import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.NullSearcherManager;
import com.b2international.snowowl.datastore.index.lucene.ComponentTermAnalyzer;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.internal.lucene.index.FilteringMergePolicy;
import com.b2international.snowowl.datastore.server.internal.lucene.store.ReadOnlyDirectory;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

public class IndexBranchService implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(IndexBranchService.class);

	private final AtomicBoolean closed = new AtomicBoolean();
	private final AtomicBoolean dirty = new AtomicBoolean(); 

	private IndexDirectory directory;
	private IndexWriter indexWriter;
	private ReferenceManager<IndexSearcher> manager;
	private boolean firstStartupAtMain;

	private final IDirectoryManager directoryManager;
	private final BranchPath branchPath;
	private final FilteringMergePolicy mergePolicy;
	private final boolean readOnly;

	private final Stopwatch stopwatch;

	public IndexBranchService(final IBranchPath logicalBranchPath, final BranchPath physicalBranchPath, final IDirectoryManager directoryManager) throws IOException {

		this.stopwatch = Stopwatch.createStarted();
		this.directoryManager = checkNotNull(directoryManager, "directoryManager");
		this.branchPath = physicalBranchPath;
		this.mergePolicy = new FilteringMergePolicy(new LogByteSizeMergePolicy());
		this.readOnly = isBasePath(logicalBranchPath);
		this.directory = directoryManager.openDirectory(physicalBranchPath, readOnly);

		if (!isMain(logicalBranchPath) && !directory.indexExists()) {

			if (!readOnly) {

				final IndexCommit baseCommit = directory.getLastBaseIndexCommit(physicalBranchPath);

				if (baseCommit == null) {
					this.indexWriter = null;
					this.manager = NullSearcherManager.getInstance();

					LOG.warn("Requested a writable index for logical branch path {}, but no base IndexCommit is present "
							+ "for physical branch path {}. Creating no-op instance.",
							logicalBranchPath, 
							physicalBranchPath);

				} else {
					this.indexWriter = createIndexWriter(false);
					this.manager = directory.createSearcherManager();
				}

			} else {
				this.indexWriter = null;
				final IndexCommit baseCommit = directory.getLastBaseIndexCommit(logicalBranchPath);
				this.manager = new SearcherManager(new ReadOnlyDirectory(baseCommit), null);
			}

		} else {
			
			if (!readOnly) {
				this.indexWriter = createIndexWriter(isMain(logicalBranchPath));
				this.manager = directory.createSearcherManager();
			} else {
				this.indexWriter = null;
				final IndexCommit baseCommit = directory.getLastBaseIndexCommit(logicalBranchPath);
				this.manager = new SearcherManager(new ReadOnlyDirectory(baseCommit), null);
			}
		}
	}

	public ReferenceManager<IndexSearcher> getManager() {
		return manager;
	}
	
	@Override
	public void close() {

		if (!closed.compareAndSet(false, true)) {
			return;
		}

		LOG.debug("Service for physical path {} closed after {} second(s).", branchPath.path(), stopwatch.stop().elapsed(TimeUnit.SECONDS));
		
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
			setDirty();
			indexWriter.addDocument(document);
		}
	}

	public void deleteDocuments(final Query query) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			setDirty();
			indexWriter.deleteDocuments(query);
		}
	}

	public void deleteDocuments(final Term term) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			setDirty();
			indexWriter.deleteDocuments(term);
		}
	}

	public void updateDocument(final long storageKey, final Document document) throws IOException {
		updateDocument(Mappings.storageKey().toTerm(storageKey), document);
	}
	
	public void updateDocument(final Term term, final Document document) throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			setDirty();
			indexWriter.updateDocument(term, document);
		}
	}

	public <D extends DocumentBuilderBase<D>> void upsert(Query query, DocumentUpdater<D> documentUpdater, DocumentBuilderFactory<D> builderFactory) throws IOException {
		ensureOpen();
		ensureWritable();
		if (indexWriter != null) {
			IndexSearcher searcher = null;
			try {
				searcher = manager.acquire();
				final TopDocs docs = searcher.search(query, 2);
				checkState(docs.totalHits <= 1, "Multiple documents with same query ('%s') on a single branch path", query);
				final D builder;
				if (docs.totalHits == 0) {
					// create new
					builder = builderFactory.createBuilder();
				} else {
					final Document doc = searcher.doc(docs.scoreDocs[0].doc);
					builder = builderFactory.createBuilder(doc);
				}
				documentUpdater.update(builder);
				final Document updatedDoc = builder.build();
				checkState(updatedDoc.getFields().size() > 0, "At least one field must be specified");
				final IndexField<Long> storageKeyField = Mappings.storageKey();
				final Long storageKey = storageKeyField.getValue(updatedDoc);
				final Term key = storageKeyField.toTerm(storageKey);
				updateDocument(key, updatedDoc);
			} finally {
				if (searcher != null) {
					try {
						manager.release(searcher);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	public void commit() throws IOException {
		ensureOpen();
		ensureWritable();
		if (null != indexWriter) {
			indexWriter.setCommitData(Collections.<String, String>emptyMap()); //override snapshot commit data
			indexWriter.commit();
			manager.maybeRefreshBlocking();
			clearDirty();
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
			clearDirty();
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

	void createIndexCommit(final IBranchPath logicalBranchPath, final BranchPath physicalBranchPath) throws IOException {
		ensureOpen();
		ensureWritable();
		ensureNotDirty();
		
		final Map<String, String> userData = ImmutableMap.<String, String>builder()
				.put(IndexUtils.INDEX_BRANCH_PATH_KEY, logicalBranchPath.getPath())
				.put(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY, physicalBranchPath.path())
				.build();

		indexWriter.setCommitData(userData);
		indexWriter.commit(); //intentionally not via #commit (reader should not be reopen, commit data should be specified)
		updateMergePolicy();
	}

	public List<String> listFiles() throws IOException {
		return directoryManager.listFiles(branchPath);
	}

	private void updateMergePolicy() throws IOException {
		mergePolicy.setMinSegmentCount(directory.getSegmentInfoCounter());
	}

	private IndexWriter createIndexWriter(final boolean commitIfEmpty) throws IOException {
		final Analyzer analyzer = new ComponentTermAnalyzer(true, true);
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		final IndexDeletionPolicy innerPolicy = new KeepBranchPointAndLastCommitDeletionPolicy();
		final SnapshotDeletionPolicy outerPolicy = new SnapshotDeletionPolicy(innerPolicy);
		config.setIndexDeletionPolicy(outerPolicy);
		config.setMergePolicy(mergePolicy);
		indexWriter = directory.createIndexWriter(config);

		// Merges may trim fully deleted segments, which interferes with the overlay directories
		ReflectionUtils.setField(IndexWriter.class, indexWriter, "keepFullyDeletedSegments", true);

		// Create empty index, if it doesn't exist yet
		if (commitIfEmpty && !directory.indexExists()) {
			firstStartupAtMain = true;
			indexWriter.commit();
		}

		updateMergePolicy();
		return indexWriter;
	}

	public boolean isFirstStartupAtMain() {
		return firstStartupAtMain;
	}

	private void ensureOpen() {
		if (closed.get()) {
			throw new AlreadyClosedException("Index branch service is already closed.");
		}
	}

	private void ensureWritable() {
		if (readOnly) {
			throw new UnsupportedOperationException("Index branch service is read-only.");
		}
	}
	
	private void ensureNotDirty() {
		if (dirty.get()) {
			throw new IllegalStateException("Index branch service for physical path " + branchPath + " contains uncommitted changes.");
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

	public IndexCommit getIndexCommit(final IBranchPath logicalBranchPath) {
		return directory.getLastBaseIndexCommit(logicalBranchPath);
	}

	public IndexCommit getLastIndexCommit() {
		return directory.getLastIndexCommit();
	}

	private void setDirty() {
		dirty.compareAndSet(false, true);
	}

	private void clearDirty() {
		dirty.compareAndSet(true, false);
	}

	public boolean isDirty() {
		ensureOpen();
		return dirty.get();
	}
}
