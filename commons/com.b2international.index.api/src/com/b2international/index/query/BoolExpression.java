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
package com.b2international.index.query;

import java.util.List;

/**
 * @since 4.7
 */
public final class BoolExpression implements Expression {

	private final List<Expression> mustClauses;
	private final List<Expression> mustNotClauses;
	private final List<Expression> shouldClauses;
	private final List<Expression> filterClauses;
	private int minShouldMatch = 1;

	BoolExpression(List<Expression> mustClauses, List<Expression> mustNotClauses, List<Expression> shouldClauses, List<Expression> filterClauses) {
		this.mustClauses = mustClauses;
		this.mustNotClauses = mustNotClauses;
		this.shouldClauses = shouldClauses;
		this.filterClauses = filterClauses;
	}
	
	void setMinShouldMatch(int minShouldMatch) {
		this.minShouldMatch = minShouldMatch;
	}
	
	public int minShouldMatch() {
		return minShouldMatch;
	}
	
	public List<Expression> mustClauses() {
		return mustClauses;
	}
	
	public List<Expression> mustNotClauses() {
		return mustNotClauses;
	}
	
	public List<Expression> shouldClauses() {
		return shouldClauses;
	}
	
	public List<Expression> filterClauses() {
		return filterClauses;
	}
	
}
