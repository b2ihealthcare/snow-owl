/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.0
 */
public final class SnomedRefSetExcelExportRequest implements Request<BranchContext, UUID> {

	@JsonProperty
	String userId;
	
	@NotEmpty
	@JsonProperty
	String refSetId;
	
	@Override
	public UUID execute(BranchContext context) {
		File exportedFile = null;
		try {
			final String exportFilePath = Files.createTempFile(String.format("dsv-export-%s", refSetId), ".xls").toString();
			exportedFile = new SnomedSimpleTypeRefSetExcelExporter(context, userId, refSetId).doExport(exportFilePath);
			// upload as attachment and return the attachment ID as result
			final UUID exportFileId = UUID.randomUUID();
			context.service(AttachmentRegistry.class).upload(exportFileId, new FileInputStream(exportedFile));
			return exportFileId;
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		} finally {
			if (exportedFile != null) {
				exportedFile.delete();
			}
		}
	}

}
