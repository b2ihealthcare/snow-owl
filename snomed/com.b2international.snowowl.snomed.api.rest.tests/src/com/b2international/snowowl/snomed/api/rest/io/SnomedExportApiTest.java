/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.api.rest.SnomedApiTestConstants.PREFERRED_ACCEPTABILITY_MAP;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentCreated;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentHasProperty;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenConceptRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.givenRelationshipRequestBody;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.getDateForNewVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedVersioningApiAssert.whenCreatingVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/**
 * @since 5.4
 */
public class SnomedExportApiTest extends AbstractSnomedExportApiTest {
	
	@Test
	public void createValidExportConfiguration() {
		final Map<?, ?> config = ImmutableMap.builder()
			.put("type", "DELTA")
			.put("branchPath", "MAIN")
			.build();
		
		final String exportId = assertExportConfigurationCanBeCreated(config);
		
		assertExportConfiguration(exportId)
			.and().body("type", equalTo("DELTA"))
			.and().body("branchPath", equalTo("MAIN"));
	}
	
	@Test
	public void createInvalidExportConfiguration() {
		final Map<Object, Object> config = ImmutableMap.builder()
				.put("type", "DELTA")
				.build();
			
		assertExportConfigurationFails(config);
	}
	
	@Test
	public void exportUnpublishedDeltaRelationships() {
		
		final Map<?, ?> conceptRequestBody = givenConceptRequestBody(null, ROOT_CONCEPT, MODULE_SCT_CORE, PREFERRED_ACCEPTABILITY_MAP, false);
		assertComponentCreated(createMainPath(), SnomedComponentType.CONCEPT, conceptRequestBody);
		
		final String dateForNewVersion = getDateForNewVersion("SNOMEDCT");
		
		whenCreatingVersion(dateForNewVersion, dateForNewVersion)
			.then().assertThat().statusCode(201);
		
		givenAuthenticatedRequest(ADMIN_API)
			.when().get("/codesystems/SNOMEDCT/versions/{id}", dateForNewVersion)
			.then().assertThat().statusCode(200);
		
		final Map<?, ?> statedRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
				"New relationship on MAIN");
		final String statedRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, statedRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, statedRelationshipId, "characteristicType",
				CharacteristicType.STATED_RELATIONSHIP.name());

		final Map<?, ?> inferredRequestBody = givenRelationshipRequestBody(DISEASE, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
				CharacteristicType.INFERRED_RELATIONSHIP, "New relationship on MAIN");
		final String inferredRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, inferredRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, inferredRelationshipId, "characteristicType",
				CharacteristicType.INFERRED_RELATIONSHIP.name());

		final Map<?, ?> additionalRequestBody = givenRelationshipRequestBody(BLEEDING, TEMPORAL_CONTEXT, FINDING_CONTEXT, MODULE_SCT_CORE,
				CharacteristicType.ADDITIONAL_RELATIONSHIP, "New relationship on MAIN");
		final String additionalRelationshipId = assertComponentCreated(createMainPath(), SnomedComponentType.RELATIONSHIP, additionalRequestBody);
		assertComponentHasProperty(createMainPath(), SnomedComponentType.RELATIONSHIP, additionalRelationshipId, "characteristicType",
				CharacteristicType.ADDITIONAL_RELATIONSHIP.name());

		final String transientEffectiveTime = "20170131";
		
		final Map<Object, Object> config = ImmutableMap.builder()
				.put("type", "DELTA")
				.put("branchPath", "MAIN")
				.put("transientEffectiveTime", transientEffectiveTime)
				.build();
			
		final String exportId = assertExportConfigurationCanBeCreated(config);
		
		assertExportConfiguration(exportId)
			.and().body("type", equalTo("DELTA"))
			.and().body("branchPath", equalTo("MAIN"))
			.and().body("transientEffectiveTime", equalTo(transientEffectiveTime));
		
		final File exportArchive = assertExportFileCreated(exportId);
		
		final List<String> statedLineElements = ImmutableList.<String> of(statedRelationshipId, transientEffectiveTime, "1", MODULE_SCT_CORE, DISEASE,
				FINDING_CONTEXT, "0", TEMPORAL_CONTEXT, CharacteristicType.STATED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
		final String statedLine = Joiner.on("\t").join(statedLineElements);

		final List<String> inferredLineElements = ImmutableList.<String> of(inferredRelationshipId, transientEffectiveTime, "1", MODULE_SCT_CORE, DISEASE,
				FINDING_CONTEXT, "0", TEMPORAL_CONTEXT, CharacteristicType.INFERRED_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
		final String inferredLine = Joiner.on("\t").join(inferredLineElements);
		
		final List<String> additionalLineElements = ImmutableList.<String> of(additionalRelationshipId, transientEffectiveTime, "1", MODULE_SCT_CORE, BLEEDING,
				FINDING_CONTEXT, "0", TEMPORAL_CONTEXT, CharacteristicType.ADDITIONAL_RELATIONSHIP.getConceptId(),
				Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
		final String additionalLine = Joiner.on("\t").join(additionalLineElements);
		
		final Multimap<String, String> fileToLinesMap = ArrayListMultimap.<String, String>create();
		
		fileToLinesMap.put("sct2_StatedRelationship", statedLine);
		fileToLinesMap.put("sct2_Relationship", inferredLine);
		fileToLinesMap.put("sct2_Relationship", additionalLine);
		
		assertArchiveContainsLines(exportArchive, fileToLinesMap);
	}
	
}
