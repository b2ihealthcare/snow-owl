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
package com.b2international.snowowl.snomed.datastore.request.dsv;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Sets.newHashSet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import com.b2international.commons.FileUtils;
import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.label.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedDsvExportItemType;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
/**
 * This class implements the export process of the DSV export for map type reference sets. 
 * Used by the SnomedSimpleTypeRefSetExportServerIndication class.
 */
final class MapTypeRefSetDSVExporter implements IRefSetDSVExporter {

	private final String tmpDir;
	private final String delimiter;
	private final String lineSeparator;
	private final SnomedRefSetDSVExportModel exportSetting;
	private final IBranchPath branchPath;
	private RevisionIndex revisionIndexService; 

	public MapTypeRefSetDSVExporter(final SnomedRefSetDSVExportModel exportSetting) {
		this.exportSetting = exportSetting;
		this.branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
		
		tmpDir = exportSetting.getExportPath();
		delimiter = exportSetting.getDelimiter();
		lineSeparator = System.getProperty("line.separator");
		
		RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		revisionIndexService = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
	}

	@Override
	public File executeDSVExport() throws Exception {
		
		final ApplicationContext applicationContext = ApplicationContext.getInstance();
		final LanguageSetting languageSetting = applicationContext.getService(LanguageSetting.class);
		final IEventBus bus = applicationContext.getService(IEventBus.class);
		
		final SnomedReferenceSet refSet = SnomedRequests.prepareGetReferenceSet(exportSetting.getRefSetId())
				.setLocales(exportSetting.getLocales())
				.setExpand("members(limit:" + Integer.MAX_VALUE + ", expand(referencedComponent(expand(pt()))))")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(bus)
				.getSync();
		
		SnomedDescription ptOfRefset = SnomedRequests.prepareGetConcept(exportSetting.getRefSetId())
				.setLocales(exportSetting.getLocales())
				.setExpand("pt()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync()
				.getPt();
		
		String fileName = ptOfRefset == null ? exportSetting.getRefSetId() : ptOfRefset.getTerm();
		
		final File file = new File(tmpDir, fileName + ".csv");
		DataOutputStream os = null;
		try {
			file.createNewFile();
			if (exportSetting.getExportItems().isEmpty()) {
				return file;
			}
			os = new DataOutputStream(new FileOutputStream(file));
			os.writeBytes(getHeader());
			
			Map<String, String> labelMap = Maps.newHashMap();
			
			for (SnomedReferenceSetMember snomedReferenceSetMember : refSet.getMembers()) {
				SnomedCoreComponent referencedComponent = snomedReferenceSetMember.getReferencedComponent();
				String id = referencedComponent.getId();
				if (referencedComponent instanceof SnomedConcept) {
					SnomedDescription pt = ((SnomedConcept) referencedComponent).getPt();
					if (pt == null) {
						labelMap.put(id, id); 
					} else {
						labelMap.put(id, pt.getTerm());
					}
				} else if (referencedComponent instanceof SnomedDescription) {
					labelMap.put(id, ((SnomedDescription) referencedComponent).getTerm());
				} else if (referencedComponent instanceof SnomedRelationship) {
					SnomedRelationship relationship = (SnomedRelationship) referencedComponent;
					labelMap.put(id, String.format("%s - %s - %s",relationship.getSourceId(), relationship.getTypeId(), relationship.getDestinationId()));
				}
			}
			
			Collection<SnomedConceptDocument> modelComponents = SnomedRequests
					.prepareSearchConcept()
					.filterByIds(ImmutableSet.of(
							Concepts.MODULE_ROOT,
							Concepts.REFSET_ATTRIBUTE))
					.setExpand("pt(),descendants(limit:100,direct:false,expand(pt()))")
					.setLocales(languageSetting.getLanguagePreference())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(bus)
					.then(input -> {
						final Collection<SnomedConcept> additionalConcepts = newHashSet();
						additionalConcepts.addAll(input.getItems());
						for (SnomedConcept concept : input) {
							additionalConcepts.addAll(concept.getDescendants().getItems());
						}
						return additionalConcepts;
					})
					.then(input -> SnomedConceptDocument.fromConcepts(input))
					.getSync();
				
			for (SnomedConceptDocument modelComponentIndexEntry : modelComponents) {
				labelMap.put(modelComponentIndexEntry.getId(), modelComponentIndexEntry.getLabel());
			}
			
			for (final SnomedRefSetMemberIndexEntry entry : SnomedRefSetMemberIndexEntry.from(refSet.getMembers())) {
				os.writeBytes(getLineForConcept(entry, labelMap));
			}
		} catch (final Exception e) {
			throw new SnowowlServiceException(e);
		} finally {
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
		File zipFile = FileUtils.createZipArchive(file.getParentFile(), Files.createTempFile("export", ".zip").toFile());
		return zipFile;
	}

	private String getHeader() {
		final StringBuffer buffer = new StringBuffer();
		for (final AbstractSnomedDsvExportItem item : exportSetting.getExportItems()) {
			buffer.append(item.getDisplayName());
			buffer.append(delimiter);
		}
		buffer.append(lineSeparator);
		return buffer.toString();
	}

	private String getLineForConcept(final SnomedRefSetMemberIndexEntry entry, Map<String, String> labelMap) {
		final StringBuffer buffer = new StringBuffer();
		for (final AbstractSnomedDsvExportItem exportItem : exportSetting.getExportItems()) {
			buffer.append(getExportItemForConcept(entry, exportItem.getType(), labelMap));
			buffer.append(delimiter);
		}
		buffer.append(lineSeparator);
		return buffer.toString();
	}

	// FIXME: Restore fetching map target labels from external terminology if map target type (code system/version?) is set
	private String getExportItemForConcept(final SnomedRefSetMemberIndexEntry member, final SnomedDsvExportItemType type, Map<String, String> labelMap) {
		switch (type) {
			case REFERENCED_COMPONENT:
				return labelMap.get(member.getReferencedComponentId());
			case REFERENCED_COMPONENT_ID:
				return member.getReferencedComponentId();
			case MAP_TARGET_ID:
				return member.getMapTarget();
			case STATUS_ID:
				return String.valueOf(member.isActive() ? 1 : 0);
			case STATUS_LABEL:
				return member.isActive() ? "active" : "inactive";
			case EFFECTIVE_TIME:
				return member.getEffectiveTimeAsString();
			case MODULE_ID:
				return member.getModuleId();
			case MODULE_LABEL:
				return labelMap.get(member.getModuleId());
			case MEMBER_ID:
				return member.getId();
			case MAP_GROUP:
				return String.valueOf(member.getMapGroup());
			case MAP_PRIORITY:
				return String.valueOf(member.getMapPriority());
			case MAP_RULE:
				return nullToEmpty(member.getMapRule());
			case MAP_ADVICE:
				return nullToEmpty(member.getMapAdvice());
			case CORRELATION:
				return labelMap.get(member.getCorrelationId());
			case SDD_CLASS:
				
				return revisionIndexService.read(branchPath.getPath(), new RevisionIndexRead<String>() {

					@Override
					public String execute(RevisionSearcher searcher) throws IOException {
						
						//we need every target, limit needs to be set as the default is 50 hits
						ExpressionBuilder condition = Expressions.builder()
								.filter(SnomedRelationshipIndexEntry.Expressions.sourceId(member.getReferencedComponentId()))
								.filter(SnomedRelationshipIndexEntry.Expressions.typeId(Concepts.HAS_SDD_CLASS));
						
						Query<SnomedRelationshipIndexEntry> query = Query.select(SnomedRelationshipIndexEntry.class).where(condition.build()).limit(1).build();
						
						
						Hits<SnomedRelationshipIndexEntry> hits = searcher.search(query);
						FluentIterable.<SnomedRelationshipIndexEntry>from(hits).first();
						for (SnomedRelationshipIndexEntry snomedRelationshipIndexEntry : hits) {
							return getConceptLabel(snomedRelationshipIndexEntry.getDestinationId());
						}
						return "";
					}
				});
			case MAP_CATEGORY:
				final String mapCategoryId = member.getMapCategoryId();
				if (StringUtils.isEmpty(mapCategoryId)) {
					return nullToEmpty(mapCategoryId); 
				} else {
					return labelMap.get(mapCategoryId);
				}
			case MAP_TARGET_DESCRIPTION:
				return nullToEmpty(member.getMapTargetDescription());
			default:
				return "";
		}
	}

	@Deprecated
	private String getConceptLabel(String conceptId) {
		return new SnomedConceptNameProvider(ApplicationContext.getServiceForClass(IEventBus.class), ApplicationContext.getServiceForClass(LanguageSetting.class)).getComponentLabel(branchPath, conceptId);
	}
}
