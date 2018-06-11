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
package com.b2international.snowowl.core.commit;

import java.util.List;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.6
 */
public final class CommitInfoDetails extends PageableCollectionResource<CommitInfoDetail> {

	@JsonCreator
	public CommitInfoDetails(
			@JsonProperty("items") final List<CommitInfoDetail> items, 
			@JsonProperty("scrollId") final String scrollId, 
			@JsonProperty("searchAfter") final Object[] searchAfter, 
			@JsonProperty("limit") final int limit, 
			@JsonProperty("total") final int total) {
		super(items, scrollId, searchAfter, limit, total);
	}

}
