/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.bundle;

import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.0
 */
public class Bundles extends PageableCollectionResource<Bundle> {

	private static final long serialVersionUID = 1L;

	@JsonCreator
	public Bundles(
			@JsonProperty("items") final List<Bundle> items, 
			@JsonProperty("searchAfter") final String searchAfter, 
			@JsonProperty("limit") final int limit, 
			@JsonProperty("total") final int total) {
		super(items, searchAfter, limit, total);
	}

}
