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
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;

import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.internal.lucene.store.CompositeDirectory;
import com.b2international.snowowl.datastore.server.internal.lucene.store.ReadOnlyDirectory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AbstractDirectoryManager implements IDirectoryManager {

	protected final String repositoryUuid;
	protected final File indexPath;
	private LoadingCache<File, Directory> fileToDirectoryCache;
	private LoadingCache<Directory, List<IndexCommit>> dirToCommitsCache;

	/**
	 * @param repositoryUuid the repository identifier
	 * @param indexPath the <b>absolute</b> path to the terminology index directories
	 */
	protected AbstractDirectoryManager(final String repositoryUuid, final File indexPath) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.indexPath = checkNotNull(indexPath, "indexPath");
		
		fileToDirectoryCache = CacheBuilder.newBuilder().build(new CacheLoader<File, Directory>() {
			@Override
			public Directory load(File key) throws Exception {
				return openWritableLuceneDirectory(key);
			}
		});
		
		dirToCommitsCache = CacheBuilder.newBuilder().build(new CacheLoader<Directory, List<IndexCommit>>() {
			@Override
			public List<IndexCommit> load(Directory key) throws Exception {
				return DirectoryReader.listCommits(key);
			}
		});
		
	}

	protected File getIndexSubDirectory(final String subDirectoryPath) throws IOException {
		return new File(indexPath, subDirectoryPath);
	}

	@Override
	public final IndexDirectory openDirectory(final BranchPath branchPath, final boolean readOnly) throws IOException {
		return new IndexDirectory(openLuceneDirectory(branchPath, readOnly));
	}

	private final Directory openLuceneDirectory(final BranchPath branchPath, final boolean readOnly) throws IOException {
		final File indexSubDirectory = getIndexSubDirectory(branchPath.path());

		if (branchPath.isMain()) {
			final Directory mainDirectory = fileToDirectoryCache.getUnchecked(indexSubDirectory);
			return readOnly ? new ReadOnlyDirectory(mainDirectory) : mainDirectory;
		}

		// Don't bother wrapping the parents in a read-only instance
		final Directory parentDirectory = openLuceneDirectory(branchPath.parent(), false);
		final Set<String> visibleFiles = Sets.newHashSet();
		
		// Make files in index commits for ancestors visible as well
		for (BranchPath ancestorPath = branchPath; !ancestorPath.isMain(); ancestorPath = ancestorPath.parent()) {
			final IndexCommit ancestorCommit = getParentCommit(parentDirectory, ancestorPath);
			/* 
			 * XXX: .del files are not incremental. If they were, we could start by adding files 
			 * referenced in the immediate parent commit, and check that ancestors are only contributing 
			 * segment_n files to the existing set. 
			 */
			visibleFiles.addAll(ancestorCommit.getFileNames());
		}
		
		final Directory parentCommitDirectory = new ReadOnlyDirectory(parentDirectory, visibleFiles);

		if (readOnly) {
			return parentCommitDirectory;
		} else {
			final Directory writeableDirectory = fileToDirectoryCache.getUnchecked(indexSubDirectory);
			return new CompositeDirectory(parentCommitDirectory, writeableDirectory);
		}
	}

	protected abstract Directory openWritableLuceneDirectory(final File folderForBranchPath) throws IOException;

	private IndexCommit getParentCommit(final Directory parentDirectory, final BranchPath branchPath) throws IOException {
		
		final List<IndexCommit> indexCommits = Lists.<IndexCommit>reverse(dirToCommitsCache.getUnchecked(parentDirectory));
		final String path = branchPath.path();

		for (final IndexCommit indexCommit : indexCommits) {
			if (path.equals(indexCommit.getUserData().get(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY))) {
				return indexCommit;
			}
		}

		return null;
	}
}
