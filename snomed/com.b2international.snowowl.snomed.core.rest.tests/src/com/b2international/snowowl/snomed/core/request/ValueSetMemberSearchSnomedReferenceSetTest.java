/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.SetMembers;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.7
 */
public class ValueSetMemberSearchSnomedReferenceSetTest {

	private static final String CODESYSTEM = "SNOMEDCT/LATEST";
	
	private static final String SYNONYM = "Synonym (core metadata concept)";
	private static final String FSN = "Fully specified name (core metadata concept)";
	private static final String DEFINITION = "Definition (core metadata concept)";
	
	@Test
	public void filterByRefset() throws Exception {
		
		SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByRefSet(Concepts.REFSET_DESCRIPTION_TYPE)
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();
		
		SetMembers setMembers = CodeSystemRequests.prepareSearchMembers()
			.all()
			.filterBySet(Concepts.REFSET_DESCRIPTION_TYPE)		
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();
				
		assertThat(setMembers.getTotal()).isEqualTo(members.getTotal());
		assertThat(setMembers.stream().allMatch(m -> SNOMED_SHORT_NAME.equals(m.getReferencedComponentURI().codeSystem())));
		
		Set<String> setMemberSourceCodes = FluentIterable.from(setMembers).transform(m -> m.getReferencedComponentURI().identifier()).toSet();
		Set<String> setMemberSourceTerms = FluentIterable.from(setMembers).transform(m -> m.getReferencedComponentURI().identifier()).toSet();
		
		assertThat(setMemberSourceCodes.contains(Concepts.TEXT_DEFINITION));
		assertThat(setMemberSourceCodes.contains(Concepts.FULLY_SPECIFIED_NAME));
		assertThat(setMemberSourceCodes.contains(Concepts.SYNONYM));
		
		assertThat(setMemberSourceTerms.contains(DEFINITION));
		assertThat(setMemberSourceTerms.contains(FSN));
		assertThat(setMemberSourceTerms.contains(SYNONYM));
	}
	
	@Test
	public void filterByComponent() {
		final String filteredId = Concepts.FINDING_SITE;
		
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, filteredId, Concepts.IS_A);
		createSimpleMapTypeRefSetMember(refSetId, filteredId, Concepts.IS_A);
		createSimpleMapTypeRefSetMember(refSetId, Concepts.IS_A, filteredId);
		createSimpleMapTypeRefSetMember(refSetId, Concepts.IS_A, Concepts.IS_A);
		
		final SnomedReferenceSetMembers refSetMembers = SnomedRequests.prepareSearchMember()
			.filterByRefSet(refSetId)
			.filterByComponentId(filteredId)
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();
		
		assertEquals(3, refSetMembers.getTotal());
		
		refSetMembers.forEach(refSetMember -> assertTrue(
				filteredId.equals(refSetMember.getReferencedComponentId()) || 
				filteredId.equals(refSetMember.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET))));
	}
	
	private String createSimpleMapTypeRefSet() {
		return SnomedRequests.prepareNewConcept()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setActive(true)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.addDescription(createDescription("filterByComponentMapTypeRefset" + " (foundation metadata concept)", SnomedConstants.Concepts.FULLY_SPECIFIED_NAME))
				.addDescription(createDescription("filterByComponentMapTypeRefset", SnomedConstants.Concepts.SYNONYM))
				.addRelationship(createIsaRelationship(Concepts.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE_MAP)))
				.addRelationship(createIsaRelationship(Concepts.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE_MAP)))
				.setRefSet(SnomedRequests.prepareNewRefSet()
						.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
						.setType(SnomedRefSetType.SIMPLE_MAP))
				.build(CODESYSTEM, RestExtensions.USER, "New Reference Set")
				.execute(Services.bus())
				.getSync()
				.getResultAs(String.class);
	}
	
	private static SnomedDescriptionCreateRequestBuilder createDescription(final String term, final String type) {
		return SnomedRequests.prepareNewDescription()
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setLanguageCode("en")
			.setTypeId(type)
			.setTerm(term)
			.setCaseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
			.setAcceptability(ImmutableMap.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
	}
	
	private static SnomedRelationshipCreateRequestBuilder createIsaRelationship(final String characteristicTypeId, String destinationId) {
		return SnomedRequests.prepareNewRelationship() 
			.setIdFromNamespace(Concepts.B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setDestinationId(destinationId)
			.setTypeId(Concepts.IS_A)
			.setCharacteristicTypeId(characteristicTypeId)
			.setModifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	}
	
	private void createSimpleMapTypeRefSetMember(final String rfId, final String sourceCode, final String targetCode) {
		SnomedRequests.prepareNewMember()
			.setId(UUID.randomUUID().toString())
			.setReferenceSetId(rfId)
			.setReferencedComponentId(sourceCode)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setProperties(ImmutableMap.of(SnomedRf2Headers.FIELD_MAP_TARGET, targetCode))
			.build(CODESYSTEM, RestExtensions.USER, "New Reference Set")
			.execute(Services.bus())
			.getSync();
	}
}
