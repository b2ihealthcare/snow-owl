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
package com.b2international.snowowl.snomed.reasoner.net4j;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;

/**
 * The SNOMED CT ontology client protocol, using a {@link CDOConnection} instance as its infrastructure.
 * 
 */
public class SnomedOntologyClientProtocol extends SignalProtocol<ICDOConnection> {

	/**
	 * Creates a new ontology client protocol instance.
	 */
	public SnomedOntologyClientProtocol() {
		super(SnomedOntologyProtocol.PROTOCOL_NAME);
	}

	
	/**
	 * Creates an export request instance and sends it using this protocol instance. 
	 * 
	 * @param monitor the progress monitor reporting export progress
	 * @param branchPath the branch path for which the ontology should be exported
	 * @param exportType the selected ontology format for exporting
	 * @param exportPath the selected destination path of the exported ontology (may not be {@code null})
	 */
	public void export(final IProgressMonitor monitor, final IBranchPath branchPath, final SnomedOntologyExportType exportType, final File exportPath) {
		send(new SnomedOntologyExportRequest(this, branchPath, exportType, exportPath), new EclipseMonitor(monitor));
	}
	
	private void send(final RequestWithMonitoring<?> request, final OMMonitor monitor) {
		try {
			request.send(monitor);
		} catch (final Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}
}