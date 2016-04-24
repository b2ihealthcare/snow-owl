/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Version;

import com.b2international.index.analyzer.ComponentTermAnalyzer;
import com.b2international.index.mapping.Mappings;
import com.google.common.io.Closer;

/**
 * @since 4.7
 */
public final class FSIndexAdmin implements LuceneIndexAdmin {

	private final String name;
	private final Path indexPath;
	
	private Closer closer;
	private FSDirectory directory;
	private IndexWriter writer;
	private ReferenceManager<IndexSearcher> manager;

	public FSIndexAdmin(File directory, String name) {
		this.name = name;
		this.indexPath = directory.toPath().resolve(name);
	}

	@Override
	public IndexWriter getWriter() {
		exists();
		return writer;
	}
	
	@Override
	public ReferenceManager<IndexSearcher> getManager() {
		exists();
		return manager;
	}
	
	@Override
	public boolean exists() {
		return directoryExists() && writer != null && manager != null;
	}

	private boolean directoryExists() {
		try {
			return directory != null && DirectoryReader.indexExists(directory);
		} catch (IOException e) {
			throw new IndexException("Failed to check directory", e);
		}
	}

	@Override
	public void create() {
		try {
			closer = Closer.create();
			directory = open(indexPath.toFile());
			closer.register(directory);
			writer = new IndexWriter(directory, createConfig(false));
			closer.register(writer);
			writer.commit(); // actually create the index
			// TODO configure warmer, use NRT??? via config???
			manager = new SearcherManager(directory, null);
			closer.register(manager);
		} catch (IOException e) {
			close();
			throw new IndexException("Couldn't create index " + name(), e);
		}
	}

	private IndexWriterConfig createConfig(boolean clean) {
		// TODO configurable analyzer and options
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, new ComponentTermAnalyzer(true, true));
		config.setOpenMode(clean ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
		return config;
	}
	
	@Override
	public void close() {
		try {
			directory = null;
			writer = null;
			manager = null;
			closer.close();
			closer = null;
		} catch (IOException e) {
			throw new IndexException("Couldn't close index " + name(), e);
		}
	}

	@Override
	public void delete() {
		try {
			// reopen writer with clean option to clear directory
			writer.close();
			writer = new IndexWriter(directory, createConfig(true));
			closer.register(writer);
			close();
		} catch (IOException e) {
			throw new IndexException("Couldn't delete index " + name(), e);
		}
	}

	@Override
	public <T> void clear(Class<T> type) {
		// TODO remove all documents matching the given type, based on mappings
		throw new UnsupportedOperationException();
	}

	@Override
	public Mappings mappings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> settings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String name() {
		return name;
	}

	/**
	 * Creates an FSDirectory instance, trying to pick the best implementation given the current environment. The directory returned uses the
	 * {@link NativeFSLockFactory}.
	 *
	 * <p>
	 * Currently this returns {@link MMapDirectory} for most Solaris, Mac OS X and Windows 64-bit JREs, {@link NIOFSDirectory} for other non-Windows
	 * JREs, and {@link SimpleFSDirectory} for other JREs on Windows. It is highly recommended that you consult the implementation's documentation for
	 * your platform before using this method.
	 *
	 * <p>
	 * <b>NOTE</b>: this method may suddenly change which implementation is returned from release to release, in the event that higher performance
	 * defaults become possible; if the precise implementation is important to your application, please instantiate it directly, instead. For optimal
	 * performance you should consider using {@link MMapDirectory} on 64 bit JVMs.
	 *
	 */
	private static FSDirectory open(final File path) throws IOException {
		return open(path, null);
	}

	/**
	 * Just like {@link #open(File)}, but allows you to also specify a custom {@link LockFactory}.
	 */
	private static FSDirectory open(final File path, final LockFactory lockFactory) throws IOException {
		if ((Constants.WINDOWS || Constants.SUN_OS || Constants.LINUX || Constants.MAC_OS_X) && Constants.JRE_IS_64BIT
				&& MMapDirectory.UNMAP_SUPPORTED) {

			return new MMapDirectory(path, lockFactory);
		} else if (Constants.WINDOWS) {
			return new SimpleFSDirectory(path, lockFactory);
		} else {
			return new NIOFSDirectory(path, lockFactory);
		}
	}

}
