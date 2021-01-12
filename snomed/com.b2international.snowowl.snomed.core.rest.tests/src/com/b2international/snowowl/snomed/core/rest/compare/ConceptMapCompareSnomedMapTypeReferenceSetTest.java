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
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.request.MappingCorrelation;
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
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableMap;

/**
 * @since 7.8
 */
public class ConceptMapCompareSnomedMapTypeReferenceSetTest extends AbstractCoreApiTest {
	
	protected static final String SOURCE_CODE_1 = Concepts.IS_A;
	protected static final String TARGET_CODE_1 = Concepts.METHOD;

	protected static final String SOURCE_CODE_2 = Concepts.AMBIGUOUS;
	protected static final String TARGET_CODE_2 = Concepts.PROCEDURE_SITE_DIRECT;

	protected static final String SOURCE_CODE_3 = Concepts.ACCEPTABILITY;
	protected static final String TARGET_CODE_3 = Concepts.INAPPROPRIATE;

	private ComponentURI baseReferenceSetURI;
	private ComponentURI compareReferenceSetURI;
	private CodeSystemURI codeSystemURI;

	@Before
	public void createMappingSets() {
		codeSystemURI = CodeSystemURI.branch(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, branchPath.getPath().replace("MAIN/", ""));
		baseReferenceSetURI = createURI(createSimpleMapTypeRefSet("rf1"));
		compareReferenceSetURI = createURI(createSimpleMapTypeRefSet("rf2"));
	}
	
	@Test
	public void compareEqualSimpleMapTypeReferenceSets() {
		String baseMapping1_1 = createSimpleMapTypeRefSetMember(baseReferenceSetURI, SOURCE_CODE_1, TARGET_CODE_1);
		String baseMapping2_2 = createSimpleMapTypeRefSetMember(baseReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_2);
		String compareMapping1_1 = createSimpleMapTypeRefSetMember(compareReferenceSetURI, SOURCE_CODE_1, TARGET_CODE_1);
		String compareMapping2_2 = createSimpleMapTypeRefSetMember(compareReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_2);
		
		ConceptMapCompareResult result = compare(baseReferenceSetURI, compareReferenceSetURI);

		assertThat(result).containsOnly(
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.SAME, mapping(baseMapping1_1, baseReferenceSetURI, SOURCE_CODE_1, TARGET_CODE_1)),
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.SAME, mapping(baseMapping2_2, baseReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_2))
		);
	}
	
	@Test
	public void compareDifferentSimpleMapTypeReferenceSets() {
		String baseMapping1_1 = createSimpleMapTypeRefSetMember(baseReferenceSetURI, SOURCE_CODE_1, TARGET_CODE_1);
		String baseMapping2_2 = createSimpleMapTypeRefSetMember(baseReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_2);
		String compareMapping2_3 = createSimpleMapTypeRefSetMember(compareReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_3);
		String compareMapping3_3 = createSimpleMapTypeRefSetMember(compareReferenceSetURI, SOURCE_CODE_3, TARGET_CODE_3);
		
		ConceptMapCompareResult result = compare(baseReferenceSetURI, compareReferenceSetURI);

		assertThat(result).containsOnly(
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.PRESENT, mapping(baseMapping1_1, baseReferenceSetURI, SOURCE_CODE_1, TARGET_CODE_1)),
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, mapping(baseMapping2_2, baseReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_2)),
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, mapping(compareMapping2_3, compareReferenceSetURI, SOURCE_CODE_2, TARGET_CODE_3)),
			new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.MISSING, mapping(compareMapping3_3, compareReferenceSetURI, SOURCE_CODE_3, TARGET_CODE_3))
		);
	}
	
	@Test
	public void compareLargeSimpleMapTypeReferenceSets() {
		final String baseSimpleMapReferenceSet = "900000000000497000";
		final String  compareSimpleMapReferenceSet = "447562003";
		ComponentURI baseURI = ComponentURI.of(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, SnomedTerminologyComponentConstants.REFSET_NUMBER, baseSimpleMapReferenceSet);
		ComponentURI compareURI = ComponentURI.of(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, SnomedTerminologyComponentConstants.REFSET_NUMBER, compareSimpleMapReferenceSet);
		
		compare(baseURI, compareURI);
	}
	
	private ConceptMapCompareResult compare(ComponentURI baseURI, ComponentURI compareURI) {
		return CodeSystemRequests.prepareConceptMapCompare(baseURI, compareURI)
				.setLocales("en")
				.setPreferredDisplay("ID_ONLY")
				.build(codeSystemURI)
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES);
	}
	
	private ConceptMapMapping mapping(String memberId, ComponentURI containerURI, String sourceCode, String targetCode) {
		return ConceptMapMapping.builder()
				.uri(ComponentURI.of(codeSystemURI.getCodeSystem(), SnomedTerminologyComponentConstants.REFSET_MEMBER_NUMBER, memberId))
				.active(true)
				.containerSetURI(containerURI)
				.containerTerm(baseReferenceSetURI == containerURI ? "rf1" : "rf2")
				.containerIconId(Concepts.REFSET_SIMPLE_MAP_TYPE)
				.sourceComponentURI(ComponentURI.of(codeSystemURI.getCodeSystem(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, sourceCode))
				.sourceTerm(sourceCode)
				.sourceIconId("attribute")
				.targetComponentURI(ComponentURI.of(codeSystemURI.getCodeSystem(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, targetCode))
				.targetTerm(targetCode)
				.targetIconId("attribute")
				.mapGroup(0)
				.mapPriority(0)
				.mappingCorrelation(MappingCorrelation.EXACT_MATCH)
				.build();
	}
	
	private ComponentURI createURI(String referenceSetId) {
		return ComponentURI.of(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, SnomedTerminologyComponentConstants.REFSET_NUMBER, referenceSetId);
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
				.build(codeSystemURI.toString(), RestExtensions.USER, "New Reference Set")
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

	private String createSimpleMapTypeRefSetMember(ComponentURI containerURI, String sourceCode, String targetCode) {
		return SnomedRequests.prepareNewMember()
			.setReferenceSetId(containerURI.identifier())
			.setReferencedComponentId(sourceCode)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setProperties(Map.of(
				SnomedRf2Headers.FIELD_MAP_TARGET, ComponentURI.of(codeSystemURI.getCodeSystem(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER, targetCode).toString()
			))
			.build(codeSystemURI.toString(), RestExtensions.USER, "New Member")
			.execute(getBus())
			.getSync()
			.getResultAs(String.class);
	}
	
	@Override
	protected String getRepositoryId() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
}
