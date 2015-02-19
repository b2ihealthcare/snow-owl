/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.advancedsearch;

/**
 * @since 3.0.1
 */
public class StringSearchCriteria extends AbstractSearchCriteria {

	private static final long serialVersionUID = 8849388127324372120L;
	private String searchString;
	private boolean exactTermSearch;

	public StringSearchCriteria(final String type, final boolean exactTerm) {
		super(type);
		this.exactTermSearch = exactTerm;
	}

	public StringSearchCriteria(String criteriaType) {
		this(criteriaType, true);
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(final String searchString) {
		this.searchString = searchString;
	}

	public boolean isExactTerm() {
		return exactTermSearch;
	}

	public void setExactTerm(boolean exactTerm) {
		this.exactTermSearch = exactTerm;
	}
}