/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.api.rest.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.snomed.api.rest.CodeSystemVersionRestRequests.getVersion;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.createImport;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.deleteImport;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.getImport;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.uploadImportFile;
import static com.b2international.snowowl.snomed.api.rest.SnomedImportRestRequests.waitForImportJob;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.google.common.collect.ImmutableMap;

/**
 * @since 2.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedImportApiTest extends AbstractSnomedApiTest {

	private void importArchive(final String fileName) {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("createVersions", false)
				.build();

		importArchive(fileName, importConfiguration);
	}

	private void importArchive(final String fileName, Map<?, ?> importConfiguration) {
		final String importId = lastPathSegment(createImport(importConfiguration).statusCode(201)
				.extract().header("Location"));

		getImport(importId).statusCode(200).body("status", equalTo(ImportStatus.WAITING_FOR_FILE.name()));
		uploadImportFile(importId, getClass(), fileName).statusCode(204);
		waitForImportJob(importId).statusCode(200).body("status", equalTo(ImportStatus.COMPLETED.name()));
	}

	@Test
	public void import01CreateValidConfiguration() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("createVersions", false)
				.build();

		final String importId = lastPathSegment(createImport(importConfiguration).statusCode(201)
				.extract().header("Location"));

		getImport(importId).statusCode(200).body("status", equalTo(ImportStatus.WAITING_FOR_FILE.name()));
	}

	@Test
	public void import02DeleteConfiguration() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", "MAIN")
				.put("createVersions", false)
				.build();

		final String importId = lastPathSegment(createImport(importConfiguration).statusCode(201)
				.extract().header("Location"));

		deleteImport(importId).statusCode(204);
		getImport(importId).statusCode(404);
	}

	@Test
	public void import03VersionsAllowedOnBranch() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("createVersions", true)
				.build();

		final String importId = lastPathSegment(createImport(importConfiguration).statusCode(201)
				.extract().header("Location"));

		getImport(importId).statusCode(200)
		.body("status", equalTo(ImportStatus.WAITING_FOR_FILE.name()));
	}

	@Test
	public void import04NewConcept() {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200).body("pt.id", equalTo("13809498114"));
	}

	@Test
	public void import05NewDescription() {
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, "11320138110").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150201_new_description.zip");
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, "11320138110").statusCode(200);
	}

	@Test
	public void import06NewRelationship() {
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "24088071128").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150202_new_relationship.zip");
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "24088071128").statusCode(200);
	}

	@Test
	public void import07NewPreferredTerm() {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150201_new_description.zip");
		importArchive("SnomedCT_Release_INT_20150203_change_pt.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200).body("pt.id", equalTo("11320138110"));
	}

	@Test
	public void import08ConceptInactivation() {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150201_new_description.zip");
		importArchive("SnomedCT_Release_INT_20150202_new_relationship.zip");
		importArchive("SnomedCT_Release_INT_20150203_change_pt.zip");
		importArchive("SnomedCT_Release_INT_20150204_inactivate_concept.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(200).body("active", equalTo(false));
	}

	@Test
	public void import09ImportSameConceptWithAdditionalDescription() throws Exception {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150201_new_description.zip");
		importArchive("SnomedCT_Release_INT_20150202_new_relationship.zip");
		importArchive("SnomedCT_Release_INT_20150203_change_pt.zip");
		importArchive("SnomedCT_Release_INT_20150204_inactivate_concept.zip");

		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200)
		.body("active", equalTo(false))
		.body("pt.id", equalTo("11320138110"));

		createCodeSystem(branchPath, "SNOMEDCT-EXT").statusCode(201);
		createVersion("SNOMEDCT-EXT", "v1", "20170301").statusCode(201);

		/*
		 * In this archive, all components are backdated, so they should have no effect on the dataset,
		 * except a new description 45527646019, which is unpublished and so should appear on the concept.
		 */
		importArchive("SnomedCT_Release_INT_20150131_index_init_bug.zip");

		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200)
		.body("active", equalTo(false))
		.body("pt.id", equalTo("11320138110"));

		getComponent(branchPath, SnomedComponentType.DESCRIPTION, "45527646019").statusCode(200);
	}

	@Test
	public void import10InvalidBranchPath() {
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", "MAIN/x/y/z")
				.put("createVersions", false)
				.build();

		createImport(importConfiguration).statusCode(404);
	}

	@Test
	public void import11ExtensionConceptWithVersion() {
		createCodeSystem(branchPath, "SNOMEDCT-NE").statusCode(201);
		getComponent(branchPath, SnomedComponentType.CONCEPT, "555231000005107").statusCode(404);

		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath", branchPath.getPath())
				.put("createVersions", true)
				.put("codeSystemShortName", "SNOMEDCT-NE")
				.build();

		importArchive("SnomedCT_Release_INT_20150205_new_extension_concept.zip", importConfiguration);
		getComponent(branchPath, SnomedComponentType.CONCEPT, "555231000005107").statusCode(200);
		getVersion("SNOMEDCT-NE", "2015-02-05").statusCode(200);
	}
	
	@Test
	public void importMoreThanOneLanguageCodeDescription() {
		final String enDescriptionId = "41320138114";
		final String svDescriptionId = "24688171113";
		final String conceptIdODescription = "301795004";
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, enDescriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, svDescriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptIdODescription).statusCode(404);
		
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath",  branchPath.getPath())
				.put("createVersions", false)
				.build();
		
		importArchive("SnomedCT_Release_INT_20150131_new_concept_for_languageCode.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptIdODescription).statusCode(200);
		
		importArchive("SnomedCT_Release_INT_20150201_new_description_with2_language_code.zip", importConfiguration);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, enDescriptionId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, svDescriptionId).statusCode(200);
	}
	
	@Test
	public void importFromMoreThanOneLanguageRefset() {
		final String enLanguageRefsetConceptId = "34d07985-48a0-41e7-b6ec-b28e6b00adfc";
		final String svLanguageRefsetConceptId = "34d07985-48a0-41e7-b6ec-b28e6b00adfb";
		final String conceptIdOfDescriptions = "301795004";
		
		getComponent(branchPath, SnomedComponentType.MEMBER, enLanguageRefsetConceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, svLanguageRefsetConceptId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptIdOfDescriptions).statusCode(404);
		
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("branchPath",  branchPath.getPath())
				.put("createVersions", false)
				.build();
		importArchive("SnomedCT_Release_INT_20150131_new_concept_for_languageCode.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, conceptIdOfDescriptions).statusCode(200);
		
		importArchive("SnomedCT_Release_INT_20150201_new_description_with2_language_code.zip", importConfiguration);
		getComponent(branchPath, SnomedComponentType.MEMBER, enLanguageRefsetConceptId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.MEMBER, svLanguageRefsetConceptId).statusCode(200);
	}
	
}
