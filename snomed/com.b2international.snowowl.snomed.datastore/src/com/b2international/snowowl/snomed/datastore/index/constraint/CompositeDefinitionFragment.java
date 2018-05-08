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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
public final class CompositeDefinitionFragment extends ConceptSetDefinitionFragment {

	private final Set<ConceptSetDefinitionFragment> children;

	@JsonCreator
	CompositeDefinitionFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("children") final Set<ConceptSetDefinitionFragment> children) {

		super(uuid, active, effectiveTime, author);
		this.children = ImmutableSet.copyOf(children);
	}

	public Set<ConceptSetDefinitionFragment> getChildren() {
		return children;
	}

	@Override
	public String toEcl() {
		final Set<String> subExpressions = children.stream()
				.map(ConceptSetDefinitionFragment::toEcl)
				.collect(Collectors.toSet());

		return Joiner.on(" OR ").join(subExpressions);
	}
}
