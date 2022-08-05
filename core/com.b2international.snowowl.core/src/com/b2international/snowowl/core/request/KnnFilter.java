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

import java.io.Serializable;

import com.b2international.collections.floats.FloatList;

/**
 * @since 8.5
 */
public final class KnnFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private final FloatList queryVector;
	private final Integer numCandidates;

	public KnnFilter(final FloatList queryVector, final Integer numCandidates) {
		this.queryVector = queryVector;
		this.numCandidates = numCandidates;
	}
	
	public FloatList getQueryVector() {
		return queryVector;
	}
	
	public Integer getNumCandidates() {
		return numCandidates;
	}
	
}
