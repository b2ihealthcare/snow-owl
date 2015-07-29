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
package com.b2international.snowowl.datastore.store;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
import com.b2international.snowowl.datastore.SingleDirectoryIndex;
import com.b2international.snowowl.datastore.index.DelimiterStopAnalyzer;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * An abstract implementation for an index service which does not use multiple directories. 
 *
 */
public abstract class SingleDirectoryIndexImpl implements SingleDirectoryIndex, IDisposableService {

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
	
	protected Directory directory;
	protected ReferenceManager<IndexSearcher> manager;
	protected IndexWriter writer;
	
	protected volatile AtomicBoolean disposed = new AtomicBoolean(false);
	
	private final File indexDirectory;
	
	/**
	 * Full path to the directory to use.
	 * @param directoryPath - the absolute directory path 
	 */
	protected SingleDirectoryIndexImpl(final File directory) {
		this(directory, false);
	}
	
	protected SingleDirectoryIndexImpl(final File directory, final boolean clean) {
		checkNotNull(directory, "indexDirectory");
		checkArgument(directory.exists() || directory.mkdirs(), "Couldn't create directories for path '%s'", directory);
		this.indexDirectory = directory;
		initLucene(indexDirectory, clean);
	}

	private void initLucene(final File indexDirectory, final boolean clean) {
		try {
			this.directory = IndexUtils.open(indexDirectory);
			final Analyzer analyzer = new DelimiterStopAnalyzer();
			final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
			config.setOpenMode(clean ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
			config.setIndexDeletionPolicy(new SnapshotDeletionPolicy(config.getIndexDeletionPolicy()));
			this.writer = new IndexWriter(directory, config);
			this.writer.commit(); // Create index if it didn't exist
			this.manager = new SearcherManager(directory, null);
		} catch (final IOException e) {
			throw new StoreException(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the directory where this {@link SingleDirectoryIndexImpl} operates.
	 * @return
	 */
	public File getDirectory() {
		return indexDirectory;
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
	public String getIndexPath() {
		return Paths.get(SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory().getAbsolutePath(), "indexes").relativize(indexDirectory.toPath()).toString();
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
		final IPath actual = new Path(indexDirectory.getAbsolutePath());
		final IPath relativePath = actual.makeRelativeTo(base);

		final Collection<String> fileNames = indexCommit.getFileNames();
		
		for (final String fileName : fileNames) {
			final File indexFilePath = new File(indexDirectory, fileName);
			
			// Only collect files from this folder
			if (indexFilePath.exists() && indexFilePath.isFile()) {
				result.add(relativePath.append(fileName).toString());
			}
		}
		
		return Ordering.natural().sortedCopy(result);
	}

	private File getIndexBasePath() {
		File relativePath = indexDirectory;
		
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
		return ClassUtils.checkAndCast(writer.getConfig().getIndexDeletionPolicy(), SnapshotDeletionPolicy.class);
	}

	private void checkNotDisposed() {
		if (isDisposed()) {
			throw new IllegalStateException("This service is disposed.");
		}
	}
}