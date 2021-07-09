/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedImportRestRequests.doImport;
import static com.b2international.snowowl.snomed.core.rest.SnomedImportRestRequests.waitForImportJob;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemRestRequests.createCodeSystem;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.assertGetVersion;
import static com.b2international.snowowl.test.commons.codesystem.CodeSystemVersionRestRequests.createVersion;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.lastPathSegment;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.jobs.RemoteJobState;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedApiTestConstants;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;
import com.google.common.collect.ImmutableMap;

import io.restassured.response.ValidatableResponse;

/**
 * @since 2.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedImportApiTest extends AbstractSnomedApiTest {

	private static final String OWL_EXPRESSION = "SubClassOf(ObjectIntersectionOf(:73211009 ObjectSomeValuesFrom(:42752001 :64572001)) :"+Concepts.ROOT_CONCEPT+")";
	
	private void importArchive(final String fileName) {
		importArchive(branchPath, false, Rf2ReleaseType.DELTA, fileName);
	}
	
	private void importArchive(IBranchPath path, boolean createVersion, Rf2ReleaseType releaseType, final String fileName) {
		importArchive(path, Collections.emptyList(), createVersion, releaseType, fileName);
	}
	
	private void importArchive(IBranchPath path, List<String> ignoreMissingReferencesIn, boolean createVersion, Rf2ReleaseType releaseType, final String fileName) {
		final Map<String, ?> importConfiguration = ImmutableMap.<String, Object>builder()
				.put("type", releaseType.name())
				.put("createVersions", createVersion)
				.put("ignoreMissingReferencesIn", ignoreMissingReferencesIn)
				.build();
		importArchive(path, importConfiguration, fileName);
	}

	private void importArchive(final IBranchPath branchPath, Map<String, ?> importConfiguration, final String fileName) {
		final String importId = lastPathSegment(doImport(branchPath, importConfiguration, getClass(), fileName).statusCode(201)
				.extract().header("Location"));
		waitForImportJob(branchPath, importId).statusCode(200).body("status", equalTo(RemoteJobState.FINISHED.name()));
	}

	@Test
	public void import01InvalidBranchPath() throws Exception {
		final IBranchPath branchPath = BranchPathUtils.createPath("MAIN/notfound");
		final Map<String, ?> importConfiguration = ImmutableMap.<String, Object>builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("createVersions", false)
				.build();
		final String importId = lastPathSegment(doImport(branchPath, importConfiguration, getClass(), "SnomedCT_Release_INT_20150131_new_concept.zip").statusCode(201)
				.extract().header("Location"));
		waitForImportJob(branchPath, importId).statusCode(200).body("status", equalTo(RemoteJobState.FAILED.name()));
	}
	
	@Test
	public void import02DefaultsNoFileSpecified() {
		givenAuthenticatedRequest(SnomedApiTestConstants.SCT_API)
			.contentType("multipart/form-data")
			.post("/{path}/import", branchPath.toString())
			.then()
			.statusCode(400);
	}

	@Test
	public void import04NewConcept() throws Exception {
		
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
	public void import05NewDescription() throws Exception {
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
	public void import07NewPreferredTerm() throws Exception {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		importArchive("SnomedCT_Release_INT_20150131_new_concept.zip");
		importArchive("SnomedCT_Release_INT_20150201_new_description.zip");
		importArchive("SnomedCT_Release_INT_20150203_change_pt.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200).body("pt.id", equalTo("11320138110"));
	}

	@Test
	public void import08ConceptInactivation() throws Exception {
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
		createVersion("SNOMEDCT-EXT", "v1", EffectiveTimes.parse("20170301", DateFormats.SHORT)).statusCode(201);
		
		// sanity check that versioning did not mess with the descriptions
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200)
			.body("active", equalTo(false))
			.body("pt.id", equalTo("11320138110"));

		/*
		 * In this archive, all components are backdated, so they should have no effect on the dataset,
		 * except a new description 45527646019, which is unpublished and so should appear on the concept.
		 */
		importArchive("SnomedCT_Release_INT_20150131_index_init_bug.zip");

		// check that the new unpublished component did get imported
		getComponent(branchPath, SnomedComponentType.DESCRIPTION, "45527646019").statusCode(200);
		
		// verify that it did not change the PT of the concept
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103", "pt()").statusCode(200)
		.body("active", equalTo(false))
		.body("pt.id", equalTo("11320138110"));

	}

	@Test
	public void import11ExtensionConceptWithVersion() throws Exception {
		createCodeSystem(branchPath, "SNOMEDCT-NE").statusCode(201);
		getComponent(branchPath, SnomedComponentType.CONCEPT, "555231000005107").statusCode(404);

		final Map<String, ?> importConfiguration = ImmutableMap.<String, Object>builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("createVersions", true)
				.build();

		importArchive(branchPath, importConfiguration, "SnomedCT_Release_INT_20150205_new_extension_concept.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "555231000005107").statusCode(200);
		assertGetVersion("SNOMEDCT-NE", "2015-02-05").statusCode(200);
	}
	
	@Test
	public void import12OnlyPubContentWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_with_effective_time.zip", true);
	}

	@Test
	public void import13OnlyPubContentWithOutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_with_effective_time.zip", false);
	}

	@Test
	public void import14PubAndUnpubContentWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_w_and_wo_effective_time.zip", true);
	}

	@Test
	public void import15PubAndUnpubContentWithOutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_w_and_wo_effective_time.zip", false);
	}

	@Test
	public void import16OnlyUnpubContentWithoutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_without_effective_time.zip", false);
	}

	@Test
	public void import17OnlyUnpubContentWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_content_without_effective_time.zip", true);
	}

	@Test
	public void import18OnlyPubRefsetMembersWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_effective_time.zip", true);
	}

	@Test
	public void import19OnlyPubRefsetMembersWithoutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_effective_time.zip", false);
	}

	@Test
	public void import20PubAndUnpubRefsetMembersWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_and_wo_effective_time.zip", true);
	}

	@Test
	public void import21PubAndUnpubRefsetMembersWithoutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_w_and_wo_effective_time.zip", false);
	}

	@Test
	public void import22OnlyUnpubRefsetMembersWithoutVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_wo_effective_time.zip", false);
	}

	@Test
	public void import23OnlyUnpubRefsetMembersWithVersioning() throws Exception {
		validateBranchHeadtimestampUpdate(branchPath,
				"SnomedCT_RF2Release_INT_20180223_only_refset_wo_effective_time.zip", true);
	}

	@Test
	public void import24IncompleteTaxonomyMustBeImported() throws Exception {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "882169191000154107").statusCode(404);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "955630781000154129").statusCode(404);
		importArchive("SnomedCT_RF2Release_INT_20180227_incomplete_taxonomy.zip");
		getComponent(branchPath, SnomedComponentType.CONCEPT, "882169191000154107").statusCode(200);
		getComponent(branchPath, SnomedComponentType.RELATIONSHIP, "955630781000154129").statusCode(200);
	}

	@Test
	public void import25WithMultipleLanguageCodes() throws Exception {
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
	public void import26OWLExpressionReferenceSetMembers() throws Exception {
		
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
	public void import27MRCMReferenceSetMembers() throws Exception {
		
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
	public void import28ImportConceptAsInactive() throws Exception {
		
		getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(404);
		
		importArchive("SnomedCT_Release_INT_20150131_concept_as_inactive.zip");

		SnomedConcept concept = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getParentIds());
		assertArrayEquals(new long[0], concept.getAncestorIds());
		assertArrayEquals(new long[] { IComponent.ROOT_IDL }, concept.getStatedParentIds());
		assertArrayEquals(new long[0], concept.getStatedAncestorIds());
		
	}
	
	@Test
	public void import29ImportExistingConceptAsUnpublished() throws Exception {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(404);
		
		importArchive("SnomedCT_Release_INT_20210502_concept_w_eff_time.zip");
		SnomedConcept conceptBefore = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		importArchive("SnomedCT_Release_INT_20210502_concept_wo_eff_time.zip");
		SnomedConcept conceptAfter = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		assertEquals(EffectiveTimes.parse("2021-05-02"), conceptBefore.getEffectiveTime());
		assertTrue(conceptBefore.isReleased());
		assertEquals(EffectiveTimes.toDate(EffectiveTimes.UNSET_EFFECTIVE_TIME), conceptAfter.getEffectiveTime());
		assertTrue(conceptAfter.isReleased());
	}
	
	@Test
	public void import30ImportExistingConceptAsPublished() throws Exception {
		getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(404);
		
		importArchive("SnomedCT_Release_INT_20210502_concept_wo_eff_time.zip");
		SnomedConcept conceptBefore = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		importArchive("SnomedCT_Release_INT_20210502_concept_w_eff_time.zip");
		SnomedConcept conceptAfter = getComponent(branchPath, SnomedComponentType.CONCEPT, "100005").statusCode(200).extract().as(SnomedConcept.class);
		
		assertEquals(EffectiveTimes.toDate(EffectiveTimes.UNSET_EFFECTIVE_TIME), conceptBefore.getEffectiveTime());
		assertFalse(conceptBefore.isReleased());
		assertEquals(EffectiveTimes.parse("2021-05-02"), conceptAfter.getEffectiveTime());
		assertTrue(conceptAfter.isReleased());
	}
	
	@Test
	public void import31MissingComponentsWithSkip() throws Exception {		
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, "5312ec36-8baf-4768-8c4b-2d6f91094d4a").statusCode(404);
		importArchive(branchPath, Collections.singletonList("900000000000490003"), false, Rf2ReleaseType.DELTA, "SnomedCT_Release_INT_20150131_missing_component.zip");
		
		//Assert that concept is imported while member with missing reference is skipped when ignoreMissingReferencesIn lists its reference set
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(200);
		getComponent(branchPath, SnomedComponentType.MEMBER, "5312ec36-8baf-4768-8c4b-2d6f91094d4a").statusCode(200);
		getComponent(branchPath, SnomedComponentType.MEMBER, "5312ec36-8baf-4768-8c4b-2d6f91094d4b").statusCode(404);
	}
	
	@Test
	public void import32MissingComponentsWithoutSkip() throws Exception {
		final String importFileName = "SnomedCT_Release_INT_20150131_missing_component.zip";	
		importArchive(importFileName);
		
		//Assert that import fails if ignoreMissingReferencesIn is not set on a member referring to a missing component
		getComponent(branchPath, SnomedComponentType.CONCEPT, "63961392103").statusCode(404);
		getComponent(branchPath, SnomedComponentType.MEMBER, "5312ec36-8baf-4768-8c4b-2d6f91094d4b").statusCode(404);
	}
	
	@Test
	public void import33CreateVersionFromBranch() throws Exception {
		final Map<String, ?> importConfiguration = ImmutableMap.<String, Object>builder()
				.put("type", Rf2ReleaseType.DELTA.name())
				.put("createVersions", true)
				.build();
		final String importId = lastPathSegment(doImport(branchPath, importConfiguration, getClass(), "SnomedCT_Release_INT_20210502_concept_wo_eff_time.zip").statusCode(201)
				.extract().header("Location"));
		waitForImportJob(branchPath, importId).statusCode(200).body("status", equalTo(RemoteJobState.FAILED.name()));
	}
	
	private void validateBranchHeadtimestampUpdate(IBranchPath branch, String importArchiveFileName,
			boolean createVersions) {

		ValidatableResponse response = branching.getBranch(branch);

		String baseTimestamp = response.extract().jsonPath().getString("baseTimestamp");
		String headTimestamp = response.extract().jsonPath().getString("headTimestamp");

		assertNotNull(baseTimestamp);
		assertNotNull(headTimestamp);

		assertEquals("Base and head timestamp must be equal after branch creation", baseTimestamp, headTimestamp);

		if (createVersions) {
			// Create a code system with the test branch path as its working branch
			final String codeSystemId = branch.lastSegment();
			
			CodeSystemRequests.prepareNewCodeSystem()
				.setBranchPath(branch.getPath())
				.setId(codeSystemId)
				.setToolingId(SnomedTerminologyComponentConstants.TOOLING_ID)
				.setUrl(SnomedTerminologyComponentConstants.SNOMED_URI_SCT + "/" + codeSystemId)
				.setTitle(codeSystemId)
				.build("info@b2international.com", "Created new code system " + codeSystemId)
				.execute(getBus())
				.getSync(1L, TimeUnit.MINUTES);
		}
		
		importArchive(branch, createVersions, Rf2ReleaseType.DELTA, importArchiveFileName);

		ValidatableResponse response2 = branching.getBranch(branch);

		String baseTimestampAfterImport = response2.extract().jsonPath().getString("baseTimestamp");
		String headTimestampAfterImport = response2.extract().jsonPath().getString("headTimestamp");

		assertNotNull(baseTimestampAfterImport);
		assertNotNull(headTimestampAfterImport);

		assertNotEquals("Base and head timestamp must differ after import", baseTimestampAfterImport,
				headTimestampAfterImport);
	}
	
}
