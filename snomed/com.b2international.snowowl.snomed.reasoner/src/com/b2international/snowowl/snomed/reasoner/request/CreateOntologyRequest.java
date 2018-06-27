package com.b2international.snowowl.snomed.reasoner.request;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.reasoner.ontology.SnomedOntologyService;
import com.google.common.io.Closeables;

public class CreateOntologyRequest {

	private IBranchPath branchPath;
	private SnomedOntologyExportType exportType;

	public void run(OMMonitor monitor, DataOutputStream out) throws IOException, OWLOntologyCreationException {
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
