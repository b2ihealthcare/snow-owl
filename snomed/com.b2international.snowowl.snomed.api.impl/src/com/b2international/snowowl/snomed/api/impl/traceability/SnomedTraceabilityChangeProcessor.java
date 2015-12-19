/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl.traceability;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.AbstractCDOChangeProcessor;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.SnomedBrowserDescriptionType;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipTarget;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationshipType;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * Change processor implementation that produces a log entry for committed transactions.
 */
public class SnomedTraceabilityChangeProcessor extends AbstractCDOChangeProcessor<SnomedIndexEntry, CDOObject> {

	private static final Logger LOGGER = LoggerFactory.getLogger("traceability");

	// Track primary terminology components and reference set members relevant to acceptability and inactivation 
	private static final Collection<EClass> TRACKED_ECLASSES = ImmutableSet.of(SnomedPackage.Literals.CONCEPT,
			SnomedPackage.Literals.DESCRIPTION,
			SnomedPackage.Literals.RELATIONSHIP,
			SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER,
			SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER,
			SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER);

	private static final Set<String> TRACKED_REFERENCE_SET_IDS = ImmutableSet.of(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
			Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			Concepts.REFSET_ALTERNATIVE_ASSOCIATION,
			Concepts.REFSET_MOVED_FROM_ASSOCIATION,
			Concepts.REFSET_MOVED_TO_ASSOCIATION,
			Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION,
			Concepts.REFSET_REFERS_TO_ASSOCIATION,
			Concepts.REFSET_REPLACED_BY_ASSOCIATION,
			Concepts.REFSET_SAME_AS_ASSOCIATION,
			Concepts.REFSET_SIMILAR_TO_ASSOCIATION,
			Concepts.REFSET_WAS_A_ASSOCIATION);

	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.descriptionConcept()
			.relationshipSource()
			.build();
	
	private static final ObjectWriter WRITER;

	private static final Set<EStructuralFeature> IGNORED_FEATURES = ImmutableSet.<EStructuralFeature>of(SnomedPackage.Literals.CONCEPT__DESCRIPTIONS,
			SnomedPackage.Literals.CONCEPT__INBOUND_RELATIONSHIPS,
			SnomedPackage.Literals.CONCEPT__OUTBOUND_RELATIONSHIPS);
	
	static {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);

		final ISO8601DateFormat df = new ISO8601DateFormat();
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(df);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		WRITER = objectMapper.writer();
	}

	private TraceabilityEntry entry;

	public SnomedTraceabilityChangeProcessor(final IIndexUpdater<SnomedIndexEntry> indexUpdater, final IBranchPath branchPath) {
		super(indexUpdater, branchPath, TRACKED_ECLASSES);
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		this.entry = new TraceabilityEntry(commitChangeSet);
		super.process(commitChangeSet);

		if (detachedComponents.isEmpty()) {
			return;
		}
		
		final Set<Long> detachedStorageKeys = newHashSetWithExpectedSize(detachedComponents.size());
		for (Entry<CDOID, EClass> entry : detachedComponents) {
			final long storageKey = CDOIDUtil.getLong(entry.getKey());
			final EClass eClass = entry.getValue();
			
			if (SnomedPackage.Literals.CONCEPT.equals(eClass) || SnomedPackage.Literals.DESCRIPTION.equals(eClass) || SnomedPackage.Literals.RELATIONSHIP.equals(eClass)) {
				detachedStorageKeys.add(storageKey);
			}
		}
		
		((IndexServerService<?>) indexService).executeReadTransaction(branchPath, new IndexRead<Void>() {
			@Override
			public Void execute(IndexSearcher index) throws IOException {
				
				final Query storageKeyQuery = SnomedMappings.newQuery()
						.and(SnomedMappings.newQuery().concept().description().relationship().matchAny())
						.and(new FilteredQuery(new MatchAllDocsQuery(), Mappings.storageKey().createTermsFilter(detachedStorageKeys)))
						.matchAll();
				
				final TopDocs topDocs = index.search(storageKeyQuery, null, detachedComponents.size(), Sort.INDEXORDER, false, false);
				if (IndexUtils.isEmpty(topDocs)) {
					return null;
				}
				
				for (int i = 0; i < topDocs.scoreDocs.length; i++) {
					final Document document = index.doc(topDocs.scoreDocs[i].doc, FIELDS_TO_LOAD);
					final String detachedComponentId = SnomedMappings.id().getValueAsString(document);
					
					String conceptId = document.get(SnomedMappings.relationshipSource().fieldName()); 
					if (conceptId != null) {
						entry.registerChange(conceptId, new TraceabilityChange(SnomedPackage.Literals.RELATIONSHIP, detachedComponentId, ChangeType.DELETE));
						continue;
					} 
					
					conceptId = document.get(SnomedMappings.descriptionConcept().fieldName());
					if (conceptId != null) {
						entry.registerChange(conceptId, new TraceabilityChange(SnomedPackage.Literals.DESCRIPTION, detachedComponentId, ChangeType.DELETE));
						continue;
					}
					
					entry.registerChange(detachedComponentId, new TraceabilityChange(SnomedPackage.Literals.CONCEPT, detachedComponentId, ChangeType.DELETE));
				}
				
				return null;
			}
		});
	}
	
	@Override
	protected void processAddition(CDOObject newComponent) {
		final EClass eClass = newComponent.eClass();

		if (SnomedPackage.Literals.CONCEPT.equals(eClass)) {
			final Concept newConcept = (Concept) newComponent;
			entry.registerChange(newConcept.getId(), new TraceabilityChange(eClass, newConcept.getId(), ChangeType.CREATE));
		} else if (SnomedPackage.Literals.DESCRIPTION.equals(eClass)) {
			final Description newDescription = (Description) newComponent;
			entry.registerChange(newDescription.getConcept().getId(), new TraceabilityChange(eClass, newDescription.getId(), ChangeType.CREATE));
		} else if (SnomedPackage.Literals.RELATIONSHIP.equals(eClass)) {
			final Relationship newRelationship = (Relationship) newComponent;
			entry.registerChange(newRelationship.getSource().getId(), new TraceabilityChange(eClass, newRelationship.getId(), ChangeType.CREATE));
		}
		
		// Reference set members are logged through their container core component change
	}
	
	@Override
	protected void processUpdate(CDOObject dirtyComponent) {
		final EClass eClass = dirtyComponent.eClass();
		final CDORevisionDelta revisionDelta = commitChangeSet.getRevisionDeltas().get(dirtyComponent.cdoID());

		if (SnomedPackage.Literals.CONCEPT.equals(eClass)) {
			final Concept dirtyConcept = (Concept) dirtyComponent;
			registerInactivationOrUpdate(eClass, dirtyConcept.getId(), dirtyConcept.getId(), revisionDelta);
		} else if (SnomedPackage.Literals.DESCRIPTION.equals(eClass)) {
			final Description dirtyDescription = (Description) dirtyComponent;
			registerInactivationOrUpdate(eClass, dirtyDescription.getConcept().getId(), dirtyDescription.getId(), revisionDelta);
		} else if (SnomedPackage.Literals.RELATIONSHIP.equals(eClass)) {
			final Relationship dirtyRelationship = (Relationship) dirtyComponent;
			registerInactivationOrUpdate(eClass, dirtyRelationship.getSource().getId(), dirtyRelationship.getId(), revisionDelta);
		} else if (SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER.equals(eClass)) {
			// An updated language reference set member is recorded as an update on the description
			final SnomedLanguageRefSetMember newMember = (SnomedLanguageRefSetMember) dirtyComponent;
			final Description description = new SnomedDescriptionLookupService().getComponent(newMember.getReferencedComponentId(), commitChangeSet.getView());
			entry.registerChange(description.getConcept().getId(), new TraceabilityChange(description.eClass(), description.getId(), ChangeType.UPDATE));
		} else if (SnomedRefSetPackage.Literals.SNOMED_ASSOCIATION_REF_SET_MEMBER.equals(eClass) || SnomedRefSetPackage.Literals.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER.equals(eClass)) {
			// An updated inactivation reason or association reference set member is recorded as an update on the referenced component
			final SnomedRefSetMember newMember = (SnomedRefSetMember) dirtyComponent;
			
			if (TRACKED_REFERENCE_SET_IDS.contains(newMember.getRefSetIdentifierId())) {
				final short referencedComponentType = newMember.getReferencedComponentType();
				final String referencedTerminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType);
				final Component referencedComponent = (Component) CoreTerminologyBroker.getInstance()
						.getLookupService(referencedTerminologyComponentId)
						.getComponent(newMember.getReferencedComponentId(), commitChangeSet.getView());
				
				final String conceptId = referencedComponent instanceof Description 
						? ((Description) referencedComponent).getConcept().getId() 
						: referencedComponent.getId();
						
				entry.registerChange(conceptId, new TraceabilityChange(referencedComponent.eClass(), referencedComponent.getId(), ChangeType.UPDATE));
			}
		}
	}

	private void registerInactivationOrUpdate(final EClass eClass, final String conceptId, final String componentId, CDORevisionDelta revisionDelta) {
		CDOSetFeatureDelta activeDelta = (CDOSetFeatureDelta) revisionDelta.getFeatureDelta(SnomedPackage.Literals.COMPONENT__ACTIVE);
		if (activeDelta != null && Boolean.FALSE.equals(activeDelta.getValue())) {
			entry.registerChange(conceptId, new TraceabilityChange(eClass, componentId, ChangeType.INACTIVATE));
		} else {
			for (CDOFeatureDelta featureDelta : revisionDelta.getFeatureDeltas()) {
				if (!IGNORED_FEATURES.contains(featureDelta.getFeature())) {
					entry.registerChange(conceptId, new TraceabilityChange(eClass, componentId, ChangeType.UPDATE));
				}
			}
		}
	}
	
	@Override
	public void afterCommit() {
		final Set<String> conceptIds = entry.getChanges().keySet();
		final String branch = commitChangeSet.getView().getBranch().getPathName();
		final IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
		
		final Request<ServiceProvider, SnomedConcepts> conceptSearchRequest = SnomedRequests.prepareSearchConcept()
			.setComponentIds(conceptIds)
			.setOffset(0)
			.setLimit(entry.getChanges().size())
			.setExpand("descriptions(),relationships(expand(destination()))")
			.build(branch);
		
		final SnomedConcepts concepts = conceptSearchRequest.executeSync(bus);
		final Set<String> hasChildrenStated = collectNonLeafs(conceptIds, branch, bus, Concepts.STATED_RELATIONSHIP);
		final Set<String> hasChildrenInferred = collectNonLeafs(conceptIds, branch, bus, Concepts.INFERRED_RELATIONSHIP);
		
		for (ISnomedConcept concept : concepts) {
			SnomedBrowserConcept convertedConcept = new SnomedBrowserConcept();
			
			convertedConcept.setActive(concept.isActive());
			convertedConcept.setConceptId(concept.getId());
			convertedConcept.setDefinitionStatus(concept.getDefinitionStatus());
			convertedConcept.setDescriptions(convertDescriptions(concept.getDescriptions()));
			convertedConcept.setEffectiveTime(concept.getEffectiveTime());
			convertedConcept.setModuleId(concept.getModuleId());
			convertedConcept.setRelationships(convertRelationships(concept.getRelationships()));
			convertedConcept.setIsLeafStated(!hasChildrenStated.contains(concept.getId()));
			convertedConcept.setIsLeafInferred(!hasChildrenInferred.contains(concept.getId()));
			convertedConcept.setFsn(concept.getId());
			
			// PT and SYN labels are not populated
			entry.setConcept(convertedConcept.getId(), convertedConcept);
		}
		
		try {
			LOGGER.info(WRITER.writeValueAsString(entry));
		} catch (IOException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
	
		super.afterCommit();
	}

	private Set<String> collectNonLeafs(final Set<String> conceptIds, final String branch, final IEventBus bus, String characteristicTypeId) {
		final Set<String> hasChildren = newHashSet();
		final Request<ServiceProvider, SnomedRelationships> hasChildrenSearchRequest = SnomedRequests.prepareSearchRelationship()
			.filterByDestination(conceptIds)
			.filterByActive(true)
			.filterByCharacteristicType(characteristicTypeId)
			.all()
			.build(branch);
		
		final SnomedRelationships relationships = hasChildrenSearchRequest.executeSync(bus);
		
		for (ISnomedRelationship relationship : relationships) {
			hasChildren.add(relationship.getDestinationId());
		}
		
		return hasChildren;
	}
	
	private List<ISnomedBrowserDescription> convertDescriptions(SnomedDescriptions descriptions) {
		return FluentIterable.from(descriptions).transform(new Function<ISnomedDescription, ISnomedBrowserDescription>() {
			@Override
			public ISnomedBrowserDescription apply(ISnomedDescription input) {
				final SnomedBrowserDescription convertedDescription = new SnomedBrowserDescription();
				
				convertedDescription.setAcceptabilityMap(input.getAcceptabilityMap());
				convertedDescription.setActive(input.isActive());
				convertedDescription.setCaseSignificance(input.getCaseSignificance());
				convertedDescription.setConceptId(input.getConceptId());
				convertedDescription.setDescriptionId(input.getId());
				convertedDescription.setEffectiveTime(input.getEffectiveTime());
				convertedDescription.setLang(input.getLanguageCode());
				convertedDescription.setModuleId(input.getModuleId());
				convertedDescription.setTerm(input.getTerm());
				convertedDescription.setType(SnomedBrowserDescriptionType.getByConceptId(input.getTypeId()));
				
				return convertedDescription;
			}
		}).toList();
	}

	private List<ISnomedBrowserRelationship> convertRelationships(SnomedRelationships relationships) {
		return FluentIterable.from(relationships).transform(new Function<ISnomedRelationship, ISnomedBrowserRelationship>() {
			@Override
			public ISnomedBrowserRelationship apply(ISnomedRelationship input) {
				final SnomedBrowserRelationship convertedRelationship = new SnomedBrowserRelationship();
				
				convertedRelationship.setActive(input.isActive());
				convertedRelationship.setCharacteristicType(input.getCharacteristicType());
				convertedRelationship.setEffectiveTime(input.getEffectiveTime());
				convertedRelationship.setGroupId(input.getGroup());
				convertedRelationship.setModifier(input.getModifier());
				convertedRelationship.setModuleId(input.getModuleId());
				convertedRelationship.setRelationshipId(input.getId());
				convertedRelationship.setSourceId(input.getSourceId());
				
				final SnomedBrowserRelationshipType type = new SnomedBrowserRelationshipType(input.getTypeId());
				type.setFsn(input.getTypeId());
				convertedRelationship.setType(type);
				
				final SnomedBrowserRelationshipTarget target = new SnomedBrowserRelationshipTarget();
				target.setActive(input.getDestinationConcept().isActive());
				target.setConceptId(input.getDestinationId());
				target.setDefinitionStatus(input.getDestinationConcept().getDefinitionStatus());
				target.setEffectiveTime(input.getDestinationConcept().getEffectiveTime());
				target.setFsn(input.getDestinationId());
				target.setModuleId(input.getDestinationConcept().getModuleId());
				convertedRelationship.setTarget(target);
				
				return convertedRelationship;
			}
		}).toList();
	}

	@Override
	protected void processDeletion(Entry<CDOID, EClass> detachedComponent) {
		// Handled separately
	}
	
	@Override
	public void reset() {
		entry = null;
		super.reset();
	}

	@Override
	public String getChangeDescription() {
		return String.format("Traceability logged for %d concept(s).", entry.getChanges().size());
	}
	
	@Override
	public String getName() {
		return "SNOMED CT Traceability";
	}
}
