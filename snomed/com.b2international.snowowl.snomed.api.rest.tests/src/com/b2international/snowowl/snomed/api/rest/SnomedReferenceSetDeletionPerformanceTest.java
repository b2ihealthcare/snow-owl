/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.databene.contiperf.junit.ContiPerfRuleExt;
import org.junit.Rule;
import org.junit.Test;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Sets;

/**
 * @since 6.9
 */
public class SnomedReferenceSetDeletionPerformanceTest extends AbstractSnomedApiTest {
	
	private static final int CONCEPT_CREATION_LIMIT = 1000;
	
	@Rule
	public ContiPerfRule rule = new ContiPerfRuleExt();
	
	@Test
	@PerfTest(invocations = 1)
	@Required(totalTime = 30000) // max 10 seconds to execute large refset deletion tests, but this should not take longer than 5-6 sec
	public void testLargeReferenceSetDeletion() {
		final String refSetId = createNewRefSet(branchPath);
 		final Set<String> conceptIds = generateConceptIds(CONCEPT_CREATION_LIMIT);
 		final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
		
 		createConcepts(conceptIds, bulk);
		
 		final Set<String> membersIds = Sets.newHashSet();
		final Iterator<String> conceptIterator = conceptIds.iterator();
		createMembers(refSetId, bulk, conceptIterator, membersIds);		
		
		SnomedRequests.prepareCommit()
			.setBody(bulk)
			.setCommitComment("Bulk request of " + CONCEPT_CREATION_LIMIT +  "reference set member creation and id generation")
			.setUserId("info@b2international.com")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		final int refsetMemberSizeAfterCreation = SnomedRequests.prepareSearchRefSet()
			.all()
			.filterById(refSetId)
			.setExpand("members(limit:0)")
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
			.execute(getBus())
			.getSync()
			.first()
			.get()
			.getMembers().getTotal();
		
		assertEquals(refsetMemberSizeAfterCreation, membersIds.size());
		SnomedRequests.prepareDeleteReferenceSet(refSetId)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath(), "info@b2international.com", "Deleting large refset")
			.execute(getBus())
			.getSync();
		
	}

	private void createMembers(final String refSetId, final BulkRequestBuilder<TransactionContext> bulk, final Iterator<String> conceptIterator, final Set<String> membersIds) {
		for (int i = 0; i < CONCEPT_CREATION_LIMIT; i++) {
			final String referencedComponentId = conceptIterator.next();
			final String memberId = UUID.randomUUID().toString();
			membersIds.add(memberId);
			final SnomedRefSetMemberCreateRequestBuilder memberCreateReqBuilder = SnomedRequests.prepareNewMember()
				.setId(memberId)
				.setActive(true)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setReferencedComponentId(referencedComponentId)
				.setReferenceSetId(refSetId);
			
			bulk.add(memberCreateReqBuilder);
		}
	}

	private void createConcepts(final Set<String> conceptIds, final BulkRequestBuilder<TransactionContext> bulk) {
		final Iterator<String> conceptIdIterator = conceptIds.iterator();
		for (int i = 0; i < CONCEPT_CREATION_LIMIT; i++) {
			final String conceptId = conceptIdIterator.next();
			final SnomedConceptCreateRequestBuilder conceptCreateRequestBuilder = SnomedRequests.prepareNewConcept()
 				.setId(conceptId)
 				.setModuleId(Concepts.MODULE_SCT_CORE)
 				.addDescription(createDescriptionRequest(Concepts.FULLY_SPECIFIED_NAME, "fsn"))
 				.addDescription(createDescriptionRequest(Concepts.SYNONYM, "pt"))
 				.addRelationship(createRelationshipRequest(Concepts.IS_A, CharacteristicType.STATED_RELATIONSHIP, Concepts.MODULE_SCT_CORE))
 				.addRelationship(createRelationshipRequest(Concepts.IS_A, CharacteristicType.INFERRED_RELATIONSHIP, Concepts.MODULE_SCT_CORE));
 			
 			bulk.add(conceptCreateRequestBuilder);
		}
	}
	
	private Set<String> generateConceptIds(int quantity) {
		return SnomedRequests.identifiers()
				.prepareGenerate()
				.setNamespace(Concepts.B2I_NAMESPACE)
				.setCategory(ComponentCategory.CONCEPT)
				.setQuantity(quantity)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus())
				.getSync()
				.stream()
				.map(SctId::getSctid)
				.collect(Collectors.toSet());
	}
	
	private SnomedRelationshipCreateRequestBuilder createRelationshipRequest(String typeId, CharacteristicType characteristicType, String desctinationId) {
		return SnomedRequests.prepareNewRelationship()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setDestinationId(desctinationId)
				.setTypeId(typeId)
				.setCharacteristicType(characteristicType);
	}

	private SnomedDescriptionCreateRequestBuilder createDescriptionRequest(String typeId, String term) {
		return SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.setTerm(term)
				.setTypeId(typeId)
				.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK);
	}

}
