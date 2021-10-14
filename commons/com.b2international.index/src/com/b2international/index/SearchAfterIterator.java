/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Iterator;

import com.b2international.index.query.Query;

/**
 * @since 6.0 (as ScrollingIterator)
 * @param <T> - the type of paged documents
 */
public final class SearchAfterIterator<T> implements Iterator<Hits<T>> {

	private final Searcher searcher;
	private final Query<T> query;
	
	private Hits<T> hits;
	private boolean done;

	public SearchAfterIterator(Searcher searcher, Query<T> query) {
		this.searcher = searcher;
		this.query = query;
	}
	
	@Override
	public boolean hasNext() {
		
		if (done) {
			return false;
		}
		
		try {
			
			final Query<T> queryForPage = (hits == null) 
				? query 
				: query.withSearchAfter(hits.getSearchAfter()).build();
			
			hits = searcher.search(queryForPage);
			
		} catch (IOException e) {
			throw new IndexException("Failed to load next page of documents", e);
		}
		
		if (!hits.iterator().hasNext()) {
			hits = null;
			done = true;
			return false;
		}
		
		return true;
	}

	@Override
	public Hits<T> next() {
		return hits;
	}

}
