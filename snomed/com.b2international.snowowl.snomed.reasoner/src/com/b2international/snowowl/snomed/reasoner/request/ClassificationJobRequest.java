/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.core.taxonomy.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyInferrer;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTracker;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntologyFactory;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.7
 */
final class ClassificationJobRequest implements Request<BranchContext, Boolean> {

	@NotEmpty
	private String reasonerId;

	@NotNull
	private List<SnomedConcept> additionalConcepts;

	private String classificationId;

	ClassificationJobRequest() {}

	void setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
	}

	void setAdditionalConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts = additionalConcepts;
	}

	@Override
	public Boolean execute(final BranchContext context) {
		final RemoteJob job = context.service(RemoteJob.class);
		classificationId = job.getId();

		final Branch branch = context.branch();
		final long headTimestamp = branch.headTimestamp();
		final ClassificationTracker tracker = context.service(ClassificationTracker.class);

		tracker.classificationRunning(classificationId, headTimestamp);

		final RevisionSearcher revisionSearcher = context.service(RevisionSearcher.class);
		final SnomedCoreConfiguration configuration = context.service(SnomedCoreConfiguration.class);
		final boolean concreteDomainSupportEnabled = configuration.isConcreteDomainSupported();

		final ReasonerTaxonomyBuilder taxonomyBuilder = new ReasonerTaxonomyBuilder();
		taxonomyBuilder.addActiveConceptIds(revisionSearcher);
		taxonomyBuilder.addActiveConceptIds(additionalConcepts.stream()
				.map(SnomedConcept::getId));
		taxonomyBuilder.finishConcepts();
		
		taxonomyBuilder.addConceptFlags(revisionSearcher);
		taxonomyBuilder.addActiveStatedEdges(revisionSearcher);
		taxonomyBuilder.addActiveStatedNonIsARelationships(revisionSearcher);
		taxonomyBuilder.addActiveInferredRelationships(revisionSearcher);

		if (concreteDomainSupportEnabled) {
			taxonomyBuilder.addActiveConcreteDomainMembers(revisionSearcher);
		}

		// Add the extra definitions
		taxonomyBuilder.addConceptFlags(additionalConcepts.stream()
				.filter(c -> DefinitionStatus.FULLY_DEFINED.equals(c.getDefinitionStatus()))
				.map(SnomedConcept::getId));

		final Supplier<Stream<SnomedRelationship>> relationshipSupplier = () -> additionalConcepts.stream()
				.flatMap(c -> c.getRelationships().stream());
		
		taxonomyBuilder.addActiveStatedEdges(relationshipSupplier.get());
		taxonomyBuilder.addActiveStatedNonIsARelationships(relationshipSupplier.get());
		taxonomyBuilder.addActiveInferredRelationships(relationshipSupplier.get());

		if (concreteDomainSupportEnabled) {
			final Stream<SnomedReferenceSetMember> conceptMembers = additionalConcepts.stream()
				.flatMap(c -> c.getMembers().stream());
			
			final Stream<SnomedReferenceSetMember> relationshipMembers = additionalConcepts.stream()
				.flatMap(c -> c.getRelationships().stream())
				.flatMap(c -> c.getMembers().stream());
			
			taxonomyBuilder.addActiveConcreteDomainMembers(Stream.concat(conceptMembers, relationshipMembers));
		}
		
		final ReasonerTaxonomy taxonomy = taxonomyBuilder.build();
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		ontologyManager.setOntologyFactories(ImmutableSet.of(new DelegateOntologyFactory(taxonomy)));
		final IRI ontologyIRI = IRI.create(DelegateOntology.NAMESPACE_SCTM + Concepts.MODULE_SCT_CORE); // TODO: custom moduleId in ontology IRI?

		try {

			final DelegateOntology ontology = (DelegateOntology) ontologyManager.createOntology(ontologyIRI);
			final ReasonerTaxonomyInferrer inferrer = new ReasonerTaxonomyInferrer(reasonerId, ontology, context);
			final ReasonerTaxonomy inferredTaxonomy = inferrer.addInferences(taxonomy);

			tracker.classificationCompleted(classificationId, inferredTaxonomy);

		} catch (final OWLOntologyCreationException e) {
			tracker.classificationFailed(classificationId);
			throw new ReasonerApiException("Caught exception while creating ontology instance.", e);
		} catch (final ReasonerApiException e) {
			tracker.classificationFailed(classificationId);
			throw e;
		}

		return Boolean.TRUE;
	}
}
