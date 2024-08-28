/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 9.2.0 
 */
public final class Description implements Comparable<Description> {

	private final String term;
	private final String language;
	
	private Object internalDescription;
	
	public Description(@JsonProperty("term") String term) {
		this(term, null);
	}
	
	@JsonCreator
	public Description(@JsonProperty("term") String term, @JsonProperty("language") String language) {
		this.term = term;
		this.language = language;
	}
	
	public String getTerm() {
		return term;
	}
	
	public String getLanguage() {
		return language;
	}
	
	@JsonIgnore
	public <T> T getInternalDescription() {
		return (T) internalDescription;
	}
	
	public Description withInternalDescription(Object internalDescription) {
		this.internalDescription = internalDescription;
		return this;
	}
	
	@Override
	public int compareTo(Description other) {
		if (other == null) return -1;
		
		var termCompareResult = Strings.nullToEmpty(term).compareTo(Strings.nullToEmpty(other.term));
		if (termCompareResult != 0) {
			return termCompareResult;
		} else {
			return Strings.nullToEmpty(language).compareTo(Strings.nullToEmpty(other.language));
		}
	}
	
}
