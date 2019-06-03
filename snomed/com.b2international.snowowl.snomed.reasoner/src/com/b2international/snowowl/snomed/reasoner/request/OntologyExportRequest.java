/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;
import java.util.concurrent.ForkJoinTask;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SetOntologyID;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.reasoner.exceptions.OntologyException;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntologyFactory;

/**
 * @since 7.0
 */
final class OntologyExportRequest implements Request<BranchContext, String> {

	private static final int PIPE_SIZE = 8192;

	@NotNull
	private OntologyExportType exportType;

	@NotEmpty
	private String ontologyModuleId;

	OntologyExportRequest() {}

	public void setExportType(final OntologyExportType exportType) {
		this.exportType = exportType;
	}

	public void setOntologyModuleId(final String ontologyModuleId) {
		this.ontologyModuleId = ontologyModuleId;
	}

	@Override
	public String execute(final BranchContext context) {

		final SnomedCoreConfiguration config = context.service(SnomedCoreConfiguration.class);
		final RevisionSearcher revisionSearcher = context.service(RevisionSearcher.class);
		final boolean concreteDomainSupportEnabled = config.isConcreteDomainSupported();
		
		final ReasonerTaxonomyBuilder taxonomyBuilder = new ReasonerTaxonomyBuilder(Concepts.UK_MODULES_NOCLASSIFY);
		
		taxonomyBuilder.addActiveConceptIds(revisionSearcher);
		taxonomyBuilder.finishConcepts();
		
		taxonomyBuilder.addConceptFlags(revisionSearcher);
		taxonomyBuilder.addActiveStatedEdges(revisionSearcher);
		taxonomyBuilder.addActiveStatedNonIsARelationships(revisionSearcher);
		
		taxonomyBuilder.addNeverGroupedTypeIds(revisionSearcher);
		taxonomyBuilder.addActiveAxioms(revisionSearcher);

		if (concreteDomainSupportEnabled) {
			taxonomyBuilder.addActiveConcreteDomainMembers(revisionSearcher);
		}

		final ReasonerTaxonomy taxonomy = taxonomyBuilder.build();
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		ontologyManager.getOntologyFactories().add(new DelegateOntologyFactory(taxonomy));
		final IRI ontologyIRI = IRI.create(DelegateOntology.NAMESPACE_SCTM + ontologyModuleId);

		try {

			final OWLOntology ontology = ontologyManager.createOntology(ontologyIRI);
			OWLOntology ontologyToExport = ontologyManager.createOntology();
			
			ontology.getLogicalAxioms().forEach(axiom -> {
				ontologyManager.addAxiom(ontologyToExport, axiom);
			});
			
			final OWLDocumentFormat documentFormat = getOWLDocumentFormat();
			final FileRegistry fileRegistry = context.service(FileRegistry.class);

			final UUID id = UUID.randomUUID();
			final PipedOutputStream os = new PipedOutputStream();
			final PipedInputStream is = new PipedInputStream(os, PIPE_SIZE);

			final ForkJoinTask<?> uploadTask = ForkJoinTask.adapt(() -> fileRegistry.upload(id, is));
			final ForkJoinTask<?> saveTask = ForkJoinTask.adapt(() -> {
				try {
					ontologyManager.saveOntology(ontologyToExport, documentFormat, os);
				} catch (final OWLOntologyStorageException e) {
					throw createExportFailedException(context, e);
				} finally {
					try {
						os.close();
					} catch (final IOException e) {
						throw createExportFailedException(context, e);
					}
				}
			});

			ForkJoinTask.invokeAll(saveTask, uploadTask);
			return id.toString();

		} catch (final OWLOntologyCreationException e) {
			throw createExportFailedException(context, e);
		} catch (final IOException e) {
			throw createExportFailedException(context, e);
		} finally {
			// invalidate static cache entries :'(
			ontologyManager.getOntologies().forEach(o -> {
				ontologyManager.applyChange(new SetOntologyID(o, new OWLOntologyID()));
			});
		}
	}

	private OntologyException createExportFailedException(final BranchContext context, final Throwable e) {
		return new OntologyException("Couldn't save ontology with module ID '" + ontologyModuleId + "' on branch '" + context.branchPath() + "'.", e);
	}

	private OWLDocumentFormat getOWLDocumentFormat() {
		switch (exportType) {
			case FUNCTIONAL: return new FunctionalSyntaxDocumentFormat();
			case MANCHESTER: return new ManchesterSyntaxDocumentFormat();
			case XML: return new RDFXMLDocumentFormat();
			default: throw new IllegalStateException("Unexpected export type '" + exportType + "'.");
		}
	}
}
