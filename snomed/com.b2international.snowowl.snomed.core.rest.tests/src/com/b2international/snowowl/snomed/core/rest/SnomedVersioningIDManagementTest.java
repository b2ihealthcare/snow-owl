/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.snomed.cis.domain.SctId;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 7.18.0
 */
public class SnomedVersioningIDManagementTest extends AbstractSnomedApiTest {

	@Test
	public void publishReleasedIdOnImport() throws Exception {
		
		String conceptId = SnomedRequests.prepareNewConcept()
			.setIdFromNamespace("")
			.setModuleId(Concepts.MODULE_SCT_CORE)
			.addDescription(SnomedRequests.prepareNewDescription()
					.setIdFromNamespace("")
					.setTerm("Test FSN (semantic tag)")
					.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
					.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK)
					.setModuleId(Concepts.MODULE_SCT_CORE))
			.addDescription(SnomedRequests.prepareNewDescription()
					.setIdFromNamespace("")
					.setTerm("Test PT")
					.setTypeId(Concepts.SYNONYM)
					.preferredIn(Concepts.REFSET_LANGUAGE_TYPE_UK)
					.setModuleId(Concepts.MODULE_SCT_CORE))
			.addRelationship(SnomedRequests.prepareNewRelationship()
					.setIdFromNamespace("")
					.setCharacteristicTypeId(Concepts.STATED_RELATIONSHIP)
					.setTypeId(Concepts.IS_A)
					.setModuleId(Concepts.MODULE_SCT_CORE)
					.setDestinationId(Concepts.REFSET_LANGUAGE_TYPE))
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, "MAIN", "info@b2international.com", "Created Concept")
			.execute(getBus())
			.getSync()
			.getResultAs(String.class);
		
		String jobId = CodeSystemRequests.prepareNewCodeSystemVersion()
			.setCodeSystemShortName(SNOMED_SHORT_NAME)
			.setDescription("New SNOMEDCT version")
			.setEffectiveTime("20210131")
			.setVersionId("2021-01-31")
			.buildAsync()
			.runAsJobWithRestart(CodeSystemRequests.versionJobKey(SNOMED_SHORT_NAME), String.format("Creating version '%s/%s'", SNOMED_SHORT_NAME, "2021-01-31"))
			.execute(Services.bus())
			.getSync(1, TimeUnit.MINUTES);
		JobRequests.waitForJob(Services.bus(), jobId, 50);
		
		SctId sctId = SnomedRequests.identifiers().prepareGet()
				.setComponentId(conceptId)
				.buildAsync()
				.execute(Services.bus())
				.getSync()
				.first()
				.get();
			
		assertEquals("Published", sctId.getStatus());
	}
	
}
