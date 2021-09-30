/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.stream.Stream;

import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Query;
import com.google.common.collect.Streams;

/**
 * @since 4.7
 */
public interface Searcher {

	/**
	 * Execute the given query among all stored items.
	 * 
	 * @param query
	 *            - the query to execute
	 * @return - an {@link Iterable} of matching values
	 * @throws IOException
	 *             - if something goes wrong during the execution of the query
	 */
	<T> Hits<T> search(Query<T> query) throws IOException;
	
	/**
	 * Execute an aggregation among all stored documents.
	 * 
	 * @param aggregation
	 * @return
	 * @throws IOException
	 */
	<T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException;
	
	/**
	 * Fetch an object by type and key from the index.
	 * 
	 * @param type
	 *            - the object's type to retrieve
	 * @param key
	 *            - the unique identifier of the object
	 * @return the object
	 * @throws IOException
	 */
	<T> T get(Class<T> type, String key) throws IOException;
	
	/**
	 * Fetch multiple objects by type and keys from the index.
	 * 
	 * @param type
	 *            - the type of the object
	 * @param keys
	 *            - the logical, unique identifiers of the objects to load
	 * @return an {@link Iterable} of {@link Object}s, never <code>null</code>.
	 * @throws IOException
	 */
	<T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException;
	
	/**
	 * Returns a {@link Stream} that computes all matches of the given query,
	 * returning them in chunks defined in the query limit.
	 * 
	 * @param query
	 * @return
	 */
	default <T> Stream<Hits<T>> stream(Query<T> query) {
		return Streams.stream(new SearchAfterIterator<T>(this, query)); 
	}
}
