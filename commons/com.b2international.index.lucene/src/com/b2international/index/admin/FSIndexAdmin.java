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
package com.b2international.index.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.lucene.store.Directory;

import com.b2international.index.lucene.Directories;
import com.b2international.index.mapping.Mappings;

/**
 * @since 4.7
 */
public final class FSIndexAdmin extends BaseLuceneIndexAdmin {

	private final Path indexPath;

	public FSIndexAdmin(File directory, String name, Mappings mappings, Map<String, Object> settings) {
		super(name, mappings, settings);
		this.indexPath = directory.toPath().resolve(name);
	}

	@Override
	protected Directory openDirectory() throws IOException {
		return Directories.openFile(indexPath);
	}
	
}
