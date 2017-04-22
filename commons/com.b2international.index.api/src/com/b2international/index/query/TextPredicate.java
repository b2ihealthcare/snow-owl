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

/**
 * @since 4.7
 */
public final class TextPredicate extends Predicate {

	public enum MatchType {
		ALL, ANY, PHRASE, FUZZY, PARSED
	}
	
	private final String term;
	private final MatchType type;

	TextPredicate(String field, String term, MatchType type) {
		super(field);
		this.term = term;
		this.type = type;
	}
	
	public String term() {
		return term;
	}
	
	public MatchType type() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.format("TEXT(%s %s %s)", getField(), type(), term());
	}

}
