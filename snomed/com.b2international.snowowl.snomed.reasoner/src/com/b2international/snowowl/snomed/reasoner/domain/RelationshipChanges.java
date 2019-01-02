/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.0
 */
public final class RelationshipChanges extends PageableCollectionResource<RelationshipChange> {

	/**
	 * Instantiates an empty pageable collection for relationship changes.
	 * 
	 * @param limit
	 *            - limit of items for a single page
	 * @param total
	 *            - total number of items in the result set
	 */
	public RelationshipChanges(final int limit, final int total) {
		super(Collections.emptyList(), null, null, limit, total);
	}

	/**
	 * Instantiates a pageable collection of relationship changes.
	 * 
	 * @param items
	 *            - list of @link {@link SnomedConcept}s
	 * @param scrollId
	 *            - scrollId for paging the result set continuously
	 * @param searchAfter
	 *            - searchAfter for paging the result set with a live cursor
	 * @param limit
	 *            - limit of items for a single page
	 * @param total
	 *            - total number of items in the result set
	 */
	@JsonCreator
	public RelationshipChanges(@JsonProperty("items") final List<RelationshipChange> items, @JsonProperty("scrollId") final String scrollId,
			@JsonProperty("searchAfter") final String searchAfter, @JsonProperty("limit") final int limit, @JsonProperty("total") final int total) {
		super(items, scrollId, searchAfter, limit, total);
	}
}
