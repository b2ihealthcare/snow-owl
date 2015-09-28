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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;

/**
 * Abstract superclass for {@link IIndexUpdater} implementations which provides a Lucene-specific (non-API) interface and routes
 * all update requests through the supplied query adapter.
 * 
 * @param E the {@link IIndexEntry} subtype this index service uses
 * 
 */
public abstract class AbstractIndexUpdater<E extends IIndexEntry> extends AbstractIndexService<E> implements IIndexUpdater<E> {

	@Override
	public final void index(final IBranchPath branchPath, final IIndexMappingStrategy indexMappingStrategy) {
		indexMappingStrategy.index(this, branchPath);
	}
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param term
	 */
	public abstract void delete(final IBranchPath branchPath, final Term term);
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param document
	 * @param id
	 * 
	 */
	public abstract void index(final IBranchPath branchPath, final Document document, final Term id);
	
	public abstract void index(final IBranchPath branchPath, final Document document, final long storageKey);
	
	public abstract <D extends DocumentBuilderBase<D>> void update(IBranchPath branchPath, long storageKey, DocumentUpdater<D> documentUpdater, DocumentBuilderFactory<D> builderFactory);
	
	public abstract <D extends DocumentBuilderBase<D>> void upsert(IBranchPath branchPath, Query term, DocumentUpdater<D> documentUpdater, DocumentBuilderFactory<D> builderFactory);
}