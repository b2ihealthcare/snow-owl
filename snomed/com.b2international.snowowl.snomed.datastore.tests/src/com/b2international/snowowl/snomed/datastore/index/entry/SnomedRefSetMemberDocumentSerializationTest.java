/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Test
	public void indexSimpleMember() throws Exception {
		final String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
			.id(id)
			.active(true)
			.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.released(false)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.referencedComponentId(Concepts.ROOT_CONCEPT)
			.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
			.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
			.referenceSetType(SnomedRefSetType.ASSOCIATION)
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexSimpleMapMember() throws Exception {
		final String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.SIMPLE_MAP)
				.field(Fields.MAP_TARGET, "A01")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexLanguageMember() throws Exception {
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				// TODO use description ID in test case
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.field(Fields.ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
		assertEquals(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexStringConcreteDomainMember() throws Exception {
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.field(Fields.DATA_TYPE, DataType.STRING)
				.field(SnomedRf2Headers.FIELD_VALUE, "TEST")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
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
		
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
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
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
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
		
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_MRCM_DOMAIN_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_DOMAIN)
				.field(Fields.MRCM_DOMAIN_CONSTRAINT, "domainConstraint")
				.field(Fields.MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, "proximalPrimitiveConstraint")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, "domainTemplateForPrecoordination")
				.field(Fields.MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, "domainTemplateForPostcoordination")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
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
		
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN)
				.field(Fields.MRCM_DOMAIN_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_GROUPED, Boolean.TRUE)
				.field(Fields.MRCM_ATTRIBUTE_CARDINALITY, "attributeCardinality")
				.field(Fields.MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, "attributeInGroupCardinality")
				.field(Fields.MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
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
		
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL)
				.referenceSetType(SnomedRefSetType.MRCM_ATTRIBUTE_RANGE)
				.field(Fields.MRCM_RANGE_CONSTRAINT, "rangeConstraint")
				.field(Fields.MRCM_ATTRIBUTE_RULE, "attributeRule")
				.field(Fields.MRCM_RULE_STRENGTH_ID, Concepts.ROOT_CONCEPT)
				.field(Fields.MRCM_CONTENT_TYPE_ID, Concepts.ROOT_CONCEPT)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
		assertEquals("rangeConstraint", actual.getRangeConstraint());
		assertEquals("attributeRule", actual.getAttributeRule());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getRuleStrengthId());
		assertEquals(Concepts.ROOT_CONCEPT, actual.getContentTypeId());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexMRCMModuleScopeMember() throws Exception {
		
		String id = UUID.randomUUID().toString();
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(id)
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_MRCM_MODULE_SCOPE)
				.referenceSetType(SnomedRefSetType.MRCM_MODULE_SCOPE)
				.field(Fields.MRCM_RULE_REFSET_ID, "mrcmRuleRefsetId")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, id);
		assertEquals("mrcmRuleRefsetId", actual.getMrcmRuleRefsetId());
		assertDocEquals(member, actual);
	}
}
