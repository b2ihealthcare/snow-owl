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
package com.b2international.index;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @since 4.7
 */
public final class Hits<T> implements Iterable<T> {

	private final List<T> hits;
	private final int offset;
	private final int limit;
	private final int total;

	public Hits(List<T> hits, int offset, int limit, int total) {
		this.hits = hits;
		this.offset = offset;
		this.limit = limit;
		this.total = total;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getHits().iterator();
	}
	
	public List<T> getHits() {
		return hits;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getTotal() {
		return total;
	}
	
	public Stream<T> stream() {
		return hits.stream();
	}

	public static <T> Hits<T> empty(int offset, int limit) {
		return new Hits<>(Collections.<T>emptyList(), offset, limit, 0);
	}
	
}
