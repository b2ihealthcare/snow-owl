/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * @since 4.7
 */
public class ConceptChangeProcessorTest extends BaseChangeProcessorTest {

//	@Test
//	public void createNewRefSetWithIdentifierConcept() throws Exception {
//		final String identifierId = generateConceptId();
//		final Concept identifierConcept = createConcept(identifierId);
//		registerNew(identifierConcept);
//		final SnomedRefSet refSet = getRegularRefSet(identifierId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
//		registerNew(refSet);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(identifierConcept).refSet(refSet).build();
//		assertEquals(1, processor.getNewMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getChangedMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void createRefSetForExistingConcept() throws Exception {
//		final String identifierId = generateConceptId();
//		final Concept identifierConcept = createConcept(identifierId);
//		final long conceptStorageKey = CDOIDUtil.getLong(identifierConcept.cdoID());
//		indexRevision(MAIN, doc(identifierConcept).storageKey(conceptStorageKey).build());
//
//		SnomedConceptDocument before = getRevision(MAIN, SnomedConceptDocument.class, identifierId);
//		assertNull(before.getRefSetType());
//		assertEquals(0, before.getReferencedComponentType());
//		assertEquals(-1L, before.getRefSetStorageKey());
//		
//		final SnomedRefSet refSet = getRegularRefSet(identifierId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
//		final long refsetStorageKey = CDOIDUtil.getLong(refSet.cdoID());
//		registerNew(refSet);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(identifierConcept).refSet(refSet).build();
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//		
//		assertEquals(1, processor.getChangedMappings().size());
//		final SnomedConceptDocument actual = (SnomedConceptDocument) processor.getChangedMappings().get(identifierId).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(SnomedRefSetType.SIMPLE, actual.getRefSetType());
//		assertEquals(100, actual.getReferencedComponentType());
//		assertEquals(refsetStorageKey, actual.getRefSetStorageKey());
//	}
//	
//	@Test
//	public void deleteRefSetButKeepIdentifierConcept() throws Exception {
//		final String refSetConceptId = generateConceptId();
//		final SnomedRefSet refSet = getRegularRefSet(refSetConceptId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
//		indexRevision(MAIN, doc(createConcept(refSetConceptId)).storageKey(nextStorageKey()).refSet(refSet).build());
//		registerDetached(refSet.cdoID(), SnomedRefSetPackage.Literals.SNOMED_REF_SET);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(1, processor.getChangedMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void updateRefSetIdentifierEffectiveTime() throws Exception {
//		final String conceptId = generateConceptId();
//		final long conceptStorageKey = nextStorageKey();
//		final SnomedRefSet refSet = getRegularRefSet(conceptId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
//		final long refSetStorageKey = CDOIDUtil.getLong(refSet.cdoID());
//		indexRevision(MAIN, doc(createConcept(conceptId)).storageKey(conceptStorageKey).refSet(refSet).build());
//		
//		// change set
//		// XXX intentionally not registering this object to the concept map
//		final Date newEffectiveTime = new Date();
//		final Concept dirtyConcept = SnomedFactory.eINSTANCE.createConcept();
//		withCDOID(dirtyConcept, conceptStorageKey);
//		dirtyConcept.setId(conceptId);
//		dirtyConcept.setEffectiveTime(newEffectiveTime);
//		dirtyConcept.setReleased(true);
//		dirtyConcept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
//		dirtyConcept.setModule(module());
//		dirtyConcept.setExhaustive(false);
//		registerDirty(dirtyConcept);
//		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__RELEASED, false, true);
//		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME, null, newEffectiveTime);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(1, processor.getChangedMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//		
//		// assert that refset props are not going to be removed from the concept doc
//		final SnomedConceptDocument newRevision = (SnomedConceptDocument) processor.getChangedMappings().get(conceptId).getNewRevision();
//		assertEquals(refSetStorageKey, newRevision.getRefSetStorageKey());
//		assertEquals(newEffectiveTime.getTime(), newRevision.getEffectiveTime());
//		assertEquals(true, newRevision.isReleased());
//	}
//	
//	@Test
//	public void addNewSimpleMemberToExistingConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
//		
//		// set current state
//		registerExistingObject(concept);
//		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
//		
//		registerNew(member);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(ImmutableSet.of(referringReferenceSetId))
//				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void addInactiveMemberToExistingConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
//		member.setActive(false);
//		
//		// set current state
//		registerExistingObject(concept);
//		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
//		
//		registerNew(member);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(ImmutableSet.of(referringReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void addNewSimpleMapMemberToExistingConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringMappingReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMapMember(conceptId, "A00", referringMappingReferenceSetId);
//		
//		// set current state
//		registerExistingObject(concept);
//		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
//		
//		registerNew(member);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with new memberOf array fields
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(ImmutableSet.of(referringMappingReferenceSetId))
//				.activeMemberOf(ImmutableSet.of(referringMappingReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void inactivateSimpleMemberOfConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
//		
//		// set current state
//		registerExistingObject(concept);
//		registerExistingObject(member);
//		indexRevision(MAIN, doc(concept)
//				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
//				.memberOf(Collections.singleton(referringReferenceSetId))
//				.activeMemberOf(Collections.singleton(referringReferenceSetId))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
//				.storageKey(CDOIDUtil.getLong(member.cdoID()))
//				.build());
//		
//		member.setActive(false);
//		registerDirty(member);
//		registerSetRevisionDelta(member, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true, false);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(ImmutableSet.of(referringReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void reactivateSimpleMemberOfConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
//		member.setActive(false);
//		
//		// set current state
//		registerExistingObject(concept);
//		registerExistingObject(member);
//		indexRevision(MAIN, doc(concept)
//				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
//				.memberOf(Collections.singleton(referringReferenceSetId))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
//				.storageKey(CDOIDUtil.getLong(member.cdoID()))
//				.build());
//		
//		member.setActive(true);
//		registerDirty(member);
//		registerSetRevisionDelta(member, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, false, true);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(ImmutableSet.of(referringReferenceSetId))
//				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void deleteSimpleMemberOfConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
//		
//		// set current state
//		registerExistingObject(concept);
//		registerExistingObject(member);
//		indexRevision(MAIN, doc(concept)
//				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
//				.memberOf(ImmutableSet.of(referringReferenceSetId))
//				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
//				.storageKey(CDOIDUtil.getLong(member.cdoID()))
//				.build());
//		
//		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept).build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void deleteOneMemberFromMultipleMembersOfConcept() {
//		final String conceptId = generateConceptId();
//		final String referringReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member1 = createSimpleMember(conceptId, referringReferenceSetId);
//		final SnomedRefSetMember member2 = createSimpleMember(conceptId, referringReferenceSetId);
//		
//		registerExistingObject(concept);
//		registerExistingObject(member1);
//		registerExistingObject(member2);
//		
//		indexRevision(MAIN, doc(concept)
//				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
//				.memberOf(ImmutableList.of(referringReferenceSetId, referringReferenceSetId))
//				.activeMemberOf(ImmutableList.of(referringReferenceSetId, referringReferenceSetId))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member1)
//				.storageKey(CDOIDUtil.getLong(member1.cdoID()))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member2)
//				.storageKey(CDOIDUtil.getLong(member2.cdoID()))
//				.build());
//		
//		registerDetached(member1.cdoID(), member1.eClass());
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the new memberOf and activeMemberOf array field values
//		final SnomedConceptDocument expected = doc(concept)
//				.memberOf(Collections.singleton(referringReferenceSetId))
//				.activeMemberOf(Collections.singleton(referringReferenceSetId))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void deleteSimpleMapMemberOfConcept() throws Exception {
//		final String conceptId = generateConceptId();
//		final String referringMappingReferenceSetId = generateConceptId();
//		
//		final Concept concept = createConcept(conceptId);
//		final SnomedRefSetMember member = createSimpleMapMember(conceptId, "A00", referringMappingReferenceSetId);
//		
//		// set current state
//		registerExistingObject(concept);
//		registerExistingObject(member);
//		indexRevision(MAIN, doc(concept)
//				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
//				.memberOf(ImmutableSet.of(referringMappingReferenceSetId))
//				.activeMemberOf(ImmutableSet.of(referringMappingReferenceSetId))
//				.build());
//		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
//				.storageKey(CDOIDUtil.getLong(member.cdoID()))
//				.build());
//		
//		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// the concept needs to be reindexed with the referring member value
//		final SnomedConceptDocument expected = doc(concept).build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void inactivateConcept() throws Exception {
//		// current state
//		final String conceptId = generateConceptId();
//		final Concept concept = createConcept(conceptId);
//		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
//		registerExistingObject(concept);
//		indexRevision(MAIN, doc(concept).storageKey(conceptStorageKey).build());
//		
//		// change set
//		// XXX intentionally not registering this object to the concept map
//		final Concept dirtyConcept = SnomedFactory.eINSTANCE.createConcept();
//		withCDOID(dirtyConcept, conceptStorageKey);
//		dirtyConcept.setId(conceptId);
//		dirtyConcept.setActive(false);
//		dirtyConcept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
//		dirtyConcept.setModule(module());
//		dirtyConcept.setExhaustive(false);
//		registerDirty(dirtyConcept);
//		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__ACTIVE, true, false);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		// expected index changes, concept should be inactive now
//		assertEquals(1, processor.getChangedMappings().size());
//		final SnomedConceptDocument expected = doc(concept).active(false).build();
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
	
//	@Test
//	public void indexNewStatedChildConceptOfRoot() throws Exception {
//		// index the ROOT concept as existing concept
//		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
//		statedChangedConceptIds.add(rootConceptId);
//		
//		final Concept concept = createConcept(generateConceptId());
//		availableImages.add(concept.getId());
//		registerNew(concept);
//		
//		final Relationship relationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
//		concept.getOutboundRelationships().add(relationship);
//		registerNew(relationship);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(concept)
//				.iconId(concept.getId())
//				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
//				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
//				.build();
//		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getChangedMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
	
//	@Test
//	public void indexNewStatedAndInferredChildConceptOfRoot() throws Exception {
//		// index the ROOT concept as existing concept
//		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
//		statedChangedConceptIds.add(rootConceptId);
//		inferredChangedConceptIds.add(rootConceptId);
//		
//		final Concept concept = createConcept(generateConceptId());
//		registerNew(concept);
//		
//		final Relationship statedRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
//		concept.getOutboundRelationships().add(statedRelationship);
//		registerNew(statedRelationship);
//		
//		final Relationship inferredRelationship = createInferredRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
//		concept.getOutboundRelationships().add(inferredRelationship);
//		registerNew(inferredRelationship);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(concept)
//				.parents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
//				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
//				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
//				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
//				.build();
//		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getChangedMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
	
}
