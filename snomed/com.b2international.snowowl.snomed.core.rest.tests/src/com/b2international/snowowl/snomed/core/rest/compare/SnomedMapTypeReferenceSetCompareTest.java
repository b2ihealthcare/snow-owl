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
package com.b2international.snowowl.snomed.core.rest.compare;

import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.SYNONYM;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.AbstractCoreApiTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @since 7.8
 */
public class SnomedMapTypeReferenceSetCompareTest extends AbstractCoreApiTest {
	
	protected static final String SOURCE_CODE_1 = Concepts.IS_A;
	protected static final String TARGET_CODE_1 = Concepts.METHOD;

	protected static final String SOURCE_CODE_2 = Concepts.AMBIGUOUS;
	protected static final String TARGET_CODE_2 = Concepts.PROCEDURE_SITE_DIRECT;

	protected static final String SOURCE_CODE_3 = Concepts.ACCEPTABILITY;
	protected static final String TARGET_CODE_3 = Concepts.INAPPROPRIATE;

	private String rf1Id;
	private String rf2Id;
	private String rf3Id;
	private String rf4Id;
	protected static final String USER = "info@b2international.com";
	private CodeSystemURI codeSystemURI;
	

	@Before
	public void createMappingSets() {
		codeSystemURI = CodeSystemURI.branch(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, branchPath.getPath().replace("MAIN/", ""));
		rf1Id = createSimpleMapTypeRefSet("rf1");
		rf2Id = createSimpleMapTypeRefSet("rf2");
		
		rf3Id = createSimpleMapTypeRefSet("rf3");
		rf4Id = createSimpleMapTypeRefSet("rf4");
	}
	
	@Test
	public void compareEqualSimpleMapTypeReferenceSets() {
		createSimpleMapTypeRefSetMember(rf1Id, SOURCE_CODE_1, TARGET_CODE_1);
		createSimpleMapTypeRefSetMember(rf1Id, SOURCE_CODE_2, TARGET_CODE_2);
		createSimpleMapTypeRefSetMember(rf2Id, SOURCE_CODE_1, TARGET_CODE_1);
		createSimpleMapTypeRefSetMember(rf2Id, SOURCE_CODE_2, TARGET_CODE_2);
		
		ComponentURI baseURI = createURI(rf1Id);
		ComponentURI compareURI = createURI(rf2Id);
		
		ConceptMapCompareResult result = CodeSystemRequests.prepareConceptMapCompare(baseURI, compareURI)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();
		
		assertThat(result.getRemovedMembers()).hasSize(0);
		assertThat(result.getAddedMembers()).hasSize(0);
		assertThat(result.getChangedMembers().entries()).hasSize(0);
	}
	
	@Test
	public void compareDifferentSimpleMapTypeReferenceSets() {
		createSimpleMapTypeRefSetMember(rf3Id, SOURCE_CODE_1, TARGET_CODE_1);
		createSimpleMapTypeRefSetMember(rf3Id, SOURCE_CODE_2, TARGET_CODE_2);
		createSimpleMapTypeRefSetMember(rf4Id, SOURCE_CODE_3, TARGET_CODE_3);
		createSimpleMapTypeRefSetMember(rf4Id, SOURCE_CODE_2, TARGET_CODE_3);
		
		ComponentURI baseURI = createURI(rf3Id);
		ComponentURI compareURI = createURI(rf4Id);
		
		ConceptMapCompareResult result = CodeSystemRequests.prepareConceptMapCompare(baseURI, compareURI)
				.build(codeSystemURI)
				.execute(getBus())
				.getSync();

		assertThat(result.getRemovedMembers()).hasSize(1);
		assertThat(result.getAddedMembers()).hasSize(1);
		assertThat(result.getChangedMembers().entries()).hasSize(1);

		assertThat(result.getRemovedMembers().get(0).getSourceComponentURI().identifier()).isEqualTo(SOURCE_CODE_1);
		assertThat(result.getAddedMembers().get(0).getSourceComponentURI().identifier()).isEqualTo(SOURCE_CODE_3);
		assertThat(result.getChangedMembers().keySet()).anyMatch(m -> TARGET_CODE_2.equals(m.getTargetComponentURI().identifier()));
		assertThat(result.getChangedMembers().values()).anyMatch(m -> TARGET_CODE_3.equals(m.getTargetComponentURI().identifier()));
	}
	
	private ComponentURI createURI(String rfId) {
		return ComponentURI.of(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, SnomedTerminologyComponentConstants.REFSET_NUMBER, rfId);
	}
	
	private String createSimpleMapTypeRefSet(String refSetName) {
		return SnomedRequests.prepareNewConcept()
				.setIdFromNamespace(Concepts.B2I_NAMESPACE)
				.setActive(true)
				.setModuleId(Concepts.MODULE_SCT_CORE)
				.addDescription(createDescription(refSetName + " (foundation metadata concept)", FULLY_SPECIFIED_NAME))
				.addDescription(createDescription(refSetName, SYNONYM))
				.addRelationship(createIsaRelationship(Concepts.STATED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE_MAP)))
				.addRelationship(createIsaRelationship(Concepts.INFERRED_RELATIONSHIP, SnomedRefSetUtil.getParentConceptId(SnomedRefSetType.SIMPLE_MAP)))
				.setRefSet(SnomedRequests.prepareNewRefSet()
						.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
						.setMapTargetComponentType(SnomedTerminologyComponentConstants.CONCEPT)
						.setType(SnomedRefSetType.SIMPLE_MAP))
				.build(codeSystemURI.toString(), USER, "New Reference Set")
				.execute(getBus())
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
			.setTypeId(IS_A)
			.setCharacteristicTypeId(characteristicTypeId)
			.setModifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
	}

	private void createSimpleMapTypeRefSetMember(String rfId, String sourceCode, String targetCode) {
		Map<String, Object> properties = Maps.newHashMap();
		properties.put(SnomedRf2Headers.FIELD_MAP_TARGET, targetCode);
		
		SnomedRequests.prepareNewMember()
		.setId(UUID.randomUUID().toString())
		.setReferenceSetId(rfId)
		.setReferencedComponentId(sourceCode)
		.setActive(true)
		.setModuleId(Concepts.MODULE_SCT_CORE)
		.setProperties(properties)
		.build(codeSystemURI.toString(), USER, "New Member")
		.execute(getBus())
		.getSync();
	}
	
	@Override
	protected String getRepositoryId() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
}
