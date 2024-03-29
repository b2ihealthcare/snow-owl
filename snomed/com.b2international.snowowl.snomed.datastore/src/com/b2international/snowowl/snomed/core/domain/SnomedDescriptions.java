/*
 * Copyright 2011-2020 B2i Healthcare, https://b2ihealthcare.com
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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public final class SnomedDescriptions extends PageableCollectionResource<SnomedDescription> {

	public SnomedDescriptions(int limit, int total) {
		super(Collections.emptyList(), null, limit, total);
	}

	@JsonCreator
	public SnomedDescriptions(
			@JsonProperty("items") List<SnomedDescription> items, 
			@JsonProperty("searchAfter") String searchAfter,
			@JsonProperty("limit") int limit, 
			@JsonProperty("total") int total) {
		super(items, searchAfter, limit, total);
	}
}
