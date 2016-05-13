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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.List;

import com.b2international.snowowl.datastore.request.RevisionSearchRequest;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;

/**
 * @since 4.5
 */
public abstract class SnomedSearchRequest<R> extends RevisionSearchRequest<R> {

	enum OptionKey {
		
		/**
		 * Language reference sets to use
		 */
		LANGUAGE_REFSET,
		
		/**
		 * Concept status to match
		 */
		ACTIVE,
		
		/**
		 * Concept module ID to match
		 */
		MODULE
	}
	
	protected SnomedSearchRequest() {}
	
	protected List<Long> languageRefSetIds() {
		return getList(OptionKey.LANGUAGE_REFSET, Long.class);
	}

	@Override
	protected String getIdField() {
		return "id";
	}
	
	protected final void addModuleClause(SnomedQueryBuilder queryBuilder) {
		if (containsKey(OptionKey.MODULE)) {
			queryBuilder.module(getString(OptionKey.MODULE));
		}
	}

	protected final void addActiveClause(SnomedQueryBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE)) {
			queryBuilder.active(getBoolean(OptionKey.ACTIVE));
		}
	}
}
