/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.*;

import javax.annotation.Resource;

import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.impl.domain.*;

import com.b2international.snowowl.snomed.datastore.*;
import com.google.common.collect.*;
import org.apache.lucene.search.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.domain.IStorageRef;
import com.b2international.snowowl.api.impl.domain.ComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalStorageRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.*;
import com.b2international.snowowl.snomed.api.domain.browser.*;
import com.b2international.snowowl.snomed.api.impl.domain.browser.*;
import com.b2international.snowowl.snomed.datastore.index.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class SnomedBrowserService implements ISnomedBrowserService {

	private static final List<ConceptEnum> CONCEPT_ENUMS;
	public static final String SNOMEDCT = "SNOMEDCT";

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
	private SnomedConceptServiceImpl conceptService;

	@Resource
	private SnomedDescriptionServiceImpl descriptionService;

	@Resource
	private SnomedRelationshipServiceImpl relationshipService;

	public SnomedBrowserService() {
		inputFactory = new InputFactory();
	}

	@Override
	public ISnomedBrowserConcept getConceptDetails(final IComponentRef conceptRef, final List<Locale> locales) {

		final InternalComponentRef internalConceptRef = ClassUtils.checkAndCast(conceptRef, InternalComponentRef.class);
		internalConceptRef.checkStorageExists();
		
		final IBranchPath branchPath = internalConceptRef.getBranch().branchPath();
		final String conceptId = conceptRef.getComponentId();
		final SnomedConceptIndexEntry concept = getTerminologyBrowser().getConcept(branchPath, conceptId);
		
		if (null == concept) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, conceptId);
		}
		
		final List<ISnomedDescription> iSnomedDescriptions = descriptionService.readConceptDescriptions(conceptRef);

		final ISnomedDescription fullySpecifiedName = descriptionService.getFullySpecifiedName(conceptRef, locales);
		final ISnomedDescription preferredSynonym = descriptionService.getPreferredTerm(conceptRef, locales);
		
		final List<SnomedRelationshipIndexEntry> relationships = getStatementBrowser().getOutboundStatements(branchPath, concept);
		final int inferredDescendantCount = getTerminologyBrowser().getSubTypeCount(branchPath, concept);

		final SnomedBrowserConcept result = new SnomedBrowserConcept();

		result.setActive(concept.isActive());
		result.setConceptId(concept.getId());
		result.setDefinitionStatus(concept.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
		result.setEffectiveTime(new Date(concept.getEffectiveTimeAsLong()));
		result.setModuleId(concept.getModuleId());

		result.setIsLeafInferred(inferredDescendantCount < 1);

		result.setDescriptions(convertDescriptions(newArrayList(iSnomedDescriptions)));

		result.setFsn(fullySpecifiedName.getTerm());
		result.setPreferredSynonym(preferredSynonym.getTerm());

		result.setRelationships(convertRelationships(relationships, conceptRef, locales));
		
		return result;
	}

	@Override
	public ISnomedBrowserConcept create(String branchPath, ISnomedBrowserConcept concept, String userId, List<Locale> locales) {
		final ISnomedConceptInput snomedConceptInput = inputFactory.createComponentInput(branchPath, concept, ISnomedConceptInput.class);
		String commitComment = getCommitComment(userId, concept, "creating");
		final ISnomedConcept iSnomedConcept = conceptService.create(snomedConceptInput, userId, commitComment);
		final IComponentRef componentRef = createComponentRef(branchPath, iSnomedConcept.getId());
		return getConceptDetails(componentRef, locales);
	}

	public ISnomedBrowserConcept update(String branchPath, ISnomedBrowserConcept newVersionConcept, String userId, ArrayList<Locale> locales) {
		LOGGER.info("Update concept start {}", newVersionConcept.getFsn());
		final IComponentRef componentRef = createComponentRef(branchPath, newVersionConcept.getConceptId());
		final ISnomedBrowserConcept existingVersionConcept = getConceptDetails(componentRef, locales);

		final SnomedEditingContext editingContext = conceptService.createEditingContext(componentRef);
		String commitComment = getCommitComment(userId, newVersionConcept, "updating");

		// Concept update
		final ISnomedConceptUpdate conceptUpdate = inputFactory.createComponentUpdate(existingVersionConcept, newVersionConcept, ISnomedConceptUpdate.class);

		// Description updates
		final List<ISnomedBrowserDescription> existingVersionDescriptions = existingVersionConcept.getDescriptions();
		final List<ISnomedBrowserDescription> newVersionDescriptions = newVersionConcept.getDescriptions();
		Set<String> descriptionDeletionIds = inputFactory.getComponentDeletions(existingVersionDescriptions, newVersionDescriptions);
		Map<String, ISnomedDescriptionUpdate> descriptionUpdates = inputFactory.createComponentUpdates(existingVersionDescriptions, newVersionDescriptions, ISnomedDescriptionUpdate.class);
		List<ISnomedDescriptionInput> descriptionInputs = inputFactory.createComponentInputs(branchPath, newVersionDescriptions, ISnomedDescriptionInput.class);
		LOGGER.info("Got description changes +{} -{} m{}, {}", descriptionInputs.size(), descriptionDeletionIds.size(), descriptionUpdates.size(), newVersionConcept.getFsn());

		// Relationship updates
		final List<ISnomedBrowserRelationship> existingVersionRelationships = existingVersionConcept.getRelationships();
		final List<ISnomedBrowserRelationship> newVersionRelationships = newVersionConcept.getRelationships();
		Set<String> relationshipDeletionIds = inputFactory.getComponentDeletions(existingVersionRelationships, newVersionRelationships);
		Map<String, ISnomedRelationshipUpdate> relationshipUpdates = inputFactory.createComponentUpdates(existingVersionRelationships, newVersionRelationships, ISnomedRelationshipUpdate.class);
		List<ISnomedRelationshipInput> relationshipInputs = inputFactory.createComponentInputs(branchPath, newVersionRelationships, ISnomedRelationshipInput.class);
		LOGGER.info("Got relationship changes +{} -{} m{}, {}", relationshipInputs.size(), relationshipDeletionIds.size(), relationshipUpdates.size(), newVersionConcept.getFsn());

		// Add updates to editing context
		if (conceptUpdate != null) {
			conceptService.doUpdate(componentRef, conceptUpdate, editingContext);
		}

		for (String descriptionDeletionId : descriptionDeletionIds) {
			descriptionService.doDelete(createComponentRef(branchPath, descriptionDeletionId), editingContext);
		}
		for (String descriptionId : descriptionUpdates.keySet()) {
			descriptionService.doUpdate(createComponentRef(branchPath, descriptionId), descriptionUpdates.get(descriptionId), editingContext);
		}
		for (ISnomedDescriptionInput descriptionInput : descriptionInputs) {
			((SnomedDescriptionInput)descriptionInput).setConceptId(existingVersionConcept.getConceptId());
			descriptionService.convertAndRegister(descriptionInput, editingContext);
		}

		for (String relationshipDeletionId : relationshipDeletionIds) {
			relationshipService.doDelete(createComponentRef(branchPath, relationshipDeletionId), editingContext);
		}
		for (String relationshipId : relationshipUpdates.keySet()) {
			relationshipService.doUpdate(createComponentRef(branchPath, relationshipId), relationshipUpdates.get(relationshipId), editingContext);
		}
		for (ISnomedRelationshipInput relationshipInput : relationshipInputs) {
			relationshipService.convertAndRegister(relationshipInput, editingContext);
		}

		// TODO - Add MRCM checks here

		// Commit
		conceptService.doCommit(userId, commitComment, editingContext);
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

	private IComponentRef createComponentRef(final String branchPath, final String componentId) {
		final ComponentRef conceptRef = new ComponentRef(SNOMEDCT, branchPath, componentId);
		conceptRef.checkStorageExists();
		return conceptRef;
	}

	private List<ISnomedBrowserDescription> convertDescriptions(final List<ISnomedDescription> descriptions) {
		final ImmutableList.Builder<ISnomedBrowserDescription> convertedDescriptionBuilder = ImmutableList.builder();

		for (final ISnomedDescription description : descriptions) {
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

	private List<ISnomedBrowserRelationship> convertRelationships(final List<SnomedRelationshipIndexEntry> relationships, final IComponentRef sourceConceptRef, final List<Locale> locales) {
		final ImmutableMap.Builder<String, ISnomedBrowserRelationship> convertedRelationshipBuilder = ImmutableMap.builder();
		for (final SnomedRelationshipIndexEntry relationship : relationships) {
			final SnomedBrowserRelationship convertedRelationship = new SnomedBrowserRelationship();
			convertedRelationship.setActive(relationship.isActive());
			convertedRelationship.setCharacteristicType(CharacteristicType.getByConceptId(relationship.getCharacteristicTypeId()));
			convertedRelationship.setEffectiveTime(new Date(relationship.getEffectiveTimeAsLong()));
			convertedRelationship.setGroupId(relationship.getGroup());
			convertedRelationship.setModifier(relationship.isUniversal() ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL);
			convertedRelationship.setModuleId(relationship.getModuleId());
			convertedRelationship.setSourceId(relationship.getObjectId());
			convertedRelationshipBuilder.put(relationship.getId(), convertedRelationship);
		}

		final Map<String, ISnomedBrowserRelationship> convertedRelationships = convertedRelationshipBuilder.build();
		
		final List<SnomedBrowserRelationshipType> types = new FsnJoinerOperation<SnomedBrowserRelationshipType>(sourceConceptRef, locales, descriptionService) {
			@Override
			protected Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId) {
				final Set<String> typeIds = newHashSet();
				for (final SnomedRelationshipIndexEntry relationship : relationships) {
					typeIds.add(relationship.getAttributeId());
				}
				return getTerminologyBrowser().getConcepts(branchPath, typeIds);
			}

			@Override
			protected SnomedBrowserRelationshipType convertConceptEntry(SnomedConceptIndexEntry conceptEntry, Optional<String> optionalFsn) {
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
		
		final List<SnomedBrowserRelationshipTarget> targets = new FsnJoinerOperation<SnomedBrowserRelationshipTarget>(sourceConceptRef, locales, descriptionService) {
			@Override
			protected Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId) {
				final Set<String> destinationConceptIds = newHashSet();
				for (final SnomedRelationshipIndexEntry relationship : relationships) {
					destinationConceptIds.add(relationship.getValueId());
				}
				return getTerminologyBrowser().getConcepts(branchPath, destinationConceptIds);
			}

			@Override
			protected SnomedBrowserRelationshipTarget convertConceptEntry(SnomedConceptIndexEntry destinationConcept, Optional<String> optionalFsn) {
				final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();
				target.setActive(destinationConcept.isActive());
				target.setConceptId(destinationConcept.getId());
				target.setDefinitionStatus(destinationConcept.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
				target.setEffectiveTime(new Date(destinationConcept.getEffectiveTimeAsLong()));
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
		for (SnomedRelationshipIndexEntry entry : relationships) {
			SnomedBrowserRelationship rel = (SnomedBrowserRelationship) convertedRelationships.get(entry.getId());
			SnomedBrowserRelationshipType type = typesById.get(entry.getAttributeId());
			SnomedBrowserRelationshipTarget target = targetsById.get(entry.getValueId());
			rel.setType(type);
			rel.setTarget(target);
		}
		return ImmutableList.copyOf(convertedRelationships.values());
	}

	@Override
	public List<ISnomedBrowserParentConcept> getConceptParents(IComponentRef conceptRef, List<Locale> locales) {

		return new FsnJoinerOperation<ISnomedBrowserParentConcept>(conceptRef, locales, descriptionService) {
			
			@Override
			protected Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId) {
				return getTerminologyBrowser().getSuperTypesById(branchPath, conceptId);
			}

			@Override
			protected ISnomedBrowserParentConcept convertConceptEntry(SnomedConceptIndexEntry conceptEntry, Optional<String> optionalFsn) {
				final String childConceptId = conceptEntry.getId();
				final SnomedBrowserParentConcept convertedConcept = new SnomedBrowserParentConcept(); 

				convertedConcept.setConceptId(childConceptId);
				convertedConcept.setDefinitionStatus(conceptEntry.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
				convertedConcept.setFsn(optionalFsn.or(childConceptId));

				return convertedConcept;
			}
			
		}.run();
	}
	
	@Override
	public List<ISnomedBrowserChildConcept> getConceptChildren(final IComponentRef conceptRef, final List<Locale> locales) {
		
		return new FsnJoinerOperation<ISnomedBrowserChildConcept>(conceptRef, locales, descriptionService) {
			
			@Override
			protected Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId) {
				return getTerminologyBrowser().getSubTypesById(branchPath, conceptId);
			}

			@Override
			protected ISnomedBrowserChildConcept convertConceptEntry(SnomedConceptIndexEntry conceptEntry, Optional<String> optionalFsn) {
				final String childConceptId = conceptEntry.getId();
				final SnomedBrowserChildConcept convertedConcept = new SnomedBrowserChildConcept(); 

				convertedConcept.setConceptId(childConceptId);
				convertedConcept.setActive(conceptEntry.isActive());
				convertedConcept.setDefinitionStatus(conceptEntry.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
				convertedConcept.setModuleId(conceptEntry.getModuleId());
				convertedConcept.setFsn(optionalFsn.or(childConceptId));

				final int subTypeCount = getTerminologyBrowser().getSubTypeCountById(branchPath, childConceptId);
				convertedConcept.setHasChild(subTypeCount > 0);

				return convertedConcept;
			}
			
		}.run();
	}

	private static SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private static SnomedStatementBrowser getStatementBrowser() {
		return ApplicationContext.getServiceForClass(SnomedStatementBrowser.class);
	}

	private IComponentRef createConceptRef(final IStorageRef sourceRef, final String newComponentId) {
		return new ComponentRef(sourceRef, newComponentId);
	}

	@Override
	public List<ISnomedBrowserDescriptionResult> getDescriptions(final IStorageRef storageRef, final String query, final List<Locale> locales, final int offset, final int limit) {
		checkNotNull(storageRef, "Storage reference may not be null.");
		checkNotNull(query, "Query may not be null.");
		checkArgument(query.length() >= 3, "Query must be at least 3 characters long.");

		final InternalStorageRef internalStorageRef = ClassUtils.checkAndCast(storageRef, InternalStorageRef.class);
		internalStorageRef.checkStorageExists();

		final IBranchPath branchPath = internalStorageRef.getBranch().branchPath();
		final SnomedDescriptionReducedQueryAdapter descriptionQueryAdapter = new SnomedDescriptionReducedQueryAdapter(query, SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_TERM) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Sort createSort() {
				return Sort.RELEVANCE;
			}
		};
		
		final Collection<SnomedDescriptionIndexEntry> descriptionIndexEntries = getIndexService().search(branchPath, descriptionQueryAdapter, offset, limit);

		final SnomedConceptFullQueryAdapter conceptQueryAdapter = new SnomedConceptFullQueryAdapter(query, 
				SnomedConceptFullQueryAdapter.SEARCH_BY_FSN | SnomedConceptFullQueryAdapter.SEARCH_BY_SYNONYM);
		
		final Collection<SnomedConceptIndexEntry> conceptIndexEntries = getIndexService().searchUnsorted(branchPath, conceptQueryAdapter);
		final Map<String, SnomedConceptIndexEntry> conceptMap = Maps.uniqueIndex(conceptIndexEntries, new Function<SnomedConceptIndexEntry, String>() {
			@Override
			public String apply(final SnomedConceptIndexEntry input) {
				return input.getId();
			}
		});
		
		final Map<String, SnomedBrowserDescriptionResultDetails> detailCache = newHashMap();
		final ImmutableList.Builder<ISnomedBrowserDescriptionResult> resultBuilder = ImmutableList.builder();
		
		for (final SnomedDescriptionIndexEntry descriptionIndexEntry : descriptionIndexEntries) {
			
			final String typeId = descriptionIndexEntry.getType();
			if (!Concepts.FULLY_SPECIFIED_NAME.equals(typeId) && !Concepts.SYNONYM.equals(typeId)) {
				continue;
			}
			
			final SnomedBrowserDescriptionResult descriptionResult = new SnomedBrowserDescriptionResult();

			descriptionResult.setActive(descriptionIndexEntry.isActive());
			descriptionResult.setTerm(descriptionIndexEntry.getLabel());

			final SnomedConceptIndexEntry conceptIndexEntry = conceptMap.get(descriptionIndexEntry.getConceptId());
			final SnomedBrowserDescriptionResultDetails details;
			
			if (detailCache.containsKey(descriptionIndexEntry.getConceptId())) {
				details = detailCache.get(descriptionIndexEntry.getConceptId());
			} else {
				details = new SnomedBrowserDescriptionResultDetails();

				final IComponentRef conceptRef = createConceptRef(storageRef, descriptionIndexEntry.getConceptId());
				details.setFsn(descriptionService.getFullySpecifiedName(conceptRef, locales).getTerm());
				
				if (conceptIndexEntry != null) {
	
					details.setActive(conceptIndexEntry.isActive());
					details.setConceptId(conceptIndexEntry.getId());
					details.setDefinitionStatus(conceptIndexEntry.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
					details.setModuleId(conceptIndexEntry.getModuleId());
	
				} else {
					LOGGER.warn("Concept {} not found via text search, skipping.", descriptionIndexEntry.getConceptId());
					continue;
				}
				
				detailCache.put(descriptionIndexEntry.getConceptId(), details);
			}

			descriptionResult.setConcept(details);
			resultBuilder.add(descriptionResult);
		}

		return resultBuilder.build();
	}

	@Override
	public Map<String, ISnomedBrowserConstant> getConstants(final IStorageRef storageRef, final List<Locale> locales) {
		checkNotNull(storageRef, "Storage reference may not be null.");
		
		final InternalStorageRef internalStorageRef = ClassUtils.checkAndCast(storageRef, InternalStorageRef.class);
		internalStorageRef.checkStorageExists();

		final ImmutableMap.Builder<String, ISnomedBrowserConstant> resultBuilder = ImmutableMap.builder();
		
		for (final ConceptEnum conceptEnum : CONCEPT_ENUMS) {
			final String conceptId = conceptEnum.getConceptId();
			final IComponentRef conceptRef = createConceptRef(storageRef, conceptId);
			
			if (!conceptService.componentExists(conceptRef)) {
				continue;
			}
			
			final SnomedBrowserConstant constant = new SnomedBrowserConstant();
			
			constant.setConceptId(conceptId);
			constant.setFsn(descriptionService.getFullySpecifiedName(conceptRef, locales).getTerm());
			resultBuilder.put(conceptEnum.name(), constant);
		}
		
		return resultBuilder.build();
	}
	
	private static SnomedIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

}
