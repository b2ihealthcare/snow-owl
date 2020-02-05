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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @since 6.5
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({ 
	@Type(value = CardinalityPredicateFragment.class, name = "cardinality"), 
	@Type(value = ConcreteDomainPredicateFragment.class, name = "concreteDomain"), 
	@Type(value = DependencyPredicateFragment.class, name = "dependency"), 
	@Type(value = DescriptionPredicateFragment.class, name = "description"), 
	@Type(value = RelationshipPredicateFragment.class, name = "relationship"), 
})
public abstract class PredicateFragment extends ConceptModelComponentFragment {

	protected PredicateFragment(
			final String uuid, 
			final boolean active, 
			final long effectiveTime, 
			final String author) {

		super(uuid, active, effectiveTime, author);
	}
	
}
