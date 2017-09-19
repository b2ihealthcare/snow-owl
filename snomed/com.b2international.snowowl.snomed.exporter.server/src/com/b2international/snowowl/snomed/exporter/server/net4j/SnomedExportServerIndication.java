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
package com.b2international.snowowl.snomed.exporter.server.net4j;

import static com.b2international.snowowl.datastore.BranchPathUtils.createPath;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.exporter.server.SnomedExporterFacade;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfigurationImpl;
import com.google.common.collect.Sets;

/**
 * This class receives requests from client side and depending the user request executes exports correspondingly. Currently the following "datastructure" is sent from the client in
 * the following order:
 * 
 * <ul>
 * <li>clientBranch - int; current branch id of the client side</li>
 * <li>clientBranchBaseTimeStamp - long; the base timestamp of the current client branch (only used if the client is on a task branch)</li>
 * <li>fromEffectiveTime - String; if empty, effectiveTime won't be used in the queries (from this date, inclusive)</li>
 * <li>toEffectiveTime - String; if empty, effectiveTime won't be used in the queries (until this date, inclusive)</li>
 * <li>coreComponentExport - boolean; if false, core component export won't be executed</li>
 * <li>numberOfRefSetsToExport - int; if 0, no refset export will be executed, the number of the refsets to be exported otherwise</li>
 * <li>refsetIdentifierConcepts - String; only if numberOfRefSetsToExport > 0; reference set identifier concept ids, the number of strings has to be read is equal to
 * <u>numberOfRefSetsToExport</u></li>
 * </ul>
 * 
 * The response is a zipped archive containing the exported files following the RF2 directory "standard". The zipped archive and the working directory can be found during the
 * export in your system dependent temporary folder. After finishing the export and uploading the zipped file to the client the working directory and the zipped archive are
 * deleted.
 * 
 * 
 */
public class SnomedExportServerIndication extends IndicationWithMonitoring {

	/* 
	 * XXX: reference equality (==) is required by AtomicReference, so use this exact string, not an equal empty one! The string contains 
	 * the text 'another user' to avoid confusing log messages if the publication finishes before the "winner" (the person who started
	 * an export before receiving this indication) can be retrieved from the AtomicReference. 
	 */
	private static final String NO_USER = "another user";

	private static final AtomicReference<String> ACTIVE_FULL_RF2_PUBLICATION_USER = new AtomicReference<String>(NO_USER);

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SnomedExportServerIndication.class);

	// this is the directory where the exported files with the RF2 directory
	// "standard" are put
	private final String TEMPORARY_WORKING_DIRECTORY = System.getProperty("java.io.tmpdir") + File.separatorChar + "export" + System.currentTimeMillis();

	private boolean coreComponentExport;
	private ContentSubType releaseType;
	private String unsetEffectiveTimeLabel;
	private boolean includeRf1;
	private boolean includeExtendedDescriptionTypes;
	private Set<String> modulesToExport;
	private Date deltaExportStartEffectiveTime;
	private Date deltaExportEndEffectiveTime;
	private String countryNamespaceElement;
	private boolean deltaExport;

	// the number of the selected refset to export, if 0, no reference export
	// will be executed
	private int numberOfRefSetsToExport;

	// the reference sets (identified by the identifier concept id) which have
	// to be exported, if empty (numberOfRefSetsToExport = 0!)
	// no reference set export will be executed
	private Set<String> refsetIdentifierConcepts;
	private Set<SnomedMapSetSetting> settings;

	// Used for logging
	private String userId;
	private IBranchPath branchPath;

	private SnomedExportResult result;

	private SnomedExportConfigurationImpl configuration;

	public SnomedExportServerIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.SNOMED_EXPORT_SIGNAL);
		refsetIdentifierConcepts = Sets.newHashSet();
	}

	@Override
	protected int getIndicatingWorkPercent() {
		return 0;
	}

	@Override
	protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception {

		userId = in.readUTF();
		branchPath = createPath(in.readUTF());
		
		deltaExport = in.readBoolean();
		String deltaExportStartEffectiveTimeString = in.readUTF();
		String deltaExportEndEffectiveTimeString = in.readUTF();
		deltaExportStartEffectiveTime = deltaExportStartEffectiveTimeString.equals("") ? null : convertRF2StringToDate(deltaExportStartEffectiveTimeString);
		deltaExportEndEffectiveTime = deltaExportEndEffectiveTimeString.equals("") ? null : convertRF2StringToDate(deltaExportEndEffectiveTimeString);
		releaseType = ContentSubType.getByValue(in.readInt());
		unsetEffectiveTimeLabel = in.readUTF();
		
		includeRf1 = in.readBoolean();
		includeExtendedDescriptionTypes = in.readBoolean();

		coreComponentExport = in.readBoolean();
		numberOfRefSetsToExport = in.readInt();

		for (int i = 0; i < numberOfRefSetsToExport; i++) {
			String refsetIdentifierConcept = in.readUTF();
			refsetIdentifierConcepts.add(refsetIdentifierConcept);
		}
		
		final int settingSize = in.readInt();
		settings = Sets.newHashSetWithExpectedSize(settingSize);
		for (int i = 0; i < settingSize; i++) {
			settings.add(SnomedMapSetSetting.read(in));
		}
		
		final int modulesToExportSize = in.readInt();
		modulesToExport = Sets.newHashSetWithExpectedSize(modulesToExportSize);
		for (int i = 0; i < modulesToExportSize; i++) {
			modulesToExport.add(in.readUTF());
		}
		
		countryNamespaceElement = in.readUTF();
		
		configuration = new SnomedExportConfigurationImpl(
				branchPath, 
				releaseType, 
				unsetEffectiveTimeLabel,
				countryNamespaceElement,
				deltaExportStartEffectiveTime, 
				deltaExportEndEffectiveTime);
		
		LogUtils.logExportActivity(LOGGER, userId, branchPath, 
				MessageFormat.format("SNOMED CT export{0}requested.", coreComponentExport ? " with core components " : " "));
	}

	@Override
	protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception {

		File file = null;
		
		result = new SnomedExportResult();

		try {
			monitor.begin(calculateProgressMonitorStep());
			
			checkOtherPublication();
			
			if (Result.IN_PROGRESS != result.getResult()) {
				file = doExport(monitor);
			}
			
			LogUtils.logExportActivity(LOGGER, userId, branchPath, "Transferring export result...");
			
			sendResult(out, file, monitor);
			
			monitor.worked(1);
			
			if (null != file) {
				file.delete();
			}
			
			LogUtils.logExportActivity(LOGGER, userId, branchPath, "SNOMED CT export finished.");
			
		} finally {
			monitor.done();
			
			/* 
			 * If we couldn't set userId on the AtomicReference at the beginning somehow, this will have no effect, which is good -- we 
			 * don't want to destroy another user's export directory if currentTimeMillis returned the same value for both users, for example.
			 */
			if (ACTIVE_FULL_RF2_PUBLICATION_USER.compareAndSet(userId, NO_USER)) {
				FileUtils.deleteDirectory(new File(TEMPORARY_WORKING_DIRECTORY));
			}
		}
	}

	private void sendResult(ExtendedDataOutputStream out, File file, OMMonitor monitor) throws IOException {
		out.writeObject(result);
		
		if (Result.SUCCESSFUL == result.getResult()) {
			long size = file.length();
			BufferedInputStream in = null;

			monitor.fork(size);

			out.writeLong(size);

			try {
				in = new BufferedInputStream(new FileInputStream(file));
				while (size != 0L) {
					int chunk = Net4jProtocolConstants.BUFFER_SIZE;
					if (size < Net4jProtocolConstants.BUFFER_SIZE) {
						chunk = (int) size;
					}

					monitor.worked(chunk / 1.0);

					byte[] buffer = new byte[chunk];
					in.read(buffer);
					out.writeByteArray(buffer);

					size -= chunk;
				}
			} finally {
				in.close();
			}
		}
	}

	private void checkOtherPublication() {
		if (coreComponentExport) {
			if (!ACTIVE_FULL_RF2_PUBLICATION_USER.compareAndSet(NO_USER, userId)) {
				final String publishingUserId = ACTIVE_FULL_RF2_PUBLICATION_USER.get();
				
				LogUtils.logExportActivity(LOGGER, userId, branchPath, 
						MessageFormat.format("SNOMED CT publication is already in progress by {0}.", publishingUserId));
				
				result.setResult(Result.IN_PROGRESS);
			}
		}
	}
	
	private File doExport(final OMMonitor monitor) {
		try {

			SnomedExporterFacade exporter = new SnomedExporterFacade(
					userId,
					branchPath,
					LOGGER,
					includeRf1, 
					includeExtendedDescriptionTypes, 
					settings, 
					modulesToExport, 
					deltaExport, 
					deltaExportStartEffectiveTime, 
					deltaExportEndEffectiveTime, 
					countryNamespaceElement);

			if (monitor.isCanceled()) {
				processCancel();
				return null;
			}
			
			if (coreComponentExport) {
				LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT core components export...");
				exporter.executeCoreExport(TEMPORARY_WORKING_DIRECTORY, configuration, monitor);
			}

			if (monitor.isCanceled()) {
				processCancel();
				return null;
			}

			if (numberOfRefSetsToExport != 0) {
				
				LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT reference set export...");
				
				for (String identifierConceptId : refsetIdentifierConcepts) {
					exporter.executeRefSetExport(TEMPORARY_WORKING_DIRECTORY, configuration, identifierConceptId, monitor);

					if (monitor.isCanceled()) {
						processCancel();
						return null;
					}
				}
			}
			
			LogUtils.logExportActivity(LOGGER, userId, branchPath, "Archiving SNOMED CT publication...");

			File root = new File(TEMPORARY_WORKING_DIRECTORY);
			File archive = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "export_" + System.currentTimeMillis() + ".zip");
			File zipFile = FileUtils.createZipArchive(root, archive);

			if (monitor.isCanceled()) {
				processCancel();
				return null;
			} else {
				monitor.worked(1);
			}

			return zipFile;
		} catch (Exception e) {
			final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
			LogUtils.logExportActivity(LOGGER, userId, branchPath, "Caught exception while exporting SNOMED CT terminology." + reason);
			LOGGER.error("Caught exception while exporting SNOMED CT terminology.", e);
			
			if (e.getClass().isAssignableFrom(RuntimeException.class)) {
				result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components: could not retrieve data from database.");
			} else if (e.getClass().isAssignableFrom(IOException.class)) {
				result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components: could not create release files.");
			} else {
				result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components.");
			}
		}
		
		return null;
	}

	private void processCancel() throws IOException {
		LogUtils.logExportActivity(LOGGER, userId, branchPath, "SNOMED CT export canceled.");
		result.setResult(Result.CANCELED);
	}

	private int calculateProgressMonitorStep() {
		int counter = 0;

		if (coreComponentExport) {
			counter += 8;
			if (includeRf1)
				counter += 6;
		}

		counter += numberOfRefSetsToExport;

		counter++; // compressing zip
		counter++; // sending file to the client;

		return counter;
	}

	private Date convertRF2StringToDate(String dateInRF2Format) {
		try {
			return EffectiveTimes.parse(dateInRF2Format, SnomedConstants.RF2_EFFECTIVE_TIME_FORMAT);
		} catch (SnowowlRuntimeException e) {
			if (e.getCause() instanceof ParseException) {
				return null;
			} else {
				throw e;
			}
		}
	}
}