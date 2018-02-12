/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateRelationshipId;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
@SuppressWarnings("restriction")
public abstract class BaseChangeProcessorTest extends BaseRevisionIndexTest {

	// fixtures
	private final Map<String, Concept> conceptsById = newHashMap();
	private final Map<String, SnomedRefSet> refSetsById = newHashMap();
	
	private CDOView view = mock(CDOView.class);
	private Collection<CDOObject> newComponents = newHashSet();
	private Collection<CDOObject> dirtyComponents = newHashSet();
	private Map<CDOID, EClass> detachedComponents = newHashMap();
	private Map<CDOID, CDORevisionDelta> revisionDeltas = newHashMap();
	
	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(
				SnomedConceptDocument.class,
				SnomedDescriptionIndexEntry.class,
				SnomedRelationshipIndexEntry.class,
				SnomedRefSetMemberIndexEntry.class,
				SnomedConstraintDocument.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	protected final void registerExistingObject(CDOObject object) {
		when(view.getObject(eq(object.cdoID()))).thenReturn(object);
		when(view.getObject(eq(object.cdoID()), anyBoolean())).thenReturn(object);
	}
	
	protected final void registerNew(CDOObject object) {
		newComponents.add(object);
	}
	
	protected final void registerDirty(CDOObject object) {
		dirtyComponents.add(object);
	}
	
	protected final void registerDetached(CDOID storageKey, EClass type) {
		detachedComponents.put(storageKey, type);
	}
	
	protected final void registerSetRevisionDelta(CDOObject object, EStructuralFeature feature, Object oldValue, Object newValue) {
		final CDOSetFeatureDeltaImpl featureDelta = new CDOSetFeatureDeltaImpl(feature, 0, newValue, oldValue);
		getRevisionDelta(object).addFeatureDelta(featureDelta);
	}
	
	protected final void registerAddRevisionDelta(CDOObject object, EStructuralFeature feature, int index, Object value) {
		final CDOAddFeatureDeltaImpl featureDelta = new CDOAddFeatureDeltaImpl(feature, 0, value);
		getRevisionDelta(object).addFeatureDelta(featureDelta);
	}
	
	protected final void registerRemoveRevisionDelta(CDOObject object, EStructuralFeature feature, int index) {
		final CDORemoveFeatureDeltaImpl featureDelta = new CDORemoveFeatureDeltaImpl(feature, 0);
		getRevisionDelta(object).addFeatureDelta(featureDelta);
	}
	
	private final CDORevisionDeltaImpl getRevisionDelta(CDOObject object) {
		final CDOID storageKey = checkNotNull(object.cdoID());
		if (!revisionDeltas.containsKey(storageKey)) {
			final CDORevisionImpl revision = (CDORevisionImpl) CDORevisionFactory.DEFAULT.createRevision(object.eClass());
//			revision.setBranchPoint(branchPoint);
			revisionDeltas.put(storageKey, CDORevisionUtil.createDelta(revision));
		}
		return (CDORevisionDeltaImpl) revisionDeltas.get(storageKey);
	}
	
	protected final void process(final ChangeSetProcessor processor) {
		index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Void>() {
			@Override
			public Void execute(RevisionSearcher index) throws IOException {
				processor.process(createChangeSet(), index);
				return null;
			}
		});
	}

	protected final CDOCommitChangeSet createChangeSet() {
		return new CDOCommitChangeSet(view, "test", "test", newComponents, dirtyComponents, detachedComponents, revisionDeltas, 1L);
	}

	protected final CDOID nextStorageKeyAsCDOID() {
		return CDOIDUtil.createLong(nextStorageKey());
	}

	protected final void withCDOID(CDOObject description, long storageKey) {
		if (description instanceof InternalCDOObject) {
			final CDOID id = CDOIDUtil.createLong(storageKey);
			((InternalCDOObject) description).cdoInternalSetID(id);
		}
	}

	protected final SnomedRefSet getMappingRefSet(String id, short referencedComponentType) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedMappingRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSet.setReferencedComponentType(referencedComponentType);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);		
	}
	
	protected final SnomedRefSet getRegularRefSet(String id, short referencedComponentType) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedRegularRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSet.setReferencedComponentType(referencedComponentType);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	protected final SnomedRefSet getStructuralRefSet(String id) {
		if (!refSetsById.containsKey(id)) {
			final SnomedRefSet refSet = SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
			withCDOID(refSet, nextStorageKey());
			refSet.setIdentifierId(id);
			refSetsById.put(id, refSet);
		}
		return refSetsById.get(id);
	}
	
	protected final Concept getConcept(String id) {
		if (!conceptsById.containsKey(id)) {
			final Concept concept = SnomedFactory.eINSTANCE.createConcept();
			withCDOID(concept, nextStorageKey());
			concept.setId(id);
			conceptsById.put(id, concept);
		}
		return conceptsById.get(id);
	}
	
	protected final Concept module() {
		return getConcept(Concepts.MODULE_SCT_CORE);
	}

	protected final Relationship createRandomRelationship() {
		return createStatedRelationship(generateConceptId(), Concepts.IS_A, generateConceptId());
	}
	
	protected final Relationship createInferredRelationship(String sourceId, String typeId, String destinationId) {
		return createRelationship(sourceId, typeId, destinationId, Concepts.INFERRED_RELATIONSHIP);
	}
	
	protected final Relationship createStatedRelationship(String sourceId, String typeId, String destinationId) {
		return createRelationship(sourceId, typeId, destinationId, Concepts.STATED_RELATIONSHIP);
	}
	
	private final Relationship createRelationship(String sourceId, String typeId, String destinationId, String characteristicType) {
		final Relationship relationship = SnomedFactory.eINSTANCE.createRelationship();
		withCDOID(relationship, nextStorageKey());
		relationship.setId(generateRelationshipId());
		relationship.setActive(true);
		relationship.setGroup(0);
		relationship.setModifier(getConcept(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER));
		relationship.setModule(module());
		relationship.setType(getConcept(typeId));
		relationship.setSource(getConcept(sourceId));
		relationship.setDestination(getConcept(destinationId));
		relationship.setCharacteristicType(getConcept(characteristicType));
		return relationship;
	}
	
	protected SnomedLanguageRefSetMember createLangMember(final String descriptionId, final Acceptability acceptability, final String refSetId) {
		final SnomedLanguageRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedLanguageRefSetMember();
		member.setAcceptabilityId(acceptability.getConceptId());
		final SnomedRefSet refSet = getStructuralRefSet(refSetId);
		refSet.setType(SnomedRefSetType.LANGUAGE);
		return createMember(member, descriptionId, refSet);
	}
	
	protected SnomedRefSetMember createSimpleMember(final String referencedComponentId, final String refSetId) {
		final SnomedRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedRefSetMember();
		final SnomedRefSet refSet = getRegularRefSet(refSetId, SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(referencedComponentId));
		refSet.setType(SnomedRefSetType.SIMPLE);
		return createMember(member, referencedComponentId, refSet);
	}
	
	protected SnomedRefSetMember createSimpleMapMember(final String referencedComponentId, final String mapTarget, final String refSetId) {
		final SnomedSimpleMapRefSetMember member = SnomedRefSetFactory.eINSTANCE.createSnomedSimpleMapRefSetMember();
		member.setMapTargetComponentId(mapTarget);
		final SnomedRefSet refSet = getMappingRefSet(refSetId, SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(referencedComponentId));
		refSet.setType(SnomedRefSetType.SIMPLE_MAP);
		return createMember(member, referencedComponentId, refSet);
	}
	
	private <T extends SnomedRefSetMember> T createMember(final T member, final String referencedComponentId, final SnomedRefSet refSet) {
		withCDOID(member, nextStorageKey());
		member.setActive(true);
		member.setModuleId(Concepts.MODULE_SCT_CORE);
		member.setReferencedComponentId(referencedComponentId);
		member.setRefSet(refSet);
		member.setUuid(UUID.randomUUID().toString());
		return member;
	}
	
	protected Description createFsnWithTwoAcceptabilityMembers() {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US);
		final SnomedLanguageRefSetMember preferredMember = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		description.getLanguageRefSetMembers().add(preferredMember);
		return description;
	}
	
	protected Description createDescription(String typeId, String term) {
		return createDescription(generateConceptId(), typeId, term);
	}
	
	protected Description createDescription(String conceptId, String typeId, String term) {
		final Description description = SnomedFactory.eINSTANCE.createDescription();
		withCDOID(description, nextStorageKey());
		description.setActive(true);
		description.setCaseSignificance(getConcept(Concepts.ENTIRE_TERM_CASE_SENSITIVE));
		description.setConcept(getConcept(conceptId));
		description.setId(generateDescriptionId());
		description.setLanguageCode("en");
		description.setModule(module());
		description.setReleased(false);
		description.setTerm("Term");
		description.setType(getConcept(typeId));
		return description;
	}

}
