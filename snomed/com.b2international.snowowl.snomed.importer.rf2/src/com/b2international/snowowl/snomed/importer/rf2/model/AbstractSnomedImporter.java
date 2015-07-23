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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;

import com.b2international.commons.FileUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.datastore.server.ServerDbUtils;
import com.b2international.snowowl.importer.AbstractImportUnit;
import com.b2international.snowowl.importer.AbstractLoggingImporter;
import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.rf2.CsvConstants;
import com.b2international.snowowl.snomed.importer.rf2.csv.AbstractComponentRow;
import com.b2international.snowowl.snomed.importer.rf2.csv.cellprocessor.ValidatingCellProcessor;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Represents a SNOMED CT importer that imports a single release file supplied
 * as an {@link InputStream}.
 * 
 */
public abstract class AbstractSnomedImporter<T extends AbstractComponentRow, C extends CDOObject> extends AbstractLoggingImporter {

	private static final org.slf4j.Logger IMPORT_LOGGER = org.slf4j.LoggerFactory.getLogger(AbstractSnomedImporter.class);
	
	/**
	 * Remaining work for {@link SubMonitor}s is repeatedly set to this value
	 * when the number of work units is not known in advance.
	 */
	private static final int INDETERMINATE_WORK_UNITS = 10000;
	
	/**	Number of work units allocated for a commit. */
	protected static final int COMMIT_WORK_UNITS = 50;

	/** A CDO transaction is committed when the number of processed elements % this value == 0. */
	protected static final int COMMIT_EVERY_NUM_ELEMENTS = 50000;

	/** 0-based index of the {@code effectiveTime} column in release files. */
	private static final int EFFECTIVE_TIME_IDX = 1;

	/**
	 * Component release files with no current effective time are assigned this key in
	 * {@link #getOrCreateImportEntry(Map, String)}.
	 */
	public static final String UNPUBLISHED_KEY = "unpublished"; 

	protected static CellProcessor createEffectiveTimeCellProcessor() {
		return new Optional(new ParseDate(SnomedConstants.RF2_EFFECTIVE_TIME_FORMAT).setTimeZone(Dates.getGmtTimeZone()));
	}

	private final SnomedImportConfiguration<T> importConfiguration;
	private final SnomedImportContext importContext;
	private final InputStream releaseFileStream;

	private final Supplier<IBranchPath> branchPathSupplier = Suppliers.memoize(new Supplier<IBranchPath>() {
		@Override public IBranchPath get() {
			return BranchPathUtils.createPath(importContext.getEditingContext().getTransaction());
		}
	});
	
	private final Supplier<String> userIdSupplier = Suppliers.memoize(new Supplier<String>() {
		@Override public String get() {
			return importContext.getUserId();
		}
	});
	
	private File componentStagingDirectory;

	public AbstractSnomedImporter(final SnomedImportConfiguration<T> importConfiguration, final SnomedImportContext importContext, 
			final InputStream releaseFileStream, final String releaseFileIdentifier) {
		
		super(checkNotNull(importContext, "importContext").getLogger());
		this.importConfiguration = checkNotNull(importConfiguration, "importConfiguration");
		this.importContext = importContext;
		this.releaseFileStream = checkNotNull(releaseFileStream, "releaseFileStream");
		
	}

	/**
	 * Logs the given message to the dedicated import log file.
	 * @param message the message to log.
	 */
	protected void log(final String message) {
		LogUtils.logImportActivity(IMPORT_LOGGER, userIdSupplier.get(), branchPathSupplier.get(), message);
	}
	
	protected SnomedImportConfiguration<T> getImportConfiguration() {
		return importConfiguration;
	}
	
	protected SnomedImportContext getImportContext() {
		return importContext;
	}
	
	protected File getComponentStagingDirectory() {
		return componentStagingDirectory;
	}
	
	private Component getComponent(final String componentId) {
		return importContext.getComponentLookup().getComponent(componentId);
	}
	
	protected Concept getConcept(final String conceptId) {
		return (Concept) getComponent(conceptId);
	}

	protected Concept getConceptSafe(final String conceptId, final String conceptField, final String componentId) {
		
		final Concept result = getConcept(conceptId);
		
		if (null == result) {
			throw new NullPointerException(MessageFormat.format("Concept ''{0}'' for field {1}, {2} ''{3}'' not found.", 
					conceptId, conceptField, getImportConfiguration().getType().getDisplayName(), componentId));
		}
		
		return result;
	}

	protected Description getDescription(final String descriptionId) {
		return (Description) getComponent(descriptionId);
	}
	
	protected Relationship getRelationship(final String relationshipId) {
		return (Relationship) getComponent(relationshipId);
	}
	
	protected Annotatable getAnnotatableComponent(final String componentId) {
		return (Annotatable) getComponent(componentId);
	}
	
	protected Inactivatable getInactivatableComponent(final String componentId) {
		return (Inactivatable) getComponent(componentId);
	}

	
	
	@Override
	public void preImport(final SubMonitor subMonitor) {

		final String message = getPreImportMessage();
		log(message);
		subMonitor.beginTask(message, 1);
		
		try {
			createComponentStagingDirectory(subMonitor.newChild(1));
		} finally {
			subMonitor.done();
		}
	}

	private String getPreImportMessage() {
		return MessageFormat.format("Preparing {0} import", importConfiguration.getType().getDisplayName());
	}

	private void createComponentStagingDirectory(final SubMonitor subMonitor) {
	
		try {
			
			componentStagingDirectory = new File(importContext.getStagingDirectory(), getComponentStagingDirectoryName());
			
			String message = MessageFormat.format("Creating staging directory ''{0}'' for {1} import.", 
					componentStagingDirectory.getAbsolutePath(), importConfiguration.getType().getDisplayName());
			getLogger().info(message);
			log(message);
			
			final boolean conceptStagingDirectoryCreated = componentStagingDirectory.mkdirs();
			
			if (!conceptStagingDirectoryCreated) {
				message = MessageFormat.format("Couldn''t create staging directory ''{0}'' for {1} import.", 
						componentStagingDirectory.getAbsolutePath(), importConfiguration.getType().getDisplayName());
				log("SNOMED CT import failed. Reason: " + message);
				throw new ImportException(message);
			}
			
		} finally {
			subMonitor.done();
		}
	}

	private String getComponentStagingDirectoryName() {
		final UUID suffix = UUID.randomUUID();
		return MessageFormat.format("{0}_{1}", importConfiguration.getType().getDirectoryPartName(), suffix);
	}

	/**Returns with the effective time of currently investigated component.
	 *<p>Could return with {@code null}.*/
	protected abstract Date getComponentEffectiveTime(C editedComponent);
	
	protected boolean skipCurrentRow(final AbstractComponentRow currentRow, final C editedComponent) {
		return skipCurrentRow(currentRow, getComponentEffectiveTime(editedComponent));		
	}
	
	private boolean skipCurrentRow(final AbstractComponentRow currentRow, final Date editedComponentDate) {
		return skipCurrentRow(currentRow.getEffectiveTime(), editedComponentDate);
	}
	
	private boolean skipCurrentRow(final Date currentRowDate, final Date editedComponentDate) {
		
		/*
		 * The RF2 row has to be imported if either the current component is unpublished, or the incoming row has no effective
		 * date set.
		 */
		if (editedComponentDate == null) {
			return false;
		} else if (currentRowDate == null) {
			return false;
		} else {
			return editedComponentDate.getTime() >= currentRowDate.getTime();
		}
	}
	
	@Override
	public List<ComponentImportUnit> getImportUnits(final SubMonitor subMonitor) {
		
		final String message = MessageFormat.format("Collecting {0} import units", getImportConfiguration().getType().getDisplayName());
		subMonitor.beginTask(message, INDETERMINATE_WORK_UNITS);
		log(message);
		
		final Map<String, ComponentImportEntry> importEntries = Maps.newHashMap();
		final InputStreamReader releaseFileReader = new InputStreamReader(releaseFileStream, CsvConstants.IHTSDO_CHARSET);
		final CsvListReader releaseFileListReader = new CsvListReader(releaseFileReader, CsvConstants.IHTSDO_CSV_PREFERENCE);

		try {
			
			final String[] actualHeader = releaseFileListReader.getCSVHeader(true);
			
			if (ImportAction.BREAK.equals(checkHeaders(importConfiguration.getExpectedHeader(), actualHeader))) {
				return ImmutableList.of();
			}
			
			final CellProcessor[] validatingCellProcessors = createValidatingCellProcessors();
			
			while (true) {
				
				List<String> row = null;
				
				try {
					row = releaseFileListReader.read(validatingCellProcessors);
				} catch (final SuperCSVException e) {
					
					if (ImportAction.CONTINUE.equals(handlePreImportException(e))) {
						// Skip the rest of the processing for this row
						continue;
					} else {
						break;
					}
				}
				
				if (null == row) {
					// End of file reached
					break;
				}
				
				final String effectiveTimeString = row.get(EFFECTIVE_TIME_IDX);
				final ComponentImportEntry importEntry = getOrCreateImportEntry(importEntries, effectiveTimeString);
				importEntry.getWriter().write(row);
				importEntry.increaseRecordCount();
				
				subMonitor.worked(1);
				subMonitor.setWorkRemaining(INDETERMINATE_WORK_UNITS);
			}
			
		} catch (final IOException e) {
			final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
			log("SNOMED CT import failed. Couldn't read row from release file." + reason);
			throw new ImportException("Couldn't read row from release file.", e);
		} finally {
			
			Closeables.closeQuietly(releaseFileListReader);
			
			for (final ComponentImportEntry importEntry : importEntries.values()) {
				Closeables.closeQuietly(importEntry.getWriter());
			}
		}

		return createImportUnits(importEntries);
	}

	private ImportAction checkHeaders(final String[] expectedHeader, final String[] actualHeader) {

		if (!StringUtils.equalsIgnoreCase(expectedHeader, actualHeader)) {
			importContext.getLogger().warn(MessageFormat.format("Release file headers [{0}] are different from the expected set [{1}]. Continuing.",
							Joiner.on(", ").join(actualHeader), 
							Joiner.on(", ").join(expectedHeader)));
		}
		
		return ImportAction.CONTINUE;
	}

	private CellProcessor[] createValidatingCellProcessors() {
		
		final Collection<CellProcessor> cellProcessors = importConfiguration.getCellProcessorMapping().values();
		final CellProcessor[] validatingCellProcessors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
		
		for (int i = 0; i < validatingCellProcessors.length; i++) {
			validatingCellProcessors[i] = new ValidatingCellProcessor(validatingCellProcessors[i]);
		}
		
		return validatingCellProcessors;
	}

	private ImportAction handlePreImportException(final SuperCSVException e) {
		final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
		log("Exception caught while reading release file. Continuing with next row." + reason);
		importContext.getLogger().warn("Exception caught while reading release file. Continuing with next row.", e);
		return ImportAction.CONTINUE;
	}

	private ComponentImportEntry getOrCreateImportEntry(final Map<String, ComponentImportEntry> importEntries, final String csvEffectiveTime) {
		
		String effectiveTimeKey = csvEffectiveTime.isEmpty() ? UNPUBLISHED_KEY : csvEffectiveTime;
		
		// SNAPSHOT import units will be registered with the highest effective time encountered; relocate the existing entry if necessary
		if (ContentSubType.SNAPSHOT.equals(importContext.getContentSubType())) {
			final Entry<String, ComponentImportEntry> entry = Iterables.getOnlyElement(importEntries.entrySet(), null);
			if (entry != null) {
				
				if (csvEffectiveTime.compareTo(entry.getKey()) > 0) {
					importEntries.remove(entry.getKey());
					importEntries.put(csvEffectiveTime, entry.getValue());
				} else {
					effectiveTimeKey = entry.getKey();
				}
			}
		}
		
		ComponentImportEntry importEntry = importEntries.get(effectiveTimeKey);

		if (importEntry == null) {
			
			final String sliceFileName = getSliceFileName(effectiveTimeKey);
			final File sliceFile = new File(componentStagingDirectory, sliceFileName);
			FileOutputStream sliceStream = null;
			
			try {
				sliceStream = new FileOutputStream(sliceFile);
			} catch (final FileNotFoundException e) {
				final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
				log("SNOMED CT import failed. Couldn't open output file '" + sliceFile.getAbsolutePath() + "' for writing." + reason);
				throw new ImportException("Couldn't open output file '" + sliceFile.getAbsolutePath() + "' for writing.", e);
			}
			
			final OutputStreamWriter sliceStreamWriter = new OutputStreamWriter(sliceStream, Charsets.UTF_8);
			final CsvListWriter sliceWriter = new CsvListWriter(sliceStreamWriter, CsvConstants.IHTSDO_CSV_PREFERENCE);
			importEntry = new ComponentImportEntry(sliceFile, sliceWriter);
			
			importEntries.put(effectiveTimeKey, importEntry);
		}
		
		return importEntry;
	}

	private String getSliceFileName(final String effectiveTimeKey) {
		return MessageFormat.format("{0}_{1}.txt", importConfiguration.getType().getDirectoryPartName(), effectiveTimeKey);
	}
	
	private List<ComponentImportUnit> createImportUnits(final Map<String, ComponentImportEntry> importEntries) {
		
		final ImmutableList.Builder<ComponentImportUnit> unitsBuilder = ImmutableList.builder();
		
		for (final Entry<String, ComponentImportEntry> unitEntry : importEntries.entrySet()) {
			unitsBuilder.add(unitEntry.getValue().createUnit(this, unitEntry.getKey(), importConfiguration.getType()));
		}
		
		return unitsBuilder.build();
	}

	@Override
	public void doImport(final SubMonitor subMonitor, final AbstractImportUnit unit) {
		
		final ComponentImportUnit concreteUnit = (ComponentImportUnit) unit;
		final int recordCount = concreteUnit.getRecordCount();
		final String effectiveTimeKey = concreteUnit.getEffectiveTimeKey();
		
		final int workUnits = getImportWorkUnits(recordCount);
		int unitsAdded = 0;
		
		final String message = getImportMessage(effectiveTimeKey);
		subMonitor.beginTask(message, workUnits);
		log(message);
		
		final File sliceFile = concreteUnit.getUnitFile();
		InputStream sliceFileStream = null;
		
		try {
			sliceFileStream = new FileInputStream(sliceFile);
		} catch (final FileNotFoundException e) {
			subMonitor.done();
			final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
			log("SNOMED CT import failed. " + MessageFormat.format("Couldn't find release slice ''{0}''.", sliceFile.getAbsolutePath()) + reason);
			throw new ImportException(MessageFormat.format("Couldn't find release slice ''{0}''.", sliceFile.getAbsolutePath()), e);
		}
		
		final InputStreamReader sliceFileReader = new InputStreamReader(sliceFileStream, Charsets.UTF_8);
		final CsvBeanReader sliceBeanReader = new CsvBeanReader(sliceFileReader, CsvConstants.IHTSDO_CSV_PREFERENCE);
		T currentRow = null;
		final Map<String, CellProcessor> cellProcessorMapping = importConfiguration.getCellProcessorMapping();
		final String[] names = cellProcessorMapping.keySet().toArray(new String[cellProcessorMapping.size()]);
		final CellProcessor[] cellProcessors = cellProcessorMapping.values().toArray(new CellProcessor[cellProcessorMapping.size()]);
		
		try {
			
			while (true) {
				
				try {
					currentRow = sliceBeanReader.read(importConfiguration.getRowClass(), names, cellProcessors);
				} catch (final SuperCSVException e) {
					
					if (ImportAction.CONTINUE.equals(handleImportException(e))) {
						continue;
					} else {
						break;
					}
					
				} catch (final IOException e) {

					if (ImportAction.CONTINUE.equals(handleImportException(e))) {
						continue;
					} else {
						break;
					}
				}
				
				if (currentRow == null) {
					// End of file reached
					break;
				}
						
				try {
					importRow(currentRow);
				} catch (final NullPointerException e) {
					
					if (ImportAction.BREAK.equals(handleImportException(e))) {
						break;
					} else {
						continue;
					}
				}
				
				unitsAdded++;
				subMonitor.worked(1);

				if (!needsCommitting(unitsAdded)) {
					continue;
				}
				
				if (ImportAction.BREAK.equals(commit(subMonitor, effectiveTimeKey))) {
					break;
				}
			}
			
		} finally {
			Closeables.closeQuietly(sliceBeanReader);
		}
		
		commit(subMonitor, effectiveTimeKey);
	}

	protected int getImportWorkUnits(final int recordCount) {
		return recordCount + (recordCount / COMMIT_EVERY_NUM_ELEMENTS + 1) * COMMIT_WORK_UNITS;
	}

	private ImportAction handleImportException(final Throwable e) {
		final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
		log("Exception caught while importing row from release file. Continuing with next row." + reason);
		importContext.getLogger().warn("Exception caught while importing row from release file. Continuing with next row.", e);
		return ImportAction.CONTINUE;
	}

	private String getImportMessage(final String effectiveTimeKey) {
		if (!UNPUBLISHED_KEY.equals(effectiveTimeKey)) {
			return MessageFormat.format("Processing {0}s for effective time {1}", importConfiguration.getType().getDisplayName(), effectiveTimeKey);
		} else {
			return MessageFormat.format("Processing {0}s", importConfiguration.getType().getDisplayName());
		}
	}

	private String getCommitMessage(final String effectiveTimeKey) {
		if (!UNPUBLISHED_KEY.equals(effectiveTimeKey)) {
			return MessageFormat.format("Imported SNOMED CT components with effective {0} from {1} release.", effectiveTimeKey, importContext.getContentSubType().getLowerCaseName());
		} else {
			return MessageFormat.format("Imported unpublished SNOMED CT components from {0} release.", importContext.getContentSubType().getLowerCaseName());
		}
	}

	protected boolean needsCommitting(final int unitsAdded) {
		return (unitsAdded % COMMIT_EVERY_NUM_ELEMENTS) == 0;
	}

	protected ImportAction commit(final SubMonitor subMonitor, final String effectiveTimeKey) {
		if (!importContext.getEditingContext().isDirty()) {
			// Nothing to commit
			subMonitor.worked(COMMIT_WORK_UNITS);
			return ImportAction.CONTINUE;
		}

		try {

			//modify the change set in the transaction, if required.
			preCommit((InternalCDOTransaction) importContext.getEditingContext().getTransaction());
			
			// Wait until the changes end up in local indexes and the semantic cache
			final ICDOTransactionAggregator aggregator = importContext.getAggregator(effectiveTimeKey);
			final String message = getCommitMessage(effectiveTimeKey);
			importContext.setCommitMessage(message);
			
			new CDOServerCommitBuilder(importContext.getUserId(), message, aggregator)
					.sendCommitNotification(importContext.isCommitNotificationEnabled())
					.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
					.commit();
			
			log(message);
			
		} catch (final SnowowlServiceException e) {
			return checkCommitException(e);
		} catch (final CommitException e) {
			return checkCommitException(e);
		} catch (final SnowowlRuntimeException e) {
			return checkCommitException(new SnowowlServiceException(e));
		}

		return ImportAction.CONTINUE;
	}

	/**
	 * Before the {@link CDOEditingContext#commit(String, org.eclipse.core.runtime.IProgressMonitor) commit} operation, clients
	 * may modify the content of the underlying transaction.
	 * <p>By default this method does nothing. Clients may override it.
	 * @param transaction the transaction to modify.
	 * @throws SnowowlServiceException when preparing the commit failed.
	 */
	protected void preCommit(final InternalCDOTransaction transaction) throws SnowowlServiceException {
		return;
	}
	
	private ImportAction checkCommitException(final SnowowlServiceException e) {
		final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
		log("SNOMED CT import failed. Caught exception while import, aborting." + reason);
		importContext.getLogger().warn("Caught exception while import, aborting.", e);
		return ImportAction.BREAK;
	}

	private ImportAction checkCommitException(final CommitException e) {
		final String reason = null != e.getMessage() ? " Reason: '" + e.getMessage() + "'" : "";
		log("SNOMED CT import failed. Caught exception while import, aborting." + reason);
		importContext.getLogger().warn("Caught exception while import, aborting.", e);
		return ImportAction.BREAK;
	}
	
	/**
	 * Modifies repository state based on the incoming CSV row bean.
	 * 
	 * @param currentRow the row to import
	 */
	protected abstract void importRow(T currentRow);

	@Override
	public void postImport(final SubMonitor subMonitor) {

		subMonitor.beginTask(getPostImportMessage(), 2);
		log(getPostImportMessage());
		
		// Tear down in opposite order
		try {
			createIndexes(subMonitor.newChild(1));
			removeComponentStagingDirectory(subMonitor.newChild(1));
		} finally {
			subMonitor.done();
		}
	}

	private String getPostImportMessage() {
		return MessageFormat.format("Finishing {0} import", importConfiguration.getType().getDisplayName());
	}
	
	private void createIndexes(final SubMonitor subMonitor) {
	
		final String message = MessageFormat.format("Creating indexes for {0} import.", importConfiguration.getType().getDisplayName());
		getLogger().info(message);
		log(message);
		subMonitor.setWorkRemaining(importConfiguration.getIndexes().size());
	
		final RepositoryConfiguration config = ApplicationContext.getInstance().getServiceChecked(SnowOwlConfiguration.class).getModuleConfig(RepositoryConfiguration.class);
		try (final Connection connection = ServerDbUtils.createConnection(SnomedPackage.eINSTANCE, config)) {
			
			for (final IndexConfiguration indexConfiguration : importConfiguration.getIndexes()) {
				indexConfiguration.create(getLogger(), connection);
				subMonitor.worked(1);
			}
		
		} catch (final SQLException e) {
			getLogger().error("Couldn't open database connection for creating indexes.", e);
		} finally {
			subMonitor.done();
		}
	}

	private void removeComponentStagingDirectory(final SubMonitor subMonitor) {
		String message = MessageFormat.format("Removing staging directory ''{0}'' from {1} import.", 
				componentStagingDirectory.getAbsolutePath(), importConfiguration.getType().getDisplayName());
		
		getLogger().info(message);
		log(message);
		
		if (!FileUtils.deleteDirectory(componentStagingDirectory)) {
			message = MessageFormat.format(
					"Couldn''t remove {0} staging directory ''{1}''.",
					importConfiguration.getType().getDisplayName(), componentStagingDirectory.getAbsolutePath());
			getLogger().error(message);
			log(message);
		}
	}
}