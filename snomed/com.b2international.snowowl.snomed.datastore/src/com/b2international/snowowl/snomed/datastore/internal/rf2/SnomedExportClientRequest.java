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
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.net4j.RequestCancelationRunnable;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.internal.rf2.SnomedExportResult.Result;
import com.google.common.base.Strings;

/**
 * This class sends user export request to the server-side. The server response contains the zipped archive file.
 * 
 */
public class SnomedExportClientRequest extends RequestWithMonitoring<File> {

	private static final int REQUEST_TIMEOUT_MILLIS = 60 * 60 * 1000;

	private final SnomedRf2ExportModel model;
	private SnomedExportResult result;

	/**
	 * Send export request to the server (can be embedded).
	 * 
	 * @param protocol the Net4J protocol
	 * @param model the export configuration model
	 */
	public SnomedExportClientRequest(final SignalProtocol<?> protocol, final SnomedRf2ExportModel model) {
		super(protocol, Net4jProtocolConstants.SNOMED_EXPORT_SIGNAL);
		this.model = model;
	}

	@Override
	protected int getRequestingWorkPercent() {
		return 0;
	}

	@Override
	protected int getConfirmingWorkPercent() {
		return 10;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		final ExecutorService executorService = getCancelationExecutorService();
		if (executorService != null) {
			executorService.execute(new RequestCancelationRunnable(monitor, getCancelationPollInterval(), this));
		}

		out.writeUTF(model.getUserId());
		out.writeUTF(model.getClientBranch().path());
		
		out.writeUTF(Strings.nullToEmpty(model.getStartEffectiveTime()));
		out.writeUTF(Strings.nullToEmpty(model.getEndEffectiveTime()));
		
		out.writeInt(model.getReleaseType().getValue());
		out.writeUTF(model.getUnsetEffectiveTimeLabel());
		out.writeBoolean(model.includeUnpublised());
		
		out.writeBoolean(model.isExportToRf1());
		out.writeBoolean(model.isExtendedDescriptionTypesForRf1());
		
		out.writeBoolean(model.isCoreComponentsToExport());
		out.writeInt(model.getRefSetIds().size());

		for (final String refsetIdentifierConcept : model.getRefSetIds()) {
			out.writeUTF(refsetIdentifierConcept);
		}

		final Set<SnomedMapSetSetting> settings = model.getSettings();
		
		out.writeInt(settings.size());
		for (final SnomedMapSetSetting setting : settings) {
			SnomedMapSetSetting.write(setting, out);
		}
		
		final Set<String> modulesToExport = model.getModulesToExport();
		
		out.writeInt(modulesToExport.size());
		for (final String moduleToExport : modulesToExport) {
			out.writeUTF(moduleToExport);
		}

		out.writeUTF(model.getNamespace());
		out.writeUTF(model.getCodeSystemShortName());
		out.writeBoolean(model.isExtensionOnly());
		out.writeUTF(model.getLocales());
	}

	@Override
	protected File confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		
		File file = null;
		BufferedOutputStream out = null;
		
		try {
			result = (SnomedExportResult) in.readObject(SnomedExportResult.class.getClassLoader());

			if (Result.SUCCESSFUL == result.getResult()) {
				long size = in.readLong();
				
				monitor.begin(size);
				
				file = new File(model.getExportPath());
				
				out = new BufferedOutputStream(new FileOutputStream(file));
				while (size != 0L) {
					int chunk = Net4jProtocolConstants.BUFFER_SIZE;
					if (size < Net4jProtocolConstants.BUFFER_SIZE) {
						chunk = (int) size;
					}
					
					monitor.worked(chunk);
					
					final byte[] buffer = in.readByteArray();
					out.write(buffer);
					
					// monitor.worked(chunk);
					size -= chunk;
					
					if (monitor.isCanceled()) {
						out.close();
						
						if (null != file) {
							file.delete();
						}
						
						break;
					}
				}
			}
		} catch (Exception e) {
			result = new SnomedExportResult(Result.EXCEPTION);
		} finally {
			if (null != out) {
				out.close();
			}
		}
		
		monitor.done();
		return file;
	}

	// Override send() methods without a timeout parameter to enforce
	// per-request timeout

	@Override
	public File send() throws Exception, RemoteException {
		return super.send(REQUEST_TIMEOUT_MILLIS);
	}

	@Override
	public File send(final OMMonitor monitor) throws Exception, RemoteException {
		return super.send(REQUEST_TIMEOUT_MILLIS, monitor);
	}

	@Override
	protected int getMonitorTimeoutSeconds() {
		return REQUEST_TIMEOUT_MILLIS / 1000;
	}
	
	public SnomedExportResult getExportResult() {
		return result;
	}

}