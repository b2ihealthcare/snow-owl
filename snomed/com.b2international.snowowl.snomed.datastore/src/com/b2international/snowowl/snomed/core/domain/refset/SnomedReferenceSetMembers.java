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
import java.util.Set;

import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.5
 */
public final class SnomedReferenceSetMembers extends PageableCollectionResource<SnomedReferenceSetMember> {

	public static final Function<SnomedReferenceSetMembers, Set<String>> GET_REFERENCED_COMPONENT_IDS = new Function<SnomedReferenceSetMembers, Set<String>>() {
		@Override
		public Set<String> apply(SnomedReferenceSetMembers input) {
			return FluentIterable.from(input).transform(SnomedReferenceSetMember.GET_REFERENCED_COMPONENT_ID).toSet();
		}
	};

	public SnomedReferenceSetMembers(int limit, int total) {
		super(Collections.emptyList(), null, limit, total);
	}
	
	@JsonCreator
	public SnomedReferenceSetMembers(
			@JsonProperty("items") List<SnomedReferenceSetMember> items, 
			@JsonProperty("scrollId") String scrollId, 
			@JsonProperty("limit") int limit, 
			@JsonProperty("total") int total) {
		super(items, scrollId, limit, total);
	}

}
