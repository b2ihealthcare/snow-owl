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

import java.util.stream.Collectors;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

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

	public static PredicateFragment from(final ConceptModelPredicate predicate) {
		
		if (predicate instanceof CardinalityPredicate) {
			return new CardinalityPredicateFragment(predicate.getUuid(), 
					predicate.isActive(), 
					EffectiveTimes.getEffectiveTime(predicate.getEffectiveTime()), 
					predicate.getAuthor(),
					((CardinalityPredicate) predicate).getMinCardinality(),
					((CardinalityPredicate) predicate).getMaxCardinality(),
					((CardinalityPredicate) predicate).getGroupRule(),
					PredicateFragment.from(((CardinalityPredicate) predicate).getPredicate()));
					
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			return new ConcreteDomainPredicateFragment(predicate.getUuid(), 
					predicate.isActive(), 
					EffectiveTimes.getEffectiveTime(predicate.getEffectiveTime()), 
					predicate.getAuthor(), 
					ConceptSetDefinitionFragment.from(((ConcreteDomainElementPredicate) predicate).getAttribute()), 
					((ConcreteDomainElementPredicate) predicate).getRange(), 
					((ConcreteDomainElementPredicate) predicate).getCharacteristicTypeConceptId());
			
		} else if (predicate instanceof DependencyPredicate) {
			return new DependencyPredicateFragment(predicate.getUuid(), 
					predicate.isActive(), 
					EffectiveTimes.getEffectiveTime(predicate.getEffectiveTime()), 
					predicate.getAuthor(),
					((DependencyPredicate) predicate).getGroupRule(),
					((DependencyPredicate) predicate).getOperator(),
					((DependencyPredicate) predicate).getChildren().stream()
						.map(PredicateFragment::from)
						.collect(Collectors.toSet()));
			
		} else if (predicate instanceof DescriptionPredicate) {
			return new DescriptionPredicateFragment(predicate.getUuid(), 
					predicate.isActive(), 
					EffectiveTimes.getEffectiveTime(predicate.getEffectiveTime()), 
					predicate.getAuthor(),
					((DescriptionPredicate) predicate).getTypeId());
			
		} else if (predicate instanceof RelationshipPredicate) {
			return new RelationshipPredicateFragment(predicate.getUuid(), 
					predicate.isActive(), 
					EffectiveTimes.getEffectiveTime(predicate.getEffectiveTime()), 
					predicate.getAuthor(),
					ConceptSetDefinitionFragment.from(((RelationshipPredicate) predicate).getAttribute()), 
					ConceptSetDefinitionFragment.from(((RelationshipPredicate) predicate).getRange()), 
					((RelationshipPredicate) predicate).getCharacteristicTypeConceptId());
			
		} else {
			throw new IllegalArgumentException("Unexpected concept model predicate class '" + predicate.getClass().getSimpleName() + "'.");
		}
	}
}
