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
package com.b2international.snowowl.datastore.server.importer;

import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.importer.TerminologyImportResult;
import com.b2international.snowowl.datastore.importer.TerminologyImportType;

/**
 * Abstract import job class for terminology import jobs.
 * 
 * @since Snow&nbsp;Owl 3.0
 */
public abstract class AbstractTerminologyImportJob extends Job {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTerminologyImportJob.class);
	
	private final String userId;
	private final IBranchPath branchPath;
	private final String importFilePath;
	private final TerminologyImportType importType;
	private final TerminologyImportResult terminologyImportResult;

	public AbstractTerminologyImportJob(final String jobName, final String excelFilePath, final String userId, final IBranchPath branchPath, final TerminologyImportType importType) {
		super(jobName);
		this.importFilePath = excelFilePath;
		this.userId = userId;
		this.branchPath = branchPath;
		this.importType = importType;
		this.terminologyImportResult = new TerminologyImportResult();
	}
	
	/**
	 * Logs the import activity with the given message.
	 * 
	 * @param message the message to be logged.
	 */
	protected void logImportActivity(final String message) {
		LogUtils.logImportActivity(LOGGER, userId, branchPath.getPath(), message);
	}
	
	/**
	 * Logs the import warning with the given message.
	 * 
	 * @param message the message to be logged.
	 */
	protected void logImportWarning(final String message) {
		LogUtils.logImportWarning(LOGGER, userId, branchPath.getPath(), message);
	}
	
	protected boolean isImportTypeClear() {
		return TerminologyImportType.CLEAR == importType;
	}
	
	protected boolean isImportTypeMerge() {
		return TerminologyImportType.MERGE == importType;
	}
	
	protected boolean isImportTypeReplace() {
		return TerminologyImportType.REPLACE == importType;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
	public String getImportFilePath() {
		return importFilePath;
	}

	public TerminologyImportResult getTerminologyImportResult() {
		return terminologyImportResult;
	}

	public TerminologyImportType getImportType() {
		return importType;
	}

}