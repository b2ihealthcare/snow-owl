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
package com.b2international.snowowl.datastore.server.internal.lucene.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.Set;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Represents a composite directory, where files may be retrieved from one "base" instance and an additional "overlay" instance.
 * <p>
 * Files can typically be written to the overlay instance only. 
 */
public class CompositeDirectory extends Directory {

	private final Directory baseDirectory;
	private final Directory overlayDirectory;

	public CompositeDirectory(final Directory baseDirectory, final Directory overlayDirectory) {
		this.baseDirectory = baseDirectory;
		this.overlayDirectory = overlayDirectory;
	}

	@Override
	public String[] listAll() throws IOException {
		final ImmutableSet.Builder<String> files = ImmutableSet.builder();
		files.add(baseDirectory.listAll());
		files.add(overlayDirectory.listAll());
		return Iterables.toArray(files.build(), String.class);
	}

	@Override
	public void deleteFile(final String name) throws IOException {
		overlayDirectory.deleteFile(name);
	}

	@Override
	public long fileLength(final String name) throws IOException {
		IOException notFoundException = null;

		try {
			return overlayDirectory.fileLength(name);
		} catch (FileNotFoundException | NoSuchFileException e1) {
			notFoundException = e1;
		} catch (final IOException e2) {
			throw e2;
		}

		try {
			return baseDirectory.fileLength(name);
		} catch (FileNotFoundException | NoSuchFileException e1) {
			notFoundException.addSuppressed(e1);
		} catch (final IOException e2) {
			throw e2;
		}

		throw notFoundException;
	}

	@Override
	public void sync(final Collection<String> names) throws IOException {
		final Set<String> allNames = ImmutableSet.copyOf(names);
		final Set<String> overlayNames = ImmutableSet.copyOf(overlayDirectory.listAll());
		final SetView<String> syncedNames = Sets.intersection(allNames, overlayNames);

		overlayDirectory.sync(syncedNames.immutableCopy());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The file will be read from the overlay directory if it exists both in base and in overlay.
	 */
	@Override
	public IndexInput openInput(final String name, final IOContext context) throws IOException {
		IOException notFoundException = null;

		try {
			return overlayDirectory.openInput(name, context);
		} catch (FileNotFoundException | NoSuchFileException e1) {
			notFoundException = e1;
		} catch (final IOException e2) {
			throw e2;
		}

		try {
			return baseDirectory.openInput(name, context);
		} catch (FileNotFoundException | NoSuchFileException e1) {
			notFoundException.addSuppressed(e1);
		} catch (final IOException e2) {
			throw e2;
		}

		throw notFoundException;
	}

	@Override
	public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
		return overlayDirectory.createOutput(name, context);
	}

	@Override
	public void close() throws IOException {
		IOException closeException = null;

		try {
			overlayDirectory.close();
		} catch (final IOException e) {
			closeException = e;
		}

		try {
			baseDirectory.close();
		} catch (final IOException e) {
			if (closeException != null) {
				closeException.addSuppressed(e);
			} else {
				closeException = e;
			}
		}

		if (closeException != null) {
			throw closeException;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + baseDirectory + "," + overlayDirectory + ")";
	}

	@Override
	public Lock obtainLock(String name) throws IOException {
		return overlayDirectory.obtainLock(name);
	}
	
	@Override
	public void renameFile(String source, String dest) throws IOException {
		overlayDirectory.renameFile(source, dest);
	}
	
}
