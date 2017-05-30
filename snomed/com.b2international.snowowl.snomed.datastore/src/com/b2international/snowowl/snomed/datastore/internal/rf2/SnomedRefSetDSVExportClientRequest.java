/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;

/**
 * This class sends the client's export request to the server. The server
 * response is an archive file. This class sends user export request to the
 * server-side. All requisite information is stored in the
 * {@link SnomedDSVExportSetting} field.
 * 
 */
public class SnomedRefSetDSVExportClientRequest extends RequestWithMonitoring<File> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRefSetDSVExportClientRequest.class);
	private final SnomedRefSetDSVExportModel exportModel;
	private SnomedExportResult result;

	public SnomedRefSetDSVExportClientRequest(final SnomedClientProtocol protocol, final SnomedRefSetDSVExportModel exportModel) {
		super(protocol, Net4jProtocolConstants.REFSET_TO_DSV_SIGNAL);
		this.exportModel = exportModel;
	}

	@Override
	protected int getRequestingWorkPercent() {
		return 0;
	}

	@Override
	protected int getConfirmingWorkPercent() {
		return 0;
	}

	@Override
	protected int getMonitorTimeoutSeconds() {
		return 1000;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		out.writeUTF(exportModel.getUserId());
		out.writeUTF(exportModel.getRefSetId());
		out.writeBoolean(exportModel.includeDescriptionId());
		out.writeBoolean(exportModel.includeRelationshipTargetId());
		
		out.writeInt(exportModel.getExportItems().size());
		for (final AbstractSnomedDsvExportItem exportItem : exportModel.getExportItems()) {
			exportItem.writeToOutputStream(out);
		}
		
		out.writeInt(exportModel.getLocales().size());
		List<ExtendedLocale> locales = exportModel.getLocales();
		for (ExtendedLocale extendedLocale : locales) {
			out.writeString(extendedLocale.toString());
		}
		out.writeUTF(exportModel.getDelimiter());
		out.writeLong(exportModel.getBranchBase());
		out.writeUTF(exportModel.getBranchPath());
	}

	@Override
	protected File confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {

		BufferedOutputStream out = null;
		Path tempFile = java.nio.file.Files.createTempFile("export", ".zip");
		final File file = tempFile.toFile();
		long size;
		try {
			result = (SnomedExportResult) in.readObject(SnomedExportResult.class.getClassLoader());
			
			if (Result.SUCCESSFUL == result.getResult()) {
				size = in.readLong();
				out = new BufferedOutputStream(new FileOutputStream(file));
				while (size != 0L) {
					int chunk = (int) Math.min(size, Net4jProtocolConstants.BUFFER_SIZE);
					
					final byte[] buffer = in.readByteArray();
					out.write(buffer);
					
					size -= chunk;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception while exporting a reference set to DSV format.", e);
			result = new SnomedExportResult(Result.EXCEPTION);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return file;
	}

	public SnomedExportResult getExportResult() {
		return result;
	}

}