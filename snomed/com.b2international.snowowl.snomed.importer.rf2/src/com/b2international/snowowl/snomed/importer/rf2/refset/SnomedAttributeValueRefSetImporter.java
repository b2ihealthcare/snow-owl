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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import static com.google.common.collect.Sets.newHashSet;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.importer.rf2.csv.AssociatingRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SnomedAttributeValueRefSetImporter extends AbstractSnomedRefSetImporter<AssociatingRefSetRow, SnomedAttributeValueRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(AssociatingRefSetRow.PROP_UUID, new ParseUuid())
			.put(AssociatingRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(AssociatingRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(AssociatingRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(AssociatingRefSetRow.PROP_ASSOCIATED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.build();
	
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDATTRIBUTEVALUEREFSETMEMBER", "VALUEID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	
	private static final SnomedImportConfiguration<AssociatingRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<AssociatingRefSetRow>(
			ComponentImportType.ATTRIBUTE_VALUE_REFSET, 
			CELL_PROCESSOR_MAPPING, 
			AssociatingRefSetRow.class, 
			SnomedRf2Headers.ATTRIBUTE_VALUE_TYPE_HEADER,
			INDEXES);

	public SnomedAttributeValueRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		
		if (Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(identifierConceptId) || 
				Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(identifierConceptId) ||
				Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(identifierConceptId)) {
		
			return SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
		} else {
			return super.createUninitializedRefSet(identifierConceptId);
		}
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.ATTRIBUTE_VALUE;
	}
	
	@Override
	protected void applyRow(SnomedAttributeValueRefSetMember member, AssociatingRefSetRow row, Collection<SnomedAttributeValueRefSetMember> componentsToAttach) {
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
		member.setValueId(row.getAssociatedComponentId());
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_ATTRIBUTE_VALUE_TYPE;
	}

	@Override
	protected SnomedAttributeValueRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedAttributeValueRefSetMember();
	}
	
	@Override
	protected void attach(Collection<SnomedAttributeValueRefSetMember> componentsToAttach) {
		final Collection<String> containerComponentIds = componentsToAttach.stream().map(SnomedAttributeValueRefSetMember::getReferencedComponentId).collect(Collectors.toSet());
		final Map<String, Component> containerComponents = getComponents(containerComponentIds).stream().collect(Collectors.toMap(Component::getId, c -> c));
		final Collection<SnomedAttributeValueRefSetMember> attachToRegular = newHashSet();
		for (SnomedAttributeValueRefSetMember member : componentsToAttach) {
			Component container = containerComponents.get(member.getReferencedComponentId());
			if (container instanceof Inactivatable && isInactivationMember(member.getRefSetIdentifierId())) {
				((Inactivatable) container).getInactivationIndicatorRefSetMembers().add(member);
			} else if (container instanceof Relationship && isRefinabilityMember(member.getRefSetIdentifierId())) {
				((Relationship) container).getRefinabilityRefSetMembers().add(member);
			} else {
				attachToRegular.add(member);
			}
		}
		super.attach(attachToRegular);
	}

	private boolean isRefinabilityMember(String refSetIdentifierId) {
		return Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(refSetIdentifierId);
	}

	private boolean isInactivationMember(String refSetIdentifierId) {
		return Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSetIdentifierId) || Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSetIdentifierId);
	}
	
}