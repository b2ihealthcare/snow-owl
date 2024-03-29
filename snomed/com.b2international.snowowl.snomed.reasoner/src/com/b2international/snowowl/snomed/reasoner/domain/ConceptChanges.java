/*
 * Copyright 2019-2020 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.14
 */
public final class ConceptChanges extends PageableCollectionResource<ConceptChange> {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates an empty pageable collection for concept changes.
	 * 
	 * @param limit
	 *            - number of items for a single page
	 * @param total
	 *            - number of items in the result set
	 */
	public ConceptChanges(final int limit, final int total) {
		super(Collections.emptyList(), null, limit, total);
	}

	/**
	 * Instantiates a pageable collection of concept changes.
	 * 
	 * @param items
	 *            - list of @link {@link ConceptChange}s
	 * @param searchAfter
	 *            - for paging the result set with a live cursor
	 * @param limit
	 *            - number of items for a single page
	 * @param total
	 *            - number of items in the result set
	 */
	@JsonCreator
	public ConceptChanges(
			@JsonProperty("items") final List<ConceptChange> items, 
			@JsonProperty("searchAfter") final String searchAfter,
			@JsonProperty("limit") final int limit, 
			@JsonProperty("total") final int total) {
		super(items, searchAfter, limit, total);
	}
}
