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
import java.util.Collections;
import java.util.List;

import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FilterDirectory;
import org.apache.lucene.store.RAMDirectory;

import com.b2international.snowowl.core.api.BranchPath;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Directory manager for a {@link RAMDirectory}.
 */
public class RAMDirectoryManager extends AbstractDirectoryManager implements IDirectoryManager {

	private final LoadingCache<File, Directory> directoryCache = CacheBuilder.newBuilder().build(new CacheLoader<File, Directory>() {
		@Override
		public Directory load(final File key) throws Exception {
			return new RAMDirectory();
		}
	});

	public RAMDirectoryManager(final String repositoryUuid, final File indexRelativeRootPath) {
		super(repositoryUuid, indexRelativeRootPath);
	}

	@Override
	protected Directory openWritableLuceneDirectory(final File folderForBranchPath) throws IOException {
		final Directory delegate = directoryCache.getUnchecked(folderForBranchPath);
		
		/* 
		 * Regular file-based directories can be opened many times in different instances, but we have to introduce a layer of indirection to avoid
		 * sharing the single RAMDirectory instance for a "physical path".
		 */
		return new FilterDirectory(delegate) {
			private volatile boolean open = true;

			@Override
			protected void ensureOpen() throws AlreadyClosedException {
				if (!open) {
					throw new AlreadyClosedException("This wrapped RAMDirectory is already closed.");
				}
			}

			@Override
			public void close() throws IOException {
				open = false;
			}
		};
	}

	@Override
	public void firstStartup(final IndexBranchService service) {
		return;
	}

	@Override
	public List<String> listFiles(final BranchPath branchPath) {
		return Collections.emptyList();
	}
}
