/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.1
 */
public final class ValidationWhiteLists extends PageableCollectionResource<ValidationWhiteList>{

	public ValidationWhiteLists(int limit, int total) {
		this(Collections.emptyList(), null, limit, total);
	}

	@JsonCreator
	public ValidationWhiteLists(
			@JsonProperty("items") List<ValidationWhiteList> items, 
			@JsonProperty("searchAfter") String searchAfter, 
			@JsonProperty("limit") int limit,
			@JsonProperty("total") int total) {
		super(items, searchAfter, limit, total);
	}
}
