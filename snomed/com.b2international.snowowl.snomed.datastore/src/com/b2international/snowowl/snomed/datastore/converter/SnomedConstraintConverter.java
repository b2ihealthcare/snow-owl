/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.snomed.core.domain.constraint.*;
import com.b2international.snowowl.snomed.datastore.index.constraint.*;

/**
 * @since 5.7
 */
public final class SnomedConstraintConverter extends BaseResourceConverter<SnomedConstraintDocument, SnomedConstraint, SnomedConstraints> {

	public SnomedConstraintConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedConstraints createCollectionResource(List<SnomedConstraint> results, String searchAfter, int limit, int total) {
		return new SnomedConstraints(results, searchAfter, limit, total);
	}

	@Override
	protected SnomedConstraint toResource(SnomedConstraintDocument document) {
		final SnomedConstraint constraint = new SnomedConstraint();
		
		constraint.setActive(document.isActive());
		constraint.setAuthor(document.getAuthor());
		constraint.setDescription(document.getDescription());
		constraint.setDomain(toResource(document.getDomain()));
		constraint.setEffectiveTime(document.getEffectiveTime());
		constraint.setForm(document.getForm());
		constraint.setId(document.getId());
		constraint.setPredicate(toResource(document.getPredicate()));
		constraint.setReleased(false);
		constraint.setStrength(document.getStrength());
		constraint.setValidationMessage(document.getValidationMessage());
		
		return constraint;
	}

	private SnomedConceptSetDefinition toResource(ConceptSetDefinitionFragment fragment) {
		final SnomedConceptSetDefinition definition;
		
		if (fragment instanceof CompositeDefinitionFragment) {
			final SnomedCompositeDefinition compositeDefinition = new SnomedCompositeDefinition();
			compositeDefinition.setChildren(((CompositeDefinitionFragment) fragment).getChildren().stream()
					.map(this::toResource)
					.collect(Collectors.toSet()));
			definition = compositeDefinition;
		} else if (fragment instanceof EnumeratedDefinitionFragment) {
			final SnomedEnumeratedDefinition enumeratedDefinition = new SnomedEnumeratedDefinition();
			enumeratedDefinition.setConceptIds(((EnumeratedDefinitionFragment) fragment).getConceptIds());
			definition = enumeratedDefinition;
		} else if (fragment instanceof HierarchyDefinitionFragment) {
			final SnomedHierarchyDefinition hierarchyDefinition = new SnomedHierarchyDefinition();
			hierarchyDefinition.setConceptId(((HierarchyDefinitionFragment) fragment).getConceptId());
			hierarchyDefinition.setInclusionType(((HierarchyDefinitionFragment) fragment).getInclusionType());
			definition = hierarchyDefinition;
		} else if (fragment instanceof SingletonDefinitionFragment) {
			final SnomedSingletonDefinition singletonDefinition = new SnomedSingletonDefinition();
			singletonDefinition.setConceptId(((SingletonDefinitionFragment) fragment).getConceptId());
			definition = singletonDefinition;
		} else if (fragment instanceof ReferenceSetDefinitionFragment) {
			final SnomedReferenceSetDefinition referenceSetDefinition = new SnomedReferenceSetDefinition();
			referenceSetDefinition.setRefSetId(((ReferenceSetDefinitionFragment) fragment).getRefSetId());
			definition = referenceSetDefinition;
		} else if (fragment instanceof RelationshipDefinitionFragment) {
			final SnomedRelationshipDefinition relationshipDefinition = new SnomedRelationshipDefinition();
			relationshipDefinition.setTypeId(((RelationshipDefinitionFragment) fragment).getTypeId());
			relationshipDefinition.setDestinationId(((RelationshipDefinitionFragment) fragment).getDestinationId());
			definition = relationshipDefinition;
		} else {
			throw new IllegalStateException("Unexpected concept set definition subtype '" + fragment.getClass().getSimpleName() + "'.");
		}
		
		definition.setActive(fragment.isActive());
		definition.setAuthor(fragment.getAuthor());
		definition.setEffectiveTime(fragment.getEffectiveTime());
		definition.setId(fragment.getUuid());
		definition.setReleased(false);
		// storage key is not set for concept set definitions 
		
		return definition;
	}

	private SnomedPredicate toResource(PredicateFragment fragment) {
		final SnomedPredicate predicate;
		
		if (fragment instanceof CardinalityPredicateFragment) {
			final SnomedCardinalityPredicate cardinalityPredicate = new SnomedCardinalityPredicate();
			cardinalityPredicate.setGroupRule(((CardinalityPredicateFragment) fragment).getGroupRule());
			cardinalityPredicate.setMaxCardinality(((CardinalityPredicateFragment) fragment).getMaxCardinality());
			cardinalityPredicate.setMinCardinality(((CardinalityPredicateFragment) fragment).getMinCardinality());
			cardinalityPredicate.setPredicate(toResource(((CardinalityPredicateFragment) fragment).getPredicate()));
			predicate = cardinalityPredicate;
		} else if (fragment instanceof ConcreteDomainPredicateFragment) {
			final SnomedConcreteDomainPredicate concreteDomainPredicate = new SnomedConcreteDomainPredicate();
			concreteDomainPredicate.setCharacteristicTypeId(((ConcreteDomainPredicateFragment) fragment).getCharacteristicTypeId());
			concreteDomainPredicate.setAttribute(toResource(((ConcreteDomainPredicateFragment) fragment).getAttribute()));
			concreteDomainPredicate.setRange(((ConcreteDomainPredicateFragment) fragment).getRange());
			predicate = concreteDomainPredicate;
		} else if (fragment instanceof DependencyPredicateFragment) {
			final SnomedDependencyPredicate dependencyPredicate = new SnomedDependencyPredicate();
			dependencyPredicate.setChildren(((DependencyPredicateFragment) fragment).getChildren().stream()
					.map(this::toResource)
					.collect(Collectors.toSet()));
			dependencyPredicate.setDependencyOperator(((DependencyPredicateFragment) fragment).getDependencyOperator());
			dependencyPredicate.setGroupRule(((DependencyPredicateFragment) fragment).getGroupRule());
			predicate = dependencyPredicate;
		} else if (fragment instanceof DescriptionPredicateFragment) {
			final SnomedDescriptionPredicate descriptionPredicate = new SnomedDescriptionPredicate();
			descriptionPredicate.setTypeId(((DescriptionPredicateFragment) fragment).getTypeId());
			predicate = descriptionPredicate;
		} else if (fragment instanceof RelationshipPredicateFragment) {
			final SnomedRelationshipPredicate relationshipPredicate = new SnomedRelationshipPredicate();
			relationshipPredicate.setAttribute(toResource(((RelationshipPredicateFragment) fragment).getAttribute()));
			relationshipPredicate.setCharacteristicTypeId(((RelationshipPredicateFragment) fragment).getCharacteristicTypeId());
			relationshipPredicate.setRange(toResource(((RelationshipPredicateFragment) fragment).getRange()));
			predicate = relationshipPredicate;
		} else {
			throw new IllegalStateException("Unexpected concept set definition subtype '" + fragment.getClass().getSimpleName() + "'.");
		}
		
		predicate.setActive(fragment.isActive());
		predicate.setAuthor(fragment.getAuthor());
		predicate.setEffectiveTime(fragment.getEffectiveTime());
		predicate.setId(fragment.getUuid());
		predicate.setReleased(false);
		// storage key is not set for predicates 

		return predicate;
	}
}
