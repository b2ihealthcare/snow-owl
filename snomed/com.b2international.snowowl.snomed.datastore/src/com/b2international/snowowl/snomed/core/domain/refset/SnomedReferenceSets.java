/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class SnomedReferenceSets extends PageableCollectionResource<SnomedReferenceSet> {

	public SnomedReferenceSets(int limit, int total) {
		super(Collections.emptyList(), null, null, limit, total);
	}
	
	@JsonCreator
	public SnomedReferenceSets(
			@JsonProperty("items") List<SnomedReferenceSet> items, 
			@JsonProperty("scrollId") String scrollId, 
			@JsonProperty("searchAfter") Object[] searchAfter,
			@JsonProperty("limit") int limit, 
			@JsonProperty("total") int total) {
		super(items, scrollId, searchAfter, limit, total);
	}

}
