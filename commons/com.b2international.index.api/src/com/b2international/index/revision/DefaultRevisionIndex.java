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
package com.b2international.index.revision;

import java.io.IOException;

import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.admin.IndexAdmin;

/**
 * @since 4.7
 */
public final class DefaultRevisionIndex implements RevisionIndex {

	private final Index index;
	private final RevisionBranchProvider branchProvider;

	public DefaultRevisionIndex(Index index, RevisionBranchProvider branchProvider) {
		this.index = index;
		this.branchProvider = branchProvider;
	}
	
	@Override
	public IndexAdmin admin() {
		return index.admin();
	}
	
	@Override
	public String name() {
		return index.name();
	}
	
	@Override
	public <T> T read(final String branchPath, final RevisionIndexRead<T> read) {
		return index.read(new IndexRead<T>() {
			@Override
			public T execute(Searcher index) throws IOException {
				final RevisionBranch branch = branchProvider.getBranch(branchPath);
				return read.execute(new DefaultRevisionSearcher(branch, index));
			}
		});
	}
	
	@Override
	public <T> T write(final String branchPath, final long commitTimestamp, final RevisionIndexWrite<T> write) {
		return index.write(new IndexWrite<T>() {
			@Override
			public T execute(Writer index) throws IOException {
				final RevisionBranch branch = branchProvider.getBranch(branchPath);
				final RevisionWriter writer = new DefaultRevisionWriter(branch.path(), commitTimestamp, index, new DefaultRevisionSearcher(branch, index.searcher()));
				return write.execute(writer);
			}
		});
	}
	
}
