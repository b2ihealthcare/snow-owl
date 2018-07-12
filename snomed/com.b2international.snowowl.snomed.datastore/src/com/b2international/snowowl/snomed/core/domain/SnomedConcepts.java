/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;

/**
 * A page-able collection of SNOMED CT concepts. To access the items of this collection call
 * see {@link #getItems()}
 * 
 * @see SnomedConcept
 * @since 4.5
 */
public final class SnomedConcepts extends PageableCollectionResource<SnomedConcept> {

	public static final Function<SnomedConcepts, List<SnomedConceptDocument>> TO_DOCS = new Function<SnomedConcepts, List<SnomedConceptDocument>>() {
		@Override
		public List<SnomedConceptDocument> apply(SnomedConcepts input) {
			return SnomedConceptDocument.fromConcepts(input);
		}
	};

	/**
	 * Instantiates an empty pageable collection for SNOMED CT concepts.
	 * @param limit of items for a single page
	 * @param total number of items in the resultset
	 */
	public SnomedConcepts(int limit, int total) {
		super(Collections.emptyList(), null, null, limit, total);
	}
	
	/**
	 * Instantiates a pageable collection of SNOMED CT concepts.
	 * @param list of @link {@link SnomedConcept}s
	 * @param scrollId for paging the result set continuously
	 * @param searchAfter for paging the result set with a live cursor
	 * @param limit of items for a single page
	 * @param total number of items in the resultset
	 */
	@JsonCreator
	public SnomedConcepts(
			@JsonProperty("items") List<SnomedConcept> items, 
			@JsonProperty("scrollId") String scrollId,
			@JsonProperty("searchAfter") String searchAfter,
			@JsonProperty("limit") int limit, 
			@JsonProperty("total") int total) {
		super(items, scrollId, searchAfter, limit, total);
	}
}
