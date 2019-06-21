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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class SnomedRefSetMemberDocumentSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedRefSetMemberIndexEntry.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	private Builder createBaseMember() {
		return SnomedRefSetMemberIndexEntry.builder()
			.id(UUID.randomUUID().toString())
			.active(true)
			.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.released(false)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.referencedComponentId(Concepts.ROOT_CONCEPT)
			.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}
	
	@Test
	public void indexSimpleMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
			.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
			.referenceSetType(SnomedRefSetType.ASSOCIATION)
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}

	@Test
	public void indexSimpleMapMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.SIMPLE_MAP)
				.field(Fields.MAP_TARGET, "A01")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexLanguageMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				// TODO use description ID in test case
				.referencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.field(Fields.ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexStringConcreteDomainMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.field(Fields.DATA_TYPE, DataType.STRING)
				.field(SnomedRf2Headers.FIELD_VALUE, "TEST")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("TEST", actual.getValue());
		assertDocEquals(member, actual);
		
		// verify that concrete domain members have only a single value field indexed
		final JsonNode json = getMapper().convertValue(member, JsonNode.class);
		assertNull(json.get(Fields.BOOLEAN_VALUE));
		assertNull(json.get(Fields.INTEGER_VALUE));
		assertNull(json.get(Fields.DECIMAL_VALUE));
	}
	
	@Test
	public void indexMRCMDomainMemberWithAllFields() throws Exception {
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_DOMAIN)
				.field(Fields.MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.field(Fields.MRCM_PARENT_DOMAIN, "parentDomain")
				.field(Fields.MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.field(Fields.MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, "proximalPrimitiveRefinement")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.field(Fields.MRCM_EDITORIAL_GUIDE_REFERENCE, "editorialGuideReference")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("domainConstraint", actual.getDomainConstraint());
		assertEquals("parentDomain", actual.getParentDomain());
		assertEquals("proximalPrimitiveConstraint", actual.getProximalPrimitiveConstraint());
		assertEquals("proximalPrimitiveRefinement", actual.getProximalPrimitiveRefinement());
		assertEquals("domainTemplateForPrecoordination", actual.getDomainTemplateForPrecoordination());
		assertEquals("domainTemplateForPostcoordination", actual.getDomainTemplateForPostcoordination());
		assertEquals("editorialGuideReference", actual.getEditorialGuideReference());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexMRCMDomainMemberWithMandatoryFields() throws Exception {
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_DOMAIN)
				.field(Fields.MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.field(Fields.MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("domainConstraint", actual.getDomainConstraint());
		assertEquals("proximalPrimitiveConstraint", actual.getProximalPrimitiveConstraint());
		assertEquals("domainTemplateForPrecoordination", actual.getDomainTemplateForPrecoordination());
		assertEquals("domainTemplateForPostcoordination", actual.getDomainTemplateForPostcoordination());
		assertDocEquals(member, actual);
		
		// verify that not mandatory members are empty
		final JsonNode json = getMapper().convertValue(member, JsonNode.class);
		assertNull(json.get(Fields.MRCM_PARENT_DOMAIN));
		assertNull(json.get(Fields.MRCM_PROXIMAL_PRIMITIVE_REFINEMENT));
		assertNull(json.get(Fields.MRCM_EDITORIAL_GUIDE_REFERENCE));
		
		// assert that isGrouped is not serialized with any valid value for this reference set member type
		assertNull(json.get(Fields.MRCM_GROUPED));
	}
	
	@Test
	public void indexMRCMAttributeDomainMember() throws Exception {
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
				.field(Fields.MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_GROUPED, Boolean.TRUE)
				.field(Fields.MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.field(Fields.MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.field(Fields.MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getDomainId());
		assertEquals(Boolean.TRUE, actual.isGrouped());
		assertEquals("attributeCardinality", actual.getAttributeCardinality());
		assertEquals("attributeInGroupCardinality", actual.getAttributeInGroupCardinality());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getRuleStrengthId());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getContentTypeId());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexMRCMAttributeRangeMember() throws Exception {
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE)
				.field(Fields.MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.field(Fields.MRCM_ATTRIBUTE_RULE, "attributeRule")
				.field(Fields.MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("rangeConstraint", actual.getRangeConstraint());
		assertEquals("attributeRule", actual.getAttributeRule());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getRuleStrengthId());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getContentTypeId());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexMRCMModuleScopeMember() throws Exception {
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referenceSetId(Concepts.REFSET_MRCM_MODULE_SCOPE)
				.referenceSetType(SnomedRefSetType.MRCM_MODULE_SCOPE)
				.field(Fields.MRCM_RULE_REFSET_ID, "mrcmRuleRefsetId")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("mrcmRuleRefsetId", actual.getMrcmRuleRefsetId());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexOWLAxiomMember_ISA() throws Exception {
		final String referencedComponentId = "410607006";
		final String owlExpression = "SubClassOf(:410607006 :138875005)";
		final SnomedOWLExpressionConverterResult owlRelationships = toSnomedOWLRelationships(referencedComponentId, owlExpression);
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referencedComponentId(referencedComponentId)
				.referenceSetId(Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.field(Fields.OWL_EXPRESSION, owlExpression)
				.classAxiomRelationships(owlRelationships.getClassAxiomRelationships())
				.gciAxiomRelationships(owlRelationships.getGciAxiomRelationships())
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(owlExpression, actual.getOwlExpression());
		assertEquals(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, Concepts.ROOT_CONCEPT, 0)), actual.getClassAxiomRelationships());
		assertThat(actual.getGciAxiomRelationships()).isEmpty();
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexOWLAxiomMember_UngroupedProperties() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createClassAxiomMember();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(
			// expected
			ImmutableList.of(
				new SnomedOWLRelationshipDocument(Concepts.IS_A, "245565004", 0),
				new SnomedOWLRelationshipDocument(Concepts.IS_A, "420479003", 0),
				new SnomedOWLRelationshipDocument(Concepts.IS_A, "7121006", 0),
				new SnomedOWLRelationshipDocument("272741003", "24028007", 0)
			), 
			// actual
			actual.getClassAxiomRelationships()
		);
		assertThat(actual.getGciAxiomRelationships()).isEmpty();
		assertDocEquals(member, actual);
	}

	@Test
	public void indexOWLAxiomMember_GroupedProperties() throws Exception {
		final String referencedComponentId = "359728003";
		final String owlExpression = "EquivalentClasses("
				+ ":359728003 "
				+ "ObjectIntersectionOf(:384723003 "
				+ "		ObjectSomeValuesFrom(:609096000 "
				+ "			ObjectIntersectionOf("
				+ "				ObjectSomeValuesFrom(:260686004 :129304002) "
				+ "				ObjectSomeValuesFrom(:405813007 :245269009)"
				+ "			)"
				+ "		) "
				+ "		ObjectSomeValuesFrom(:609096000 "
				+ "			ObjectIntersectionOf("
				+ "				ObjectSomeValuesFrom(:260686004 :129304002) "
				+ "				ObjectSomeValuesFrom(:405813007 :81802002)"
				+ "			)"
				+ "		)"
				+ "))";
		final SnomedOWLExpressionConverterResult owlRelationships = toSnomedOWLRelationships(referencedComponentId, owlExpression);
		
		final SnomedRefSetMemberIndexEntry member = createBaseMember()
				.referencedComponentId(referencedComponentId)
				.referenceSetId(Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.field(Fields.OWL_EXPRESSION, owlExpression)
				.classAxiomRelationships(owlRelationships.getClassAxiomRelationships())
				.gciAxiomRelationships(owlRelationships.getGciAxiomRelationships())
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(owlExpression, actual.getOwlExpression());
		assertEquals(
			// expected
			ImmutableList.of(
				new SnomedOWLRelationshipDocument(Concepts.IS_A, "384723003", 0),
				new SnomedOWLRelationshipDocument("260686004", "129304002", 1),
				new SnomedOWLRelationshipDocument("405813007", "245269009", 1),
				new SnomedOWLRelationshipDocument("260686004", "129304002", 2),
				new SnomedOWLRelationshipDocument("405813007", "81802002", 2)
			), 
			// actual
			actual.getClassAxiomRelationships()
		);
		assertThat(actual.getGciAxiomRelationships()).isEmpty();
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexOWLAxiomMember_GCIAxiom() throws Exception {
		final SnomedRefSetMemberIndexEntry member = createGciAxiomMember();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertThat(actual.getClassAxiomRelationships()).isEmpty();
		assertEquals(
			// expected
			ImmutableList.of(
				new SnomedOWLRelationshipDocument(Concepts.IS_A, "193783008", 0),
				new SnomedOWLRelationshipDocument("116676008", "23583003", 1),
				new SnomedOWLRelationshipDocument("246075003", "19551004", 1),
				new SnomedOWLRelationshipDocument("363698007", "65431007", 1),
				new SnomedOWLRelationshipDocument("370135005", "441862004", 1)
			), 
			// actual
			actual.getGciAxiomRelationships()
		);
		assertDocEquals(member, actual);
	}

	@Test
	public void searchByOwlExpressionConcept_TypeId() throws Exception {
		final SnomedRefSetMemberIndexEntry gciAxiomMember = createGciAxiomMember();
		SnomedRefSetMemberIndexEntry classAxiomMember = createClassAxiomMember();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, gciAxiomMember);
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY2, classAxiomMember);

		Hits<SnomedRefSetMemberIndexEntry> hits = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionConcept("272741003"))
				.build());
		assertThat(hits).hasSize(1);
		SnomedRefSetMemberIndexEntry hit = Iterables.getOnlyElement(hits);
		assertDocEquals(classAxiomMember, hit);
	}
	
	@Test
	public void searchByOwlExpression_TypeId() throws Exception {
		final SnomedRefSetMemberIndexEntry gciAxiomMember = createGciAxiomMember();
		SnomedRefSetMemberIndexEntry classAxiomMember = createClassAxiomMember();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, gciAxiomMember);
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY2, classAxiomMember);

		Hits<SnomedRefSetMemberIndexEntry> hits = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionType(singleton("272741003")))
				.build());
		assertThat(hits).hasSize(1);
		SnomedRefSetMemberIndexEntry hit = Iterables.getOnlyElement(hits);
		assertDocEquals(classAxiomMember, hit);
	}
	
	@Test
	public void searchByOwlExpression_DestinationId() throws Exception {
		final SnomedRefSetMemberIndexEntry gciAxiomMember = createGciAxiomMember();
		SnomedRefSetMemberIndexEntry classAxiomMember = createClassAxiomMember();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, gciAxiomMember);
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY2, classAxiomMember);

		Hits<SnomedRefSetMemberIndexEntry> hits = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionDestination(singleton("441862004")))
				.build());
		assertThat(hits).hasSize(1);
		SnomedRefSetMemberIndexEntry hit = Iterables.getOnlyElement(hits);
		assertDocEquals(gciAxiomMember, hit);
	}
	
	@Test
	public void searchByOwlExpressionConcept_DestinationId() throws Exception {
		final SnomedRefSetMemberIndexEntry gciAxiomMember = createGciAxiomMember();
		SnomedRefSetMemberIndexEntry classAxiomMember = createClassAxiomMember();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, gciAxiomMember);
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY2, classAxiomMember);

		Hits<SnomedRefSetMemberIndexEntry> hits = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class)
				.where(SnomedRefSetMemberIndexEntry.Expressions.owlExpressionConcept("441862004"))
				.build());
		assertThat(hits).hasSize(1);
		SnomedRefSetMemberIndexEntry hit = Iterables.getOnlyElement(hits);
		assertDocEquals(gciAxiomMember, hit);
	}
	
	@Test
	public void searchByOwlExpressionGCI() throws Exception {
		final SnomedRefSetMemberIndexEntry gciAxiomMember = createGciAxiomMember();
		SnomedRefSetMemberIndexEntry classAxiomMember = createClassAxiomMember();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, gciAxiomMember);
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY2, classAxiomMember);
		
		Hits<SnomedRefSetMemberIndexEntry> gciAxioms = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class).where(SnomedRefSetMemberIndexEntry.Expressions.gciAxiom(true))
				.build());
		assertThat(gciAxioms).hasSize(1);
		SnomedRefSetMemberIndexEntry hit = Iterables.getOnlyElement(gciAxioms);
		assertDocEquals(gciAxiomMember, hit);
		
		Hits<SnomedRefSetMemberIndexEntry> classAxioms = search(MAIN, Query.select(SnomedRefSetMemberIndexEntry.class).where(SnomedRefSetMemberIndexEntry.Expressions.gciAxiom(false))
				.build());
		assertThat(classAxioms).hasSize(1);
		hit = Iterables.getOnlyElement(classAxioms);
		assertDocEquals(classAxiomMember, hit);
	}
	
	private SnomedRefSetMemberIndexEntry createGciAxiomMember() {
		final String referencedComponentId = "231907006";
		final String owlExpression = ""
				+ "SubClassOf("
				+ "	ObjectIntersectionOf("
				+ "		:193783008 "
				+ "		ObjectSomeValuesFrom(:609096000 "
				+ "			ObjectIntersectionOf("
				+ "				ObjectSomeValuesFrom(:116676008 :23583003) "
				+ "				ObjectSomeValuesFrom(:246075003 :19551004) "
				+ "				ObjectSomeValuesFrom(:363698007 :65431007) "
				+ "				ObjectSomeValuesFrom(:370135005 :441862004)"
				+ "			)"
				+ "		)"
				+ "	)"
				+ "	:231907006"
				+ ")";
		final SnomedOWLExpressionConverterResult owlRelationships = toSnomedOWLRelationships(referencedComponentId, owlExpression);
		
		return createBaseMember()
				.referencedComponentId(referencedComponentId)
				.referenceSetId(Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.field(Fields.OWL_EXPRESSION, owlExpression)
				.classAxiomRelationships(owlRelationships.getClassAxiomRelationships())
				.gciAxiomRelationships(owlRelationships.getGciAxiomRelationships())
				.build();
	}
	
	private SnomedRefSetMemberIndexEntry createClassAxiomMember() {
		final String referencedComponentId = "245567007";
		final String owlExpression = "SubClassOf(:245567007 ObjectIntersectionOf(:245565004 :420479003 :7121006 ObjectSomeValuesFrom(:272741003 :24028007)))";
		final SnomedOWLExpressionConverterResult owlRelationships = toSnomedOWLRelationships(referencedComponentId, owlExpression);
		
		return createBaseMember()
				.referencedComponentId(referencedComponentId)
				.referenceSetId(Concepts.REFSET_OWL_AXIOM)
				.referenceSetType(SnomedRefSetType.OWL_AXIOM)
				.field(Fields.OWL_EXPRESSION, owlExpression)
				.classAxiomRelationships(owlRelationships.getClassAxiomRelationships())
				.gciAxiomRelationships(owlRelationships.getGciAxiomRelationships())
				.build();
	}
	
	private SnomedOWLExpressionConverterResult toSnomedOWLRelationships(String referencedComponentId, String owlExpression) {
		return index().read(RevisionBranch.MAIN_PATH, searcher -> {
			return new SnomedOWLExpressionConverter(
				TestBranchContext.on(searcher.branch())
					.with(RevisionSearcher.class, searcher)
				.build()
			).toSnomedOWLRelationships(referencedComponentId, owlExpression);
		});
	}
	
}
