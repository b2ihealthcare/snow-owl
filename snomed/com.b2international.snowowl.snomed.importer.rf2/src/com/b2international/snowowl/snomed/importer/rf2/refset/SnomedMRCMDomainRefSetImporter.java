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
import com.b2international.snowowl.snomed.importer.rf2.csv.MRCMDomainRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public class SnomedMRCMDomainRefSetImporter extends AbstractSnomedRefSetImporter<MRCMDomainRefSetRow, SnomedMRCMDomainRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(MRCMDomainRefSetRow.PROP_UUID, new ParseUuid())
			.put(MRCMDomainRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(MRCMDomainRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(MRCMDomainRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(MRCMDomainRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(MRCMDomainRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_CONSTRAINT, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_PARENT_DOMAIN, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, NullObjectPattern.INSTANCE)
			.put(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE, NullObjectPattern.INSTANCE)
			.build();

	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDMRCMDOMAINREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();

	private static final SnomedImportConfiguration<MRCMDomainRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<>(
			ComponentImportType.MRCM_DOMAIN_REFSET,
			CELL_PROCESSOR_MAPPING,
			MRCMDomainRefSetRow.class,
			SnomedRf2Headers.MRCM_DOMAIN_HEADER,
			INDEXES);

	public SnomedMRCMDomainRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_MRCM_DOMAIN_ROOT;
	}

	@Override
	protected SnomedMRCMDomainRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMDomainRefSetMember();
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.MRCM_DOMAIN;
	}

	@Override
	protected void applyRow(final SnomedMRCMDomainRefSetMember member, final MRCMDomainRefSetRow row, final Collection<SnomedMRCMDomainRefSetMember> componentsToAttach) {

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

		member.setDomainConstraint(row.getDomainConstraint());
		member.setParentDomain(row.getParentDomain());
		member.setProximalPrimitiveConstraint(row.getProximalPrimitiveConstraint());
		member.setProximalPrimitiveRefinement(row.getProximalPrimitiveRefinement());
		member.setDomainTemplateForPrecoordination(row.getDomainTemplateForPrecoordination());
		member.setDomainTemplateForPostcoordination(row.getDomainTemplateForPostcoordination());
		member.setEditorialGuideReference(row.getGuideURL());

	}

}
