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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @since 4.7
 */
public class IntSetPredicate extends Predicate {

	private final Set<Integer> values;

	IntSetPredicate(String field, Iterable<Integer> values) {
		super(field);
		this.values = ImmutableSet.copyOf(values);
	}
	
	public Set<Integer> values() {
		return values;
	}
	
	@Override
	public String toString() {
		return String.format("ANY%s", values);
	}
	
}
