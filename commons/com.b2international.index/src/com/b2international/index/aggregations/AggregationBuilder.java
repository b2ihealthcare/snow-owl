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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.index.ScriptExpression;
import com.b2international.index.query.Expression;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0
 */
public final class AggregationBuilder<T> implements ScriptExpression {
	
	private final String name;
	private final Class<T> select;
	private final Class<?> from;
	
	private Expression query;
	private String groupByField;
	private List<String> fields = Collections.emptyList();
	private String groupByScript;
	private int minBucketSize = 1;
	private int bucketHitsLimit = 10;

	AggregationBuilder(String name, Class<T> select, Class<?> from) {
		this.name = name;
		this.select = select;
		this.from = from;
	}
	
	public AggregationBuilder<T> query(Expression query) {
		this.query = query;
		return this;
	}
	
	public AggregationBuilder<T> onFieldValue(String field) {
		this.groupByField = field;
		return this;
	}
	
	public AggregationBuilder<T> fields(String...fields) {
		return fields(ImmutableList.copyOf(fields));
	}
	
	public AggregationBuilder<T> fields(List<String> fields) {
		this.fields = fields;
		return this;
	}
	
	public AggregationBuilder<T> onScriptValue(String script) {
		this.groupByScript = script;
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
	
	public Class<T> getSelect() {
		return select;
	}
	
	public Class<?> getFrom() {
		return from;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGroupByField() {
		return groupByField;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public String getGroupByScript() {
		return groupByScript;
	}
	
	@Override
	public String getScript() {
		return getGroupByScript();
	}
	
	@Override
	public Map<String, Object> getParams() {
		return Collections.emptyMap();
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
		return bucket(name, from, from);
	}
	
	public static <T> AggregationBuilder<T> bucket(String name, Class<T> select, Class<?> from) {
		return new AggregationBuilder<>(name, select, from);
	}

}
