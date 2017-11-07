/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.google.common.base.Strings;

/**
 * @since 6.0
 * @param <T> - the type of scrolled documents
 */
public final class ScrollingIterator<T> implements Iterator<Hits<T>> {

	private final Searcher searcher;
	private final Query<T> query;
	
	private Hits<T> hits;
	private boolean done;

	public ScrollingIterator(Searcher searcher, Query<T> query) {
		this.searcher = searcher;
		this.query = query;
		
		// ensure we have defined scroll keep alive value
		if (Strings.isNullOrEmpty(query.getScrollKeepAlive())) {
			query.setScrollKeepAlive(Query.DEFAULT_SCROLL_KEEP_ALIVE);
		}
	}
	
	@Override
	public boolean hasNext() {
		
		if (done) {
			return false;
		}
		
		try {
			if (hits == null) {
				hits = searcher.search(query);
			} else {
				hits = searcher.scroll(new Scroll<>(query.getSelect(), query.getFrom(), hits.getScrollId(), query.getScrollKeepAlive()));
			}
		} catch (IOException e) {
			throw new IndexException("Failed to load next page of scrolled documents", e);
		}
		
		if (!hits.iterator().hasNext()) {
			searcher.cancelScroll(hits.getScrollId());
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
