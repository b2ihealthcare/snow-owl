/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @since 7.7
 */
public final class QueryExpressionDiff implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<QueryExpression> addToInclusion;
	private final List<QueryExpression> addToExclusion;
	private final List<QueryExpression> remove;
	
	public QueryExpressionDiff(List<QueryExpression> addToInclusion, List<QueryExpression> addToExclusion, List<QueryExpression> remove) {
		this.addToInclusion = addToInclusion;
		this.addToExclusion = addToExclusion;
		this.remove = remove;
	}

	public List<QueryExpression> getAddToInclusion() {
		return addToInclusion;
	}
	
	public List<QueryExpression> getAddToExclusion() {
		return addToExclusion;
	}
	
	public List<QueryExpression> getRemove() {
		return remove;
	}
}
