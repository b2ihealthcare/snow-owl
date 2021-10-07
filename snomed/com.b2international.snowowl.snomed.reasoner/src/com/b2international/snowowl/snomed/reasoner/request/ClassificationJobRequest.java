/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REASONER_EXCLUDE_MODULE_IDS;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.jobs.RemoteJob;
import com.b2international.snowowl.core.locks.Locks;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomy;
import com.b2international.snowowl.snomed.datastore.index.taxonomy.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationTracker;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyInferrer;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.b2international.snowowl.snomed.reasoner.normalform.NormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntologyFactory;

/**
 * Encapsulates the computation-intensive part of a classification.
 * <p>
 * This request should be run as part of a remote job, not directly.
 * 
 * @since 5.7
 */
final class ClassificationJobRequest implements Request<BranchContext, Boolean>, AccessControl {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger("reasoner");
	
	@NotEmpty
	private String reasonerId;

	@NotNull
	private List<SnomedConcept> additionalConcepts;

	@NotNull
	private String parentLockContext;

	ClassificationJobRequest() {}

	void setReasonerId(final String reasonerId) {
		this.reasonerId = reasonerId;
	}

	void setAdditionalConcepts(final List<SnomedConcept> additionalConcepts) {
		this.additionalConcepts = additionalConcepts;
	}
	
	void setParentLockContext(final String parentLockContext) {
		this.parentLockContext = parentLockContext;
	}

	@Override
	public Boolean execute(final BranchContext context) {
		final RemoteJob job = context.service(RemoteJob.class);
		final String classificationId = job.getKey();

		final Branch branch = context.branch();
		final long headTimestamp = branch.headTimestamp();
		final ClassificationTracker tracker = context.service(ClassificationTracker.class);

		tracker.classificationRunning(classificationId, headTimestamp);

		try {
			executeClassification(context, classificationId, tracker);
		} catch (final ReasonerApiException e) {
			tracker.classificationFailed(classificationId);
			throw e;
		} catch (final Exception e) {
			LOGGER.error("Unexpected error encountered while running classification job.", e);
			tracker.classificationFailed(classificationId);
			throw new ReasonerApiException("Exception caught while running classification.", e);
		}

		return Boolean.TRUE;
	}

	private void executeClassification(final BranchContext context, 
			final String classificationId, 
			final ClassificationTracker tracker) {
		
		final RevisionSearcher revisionSearcher = context.service(RevisionSearcher.class);
		
		TerminologyResource resource = context.service(TerminologyResource.class);
		
		@SuppressWarnings("unchecked")
		final Set<String> reasonerExcludedModuleIds = Collections3.toImmutableSet((Iterable) resource.getSettings().getOrDefault(REASONER_EXCLUDE_MODULE_IDS, Collections.emptySet()));
		final SnomedCoreConfiguration configuration = context.service(SnomedCoreConfiguration.class);
		final boolean concreteDomainSupported = configuration.isConcreteDomainSupported();

		final ReasonerTaxonomy taxonomy;
		try (Locks locks = Locks.on(context).lock(DatastoreLockContextDescriptions.CLASSIFY, parentLockContext)) {
			taxonomy = buildTaxonomy(revisionSearcher, reasonerExcludedModuleIds, concreteDomainSupported);
		} catch (final LockedException e) {
			throw new ReasonerApiException("Couldn't acquire exclusive access to terminology store for classification; %s", e.getMessage(), e);
		}
		
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		ontologyManager.getOntologyFactories().add(new DelegateOntologyFactory(taxonomy));
		final IRI ontologyIRI = IRI.create(DelegateOntology.NAMESPACE_SCTM + Concepts.MODULE_SCT_CORE); // TODO: custom moduleId in ontology IRI?

		try {

			final DelegateOntology ontology = (DelegateOntology) ontologyManager.createOntology(ontologyIRI);
			final ReasonerTaxonomyInferrer inferrer = new ReasonerTaxonomyInferrer(reasonerId, ontology, context);
			final ReasonerTaxonomy inferredTaxonomy = inferrer.addInferences(taxonomy);
			final NormalFormGenerator normalFormGenerator = new NormalFormGenerator(inferredTaxonomy);
			
			tracker.classificationCompleted(classificationId, inferredTaxonomy, normalFormGenerator);

		} catch (final OWLOntologyCreationException e) {
			throw new ReasonerApiException("Exception caught while creating ontology instance.", e);
		} catch (final ReasonerInterruptedException | OWLReasonerRuntimeException e) {
			throw new ReasonerApiException("Exception caught while classifying the ontology.", e);	
		}
	}

	private ReasonerTaxonomy buildTaxonomy(final RevisionSearcher revisionSearcher, final Set<String> excludedModuleIds, final boolean concreteDomainSupported) {
		final ReasonerTaxonomyBuilder taxonomyBuilder = new ReasonerTaxonomyBuilder(excludedModuleIds);
		
		taxonomyBuilder.addActiveConceptIds(revisionSearcher);
		taxonomyBuilder.addActiveConceptIds(additionalConcepts.stream());
		taxonomyBuilder.finishConcepts();
		
		taxonomyBuilder.addConceptFlags(revisionSearcher);
		taxonomyBuilder.addActiveStatedEdges(revisionSearcher);
		taxonomyBuilder.addActiveStatedRelationships(revisionSearcher);
		taxonomyBuilder.addActiveInferredRelationships(revisionSearcher);
		taxonomyBuilder.addActiveAdditionalGroupedRelationships(revisionSearcher);
		
		taxonomyBuilder.addNeverGroupedTypeIds(revisionSearcher);
		taxonomyBuilder.addActiveAxioms(revisionSearcher);

		if (concreteDomainSupported) {
			taxonomyBuilder.addActiveConcreteDomainMembers(revisionSearcher);
		}

		// Add the extra definitions
		taxonomyBuilder.addConceptFlags(additionalConcepts.stream());

		final Supplier<Stream<SnomedRelationship>> relationshipSupplier = () -> additionalConcepts.stream()
				.flatMap(c -> c.getRelationships().stream());
		
		taxonomyBuilder.addActiveStatedEdges(relationshipSupplier.get());
		taxonomyBuilder.addActiveStatedRelationships(relationshipSupplier.get());
		taxonomyBuilder.addActiveInferredRelationships(relationshipSupplier.get());
		taxonomyBuilder.addActiveAdditionalGroupedRelationships(relationshipSupplier.get());

		if (concreteDomainSupported) {
			final Stream<SnomedReferenceSetMember> conceptMembers = additionalConcepts.stream()
				.flatMap(c -> c.getMembers().stream());
			
			taxonomyBuilder.addActiveConcreteDomainMembers(conceptMembers);
		}
		
		return taxonomyBuilder.build();
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_CLASSIFY;
	}
	
}
