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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

/**
 * @since 5.0
 */
public final class DisMaxPredicate implements Expression {

	private final float tieBreaker;
	private final Collection<Expression> disjuncts;

	DisMaxPredicate(Collection<Expression> disjuncts, float tieBreaker) {
		checkArgument(!disjuncts.isEmpty(), "At least one disjunct expression is required");
		this.disjuncts = disjuncts;
		this.tieBreaker = tieBreaker;
	}
	
	public float tieBreaker() {
		return tieBreaker;
	}

	public Collection<Expression> disjuncts() {
		return disjuncts;
	}
	
	@Override
	public String toString() {
		return String.format("DISMAX(%s)", disjuncts());
	}
	
}
