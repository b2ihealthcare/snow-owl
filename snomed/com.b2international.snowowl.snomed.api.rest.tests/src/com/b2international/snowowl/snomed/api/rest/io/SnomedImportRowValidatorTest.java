/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.Rf2ImportResponse;
import com.b2international.snowowl.snomed.datastore.request.rf2.validation.Rf2ValidationDefects;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.Iterables;

/**
 * @since 7.0
 */
public class SnomedImportRowValidatorTest extends AbstractSnomedApiTest {
	
	private static final String REPOSITORY_ID = SnomedDatastoreActivator.REPOSITORY_UUID;
	private static String codeSystemShortName;
	private UUID archiveId;
	
	
	@BeforeClass
	public static void beforeClass() {
		codeSystemShortName = "SNOMEDCT-TEST";
	}
	
	@Before
	public void init() {
		archiveId = UUID.randomUUID();
		createCodeSystemIfNotExists("valami-699");
	}
	
	@Test
	public void importConceptWithDescriptionAsDefStatusId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150131_concept_with_desc_as_defStatusId.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		assertThat(issues).hasSize(1);
		assertEquals(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.getLabel(), Iterables.getOnlyElement(issues));
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	@Test
	public void importDescriptionWithDescriptionAsConceptId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150201_new_description_with_description_as_concept_id.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		assertThat(issues).hasSize(1);
		assertEquals(Rf2ValidationDefects.UNEXPECTED_COMPONENT_CATEGORY.getLabel(), Iterables.getOnlyElement(issues));
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	@Test
	public void importRelationshipWithSameSourceDestination() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_Release_INT_20150202_new_relationship_with_same_source_destination.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.RELATIONSHIP_SOURCE_DESTINATION_EQUALS.getLabel());
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	@Test
	public void importReferenceSetWithNonUUID() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_member_with_non_uuid.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_UUID.getLabel());
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	@Test
	public void importAssociationRefSetMemberWithInvalidTargetComponent() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_association_member_with_invalid_target_component.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_ID.getLabel());
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	@Test
	public void importAttributeValueRefSetMemberWithInvalidValueId() throws FileNotFoundException {
		final String archiveFilePath = "SnomedCT_RF2Release_INT_20180223_attribute_member_with_invalid_valueId.zip";
		final Rf2ImportResponse response = importArchive(archiveFilePath, Rf2ReleaseType.DELTA);
		
		final Collection<String> issues = response.getIssues();
		final String issue = Iterables.getOnlyElement(issues);
		assertThat(issues).hasSize(1);
		assertThat(issue).containsSequence(Rf2ValidationDefects.INVALID_ID.getLabel());
		assertEquals(ImportStatus.FAILED, response.getStatus());
	}
	
	private void createCodeSystemIfNotExists(final String oid) {
		try {
			CodeSystemRequests.prepareGetCodeSystem(codeSystemShortName)
				.build(REPOSITORY_ID)
				.execute(getBus())
				.getSync();
			
		} catch (NotFoundException e) {
			CodeSystemRequests.prepareNewCodeSystem()
				.setShortName(codeSystemShortName)
				.setOid(oid)
				.setName(String.format("%s - %s", codeSystemShortName, oid))
				.setLanguage("en")
				.setBranchPath(branchPath.getPath())
				.setCitation("citation")
				.setIconPath("snomed.png")
				.setRepositoryUuid(REPOSITORY_ID)
				.setTerminologyId("concept")
				.setLink("www.ihtsdo.org")
				.build(REPOSITORY_ID, branchPath.getPath(), "system", String.format("New code system %s", codeSystemShortName))
				.execute(getBus())
				.getSync();
				
		}
		
	}
	
	private Rf2ImportResponse importArchive(String archiveFilePath, Rf2ReleaseType releaseType) throws FileNotFoundException {
		final File importArchive = new File(PlatformUtil.toAbsolutePath(this.getClass(), archiveFilePath));
		ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(archiveId, new FileInputStream(importArchive));
		
		return SnomedRequests.rf2().prepareImport()
			.setCodeSystemShortName(codeSystemShortName)
			.setCreateVersions(false)
			.setUserId("info@b2international.com")
			.setReleaseType(releaseType)
			.setRf2ArchiveId(archiveId)
			.build(REPOSITORY_ID, branchPath.getPath())
			.execute(getBus())
			.getSync();
	}

}
