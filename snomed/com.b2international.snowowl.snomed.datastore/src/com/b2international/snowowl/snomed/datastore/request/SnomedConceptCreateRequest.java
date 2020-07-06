/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.b2international.collections.PrimitiveSets;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

/**
 * @since 4.5
 */
public final class SnomedConceptCreateRequest extends BaseSnomedComponentCreateRequest {

	@Size(min = 2)
	private List<SnomedDescriptionCreateRequest> descriptions = Collections.emptyList();
	
	private List<SnomedRelationshipCreateRequest> relationships = Collections.emptyList();
	
	private SnomedRefSetCreateRequest refSetRequest;

	@NotNull
	private String definitionStatusId = Concepts.PRIMITIVE;
	
	@NotNull
	private SubclassDefinitionStatus subclassDefinitionStatus = SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES;

	SnomedConceptCreateRequest() {}
	
	void setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}
	
	void setDefinitionStatusId(String definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}
	
	void setDescriptions(final List<SnomedDescriptionCreateRequest> descriptions) {
		this.descriptions = ImmutableList.copyOf(descriptions);
	}
	
	void setRelationships(final List<SnomedRelationshipCreateRequest> relationships) {
		this.relationships = ImmutableList.copyOf(relationships);
	}
	
	void setRefSet(SnomedRefSetCreateRequest refSet) {
		this.refSetRequest = refSet;
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		return ImmutableSet.<String>builder()
				.addAll(super.getRequiredComponentIds(context))
				.addAll(ImmutableList.of(Concepts.FULLY_DEFINED, Concepts.PRIMITIVE))
				.addAll(descriptions.stream().flatMap(req -> req.getRequiredComponentIds(context).stream()).collect(Collectors.toSet()))
				.addAll(relationships.stream().flatMap(req -> req.getRequiredComponentIds(context).stream()).collect(Collectors.toSet()))
				.build();
	}

	@Override
	public String execute(TransactionContext context) {
		final SnomedConceptDocument concept = convertConcept(context);
		context.add(concept);

		convertDescriptions(context, concept.getId());
		convertRelationships(context, concept.getId());
		convertMembers(context, concept.getId());

		createRefSet(context, concept.getId());
		return concept.getId();
	}

	private SnomedConceptDocument convertConcept(final TransactionContext context) {
		final String newDefinitionStatusId;
		final Set<String> newAxiomExpressions = getOwlAxiomExpressions();

		if (!newAxiomExpressions.isEmpty()) {
			newDefinitionStatusId = SnomedOWLAxiomHelper.getDefinitionStatusFromExpressions(newAxiomExpressions);
		} else {
			if (definitionStatusId == null) {
				throw new BadRequestException("No axiom members or definition status was set for concept");
			}
			newDefinitionStatusId = definitionStatusId;
		}
		
		try {
			final String conceptId = ((ConstantIdStrategy) getIdGenerationStrategy()).getId();
			return SnomedComponents.newConcept()
					.withId(conceptId)
					.withActive(isActive())
					.withModule(getModuleId())
					.withDefinitionStatusId(newDefinitionStatusId)
					.withExhaustive(subclassDefinitionStatus.isExhaustive())
					.build(context);
		} catch (final ComponentNotFoundException e) {
			throw e.toBadRequestException();
		}
	}

	private Set<String> getOwlAxiomExpressions() {
		return Optional.ofNullable(members()).map(Collection::stream).orElseGet(Stream::empty)
			.filter(req -> req.hasProperty(SnomedRf2Headers.FIELD_OWL_EXPRESSION))
			.map(req -> req.getProperty(SnomedRf2Headers.FIELD_OWL_EXPRESSION))
			.collect(Collectors.toSet());
		
	}
	
	private void convertDescriptions(TransactionContext context, final String conceptId) {
		final Set<String> requiredDescriptionTypes = newHashSet(Concepts.FULLY_SPECIFIED_NAME, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
		final Multiset<String> preferredLanguageRefSetIds = HashMultiset.create();
		final Set<String> synonymAndDescendantIds = context.service(Synonyms.class).get();

		for (final SnomedDescriptionCreateRequest descriptionRequest : descriptions) {

			descriptionRequest.setConceptId(conceptId);

			if (null == descriptionRequest.getModuleId()) {
				descriptionRequest.setModuleId(getModuleId());
			}

			descriptionRequest.execute(context);

			final String typeId = descriptionRequest.getTypeId();

			if (synonymAndDescendantIds.contains(typeId)) {
				for (final Entry<String, Acceptability> acceptability : descriptionRequest.getAcceptability().entrySet()) {
					if (Acceptability.PREFERRED.equals(acceptability.getValue())) {
						preferredLanguageRefSetIds.add(acceptability.getKey());
						requiredDescriptionTypes.remove(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
					}
				}
			}

			requiredDescriptionTypes.remove(typeId);
		}

		if (!requiredDescriptionTypes.isEmpty()) {
			throw new BadRequestException("At least one fully specified name and one preferred term must be supplied with the concept.");
		}

		for (final com.google.common.collect.Multiset.Entry<String> languageRefSetIdOccurence : preferredLanguageRefSetIds.entrySet()) {
			if (languageRefSetIdOccurence.getCount() > 1) {
				throw new BadRequestException("More than one preferred term has been added for language reference set %s.", languageRefSetIdOccurence.getElement());				
			}
		}
	}
	
	private void convertRelationships(final TransactionContext context, String conceptId) {
		if (relationships != null) {
			for (final SnomedRelationshipCreateRequest relationshipRequest : relationships) {
				relationshipRequest.setSourceId(conceptId);
				
				if (null == relationshipRequest.getModuleId()) {
					relationshipRequest.setModuleId(getModuleId());
				}
				
				relationshipRequest.execute(context);
			}
		}
	}
	
	private void createRefSet(final TransactionContext context, String conceptId) {
		if (refSetRequest == null) {
			return;
		}
		
		checkParent(context);
		refSetRequest.setIdentifierId(conceptId);
		refSetRequest.execute(context);
	}
	
	private void checkParent(TransactionContext context) {
		final SnomedRefSetType refSetType = refSetRequest.getRefSetType();
		final String refSetTypeRootParent = SnomedRefSetUtil.getParentConceptId(refSetType);
		final Set<String> parents = getParents();
		if (!isValidParentage(context, refSetTypeRootParent, parents)) {
			throw new BadRequestException("'%s' type reference sets should be subtype of '%s' concept.", refSetType, refSetTypeRootParent);
		}
	}

	private boolean isValidParentage(TransactionContext context, String requiredSuperType, Collection<String> parents) {
		// first check if the requiredSuperType is specified in the parents collection
		if (parents.contains(requiredSuperType)) {
			return true;
		}
		
		// if not, then check if any of the specified parents is subTypeOf the requiredSuperType
		final long superTypeIdLong = Long.parseLong(requiredSuperType);
		final SnomedConcepts parentConcepts = SnomedRequests.prepareSearchConcept().setLimit(parents.size()).filterByIds(parents).build().execute(context);
		for (SnomedConcept parentConcept : parentConcepts) {
			if (parentConcept.getParentIds() != null) {
				if (PrimitiveSets.newLongOpenHashSet(parentConcept.getParentIds()).contains(superTypeIdLong)) {
					return true;
				}
			}
			if (parentConcept.getAncestorIds() != null) {
				if (PrimitiveSets.newLongOpenHashSet(parentConcept.getAncestorIds()).contains(superTypeIdLong)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public Collection<Request<?, ?>> getNestedRequests() {
		return ImmutableList.<Request<?, ?>>builder()
			.add(this)
			.addAll(descriptions)
			.addAll(relationships)
			.build();
	}

	/**
	 * @return all parent concept IDs from the relationship create requests.
	 */
	Set<String> getParents() {
		return relationships.stream().filter(req -> Concepts.IS_A.equals(req.getTypeId())).map(req -> req.getDestinationId()).collect(Collectors.toSet());
	}
}
