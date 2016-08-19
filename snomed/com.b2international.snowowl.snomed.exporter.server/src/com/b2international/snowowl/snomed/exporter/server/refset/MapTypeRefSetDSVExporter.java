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
import static com.google.common.collect.Sets.newHashSet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.model.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.exporter.model.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.exporter.model.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
		
		final int memberNumberToSignal = 100;
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		final SnomedRefSetBrowser refSetBrowser = applicationContext.getService(SnomedRefSetBrowser.class);
		LanguageSetting languageSetting = applicationContext.getService(LanguageSetting.class);
		IEventBus eventBus = applicationContext.getService(IEventBus.class);
		
		int activeMemberCount = refSetBrowser.getActiveMemberCount(branchPath, exportSetting.getRefSetId());
		if (activeMemberCount < memberNumberToSignal) {
			monitor.begin(1);
		} else {
			monitor.begin(activeMemberCount/memberNumberToSignal);
		}
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
			
			SnomedReferenceSet snomedRefset = SnomedRequests.prepareGetReferenceSet()
				.setComponentId(exportSetting.getRefSetId())
				.setExpand("members(limit:" + Integer.MAX_VALUE + ", expand(referencedComponent(expand(pt()))))")
				.setLocales(languageSetting.getLanguagePreference())
				.build(branchPath.getPath()).executeSync(eventBus);
				
			Map<String, String> labelMap = Maps.newHashMap();
			
			for (SnomedReferenceSetMember snomedReferenceSetMember : snomedRefset.getMembers()) {
				SnomedCoreComponent referencedComponent = snomedReferenceSetMember.getReferencedComponent();
				String id = referencedComponent.getId();
				if (referencedComponent instanceof ISnomedConcept) {
					ISnomedDescription pt = ((ISnomedConcept) referencedComponent).getPt();
					if (pt == null) {
						labelMap.put(id, id); 
					} else {
						labelMap.put(id, pt.getTerm());
					}
				} else if (referencedComponent instanceof ISnomedDescription) {
					labelMap.put(id, ((ISnomedDescription) referencedComponent).getTerm());
				} else if (referencedComponent instanceof ISnomedRelationship) {
					ISnomedRelationship relationship = (ISnomedRelationship) referencedComponent;
					labelMap.put(id, String.format("%s - %s - %s",relationship.getSourceId(), relationship.getTypeId(), relationship.getDestinationId()));
				}
			}
			
			Collection<SnomedConceptIndexEntry> modelComponents = SnomedRequests
					.prepareSearchConcept()
					.setComponentIds(ImmutableSet.of(
							Concepts.MODULE_ROOT,
							Concepts.REFSET_ATTRIBUTE))
					.setExpand("pt(),descendants(limit:100,form:\"inferred\",direct:false,expand(pt(),parentIds(),ancestorIds()))")
					.setLocales(languageSetting.getLanguagePreference())
					.build(branchPath.getPath())
					.execute(eventBus)
							.then(new Function<SnomedConcepts, Collection<ISnomedConcept>>() {
								@Override
								public Collection<ISnomedConcept> apply(SnomedConcepts input) {
									final Collection<ISnomedConcept> additionalConcepts = newHashSet();
									additionalConcepts.addAll(input.getItems());
									for (ISnomedConcept concept : input) {
										additionalConcepts.addAll(concept.getDescendants().getItems());
									}
									return additionalConcepts;
								}
							})
							.then(new Function<Collection<ISnomedConcept>, Collection<SnomedConceptIndexEntry>>() {
								@Override
								public Collection<SnomedConceptIndexEntry> apply(Collection<ISnomedConcept> input) {
									return SnomedConceptIndexEntry.fromConcepts(input);
								}
							}).getSync();
				
			for (SnomedConceptIndexEntry modelComponentIndexEntry : modelComponents) {
				labelMap.put(modelComponentIndexEntry.getId(), modelComponentIndexEntry.getLabel());
			}
			
			int count = 0;
			for (final SnomedRefSetMemberIndexEntry entry : members) {
				os.writeBytes(getLineForConcept(entry, labelMap));
				count++;
				if (count % memberNumberToSignal == 0) {
					monitor.worked();
				}
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

	private String getLineForConcept(final SnomedRefSetMemberIndexEntry entry, Map<String, String> labelMap) {
		final StringBuffer buffer = new StringBuffer();
		for (final AbstractSnomedDsvExportItem exportItem : exportSetting.getExportItems()) {
			buffer.append(getExportItemForConcept(entry, exportItem.getType(), labelMap));
			buffer.append(DELIMITER);
		}
		buffer.append(LINE_SEPARATOR);
		return buffer.toString();
	}

	private String getExportItemForConcept(final SnomedRefSetMemberIndexEntry member, final SnomedDsvExportItemType type, Map<String, String> labelMap) {
		switch (type) {
			case REFERENCED_COMPONENT:
				return labelMap.get(member.getReferencedComponentId());
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
				return labelMap.get(member.getModuleId());
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
					return labelMap.get(complexEntry.getCorrelationId());
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
					return isEmpty(mapCategoryId) ? nullToEmpty(mapCategoryId) : labelMap.get(mapCategoryId);
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

	@Deprecated
	private String getConceptLabel(String conceptId) {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, conceptId);
	}
}
