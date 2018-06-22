/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.net.URI;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyID;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.model.SnomedOntologyUtils;

import uk.ac.manchester.cs.owl.owlapi.EmptyInMemOWLOntologyFactory;

/**
 * 
 */
public class DelegateOntologyFactory extends EmptyInMemOWLOntologyFactory implements OWLOntologyFactory {
	
	@Override
	public boolean canCreateFromDocumentIRI(final IRI documentIRI) {
		return documentIRI.getStart().startsWith(SnomedOntologyUtils.BASE_NAMESPACE);
	}
	
	@Override
	public boolean canLoad(final OWLOntologyDocumentSource documentSource) {
		return canCreateFromDocumentIRI(documentSource.getDocumentIRI());
	}
	
	@Override
	public OWLOntology createOWLOntology(final OWLOntologyID ontologyID, final IRI documentIRI, final OWLOntologyCreationHandler handler) throws OWLOntologyCreationException {
		final URI relativeUri = SnomedOntologyUtils.BASE_IRI.toURI().relativize(documentIRI.toURI());
		final String path = relativeUri.getPath();
		final IBranchPath branchPath = BranchPathUtils.createPath(path);
        final DelegateOntology ont = new DelegateOntology(getOWLOntologyManager(), ontologyID, branchPath, getIndex(), isConcreteDomainSupported());
        handler.ontologyCreated(ont);
        return ont;
	}
	
	private boolean isConcreteDomainSupported() {
		return ApplicationContext.getInstance().getService(SnowOwlConfiguration.class).getModuleConfig(SnomedCoreConfiguration.class).isConcreteDomainSupported();
	}

	private RevisionIndex getIndex() {
		return ApplicationContext.getInstance().getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
	}

	@Override
	public OWLOntology loadOWLOntology(final OWLOntologyDocumentSource documentSource, final OWLOntologyCreationHandler handler) throws OWLOntologyCreationException {
		return createOWLOntology(new OWLOntologyID(documentSource.getDocumentIRI()), documentSource.getDocumentIRI(), handler);
	}
}