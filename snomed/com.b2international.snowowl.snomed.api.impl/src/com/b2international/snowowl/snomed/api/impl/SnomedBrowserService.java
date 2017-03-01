/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.domain.IStorageRef;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.datastore.server.domain.InternalStorageRef;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserParentConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.SnomedBrowserDescriptionType;
import com.b2international.snowowl.snomed.api.domain.browser.TaxonomyNode;
import com.b2international.snowowl.snomed.api.impl.domain.InputFactory;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescriptionResultDetails;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserParentConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ConceptEnum;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SnomedBrowserService implements ISnomedBrowserService {

	private static final List<ConceptEnum> CONCEPT_ENUMS;

	static {
		final ImmutableList.Builder<ConceptEnum> conceptEnumsBuilder = ImmutableList.builder();
		
		conceptEnumsBuilder.add(DefinitionStatus.values());
		conceptEnumsBuilder.add(CharacteristicType.values());
		conceptEnumsBuilder.add(CaseSignificance.values());
		conceptEnumsBuilder.add(SnomedBrowserDescriptionType.values());
		conceptEnumsBuilder.add(RelationshipModifier.values());
		
		CONCEPT_ENUMS = conceptEnumsBuilder.build();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedBrowserService.class);

	private final InputFactory inputFactory;

	@Resource
	private IEventBus bus;

	public SnomedBrowserService() {
		inputFactory = new InputFactory();
	}

	@Override
	public ISnomedBrowserConcept getConceptDetails(final IComponentRef conceptRef, final List<ExtendedLocale> locales) {

		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		internalConceptRef.checkStorageExists();
		
		final IBranchPath branchPath = internalConceptRef.getBranch().branchPath();
		final String conceptId = conceptRef.getComponentId();
		final SnomedConcept concept = SnomedRequests.prepareGetConcept(conceptId)
				.setLocales(locales)
				.setExpand("pt(),fsn(),descriptions(limit:"+Integer.MAX_VALUE+"),relationships(limit:"+Integer.MAX_VALUE+")")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(bus)
				.getSync();
		
		final DescriptionService descriptionService = new DescriptionService(bus, conceptRef.getBranchPath());
		
		final SnomedDescription fullySpecifiedName = concept.getFsn();
		final SnomedDescription preferredSynonym = concept.getPt();

		final SnomedBrowserConcept result = new SnomedBrowserConcept();

		result.setActive(concept.isActive());
		result.setConceptId(concept.getId());
		result.setDefinitionStatus(concept.getDefinitionStatus());
		result.setEffectiveTime(concept.getEffectiveTime());
		result.setModuleId(concept.getModuleId());
		
		populateLeafFields(branchPath.getPath(), conceptId, result);
		
		result.setDescriptions(convertDescriptions(concept.getDescriptions()));
		
		if (fullySpecifiedName != null) {
			result.setFsn(fullySpecifiedName.getTerm());
		} else {
			result.setFsn(conceptId);
		}
		
		if (preferredSynonym != null) {
			result.setPreferredSynonym(preferredSynonym.getTerm());
		}

		result.setRelationships(convertRelationships(concept.getRelationships(), conceptRef, locales));
		
		return result;
	}

	@Override
	public ISnomedBrowserConcept create(String branchPath, ISnomedBrowserConcept concept, String userId, List<ExtendedLocale> locales) {
		final SnomedConceptCreateRequest req = inputFactory.createComponentInput(branchPath, concept, SnomedConceptCreateRequest.class);
		String commitComment = getCommitComment(userId, concept, "creating");
		final String createdConceptId = SnomedRequests
				.prepareCommit()
				.setCommitComment(commitComment)
				.setBody(req)
				.setUserId(userId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
				.execute(bus)
				.getSync()
				.getResultAs(String.class);
		final IComponentRef componentRef = SnomedServiceHelper.createComponentRef(branchPath, createdConceptId);
		return getConceptDetails(componentRef, locales);
	}

	@Override
	public ISnomedBrowserConcept update(String branchPath, ISnomedBrowserConceptUpdate newVersionConcept, String userId, List<ExtendedLocale> locales) {
		final Stopwatch watch = Stopwatch.createStarted();
		
		LOGGER.info("Update concept start {}", newVersionConcept.getFsn());
		final IComponentRef componentRef = SnomedServiceHelper.createComponentRef(branchPath, newVersionConcept.getConceptId());

		final ISnomedBrowserConcept existingVersionConcept = getConceptDetails(componentRef, locales);

		// Concept update
		final SnomedConceptUpdateRequest conceptUpdate = inputFactory.createComponentUpdate(existingVersionConcept, newVersionConcept, SnomedConceptUpdateRequest.class);
		
		// Description updates
		final List<ISnomedBrowserDescription> existingVersionDescriptions = existingVersionConcept.getDescriptions();
		final List<ISnomedBrowserDescription> newVersionDescriptions = newVersionConcept.getDescriptions();
		Set<String> descriptionDeletionIds = inputFactory.getComponentDeletions(existingVersionDescriptions, newVersionDescriptions);
		Map<String, SnomedDescriptionUpdateRequest> descriptionUpdates = inputFactory.createComponentUpdates(existingVersionDescriptions, newVersionDescriptions, SnomedDescriptionUpdateRequest.class);
		List<SnomedDescriptionCreateRequest> descriptionInputs = inputFactory.createComponentInputs(branchPath, newVersionDescriptions, SnomedDescriptionCreateRequest.class);
		LOGGER.info("Got description changes +{} -{} m{}, {}", descriptionInputs.size(), descriptionDeletionIds.size(), descriptionUpdates.size(), newVersionConcept.getFsn());

		// Relationship updates
		final List<ISnomedBrowserRelationship> existingVersionRelationships = existingVersionConcept.getRelationships();
		final List<ISnomedBrowserRelationship> newVersionRelationships = newVersionConcept.getRelationships();
		Set<String> relationshipDeletionIds = inputFactory.getComponentDeletions(existingVersionRelationships, newVersionRelationships);
		Map<String, SnomedRelationshipUpdateRequest> relationshipUpdates = inputFactory.createComponentUpdates(existingVersionRelationships, newVersionRelationships, SnomedRelationshipUpdateRequest.class);
		List<SnomedRelationshipCreateRequest> relationshipInputs = inputFactory.createComponentInputs(branchPath, newVersionRelationships, SnomedRelationshipCreateRequest.class);
		LOGGER.info("Got relationship changes +{} -{} m{}, {}", relationshipInputs.size(), relationshipDeletionIds.size(), relationshipUpdates.size(), newVersionConcept.getFsn());

		// In the case of inactivation, other updates seem to go more smoothly if this is done later
		final BulkRequestBuilder<TransactionContext> commitReq = BulkRequest.create();
		
		boolean conceptInactivation = conceptUpdate != null && conceptUpdate.isActive() != null && Boolean.FALSE.equals(conceptUpdate.isActive());
		if (conceptUpdate != null && !conceptInactivation) {
			commitReq.add(conceptUpdate);
		}
		
		for (String descriptionId : descriptionUpdates.keySet()) {
			commitReq.add(descriptionUpdates.get(descriptionId));
		}
		for (SnomedDescriptionCreateRequest descriptionReq : descriptionInputs) {
			descriptionReq.setConceptId(existingVersionConcept.getConceptId());
			commitReq.add(descriptionReq);
		}
		for (String descriptionDeletionId : descriptionDeletionIds) {
			commitReq.add(SnomedRequests.prepareDeleteDescription().setComponentId(descriptionDeletionId).build());
		}

		for (String relationshipId : relationshipUpdates.keySet()) {
			commitReq.add(relationshipUpdates.get(relationshipId));
		}
		for (SnomedRelationshipCreateRequest relationshipReq : relationshipInputs) {
			commitReq.add(relationshipReq);
		}
		for (String relationshipDeletionId : relationshipDeletionIds) {
			commitReq.add(SnomedRequests.prepareDeleteRelationship().setComponentId(relationshipDeletionId).build());
		}

		// Inactivate concept last
		if (conceptUpdate != null && conceptInactivation) {
			commitReq.add(conceptUpdate);
		}

		// TODO - Add MRCM checks here

		// Commit
		final String commitComment = getCommitComment(userId, newVersionConcept, "updating");
		SnomedRequests
			.prepareCommit()
			.setUserId(userId)
			.setCommitComment(commitComment)
			.setBody(commitReq)
			.setPreparationTime(watch.elapsed(TimeUnit.MILLISECONDS))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
			.execute(bus)
			.getSync();
		LOGGER.info("Committed changes for concept {}", newVersionConcept.getFsn());

		return getConceptDetails(componentRef, locales);
	}
	
	private String getCommitComment(String userId, ISnomedBrowserConcept snomedConceptInput, String action) {
		String fsn = getFsn(snomedConceptInput);
		return userId + " " + action + " concept " + fsn;
	}

	private String getFsn(ISnomedBrowserConcept snomedConceptInput) {
		for (ISnomedBrowserDescription descriptionInput : snomedConceptInput.getDescriptions()) {
			if (Concepts.FULLY_SPECIFIED_NAME.equals(descriptionInput.getType().getConceptId())) {
				return descriptionInput.getTerm();
			}
		}
		return null;
	}

	private List<ISnomedBrowserDescription> convertDescriptions(final Iterable<SnomedDescription> descriptions) {
		final ImmutableList.Builder<ISnomedBrowserDescription> convertedDescriptionBuilder = ImmutableList.builder();

		for (final SnomedDescription description : descriptions) {
			final SnomedBrowserDescription convertedDescription = new SnomedBrowserDescription();

			final SnomedBrowserDescriptionType descriptionType = convertDescriptionType(description.getTypeId());
			final String descriptionId = description.getId();
			if (null == descriptionType) {
				LOGGER.warn("Unsupported description type ID {} on description {}, ignoring.", description.getTypeId(), descriptionId);
				continue;
			}
			convertedDescription.setActive(description.isActive());
			convertedDescription.setCaseSignificance(description.getCaseSignificance());
			convertedDescription.setConceptId(description.getConceptId());
			convertedDescription.setDescriptionId(descriptionId);
			convertedDescription.setEffectiveTime(description.getEffectiveTime());
			convertedDescription.setLang(description.getLanguageCode());
			convertedDescription.setModuleId(description.getModuleId());
			convertedDescription.setTerm(description.getTerm());
			convertedDescription.setType(descriptionType);
			convertedDescription.setAcceptabilityMap(description.getAcceptabilityMap());
			convertedDescriptionBuilder.add(convertedDescription);
		}

		return convertedDescriptionBuilder.build();
	}

	private SnomedBrowserDescriptionType convertDescriptionType(final String typeId) {
		return SnomedBrowserDescriptionType.getByConceptId(typeId);
	}

	private List<ISnomedBrowserRelationship> convertRelationships(final Iterable<SnomedRelationship> relationships, final IComponentRef sourceConceptRef, final List<ExtendedLocale> locales) {
		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(sourceConceptRef, InternalComponentRef.class);
		final IBranchPath branchPath = internalConceptRef.getBranch().branchPath();
		final DescriptionService descriptionService = new DescriptionService(bus, sourceConceptRef.getBranchPath());

		final ImmutableMap.Builder<String, ISnomedBrowserRelationship> convertedRelationshipBuilder = ImmutableMap.builder();
		for (final SnomedRelationship relationship : relationships) {
			final SnomedBrowserRelationship convertedRelationship = new SnomedBrowserRelationship(relationship.getId());
			convertedRelationship.setActive(relationship.isActive());
			convertedRelationship.setCharacteristicType(relationship.getCharacteristicType());
			convertedRelationship.setEffectiveTime(relationship.getEffectiveTime());
			convertedRelationship.setGroupId(relationship.getGroup());
			convertedRelationship.setModifier(relationship.getModifier());
			convertedRelationship.setModuleId(relationship.getModuleId());
			convertedRelationship.setSourceId(relationship.getSourceId());
			convertedRelationshipBuilder.put(relationship.getId(), convertedRelationship);
		}
		
		final Map<String, ISnomedBrowserRelationship> convertedRelationships = convertedRelationshipBuilder.build();
		
		final List<SnomedBrowserRelationshipType> types = new FsnJoinerOperation<SnomedBrowserRelationshipType>(sourceConceptRef.getComponentId(), 
				locales, 
				descriptionService) {
			
			@Override
			protected Iterable<SnomedConcept> getConceptEntries(String conceptId) {
				final Set<String> typeIds = newHashSet();
				for (final SnomedRelationship relationship : relationships) {
					typeIds.add(relationship.getTypeId());
				}
				return getConcepts(branchPath, typeIds);
			}

			@Override
			protected SnomedBrowserRelationshipType convertConceptEntry(SnomedConcept conceptEntry, Optional<String> optionalFsn) {
				final SnomedBrowserRelationshipType type = new SnomedBrowserRelationshipType();
				type.setConceptId(conceptEntry.getId());
				type.setFsn(optionalFsn.or(conceptEntry.getId()));
				return type;
			}
		}.run();
		
		final Map<String, SnomedBrowserRelationshipType> typesById = Maps.uniqueIndex(types, new Function<SnomedBrowserRelationshipType, String>() {
			@Override
			public String apply(SnomedBrowserRelationshipType input) {
				return input.getConceptId();
			}
		});
		
		final List<SnomedBrowserRelationshipTarget> targets = new FsnJoinerOperation<SnomedBrowserRelationshipTarget>(sourceConceptRef.getComponentId(), 
				locales, 
				descriptionService) {
			
			@Override
			protected Iterable<SnomedConcept> getConceptEntries(String conceptId) {
				final Set<String> destinationConceptIds = newHashSet();
				for (final SnomedRelationship relationship : relationships) {
					destinationConceptIds.add(relationship.getDestinationId());
				}
				return getConcepts(branchPath, destinationConceptIds);
			}

			@Override
			protected SnomedBrowserRelationshipTarget convertConceptEntry(SnomedConcept destinationConcept, Optional<String> optionalFsn) {
				final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();
				target.setActive(destinationConcept.isActive());
				target.setConceptId(destinationConcept.getId());
				target.setDefinitionStatus(destinationConcept.getDefinitionStatus());
				target.setEffectiveTime(destinationConcept.getEffectiveTime());
				target.setModuleId(destinationConcept.getModuleId());
				target.setFsn(optionalFsn.or(destinationConcept.getId()));
				return target;
			}
		}.run();
		
		final Map<String, SnomedBrowserRelationshipTarget> targetsById = Maps.uniqueIndex(targets, new Function<SnomedBrowserRelationshipTarget, String>() {
			@Override
			public String apply(SnomedBrowserRelationshipTarget input) {
				return input.getConceptId();
			}
		});
		
		for (SnomedRelationship entry : relationships) {
			SnomedBrowserRelationship rel = (SnomedBrowserRelationship) convertedRelationships.get(entry.getId());
			SnomedBrowserRelationshipType type = typesById.get(entry.getTypeId());
			SnomedBrowserRelationshipTarget target = targetsById.get(entry.getDestinationId());
			rel.setType(type);
			rel.setTarget(target);
		}
		
		return ImmutableList.copyOf(convertedRelationships.values());
	}
	
	private SnomedConcepts getConcepts(final IBranchPath branchPath, final Set<String> destinationConceptIds) {
		if (destinationConceptIds.isEmpty()) {
			return new SnomedConcepts(0, 0, 0);
		}
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(destinationConceptIds)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(bus)
				.getSync();
	}

	protected SnomedBrowserRelationshipTarget getSnomedBrowserRelationshipTarget(SnomedConcept destinationConcept, String branch, List<ExtendedLocale> locales) {
		final DescriptionService descriptionService = new DescriptionService(bus, branch);
		final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();

		target.setActive(destinationConcept.isActive());
		target.setConceptId(destinationConcept.getId());
		target.setDefinitionStatus(destinationConcept.getDefinitionStatus());
		target.setEffectiveTime(destinationConcept.getEffectiveTime());
		target.setModuleId(destinationConcept.getModuleId());

		SnomedDescription fullySpecifiedName = descriptionService.getFullySpecifiedName(destinationConcept.getId(), locales);
		if (fullySpecifiedName != null) {
			target.setFsn(fullySpecifiedName.getTerm());
		} else {
			target.setFsn(destinationConcept.getId());
		}
		
		return target;
	}

	@Override
	public List<ISnomedBrowserParentConcept> getConceptParents(final IComponentRef conceptRef, final List<ExtendedLocale> locales) {
		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		final IBranchPath branchPath = internalConceptRef.getBranch().branchPath();
		final DescriptionService descriptionService = new DescriptionService(bus, conceptRef.getBranchPath());

		return new FsnJoinerOperation<ISnomedBrowserParentConcept>(conceptRef.getComponentId(), locales, descriptionService) {
			
			@Override
			protected Iterable<SnomedConcept> getConceptEntries(String conceptId) {
				return SnomedRequests.prepareGetConcept(conceptId)
						.setExpand("ancestors(form:\"inferred\",direct:true)")
						.setLocales(locales)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
						.execute(bus)
						.getSync().getAncestors();
			}

			@Override
			protected ISnomedBrowserParentConcept convertConceptEntry(SnomedConcept conceptEntry, Optional<String> optionalFsn) {
				final String childConceptId = conceptEntry.getId();
				final SnomedBrowserParentConcept convertedConcept = new SnomedBrowserParentConcept(); 

				convertedConcept.setConceptId(childConceptId);
				convertedConcept.setDefinitionStatus(conceptEntry.getDefinitionStatus());
				convertedConcept.setFsn(optionalFsn.or(childConceptId));

				return convertedConcept;
			}
			
		}.run();
	}
	
	@Override
	public List<ISnomedBrowserChildConcept> getConceptChildren(final IComponentRef conceptRef, final List<ExtendedLocale> locales, final boolean stated) {
		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		final String branch = internalConceptRef.getBranch().path();
		final DescriptionService descriptionService = new DescriptionService(bus, conceptRef.getBranchPath());

		return new FsnJoinerOperation<ISnomedBrowserChildConcept>(conceptRef.getComponentId(), locales, descriptionService) {
			
			@Override
			protected Iterable<SnomedConcept> getConceptEntries(String conceptId) {
				return SnomedRequests.prepareSearchConcept()
						.all()
						.filterByActive(true)
						.filterByParent(stated ? null : conceptId)
						.filterByStatedParent(stated ? conceptId : null)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
						.execute(bus)
						.getSync();
				
			}

			@Override
			protected ISnomedBrowserChildConcept convertConceptEntry(SnomedConcept conceptEntry, Optional<String> optionalFsn) {
				final String childConceptId = conceptEntry.getId();
				final SnomedBrowserChildConcept convertedConcept = new SnomedBrowserChildConcept(); 

				convertedConcept.setConceptId(childConceptId);
				convertedConcept.setActive(conceptEntry.isActive());
				convertedConcept.setDefinitionStatus(conceptEntry.getDefinitionStatus());
				convertedConcept.setModuleId(conceptEntry.getModuleId());
				convertedConcept.setFsn(optionalFsn.or(childConceptId));

				populateLeafFields(branch, childConceptId, convertedConcept);

				return convertedConcept;
			}
			
		}.run();
	}

	private void populateLeafFields(final String branch, final String conceptId, final TaxonomyNode node) {
		node.setIsLeafStated(!hasInboundRelationships(branch, conceptId, Concepts.STATED_RELATIONSHIP));
		node.setIsLeafInferred(!hasInboundRelationships(branch, conceptId, Concepts.INFERRED_RELATIONSHIP));
	}

	private boolean hasInboundRelationships(String branch, String conceptId, String characteristicTypeId) {
		return SnomedRequests.prepareSearchRelationship()
				.filterByActive(true)
				.filterByCharacteristicType(characteristicTypeId)
				.filterByDestination(conceptId)
				.filterByType(Concepts.IS_A)
				.setLimit(0)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(bus)
				.getSync().getTotal() > 0;
	}

	@Override
	public List<ISnomedBrowserDescriptionResult> getDescriptions(final IStorageRef storageRef, final String query, final List<ExtendedLocale> locales, final ISnomedBrowserDescriptionResult.TermType resultConceptTermType, final int offset, final int limit) {
		checkNotNull(storageRef, "Storage reference may not be null.");
		checkNotNull(query, "Query may not be null.");
		checkArgument(query.length() >= 3, "Query must be at least 3 characters long.");

		final InternalStorageRef internalStorageRef = ClassUtils.checkAndCast(storageRef, InternalStorageRef.class);
		internalStorageRef.checkStorageExists();

		final IBranchPath branchPath = internalStorageRef.getBranch().branchPath();
		final DescriptionService descriptionService = new DescriptionService(bus, storageRef.getBranchPath());
		
		final Collection<SnomedDescription> descriptions = SnomedRequests.prepareSearchDescription()
			.setOffset(offset)
			.setLimit(limit)
			.filterByTerm(query)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(bus)
			.getSync()
			.getItems();

		final Set<String> conceptIds = FluentIterable.from(descriptions)
			.transform(new Function<SnomedDescription, String>() {
				@Override public String apply(SnomedDescription input) {
					return input.getConceptId();
				}
			})
			.toSet();
		
		final Iterable<SnomedConcept> conceptIndexEntries = getConcepts(branchPath, conceptIds);
		final Map<String, SnomedConcept> conceptMap = Maps.uniqueIndex(conceptIndexEntries, IComponent.ID_FUNCTION);
		
		final Map<String, SnomedDescription> fsnPropertyMap; 
		switch (resultConceptTermType) {
		case FSN:
			fsnPropertyMap = descriptionService.getFullySpecifiedNames(conceptIds, locales);
			break;
		default:
			fsnPropertyMap = descriptionService.getPreferredTerms(conceptIds, locales);
			break;
		}
		
		final Cache<String, SnomedBrowserDescriptionResultDetails> detailCache = CacheBuilder.newBuilder().build();
		final ImmutableList.Builder<ISnomedBrowserDescriptionResult> resultBuilder = ImmutableList.builder();
		
		for (final SnomedDescription description : descriptions) {
			
			final String typeId = description.getTypeId();
			if (!Concepts.FULLY_SPECIFIED_NAME.equals(typeId) && !Concepts.SYNONYM.equals(typeId)) {
				continue;
			}
			
			final SnomedBrowserDescriptionResult descriptionResult = new SnomedBrowserDescriptionResult();
			descriptionResult.setActive(description.isActive());
			descriptionResult.setTerm(description.getTerm());
			
			try {
				final SnomedBrowserDescriptionResultDetails details = detailCache.get(description.getConceptId(), 
						new Callable<SnomedBrowserDescriptionResultDetails>() {
					
					@Override
					public SnomedBrowserDescriptionResultDetails call() throws Exception {
						final String conceptId = description.getConceptId();
						final SnomedConcept conceptIndexEntry = conceptMap.get(conceptId);
						final SnomedBrowserDescriptionResultDetails details = new SnomedBrowserDescriptionResultDetails();
						
						if (conceptIndexEntry != null) {
							details.setActive(conceptIndexEntry.isActive());
							details.setConceptId(conceptIndexEntry.getId());
							details.setDefinitionStatus(conceptIndexEntry.getDefinitionStatus());
							details.setModuleId(conceptIndexEntry.getModuleId());
							
							if (fsnPropertyMap.containsKey(conceptId)) {
								details.setFsn(fsnPropertyMap.get(conceptId).getTerm());
							} else {
								details.setFsn(conceptId);
							}
						} else {
							LOGGER.warn("Concept {} not found in map, properties will not be set.", conceptId);
						}
						
						return details;
					}
				});
				
				descriptionResult.setConcept(details);
			} catch (ExecutionException e) {
				LOGGER.error("Exception thrown during computing details for concept {}, properties will not be set.", description.getConceptId(), e);
			}
			
			resultBuilder.add(descriptionResult);
		}

		return resultBuilder.build();
	}

	@Override
	public Map<String, ISnomedBrowserConstant> getConstants(final String branch, final List<ExtendedLocale> locales) {
		final ImmutableMap.Builder<String, ISnomedBrowserConstant> resultBuilder = ImmutableMap.builder();
		final DescriptionService descriptionService = new DescriptionService(bus, branch);
		
		for (final ConceptEnum conceptEnum : CONCEPT_ENUMS) {
			try {
				final String conceptId = conceptEnum.getConceptId();

				// Check if the corresponding concept exists
				SnomedRequests.prepareGetConcept(conceptId)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
						.execute(bus)
						.getSync();
				
				final SnomedBrowserConstant constant = new SnomedBrowserConstant();
				constant.setConceptId(conceptId);
				
				final SnomedDescription fullySpecifiedName = descriptionService.getFullySpecifiedName(conceptId, locales);
				if (fullySpecifiedName != null) {
					constant.setFsn(fullySpecifiedName.getTerm());
				} else {
					constant.setFsn(conceptId);
				}
				
				resultBuilder.put(conceptEnum.name(), constant);
			} catch (ComponentNotFoundException e) {
				// ignore
			}
		}
		
		return resultBuilder.build();
	}
	
}
