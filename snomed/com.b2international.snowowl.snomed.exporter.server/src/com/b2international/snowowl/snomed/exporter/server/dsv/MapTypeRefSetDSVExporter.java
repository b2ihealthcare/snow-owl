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
package com.b2international.snowowl.snomed.exporter.server.dsv;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * This class implements the export process of the DSV export for map type reference sets. 
 * Used by the SnomedSimpleTypeRefSetExportServerIndication class.
 */
public final class MapTypeRefSetDSVExporter implements IRefSetDSVExporter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private final SnomedRefSetDSVExportModel exportSetting;

	public MapTypeRefSetDSVExporter(final SnomedRefSetDSVExportModel exportSetting) {
		this.exportSetting = exportSetting;
	}

	@Override
	public File executeDSVExport(final OMMonitor monitor) throws SnowowlServiceException, IOException {
		final int memberNumberToSignal = 100;
		
		final SnomedConcept refSetToExport = SnomedRequests.prepareGetConcept(exportSetting.getRefSetId())
				.setLocales(exportSetting.getLocales())
				.setExpand("referenceSet(expand(members(limit:" + Integer.MAX_VALUE + ", expand(referencedComponent(expand(fsn()))))))")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, exportSetting.getBranchPath())
				.get();
		
		final SnomedReferenceSetMembers membersToExport = refSetToExport.getReferenceSet().getMembers();
		final int activeMemberCount = membersToExport.getTotal();
		
		if (activeMemberCount < memberNumberToSignal) {
			monitor.begin(1);
		} else {
			monitor.begin(activeMemberCount / memberNumberToSignal);
		}
		
		final File file = Files.createTempFile("dsv-export-" + refSetToExport.getId() + Dates.now(), ".csv").toFile();
		
		try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
			
			if (exportSetting.getExportItems().isEmpty()) {
				return file;
			}
		
			writeLine(os, toHeader());
			
			Map<String, String> labels = prepareLabelCache(membersToExport);
			
			int count = 0;
			for (final SnomedReferenceSetMember member : membersToExport) {
				writeLine(os, toDsvLine(member, labels));
				count++;
				if (count % memberNumberToSignal == 0) {
					monitor.worked();
				}
			}
		} catch (final Exception e) {
			throw new SnowowlRuntimeException(e);
		} finally {
			if (null != monitor) {
				monitor.done();
			}
		}
		return file;
	}

	private void writeLine(DataOutputStream os, String line) throws IOException {
		os.writeBytes(line);
		os.writeBytes(LINE_SEPARATOR);
	}

	private Map<String, String> prepareLabelCache(SnomedReferenceSetMembers membersToExport) {
		final SnomedConcepts modelComponents = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByEcl(String.format("< (%s OR %s) ", Concepts.MODULE_ROOT, Concepts.REFSET_ATTRIBUTE))
			.setExpand("fsn()")
			.setLocales(exportSetting.getLocales())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, exportSetting.getBranchPath())
			.get();
		
		final Map<String, String> labels = newHashMapWithExpectedSize(membersToExport.getTotal() + modelComponents.getTotal());
		
		modelComponents.forEach(modelComponent -> {
			labels.put(modelComponent.getId(), getFsnOrId(modelComponent));
		});
		
		for (SnomedReferenceSetMember snomedReferenceSetMember : membersToExport) {
			SnomedCoreComponent referencedComponent = snomedReferenceSetMember.getReferencedComponent();
			String id = referencedComponent.getId();
			if (referencedComponent instanceof SnomedConcept) {
				labels.put(id, getFsnOrId((SnomedConcept) referencedComponent));
			} else if (referencedComponent instanceof SnomedDescription) {
				labels.put(id, ((SnomedDescription) referencedComponent).getTerm());
			} else if (referencedComponent instanceof SnomedRelationship) {
				SnomedRelationship relationship = (SnomedRelationship) referencedComponent;
				labels.put(id, String.format("%s - %s - %s",relationship.getSourceId(), relationship.getTypeId(), relationship.getDestinationId()));
			}
		}
		
		return labels;
	}

	private String getFsnOrId(SnomedConcept concept) {
		return concept.getFsn() != null ? concept.getFsn().getTerm() : concept.getId();
	}

	private String toHeader() {
		return exportSetting.getExportItems()
				.stream()
				.map(AbstractSnomedDsvExportItem::getDisplayName)
				.collect(Collectors.joining(exportSetting.getDelimiter()));
	}

	private String toDsvLine(final SnomedReferenceSetMember member, Map<String, String> labelMap) {
		return exportSetting.getExportItems()
				.stream()
				.map(item -> getExportItemForConcept(member, item.getType(), labelMap))
				.collect(Collectors.joining(exportSetting.getDelimiter()));
	}

	// FIXME: Restore fetching map target labels from external terminology if map target type (code system/version?) is set
	private String getExportItemForConcept(final SnomedReferenceSetMember member, final SnomedDsvExportItemType type, Map<String, String> labelMap) {
		switch (type) {
			case REFERENCED_COMPONENT:
				return labelMap.get(member.getReferencedComponent().getId());
			case REFERENCED_COMPONENT_ID:
				return member.getReferencedComponent().getId();
			case MAP_TARGET_ID:
				return nullToEmpty((String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET));
			case STATUS_ID:
				return String.valueOf(member.isActive() ? 1 : 0);
			case STATUS_LABEL:
				return member.isActive() ? "active" : "inactive";
			case EFFECTIVE_TIME:
				return EffectiveTimes.format(member.getEffectiveTime());
			case MODULE_ID:
				return member.getModuleId();
			case MODULE_LABEL:
				return labelMap.get(member.getModuleId());
			case MEMBER_ID:
				return member.getId();
			case MAP_GROUP:
				return String.valueOf(member.getProperties().get(SnomedRf2Headers.FIELD_MAP_GROUP));
			case MAP_PRIORITY:
				return String.valueOf(member.getProperties().get(SnomedRf2Headers.FIELD_MAP_PRIORITY));
			case MAP_RULE:
				return String.valueOf(member.getProperties().get(SnomedRf2Headers.FIELD_MAP_RULE));
			case MAP_ADVICE:
				return String.valueOf(member.getProperties().get(SnomedRf2Headers.FIELD_MAP_ADVICE));
			case CORRELATION:
				return labelMap.get(member.getProperties().get(SnomedRf2Headers.FIELD_CORRELATION_ID));
			case SDD_CLASS:
				return SnomedRequests.prepareSearchRelationship()
						.one()
						// XXX intentionally using PT here for SDD class properties
						.setExpand("destination(expand(pt()))")
						.setLocales(exportSetting.getLocales())
						.filterBySource(member.getReferencedComponent().getId())
						.filterByType(Concepts.HAS_SDD_CLASS)
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, exportSetting.getBranchPath())
						.get()
						.first()
						.map(relationship -> {
							SnomedDescription pt = relationship.getDestination().getPt();
							return pt != null ? pt.getTerm() : relationship.getDestinationId();
						})
						.get();
			case MAP_CATEGORY:
				final String mapCategoryId = (String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID);
				if (StringUtils.isEmpty(mapCategoryId)) {
					return nullToEmpty(mapCategoryId); 
				} else {
					return labelMap.get(mapCategoryId);
				}
			case MAP_TARGET_DESCRIPTION:
				return nullToEmpty((String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION));
			default:
				return "";
		}
	}

}
