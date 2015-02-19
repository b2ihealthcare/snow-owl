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
package com.b2international.snowowl.datastore.index;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.core.api.index.IndexException;

/**
 * Query adapter singleton used for requesting server side index service initialization.
 * <p><b>NOTE:&nbsp;</b>this may trigger creating snapshot of existing index folders.
 * <br>Always returns with empty collections as search results and {@code 0} for hit count.
 * <br>This class should be eliminated once and for all whenever <a href="https://github.com/b2ihealthcare/snowowl/issues/555">Implement branch path change listener mechanism on client</a> issue is solved.
 * @see IIndexQueryAdapter
 */
public enum FakeQueryAdapter implements IIndexQueryAdapter<IIndexEntry> {

	/**
	 * Singleton.
	 */
	INSTANCE;
	
	private static final long serialVersionUID = -3040805047666059028L;
	private static final String EMPTY_STRING = "";

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#search(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public List<IIndexEntry> search(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath) {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#search(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int)
	 */
	@Override
	public List<IIndexEntry> search(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#search(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int, int)
	 */
	@Override
	public List<IIndexEntry> search(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath, final int offset, final int limit) {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#searchIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int)
	 */
	@Override
	public List<String> searchIds(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#getHitCount(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public int getHitCount(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath) {
		final BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(EMPTY_STRING, EMPTY_STRING)), Occur.MUST);
		ClassUtils.checkAndCast(indexService, AbstractIndexService.class).getHitCount(branchPath, query, null);
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#searchUnsorted(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<IIndexEntry> searchUnsorted(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath) {
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#searchUnsortedIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<String> searchUnsortedIds(final IIndexService<? super IIndexEntry> indexService, final IBranchPath branchPath) {
		return Collections.emptySet();
	}
	
	@Override
	public void validate() throws IndexException {
		// Empty implementation
	}
}