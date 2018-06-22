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
package com.b2international.snowowl.snomed.reasoner.ontology;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.reasoner.exceptions.OntologyException;
import com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils;
import com.b2international.snowowl.snomed.reasoner.request.SnomedOntologyExportType;
import com.google.common.base.Stopwatch;
import com.google.common.io.Closeables;

/**
 * Provides a common entry point for creating {@link OWLOntology OWLOntologies} containing the OWL representation of an entire branch.
 *
 */
public class SnomedOntologyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedOntologyService.class);
	
	private final AtomicLong uniqueSuffix = new AtomicLong();
	
	private final OWLOntologyManager manager;
	
	public SnomedOntologyService() {
		manager = OWLManager.createOWLOntologyManager();
		manager.addOntologyFactory(new DelegateOntologyFactory());
	}
	
	private IRI getOntologyIRI(final IBranchPath branchPath) {
		return SnomedOntologyUtils.BASE_IRI.resolve(branchPath.getPath());
	}
	
	/**
	 * 
	 * @param branchPath
	 * @return
	 */
	public OWLOntology getOntologyIfExists(final IBranchPath branchPath) {
		return manager.getOntology(new OWLOntologyID(getOntologyIRI(branchPath)));
	}
	
	/**
	 * 
	 * @param branchPath
	 * @return
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology createOntology(final IBranchPath branchPath) throws IOException, OWLOntologyCreationException {
		return createOntology(branchPath, false);
	}

	public OWLOntology createOntology(final IBranchPath branchPath, final boolean shared) throws OWLOntologyCreationException {
		final IRI ontologyIRI = getOntologyIRI(branchPath);
		final IRI versionIRI = shared ? null : ontologyIRI.resolve("#" + System.currentTimeMillis() + "_" + uniqueSuffix.getAndIncrement());
		return createOntology(ontologyIRI, versionIRI);
	}

	private OWLOntology createOntology(final IRI ontologyIRI, final IRI versionIRI) throws OWLOntologyCreationException {
		final Stopwatch stopwatch = Stopwatch.createStarted();
		LOGGER.info(MessageFormat.format(">>> Creating ontology ''{0}''", ontologyIRI));
		
		OWLOntology ontology = null;
		final OWLOntologyID ontologyID = new OWLOntologyID(ontologyIRI, versionIRI);
		ontology = manager.getOntology(ontologyID);
		
		if (null != ontology) {
			LOGGER.info(MessageFormat.format("<<< Creating ontology ''{0}'' [cached] [{1}]", ontologyIRI, stopwatch.toString()));
			return ontology;
		}
		
		ontology = manager.createOntology(ontologyID);
		
		LOGGER.info(MessageFormat.format("<<< Creating ontology ''{0}'' [built] [{1}]", ontologyIRI, stopwatch.toString()));
		return ontology;
	}

	/**
	 * 
	 * @param ontology
	 */
	public void removeOntology(final OWLOntology ontology) {
		final OWLOntologyID ontologyID = ontology.getOntologyID();
		LOGGER.info(MessageFormat.format("Unloading ontology ''{0}''.", ontologyID));
		
		if (ontology instanceof DelegateOntology) {
			((DelegateOntology) ontology).dispose();
		} else {
			manager.removeOntology(ontology);
		}
	}
	
	/**
	 * 
	 * @param ontology
	 * @param exportType
	 * @param outputFile
	 * @throws IOException 
	 */
	public void saveOntology(final OWLOntology ontology, final SnomedOntologyExportType exportType, final File outputFile) throws IOException {
		OutputStream outputStream = null;
		
		try {
			final OWLOntologyFormat ontologyFormat = exportType.getFormat();
			if (ontologyFormat.isPrefixOWLOntologyFormat()) {
				ontologyFormat.asPrefixOWLOntologyFormat().copyPrefixesFrom(SnomedOntologyUtils.createPrefixManager(ontology));
			}
		
			outputStream = new FileOutputStream(outputFile);
			outputStream = new BufferedOutputStream(outputStream);
			manager.saveOntology(ontology, ontologyFormat, outputStream);
		} catch (final IOException e) {
			throw new OntologyException(e);
		} catch (final OWLOntologyStorageException e) {
			throw new OntologyException(e);
		} finally {
			Closeables.close(outputStream, true);
		}
	}

	/**
	 * 
	 * @param branchPath
	 * @param changes 
	 */
	public void applyChanges(final OWLOntology ontology, final List<OWLOntologyChange> changes) {
		// Don't apply if the ontology was already removed from the manager
		if (!manager.contains(ontology.getOntologyID())) {
			return;
		}
		
		manager.applyChanges(changes);
	}
}