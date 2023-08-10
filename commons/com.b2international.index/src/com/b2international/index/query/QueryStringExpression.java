/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query;

import java.util.Objects;

/**
 * @since 8.12.0
 */
public final class QueryStringExpression implements Expression {

	private final String query;
	private final String defaultField;

	public QueryStringExpression(String query, String defaultField) {
		this.query = query;
		this.defaultField = defaultField;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getDefaultField() {
		return defaultField;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(query, defaultField);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		QueryStringExpression predicate = (QueryStringExpression) obj;
		return Objects.equals(query, predicate.query) 
				&& Objects.equals(defaultField, predicate.defaultField);
	}
	
	@Override
	public String toString() {
		return String.format("QUERY_STRING(%s)[defaultField:'%s']", query, defaultField);
	}
	
}
