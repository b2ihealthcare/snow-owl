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
package com.b2international.snowowl.datastore.server.internal.lucene.store;

import static org.apache.lucene.store.NoLockFactory.getNoLockFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

/**
 * Represents a read only directory backed by a read only {@link IndexCommit}.
 *
 */
public class ReadOnlyDirectory extends Directory {

	private final IndexCommit commit;

	public ReadOnlyDirectory(final IndexCommit commit) {
		if (null == commit) {
			throw new NullPointerException("commit");
		}
		this.commit = commit;
	}
	
	@Override
	public String[] listAll() throws IOException {
		return Arrays.copyOf(commit.getFileNames().toArray(), commit.getFileNames().size(), String[].class);
	}

	@Override
	public boolean fileExists(final String name) throws IOException {
		return commit.getFileNames().contains(name);
	}

	@Override
	public void deleteFile(final String name) throws IOException {
		throw new UnsupportedOperationException("Deletion is not allowed in " + ReadOnlyDirectory.class);
	}

	@Override
	public long fileLength(final String name) throws IOException {
		return commit.getDirectory().fileLength(name);
	}

	@Override
	public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
		throw new UnsupportedOperationException("Index output creation is not allowed in " + ReadOnlyDirectory.class);
	}

	@Override
	public void sync(final Collection<String> names) throws IOException {
		//ignored. nothing to synchronize. index commit is immutable.
	}

	@Override
	public IndexInput openInput(final String name, final IOContext context) throws IOException {
		if (!fileExists(name)) {
			throw new FileNotFoundException();
		}
		return commit.getDirectory().openInput(name, context);
	}

	@Override
	public Lock makeLock(final String name) {
		return getNoLockFactory().makeLock(name);
	}

	@Override
	public void clearLock(final String name) throws IOException {
		getNoLockFactory().clearLock(name);
	}

	@Override
	public void close() throws IOException {
		//ignored. immutable index commit cannot be closed
	}

	@Override
	public void setLockFactory(final LockFactory lockFactory) throws IOException {
		//ignored
	}

	@Override
	public LockFactory getLockFactory() {
		return getNoLockFactory();
	}

}