/*
 * Copyright 2018-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.request.io.ImportDefect;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.util.PlatformUtil;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Iterables;

/**
 * @since 7.0
 */
public class SnomedImportRowValidatorTest extends AbstractSnomedApiTest {
	
	// TODO: Implement tests for every reference set type
	
	@Test
	public void importConceptWithDescriptionAsDefStatusId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150131_concept_with_desc_as_defStatusId.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		assertThat(issues).hasSize(1);
		assertEquals(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.format(ComponentCategory.CONCEPT, ComponentCategory.DESCRIPTION, "11320138110"), Iterables.getOnlyElement(issues));
		assertFalse(response.isSuccess());
	}
	
	@Test
	public void importDescriptionWithDescriptionAsConceptId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150201_new_description_with_description_as_concept_id.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		assertThat(issues).hasSize(1);
		assertEquals(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.format(ComponentCategory.CONCEPT, ComponentCategory.DESCRIPTION, "11000172113"), Iterables.getOnlyElement(issues));
		assertFalse(response.isSuccess());
	}
	
	@Test
	public void importRelationshipWithSameSourceDestination() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150202_new_relationship_with_same_source_destination.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.RELATIONSHIP_SOURCE_DESTINATION_EQUALS.getLabel());
		assertFalse(response.isSuccess());
	}
	
	@Test
	public void importReferenceSetWithNonUUID() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_member_with_non_uuid.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_UUID.getLabel());
		assertFalse(response.isSuccess());
	}
	
	@Test
	public void importAssociationRefSetMemberWithInvalidTargetComponent() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_association_member_with_invalid_target_component.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_ID.getLabel());
		assertFalse(response.isSuccess());
	}
	
	@Test
	public void importAttributeValueRefSetMemberWithInvalidValueId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_attribute_member_with_invalid_valueId.zip";
		final ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = getDefectMessages(response);
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_ID.getLabel());
		assertFalse(response.isSuccess());
	}
	
	private ImportResponse importArchive(String archiveFilePath, Rf2ReleaseType releaseType) throws FileNotFoundException {

		final String codeSystemId = branchPath.lastSegment();
		
		try {
			CodeSystemRequests.prepareGetCodeSystem(codeSystemId)
				.buildAsync()
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);
			
		} catch (NotFoundException e) {
			CodeSystemRequests.prepareNewCodeSystem()
				.setBranchPath(branchPath.getPath())
				.setId(codeSystemId)
				.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
				.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + codeSystemId)
				.setTitle(codeSystemId)
				.build("info@b2ihealthcare.com", "Created new code system " + codeSystemId)
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);
		}
		
		Attachment attachment = Attachment.upload(Services.context(), PlatformUtil.toAbsolutePath(this.getClass(), archiveFilePath));
		return SnomedRequests.rf2().prepareImport()
			.setCreateVersions(false)
			.setReleaseType(releaseType)
			.setRf2Archive(attachment)
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
	}

	private Collection<String> getDefectMessages(ImportResponse response) {
		return response.getDefects()
			.stream()
			.map(ImportDefect::getMessage)
			.collect(Collectors.toList());
	}
}
