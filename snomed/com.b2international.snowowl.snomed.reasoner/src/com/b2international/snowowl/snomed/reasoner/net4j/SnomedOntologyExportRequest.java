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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.io.Closeables;

/**
 * Represents a Net4j request for exporting the ontology of the specified branch path.
 * 
 */
public class SnomedOntologyExportRequest extends RequestWithMonitoring<Void> {

	private final IBranchPath branchPath;
	
	private final SnomedOntologyExportType exportType;

	private final File exportPath; 
	
	/**
	 * Creates a new request instance with the specified arguments.
	 * 
	 * @param protocol the client protocol instance to use
	 * @param branchPath the branch path for which the OWL ontology should be exported
	 * @param exportType the selected export type
	 * @param exportPath the target path of the exported ontology (may not be {@code null})
	 */
	public SnomedOntologyExportRequest(final SnomedOntologyClientProtocol protocol, final IBranchPath branchPath, final SnomedOntologyExportType exportType, final File exportPath) {
		super(protocol, SnomedOntologyProtocol.EXPORT_SIGNAL_ID);
		this.branchPath = branchPath;
		this.exportType = exportType;
		this.exportPath = checkNotNull(exportPath, "exportPath");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getMonitorTimeoutSeconds()
	 */
	@Override
	protected int getMonitorTimeoutSeconds() {
		return 100;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getRequestingWorkPercent()
	 */
	@Override
	protected int getRequestingWorkPercent() {
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#getConfirmingWorkPercent()
	 */
	@Override
	protected int getConfirmingWorkPercent() {
		return 99;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#requesting(org.eclipse.net4j.util.io.ExtendedDataOutputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		try {
			out.writeString(branchPath.getPath());
			out.writeEnum(exportType);
		} finally {
			monitor.done();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.RequestWithMonitoring#confirming(org.eclipse.net4j.util.io.ExtendedDataInputStream, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected Void confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		OutputStream outputStream = null;
		
		try {
			final long length = in.readLong();
			outputStream = IOUtil.openOutputStream(exportPath);
			outputStream = new BufferedOutputStream(outputStream);
			IOUtil.copyBinary(in, outputStream, length);
		} finally {
			Closeables.close(outputStream, true);
			monitor.done();
		}
		
		return null;
	}
}