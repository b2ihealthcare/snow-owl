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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.internal.lucene.store.CompositeDirectory;

/**
 * Directory manager for a {@link RAMDirectory}.
 */
public class RAMDirectoryManager implements IDirectoryManager {

	@Override
	public Directory createDirectory(final IBranchPath branchPath, final IndexBranchService baseService) throws IOException {
		if (BranchPathUtils.isMain(branchPath)) {
			return new RAMDirectory();
		} else {
			final IndexCommit commit = baseService.getIndexCommit(branchPath);
			return new CompositeDirectory(commit, new RAMDirectory());
		}
	}

	@Override
	public void deleteIndex(final IBranchPath branchPath) {
		//intentionally ignored
	}

	@Override
	public List<String> listFiles(final IBranchPath branchPath) {
		return Collections.emptyList();
	}
	
	@Override
	public void fireFirstStartup(final IndexBranchService service) {
		//does nothing
	}
}
