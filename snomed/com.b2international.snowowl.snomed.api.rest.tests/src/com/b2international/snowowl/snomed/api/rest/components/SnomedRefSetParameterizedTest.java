/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests.createBranch;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.createComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.deleteComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.api.rest.SnomedRestFixtures.getFirstAllowedReferencedComponentType;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.RefSetSupport;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.jayway.restassured.response.ValidatableResponse;

/**
 * @since 5.7
 */
@RunWith(Parameterized.class)
public class SnomedRefSetParameterizedTest extends AbstractSnomedApiTest {

	private static final Splitter SPLITTER = Splitter.on('.');

	private static final List<String> REFERENCED_COMPONENT_TYPES = ImmutableList.of(CONCEPT, DESCRIPTION, RELATIONSHIP, REFSET);

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ 	SnomedRefSetType.ASSOCIATION					}, 
			{ 	SnomedRefSetType.ATTRIBUTE_VALUE				}, 
			//  Concrete data type reference sets are tested separately 
			{ 	SnomedRefSetType.COMPLEX_MAP					},
			{ 	SnomedRefSetType.DESCRIPTION_TYPE				}, 
			{ 	SnomedRefSetType.EXTENDED_MAP					},
			{ 	SnomedRefSetType.LANGUAGE						},
			{ 	SnomedRefSetType.MODULE_DEPENDENCY				},
			{ 	SnomedRefSetType.QUERY							}, 
			{ 	SnomedRefSetType.SIMPLE							}, 
			{ 	SnomedRefSetType.SIMPLE_MAP						}, 
			{ 	SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION	}, 
		});
	}

	private final SnomedRefSetType refSetType;

	public SnomedRefSetParameterizedTest(SnomedRefSetType refSetType) {
		this.refSetType = refSetType;
	}

	@Test
	public void acceptValidRequest() {
		final String parentConceptId = SnomedRefSetUtil.getConceptId(refSetType);
		for (String referencedComponentType : RefSetSupport.getSupportedReferencedComponentTypes(refSetType)) {

			IBranchPath subPath = BranchPathUtils.createPath(branchPath, Iterables.getLast(SPLITTER.split(referencedComponentType)));
			createBranch(subPath).statusCode(201);

			String refSetId = lastPathSegment(createRefSet(subPath, parentConceptId, referencedComponentType)
					.statusCode(201)
					.extract().header("Location"));

			getComponent(subPath, SnomedComponentType.REFSET, refSetId).statusCode(200)
			.body("type", equalTo(refSetType.name()))
			.body("referencedComponentType", equalTo(referencedComponentType));
		}
	}

	@Test
	public void rejectInvalidParent() {
		String referencedComponentType = getFirstAllowedReferencedComponentType(refSetType);
		createRefSet(branchPath, Concepts.ROOT_CONCEPT, referencedComponentType).statusCode(400);
	}

	@Test
	public void rejectInvalidComponentType() {
		for (String referencedComponentType : REFERENCED_COMPONENT_TYPES) {
			if (!RefSetSupport.isReferencedComponentTypeSupported(refSetType, referencedComponentType)) {
				createRefSet(branchPath, Concepts.ROOT_CONCEPT, referencedComponentType).statusCode(400);
			}
		}
	}

	@Test
	public void deleteRefSet() {
		String refSetId = createNewRefSet(branchPath, refSetType);
		deleteComponent(branchPath, SnomedComponentType.REFSET, refSetId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, refSetId).statusCode(200);
	}

	@Test
	public void deleteIdentifierConcept() {
		String refSetId = createNewRefSet(branchPath, refSetType);
		deleteComponent(branchPath, SnomedComponentType.CONCEPT, refSetId, false).statusCode(204);
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, refSetId).statusCode(404);
	}

	private ValidatableResponse createRefSet(IBranchPath refSetPath, String parentConceptId, String referencedComponentType) {
		Map<?, ?> refSetRequestBody = createConceptRequestBody(parentConceptId)
				.put("type", refSetType)
				.put("referencedComponentType", referencedComponentType)
				.put("commitComment", "Created new reference set")
				.build();

		return createComponent(refSetPath, SnomedComponentType.REFSET, refSetRequestBody);
	}
}
