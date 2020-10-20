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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.11
 */
public class SnomedConceptMapSearchRequestTest {
	
	private static final String CODESYSTEM = "SNOMEDCT/LATEST";
	
	private static final String REFERENCED_COMPONENT = Concepts.SUBSTANCE;
	
	private static final String ID = "105590001";
	private static final String PT = "Substance";
	private static final String FSN = "Substance (substance)";
	
	private static final String MAP_TARGET_1 = "map_target_1";
	private static final String MAP_TARGET_2 = "map_target_2";

	
	@Rule 
	public TestName testName = new TestName();
	
	@Test
	public void filterByReferencedComponent() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByReferencedComponentId(REFERENCED_COMPONENT)
				.setLocales("en")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertTrue(!conceptMaps.isEmpty());
		conceptMaps.forEach(concepMap -> assertEquals(REFERENCED_COMPONENT, concepMap.getSourceComponentURI().identifier()));
	}
	
	@Test
	public void filterByMapTarget() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByMapTarget(MAP_TARGET_1)
				.setLocales("en")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertTrue(!conceptMaps.isEmpty());
		conceptMaps.forEach(concepMap -> assertEquals(MAP_TARGET_1, concepMap.getTargetComponentURI().identifier()));
	}
	
	@Test
	public void filterByComponent() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, Concepts.IS_A);
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, Concepts.IS_A);
		createSimpleMapTypeRefSetMember(refSetId, Concepts.IS_A, REFERENCED_COMPONENT);
		createSimpleMapTypeRefSetMember(refSetId, Concepts.IS_A, Concepts.IS_A);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
			.filterByComponentId(REFERENCED_COMPONENT)
			.setLocales("en")
			.build(CODESYSTEM)
			.execute(Services.bus())
			.getSync();

		assertTrue(!conceptMaps.isEmpty());
		conceptMaps.forEach(concepMap -> assertTrue(
				REFERENCED_COMPONENT.equals(concepMap.getSourceComponentURI().identifier()) || 
				REFERENCED_COMPONENT.equals(concepMap.getTargetComponentURI().identifier())));
	}
	
	@Test
	public void filterByConceptMap() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_2);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(refSetId)
				.setLocales("en")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertEquals(2, conceptMaps.getTotal());
	}
	
	@Test
	public void useDefaultPreferredDisplay() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(refSetId)
				.setLocales("en")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(ID, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	@Test
	public void setPreferredDisplayToPT() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(refSetId)
				.setLocales("en")
				.setPreferredDisplay("PT")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(PT, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	@Test
	public void setPreferredDisplayToFSN() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(refSetId)
				.setPreferredDisplay("FSN")
				.setLocales("en")
				.build(CODESYSTEM)
				.execute(Services.bus())
				.getSync();

		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(FSN, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	private String createSimpleMapTypeRefSet() {
		return SnomedRequests.prepareNewConcept()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setActive(true)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.addDescription(createDescription(testName.getMethodName() + " (foundation metadata concept)", SnomedConstants.Concepts.FULLY_SPECIFIED_NAME))
				.addDescription(createDescription(testName.getMethodName(), SnomedConstants.Concepts.SYNONYM))
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
