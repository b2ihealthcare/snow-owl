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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Directory manager for a a file system based {@link FSDirectory}.
 */
public class FSDirectoryManager extends AbstractDirectoryManager implements IDirectoryManager {

	public FSDirectoryManager(final String repositoryUuid, final File indexPath) {
		super(repositoryUuid, indexPath);
	}

	@Override
	protected Directory openWritableLuceneDirectory(final File folderForBranchPath) throws IOException {
		return IndexUtils.open(folderForBranchPath);
	}

	@Override
	public List<String> listFiles(final BranchPath branchPath) throws IOException {

		final Set<String> result = Sets.newHashSet();
		final File folderForBranchPath = getIndexSubDirectory(branchPath.path());

		final IPath base = new Path(getIndexSubDirectory("..").getAbsolutePath());
		final IPath actual = new Path(folderForBranchPath.getAbsolutePath());
		final IPath relativePath = actual.makeRelativeTo(base);

		try (final IndexDirectory directory = openDirectory(branchPath, false)) {

			final List<IndexCommit> commits = directory.listCommits();
			for (final IndexCommit commit : commits) {

				final Collection<String> fileNames = commit.getFileNames();
				for (final String fileName : fileNames) {
					final File indexFilePath = new File(folderForBranchPath, fileName);

					// Only collect files from this folder
					if (indexFilePath.exists() && indexFilePath.isFile()) {
						result.add(relativePath.append(fileName).toString());
					}
				}
			}

		} catch (final IndexNotFoundException ignored) {
			// An empty result can be returned if no commits can be collected from the directory
		}

		return Ordering.natural().sortedCopy(result);
	}

}
