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
package com.b2international.snowowl.snomed.exporter.server.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.commons.FileUtils;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemService;
import com.b2international.snowowl.datastore.ICodeSystemVersion;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult;
import com.b2international.snowowl.snomed.exporter.model.SnomedExportResult.Result;
import com.b2international.snowowl.snomed.exporter.server.ExportFormat;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContextImpl;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportExecutor;
import com.b2international.snowowl.snomed.exporter.server.SnomedRefSetExporterFactory;
import com.b2international.snowowl.snomed.exporter.server.rf1.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.rf1.SnomedRf1DescriptionExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.NoopExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedInferredRelationshipExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedRf2ConceptExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedRf2DescriptionExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedStatedRelationshipExporter;
import com.b2international.snowowl.snomed.exporter.server.rf2.SnomedTextDefinitionExporter;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
	private String clientNamespace;

	// the number of the selected refset to export, if 0, no reference export will be executed
	private int numberOfRefSetsToExport;

	// the reference sets (identified by the identifier concept id) which have to be exported, if empty (numberOfRefSetsToExport = 0!)
	// no reference set export will be executed
	private Set<String> refsetIdentifierConcepts;
	private Set<SnomedMapSetSetting> settings;

	// Used for logging
	private String userId;
	private IBranchPath branchPath;

	private SnomedExportResult result;

	private SnomedExportContext exportContext;

	//indicates whether the unpublished artifacts should be part of the export process
	private boolean includeUnpublished;

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
		branchPath = BranchPathUtils.createPath(in.readUTF());
		
		String deltaExportStartEffectiveTimeString = in.readUTF();
		String deltaExportEndEffectiveTimeString = in.readUTF();
		deltaExportStartEffectiveTime = deltaExportStartEffectiveTimeString.equals("") ? null : convertRF2StringToDate(deltaExportStartEffectiveTimeString);
		deltaExportEndEffectiveTime = deltaExportEndEffectiveTimeString.equals("") ? null : convertRF2StringToDate(deltaExportEndEffectiveTimeString);
		releaseType = ContentSubType.getByValue(in.readInt());
		unsetEffectiveTimeLabel = in.readUTF();
		includeUnpublished = in.readBoolean();
		
		
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
		
		clientNamespace = in.readUTF();
		
		exportContext = new SnomedExportContextImpl(
				ExportFormat.RF2,
				branchPath, 
				releaseType, 
				unsetEffectiveTimeLabel,
				deltaExportStartEffectiveTime, 
				deltaExportEndEffectiveTime,
				modulesToExport,
				new Id2Rf1PropertyMapper());
		
		LogUtils.logExportActivity(LOGGER, userId, branchPath, 
				MessageFormat.format("SNOMED CT export{0}requested.", coreComponentExport ? " with core components " : " "));
	}

	@Override
	protected void responding(ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		File file = null;
		
		result = new SnomedExportResult();

		try {
			monitor.begin(calculateProgressMonitorStep());
			
			checkOtherPublication();
			
			if (Result.IN_PROGRESS != result.getResult()) {
				
				//obtain the index service here to ensure a consistent view of the data during the export process
				RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
				RevisionIndex revisionIndex = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
				file = doExport(revisionIndex, monitor);
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
	
	private File doExport(final RevisionIndex revisionIndex, final OMMonitor monitor) {
		
		try {
			Collection<ICodeSystemVersion> codeSystemVersions = ApplicationContext.getServiceForClass(CodeSystemService.class).getAllTags(SnomedDatastoreActivator.REPOSITORY_UUID);
			
			List<String> exportBranchPaths = Lists.newArrayList();
			final ContentSubType subType = exportContext.getContentSubType();
			switch (subType) {
			case DELTA:
				
				Collection<ICodeSystemVersion> filteredVersions = Collections2.filter(codeSystemVersions, new Predicate<ICodeSystemVersion>() {

					@Override
					public boolean apply(ICodeSystemVersion input) {
						
						//special case, unpublished only, do not handle here
						if (deltaExportStartEffectiveTime == null && deltaExportEndEffectiveTime == null) {
							return false;
						} else if (deltaExportStartEffectiveTime == null) {
							Date effectiveDate = new Date(input.getEffectiveDate());
							if (effectiveDate.before(deltaExportEndEffectiveTime) || effectiveDate.equals(deltaExportEndEffectiveTime)) {
								return true;
							}
						} else if (deltaExportEndEffectiveTime == null) {
							Date effectiveDate = new Date(input.getEffectiveDate());
							if (effectiveDate.after(deltaExportStartEffectiveTime) || effectiveDate.equals(deltaExportStartEffectiveTime)) {
								return true;
							}
						} else {
							Date effectiveDate = new Date(input.getEffectiveDate());
							if ((effectiveDate.after(deltaExportStartEffectiveTime) || effectiveDate.equals(deltaExportStartEffectiveTime))
									&& (effectiveDate.before(deltaExportEndEffectiveTime) || effectiveDate.equals(deltaExportEndEffectiveTime))) {
								return true;
							}
						}
						return false;
					}
				});
				
				Collection<String> versionBranchPaths = Collections2.transform(filteredVersions, new Function<ICodeSystemVersion, String>() {
					@Override
					public String apply(ICodeSystemVersion input) {
						return input.getPath(); 
					}
				});
				exportBranchPaths = Lists.newArrayList(versionBranchPaths);
				
				break;
			case SNAPSHOT:
				exportBranchPaths = Lists.newArrayList(branchPath.getPath());
				break;
			case FULL:
				versionBranchPaths = Collections2.transform(codeSystemVersions, new Function<ICodeSystemVersion, String>() {

					@Override
					public String apply(ICodeSystemVersion input) {
						return input.getPath(); 
					}
				});
				exportBranchPaths = Lists.newArrayList(versionBranchPaths);
				break;
			default:
				break;
			}
			
			if (exportBranchPaths.isEmpty() && !isUnpublishedExport(subType)) {
				throw new BadRequestException("No branch paths were found for the export.");
			}
			
			Collections.sort(exportBranchPaths);
			for (String versionBranchPath : exportBranchPaths) {
				
				revisionIndex.read(versionBranchPath, new RevisionIndexRead<Void>() {

					@Override
					public Void execute(RevisionSearcher revisionSearcher) throws IOException {

						if (monitor.isCanceled()) {
							processCancel();
							return null;
						}
						
						if (coreComponentExport) {
							LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT core components " + subType+ " export...");
							executeCoreExport(TEMPORARY_WORKING_DIRECTORY, exportContext, revisionSearcher, monitor);
						}
						
						if (numberOfRefSetsToExport != 0) {
							
							LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT reference set " + subType+ " export...");
							
							for (String identifierConceptId : refsetIdentifierConcepts) {
								executeRefSetExport(TEMPORARY_WORKING_DIRECTORY, exportContext, identifierConceptId, revisionSearcher, false, monitor);
								
								if (monitor.isCanceled()) {
									processCancel();
									return null;
								}
							}
						}
						return null;
					}
				});
			}
			
			//special case
			if (subType == ContentSubType.DELTA && deltaExportStartEffectiveTime == null && deltaExportEndEffectiveTime == null) {
				revisionIndex.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
					@Override
					public Void execute(RevisionSearcher revisionSearcher) throws IOException {
						LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT " + subType+ " export for unpublished components...");
						
						//do not append as this is the only content
						exportUnpublished(TEMPORARY_WORKING_DIRECTORY, exportContext, revisionSearcher, false, monitor);
						return null;
					}
				});
			} else if (includeUnpublished) {
				revisionIndex.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
					@Override
					public Void execute(RevisionSearcher revisionSearcher) throws IOException {
						LogUtils.logExportActivity(LOGGER, userId, branchPath, "Starting SNOMED CT " + subType+ " export for unpublished components...");
						exportUnpublished(TEMPORARY_WORKING_DIRECTORY, exportContext, revisionSearcher, true, monitor);
						return null;
					}
				});
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
			
			if (e.getClass().isAssignableFrom(RuntimeException.class)) {
				result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components: could not retrieve data from database.");
			} else if (e.getClass().isAssignableFrom(IOException.class)) {
				result.setResultAndMessage(Result.EXCEPTION, "An error occurred while exporting SNOMED CT components: could not create release files.");
			}
		}
		return null;
	}
	
	private boolean isUnpublishedExport(final ContentSubType subType) {
		return includeUnpublished
				|| (subType == ContentSubType.DELTA && deltaExportStartEffectiveTime == null && deltaExportEndEffectiveTime == null);
	}

	private void executeCoreExport(final String workingDirectory, final SnomedExportContext context, final RevisionSearcher revisionSearcher, final OMMonitor monitor) throws IOException {

		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT concepts into RF2 format.");
		
		SnomedExporter conceptExporter = new SnomedRf2ConceptExporter(context, revisionSearcher, false);
		new SnomedExportExecutor(conceptExporter, workingDirectory, clientNamespace).execute(false);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT description into RF2 format.");
		SnomedExporter descriptionExporter = new SnomedRf2DescriptionExporter(context, revisionSearcher, false);
		new SnomedExportExecutor(descriptionExporter, workingDirectory, clientNamespace).execute(false);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT text definitions into RF2 format.");
		SnomedExporter textDefinitionExporter = new SnomedTextDefinitionExporter(context, revisionSearcher, false);
		new SnomedExportExecutor(textDefinitionExporter, workingDirectory, clientNamespace).execute(false);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT non-stated relationships into RF2 format.");
		SnomedExporter relationshipExporter = new SnomedInferredRelationshipExporter(context, revisionSearcher, false);
		new SnomedExportExecutor(relationshipExporter, workingDirectory, clientNamespace).execute(false);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT stated relationships into RF2 format.");
		SnomedExporter statedRelationshipExporter = new SnomedStatedRelationshipExporter(context, revisionSearcher, false);
		new SnomedExportExecutor(statedRelationshipExporter, workingDirectory, clientNamespace).execute(false);
		
		if (includeRf1) {
			
			exportContext.setExportFormat(ExportFormat.RF1);
			
			logActivity("Publishing SNOMED CT concepts into RF1 format.");
			conceptExporter = new SnomedRf2ConceptExporter(context, revisionSearcher, false);
			new SnomedExportExecutor(conceptExporter, workingDirectory, clientNamespace).execute(false);
			
			if (monitor.isCanceled()) {
				return;
			} else {
				monitor.worked(2);
			}
			
			logActivity("Publishing SNOMED CT descriptions into RF1 format.");
			descriptionExporter = new SnomedRf1DescriptionExporter(context, revisionSearcher, false, includeExtendedDescriptionTypes);
			final SnomedExportExecutor exportExecutor = new SnomedExportExecutor(descriptionExporter, workingDirectory, clientNamespace);
			exportExecutor.execute(true);
			
			if (includeExtendedDescriptionTypes) {
				exportExecutor.writeExtendedDescriptionTypeExplanation();
			}
			
			if (monitor.isCanceled()) {
				return;
			} else {
				monitor.worked(2);
			}
			
			logActivity("Publishing SNOMED CT relationships into RF1 format.");
			relationshipExporter = new SnomedInferredRelationshipExporter(context, revisionSearcher, false);
			new SnomedExportExecutor(relationshipExporter, workingDirectory, clientNamespace).execute(false);
			
			if (monitor.isCanceled()) {
				return;
			} else {
				monitor.worked(2);
			}
		}
	}
	
	private void exportUnpublished(final String workingDirectory, final SnomedExportContext context, 
			final RevisionSearcher revisionSearcher, final boolean append, final OMMonitor monitor) throws IOException {
		
		logActivity("Publishing SNOMED CT unpublished concepts into RF2 format.");
		SnomedExporter unpublishedConceptExporter = new SnomedRf2ConceptExporter(context, revisionSearcher, true);
		new SnomedExportExecutor(unpublishedConceptExporter, workingDirectory, clientNamespace).execute(append);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT unpublished descriptions into RF2 format.");
		SnomedExporter unpublishedDescriptionExporter = new SnomedRf2DescriptionExporter(context, revisionSearcher, true);
		new SnomedExportExecutor(unpublishedDescriptionExporter, workingDirectory, clientNamespace).execute(append);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT unpublished text definitions into RF2 format.");
		SnomedExporter unpublishedTextDefinitionExporter = new SnomedTextDefinitionExporter(context, revisionSearcher, true);
		new SnomedExportExecutor(unpublishedTextDefinitionExporter, workingDirectory, clientNamespace).execute(append);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT unpublished stated relationships into RF2 format.");
		SnomedExporter unpublishedStatedRelationshipExporter = new SnomedStatedRelationshipExporter(context, revisionSearcher, true);
		new SnomedExportExecutor(unpublishedStatedRelationshipExporter, workingDirectory, clientNamespace).execute(append);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		logActivity("Publishing SNOMED CT unpublished inferred relationships into RF2 format.");
		SnomedExporter unpublishedinferredRelationshipExporter = new SnomedInferredRelationshipExporter(context, revisionSearcher, true);
		new SnomedExportExecutor(unpublishedinferredRelationshipExporter, workingDirectory, clientNamespace).execute(append);
		
		if (monitor.isCanceled()) {
			return;
		} else {
			monitor.worked(2);
		}
		
		for (String identifierConceptId : refsetIdentifierConcepts) {
			executeRefSetExport(TEMPORARY_WORKING_DIRECTORY, exportContext, identifierConceptId, revisionSearcher, true, monitor);
		}
	}

	private void executeRefSetExport(final String workingDirectory, final SnomedExportContext configuration, final String refSetId, 
			final RevisionSearcher revisionSearcher, final boolean unpublished, final OMMonitor monitor) throws IOException {
		
		final SnomedExporter refSetExporter = SnomedRefSetExporterFactory.getRefSetExporter(refSetId, configuration, revisionSearcher, unpublished);
		
		if (NoopExporter.INSTANCE == refSetExporter) {
			return;
		}
		
		logActivity("Publishing SNOMED CT reference set into RF2 format. Reference set identifier concept ID: " + refSetId);
		new SnomedExportExecutor(refSetExporter, workingDirectory, clientNamespace).execute(false);

		//RF1 export
		if (includeRf1) {
			//RF1 subset exporter.
			boolean alreadyLogged = false;
			for (final SnomedExporter exporter : SnomedRefSetExporterFactory.getSubsetExporter(refSetId, configuration, revisionSearcher, unpublished)) {
				if (NoopExporter.INSTANCE != exporter) {
					if (!alreadyLogged) {
						logActivity("Publishing SNOMED CT reference set into RF1 format. Reference set identifier concept ID: " + refSetId);
						alreadyLogged = true;
					}
					new SnomedExportExecutor(exporter, workingDirectory, clientNamespace).execute(false);
				}
			}
			//RF1 map set exporter.
			final SnomedMapSetSetting mapsetSetting = getSettingForRefSet(refSetId);
			if (null != mapsetSetting) {
				alreadyLogged = false;
				for (final SnomedExporter exporter : SnomedRefSetExporterFactory.getCrossMapExporter(refSetId, configuration, mapsetSetting, revisionSearcher, unpublished)) {
					if (NoopExporter.INSTANCE != exporter) {
						if (!alreadyLogged) {
							logActivity("Publishing SNOMED CT reference set into RF1 format. Reference set identifier concept ID: " + refSetId);
							alreadyLogged = true;
						}
						new SnomedExportExecutor(exporter, workingDirectory, clientNamespace).execute(false);
					}
				}
			}
		}
		monitor.worked(1);
	}
	
	private void processCancel() throws IOException {
		LogUtils.logExportActivity(LOGGER, userId, branchPath, "SNOMED CT export canceled.");
		result.setResult(Result.CANCELED);
	}

	private int calculateProgressMonitorStep() {
		int counter = 0;

		if (coreComponentExport) {
			counter += 10;
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
			LOGGER.error(String.format("Couldn't parse RF2 date %s.", dateInRF2Format), e);
			throw new BadRequestException("Couldn't parse RF2 date %s.", dateInRF2Format);
		}
	}
	
	/*returns with the map set setting for the specified reference set identifier concept ID*/
	private SnomedMapSetSetting getSettingForRefSet(final String refSetId) {
		return Iterables.getOnlyElement(Iterables.filter(settings, new Predicate<SnomedMapSetSetting>() {
			@Override public boolean apply(final SnomedMapSetSetting setting) {
				return setting.getRefSetId().equals(refSetId);
			}
		}), null);
	}
	
	private void logActivity(final String message) {
		LOGGER.info(message);
		LogUtils.logExportActivity(LOGGER, userId, branchPath, message);
	}
}