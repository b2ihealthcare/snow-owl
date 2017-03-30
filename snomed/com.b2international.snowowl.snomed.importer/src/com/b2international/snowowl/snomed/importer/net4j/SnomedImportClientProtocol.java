/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.GZIPStreamWrapper;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportConfiguration.SubsetEntry;

public class SnomedImportClientProtocol extends SignalProtocol<ICDOConnection> {

	public SnomedImportClientProtocol() {
		super(SnomedImportProtocolConstants.PROTOCOL_NAME);
		setStreamWrapper(new GZIPStreamWrapper());
	}
	
	public SnomedImportResult sendRf2ImportRequest(final String userId, final ImportConfiguration importConfiguration, final OMMonitor monitor) {
		return send(new SnomedImportRequest(this, userId, importConfiguration), monitor);
	}
	
	public SnomedUnimportedRefSets sendSubsetImportRequest(final String branchPath, final SubsetEntry entry, final File importFile, final OMMonitor monitor) {
		return send(new SnomedSubsetImportRequest(branchPath, entry, this, importFile), monitor);
	}

	private <T> T send(final RequestWithMonitoring<T> request, final OMMonitor monitor) {
		try {
			return request.send(monitor);
		} catch (final Exception e) {
			throw new ImportException("Failed to send import request.", e);
		}
	}
}