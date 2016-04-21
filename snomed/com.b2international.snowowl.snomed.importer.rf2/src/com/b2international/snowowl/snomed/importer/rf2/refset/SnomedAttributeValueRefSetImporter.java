/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
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
	protected SnomedAttributeValueRefSetMember doImportRow(final AssociatingRefSetRow currentRow) {

		final SnomedAttributeValueRefSetMember editedMember = (SnomedAttributeValueRefSetMember) getOrCreateMember(currentRow.getUuid());
		
		if (skipCurrentRow(currentRow, editedMember)) {
			getLogger().warn("Not importing attribute value reference set member '{}' with effective time '{}'; it should have been filtered from the input file.",
					currentRow.getUuid(), 
					EffectiveTimes.format(currentRow.getEffectiveTime(), DateFormats.SHORT));

			return null;
		}

		if (currentRow.getEffectiveTime() != null) {
			editedMember.setEffectiveTime(currentRow.getEffectiveTime());
			editedMember.setReleased(true);
		} else {
			editedMember.unsetEffectiveTime();
		}

		editedMember.setRefSet(getOrCreateRefSet(currentRow.getRefSetId(), currentRow.getReferencedComponentId()));
		editedMember.setActive(currentRow.isActive());
		editedMember.setModuleId(currentRow.getModuleId());
		editedMember.setReferencedComponentId(currentRow.getReferencedComponentId());
		editedMember.setValueId(currentRow.getAssociatedComponentId());
		
		return editedMember;
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_ATTRIBUTE_VALUE_TYPE;
	}

	@Override
	protected SnomedAttributeValueRefSetMember createRefSetMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedAttributeValueRefSetMember();
	}
	
	@Override
	protected boolean addToMembersList(final SnomedAttributeValueRefSetMember currentMember) {
		
		final String refSetIdentifierId = currentMember.getRefSetIdentifierId();
		final String referencedComponentId = currentMember.getReferencedComponentId();
		
		if (Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR.equals(refSetIdentifierId) || 
				Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR.equals(refSetIdentifierId)) {
			
			final Inactivatable inactivatableComponent = getInactivatableComponent(referencedComponentId);
			
			if (null == inactivatableComponent) {
				String message = MessageFormat.format("Inactivatable component with ID ''{0}'' could not be found, skipping refset member.", currentMember.getReferencedComponentId());
				getLogger().warn(message);
				log(message);
				return false;
			}
			
			inactivatableComponent.getInactivationIndicatorRefSetMembers().add(currentMember);
			return true;
		
		} 
		
		if (Concepts.REFSET_RELATIONSHIP_REFINABILITY.equals(refSetIdentifierId)) {

			final Relationship relationship = getRelationship(referencedComponentId);
			
			if (null == relationship) {
				String message = MessageFormat.format("Relationship with ID ''{0}'' could not be found, skipping refset member.", currentMember.getReferencedComponentId());
				getLogger().warn(message);
				log(message);
				return false;
			}
			
			relationship.getRefinabilityRefSetMembers().add(currentMember);
			return true;
		}

		return	super.addToMembersList(currentMember);
	}
}