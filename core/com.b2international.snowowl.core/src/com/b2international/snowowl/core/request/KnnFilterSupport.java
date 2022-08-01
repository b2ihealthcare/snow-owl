/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.collections.floats.FloatList;

/**
 * Interface to configure knn search filter in a given request API
 * 
 * @since 8.5
 */
public interface KnnFilterSupport<T> {

	/**
	 * Filter results by returning semantically similar matches compared to the given query vector.
	 * 
	 * @param queryVector
	 * @return
	 */
	default T filterByKnn(FloatList queryVector) {
		return filterByKnn(queryVector, null);
	}
	
	/**
	 * Filter results by returning semantically similar matches compared to the given query vector.
	 * 
	 * @param queryVector - the query vector to match against
	 * @param numCandidates - can be used to configure the underlying Elasticsearch knn search to improve accuracy
	 * @return
	 * @see #filterByKnn(FloatList)
	 */
	default T filterByKnn(FloatList queryVector, Integer numCandidates) {
		return filterByKnn(queryVector == null ? null : new KnnFilter(queryVector, numCandidates));
	}
	
	/**
	 * Filter results by returning semantically similar matches compared to the given knn filter (query vector and num candidates configuration).
	 *
	 * @param knnFilter
	 * @return
	 * @see #filterByKnn(FloatList)
	 * @see #filterByKnn(FloatList, Integer)
	 */
	T filterByKnn(KnnFilter knnFilter);
	
}
