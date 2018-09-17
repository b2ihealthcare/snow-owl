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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.NullObjectPattern;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.importer.rf2.csv.ConcreteDomainRefSetRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ParseUuid;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.model.IndexConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * SNOMED&nbsp;CT concrete domain reference set importer.
 * @see AbstractSnomedRefSetImporter
 */
public class SnomedConcreteDataTypeRefSetImporter extends AbstractSnomedRefSetImporter<ConcreteDomainRefSetRow, SnomedConcreteDataTypeRefSetMember> {

	private static final Map<String, CellProcessor> CELL_PROCESSOR_MAPPING = ImmutableMap.<String, CellProcessor>builder()
			.put(ConcreteDomainRefSetRow.PROP_UUID, new ParseUuid())
			.put(ConcreteDomainRefSetRow.PROP_EFFECTIVE_TIME, createEffectiveTimeCellProcessor())
			.put(ConcreteDomainRefSetRow.PROP_ACTIVE, new ParseBool("1", "0"))
			.put(ConcreteDomainRefSetRow.PROP_MODULE_ID, NullObjectPattern.INSTANCE)
			.put(ConcreteDomainRefSetRow.PROP_REF_SET_ID, NullObjectPattern.INSTANCE)
			.put(ConcreteDomainRefSetRow.PROP_REFERENCED_COMPONENT_ID, NullObjectPattern.INSTANCE)
			.put(ConcreteDomainRefSetRow.PROP_VALUE, NullObjectPattern.INSTANCE)
			.put(ConcreteDomainRefSetRow.PROP_RELATIONSHIP_GROUP, new ParseInt())
			.put(ConcreteDomainRefSetRow.PROP_TYPE_ID, NullObjectPattern.INSTANCE)
			.put(ConcreteDomainRefSetRow.PROP_CHARACTERISTIC_TYPE_ID, NullObjectPattern.INSTANCE)
			.build();
			
	public static final List<IndexConfiguration> INDEXES = ImmutableList.<IndexConfiguration>builder()
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1000", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "CDO_CREATED"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1001", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "CDO_CONTAINER", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1002", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "REFERENCEDCOMPONENTID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1003", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "UUID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1005", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "SERIALIZEDVALUE/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1004", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "TYPEID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.add(new IndexConfiguration("SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER_IDX1006", "SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSETMEMBER", "CHARACTERISTICTYPEID/*!(255)*/", "CDO_BRANCH", "CDO_VERSION"))
			.build();
	
	private static final SnomedImportConfiguration<ConcreteDomainRefSetRow> IMPORT_CONFIGURATION = new SnomedImportConfiguration<ConcreteDomainRefSetRow>(
			ComponentImportType.CONCRETE_DOMAIN_REFSET, 
			CELL_PROCESSOR_MAPPING, 
			ConcreteDomainRefSetRow.class, 
			SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER, 
			INDEXES);

	public SnomedConcreteDataTypeRefSetImporter(final SnomedImportContext importContext, final InputStream releaseFileStream, final String releaseFileIdentifier) {
		super(IMPORT_CONFIGURATION, importContext, releaseFileStream, releaseFileIdentifier);
	}

	@Override
	protected SnomedRefSet createUninitializedRefSet(final String identifierConceptId) {
		return SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSet();
	}
	
	@Override
	protected void initRefSet(final SnomedRefSet refSet, final String referencedComponentId) {
		((SnomedConcreteDataTypeRefSet) refSet).setDataType(getDataType(refSet.getIdentifierId()));
		refSet.setReferencedComponentType(CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT);
	}

	@Override
	protected SnomedRefSetType getRefSetType() {
		return SnomedRefSetType.CONCRETE_DATA_TYPE;
	}

	@Override
	protected void applyRow(SnomedConcreteDataTypeRefSetMember member, ConcreteDomainRefSetRow row, Collection<SnomedConcreteDataTypeRefSetMember> componentsToAttach) {
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
		member.setSerializedValue(row.getValue());
		member.setGroup(row.getRelationshipGroup());
		member.setTypeId(row.getTypeId());
		member.setCharacteristicTypeId(row.getCharacteristicTypeId());
	}
	
	/**
	 * Returns with the {@link DataType} based on the specified reference set identifier concept ID.
	 * @param identifierConceptId the reference set identifier concept ID.
	 * @return the data type associated with the reference set specified by the identifier concept ID.
	 */
	protected DataType getDataType(final String identifierConceptId) {
		return SnomedRefSetUtil.getDataType(identifierConceptId);
	}

	@Override
	protected String getIdentifierParentConceptId(final String refSetId) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	protected SnomedConcreteDataTypeRefSetMember createMember() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedConcreteDataTypeRefSetMember();
	}
	
	@Override
	protected void attach(Collection<SnomedConcreteDataTypeRefSetMember> componentsToAttach) {
		final Collection<String> containerComponentIds = componentsToAttach.stream()
				.map(SnomedRefSetMember::getReferencedComponentId)
				.collect(Collectors.toSet());
		
		final Map<String, Component> containerComponents = getComponents(containerComponentIds)
				.stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));
		
		for (SnomedConcreteDataTypeRefSetMember member : componentsToAttach) {
			Component container = containerComponents.get(member.getReferencedComponentId());
			if (container instanceof Annotatable) {
				((Annotatable) container).getConcreteDomainRefSetMembers().add(member);
			} else {
				String message = MessageFormat.format("Annotatable component with ID ''{0}'' could not be found, skipping refset member.", member.getReferencedComponentId());
				getLogger().warn(message);
			}
		}
	}
}
