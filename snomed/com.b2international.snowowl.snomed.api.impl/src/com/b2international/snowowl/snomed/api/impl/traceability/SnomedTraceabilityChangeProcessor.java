/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkResponse;
import com.b2international.snowowl.core.events.metrics.MetricsThreadLocal;
import com.b2international.snowowl.core.events.metrics.Timer;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.DelegatingIndexCommitChangeSet;
import com.b2international.snowowl.datastore.index.ImmutableIndexCommitChangeSet;
import com.b2international.snowowl.datastore.index.IndexCommitChangeSet;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
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
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
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
public class SnomedTraceabilityChangeProcessor implements ICDOChangeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger("traceability");

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

	private static final ObjectWriter WRITER;

	private static final Set<EStructuralFeature> IGNORED_FEATURES = ImmutableSet.<EStructuralFeature>of(SnomedPackage.Literals.CONCEPT__DESCRIPTIONS,
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

	private final boolean collectSystemChanges;
	private final RevisionIndex index;
	private final IBranchPath branchPath;

	private ICDOCommitChangeSet commitChangeSet;

	public SnomedTraceabilityChangeProcessor(final RevisionIndex index, final IBranchPath branchPath, final boolean collectSystemChanges) {
		this.branchPath = branchPath;
		this.index = index;
		this.collectSystemChanges = collectSystemChanges;
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		final Timer traceabilityTimer = MetricsThreadLocal.get().timer("traceability");
		try {
			traceabilityTimer.start();
			this.commitChangeSet = commitChangeSet;
			this.entry = new TraceabilityEntry(commitChangeSet);
			
			// No other details required for "System user" commits
			if (isSystemCommit() && !collectSystemChanges) {
				return;
			}
			
			for (CDOObject newComponent : commitChangeSet.getNewComponents()) {
				processAddition(newComponent);
			}
			
			for (CDOObject dirtyComponent : commitChangeSet.getDirtyComponents()) {
				processUpdate(dirtyComponent);
			}
			
			final Set<Long> detachedConceptStorageKeys = newHashSet(CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedPackage.Literals.CONCEPT)));
			final Set<Long> detachedDescriptionStorageKeys = newHashSet(CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedPackage.Literals.DESCRIPTION)));
			final Set<Long> detachedRelationshipStorageKeys = newHashSet(CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedPackage.Literals.RELATIONSHIP)));
			
			index.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
				@Override
				public Void execute(RevisionSearcher searcher) throws IOException {
					
					for (SnomedConceptDocument detachedConcept : searcher.get(SnomedConceptDocument.class, detachedConceptStorageKeys)) {
						entry.registerChange(detachedConcept.getId(), new TraceabilityChange(SnomedPackage.Literals.CONCEPT, detachedConcept.getId(), ChangeType.DELETE));
					}
					
					for (SnomedDescriptionIndexEntry detachedDescription : searcher.get(SnomedDescriptionIndexEntry.class, detachedDescriptionStorageKeys)) {
						entry.registerChange(detachedDescription.getConceptId(), new TraceabilityChange(SnomedPackage.Literals.DESCRIPTION, detachedDescription.getId(), ChangeType.DELETE));
					}
					
					for (SnomedRelationshipIndexEntry detachedRelationship : searcher.get(SnomedRelationshipIndexEntry.class, detachedRelationshipStorageKeys)) {
						entry.registerChange(detachedRelationship.getSourceId(), new TraceabilityChange(SnomedPackage.Literals.RELATIONSHIP, detachedRelationship.getId(), ChangeType.DELETE));
					}
					
					return null;
				}
			});
		} finally {
			traceabilityTimer.stop();
		}
	}

	private boolean isSystemCommit() {
		return SpecialUserStore.SYSTEM_USER_NAME.equals(commitChangeSet.getUserId());
	}
	
	private void processAddition(CDOObject newComponent) {
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
	
	private void processUpdate(CDOObject dirtyComponent) {
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
		final Timer traceabilityTimer = MetricsThreadLocal.get().timer("traceability");
		try {
			traceabilityTimer.start();
			if (commitChangeSet != null) {
				final ImmutableSet<String> conceptIds = ImmutableSet.copyOf(entry.getChanges().keySet());
				final String branch = commitChangeSet.getView().getBranch().getPathName();
				final IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
				
				final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
					.filterByIds(conceptIds)
					.setOffset(0)
					.setLimit(entry.getChanges().size())
					.setExpand("descriptions(),relationships(expand(destination()))")
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.execute(bus)
					.getSync();
				
				final Set<String> hasChildrenStated = collectNonLeafs(conceptIds, branch, bus, Concepts.STATED_RELATIONSHIP);
				final Set<String> hasChildrenInferred = collectNonLeafs(conceptIds, branch, bus, Concepts.INFERRED_RELATIONSHIP);
				
				for (SnomedConcept concept : concepts) {
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
			}
		
			LOGGER.info(WRITER.writeValueAsString(entry));
		} catch (IOException e) {
			throw SnowowlRuntimeException.wrap(e);
		} finally {
			traceabilityTimer.stop();
		}
	}

	private Set<String> collectNonLeafs(final ImmutableSet<String> conceptIds, final String branch, final IEventBus bus, String characteristicTypeId) {
		final Set<String> hasChildren = newHashSet();
		final BulkRequestBuilder<BranchContext> hasChildrenBulkRequest = BulkRequest.create();
		
		for (String conceptId : conceptIds) {
			
			final RequestBuilder<BranchContext, SnomedRelationships> hasChildrenSearchRequest = SnomedRequests.prepareSearchRelationship()
					.filterByDestination(conceptId)
					.filterByActive(true)
					.filterByCharacteristicType(characteristicTypeId)
					.filterByType(Concepts.IS_A)
					.setLimit(0);
			
			hasChildrenBulkRequest.add(hasChildrenSearchRequest);
		}
		
		final BulkResponse relationshipResponses = RepositoryRequests.prepareBulkRead()
			.setBody(hasChildrenBulkRequest)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(bus)
			.getSync();

		final Iterator<SnomedRelationships> responseIterator = relationshipResponses.getResponses(SnomedRelationships.class).iterator();
		
		for (String conceptId : conceptIds) {
			final SnomedRelationships relationships = responseIterator.next();
			if (relationships.getTotal() > 0) {
				hasChildren.add(conceptId);
			}
		}
		
		return hasChildren;
	}
	
	private List<ISnomedBrowserDescription> convertDescriptions(SnomedDescriptions descriptions) {
		return FluentIterable.from(descriptions).transform(new Function<SnomedDescription, ISnomedBrowserDescription>() {
			@Override
			public ISnomedBrowserDescription apply(SnomedDescription input) {
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
		return FluentIterable.from(relationships).transform(new Function<SnomedRelationship, ISnomedBrowserRelationship>() {
			@Override
			public ISnomedBrowserRelationship apply(SnomedRelationship input) {
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
				target.setActive(input.getDestination().isActive());
				target.setConceptId(input.getDestinationId());
				target.setDefinitionStatus(input.getDestination().getDefinitionStatus());
				target.setEffectiveTime(input.getDestination().getEffectiveTime());
				target.setFsn(input.getDestinationId());
				target.setModuleId(input.getDestination().getModuleId());
				convertedRelationship.setTarget(target);
				
				return convertedRelationship;
			}
		}).toList();
	}

	@Override
	public String getName() {
		return "SNOMED CT Traceability";
	}

	@Override
	public IndexCommitChangeSet commit() throws SnowowlServiceException {
		return new DelegatingIndexCommitChangeSet(ImmutableIndexCommitChangeSet.builder().build()) {
			@Override
			public String getDescription() {
				return String.format("Traceability logged for %d concept(s).", entry.getChanges().size());
			}
		};
	}

	@Override
	public void rollback() throws SnowowlServiceException {
		this.entry = null;
		this.commitChangeSet = null;
	}

}
