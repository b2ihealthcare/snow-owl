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
package com.b2international.snowowl.snomed.reasoner.server.net4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.semanticweb.owlapi.model.OWLOntology;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.reasoner.net4j.SnomedOntologyExportType;
import com.b2international.snowowl.snomed.reasoner.net4j.SnomedOntologyProtocol;
import com.b2international.snowowl.snomed.reasoner.server.ontology.SnomedOntologyService;
import com.google.common.io.Closeables;

/**
 * Represents an indication for an inbound SNOMED CT ontology export request. 
 * 
 */
public class SnomedOntologyExportIndication extends IndicationWithMonitoring {

	private IBranchPath branchPath;
	
	private SnomedOntologyExportType exportType;

	/**
	 * Creates a new ontology export indication with the specified arguments.
	 * 
	 * @param protocol the server protocol instance to use (may not be {@code null})
	 */
	public SnomedOntologyExportIndication(final SnomedOntologyServerProtocol protocol) {
		super(protocol, SnomedOntologyProtocol.EXPORT_SIGNAL_ID);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#getIndicatingWorkPercent()
	 */
	@Override
	protected int getIndicatingWorkPercent() {
		return 5;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#indicating(org.eclipse.net4j.util.io.ExtendedDataInputStream, 
	 * org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void indicating(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		try {
			branchPath = BranchPathUtils.createPath(in.readString());
			exportType = in.readEnum(SnomedOntologyExportType.class);
		} finally {
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.signal.IndicationWithMonitoring#responding(org.eclipse.net4j.util.io.ExtendedDataOutputStream, 
	 * org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	@Override
	protected void responding(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		final SnomedOntologyService ontologyService = ApplicationContext.getInstance().getService(SnomedOntologyService.class);
		OWLOntology ontology = null;
		Async async = null;
		
		monitor.begin();
		
		try {
			async = monitor.forkAsync();
		
			final File ontologyTempFile = TMPUtil.createTempFile();
			ontology = ontologyService.createOntology(branchPath, true);
			ontologyService.saveOntology(ontology, exportType, ontologyTempFile);
			
			final long length = ontologyTempFile.length();
			out.writeLong(length);
			
			InputStream inputStream = null;
			
			try {
				inputStream = IOUtil.openInputStream(ontologyTempFile);
				inputStream = new BufferedInputStream(inputStream);
				IOUtil.copyBinary(inputStream, out, length);
			} finally {
				Closeables.closeQuietly(inputStream);
			}
			
		} finally {
			if (null != async) {
				async.stop();
			}
			
			if (null != ontology) {
				ontologyService.removeOntology(ontology);
			}
			
			monitor.done();
		}
	}
}