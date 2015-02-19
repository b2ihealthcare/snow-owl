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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

public class CompositeDirectory extends Directory {

	// The secondary directory will not be closed when the composite directory closes
	private final Directory primaryDirectory;
	private final IndexCommit parentCommit;

	public CompositeDirectory(final IndexCommit parentCommit, final Directory primaryDirectory) {
		this.parentCommit = parentCommit;
		this.primaryDirectory = primaryDirectory;
	}

	private void checkExists(final String name) throws IOException, FileNotFoundException {
		if (!fileExists(name)) {
			throw new FileNotFoundException(name);
		}
	}

	@Override
	public String[] listAll() throws IOException {
		final Set<String> files = new HashSet<String>(parentCommit.getFileNames());
		// Files will be fetched from the primary directory in case file names overlap
		files.addAll(Arrays.asList(primaryDirectory.listAll()));
		return (String[]) files.toArray(new String[files.size()]);
	}

	@Override
	public boolean fileExists(final String name) throws IOException {
		return primaryDirectory.fileExists(name) || parentCommit.getFileNames().contains(name);
	}

	@Override
	public void deleteFile(final String name) throws IOException {
		if (primaryDirectory.fileExists(name)) {
			primaryDirectory.deleteFile(name);
		}
	}

	@Override
	public long fileLength(final String name) throws IOException {
		checkExists(name);
		return primaryDirectory.fileExists(name) ? primaryDirectory.fileLength(name) : parentCommit.getDirectory().fileLength(name);
	}

	@Override
	public void sync(final Collection<String> names) throws IOException {
		final Set<String> primaryNames = new HashSet<String>(names);
		primaryNames.retainAll(Arrays.asList(primaryDirectory.listAll()));
		primaryDirectory.sync(primaryNames);
	}

	@Override
	public IndexInput openInput(final String name, final IOContext context) throws IOException {
		checkExists(name);
		return primaryDirectory.fileExists(name) 
				? primaryDirectory.openInput(name, context) 
				: parentCommit.getDirectory().openInput(name, context);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#createOutput(java.lang.String, org.apache.lucene.store.IOContext)
	 */
	@Override
	public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
		return primaryDirectory.createOutput(name, context);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.store.Directory#close()
	 */
	@Override
	public void close() throws IOException {
		primaryDirectory.close();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + parentCommit + "," + primaryDirectory + ")";
	}

	@Override
	public Lock makeLock(final String name) {
		return primaryDirectory.makeLock(name);
	}

	@Override
	public void clearLock(final String name) throws IOException {
		primaryDirectory.clearLock(name);
	}

	@Override
	public void setLockFactory(final LockFactory lockFactory) throws IOException {
		primaryDirectory.setLockFactory(lockFactory);
	}

	@Override
	public LockFactory getLockFactory() {
		return primaryDirectory.getLockFactory();
	} 
}