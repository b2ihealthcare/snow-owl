/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.importer;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Iterables.getFirst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.b2international.snowowl.datastore.cdo.CDOTransactionAggregator;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDOTransactionAggregator;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect;
import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect.Defect;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Abstract import job class for terminology excel import jobs.
 * 
 * @since Snow&nbsp;Owl 3.0
 */
public abstract class AbstractTerminologyExcelImportJob<T extends CDOObject> extends AbstractTerminologyImportJob {

	public static final String INDEX_SHEET = "index";
	protected static final boolean DEFAULT_ACTIVE_STATUS = true;

	private long latestSuccessfulCommitTime;

	private final Map<String, T> existingComponents = Maps.newHashMap();
	private final TerminologyImportExcelParser excelParser = new TerminologyImportExcelParser();

	public AbstractTerminologyExcelImportJob(final String jobName, final String excelFilePath, final String userId, final IBranchPath branchPath, final TerminologyImportType importType) {
		super(jobName, excelFilePath, userId, branchPath, importType);
	}

	/**
	 * Gets the current, imported terminology name.
	 * 
	 * @return the name of the terminology.
	 */
	protected abstract String getTerminologyName();

	/**
	 * Gets the editing context of the imported terminology.
	 * 
	 * @return
	 */
	protected abstract CDOEditingContext getEditingContext();

	/**
	 * Creates the terminology specific validator.
	 * 
	 * @return the created validator
	 */
	protected abstract AbstractTerminologyImportValidator<T> createValidator();

	/**
	 * Gets the number of that row which is after the terminology property rows.
	 * 
	 * @return
	 */
	protected abstract int getPropertyIndex();

	/**
	 * Initializes services for the import.
	 * 
	 * @throws SnowowlServiceException
	 */
	protected abstract void initializeServices() throws SnowowlServiceException;

	/**
	 * Import terminology from the given sheet.
	 * 
	 * @param sheet
	 *            the sheet which contains the terminology values.
	 * @return
	 * @throws Exception
	 */
	protected abstract String importTerminology(final String sheetName) throws Exception;

	/**
	 * Gets the terminology specific component from the database.
	 * 
	 * @param sheetName
	 *            the name of the sheet which contains information for the query.
	 * @return the found component or <code>null</code>.
	 */
	protected abstract T getComponentFromDatabase(final String sheetName);

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		logImportActivity(MessageFormat.format("Importing {0}s from excel file: {1}", getTerminologyName(), getImportFilePath()));

		FileInputStream inputStream = null;
		final String fileName = getImportFilePath().substring(getImportFilePath().lastIndexOf(File.separator) + 1);

		try {
			inputStream = new FileInputStream(getImportFilePath());
			final Workbook workbook = WorkbookFactory.create(inputStream);

			initializeServices();

			final Set<Sheet> sheets = collectSheets(workbook);

			excelParser.parse(sheets, getPropertyIndex());
			getComponentsFromDatabase();
			validate();

			if (getTerminologyImportResult().hasValidationDefects()) {
				logImportActivity("Validation encountered one or more errors:");

				for (final TerminologyImportValidationDefect validationDefect : getTerminologyImportResult().getValidationDefects()) {
					logImportActivity(MessageFormat.format("Sheet name: {0}", validationDefect.getSheetName()));

					for (final Defect defect : validationDefect.getDefects()) {
						logImportActivity(MessageFormat.format("Error: {0}", defect.getErrorMessage()));
					}
				}

				return Status.CANCEL_STATUS;
			}

			if (isImportTypeClear()) {
				clearDatabase();
			}

			final long lastCommitTime = getLastCommitTimeBeforeImport();
			latestSuccessfulCommitTime = lastCommitTime;
			
			commitSheets(fileName, sheets);

			final CDOCommitInfo commitInfo = 
					CDOCommitInfoUtils.createEmptyCommitInfo(getRepositoryUuid(), getBranchPath(), getUserId(), String.format("Imported %ss from Excel file %s.", getTerminologyName(), fileName), getLatestSuccessfulCommitTime(), lastCommitTime);
			CDOServerUtils.sendCommitNotification(commitInfo);

			return Status.OK_STATUS;

		} catch (IOException e) {
			final String message = MessageFormat.format("Problem while reading file {0}", getImportFilePath());
			LOGGER.error(message, e);

			return new Status(IStatus.ERROR, "unknown", 1, message, e);
		} catch (InvalidFormatException e) {
			final String message = MessageFormat.format("Excel file is not valid: {0}", getImportFilePath());
			LOGGER.error(message, e);

			return new Status(IStatus.ERROR, "unknown", 1, message, e);
		} catch (SnowowlServiceException e) {
			final String message = MessageFormat.format("Error while committing {0}s", getTerminologyName());
			LOGGER.error(message, e);

			return new Status(IStatus.ERROR, "unknown", 1, message, e);
		} catch (Exception e) {
			final String message = MessageFormat.format("Error while importing {0}s", getTerminologyName());
			LOGGER.error(message, e);

			return new Status(IStatus.ERROR, "unknown", 1, message, e);
		} finally {
			if (null != getEditingContext()) {
				getEditingContext().close();
			}

			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}

			monitor.done();
		}
	}
	
	protected void commitSheets(final String fileName, final Set<Sheet> sheets) throws Exception, CommitException {
		for (final Sheet sheet : sheets) {
			final String terminologyName = importTerminology(sheet.getSheetName());
			logImportActivity(MessageFormat.format("Processed excel sheet {0} for {1}", sheet.getSheetName(), getTerminologyName()));
			final CDOCommitInfo commitInfo = commitChanges(terminologyName, fileName);
			if (null != commitInfo) {
				latestSuccessfulCommitTime = commitInfo.getTimeStamp();
			}
		}
	}

	/**
	 * Clears the database for the current imported terminology.
	 * 
	 * @throws SnowowlServiceException
	 */
	protected void clearDatabase() throws CommitException {
		if (!getEditingContext().isContentsEmpty()) {
			LOGGER.info(MessageFormat.format("Deleting existing {0}s from database", getTerminologyName()));

			getEditingContext().clearContents();
			CDOServerUtils.commit(getEditingContext().getTransaction(), getUserId(), MessageFormat.format("Removed existing {0}s from database", getTerminologyName()), null);
		}
	}

	/**
	 * Commit the changes for the imported terminology.
	 * 
	 * @param terminologyName
	 *            the name of the imported terminology.
	 * @param fileName
	 *            the name of the import file.
	 * @throws SnowowlServiceException
	 */
	@Nullable protected CDOCommitInfo commitChanges(final String terminologyName, final String fileName) throws CommitException {
		if (getEditingContext().getTransaction().isDirty()) {
			final String comment = MessageFormat.format("Imported {0} {1} from excel file {2}", getTerminologyName(), terminologyName, fileName);
			final CDOTransaction transaction = getEditingContext().getTransaction();
			final ICDOTransactionAggregator aggregator = CDOTransactionAggregator.create(transaction);
			final Iterable<CDOCommitInfo> commit = CDOServerUtils.commit(aggregator, getUserId(), comment, true, false, null);
			return getFirst(commit, null);
		}
		return null;
	}

	protected long getLatestSuccessfulCommitTime() {
		return latestSuccessfulCommitTime;
	}
	
	protected void setLatestSuccessfulCommitTime(long timeStamp) {
		this.latestSuccessfulCommitTime = timeStamp;
	}

	protected boolean getStatusBooleanValue(final String value, boolean defaultValue) {
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}

		return value.equalsIgnoreCase("active");
	}

	public TerminologyImportExcelParser getExcelParser() {
		return excelParser;
	}

	public Map<String, T> getExistingComponents() {
		return existingComponents;
	}

	private long getLastCommitTimeBeforeImport() {
		return CDOServerUtils.getLastCommitTime(getEditingContext().getTransaction().getBranch());
	}

	public static Set<Sheet> collectSheets(final Workbook workbook) {
		final Set<Sheet> sheets = Sets.newHashSet();
		final int numberOfSheets = workbook.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++) {
			final Sheet sheet = workbook.getSheetAt(i);

			if (!INDEX_SHEET.equalsIgnoreCase(sheet.getSheetName())) {
				sheets.add(sheet);
			}

		}

		return sheets;
	}

	private void validate() {

		final AbstractTerminologyImportValidator<T> validator = createValidator();
		final Collection<TerminologyImportValidationDefect> defects = validator.validate();
		getTerminologyImportResult().getValidationDefects().addAll(defects);

	}

	private void getComponentsFromDatabase() {

		for (final String sheetName : excelParser.getProperties().keySet()) {

			final T component = getComponentFromDatabase(sheetName);

			if (null != component) {

				existingComponents.put(sheetName, component);

			}

		}

	}

	private String getRepositoryUuid() {
		return getServiceForClass(ICDOConnectionManager.class).get(getEditingContext().getTransaction()).getUuid();
	}

}