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
package com.b2international.snowowl.snomed.api.impl.browser;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.api.domain.ComponentCategory;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.domain.IStorageRef;
import com.b2international.snowowl.api.exception.ComponentNotFoundException;
import com.b2international.snowowl.api.impl.domain.ComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalComponentRef;
import com.b2international.snowowl.api.impl.domain.InternalStorageRef;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.domain.CharacteristicType;
import com.b2international.snowowl.snomed.api.domain.ConceptEnum;
import com.b2international.snowowl.snomed.api.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.api.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.api.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.SnomedBrowserDescriptionType;
import com.b2international.snowowl.snomed.api.impl.SnomedConceptServiceImpl;
import com.b2international.snowowl.snomed.api.impl.SnomedDescriptionServiceImpl;
import com.b2international.snowowl.snomed.api.impl.SnomedStatementBrowserServiceImpl;
import com.b2international.snowowl.snomed.api.impl.SnomedTerminologyBrowserServiceImpl;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescriptionResultDetails;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptFullQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SnomedBrowserService implements ISnomedBrowserService {

	private static final int MAX_COMPONENTS = 1_000_000;
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

	private SnomedConceptServiceImpl conceptService;
	private SnomedDescriptionServiceImpl descriptionService;
	private SnomedStatementBrowserServiceImpl statementBrowserService;
	private SnomedTerminologyBrowserServiceImpl terminologyBrowserService;

	@Resource
	public void setConceptService(final SnomedConceptServiceImpl conceptService) {
		this.conceptService = conceptService;
	}

	@Resource
	public void setDescriptionService(final SnomedDescriptionServiceImpl descriptionService) {
		this.descriptionService = descriptionService;
	}

	@Resource
	public void setStatementBrowserService(final SnomedStatementBrowserServiceImpl statementBrowserService) {
		this.statementBrowserService = statementBrowserService;
	}

	@Resource
	public void setTerminologyBrowserService(final SnomedTerminologyBrowserServiceImpl terminologyBrowserService) {
		this.terminologyBrowserService = terminologyBrowserService;
	}

	@Override
	public ISnomedBrowserConcept getConceptDetails(final IComponentRef conceptRef, final List<Locale> locales) {

		final ISnomedConcept concept = conceptService.read(conceptRef);

		final List<ISnomedDescription> descriptions = descriptionService.readConceptDescriptions(conceptRef);
		final ISnomedDescription fullySpecifiedName = descriptionService.getFullySpecifiedName(conceptRef, locales);
		final ISnomedDescription preferredSynonym = descriptionService.getPreferredTerm(conceptRef, locales);
		final IComponentList<ISnomedRelationship> relationships = statementBrowserService.getOutboundEdges(conceptRef, 0, MAX_COMPONENTS);
		final IComponentList<ISnomedConcept> inferredDescendants = terminologyBrowserService.getDescendants(conceptRef, true, 0, 1);

		final SnomedBrowserConcept result = new SnomedBrowserConcept();

		result.setActive(concept.isActive());
		result.setConceptId(concept.getId());
		result.setDefinitionStatus(concept.getDefinitionStatus());
		result.setEffectiveTime(concept.getEffectiveTime());
		result.setModuleId(concept.getModuleId());

		result.setLeafInferred(inferredDescendants.getTotalMembers() < 1);

		result.setDescriptions(convertDescriptions(descriptions));
		result.setFsn(fullySpecifiedName.getTerm());
		result.setPreferredSynonym(preferredSynonym.getTerm());

		result.setRelationships(convertRelationships(relationships, conceptRef, locales));

		return result;
	}

	private List<ISnomedBrowserDescription> convertDescriptions(final List<ISnomedDescription> descriptions) {
		final ImmutableList.Builder<ISnomedBrowserDescription> convertedDescriptionBuilder = ImmutableList.builder();

		for (final ISnomedDescription description : descriptions) {
			final SnomedBrowserDescription convertedDescription = new SnomedBrowserDescription();

			final SnomedBrowserDescriptionType descriptionType = convertDescriptionType(description.getTypeId());
			if (null == descriptionType) {
				LOGGER.warn("Unsupported description type ID {} on description {}, ignoring.", description.getTypeId(), description.getId());
				continue;
			}

			convertedDescription.setActive(description.isActive());
			convertedDescription.setCaseSignificance(description.getCaseSignificance());
			convertedDescription.setConceptId(description.getConceptId());
			convertedDescription.setDescriptionId(description.getId());
			convertedDescription.setEffectiveTime(description.getEffectiveTime());
			convertedDescription.setLang(description.getLanguageCode());
			convertedDescription.setModuleId(description.getModuleId());
			convertedDescription.setTerm(description.getTerm());
			convertedDescription.setType(descriptionType);

			convertedDescriptionBuilder.add(convertedDescription);
		}

		return convertedDescriptionBuilder.build();
	}

	private SnomedBrowserDescriptionType convertDescriptionType(final String typeId) {
		return SnomedBrowserDescriptionType.getByConceptId(typeId);
	}

	private List<ISnomedBrowserRelationship> convertRelationships(final IComponentList<ISnomedRelationship> relationships, final IComponentRef sourceConceptRef, final List<Locale> locales) {
		final ImmutableList.Builder<ISnomedBrowserRelationship> convertedRelationshipBuilder = ImmutableList.builder();

		for (final ISnomedRelationship relationship : relationships.getMembers()) {
			final SnomedBrowserRelationship convertedRelationship = new SnomedBrowserRelationship();

			final SnomedBrowserRelationshipType type = new SnomedBrowserRelationshipType();
			type.setConceptId(relationship.getTypeId());
			type.setFsn(descriptionService.getFullySpecifiedName(createConceptRef(sourceConceptRef, relationship.getTypeId()), locales).getTerm());

			final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();
			final IComponentRef targetConceptRef = createConceptRef(sourceConceptRef, relationship.getDestinationId());
			final ISnomedConcept targetConcept = conceptService.read(targetConceptRef);

			target.setActive(targetConcept.isActive());
			target.setConceptId(targetConcept.getId());
			target.setDefinitionStatus(targetConcept.getDefinitionStatus());
			target.setEffectiveTime(targetConcept.getEffectiveTime());
			target.setFsn(descriptionService.getFullySpecifiedName(targetConceptRef, locales).getTerm());
			target.setModuleId(targetConcept.getModuleId());

			convertedRelationship.setActive(relationship.isActive());
			convertedRelationship.setCharacteristicType(relationship.getCharacteristicType());
			convertedRelationship.setEffectiveTime(relationship.getEffectiveTime());
			convertedRelationship.setGroupId(relationship.getGroup());
			convertedRelationship.setModifier(relationship.getModifier());
			convertedRelationship.setModuleId(relationship.getModuleId());
			convertedRelationship.setSourceId(relationship.getSourceId());
			convertedRelationship.setTarget(target);
			convertedRelationship.setType(type);

			convertedRelationshipBuilder.add(convertedRelationship);
		}

		return convertedRelationshipBuilder.build();
	}

	@Override
	public List<ISnomedBrowserChildConcept> getConceptChildren(final IComponentRef conceptRef, final List<Locale> locales) {
		
		if (!conceptService.componentExists(conceptRef)) {
			throw new ComponentNotFoundException(ComponentCategory.CONCEPT, conceptRef.getComponentId());
		}
		
		final IBranchPath branchPath = ((InternalComponentRef) conceptRef).getBranchPath();
		final Collection<SnomedRelationshipIndexEntry> inboundRelationships = getStatementBrowser().getActiveInboundStatementsById(branchPath, conceptRef.getComponentId());
		
		final ImmutableList.Builder<ISnomedBrowserChildConcept> resultBuilder = ImmutableList.builder();

		for (final SnomedRelationshipIndexEntry inboundRelationship : inboundRelationships) {
			
			if (!Concepts.IS_A.equals(inboundRelationship.getAttributeId())) {
				continue;
			}

			if (!inboundRelationship.isActive()) {
				continue;
			}

			final SnomedBrowserChildConcept convertedDescendant = new SnomedBrowserChildConcept(); 
			final String descendantId = inboundRelationship.getObjectId();
			
			final SnomedConceptIndexEntry descendant = getTerminologyBrowser().getConcept(branchPath, descendantId);

			convertedDescendant.setActive(descendant.isActive());
			convertedDescendant.setDefinitionStatus(descendant.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
			convertedDescendant.setModuleId(descendant.getModuleId());
			
			final IComponentRef descendantRef = createConceptRef(conceptRef, descendantId);
			convertedDescendant.setFsn(descriptionService.getFullySpecifiedName(descendantRef, locales).getTerm());

			convertedDescendant.setCharacteristicType(CharacteristicType.getByConceptId(inboundRelationship.getCharacteristicTypeId()));
			convertedDescendant.setConceptId(descendantId);

			final int subTypeCount = getTerminologyBrowser().getSubTypeCountById(branchPath, descendantId);
			convertedDescendant.setHasChild(subTypeCount > 0);

			resultBuilder.add(convertedDescendant);
		}

		return resultBuilder.build();
	}

	private static SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private static SnomedStatementBrowser getStatementBrowser() {
		return ApplicationContext.getServiceForClass(SnomedStatementBrowser.class);
	}

	private IComponentRef createConceptRef(final IStorageRef sourceRef, final String newComponentId) {
		final ComponentRef conceptRef = new ComponentRef();

		conceptRef.setShortName(sourceRef.getShortName());
		conceptRef.setVersion(sourceRef.getVersion());
		conceptRef.setTaskId(sourceRef.getTaskId());
		conceptRef.setComponentId(newComponentId);

		return conceptRef;
	}

	@Override
	public List<ISnomedBrowserDescriptionResult> getDescriptions(final IStorageRef storageRef, final String query, final List<Locale> locales) {
		checkNotNull(storageRef, "Storage reference may not be null.");
		checkNotNull(query, "Query may not be null.");
		checkArgument(query.length() >= 3, "Query must be at least 3 characters long.");

		final InternalStorageRef internalStorageRef = ClassUtils.checkAndCast(storageRef, InternalStorageRef.class);
		internalStorageRef.checkStorageExists();

		final SnomedDescriptionReducedQueryAdapter descriptionQueryAdapter = new SnomedDescriptionReducedQueryAdapter(query, SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_TERM);
		final Collection<SnomedDescriptionIndexEntry> descriptionIndexEntries = getIndexService().searchUnsorted(internalStorageRef.getBranchPath(), descriptionQueryAdapter);

		final SnomedConceptFullQueryAdapter conceptQueryAdapter = new SnomedConceptFullQueryAdapter(query, 
				SnomedConceptFullQueryAdapter.SEARCH_BY_FSN 
				| SnomedConceptFullQueryAdapter.SEARCH_BY_LABEL 
				| SnomedConceptFullQueryAdapter.SEARCH_BY_SYNONYM 
				| SnomedConceptFullQueryAdapter.SEARCH_BY_OTHER); 
		final Collection<SnomedConceptIndexEntry> conceptIndexEntries = getIndexService().searchUnsorted(internalStorageRef.getBranchPath(), conceptQueryAdapter);
		final Map<String, SnomedConceptIndexEntry> conceptMap = Maps.uniqueIndex(conceptIndexEntries, new Function<SnomedConceptIndexEntry, String>() {
			@Override
			public String apply(final SnomedConceptIndexEntry input) {
				return input.getId();
			}
		});

		final ImmutableList.Builder<ISnomedBrowserDescriptionResult> resultBuilder = ImmutableList.builder();
		for (final SnomedDescriptionIndexEntry descriptionIndexEntry : descriptionIndexEntries) {
			final SnomedBrowserDescriptionResult descriptionResult = new SnomedBrowserDescriptionResult();

			descriptionResult.setActive(descriptionIndexEntry.isActive());
			descriptionResult.setTerm(descriptionIndexEntry.getLabel());

			final SnomedConceptIndexEntry conceptIndexEntry = conceptMap.get(descriptionIndexEntry.getConceptId());
			final SnomedBrowserDescriptionResultDetails details = new SnomedBrowserDescriptionResultDetails();

			final IComponentRef conceptRef = createConceptRef(storageRef, descriptionIndexEntry.getConceptId());
			details.setFsn(descriptionService.getFullySpecifiedName(conceptRef, locales).getTerm());

			if (conceptIndexEntry != null) {

				details.setActive(conceptIndexEntry.isActive());
				details.setConceptId(conceptIndexEntry.getId());
				details.setDefinitionStatus(conceptIndexEntry.isPrimitive() ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED);
				details.setModuleId(conceptIndexEntry.getModuleId());

			} else {

				final ISnomedConcept concept = conceptService.read(conceptRef);

				details.setActive(concept.isActive());
				details.setConceptId(concept.getId());
				details.setDefinitionStatus(concept.getDefinitionStatus());
				details.setModuleId(concept.getModuleId());
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
