/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCardinalityPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedCompositeDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConceptSetDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConcreteDomainPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedDependencyPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedDescriptionPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedEnumeratedDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedHierarchyDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedPredicate;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedReferenceSetDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipDefinition;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedRelationshipPredicate;
import com.b2international.snowowl.snomed.datastore.index.constraint.CardinalityPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.CompositeDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ConceptSetDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ConcreteDomainPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.DependencyPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.DescriptionPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.EnumeratedDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.HierarchyDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.PredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ReferenceSetDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;

/**
 * @since 5.7
 */
final class SnomedConstraintConverter extends BaseResourceConverter<SnomedConstraintDocument, SnomedConstraint, SnomedConstraints> {

	protected SnomedConstraintConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedConstraints createCollectionResource(List<SnomedConstraint> results, String scrollId, Object[] searchAfter, int limit, int total) {
		return new SnomedConstraints(results, scrollId, searchAfter, limit, total);
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
		constraint.setStorageKey(document.getStorageKey());
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
			concreteDomainPredicate.setLabel(((ConcreteDomainPredicateFragment) fragment).getLabel());
			concreteDomainPredicate.setName(((ConcreteDomainPredicateFragment) fragment).getName());
			concreteDomainPredicate.setType(((ConcreteDomainPredicateFragment) fragment).getType());
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
