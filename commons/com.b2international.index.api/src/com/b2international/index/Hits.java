/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.b2international.commons.StringUtils;
import com.google.common.base.MoreObjects;

/**
 * @since 4.7
 */
public final class Hits<T> implements Iterable<T> {

	private final List<T> hits;
	private final String scrollId;
	private final String searchAfter;
	private final int limit;
	private final int total;

	public Hits(List<T> hits, String scrollId, String searchAfter, int limit, int total) {
		this.hits = hits;
		this.scrollId = scrollId;
		this.searchAfter = searchAfter;
		this.limit = limit;
		this.total = total;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getHits().iterator();
	}
	
	public Stream<T> stream() {
		return hits.stream();
	}
	
	public boolean isEmpty() {
		return hits.isEmpty();
	}
	
	public List<T> getHits() {
		return hits;
	}
	
	public String getScrollId() {
		return scrollId;
	}
	
	public String getSearchAfter() {
		return searchAfter;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public int getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("hits", StringUtils.limitedToString(hits, 10))
				.add("limit", limit)
				.add("total", total)
				.add("scrollId", scrollId)
				.add("searchAfter", searchAfter)
				.toString();
	}

	public static <T> Hits<T> empty(int limit) {
		return new Hits<>(Collections.<T>emptyList(), null, null, limit, 0);
	}
}
