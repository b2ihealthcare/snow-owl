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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.google.common.collect.Iterables;


/**
 * TODO remove Lucene stuff from the API
 * @since 4.3
 */
public class QueryBuilderBase<Q extends QueryBuilderBase<Q>> {
	
	private Collection<Query> queries = newArrayList();
	
	public static final class QueryBuilder extends QueryBuilderBase<QueryBuilder> {}
	
	protected QueryBuilderBase() {
	}
	
	protected final <T> Q addToQuery(IndexField<T> field, T value) {
		return and(field.toQuery(value));
	}
	
	public Q and(Query query) {
		queries.add(query);
		return (Q) this;
	}
	
	public Q id(String value) {
		return addToQuery(Fields.id(), value);
	}
	
	public Q type(int value) {
		return addToQuery(Fields.type(), value);
	}
	
	public Q type(short value) {
		return type((int) value);
	}
	
	public Q ancestor(String value) {
		return addToQuery(Fields.ancestor(), value);
	}
	
	public Q parent(String value) {
		return addToQuery(Fields.parent(), value);
	}
	
	public Q storageKey(Long value) {
		return addToQuery(Fields.storageKey(), value);
	}
	
	public Q field(String fieldName, String value) {
		return addToQuery(Fields.stringField(fieldName), value);
	}
	
	public Q field(String fieldName, Long value) {
		return addToQuery(Fields.longField(fieldName), value);
	}
	
	public Q field(String fieldName, Integer value) {
		return addToQuery(Fields.intField(fieldName), value);
	}
	
	public Q field(String fieldName, boolean value) {
		return addToQuery(Fields.boolField(fieldName), value);
	}
	
	public Query matchAll() {
		return build(Occur.MUST, queries);
	}
	
	public Query matchAny() {
		return build(Occur.SHOULD, queries);
	}
	
	public final boolean isEmpty() {
		return queries.isEmpty();
	}
	
	private Query build(final Occur occur, Iterable<Query> queries) {
		final int size = Iterables.size(queries);
		checkArgument(size > 0, "At least one clause must be specified to build a query");
		if (size == 1) {
			return Iterables.getOnlyElement(queries);
		} else {
			final BooleanQuery.Builder query = new BooleanQuery.Builder();
			for (Query q : queries) {
				query.add(q, occur);
			}
			return query.build();
		}
	}

}
