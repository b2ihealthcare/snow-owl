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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.importer.rf2.csv.MRCMAttributeDomainRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class SnomedMRCMAttributeDomainRefSetImporter extends AbstractSnomedRefSetImporter<MRCMAttributeDomainRow, SnomedMRCMAttributeDomainRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(MRCMAttributeDomainRow.PROP_UUID, new ParseUuid())
			.put(MRCMAttributeDomainRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(MRCMAttributeDomainRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(MRCMAttributeDomainRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(MRCMAttributeDomainRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(MRCMAttributeDomainRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, new ParseBool("1", "0"))
			.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_CARDINALITY, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration> builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER_IDX1000",
					"SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER_IDX1001",
					"SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER_IDX1002",
					"SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER_IDX1003",
					"SNOMEDREFSET_SNOMEDMRCMATTRIBUTEDOMAINREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<MRCMAttributeDomainRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<>(
			ComponentImportType.MRCM_ATTRIBUTE_DOMAIN_REFSET,
			CELL_PROCESSOR_MAPPING,
			MRCMAttributeDomainRow.class,
			SnomedRf2Headers.MRCM_ATTRIBUTE_DOMAIN_HEADER,
			INDEXES);

	public SnomedMRCMAttributeDomainRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_MRCM_ATTRIBUTE_DOMAIN_ROOT;
	}

	@Override
	protected SnomedMRCMAttributeDomainRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMAttributeDomainRefSetMember();
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN;
	}

	@Override
	protected void applyRow(final SnomedMRCMAttributeDomainRefSetMember member, final MRCMAttributeDomainRow row, final Collection<SnomedMRCMAttributeDomainRefSetMember> componentsToAttach) {

		if (row.getEffectiveTime() != null) {
			member.setEffectiveTime(row.getEffectiveTime());
			member.setReleased(true);
		} else {
			member.unsetEffectiveTime();
		}

		member.setRefSet(getOrCreateRefSet(row.getRefSetId(), row.getReferencedComponentId()));
		member.setActive(row.isActive());
		member.setModuleId(row.getModuleId());
		member.setReferencedComponentId(row.getReferencedComponentId());

		member.setDomainId(row.getDomainId());
		member.setGrouped(row.isGrouped());
		member.setAttributeCardinality(row.getAttributeCardinality());
		member.setAttributeInGroupCardinality(row.getAttributeInGroupCardinality());
		member.setRuleStrengthId(row.getRuleStrengthId());
		member.setContentTypeId(row.getContentTypeId());

	}

}
