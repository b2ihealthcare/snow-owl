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

import java.util.Objects;
import java.util.Set;

import com.b2international.snowowl.snomed.core.domain.constraint.DependencyOperator;
import com.b2international.snowowl.snomed.core.domain.constraint.GroupRule;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
public final class DependencyPredicateFragment extends PredicateFragment {

	private final GroupRule groupRule;
	private final DependencyOperator dependencyOperator;
	private final Set<PredicateFragment> children;

	@JsonCreator
	public DependencyPredicateFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("groupRule") final GroupRule groupRule, 
			@JsonProperty("dependencyOperator") final DependencyOperator dependencyOperator, 
			@JsonProperty("children") final Set<PredicateFragment> children) {

		super(uuid, active, effectiveTime, author);

		this.groupRule = groupRule;
		this.dependencyOperator = dependencyOperator;
		this.children = ImmutableSet.copyOf(children);
	}

	public GroupRule getGroupRule() {
		return groupRule;
	}

	public DependencyOperator getDependencyOperator() {
		return dependencyOperator;
	}

	public Set<PredicateFragment> getChildren() {
		return children;
	}
	
	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(groupRule, dependencyOperator, children);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		
		DependencyPredicateFragment other = (DependencyPredicateFragment) obj;
		
		return Objects.equals(groupRule, other.groupRule)
				&& Objects.equals(dependencyOperator, other.dependencyOperator)
				&& Objects.equals(children, other.children);
	}
}
