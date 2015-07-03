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

import java.util.*;

import javax.annotation.Resource;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.impl.domain.*;

import com.b2international.snowowl.snomed.datastore.*;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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

	private SnomedConceptServiceImpl conceptService;
	private SnomedDescriptionServiceImpl descriptionService;

	@Resource
	public void setConceptService(final SnomedConceptServiceImpl conceptService) {
		this.conceptService = conceptService;
	}

	@Resource
	public void setDescriptionService(final SnomedDescriptionServiceImpl descriptionService) {
		this.descriptionService = descriptionService;
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
		final ISnomedDescription fullySpecifiedName = descriptionService.getFullySpecifiedName(iSnomedDescriptions, conceptRef, locales);
		final ISnomedDescription preferredSynonym = descriptionService.getPreferredTerm(iSnomedDescriptions, conceptRef, locales);

		final List<SnomedRelationshipIndexEntry> relationships = getStatementBrowser().getOutboundStatements(branchPath, concept);
		final int inferredDescendantCount = getTerminologyBrowser().getSubTypeCount(branchPath, concept);

		final SnomedBrowserConcept result = new SnomedBrowserConcept();

		result.setActive(concept.isActive());
		result.setConceptId(concept.getId());
		result.setDefinitionStatus(concept.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
		result.setEffectiveTime(new Date(concept.getEffectiveTimeAsLong()));
		result.setModuleId(concept.getModuleId());

		result.setIsLeafInferred(inferredDescendantCount < 1);

		result.setDescriptions(convertDescriptions(iSnomedDescriptions));
		result.setFsn(fullySpecifiedName.getTerm());
		result.setPreferredSynonym(preferredSynonym.getTerm());

		result.setRelationships(convertRelationships(relationships, conceptRef, locales));
		
		return result;
	}

	@Override
	public ISnomedBrowserConcept create(String branchPath, ISnomedBrowserConcept concept, String userId, List<Locale> locales) {
		final SnomedConceptInput snomedConceptInput = toConceptInput(branchPath, concept);
		String commitComment = getCommitComment(userId, concept, "creating");
		final ISnomedConcept iSnomedConcept = conceptService.create(snomedConceptInput, userId, commitComment);
		final IComponentRef componentRef = createComponentRef(branchPath, iSnomedConcept.getId());
		return getConceptDetails(componentRef, locales);
	}

	public ISnomedBrowserConcept update(String branchPath, ISnomedBrowserConcept newVersionConcept, String userId, ArrayList<Locale> locales) {
		final IComponentRef componentRef = createComponentRef(branchPath, newVersionConcept.getConceptId());
		final ISnomedBrowserConcept existingVersionConcept = getConceptDetails(componentRef, locales);

		final SnomedEditingContext editingContext = conceptService.createEditingContext(componentRef);
		String commitComment = getCommitComment(userId, newVersionConcept, "updating");

		// Gather updates
		final ISnomedConceptUpdate conceptUpdate = getConceptUpdate(existingVersionConcept, newVersionConcept);
		final List<ISnomedBrowserDescription> existingVersionDescriptions = existingVersionConcept.getDescriptions();
		final List<ISnomedBrowserDescription> newVersionDescriptions = newVersionConcept.getDescriptions();
		Set<String> descriptionDeletionIds = getDescriptionDeletions(existingVersionDescriptions, newVersionDescriptions);
		Map<String, ISnomedDescriptionUpdate> descriptionUpdates = getDescriptionUpdates(existingVersionDescriptions, newVersionDescriptions);
		List<SnomedDescriptionInput> descriptionInputs = getDescriptionCreations(branchPath, newVersionDescriptions);

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
		for (SnomedDescriptionInput descriptionInput : descriptionInputs) {
			descriptionInput.setConceptId(existingVersionConcept.getConceptId());
			descriptionService.convertAndRegister(descriptionInput, editingContext);
		}

		// TODO - Add MRCM checks here

		// Commit
		conceptService.doCommit(userId, commitComment, editingContext);

		return getConceptDetails(componentRef, locales);
	}

	private ISnomedConceptUpdate getConceptUpdate(ISnomedBrowserConcept existingVersion, ISnomedBrowserConcept newVersion) {

		boolean anyDifference = existingVersion.isActive() != newVersion.isActive()
				|| !existingVersion.getModuleId().equals(newVersion.getModuleId())
				|| !existingVersion.getDefinitionStatus().equals(newVersion.getDefinitionStatus());

		if (anyDifference) {
			final SnomedConceptUpdate snomedConceptUpdate = new SnomedConceptUpdate();
			snomedConceptUpdate.setModuleId(newVersion.getModuleId());
			snomedConceptUpdate.setDefinitionStatus(newVersion.getDefinitionStatus());
			snomedConceptUpdate.setActive(newVersion.isActive());
			return snomedConceptUpdate;
		} else {
			return null;
		}
	}

	private Set<String> getDescriptionDeletions(List<ISnomedBrowserDescription> existingVersion, List<ISnomedBrowserDescription> newVersion) {
		Set<String> existingIds = toIdSet(existingVersion);
		Set<String> newIds = toIdSet(newVersion);
		existingIds.removeAll(newIds);
		return existingIds;
	}

	private Set<String> toIdSet(Collection<ISnomedBrowserDescription> components) {
		Set<String> ids = new HashSet<String>();
		for (ISnomedBrowserDescription component : components) {
			ids.add(component.getDescriptionId());
		}
		return ids;
	}

	private Map<String, ISnomedDescriptionUpdate> getDescriptionUpdates(List<ISnomedBrowserDescription> existingVersions, List<ISnomedBrowserDescription> newVersions) {
		Map<String, ISnomedDescriptionUpdate> updateMap = new HashMap<String, ISnomedDescriptionUpdate>();
		for (ISnomedBrowserDescription existingDesc : existingVersions) {
			for (ISnomedBrowserDescription newVersionDesc : newVersions) {
				final String descriptionId = existingDesc.getDescriptionId();
				if (descriptionId.equals(newVersionDesc.getDescriptionId())) {
					final SnomedDescriptionUpdate update = new SnomedDescriptionUpdate();
					update.setActive(newVersionDesc.isActive());
					update.setModuleId(newVersionDesc.getModuleId());
					final Map<String, Acceptability> newAcceptabilityMap = newVersionDesc.getAcceptabilityMap();
					if (!existingDesc.getAcceptabilityMap().equals(newAcceptabilityMap)) {
						update.setAcceptability(newAcceptabilityMap);
					}
					update.setCaseSignificance(newVersionDesc.getCaseSignificance());
					updateMap.put(descriptionId, update);
				}
			}
		}
		return updateMap;
	}

	private List<SnomedDescriptionInput> getDescriptionCreations(String branchPath, List<ISnomedBrowserDescription> newVersionDescriptions) {
		List<SnomedDescriptionInput> inputs = new ArrayList<SnomedDescriptionInput>();
		for (ISnomedBrowserDescription description : newVersionDescriptions) {
			if (description.getDescriptionId() == null) {
				inputs.add(toDescriptionInput(branchPath, description));
			}
		}
		return inputs;
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

	private SnomedConceptInput toConceptInput(final String branchPath, ISnomedBrowserConcept concept) {
		final SnomedConceptInput conceptInput = new SnomedConceptInput();
		setCommonComponentProperties(branchPath, concept, conceptInput, ComponentCategory.CONCEPT);
		conceptInput.setIsAIdGenerationStrategy(conceptInput.getIdGenerationStrategy());

		// Find a parent relationship
		final String parentRelationshipId = getParentId(concept);
		conceptInput.setParentId(parentRelationshipId);

		final List<SnomedDescriptionInput> descriptionInputs = newArrayList();
		for (ISnomedBrowserDescription description : concept.getDescriptions()) {
			descriptionInputs.add(toDescriptionInput(branchPath, description));
		}

		conceptInput.setDescriptions(descriptionInputs);

		return conceptInput;
	}

	private void setCommonComponentProperties(String branchPath, ISnomedBrowserComponent component, AbstractSnomedComponentInput componentInput, ComponentCategory componentCategory) {
		componentInput.setBranchPath(branchPath);
		componentInput.setCodeSystemShortName(SNOMEDCT);
		final String moduleId = component.getModuleId();
		componentInput.setModuleId(moduleId != null ? moduleId : Concepts.MODULE_SCT_CORE);
		// Use default namespace
		final NamespaceIdGenerationStrategy idGenerationStrategy = new NamespaceIdGenerationStrategy(componentCategory, null);
		componentInput.setIdGenerationStrategy(idGenerationStrategy);
	}

	private SnomedDescriptionInput toDescriptionInput(String branchPath, ISnomedBrowserDescription description) {
		final SnomedDescriptionInput descriptionInput = new SnomedDescriptionInput();
		setCommonComponentProperties(branchPath, description, descriptionInput, ComponentCategory.DESCRIPTION);
		descriptionInput.setLanguageCode(description.getLang());
		descriptionInput.setTypeId(description.getType().getConceptId());
		descriptionInput.setTerm(description.getTerm());
		descriptionInput.setAcceptability(description.getAcceptabilityMap());
		return descriptionInput;
	}

	private String getParentId(ISnomedBrowserConcept concept) {
		ISnomedBrowserRelationship parentRelationship = null;
		for (ISnomedBrowserRelationship relationship : concept.getRelationships()) {
			final ISnomedBrowserRelationshipType type = relationship.getType();
			final String conceptId = type.getConceptId();
			if (SnomedConstants.Concepts.IS_A.equals(conceptId)) {
				parentRelationship = relationship;
			}
		}
		if (parentRelationship != null) {
			return parentRelationship.getTarget().getConceptId();
		} else {
			throw new BadRequestException("At least one isA relationship is required.");
		}
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
		final ImmutableList.Builder<ISnomedBrowserRelationship> convertedRelationshipBuilder = ImmutableList.builder();
		
		final List<String> destinationConceptIds = newArrayList();
		for (final SnomedRelationshipIndexEntry relationship : relationships) {
			destinationConceptIds.add(relationship.getValueId());
		}
		
		final IBranchPath branchPath = ((InternalComponentRef) sourceConceptRef).getBranch().branchPath();
		final Collection<SnomedConceptIndexEntry> destinationConcepts = getTerminologyBrowser().getConcepts(branchPath, destinationConceptIds);
		final Map<String, SnomedConceptIndexEntry> destinationConceptMap = Maps.uniqueIndex(destinationConcepts, new Function<SnomedConceptIndexEntry, String>() {
			@Override
			public String apply(final SnomedConceptIndexEntry input) {
				return input.getId();
			}
		});
		
		for (final SnomedRelationshipIndexEntry relationship : relationships) {
			final SnomedBrowserRelationship convertedRelationship = new SnomedBrowserRelationship();

			final SnomedBrowserRelationshipType type = new SnomedBrowserRelationshipType();
			type.setConceptId(relationship.getAttributeId());
			type.setFsn(descriptionService.getFullySpecifiedName(createConceptRef(sourceConceptRef, relationship.getAttributeId()), locales).getTerm());

			final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();
			final SnomedConceptIndexEntry destinationConcept = destinationConceptMap.get(relationship.getValueId());
			
			target.setActive(destinationConcept.isActive());
			target.setConceptId(destinationConcept.getId());
			target.setDefinitionStatus(destinationConcept.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
			target.setEffectiveTime(new Date(destinationConcept.getEffectiveTimeAsLong()));
			target.setModuleId(destinationConcept.getModuleId());
			
			final IComponentRef targetConceptRef = createConceptRef(sourceConceptRef, relationship.getValueId());
			target.setFsn(descriptionService.getFullySpecifiedName(targetConceptRef, locales).getTerm());

			convertedRelationship.setActive(relationship.isActive());
			convertedRelationship.setCharacteristicType(CharacteristicType.getByConceptId(relationship.getCharacteristicTypeId()));
			convertedRelationship.setEffectiveTime(new Date(relationship.getEffectiveTimeAsLong()));
			convertedRelationship.setGroupId(relationship.getGroup());
			convertedRelationship.setModifier(relationship.isUniversal() ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL);
			convertedRelationship.setModuleId(relationship.getModuleId());
			convertedRelationship.setSourceId(relationship.getObjectId());
			convertedRelationship.setTarget(target);
			convertedRelationship.setType(type);

			convertedRelationshipBuilder.add(convertedRelationship);
		}

		return convertedRelationshipBuilder.build();
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
