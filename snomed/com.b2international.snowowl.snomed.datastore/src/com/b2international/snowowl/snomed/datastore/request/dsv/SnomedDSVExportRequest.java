/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		File file = doExport(context);
		try (FileInputStream in = new FileInputStream(file)) {
			UUID fileId = UUID.randomUUID();
			context.service(AttachmentRegistry.class).upload(fileId, in);
			return fileId;
		} catch (Exception e) {
			throw new RuntimeException("Error occurred during DSV export.", e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	private SnomedRefSetDSVExportModel toExportModel(BranchContext context) {
		SnomedRefSetDSVExportModel model = new SnomedRefSetDSVExportModel();
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
	
	private File doExport(BranchContext context) {
		File response = null;
		SnomedExportResult result = new SnomedExportResult();
		IRefSetDSVExporter exporter = getRefSetExporter(context);
		
		try {
			response = exporter.executeDSVExport(context.service(IProgressMonitor.class));
		} catch (Exception e) {
			LOG.error("Error while exporting DSV.", e);
			result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components to delimiter separated files.");
		}

		if (result.getResult().equals(SnomedExportResult.Result.CANCELED) || result.getResult().equals(SnomedExportResult.Result.EXCEPTION)) {
			throw new RuntimeException(result.getMessage());
		}
		
		return response;
	}
	
	private IRefSetDSVExporter getRefSetExporter(BranchContext context) {
		final SnomedReferenceSet refSet = SnomedRequests.prepareGetReferenceSet(refSetId).build().execute(context);
		IRefSetDSVExporter exporter = null;
		if (SnomedRefSetType.SIMPLE.equals(refSet.getType())) {
			exporter = new SnomedSimpleTypeRefSetDSVExporter(context, toExportModel(context));
		} else if (SnomedRefSetUtil.isMapping(refSet.getType())) {
			exporter = new MapTypeRefSetDSVExporter(context, toExportModel(context));
		} else {
			throw new BadRequestException("Unsupported reference set '%s' with type '%s' in DSV export", refSetId, refSet.getType());
		}
		return exporter;
	}
	
}
