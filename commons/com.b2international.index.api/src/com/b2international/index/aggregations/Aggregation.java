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

import java.util.Map;

/**
 * @since 5.12.0 
 * @param <T> - the type of documents to aggregate
 */
public final class Aggregation<T> {

	private final String name;
	private final Map<Object, Bucket<T>> buckets;
	
	public Aggregation(String name, Map<Object, Bucket<T>> buckets) {
		this.name = name;
		this.buckets = buckets;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<Object, Bucket<T>> getBuckets() {
		return buckets;
	}
	
	public Bucket<T> getBucket(Object key) {
		return buckets.get(key);
	}
	
}
