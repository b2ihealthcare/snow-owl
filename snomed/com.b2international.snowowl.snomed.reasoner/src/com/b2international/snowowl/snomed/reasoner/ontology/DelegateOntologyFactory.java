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
package com.b2international.snowowl.snomed.reasoner.ontology;

import org.semanticweb.owlapi.annotations.HasPriority;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;

/**
 * @since
 */
@HasPriority(0.5)
public final class DelegateOntologyFactory implements OWLOntologyFactory {

	private final ReasonerTaxonomy taxonomy;

	public DelegateOntologyFactory(final ReasonerTaxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	@Override
	public boolean canCreateFromDocumentIRI(final IRI documentIRI) {
		return documentIRI.getNamespace().startsWith(DelegateOntology.NAMESPACE_SCTM);
	}

	@Override
	public boolean canLoad(final OWLOntologyDocumentSource documentSource) {
		return false;
	}

	@Override
	public OWLOntology createOWLOntology(final OWLOntologyManager manager,
			final OWLOntologyID ontologyID, 
			final IRI ontologyIRI, 
			final OWLOntologyCreationHandler handler)
			throws OWLOntologyCreationException {
		
		final DelegateOntology owlOntology = new DelegateOntology(manager, ontologyID, taxonomy);
		handler.ontologyCreated(owlOntology);
		handler.setOntologyFormat(owlOntology, new RDFXMLDocumentFormat());
		return owlOntology;
	}

	@Override
	public OWLOntology loadOWLOntology(final OWLOntologyManager manager, 
			final OWLOntologyDocumentSource documentSource, 
			final OWLOntologyCreationHandler handler,
			final OWLOntologyLoaderConfiguration configuration) throws OWLOntologyCreationException {
		
		throw new OWLOntologyCreationException("This ontology factory does not support loading OWL ontologies.");
	}
}
