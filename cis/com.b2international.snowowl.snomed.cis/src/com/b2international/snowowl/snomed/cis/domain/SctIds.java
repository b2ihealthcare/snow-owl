/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.domain;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.5
 */
public final class SctIds extends CollectionResource<SctId> {

	@JsonCreator
	public SctIds(@JsonProperty("items") Collection<SctId> componentIds) {
		super(ImmutableList.copyOf(componentIds));
	}
	
	@JsonIgnore
	public Set<String> getComponentIds() {
		return stream().map(SctId::getSctid).collect(Collectors.toSet());
	}

}
