/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;

/**
 * Abstract exporter to export terminology.
 * 
 * @since Snow&nbsp;Owl 3.0
 */
public abstract class AbstractTerminologyExporter implements ITerminologyExporter {
	
	private final static String TEMP_DIR = System.getProperty("java.io.tmpdir"); //$NON-NLS-N$
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractTerminologyExporter.class);
	
	private final String userId;
	private final String branchPath;
	
	public AbstractTerminologyExporter(final String userId, final String branchPath) {
		this.userId = userId;
		this.branchPath = branchPath;
	}
	
	/**
	 * Gets the terminology specific name.
	 * 
	 * @return
	 */
	protected abstract String getTerminologyName();
	
	/**
	 * Initializes resources for the export.
	 * 
	 * @param exportFilePath the path of the exported file.
	 * @param monitor 
	 * @return
	 * @throws IOException
	 * @throws SnowowlServiceException
	 */
	protected abstract File exportTerminology(final String exportFilePath, final OMMonitor monitor) throws IOException;

	/**
	 * Exports the terminology components.
	 */
	protected abstract void exportTerminologyComponents(final OMMonitor monitor);
	
	/**
	 * Gets the extension of the exported file.
	 * 
	 * @return
	 */
	protected abstract String getFileExtension();
	
	@Override
	public File doExport(final OMMonitor monitor) throws IOException {
		String fileSeparator = "";
		
		if (!TEMP_DIR.endsWith(String.valueOf(File.separatorChar))){
			fileSeparator = String.valueOf(File.separatorChar);
		}
		
		final String exportFilePath = MessageFormat.format("{0}{1}{2}_{3}.{4}", TEMP_DIR, fileSeparator, getTerminologyName().replace(" ", ""),
				 Dates.formatByHostTimeZone(new Date(), "yyyyMMdd_HHmmss"), getFileExtension());

		return exportTerminology(exportFilePath, monitor);
	}
	
	@Override
	public File doExport(final String exportFilePath) throws IOException {
		return exportTerminology(exportFilePath, createMonitor());
	}

	private OMMonitor createMonitor() {
		return new Monitor().begin();
	}
	
	protected String getExportedEffectiveTime(final Date effectiveTime) {
		return EffectiveTimes.format(effectiveTime);
	}
	
	protected final void logExportActivity(final String message) {
		LogUtils.logExportActivity(LOGGER, userId, getBranchPath(), message);
	}

	protected final String getBranchPath() {
		return branchPath;
	}

}