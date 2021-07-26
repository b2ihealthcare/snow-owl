/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.conceptmap.ConceptMapRequests;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.Sets;

/**
 * @since 7.11
 */
public class ConceptMapSearchMappingRequestSnomedMapTypeReferenceSetTest {
	
	private static final ResourceURI CODESYSTEM = SnomedContentRule.SNOMEDCT.asLatest();
	
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
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByReferencedComponentId(REFERENCED_COMPONENT)
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertTrue(!conceptMaps.isEmpty());
		conceptMaps.forEach(concepMap -> assertEquals(REFERENCED_COMPONENT, concepMap.getSourceComponentURI().identifier()));
	}
	
	@Test
	public void filterByMapTarget() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_2);
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.filterByMapTarget(MAP_TARGET_1)
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertTrue(!conceptMaps.isEmpty());
		conceptMaps.forEach(concepMap -> assertEquals(MAP_TARGET_1, concepMap.getTargetComponentURI().identifier()));
	}
	
	@Test
	public void filterByComponentUriAndId() {
		final String refSetId = createSimpleMapTypeRefSet();
		final String filterId = "12345";
		final ComponentURI uri = ComponentURI.of(CODESYSTEM, SnomedConcept.TYPE, filterId);
		final ComponentURI sourceUri = ComponentURI.of(CODESYSTEM, SnomedConcept.TYPE, REFERENCED_COMPONENT);
		
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, filterId);
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, "Random map target");
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, uri.toString());
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByComponentIds(Set.of(uri.toString(), uri.identifier()))
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertEquals(2, conceptMaps.getTotal());
		Set<ComponentURI> componentUris = getComponentUris(conceptMaps);
		assertThat(componentUris).containsOnly(sourceUri, uri, ComponentURI.unspecified(filterId));
	}
	
	@Test
	public void filterByComponentUris() {
		final String refSetId = createSimpleMapTypeRefSet();

		final ComponentURI uri = ComponentURI.of(CODESYSTEM, SnomedConcept.TYPE, "12345");
		final ComponentURI uri2 = ComponentURI.of(CODESYSTEM, SnomedConcept.TYPE, "54321");
		final ComponentURI sourceUri = ComponentURI.of(CODESYSTEM, SnomedConcept.TYPE, REFERENCED_COMPONENT);
		
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, uri.toString());
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, uri2.toString());
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, "Random map target");
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByComponentIds(Set.of(uri.toString(), uri2.toString()))
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertEquals(2, conceptMaps.getTotal());
		Set<ComponentURI> componentUris = getComponentUris(conceptMaps);
		assertThat(componentUris).containsOnly(sourceUri, uri, uri2);
	}
	
	@Test
	public void filterByConceptMap() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_2);
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertEquals(2, conceptMaps.getTotal());
	}
	
	@Test
	public void useDefaultPreferredDisplay() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(ID, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	@Test
	public void setPreferredDisplayToPT() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setLocales("en")
				.setPreferredDisplay("PT")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);

		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(PT, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	@Test
	public void setPreferredDisplayToFSN() {
		final String refSetId = createSimpleMapTypeRefSet();
		createSimpleMapTypeRefSetMember(refSetId, REFERENCED_COMPONENT, MAP_TARGET_1);
		
		final ConceptMapMappings conceptMaps = ConceptMapRequests.prepareSearchConceptMapMappings()
				.all()
				.filterByConceptMap(ComponentURI.of(CODESYSTEM, SnomedConcept.REFSET_TYPE, refSetId).toString())
				.setPreferredDisplay("FSN")
				.setLocales("en")
				.buildAsync()
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertEquals(1, conceptMaps.getTotal());
		final ConceptMapMapping conceptMapMapping = conceptMaps.first().get();
		assertEquals(FSN, conceptMapMapping.getSourceTerm());
		assertEquals(testName.getMethodName(), conceptMapMapping.getContainerTerm());
	}
	
	private Set<ComponentURI> getComponentUris(ConceptMapMappings maps) {
		Set<ComponentURI> uris = Sets.newHashSet();
		maps.stream().forEach(map -> {
			uris.add(map.getSourceComponentURI());
			uris.add(map.getTargetComponentURI());
		});
		
		return uris;
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
						.setReferencedComponentType(SnomedConcept.TYPE)
						.setType(SnomedRefSetType.SIMPLE_MAP))
				.build(CODESYSTEM, RestExtensions.USER, "New Reference Set")
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
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
			.setAcceptability(Map.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));
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
			.setRefsetId(rfId)
			.setReferencedComponentId(sourceCode)
			.setActive(true)
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.setProperties(Map.of(SnomedRf2Headers.FIELD_MAP_TARGET, targetCode))
			.build(CODESYSTEM, RestExtensions.USER, "New Reference Set")
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
	}
}
