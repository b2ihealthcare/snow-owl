/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.*;
import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.*;

import java.util.Map;

/**
 * @since 4.5
 */
public class SnomedRefSetApiTest extends AbstractSnomedApiTest {

	@Test
	public void createSimpleTypeRefSet() throws Exception {
		givenBranchWithPath(testBranchPath);
		
		final Map<String, Object> conceptBody = (Map<String, Object>) givenConceptRequestBody(null, Concepts.REFSET_SIMPLE_TYPE, Concepts.MODULE_SCT_CORE, SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP, true);
		final Builder<String, Object> requestBody = ImmutableMap.builder();
		requestBody.putAll(conceptBody);
		requestBody.put("commitComment", "New simple type reference set");
		requestBody.put("type", SnomedRefSetType.SIMPLE);
		requestBody.put("referencedComponentType", SnomedTerminologyComponentConstants.CONCEPT);
		
		final String refSetId = assertComponentCreated(testBranchPath, SnomedComponentType.REFSET, requestBody.build());
		assertComponentExists(testBranchPath, SnomedComponentType.REFSET, refSetId);
	}
	
}
