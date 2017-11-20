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
package com.b2international.index.aggregations;

import java.util.Iterator;
import java.util.stream.Stream;

import com.b2international.index.Hits;

/**
 * @since 5.12.0
 */
public final class Bucket<T> implements Iterable<T> {

	private final Object key;
	private final Hits<T> hits;
	
	public Bucket(Object key, Hits<T> hits) {
		this.key = key;
		this.hits = hits;
	}
	
	public Object getKey() {
		return key;
	}
	
	public Hits<T> getHits() {
		return hits;
	}
	
	@Override
	public Iterator<T> iterator() {
		return hits.iterator();
	}
	
	public Stream<T> stream() {
		return hits.stream();
	}

}
