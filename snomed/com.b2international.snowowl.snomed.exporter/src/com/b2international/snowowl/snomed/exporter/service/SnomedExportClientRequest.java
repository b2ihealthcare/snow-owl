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
package com.b2international.snowowl.snomed.exporter.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.net4j.MonitorCanceledRequest;
import com.b2international.snowowl.datastore.net4j.RequestCancelationRunnable;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConfiguration;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.exporter.model.SnomedRf2ExportModel;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * This class sends user export request to the server-side. Currently the following parameters are sent to the server in the following order:
 * <ul>
 * <li>clientBranchId - int; the current branch id of the client</li>
 * <li>clientBranchBaseTimeStamp - long; the base timestamp of the current branch of the client (only used if on task branch)</li>
 * <li>fromEffectiveTime - String; if empty, effectiveTime won't be used in the queries (from this date, inclusive)</li>
 * <li>toEffectiveTime - String; if empty, effectiveTime won't be used in the queries (until this date, inclusive)</li>
 * <li>coreComponentExport - boolean; if false, core component export won't be executed</li>
 * <li>numberOfRefSetsToExport - int; if 0, no refset export will be executed, the number of the refsets to be exported otherwise</li>
 * <li>refsetIdentifierConcepts - String; only if numberOfRefSetsToExport > 0; reference set identifier concept ids, the number of strings has to be read is equal to
 * <u>numberOfRefSetsToExport</u></li>
 * </ul>
 * 
 * The server response contains the zipped archive file and the archive itself.
 * 
 * <b>Note:</b> cancel request is overridden {@link MonitorCanceledRequest}
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
		out.writeUTF(model.getClientBranch().getPath());
		out.writeUTF(convertDateToRF2String(model.getStartEffectiveTime()));
		out.writeUTF(convertDateToRF2String(model.getEndEffectiveTime()));
		out.writeInt(model.getReleaseType().getValue());
		out.writeUTF(model.getUnsetEffectiveTimeLabel());
		out.writeBoolean(model.includeUnpublised());
		out.writeBoolean(model.isExportToRf1());
		out.writeBoolean(model.isExtendedDescriptionTypesForRf1());
		final boolean coreComponentsToExport = model.isCoreComponentsToExport();
		out.writeBoolean(coreComponentsToExport);
		final Set<String> refsetConceptIdentifiers = model.getRefSetIds();
		if (coreComponentsToExport) {
			refsetConceptIdentifiers.addAll(getHiddenRefSetIds());
		}

		out.writeInt(refsetConceptIdentifiers.size());

		for (final String refsetIdentifierConcept : refsetConceptIdentifiers) {
			out.writeUTF(refsetIdentifierConcept);
		}

		final Set<SnomedMapSetSetting> settings = model.getSettings();
		
		out.writeInt(settings.size());
		for (final SnomedMapSetSetting setting : settings)
			SnomedMapSetSetting.write(setting, out);
		
		final Set<String> modulesToExport = model.getModulesToExport();
		
		out.writeInt(modulesToExport.size());
		for (final String moduleToExport : modulesToExport) {
			out.writeUTF(moduleToExport);
		}

		out.writeUTF(model.getNamespace());
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

	private String convertDateToRF2String(final Date date) {
		if (date != null) {
			return EffectiveTimes.format(date, SnomedConstants.RF2_EFFECTIVE_TIME_FORMAT);
		} else {
			return "";
		}
	}

	/* returns with the hidden/structural reference set IDs. */
	private Set<String> getHiddenRefSetIds() {
		final AtomicReference<CDOView> viewReference = new AtomicReference<CDOView>();
		try {
			viewReference.set(createView());
			// get all hidden/structural reference set
			return Sets.newHashSet(Iterables.filter(ApplicationContext.getInstance().getService(SnomedConfiguration.class).getHiddenReferenceSets().getChildren().keySet(),
					new Predicate<String>() {
						@Override
						public boolean apply(final String id) {
							// filter out the non existing ones.
							return null != getRefSet(id, viewReference.get());
						}
					}));
		} finally {
			if (null != viewReference.get())
				viewReference.get().close();
		}
	}

	/*
	 * returns with a SNOMED CT reference set identified by the specified identifier concept ID. the reference set is looked up in the specified CDO view.
	 */
	private SnomedRefSet getRefSet(final String id, final CDOView view) {
		return getLookupSerive().getComponent(id, view);
	}

	/* returns with the SNOMED CT reference set lookup service. */
	private ILookupService<String, SnomedRefSet, CDOView> getLookupSerive() {
		return CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET);
	}

	/* creates a CDO view. */
	private CDOView createView() {
		return getConnection().createView(model.getClientBranch());
	}

	/* returns with the connection service. */
	private ICDOConnection getConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}

}