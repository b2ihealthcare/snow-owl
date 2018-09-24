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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

/**
 * Represents a predicate with a single argument.
 * 
 * @since 4.7
 * @param <A> the argument type
 */
public abstract class SingleArgumentPredicate<A> extends Predicate {

	private final A argument;

	public SingleArgumentPredicate(String field, A argument) {
		super(field);
		this.argument = checkNotNull(argument, "argument");
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getField(), argument);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			SingleArgumentPredicate<?> other = (SingleArgumentPredicate<?>) obj; 
			return Objects.equals(argument, other.argument);
		} else {
			return false;
		}
	}
	
	public final A getArgument() {
		return argument;
	}

	@Override
	public String toString() {
		return String.format("%s = %s", getField(), getArgument());
	}
}