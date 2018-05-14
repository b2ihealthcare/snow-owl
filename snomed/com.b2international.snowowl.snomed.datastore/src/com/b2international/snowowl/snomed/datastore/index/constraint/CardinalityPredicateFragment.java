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

import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.5
 */
public final class CardinalityPredicateFragment extends PredicateFragment {

	private final int minCardinality;
	private final int maxCardinality;
	private final GroupRule groupRule;
	private final PredicateFragment predicate;

	@JsonCreator
	public CardinalityPredicateFragment(
			@JsonProperty("uuid") final String uuid, 
			@JsonProperty("active") final boolean active, 
			@JsonProperty("effectiveTime") final long effectiveTime, 
			@JsonProperty("author") final String author,
			@JsonProperty("minCardinality") final int minCardinality, 
			@JsonProperty("maxCardinality") final int maxCardinality, 
			@JsonProperty("groupRule") final GroupRule groupRule, 
			@JsonProperty("predicate") final PredicateFragment predicate) {

		super(uuid, active, effectiveTime, author);

		this.minCardinality = minCardinality;
		this.maxCardinality = maxCardinality;
		this.groupRule = groupRule;
		this.predicate = predicate;
	}

	public int getMinCardinality() {
		return minCardinality;
	}

	public int getMaxCardinality() {
		return maxCardinality;
	}

	public GroupRule getGroupRule() {
		return groupRule;
	}

	public PredicateFragment getPredicate() {
		return predicate;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + Objects.hash(groupRule, maxCardinality, minCardinality, predicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		
		CardinalityPredicateFragment other = (CardinalityPredicateFragment) obj;
		
		return Objects.equals(groupRule, other.groupRule)
				&& maxCardinality == other.maxCardinality
				&& minCardinality == other.minCardinality
				&& Objects.equals(predicate, other.predicate);
	}
}
