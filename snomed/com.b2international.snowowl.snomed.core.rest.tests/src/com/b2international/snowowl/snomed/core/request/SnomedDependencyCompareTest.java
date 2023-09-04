/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.compare.AnalysisCompareResultItem;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.test.commons.Services;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.rest.RestExtensions;
import com.google.common.collect.ImmutableMap;

/**
 * @since 9.0
 */
public class SnomedDependencyCompareTest {

	private static final String CODE_SYSTEM_ID = "SNOMEDCT-DEP";

	private static IBranchPath branchPath;

	@BeforeClass
	public static void createCodeSystem() {

		try {

			CodeSystemRequests.prepareNewCodeSystem()
				.setId(CODE_SYSTEM_ID)
				.setUrl(String.format("http://snomed.info/sct/%s", CODE_SYSTEM_ID))
				.setTitle(String.format(CODE_SYSTEM_ID))
				.setLanguage("en")
				.setDescription("citation")
				.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
				.setDependencies(Dependency.of(SnomedContentRule.SNOMEDCT.withPath("2021-01-31"), TerminologyResource.DependencyScope.EXTENSION_OF))
				.build(RestExtensions.USER, "Created new code system")
				.execute(Services.bus())
				.getSync();

		} catch (final AlreadyExistsException ignored) {
			// Nothing to do
		}

		branchPath = BranchPathUtils.createPath(CodeSystemRequests.prepareGetCodeSystem(CODE_SYSTEM_ID)
			.buildAsync()
			.execute(Services.bus())
			.getSync()
			.getBranchPath());
	}

	@Test
	public void testMissingFromUri() {
		final BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			ResourceRequests.prepareCompareDependency()
				// .setFromUri(...)
				.setToUri(CodeSystem.uri(CODE_SYSTEM_ID))
				// .setIncludeChanges(...)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		});

		assertThat(exception.getMessage()).isEqualTo("1 validation error");
		assertThat(exception.getAdditionalInfo().get("violations"))
			.asList()
			.containsExactly("'fromUri' may not be null (was 'null')");
	}

	@Test
	public void testMissingToUri() {
		final BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			ResourceRequests.prepareCompareDependency()
				.setFromUri(CodeSystem.uri(CODE_SYSTEM_ID))
				// .setToUri(...)
				// .setIncludeChanges(...)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		});

		assertThat(exception.getMessage()).isEqualTo("1 validation error");
		assertThat(exception.getAdditionalInfo().get("violations"))
			.asList()
			.containsExactly("'toUri' may not be null (was 'null')");
	}

	@Test
	public void testNoCommonResourceId() {
		final ResourceURI fromUri = CodeSystem.uri(CODE_SYSTEM_ID);
		final ResourceURI toUri = SnomedContentRule.SNOMEDCT;

		final BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			ResourceRequests.prepareCompareDependency()
				.setFromUri(fromUri)
				.setToUri(toUri)
				// .setIncludeChanges(...)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		});

		assertThat(exception.getMessage())
			.isEqualTo("Resource URIs should have a common root, got '%s' and '%s'", fromUri, toUri);
	}

	@Test
	public void testNonExistentResource() {
		final ResourceURI fromUri = CodeSystem.uri("SNOMEDCT-NOP", "v1");
		final ResourceURI toUri = CodeSystem.uri("SNOMEDCT-NOP", "v2");

		final NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			ResourceRequests.prepareCompareDependency()
				.setFromUri(fromUri)
				.setToUri(toUri)
				// .setIncludeChanges(...)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		});

		assertThat(exception.getMessage())
			.isEqualTo("Resource with identifier '%s' could not be found.", fromUri.getResourceId());
	}

	@Test
	public void testCompareBundles() {
		final String bundleId = IDs.base62UUID();

		ResourceRequests.bundles()
			.prepareCreate()
			.setId(bundleId)
			.setUrl("https://b2ihealthcare.com/bundles/" + bundleId)
			.setTitle("Test Bundle")
			.build(RestExtensions.USER, "Created bundle 'Test bundle'")
			.execute(Services.bus())
			.getSync();

		final ResourceURI fromUri = Bundle.uri(bundleId, "v1");
		final ResourceURI toUri = Bundle.uri(bundleId, "v2");

		final BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			ResourceRequests.prepareCompareDependency()
				.setFromUri(fromUri)
				.setToUri(toUri)
				// .setIncludeChanges(...)
				.buildAsync()
				.execute(Services.bus())
				.getSync();
		});

		assertThat(exception.getMessage())
			.isEqualTo("Only terminology resources are supported, got '%s'", Bundle.class.getSimpleName());
	}

	@Test
	public void testEmptyCompare() {
		final ResourceURI codeSystemUri = CodeSystem.uri(CODE_SYSTEM_ID);

		final ResourceURI fromUri = createVersion(codeSystemUri);
		// No change between versions
		final ResourceURI toUri = createVersion(codeSystemUri);

		final AnalysisCompareResult compareResult = ResourceRequests.prepareCompareDependency()
			.setFromUri(fromUri)
			.setToUri(toUri)
			// .setIncludeChanges(...)
			.buildAsync()
			.execute(Services.bus())
			.getSync();

		assertThat(compareResult.getFromUri().getResourceUri()).isEqualTo(fromUri);
		assertThat(compareResult.getToUri().getResourceUri()).isEqualTo(toUri);
		assertThat(compareResult.getItems()).isEmpty();
		assertThat(compareResult.getNewComponents()).isZero();
		assertThat(compareResult.getChangedComponents()).isZero();
		assertThat(compareResult.getDeletedComponents()).isZero();
	}

	@Test
	public void testAddedConcept() {
		final ResourceURI codeSystemUri = CodeSystem.uri(CODE_SYSTEM_ID);

		final ResourceURI fromUri = createVersion(codeSystemUri);
		createNewConcept(branchPath);
		final ResourceURI toUri = createVersion(codeSystemUri);

		final AnalysisCompareResult compareResult = ResourceRequests.prepareCompareDependency()
			.setFromUri(fromUri)
			.setToUri(toUri)
			// .setIncludeChanges(...)
			.buildAsync()
			.execute(Services.bus())
			.getSync();

		assertThat(compareResult.getFromUri().getResourceUri()).isEqualTo(fromUri);
		assertThat(compareResult.getToUri().getResourceUri()).isEqualTo(toUri);
		assertThat(compareResult.getItems()).isEmpty();
		assertThat(compareResult.getNewComponents()).isOne();
		assertThat(compareResult.getChangedComponents()).isZero();
		assertThat(compareResult.getDeletedComponents()).isZero();
	}

	@Test
	public void testChanged_Concept() {
		final ResourceURI codeSystemUri = CodeSystem.uri(CODE_SYSTEM_ID);

		final String hasDefinitionStatusChange = createNewConcept(branchPath);

		final ResourceURI fromUri = createVersion(codeSystemUri);
		changeToDefining(branchPath, hasDefinitionStatusChange);
		final ResourceURI toUri = createVersion(codeSystemUri);

		final AnalysisCompareResult compareResult = ResourceRequests.prepareCompareDependency()
			.setFromUri(fromUri)
			.setToUri(toUri)
			.setIncludeChanges(true)
			.buildAsync()
			.execute(Services.bus())
			.getSync();

		assertThat(compareResult.getFromUri().getResourceUri()).isEqualTo(fromUri);
		assertThat(compareResult.getToUri().getResourceUri()).isEqualTo(toUri);

		assertThat(compareResult.getItems())
			.extracting(AnalysisCompareResultItem::id) 
			.containsExactlyInAnyOrder(hasDefinitionStatusChange);

		assertThat(compareResult.getNewComponents()).isZero();
		assertThat(compareResult.getChangedComponents()).isEqualTo(1);
		assertThat(compareResult.getDeletedComponents()).isZero();
	}

	@Test
	public void testChanged_NewCoreComponent() {
		final ResourceURI codeSystemUri = CodeSystem.uri(CODE_SYSTEM_ID);

		final String hasNewDescription = createNewConcept(branchPath);
		final String hasNewRelationship = createNewConcept(branchPath);
		final String hasNewAxiom = createNewConcept(branchPath);
		final String hasNewLanguageMember = createNewConcept(branchPath);

		final String simpleTypeRefset = createNewRefSet(branchPath);
		final String shouldNotAppearInChanges = createNewConcept(branchPath);

		final ResourceURI fromUri = createVersion(codeSystemUri);

		createNewDescription(branchPath, hasNewDescription);
		createNewRelationship(branchPath, hasNewRelationship, Concepts.HAS_ACTIVE_INGREDIENT, Concepts.SUBSTANCE);
		createNewRefSetMember(branchPath, hasNewAxiom, Concepts.REFSET_OWL_AXIOM, ImmutableMap.of(
			SnomedRf2Headers.FIELD_OWL_EXPRESSION, "SubClassOf(:" + hasNewAxiom + " :" + Concepts.ROOT_CONCEPT + ")"
		));

		createNewRefSetMember(branchPath, shouldNotAppearInChanges, simpleTypeRefset);

		final SnomedDescription synonym = SnomedRequests.prepareSearchDescription()
			.filterByActive(true)
			.filterByConcept(hasNewLanguageMember)
			.filterByType(Concepts.SYNONYM)
			.setLimit(1)
			.build(codeSystemUri)
			.execute(Services.bus())
			.getSync()
			.first()
			.get();

		createNewLanguageRefSetMember(branchPath, synonym.getId(), Concepts.REFSET_LANGUAGE_TYPE_US, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);

		final ResourceURI toUri = createVersion(codeSystemUri);

		final AnalysisCompareResult compareResult = ResourceRequests.prepareCompareDependency()
			.setFromUri(fromUri)
			.setToUri(toUri)
			.setIncludeChanges(true)
			.buildAsync()
			.execute(Services.bus())
			.getSync();

		assertThat(compareResult.getFromUri().getResourceUri()).isEqualTo(fromUri);
		assertThat(compareResult.getToUri().getResourceUri()).isEqualTo(toUri);

		assertThat(compareResult.getItems())
			.extracting(AnalysisCompareResultItem::id) 
			.containsExactlyInAnyOrder(hasNewDescription, hasNewRelationship, hasNewAxiom, hasNewLanguageMember);

		assertThat(compareResult.getNewComponents()).isZero();
		assertThat(compareResult.getChangedComponents()).isEqualTo(4);
		assertThat(compareResult.getDeletedComponents()).isZero();
	}

	@Test
	public void testChanged_ChangedCoreComponent() {
		final ResourceURI codeSystemUri = CodeSystem.uri(CODE_SYSTEM_ID);
		
		final String hasChangedDescription = createNewConcept(branchPath);
		final String hasChangedRelationship = createNewConcept(branchPath);
		final String hasChangedAxiom = createNewConcept(branchPath);
		final String hasChangedLanguageMember = createNewConcept(branchPath);
		
		final String simpleTypeRefset = createNewRefSet(branchPath);
		final String shouldNotAppearInChanges = createNewConcept(branchPath);
		
		final String changedDescriptionId = createNewDescription(branchPath, hasChangedDescription);
		final String changedRelationshipId = createNewRelationship(branchPath, hasChangedRelationship, Concepts.HAS_ACTIVE_INGREDIENT, Concepts.SUBSTANCE);
		final String changedAxiomId = createNewRefSetMember(branchPath, hasChangedAxiom, Concepts.REFSET_OWL_AXIOM, ImmutableMap.of(
			SnomedRf2Headers.FIELD_OWL_EXPRESSION, "SubClassOf(:" + hasChangedAxiom + " :" + Concepts.ROOT_CONCEPT + ")"
		));
		
		final SnomedDescription synonym = SnomedRequests.prepareSearchDescription()
			.filterByActive(true)
			.filterByConcept(hasChangedLanguageMember)
			.filterByType(Concepts.SYNONYM)
			.setLimit(1)
			.build(codeSystemUri)
			.execute(Services.bus())
			.getSync()
			.first()
			.get();
		
		final String changedLanguageMemberId = createNewLanguageRefSetMember(branchPath, synonym.getId(), Concepts.REFSET_LANGUAGE_TYPE_US, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
		final String changedSimpleMemberId = createNewRefSetMember(branchPath, shouldNotAppearInChanges, simpleTypeRefset);
		
		final ResourceURI fromUri = createVersion(codeSystemUri);
		
		inactivateDescription(branchPath, changedDescriptionId);
		inactivateRelationship(branchPath, changedRelationshipId);
		inactivateMember(branchPath, changedAxiomId);
		inactivateMember(branchPath, changedLanguageMemberId);
		inactivateMember(branchPath, changedSimpleMemberId);
		
		final ResourceURI toUri = createVersion(codeSystemUri);
		
		final AnalysisCompareResult compareResult = ResourceRequests.prepareCompareDependency()
			.setFromUri(fromUri)
			.setToUri(toUri)
			.setIncludeChanges(true)
			.buildAsync()
			.execute(Services.bus())
			.getSync();
		
		assertThat(compareResult.getFromUri().getResourceUri()).isEqualTo(fromUri);
		assertThat(compareResult.getToUri().getResourceUri()).isEqualTo(toUri);
		
		assertThat(compareResult.getItems())
			.extracting(AnalysisCompareResultItem::id) 
			.containsExactlyInAnyOrder(hasChangedDescription, hasChangedRelationship, hasChangedAxiom, hasChangedLanguageMember);
		
		assertThat(compareResult.getNewComponents()).isZero();
		assertThat(compareResult.getChangedComponents()).isEqualTo(4);
		assertThat(compareResult.getDeletedComponents()).isZero();
	}
	
	private static ResourceURI createVersion(final ResourceURI codeSystemUri) {
		final LocalDate nextDate = ResourceRequests.prepareSearchVersion()
			.filterByResource(codeSystemUri)
			.setLimit(1)
			.sortBy("createdAt:desc")
			.buildAsync()
			.execute(Services.bus())
			.getSync()
			.first()
			.map(Version::getEffectiveTime)
			.orElse(LocalDate.now())
			.plusDays(1);

		final String versionId = nextDate.toString();
		final String jobId = ResourceRequests.prepareNewVersion()
			.setResource(codeSystemUri)
			.setVersion(versionId)
			.setDescription(versionId)
			.setEffectiveTime(nextDate)
			.setAuthor(RestExtensions.USER)
			.setCommitComment(String.format("Created version '%s'", versionId))
			.buildAsync()
			.runAsJob(String.format("Creating code system version '%s'", codeSystemUri.withPath(versionId).withoutResourceType()))
			.execute(Services.bus())
			.getSync();

		JobRequests.waitForJob(Services.bus(), jobId);
		return codeSystemUri.withPath(versionId); 
	}
}
