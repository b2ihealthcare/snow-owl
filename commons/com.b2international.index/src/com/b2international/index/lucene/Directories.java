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
package com.b2international.index.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Constants;

/**
 * @since 4.7
 */
public class Directories {

	/**
	 * Creates a new {@link RAMDirectory} instance.
	 * 
	 * @return
	 */
	public static RAMDirectory openRam() {
		return new RAMDirectory();
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
	public static FSDirectory openFile(final Path path) throws IOException {
		return openFile(path, NativeFSLockFactory.INSTANCE);
	}

	/**
	 * Just like {@link #openFile(File)}, but allows you to also specify a custom {@link LockFactory}.
	 */
	public static FSDirectory openFile(final Path path, final LockFactory lockFactory) throws IOException {
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
