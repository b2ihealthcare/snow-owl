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

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;

/**
 * Reference manager providing the same {@link NullIndexSearcher NULL index searcher} instance.
 */
public final class NullSearcherManager extends ReferenceManager<IndexSearcher> {

	/**The shared instance.*/
	private static ReferenceManager<IndexSearcher> instance;
	
	/**Returns with the singleton NULL searcher manager.*/
	public static ReferenceManager<IndexSearcher> getInstance() {
		
		if (null == instance) {
			
			synchronized (NullSearcherManager.class) {
				
				if (null == instance) {
					
					instance = new NullSearcherManager();
					
				}
				
			}
			
		}
		
		return instance;
		
	}
	
	@Override
	protected void decRef(final IndexSearcher reference) throws IOException {
		//intentionally does nothing
	}

	/**
	 * Always returns with {@code null} to indicate that no refresh is needed for the null searcher.
	 */
	@Override
	protected IndexSearcher refreshIfNeeded(final IndexSearcher referenceToRefresh) throws IOException {
		return NullIndexSearcher.getInstance();
	}

	@Override
	protected int getRefCount(IndexSearcher reference) {
		return 1;
	}
	
	/**
	 * Always return with {@code true}. 
	 */
	@Override
	protected boolean tryIncRef(final IndexSearcher reference) throws IOException {
		return true;
	}
	
	@Override
	protected void afterClose() throws IOException {
		// Sneak the NullIndexSearcher back in
		current = NullIndexSearcher.getInstance();
	}

	/**Private constructor. Initialize the current wrapped service to the {@link NullIndexSearcher NULL index searcher} instance.*/
	private NullSearcherManager() {
		current = NullIndexSearcher.getInstance();
	}
}