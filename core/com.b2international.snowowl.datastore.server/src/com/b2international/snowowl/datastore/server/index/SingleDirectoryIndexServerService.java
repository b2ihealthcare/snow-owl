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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.datastore.ISingleDirectoryIndexService;
import com.b2international.snowowl.datastore.index.DelimiterStopAnalyzer;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.personalization.ComponentSetManagerException;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * An abstract implementation for an index service which does not use multiple directories. 
 *
 */
public abstract class SingleDirectoryIndexServerService implements ISingleDirectoryIndexService, IDisposableService {

	private final class SnapshotOrdering extends Ordering<String> {
		@Override
		public int compare(final String left, final String right) {
			final IndexCommit leftCommit = heldSnapshots.get(left);
			final IndexCommit rightCommit = heldSnapshots.get(right);
			return leftCommit.compareTo(rightCommit);
		}
	}

	private final Ordering<String> snapshotReverseOrdering = new SnapshotOrdering().reverse();

	private final Map<String, IndexCommit> heldSnapshots = new MapMaker().makeMap();
	private final File indexRootPath;
	private final File indexRelativePath;
	
	protected Directory directory;
	protected ReferenceManager<IndexSearcher> manager;
	protected IndexWriter writer;
	
	protected volatile AtomicBoolean disposed = new AtomicBoolean(false);
	
	protected SingleDirectoryIndexServerService(final File indexRootPath) {
		this(indexRootPath, OpenMode.CREATE_OR_APPEND);
	}
	
	protected SingleDirectoryIndexServerService(final File indexRootPath, final OpenMode openMode) {
		checkNotNull(openMode, "Index open mode may not be null.");
		this.indexRootPath = checkNotNull(indexRootPath, "indexRootPath");
		
		final File indexPath = new File(getDataDirectory(), "indexes");
		indexRelativePath = new File(indexPath, indexRootPath.getPath());
		
		if (!indexRelativePath.isDirectory() && !indexRelativePath.mkdirs()) {
			throw new ComponentSetManagerException(MessageFormat.format("Couldn''t create directories for index path ''{0}''.", indexRelativePath));
		}
		
		try {
			this.directory = IndexUtils.open(indexRelativePath);
			final Analyzer analyzer = new DelimiterStopAnalyzer();
			final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			config.setOpenMode(openMode);
			config.setIndexDeletionPolicy(new SnapshotDeletionPolicy(config.getIndexDeletionPolicy()));
			this.writer = new IndexWriter(directory, config);
			this.writer.commit(); // Create index if it didn't exist
			this.manager = new SearcherManager(directory, null);
		} catch (final IOException e) {
			throw new ComponentSetManagerException(e);
		}
	}

	private File getDataDirectory() {
		return SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory();
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}

	@Override
	public final void dispose() {
		
		if (disposed.compareAndSet(false, true)) {
			doDispose();
		}
	}

	@OverridingMethodsMustInvokeSuper
	protected void doDispose() {
		
		for (final IndexCommit heldCommit : heldSnapshots.values()) {
			try {
				releaseSnapshotCommit(heldCommit);
			} catch (final IOException ignored) {
				// XXX: dispose should succeed regardless of errors
			}
		}
		
		Closeables.closeQuietly(manager);
		Closeables.closeQuietly(writer);
		Closeables.closeQuietly(directory);
		
		manager = null;
		writer = null;
		directory = null;
	}

	@Override
	public File getIndexRootPath() {
		return indexRootPath;
	}
	
	@Override
	public String snapshot() throws IOException {
		checkNotDisposed();
		
		final IndexCommit snapshot = getSnapshotDeletionPolicy().snapshot();
		final String snapshotId = UUID.randomUUID().toString();
		heldSnapshots.put(snapshotId, snapshot);
		
		return snapshotId;
	}

	@Override
	public List<String> getSnapshotIds() {
		return snapshotReverseOrdering.sortedCopy(heldSnapshots.keySet());
	}
	
	@Override
	public List<String> listFiles(final String snapshotId) throws IOException {
		checkNotDisposed();
		checkNotNull(snapshotId, "Snapshot identifier may not be null.");
		
		final Set<String> result = Sets.newHashSet();
		final IndexCommit indexCommit = heldSnapshots.get(snapshotId);
		
		if (null == indexCommit) {
			return Lists.newArrayList();
		}
		
		final File basePath = getIndexBasePath();
		
		if (null == basePath)  {
			return Lists.newArrayList();
		}
		
		final IPath base = new Path(basePath.getAbsolutePath());
		final IPath actual = new Path(indexRelativePath.getAbsolutePath());
		final IPath relativePath = actual.makeRelativeTo(base);

		final Collection<String> fileNames = indexCommit.getFileNames();
		
		for (final String fileName : fileNames) {
			final File indexFilePath = new File(indexRelativePath, fileName);
			
			// Only collect files from this folder
			if (indexFilePath.exists() && indexFilePath.isFile()) {
				result.add(relativePath.append(fileName).toString());
			}
		}
		
		return Ordering.natural().sortedCopy(result);
	}

	private File getIndexBasePath() {
		File relativePath = indexRelativePath;
		
		while (null != relativePath && !relativePath.getName().equals("indexes")) {
			relativePath = relativePath.getParentFile();
		}
		
		return relativePath;
	}
	
	@Override
	public void releaseSnapshot(final String snapshotId) throws IOException {
		checkNotDisposed();
		checkNotNull(snapshotId, "Snapshot identifier may not be null.");

		final IndexCommit indexCommit = heldSnapshots.remove(snapshotId);

		if (null != indexCommit) {
			releaseSnapshotCommit(indexCommit);
		}
	}

	private void releaseSnapshotCommit(final IndexCommit indexCommit) throws IOException {
		getSnapshotDeletionPolicy().release(indexCommit);
	}
	
	protected final void commit() throws IOException {
		writer.commit();
		manager.maybeRefreshBlocking();
	}

	private SnapshotDeletionPolicy getSnapshotDeletionPolicy() {
		final SnapshotDeletionPolicy indexDeletionPolicy = ClassUtils.checkAndCast(writer.getConfig().getIndexDeletionPolicy(), SnapshotDeletionPolicy.class);
		return indexDeletionPolicy;
	}

	private void checkNotDisposed() {
		if (isDisposed()) {
			throw new IllegalStateException("This service is disposed.");
		}
	}
}