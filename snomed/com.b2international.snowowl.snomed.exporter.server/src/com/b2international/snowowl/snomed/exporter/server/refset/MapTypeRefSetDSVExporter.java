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
package com.b2international.snowowl.snomed.exporter.server.refset;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.exporter.model.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.exporter.model.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Lists;

/**
 * This class implements the export process of the DSV export for map type reference sets. 
 * Used by the SnomedSimpleTypeRefSetExportServerIndication class.
 */
public class MapTypeRefSetDSVExporter implements IRefSetDSVExporter {

	private static String TEMPORARY_WORKING_DIRECTORY;
	private static String DELIMITER;
	private static String LINE_SEPARATOR;
	
	private final SnomedRefSetDSVExportModel exportSetting;
	private final IBranchPath branchPath; 

	public MapTypeRefSetDSVExporter(final SnomedRefSetDSVExportModel exportSetting) {
		this.exportSetting = exportSetting;
		this.branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
		
		TEMPORARY_WORKING_DIRECTORY = exportSetting.getExportPath();
		DELIMITER = exportSetting.getDelimiter();
		LINE_SEPARATOR = System.getProperty("line.separator");
	}

	@Override
	public File executeDSVExport(final OMMonitor monitor) throws SnowowlServiceException {
		
		final SnomedRefSetBrowser refSetBrowser = ApplicationContext.getInstance().getService(SnomedRefSetBrowser.class);
		monitor.begin(refSetBrowser.getActiveMemberCount(branchPath, exportSetting.getRefSetId()));
		final File file = new File(TEMPORARY_WORKING_DIRECTORY);
		DataOutputStream os = null;
		try {
			file.createNewFile();
			if (exportSetting.getExportItems().isEmpty()) {
				return file;
			}
			os = new DataOutputStream(new FileOutputStream(file));
			os.writeBytes(getHeader());
			final SnomedRefSetType refSetType = SnomedRefSetType.get(refSetBrowser.getTypeOrdinal(branchPath, exportSetting.getRefSetId()));
			final Collection<SnomedRefSetMemberIndexEntry> members = getMembers(refSetType, branchPath);
			for (final SnomedRefSetMemberIndexEntry entry : members) {
				os.writeBytes(getLineForConcept(entry));
			}
		} catch (final Exception e) {
			throw new SnowowlServiceException(e);
		} finally {
			if (null != monitor) {
				monitor.done();
			}
			if (null != os) {
				try {
					os.close();
				} catch (final IOException e) {
					try {
						os.close();
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
					throw new RuntimeException("Error while closing stream.", e);
				}
			}
		}
		return file;
	}

	private Collection<SnomedRefSetMemberIndexEntry> getMembers(final SnomedRefSetType refSetType, final IBranchPath branchPath) {
		Collection<SnomedRefSetMemberIndexEntry> members = Lists.newArrayList();
		if (SnomedRefSetUtil.isMapping(refSetType)) {
			final SnomedRefSetMemberIndexQueryAdapter queryAdapter = new SnomedRefSetMemberIndexQueryAdapter(exportSetting.getRefSetId(), "");
			members = ApplicationContext.getInstance().getService(SnomedIndexService.class).searchUnsorted(branchPath, queryAdapter);
		}
		return members;
	}

	private String getHeader() {
		final StringBuffer buffer = new StringBuffer();
		for (final AbstractSnomedDsvExportItem item : exportSetting.getExportItems()) {
			buffer.append(item.getDisplayName());
			buffer.append(DELIMITER);
		}
		buffer.append(LINE_SEPARATOR);
		return buffer.toString();
	}

	private String getLineForConcept(final SnomedRefSetMemberIndexEntry entry) {
		final StringBuffer buffer = new StringBuffer();
		for (final AbstractSnomedDsvExportItem exportItem : exportSetting.getExportItems()) {
			buffer.append(getExportItemForConcept(entry, exportItem.getType()));
			buffer.append(DELIMITER);
		}
		buffer.append(LINE_SEPARATOR);
		return buffer.toString();
	}

	private String getExportItemForConcept(final SnomedRefSetMemberIndexEntry member, final SnomedDsvExportItemType type) {
		switch (type) {
			case REFERENCED_COMPONENT:
				return getComponentLabel(member.getReferencedComponentType(), member.getReferencedComponentId());
			case REFERENCED_COMPONENT_ID:
				return member.getReferencedComponentId();
			case MAP_TARGET:
				return getComponentLabel(member.getMapTargetComponentType(), member.getMapTargetComponentId());
			case MAP_TARGET_ID:
				return member.getMapTargetComponentId();
			case STATUS_ID:
				return String.valueOf(member.isActive() ? 1 : 0);
			case STATUS_LABEL:
				return member.isActive() ? "active" : "inactive";
			case EFFECTIVE_TIME:
				return member.getEffectiveTime();
			case MODULE_ID:
				return member.getModuleId();
			case MODULE_LABEL:
				return getConceptLabel(member.getModuleId());
			case MEMBER_ID:
				return member.getId();
			case MAP_GROUP:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexEntry = (SnomedRefSetMemberIndexEntry) member;
					return String.valueOf(complexEntry.getMapGroup());
				}
			case MAP_PRIORITY:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexEntry = (SnomedRefSetMemberIndexEntry) member;
					return String.valueOf(complexEntry.getMapPriority());
				}
			case MAP_RULE:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexEntry = (SnomedRefSetMemberIndexEntry) member;
					return nullToEmpty(complexEntry.getMapRule());
				}
			case MAP_ADVICE:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexEntry = (SnomedRefSetMemberIndexEntry) member;
					return nullToEmpty(complexEntry.getMapAdvice());
				}
			case CORRELATION:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexEntry = (SnomedRefSetMemberIndexEntry) member;
					return getConceptLabel(complexEntry.getCorrelationId());
				}
			case SDD_CLASS:
				final List<SnomedRelationshipIndexEntry> relationships = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class)
						.getOutboundStatementsById(member.getReferencedComponentId());
				for (final SnomedRelationshipIndexEntry relationship : relationships) {
					if (Concepts.HAS_SDD_CLASS.equals(relationship.getAttributeId())) {
						return getConceptLabel(relationship.getValueId());
					}
				}
			case MAP_CATEGORY:
				if (member instanceof SnomedRefSetMemberIndexEntry) {
					final SnomedRefSetMemberIndexEntry complexMember = (SnomedRefSetMemberIndexEntry) member;
					final String mapCategoryId = complexMember.getMapCategoryId();
					return isEmpty(mapCategoryId) ? nullToEmpty(mapCategoryId) : getConceptLabel(mapCategoryId);
				}
			case MAP_TARGET_DESCRIPTION:
				return nullToEmpty(member.getMapTargetDescription());
			default:
				return "";
		}
	}

	private String getComponentLabel(final String componentType, String componentId) {
		// XXX: Which branch path to use for an external terminology?
		return CoreTerminologyBroker.getInstance().getNameProviderFactory(componentType).getNameProvider().getComponentLabel(branchPath, componentId);
	}

	private String getConceptLabel(String conceptId) {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, conceptId);
	}
}
