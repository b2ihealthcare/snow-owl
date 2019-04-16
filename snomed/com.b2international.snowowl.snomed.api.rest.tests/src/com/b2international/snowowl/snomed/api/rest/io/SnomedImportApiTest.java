/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedBranchingRestRequests;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.google.common.collect.ImmutableMap;

import io.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedImportApiTest extends AbstractSnomedApiTest {

	private static final String OWL_EXPRESSION = "SubClassOf(ObjectIntersectionOf(:73211009 ObjectSomeValuesFrom(:42752001 :64572001)) :8801005)";

	private void importArchive(final String fileName) {
		importArchive(fileName, branchPath, false, Rf2ReleaseType.DELTA);
	}
	
	private void importArchive(String fileName, IBranchPath path, boolean createVersion, Rf2ReleaseType releaseType) {
		
		final Map<?, ?> importConfiguration = ImmutableMap.builder()
				.put("type", releaseType.name())
				.put("branchPath", path.getPath())
				.put("createVersions", createVersion)
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
		
		SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()")
				.statusCode(200)
				.body("pt.id", equalTo("13809498114"))
				.extract().as(SnomedConcept.class);
		
		// assert proper parent/ancestor array updates
		
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getParentIds());
		assertArrayEquals(new long[0], concept.getAncestorIds());
		assertArrayEquals(new long[] { Long.valueOf(Concepts.ROOT_CONCEPT) }, concept.getStatedParentIds());
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getStatedAncestorIds());
		
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
	public void import12OnlyPubContentWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_with_effective_time.zip", true);
	}

	@Test
	public void import13OnlyPubContentWithOutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_with_effective_time.zip", false);
	}

	@Test
	public void import14PubAndUnpubContentWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_w_and_wo_effective_time.zip", true);
	}

	@Test
	public void import15PubAndUnpubContentWithOutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_w_and_wo_effective_time.zip", false);
	}

	@Test
	public void import16OnlyUnpubContentWithoutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_without_effective_time.zip", false);
	}

	@Test
	public void import17OnlyUnpubContentWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_without_effective_time.zip", true);
	}

	@Test
	public void import18OnlyPubRefsetMembersWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_effective_time.zip", true);
	}

	@Test
	public void import19OnlyPubRefsetMembersWithoutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_effective_time.zip", false);
	}

	@Test
	public void import20PubAndUnpubRefsetMembersWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_and_wo_effective_time.zip", true);
	}

	@Test
	public void import21PubAndUnpubRefsetMembersWithoutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_and_wo_effective_time.zip", false);
	}

	@Test
	public void import22OnlyUnpubRefsetMembersWithoutVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_wo_effective_time.zip", false);
	}

	@Test
	public void import23OnlyUnpubRefsetMembersWithVersioning() {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_wo_effective_time.zip", true);
	}

	@Test
	public void import24IncompleteTaxonomyMustBeImported() {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "882169191000154107").statusCode(404);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "955630781000154129").statusCode(404);
		importArchive("SnomedCT_RF2Release_INT_20180227_incomplete_taxonomy.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "882169191000154107").statusCode(200);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "955630781000154129").statusCode(200);
	}

	@Test
	public void import25WithMultipleLanguageCodes() {
		final String enDescriptionId = "41320138114";
		final String svDescriptionId = "24688171113";
		final String enLanguageRefsetMemberId = "34d07985-48a0-41e7-b6ec-b28e6b00adfc";
		final String svLanguageRefsetMemberId = "34d07985-48a0-41e7-b6ec-b28e6b00adfb";
		
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, enDescriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, svDescriptionId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, enLanguageRefsetMemberId).statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, svLanguageRefsetMemberId).statusCode(404);
		importArchive("SnomedCT_Release_INT_20150201_descriptions_with_multiple_language_codes.zip");
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, enDescriptionId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, svDescriptionId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.MEMBER, enLanguageRefsetMemberId).statusCode(200);
		getComponent(branchPath, SnomedComponentType.MEMBER, svLanguageRefsetMemberId).statusCode(200);
	}
	
	@Test
	public void import26OWLExpressionReferenceSetMembers() {
		
		SnomedConcept oldRoot = getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "members()").extract().as(SnomedConcept.class);
		assertTrue(oldRoot.getMembers().getItems().stream()
			.noneMatch(m -> m.getReferenceSetId().equals(Concepts.REFSET_OWL_AXIOM) || m.getReferenceSetId().equals(Concepts.REFSET_OWL_ONTOLOGY)));
		
		importArchive("SnomedCT_Release_INT_20170731_new_owl_expression_members.zip");
		SnomedConcept root = getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "members()").extract().as(SnomedConcept.class);
		
		Optional<SnomedReferenceSetMember> axiomMember = root.getMembers().getItems().stream()
			.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_OWL_AXIOM))
			.findFirst();
		
		assertTrue(axiomMember.isPresent());
		assertEquals("ec2cc6be-a10b-44b1-a2cc-42a3f11d406e", axiomMember.get().getId());
		assertEquals(Concepts.MODULE_SCT_CORE, axiomMember.get().getModuleId());
		assertEquals(Concepts.REFSET_OWL_AXIOM, axiomMember.get().getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, axiomMember.get().getReferencedComponent().getId());
		assertEquals(OWL_EXPRESSION, axiomMember.get().getProperties().get(SnomedRf2Headers.FIELD_OWL_EXPRESSION));
		
		Optional<SnomedReferenceSetMember> ontologyMember = root.getMembers().getItems().stream()
				.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_OWL_ONTOLOGY))
				.findFirst();
			
		assertTrue(ontologyMember.isPresent());
		assertEquals("f81c24fb-c40a-4b28-9adb-85f748f71395", ontologyMember.get().getId());
		assertEquals(Concepts.MODULE_SCT_CORE, ontologyMember.get().getModuleId());
		assertEquals(Concepts.REFSET_OWL_ONTOLOGY, ontologyMember.get().getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, ontologyMember.get().getReferencedComponent().getId());
		assertEquals("Ontology(<http://snomed.info/sct/900000000000207008>)", ontologyMember.get().getProperties().get(SnomedRf2Headers.FIELD_OWL_EXPRESSION));
	}

	@Test
	public void import27MRCMReferenceSetMembers() {
		
		SnomedConcept rootConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "members()")
				.extract()
				.as(SnomedConcept.class);
		
		assertTrue(StreamSupport.stream(rootConcept.getMembers().spliterator(), false).noneMatch(m -> {
			return m.getReferenceSetId().equals(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL) ||
			m.getReferenceSetId().equals(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL) ||
			m.getReferenceSetId().equals(Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL) ||
			m.getReferenceSetId().equals(Concepts.REFSET_MRCM_MODULE_SCOPE);
		}));
		
		importArchive("SnomedCT_Release_INT_20170731_new_mrcm_members.zip");
		
		SnomedConcept newRootConcept = getComponent(branchPath, SnomedComponentType.CONCEPT, Concepts.ROOT_CONCEPT, "members()")
				.extract()
				.as(SnomedConcept.class);
		
		Optional<SnomedReferenceSetMember> mrcmDomainMemberCandidate = StreamSupport.stream(newRootConcept.getMembers().spliterator(), false)
			.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL))
			.findFirst();
		
		assertTrue(mrcmDomainMemberCandidate.isPresent());
		SnomedReferenceSetMember mrcmDomainMember = mrcmDomainMemberCandidate.get();
		
		assertEquals("28ecaa32-8f0e-4ff8-b6b1-b642e40519d8", mrcmDomainMember.getId());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, mrcmDomainMember.getModuleId());
		assertEquals(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL, mrcmDomainMember.getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, mrcmDomainMember.getReferencedComponent().getId());
		Map<String, Object> domainMemberProps = mrcmDomainMember.getProperties();
		assertEquals("domainConstraint", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT));
		assertEquals("parentDomain", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN));
		assertEquals("proximalPrimitiveConstraint", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT));
		assertEquals("proximalPrimitiveRefinement", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT));
		assertEquals("domainTemplateForPrecoordination", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION));
		assertEquals("domainTemplateForPostcoordination", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION));
		assertEquals("guideURL", domainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE));
		
		Optional<SnomedReferenceSetMember> mrcmAttributeDomainMemberCandidate = StreamSupport.stream(newRootConcept.getMembers().spliterator(), false)
				.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL))
				.findFirst();
			
		assertTrue(mrcmAttributeDomainMemberCandidate.isPresent());
		SnomedReferenceSetMember mrcmAttributeDomainMember = mrcmAttributeDomainMemberCandidate.get();
		
		assertEquals("126bf3f1-4f34-439d-ba0a-a832824d072a", mrcmAttributeDomainMember.getId());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, mrcmAttributeDomainMember.getModuleId());
		assertEquals(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL, mrcmAttributeDomainMember.getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, mrcmAttributeDomainMember.getReferencedComponent().getId());
		Map<String, Object> attributeDomainMemberProps = mrcmAttributeDomainMember.getProperties();
		assertEquals(Concepts.ROOT_CONCEPT, attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID));
		assertEquals(Boolean.TRUE, attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_GROUPED));
		assertEquals("attributeCardinality", attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY));
		assertEquals("attributeInGroupCardinality", attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY));
		assertEquals(Concepts.ROOT_CONCEPT, attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID));
		assertEquals(Concepts.ROOT_CONCEPT, attributeDomainMemberProps.get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID));
		
		Optional<SnomedReferenceSetMember> mrcmAttributeRangeMemberCandidate = StreamSupport.stream(newRootConcept.getMembers().spliterator(), false)
				.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL))
				.findFirst();
			
		assertTrue(mrcmAttributeRangeMemberCandidate.isPresent());
		SnomedReferenceSetMember mrcmAttributeRangeMember = mrcmAttributeRangeMemberCandidate.get();
		
		assertEquals("ae090cc3-2827-4e39-80c6-364435d30c17", mrcmAttributeRangeMember.getId());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, mrcmAttributeRangeMember.getModuleId());
		assertEquals(Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL, mrcmAttributeRangeMember.getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, mrcmAttributeRangeMember.getReferencedComponent().getId());
		Map<String, Object> attributeRangeMemberProps = mrcmAttributeRangeMember.getProperties();
		assertEquals("rangeConstraint", attributeRangeMemberProps.get(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT));
		assertEquals("attributeRule", attributeRangeMemberProps.get(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_RULE));
		assertEquals(Concepts.ROOT_CONCEPT, attributeRangeMemberProps.get(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID));
		assertEquals(Concepts.ROOT_CONCEPT, attributeRangeMemberProps.get(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID));
		
		Optional<SnomedReferenceSetMember> mrcmModuleScopeMemberCandidate = StreamSupport.stream(newRootConcept.getMembers().spliterator(), false)
				.filter(m -> m.getReferenceSetId().equals(Concepts.REFSET_MRCM_MODULE_SCOPE))
				.findFirst();
			
		assertTrue(mrcmModuleScopeMemberCandidate.isPresent());
		SnomedReferenceSetMember mrmcModuleScopeMember = mrcmModuleScopeMemberCandidate.get();
		
		assertEquals("52d29f1b-f7a3-4a0f-828c-383c6259c3f5", mrmcModuleScopeMember.getId());
		assertEquals(Concepts.MODULE_SCT_MODEL_COMPONENT, mrmcModuleScopeMember.getModuleId());
		assertEquals(Concepts.REFSET_MRCM_MODULE_SCOPE, mrmcModuleScopeMember.getReferenceSetId());
		assertEquals(Concepts.ROOT_CONCEPT, mrmcModuleScopeMember.getReferencedComponent().getId());
		assertEquals(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL, mrmcModuleScopeMember.getProperties().get(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID));
	}

	@Test
	public void import28ImportConceptAsInactive() {
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(404);
		
		importArchive("SnomedCT_Release_INT_20150131_concept_as_inactive.zip");

		SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getParentIds());
		assertArrayEquals(new long[0], concept.getAncestorIds());
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getStatedParentIds());
		assertArrayEquals(new long[0], concept.getStatedAncestorIds());
		
	}
	
	private void validateBranchHeadtimestampUpdate(IBranchPath branch, String importArchiveFileName,
			boolean createVersions) {

		ValidatableResponse response = SnomedBranchingRestRequests.getBranch(branch);

		String baseTimestamp = response.extract().jsonPath().getString("baseTimestamp");
		String headTimestamp = response.extract().jsonPath().getString("headTimestamp");

		assertNotNull(baseTimestamp);
		assertNotNull(headTimestamp);

		assertEquals("Base and head timestamp must be equal after branch creation", baseTimestamp, headTimestamp);

		importArchive(importArchiveFileName, branch, createVersions, Rf2ReleaseType.DELTA);

		ValidatableResponse response2 = SnomedBranchingRestRequests.getBranch(branch);

		String baseTimestampAfterImport = response2.extract().jsonPath().getString("baseTimestamp");
		String headTimestampAfterImport = response2.extract().jsonPath().getString("headTimestamp");

		assertNotNull(baseTimestampAfterImport);
		assertNotNull(headTimestampAfterImport);

		assertNotEquals("Base and head timestamp must differ after import", baseTimestampAfterImport,
				headTimestampAfterImport);
	}
	
}
