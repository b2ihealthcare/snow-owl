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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * The client-side Net4J request for SNOMED CT imports.
 */
public class SnomedImportRequest extends RequestWithMonitoring<SnomedImportResult> {

	private static final int EOF = -1;

	private final String userId;
	private final ImportConfiguration importConfiguration;

	public SnomedImportRequest(final SnomedImportClientProtocol protocol, final String userId, final ImportConfiguration importConfiguration) {
		super(protocol, SnomedImportProtocolConstants.SIGNAL_IMPORT_RF2);
		this.userId = userId;
		this.importConfiguration = importConfiguration;
	}

	@Override
	protected int getMonitorTimeoutSeconds() {
		return 100;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {

		monitor.begin(1 + 7 + importConfiguration.getRefSetUrls().size());
		
		try {
			
			//adding requesting branch path to output stream
			out.writeUTF(importConfiguration.getBranchPath());
			out.writeString(userId);
			out.writeEnum(importConfiguration.getVersion());
			out.writeBoolean(importConfiguration.isCreateVersions());
			out.writeInt(importConfiguration.getExcludedRefSetIds().size());
			
			for (final String excludedId : importConfiguration.getExcludedRefSetIds()) {
				out.writeString(excludedId);
			}
			
			out.writeUTF(importConfiguration.getCodeSystemShortName());
			
			monitor.worked(); // 1
			
			writeComponent(out, importConfiguration.getConceptsFile(), monitor.fork());
			writeComponent(out, importConfiguration.getDescriptionsFile(), monitor.fork());
			writeComponent(out, importConfiguration.getTextDefinitionFile(), monitor.fork());
			writeComponent(out, importConfiguration.getRelationshipsFile(), monitor.fork());
			writeComponent(out, importConfiguration.getStatedRelationshipsFile(), monitor.fork());
			writeComponent(out, importConfiguration.getDescriptionType(), monitor.fork());
			writeComponent(out, importConfiguration.getLanguageRefSetFile(), monitor.fork()); // + 7
			
			out.writeInt(importConfiguration.getRefSetUrls().size());
			
			for (final URL refSetUrl : importConfiguration.getRefSetUrls()) {
				writeFile(out, importConfiguration, refSetUrl, monitor.fork()); // + getRefSetUrls().size()
			}
			
		} finally {
			monitor.done();
		}
	}

	private void writeComponent(final ExtendedDataOutputStream out, final File componentFile, final OMMonitor monitor) throws IOException {
		if (!ImportConfiguration.isValidReleaseFile(componentFile)) {
			writeNoFile(out, monitor);
		} else {
			final URL componentUrl = importConfiguration.toURL(componentFile);
			writeFile(out, importConfiguration, componentUrl, monitor);
		}
	}

	private void writeNoFile(final ExtendedDataOutputStream out, final OMMonitor monitor) throws IOException {
		
		monitor.begin();
		
		try {
			out.writeBoolean(false);
		} finally {
			monitor.done();
		}
	}

	private void writeFile(final ExtendedDataOutputStream out, final ImportConfiguration importConfiguration, final URL componentUrl, final OMMonitor monitor) throws IOException {
		
		monitor.begin();
		
		try {
			
			out.writeBoolean(true);
			out.writeString(importConfiguration.getMappedName(componentUrl.getPath()));
			
			final long fileSize = getFileSize(componentUrl);
			
			if (fileSize > Integer.MAX_VALUE) {
				throw new IOException("Can't send files greater that 2GB in size.");
			}
			
			out.writeInt((int) fileSize);
			writeFileContents(out, componentUrl);
			
		} finally {
			monitor.done();
		}
	}

	private long getFileSize(final URL componentUrl) throws IOException {
		try (InputStream componentStream = componentUrl.openStream()) {
			try (InputStream bufferedComponentStream = new BufferedInputStream(componentStream)) {
				long size = 0;
				while (bufferedComponentStream.read() != EOF) {
					++size;
				}
				return size;
			}
		}
	}
	
	private void writeFileContents(final ExtendedDataOutputStream out, final URL componentUrl) throws IOException {
		try (InputStream componentStream = componentUrl.openStream()) {
			IOUtil.copy(componentStream, out, SnomedImportProtocolConstants.BUFFER_SIZE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#confirming(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override 
	protected SnomedImportResult confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		
		final int visitedConceptCount = in.readInt();
		final int validationDefectCount = in.readInt();
		
		monitor.begin(visitedConceptCount + validationDefectCount);
		
		try {

			final SnomedImportResult result = new SnomedImportResult();
			final ClassLoader validationDefectLoader = SnomedValidationDefect.class.getClassLoader();
			
			for (int i = 0; i < visitedConceptCount; i++) {
				result.getVisitedConcepts().add(in.readUTF());
			}
			
			for (int i = 0; i < validationDefectCount; i++) {
				result.getValidationDefects().add((SnomedValidationDefect) in.readObject(validationDefectLoader));
			}
			
			return result;
			
		} finally {
			monitor.done();
		}
	}
}