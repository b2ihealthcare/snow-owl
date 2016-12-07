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
 * Represents a predicate against a single field
 * 
 * @since 4.7
 */
public abstract class Predicate implements Expression {

	private final String field;
	
	protected Predicate(String field) {
		this.field = checkNotNull(field, "feature");
	}

	@Override
	public int hashCode() {
		return Objects.hash(field);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Predicate predicate = (Predicate) obj;
		return Objects.equals(field, predicate.field);
	}
	
	public final String getField() {
		return field;
	}
	
}