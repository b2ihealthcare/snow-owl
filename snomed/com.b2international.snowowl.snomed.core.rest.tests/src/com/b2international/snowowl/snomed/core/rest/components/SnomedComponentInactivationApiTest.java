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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.updateComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewConcept;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 7.9.2
 */
public class SnomedComponentInactivationApiTest extends AbstractSnomedApiTest {

	@Test
	public void reuseConceptAndDescriptionInactivationIndicators() throws Exception {
		// create a concept
		String conceptId = createNewConcept(branchPath);
		SnomedConcept concept = getConcept(conceptId, "descriptions()");
		
		// add pending move to concept and descriptions
		Map<?, ?> pendingMoveUpdate = ImmutableMap.of(
			"inactivationProperties", new InactivationProperties(Concepts.PENDING_MOVE, null),
			"commitComment", "Set to Pending Move"
		);

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, pendingMoveUpdate)
			.statusCode(204);
		
		for (SnomedDescription description : concept.getDescriptions()) {
			updateComponent(branchPath, SnomedComponentType.DESCRIPTION, description.getId(), pendingMoveUpdate)
				.statusCode(204);
		}
		
		SnomedConcept pendingMoveConcept = getConcept(conceptId, "members(),descriptions(expand(members()))"); // XXX intentionally using the members() expand here to check duplication
		
		// verify and collect inactivation indicator members
		SnomedReferenceSetMember conceptInactivationIndicatorMember = getIndicatorMember(pendingMoveConcept, Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR);
		SnomedReferenceSetMember fsnInactivationIndicatorMember = null;
		SnomedReferenceSetMember ptInactivationIndicatorMember = null;
		
		for (SnomedDescription description : pendingMoveConcept.getDescriptions()) {
			if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
				fsnInactivationIndicatorMember = getIndicatorMember(description, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
			} else if (Concepts.SYNONYM.equals(description.getTypeId())) {
				ptInactivationIndicatorMember = getIndicatorMember(description, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
			} else {
				throw new UnsupportedOperationException();
			}
		}
		
		Map<?, ?> inactivationConceptRequest = ImmutableMap.builder()
				.put("active", false)
				.put("inactivationProperties", new InactivationProperties(Concepts.DUPLICATE, null))
				.put("commitComment", "Inactivated concept")
				.build();

		updateComponent(branchPath, SnomedComponentType.CONCEPT, conceptId, inactivationConceptRequest).statusCode(204);
		
		SnomedConcept inactivatedConcept = getConcept(conceptId, "members(),descriptions(expand(members()))"); // XXX intentionally using the members() expand here to check duplication/member issues
		
		SnomedReferenceSetMember afterInactivationConceptInactivationIndicatorMember = getIndicatorMember(inactivatedConcept, Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR);
		SnomedReferenceSetMember afterInactivationFsnInactivationIndicatorMember = null;
		SnomedReferenceSetMember afterInactivationPtInactivationIndicatorMember = null;
		
		for (SnomedDescription description : inactivatedConcept.getDescriptions()) {
			if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
				afterInactivationFsnInactivationIndicatorMember = getIndicatorMember(description, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
			} else if (Concepts.SYNONYM.equals(description.getTypeId())) {
				afterInactivationPtInactivationIndicatorMember = getIndicatorMember(description, Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
			} else {
				throw new UnsupportedOperationException();
			}
		}
		
		assertEquals(conceptInactivationIndicatorMember.getId(), afterInactivationConceptInactivationIndicatorMember.getId());
		assertEquals(null, afterInactivationConceptInactivationIndicatorMember.getEffectiveTime());
		assertEquals(false, afterInactivationConceptInactivationIndicatorMember.isReleased());
		assertEquals(Concepts.DUPLICATE, afterInactivationConceptInactivationIndicatorMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
		
		assertEquals(fsnInactivationIndicatorMember.getId(), afterInactivationFsnInactivationIndicatorMember.getId());
		assertEquals(null, afterInactivationFsnInactivationIndicatorMember.getEffectiveTime());
		assertEquals(false, afterInactivationFsnInactivationIndicatorMember.isReleased());
		assertEquals(Concepts.CONCEPT_NON_CURRENT, afterInactivationFsnInactivationIndicatorMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
		
		assertEquals(ptInactivationIndicatorMember.getId(), afterInactivationPtInactivationIndicatorMember.getId());
		assertEquals(null, afterInactivationPtInactivationIndicatorMember.getEffectiveTime());
		assertEquals(false, afterInactivationPtInactivationIndicatorMember.isReleased());
		assertEquals(Concepts.CONCEPT_NON_CURRENT, afterInactivationPtInactivationIndicatorMember.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
		
	}

	private SnomedReferenceSetMember getIndicatorMember(SnomedCoreComponent component, String inactivationIndicatorRefSetId) {
		List<SnomedReferenceSetMember> members = component.getMembers()
			.stream()
			.filter(member -> inactivationIndicatorRefSetId.equals(member.getReferenceSetId()))
			.collect(Collectors.toList());
		if (members.size() != 1) {
			fail("Missing or duplicate inactivation member detected for component: " + component.getId() + ", " + members);
		}
		return Iterables.getOnlyElement(members);
	}
	
}
