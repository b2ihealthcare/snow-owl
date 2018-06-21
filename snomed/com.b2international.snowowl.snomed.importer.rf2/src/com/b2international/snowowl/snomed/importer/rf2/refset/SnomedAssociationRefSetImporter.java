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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.importer.rf2.csv.AssociatingRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class SnomedAssociationRefSetImporter extends AbstractSnomedRefSetImporter<AssociatingRefSetRow, SnomedAssociationRefSetMember> {

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
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDASSOCIATIONREFSETMEMBER", "TARGETCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	
	private static final SnomedImportConfiguration<AssociatingRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<AssociatingRefSetRow>(
			ComponentImportType.ASSOCIATION_TYPE_REFSET, 
			CELL_PROCESSOR_MAPPING, 
			AssociatingRefSetRow.class, 
			SnomedRf2Headers.ASSOCIATION_TYPE_HEADER,
			INDEXES);

	public SnomedAssociationRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.ASSOCIATION;
	}
	
	@Override
	protected SnomedAssociationRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedAssociationRefSetMember();
	}
	
	@Override
	protected void applyRow(SnomedAssociationRefSetMember member, AssociatingRefSetRow row, Collection<SnomedAssociationRefSetMember> componentsToAttach) {
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
		member.setTargetComponentId(row.getAssociatedComponentId());
	}
	
	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		return Concepts.REFSET_ASSOCIATION_TYPE;
	}

	@Override
	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		return SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
	}

	@Override
	protected void attach(Collection<SnomedAssociationRefSetMember> componentsToAttach) {
		final Collection<String> containerComponentIds = componentsToAttach.stream().map(SnomedRefSetMember::getReferencedComponentId).collect(Collectors.toSet());
		final Map<String, Component> containerComponents = getComponents(containerComponentIds).stream().collect(Collectors.toMap(Component::getId, c -> c));
		for (SnomedAssociationRefSetMember member : componentsToAttach) {
			Component container = containerComponents.get(member.getReferencedComponentId());
			if (container instanceof Inactivatable) {
				((Inactivatable) container).getAssociationRefSetMembers().add(member);
			} else {
				String message = MessageFormat.format("Inactivatable component with ID ''{0}'' could not be found, skipping refset member.", member.getReferencedComponentId());
				getLogger().warn(message);
			}
		}
	}
	
}