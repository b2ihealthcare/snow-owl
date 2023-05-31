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
package com.b2international.snowowl.core.rest.concepts;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

/**
 * @since 8.11.0
 */
public class ConceptRestSearch extends ObjectRestSearch {

	private Boolean active;
	private List<String> codeSystem;
	private String term;
	private String query;
	
	public Boolean getActive() {
		return active;
	}
	
	public List<String> getCodeSystem() {
		return codeSystem;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public void setCodeSystem(List<String> codeSystem) {
		this.codeSystem = codeSystem;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
}
