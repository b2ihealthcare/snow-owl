/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.rmi.server.ExportException;
import java.util.List;
import java.util.UUID;

import org.eclipse.net4j.util.om.monitor.Monitor;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedClientProtocol;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportClientRequest;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedRefSetDSVExportModel;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.11
 */
public final class SnomedDSVExportRequest implements Request<BranchContext, UUID> {

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

	private File doExport(SnomedRefSetDSVExportModel exportModel) throws Exception {
		SnomedRefSetDSVExportClientRequest dsvRequest = new SnomedRefSetDSVExportClientRequest(SnomedClientProtocol.getInstance(), exportModel);

		File exportFile = dsvRequest.send(new Monitor());
		SnomedExportResult result = dsvRequest.getExportResult();
		if (result.getResult().equals(SnomedExportResult.Result.CANCELED) || result.getResult().equals(SnomedExportResult.Result.EXCEPTION)) {
			throw new ExportException(result.getMessage());
		}
		
		exportModel.getExportResult().setResultAndMessage(result.getResult(), result.getMessage());
		return exportFile;
	}

	private SnomedRefSetDSVExportModel toExportModel(BranchContext context) {
		SnomedRefSetDSVExportModel model = new SnomedRefSetDSVExportModel();
		Branch branch = context.branch();
		model.setUserId(User.SYSTEM.getUsername());
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
}
