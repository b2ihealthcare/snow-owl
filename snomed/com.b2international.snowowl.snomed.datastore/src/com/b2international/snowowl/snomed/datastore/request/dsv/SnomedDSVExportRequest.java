/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

/**
 * @since 5.11
 */
final class SnomedDSVExportRequest implements Request<BranchContext, UUID> {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedDSVExportRequest.class);
	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String refSetId;
	
	@JsonProperty
	private boolean descriptionIdExpected;
	
	@JsonProperty
	private boolean relationshipTargetExpected;
	
	@JsonProperty
	private boolean includeInactiveMembersExpected;
	
	@JsonProperty
	private String delimiter;

	@JsonProperty
	private List<AbstractSnomedDsvExportItem> exportItems;

	@JsonProperty
	private List<ExtendedLocale> locales;

	SnomedDSVExportRequest() {}
	
	@Override
	public UUID execute(BranchContext context) {
		File file = null;
		try {
			file = doExport(toExportModel(context));
			UUID fileId = UUID.randomUUID();
			context.service(AttachmentRegistry.class).upload(fileId, new FileInputStream(file));
			return fileId;
		} catch (Exception e) {
			throw new RuntimeException("Error occurred during DSV export.", e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	private SnomedRefSetDSVExportModel toExportModel(BranchContext context) throws Exception {
		SnomedRefSetDSVExportModel model = new SnomedRefSetDSVExportModel();
		Branch branch = context.branch();
		model.setExportPath(java.nio.file.Files.createTempDirectory("dsv-export-temp-dir").toFile().getAbsolutePath());
		model.setBranchBase(branch.baseTimestamp());
		model.setBranchPath(context.branchPath());
		model.setDelimiter(delimiter);
		model.setIncludeDescriptionId(descriptionIdExpected);
		model.setIncludeRelationshipTargetId(relationshipTargetExpected);
		model.setIncludeInactiveMembers(includeInactiveMembersExpected);
		model.setLocales(locales);
		model.setRefSetId(refSetId);
		model.addExportItems(exportItems);
		return model;
	}

	void setRefsetId(String refSetId) {
		this.refSetId = refSetId;
	}

	void setDescriptionIdExpected(boolean descriptionIdExpected) {
		this.descriptionIdExpected = descriptionIdExpected;
	}

	void setRelationshipTargetExpected(boolean relationshipTargetExpected) {
		this.relationshipTargetExpected = relationshipTargetExpected;
	}
	
	void setIncludeInactiveMembersExpected(boolean includeInactiveMembersExpected) {
		this.includeInactiveMembersExpected = includeInactiveMembersExpected;
	}

	void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	void setExportItems(List<AbstractSnomedDsvExportItem> exportItems) {
		this.exportItems = exportItems;
	}

	void setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	private File doExport(SnomedRefSetDSVExportModel exportModel) throws Exception {
		File response = null;
		SnomedExportResult result = new SnomedExportResult();
		IRefSetDSVExporter exporter = getRefSetExporter(exportModel);
		
		try {
			response = exporter.executeDSVExport();
		} catch (Exception e) {
			LOG.error("Error while exporting DSV.", e);
			result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components to delimiter separated files.");
		}

		if (result.getResult().equals(SnomedExportResult.Result.CANCELED) || result.getResult().equals(SnomedExportResult.Result.EXCEPTION)) {
			throw new ExportException(result.getMessage());
		}
		
		exportModel.getExportResult().setResultAndMessage(result.getResult(), result.getMessage());
		return response;
	}
	
	private IRefSetDSVExporter getRefSetExporter(SnomedRefSetDSVExportModel exportSetting) {
		IBranchPath branchPath = BranchPathUtils.createPath(exportSetting.getBranchPath());
		
		RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		RevisionIndex revisionIndex = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
		
		QueryBuilder<SnomedConceptDocument> builder = Query.select(SnomedConceptDocument.class);

		final Query<SnomedConceptDocument> query = builder.where(SnomedConceptDocument.Expressions.id(exportSetting.getRefSetId())).build();
		
		
		SnomedConceptDocument refsetConcept = revisionIndex.read(branchPath.getPath(), new RevisionIndexRead<SnomedConceptDocument>() {

			@Override
			public SnomedConceptDocument execute(RevisionSearcher searcher) throws IOException {
				
				Hits<SnomedConceptDocument> snomedConceptDocuments = searcher.search(query);
				Optional<SnomedConceptDocument> first = FluentIterable.<SnomedConceptDocument>from(snomedConceptDocuments).first();
				if (first.isPresent()) {
					return first.get();
				} else {
					throw new IllegalArgumentException("Could not find reference set with id: " + exportSetting.getRefSetId());
				}
			}
		});
		
		IRefSetDSVExporter exporter = null;
		if (SnomedRefSetType.SIMPLE.equals(refsetConcept.getRefSetType())) {
			exporter = new SnomedSimpleTypeRefSetDSVExporter(exportSetting);
		} else if (SnomedRefSetUtil.isMapping(refsetConcept.getRefSetType())) {
			exporter = new MapTypeRefSetDSVExporter(exportSetting);
		}
		return exporter;
	}
	
}
