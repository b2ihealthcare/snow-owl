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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.11
 */
public class Description implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum Type {
		FSN,
		PT,
		SYNONYM,
		UNKNOWN;
	}
	
	private final String term;
	private final Type type;
	
	public Description(final String term, final Type type) {
		this.term = term;
		this.type = type;
	}
	
	public String getTerm() {
		return term;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return term + " " + type.name();
	}
	
	@JsonIgnore
	public static Description toSynonym(final String term) {
		return new Description(term, Type.SYNONYM);
	}
}
