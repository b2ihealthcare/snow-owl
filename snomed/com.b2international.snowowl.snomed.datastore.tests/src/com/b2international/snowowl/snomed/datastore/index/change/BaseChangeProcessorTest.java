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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateRelationshipId;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.index.ChangeSetProcessor;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public abstract class BaseChangeProcessorTest extends BaseRevisionIndexTest {

	protected final long ROOT_CONCEPTL = Long.parseLong(Concepts.ROOT_CONCEPT);
	
	private StagingArea staging;
	
	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(
			SnomedConceptDocument.class,
			SnomedDescriptionIndexEntry.class,
			SnomedRelationshipIndexEntry.class,
			SnomedRefSetMemberIndexEntry.class
//			SnomedConstraintDocument.class
		);
	}
	
	@Override
	public void setup() {
		super.setup();
		staging = index().prepareCommit(MAIN);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	protected final StagingArea staging() {
		return staging;
	}
	
	protected final void stageNew(Revision revision) {
		staging.stageNew(revision);
	}
	
	protected final void stageChange(Revision oldRevision, Revision newRevision) {
		staging.stageChange(oldRevision, newRevision);
	}
	
	protected final void stageRemove(Revision revision) {
		staging.stageRemove(revision);
	}
	
	protected final void initRevisions(Revision...revisions) {
		final StagingArea init = index().prepareCommit(MAIN);
		Arrays.asList(revisions).forEach(init::stageNew);
		init.commit(currentTime(), "test", "Test Fixture Initialization");
	}
	
//	protected final void registerSetRevisionDelta(CDOObject object, EStructuralFeature feature, Object oldValue, Object newValue) {
//		final CDOSetFeatureDeltaImpl featureDelta = new CDOSetFeatureDeltaImpl(feature, 0, newValue, oldValue);
//		getRevisionDelta(object).addFeatureDelta(featureDelta);
//	}
//	
//	protected final void registerAddRevisionDelta(CDOObject object, EStructuralFeature feature, int index, Object value) {
//		final CDOAddFeatureDeltaImpl featureDelta = new CDOAddFeatureDeltaImpl(feature, index, value);
//		getRevisionDelta(object).addFeatureDelta(featureDelta);
//	}
//	
//	protected final void registerRemoveRevisionDelta(CDOObject object, EStructuralFeature feature, int index) {
//		final CDORemoveFeatureDeltaImpl featureDelta = new CDORemoveFeatureDeltaImpl(feature, index);
//		getRevisionDelta(object).addFeatureDelta(featureDelta);
//	}
	
//	private final CDORevisionDeltaImpl getRevisionDelta(CDOObject object) {
//		final CDOID storageKey = checkNotNull(object.cdoID());
//		if (!revisionDeltas.containsKey(storageKey)) {
//			final CDORevisionImpl revision = (CDORevisionImpl) CDORevisionFactory.DEFAULT.createRevision(object.eClass());
////			revision.setBranchPoint(branchPoint);
//			revisionDeltas.put(storageKey, CDORevisionUtil.createDelta(revision));
//		}
//		return (CDORevisionDeltaImpl) revisionDeltas.get(storageKey);
//	}
	
	protected final void process(final ChangeSetProcessor processor) {
		index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Void>() {
			@Override
			public Void execute(RevisionSearcher index) throws IOException {
				processor.process(staging(), index);
				return null;
			}
		});
	}

//	protected final SnomedConceptDocument getRefSet(String conceptId, SnomedRefSetType refSetType, short referencedComponentType) {
//		if (!conceptsById.containsKey(conceptId)) {
//			final SnomedConceptDocument refSet = SnomedConceptDocument.builder()
//					.id(conceptId)
//					.refSetType(refSetType)
//					.referencedComponentType(referencedComponentType)
//					.build();
//			conceptsById.put(conceptId, refSet);
//		}
//		return conceptsById.get(conceptId);		
//	}
//	
//	protected final SnomedConceptDocument getConcept(String conceptId) {
//		if (!conceptsById.containsKey(conceptId)) {
//			final SnomedConceptDocument concept = SnomedConceptDocument.builder()
//					.id(conceptId)
//					.build();
//			conceptsById.put(conceptId, concept);
//		}
//		return conceptsById.get(conceptId);
//	}
	
	protected final String module() {
		return Concepts.MODULE_SCT_CORE;
	}

	protected final SnomedRelationshipIndexEntry createRandomRelationship() {
		return createStatedRelationship(generateConceptId(), Concepts.IS_A, generateConceptId());
	}
	
	protected final SnomedRelationshipIndexEntry createInferredRelationship(String sourceId, String typeId, String destinationId) {
		return createRelationship(sourceId, typeId, destinationId, Concepts.INFERRED_RELATIONSHIP);
	}
	
	protected final SnomedRelationshipIndexEntry createStatedRelationship(String sourceId, String typeId, String destinationId) {
		return createRelationship(sourceId, typeId, destinationId, Concepts.STATED_RELATIONSHIP);
	}
	
	private final SnomedRelationshipIndexEntry createRelationship(String sourceId, String typeId, String destinationId, String characteristicTypeId) {
		return SnomedRelationshipIndexEntry.builder()
				.id(generateRelationshipId())
				.active(true)
				.group(0)
				.unionGroup(0)
				.modifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER)
				.moduleId(module())
				.typeId(typeId)
				.sourceId(sourceId)
				.destinationId(destinationId)
				.characteristicTypeId(characteristicTypeId)
				.build();
	}
	
	protected final SnomedRefSetMemberIndexEntry langMember(final String descriptionId, final Acceptability acceptability, final String refSetId) {
		final SnomedRefSetMemberIndexEntry.Builder member = SnomedRefSetMemberIndexEntry.builder()
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.referencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
				.field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, acceptability.getConceptId());
		return createMember(member, descriptionId, refSetId);
	}
	
	protected final SnomedRefSetMemberIndexEntry simpleMember(final String referencedComponentId, final String refSetId) {
		final SnomedRefSetMemberIndexEntry.Builder member = SnomedRefSetMemberIndexEntry.builder()
				.referenceSetType(SnomedRefSetType.SIMPLE);
		return createMember(member, referencedComponentId, refSetId);
	}
	
	protected final SnomedRefSetMemberIndexEntry createSimpleMapMember(final String referencedComponentId, final String mapTarget, final String refSetId) {
		final SnomedRefSetMemberIndexEntry.Builder member = SnomedRefSetMemberIndexEntry.builder().referenceSetType(SnomedRefSetType.SIMPLE_MAP)
				.field(SnomedRf2Headers.FIELD_MAP_TARGET, mapTarget);
		return createMember(member, referencedComponentId, refSetId);
	}
	
	private final SnomedRefSetMemberIndexEntry createMember(final SnomedRefSetMemberIndexEntry.Builder member, final String referencedComponentId, final String refSetId) {
		return member.active(true)
				.id(UUID.randomUUID().toString())
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(referencedComponentId)
				.referencedComponentType(SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(referencedComponentId))
				.referenceSetId(refSetId)
				.build();
	}
	
	protected final SnomedDescriptionIndexEntry fsn(String conceptId, Map<String, Acceptability> acceptabilityMap) {
		return description(conceptId, Concepts.FULLY_SPECIFIED_NAME, "Example FSN", acceptabilityMap);
	}
	
	protected final SnomedDescriptionIndexEntry synonym(String conceptId, Map<String, Acceptability> acceptabilityMap) {
		return description(conceptId, Concepts.SYNONYM, "Example Synonym", acceptabilityMap);
	}
	
	protected final SnomedDescriptionIndexEntry definition(String conceptId, Map<String, Acceptability> acceptabilityMap) {
		return description(conceptId, Concepts.TEXT_DEFINITION, "Example Text Def", acceptabilityMap);
	}
	
	protected final SnomedDescriptionIndexEntry description(String conceptId, String typeId, String term, Map<String, Acceptability> acceptabilityMap) {
		return SnomedDescriptionIndexEntry.builder()
				.active(true)
				.released(false)
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_SENSITIVE)
				.id(generateDescriptionId())
				.conceptId(conceptId)
				.term(term)
				.typeId(typeId)
				.languageCode("en")
				.moduleId(module())
				.acceptabilityMap(acceptabilityMap)
				.build();
	}
	
	protected final SnomedConceptDocument.Builder docWithDefaults(final SnomedConceptDocument concept) {
		return SnomedConceptDocument.builder(concept)
				// new concepts without any ISA or Description should get the following derived values as defaults via the change processor
				.iconId(Concepts.ROOT_CONCEPT)
				.parents(IComponent.ROOT_IDL)
				.ancestors(PrimitiveSets.emptyLongSet())
				.statedParents(IComponent.ROOT_IDL)
				.statedAncestors(PrimitiveSets.emptyLongSet());
	}

	protected final SnomedConceptDocument.Builder concept() {
		return concept(generateConceptId());
	}
	
	protected final SnomedConceptDocument.Builder concept(final String id) {
		return SnomedConceptDocument.builder()
				.id(id)
				.active(true)
				.primitive(false)
				.moduleId(module())
				.exhaustive(false);
	}

}
