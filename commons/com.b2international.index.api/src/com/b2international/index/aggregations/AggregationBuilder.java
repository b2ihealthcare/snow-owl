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

import com.b2international.index.query.Expression;

/**
 * @since 5.12.0
 */
public final class AggregationBuilder<T> {
	
	private final String name;
	private final Class<T> from;
	
	private Expression query;
	private String field;
	private String script;
	private int minBucketSize = 1;
	private int bucketHitsLimit = 10;

	AggregationBuilder(String name, Class<T> from) {
		this.name = name;
		this.from = from;
	}
	
	public AggregationBuilder<T> query(Expression query) {
		this.query = query;
		return this;
	}
	
	public AggregationBuilder<T> onFieldValue(String field) {
		this.field = field;
		return this;
	}
	
	public AggregationBuilder<T> onScriptValue(String script) {
		this.script = script;
		return this;
	}
	
	public AggregationBuilder<T> minBucketSize(int minBucketSize) {
		this.minBucketSize = minBucketSize;
		return this;
	}
	
	public AggregationBuilder<T> setBucketHitsLimit(int minTopHitsPerBucket) {
		this.bucketHitsLimit = minTopHitsPerBucket;
		return this;
	}
	
	public Class<T> getFrom() {
		return from;
	}
	
	public String getName() {
		return name;
	}
	
	public String getField() {
		return field;
	}
	
	public String getScript() {
		return script;
	}
	
	public int getMinBucketSize() {
		return minBucketSize;
	}
	
	public int getBucketHitsLimit() {
		return bucketHitsLimit;
	}
	
	public Expression getQuery() {
		return query;
	}
	
	public static <T> AggregationBuilder<T> bucket(String name, Class<T> from) {
		return new AggregationBuilder<>(name, from);
	}

}
