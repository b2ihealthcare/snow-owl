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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Represents a read-only directory backed by an {@link IndexCommit}, or another {@link Directory}.
 * <p>
 * In case of a directory, the visible list of files is captured at construction time, and is not updated later on.
 */
public class ReadOnlyDirectory extends Directory {

	private final Set<String> fileNames;
	private final Directory directory;

	public ReadOnlyDirectory(final IndexCommit commit) throws IOException {
		this(checkNotNull(commit, "Index commit may not be null.").getDirectory(), commit.getFileNames());
	}

	public ReadOnlyDirectory(final Directory directory) throws IOException {
		this(directory, ImmutableSet.copyOf(directory.listAll()));
	}

	public ReadOnlyDirectory(final Directory directory, final Collection<String> fileNames) {
		checkNotNull(directory, "Wrapped directory may not be null.");
		this.directory = directory;
		this.fileNames = ImmutableSet.copyOf(fileNames);
	}

	private void checkExists(final String name) throws FileNotFoundException {
		if (!fileExists(name)) {
			throw new FileNotFoundException();
		}
	}

	@Override
	public String[] listAll() throws IOException {
		return Iterables.toArray(fileNames, String.class);
	}

	@Override
	@Deprecated
	public boolean fileExists(final String name) {
		return fileNames.contains(name);
	}

	@Override
	public void deleteFile(final String name) throws IOException {
		throw new UnsupportedOperationException("Deleting files is not allowed in " + ReadOnlyDirectory.class);
	}

	@Override
	public long fileLength(final String name) throws IOException {
		checkExists(name);
		return directory.fileLength(name);
	}

	@Override
	public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
		throw new UnsupportedOperationException("Creating new files is not allowed in " + ReadOnlyDirectory.class);
	}

	@Override
	public void sync(final Collection<String> names) throws IOException {
		throw new UnsupportedOperationException("Syncing files is not allowed in " + ReadOnlyDirectory.class);
	}

	@Override
	public IndexInput openInput(final String name, final IOContext context) throws IOException {
		checkExists(name);
		return directory.openInput(name, context);
	}

	/* 
	 * XXX: Lock operations are still delegated to the backing directory, in case someone wants to pass this instance
	 * to IndexWriter#addIndexes(Directory...)
	 */
	@Override
	public Lock makeLock(final String name) {
		return directory.makeLock(name);
	}

	@Override
	public void clearLock(final String name) throws IOException {
		directory.clearLock(name);
	}

	@Override
	public void close() throws IOException {
		directory.close();
	}

	@Override
	public void setLockFactory(final LockFactory lockFactory) throws IOException {
		directory.setLockFactory(lockFactory);
	}

	@Override
	public LockFactory getLockFactory() {
		return directory.getLockFactory();
	}
}
