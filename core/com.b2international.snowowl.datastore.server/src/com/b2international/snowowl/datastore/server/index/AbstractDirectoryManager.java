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

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.internal.lucene.store.CompositeDirectory;
import com.b2international.snowowl.datastore.server.internal.lucene.store.ReadOnlyDirectory;
import com.google.common.collect.Lists;

public abstract class AbstractDirectoryManager implements IDirectoryManager {

	private static final String INDEXES_CHILD_FOLDER = "indexes";

	protected final String repositoryUuid;
	protected final File indexRelativeRootPath;

	protected AbstractDirectoryManager(final String repositoryUuid, final File indexRelativeRootPath) {
		this.repositoryUuid = checkNotNull(repositoryUuid, "repositoryUuid");
		this.indexRelativeRootPath = checkNotNull(indexRelativeRootPath, "indexRelativeRootPath");
	}

	@Override
	public Directory openDirectory(final BranchPath branchPath, final boolean readOnly) throws IOException {
		final File folderForBranchPath = getFolderForBranchPath(branchPath);

		if (branchPath.isMain()) {
			final Directory mainDirectory = openReadWriteDirectory(folderForBranchPath);
			return readOnly ? new ReadOnlyDirectory(mainDirectory) : mainDirectory;
		}

		// Don't bother wrapping the parents in a read-only instance
		final Directory parentDirectory = openDirectory(branchPath.parent(), false);
		final IndexCommit parentCommit = getParentCommit(parentDirectory, branchPath);
		final Directory parentCommitDirectory = new ReadOnlyDirectory(parentCommit);

		if (readOnly) {
			return parentCommitDirectory;
		} else {
			final Directory writeableDirectory = openReadWriteDirectory(folderForBranchPath);
			return new CompositeDirectory(parentCommitDirectory, writeableDirectory);
		}
	}

	private IndexCommit getParentCommit(final Directory parentDirectory, final BranchPath branchPath) throws IOException {
		final List<IndexCommit> indexCommits = Lists.<IndexCommit>reverse(DirectoryReader.listCommits(parentDirectory));
		final String path = branchPath.path();

		for (final IndexCommit indexCommit : indexCommits) {
			if (indexCommit.getUserData().get(IndexUtils.INDEX_CDO_BRANCH_PATH_KEY).equals(path)) {
				return indexCommit;
			}
		}

		return null;
	}

	private File getDataDirectory() {
		return SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory();
	}

	protected File getIndexRootPath() {
		return new File(getDataDirectory(), INDEXES_CHILD_FOLDER);
	}

	private File getIndexAbsolutePath() {
		return new File(getIndexRootPath(), indexRelativeRootPath.getPath());
	}

	protected File getFolderForBranchPath(final BranchPath branchPath) throws IOException {
		return new File(getIndexAbsolutePath(), branchPath.path());
	}

	protected abstract Directory openReadWriteDirectory(final File folderForBranchPath) throws IOException;
}
