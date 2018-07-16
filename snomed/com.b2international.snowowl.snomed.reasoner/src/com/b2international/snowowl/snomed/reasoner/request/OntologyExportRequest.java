/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.server.snomed.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.datastore.server.snomed.index.taxonomy.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.exceptions.OntologyException;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntologyFactory;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.0
 */
final class OntologyExportRequest implements Request<BranchContext, String> {

	private static final int PIPE_SIZE = 8192;

	public enum Type {
		FUNCTIONAL,
		MANCHESTER,
		XML
	}

	@NotNull
	private Type exportType;

	@NotEmpty
	private String ontologyModuleId;

	OntologyExportRequest() {}

	public void setExportType(final Type exportType) {
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
		
		final ReasonerTaxonomyBuilder taxonomyBuilder = new ReasonerTaxonomyBuilder();
		taxonomyBuilder.addActiveConceptIds(revisionSearcher);
		taxonomyBuilder.finishConcepts();
		
		taxonomyBuilder.addConceptFlags(revisionSearcher);
		taxonomyBuilder.addActiveStatedEdges(revisionSearcher);
		taxonomyBuilder.addActiveStatedNonIsARelationships(revisionSearcher);
		
		if (concreteDomainSupportEnabled) {
			taxonomyBuilder.addActiveConcreteDomainMembers(revisionSearcher);
		}

		final ReasonerTaxonomy taxonomy = taxonomyBuilder.build();
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		ontologyManager.setOntologyFactories(ImmutableSet.of(new DelegateOntologyFactory(taxonomy)));
		final IRI ontologyIRI = IRI.create(DelegateOntology.NAMESPACE_SCTM + ontologyModuleId);

		try {

			final OWLOntology ontology = ontologyManager.createOntology(ontologyIRI);
			final OWLDocumentFormat documentFormat = getOWLDocumentFormat();
			final AttachmentRegistry fileRegistry = context.service(AttachmentRegistry.class);

			final UUID id = UUID.randomUUID();
			final PipedOutputStream os = new PipedOutputStream();
			final PipedInputStream is = new PipedInputStream(os, PIPE_SIZE);

			final ForkJoinTask<?> uploadTask = ForkJoinTask.adapt(() -> fileRegistry.upload(id, is));
			final ForkJoinTask<?> saveTask = ForkJoinTask.adapt(() -> {
				try {
					ontologyManager.saveOntology(ontology, documentFormat, os);
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
