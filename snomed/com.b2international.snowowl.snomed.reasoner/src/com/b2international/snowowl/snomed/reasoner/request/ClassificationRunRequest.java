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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.DocWriter;
import com.b2international.index.Index;
import com.b2international.index.IndexException;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.remotejobs.RemoteJob;
import com.b2international.snowowl.datastore.server.snomed.index.ReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.reasoner.classification.ReasonerTaxonomyWalker;
import com.b2international.snowowl.snomed.reasoner.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.reasoner.diff.concretedomain.ConcreteDomainChangeOrdering;
import com.b2international.snowowl.snomed.reasoner.diff.relationship.StatementFragmentOrdering;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationRepository;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;
import com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;
import com.b2international.snowowl.snomed.reasoner.normalform.ConceptConcreteDomainNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.normalform.RelationshipNormalFormGenerator;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntology;
import com.b2international.snowowl.snomed.reasoner.ontology.DelegateOntologyFactory;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.7
 */
final class ClassificationRunRequest implements Request<BranchContext, Boolean> {

	private final class RelationshipChangeProcessor extends OntologyChangeProcessor<StatementFragment> {

		private final DocWriter writer;

		public RelationshipChangeProcessor(final DocWriter writer) {
			this.writer = writer;
		}

		@Override
		protected void handleAddedSubject(final String conceptId, final StatementFragment addedSubject) {
			indexRelationshipChange(conceptId, addedSubject, ChangeNature.INFERRED);
		}

		@Override
		protected void handleRemovedSubject(final String conceptId, final StatementFragment removedSubject) {
			indexRelationshipChange(conceptId, removedSubject, ChangeNature.REDUNDANT);
		}

		private void indexRelationshipChange(final String conceptId, final StatementFragment fragment, final ChangeNature nature) {

			final RelationshipChangeDocument.Builder builder = RelationshipChangeDocument.builder()
					.nature(nature)
					.classificationId(classificationId)
					.sourceId(conceptId)
					.group(fragment.getGroup())
					.unionGroup(fragment.getUnionGroup());

			if (fragment.getStatementId() == -1L) {
				builder.typeId(Long.toString(fragment.getTypeId()));
				builder.destinationId(Long.toString(fragment.getDestinationId()));
			} else {
				builder.relationshipId(Long.toString(fragment.getStatementId()));
			}

			indexChange(writer, classificationId, builder.build());
		}
	}

	private final class ConcreteDomainChangeProcessor extends OntologyChangeProcessor<ConcreteDomainFragment> {

		private final DocWriter writer;

		public ConcreteDomainChangeProcessor(final DocWriter writer) {
			this.writer = writer;
		}

		@Override
		protected void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedSubject) {
			indexConcreteDomainChange(conceptId, addedSubject, ChangeNature.INFERRED);
		}

		@Override
		protected void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedSubject) {
			indexConcreteDomainChange(conceptId, removedSubject, ChangeNature.REDUNDANT);
		}

		private void indexConcreteDomainChange(final String conceptId, final ConcreteDomainFragment fragment, final ChangeNature nature) {

			final ConcreteDomainChangeDocument relationshipDoc = ConcreteDomainChangeDocument.builder()
					.nature(nature)
					.classificationId(classificationId)
					.memberId(fragment.getMemberId())
					.referencedComponentId(conceptId)
					.build();

			indexChange(writer, classificationId, relationshipDoc);
		}
	}

	@NotEmpty
	private String reasonerId;

	@NotNull
	private List<SnomedConcept> additionalConcepts;

	private int writeOps;
	private String classificationId;
	private RelationshipNormalFormGenerator relationshipGenerator;
	private ConceptConcreteDomainNormalFormGenerator concreteDomainGenerator;

	ClassificationRunRequest() {}

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

		final Index rawIndex = context.service(Index.class);
		final ClassificationRepository repository = new ClassificationRepository(rawIndex);
		repository.updateStatus(classificationId, ClassificationStatus.RUNNING);

		final RevisionSearcher revisionSearcher = context.service(RevisionSearcher.class);
		final SnomedCoreConfiguration configuration = context.service(SnomedCoreConfiguration.class);
		final boolean concreteDomainSupportEnabled = configuration.isConcreteDomainSupported();

		final ReasonerTaxonomyBuilder taxonomyBuilder = new ReasonerTaxonomyBuilder(revisionSearcher, concreteDomainSupportEnabled);

		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		ontologyManager.setOntologyFactories(ImmutableSet.of(new DelegateOntologyFactory(taxonomyBuilder)));
		final IRI ontologyIRI = IRI.create(DelegateOntology.NAMESPACE_SCTM + Concepts.MODULE_SCT_CORE); // TODO: custom moduleId in ontology IRI?

		try {

			final DelegateOntology ontology = (DelegateOntology) ontologyManager.createOntology(ontologyIRI);

			repository.write(writer -> {
				final ReasonerTaxonomyWalker taxonomyWalker = new ReasonerTaxonomyWalker(reasonerId, ontology, context, 
						(conceptId, parentIds, ancestorIds) -> onConcept(writer, classificationId, conceptId, parentIds, ancestorIds),
						(unsatisfiable, conceptIds) -> onEquivalentSet(writer, classificationId, unsatisfiable, conceptIds));

				relationshipGenerator = new RelationshipNormalFormGenerator(taxonomyBuilder, taxonomyWalker, 
						new RelationshipChangeProcessor(writer), StatementFragmentOrdering.INSTANCE);

				concreteDomainGenerator = new ConceptConcreteDomainNormalFormGenerator(taxonomyBuilder, taxonomyWalker, 
						new ConcreteDomainChangeProcessor(writer), ConcreteDomainChangeOrdering.INSTANCE);

				taxonomyWalker.walk();
				return null;
			});

			repository.updateStatus(classificationId, ClassificationStatus.COMPLETED, new Date());

		} catch (final OWLOntologyCreationException e) {
			repository.updateStatus(classificationId, ClassificationStatus.FAILED, new Date());
			// throw createExportFailedException(context, e);
			// set job status
		}

		return Boolean.TRUE;
	}

	public void onConcept(final DocWriter writer, final String classificationId, 
			final long conceptId, 
			final LongSet parentIds, 
			final LongSet ancestorIds) {

		relationshipGenerator.onConcept(conceptId, parentIds, ancestorIds);
		concreteDomainGenerator.onConcept(conceptId, parentIds, ancestorIds);
	}

	public void onEquivalentSet(final DocWriter writer, final String classificationId, 
			final boolean unsatisfiable, 
			final LongList conceptIds) {

		final EquivalentConceptSetDocument equivalentDoc = EquivalentConceptSetDocument.builder()
				.classificationId(classificationId)
				.conceptIds(conceptIds)
				.unsatisfiable(unsatisfiable)
				.build();

		indexChange(writer, classificationId, equivalentDoc);
	}

	private void indexChange(final DocWriter writer, final String classificationId, final Object doc) {
		writer.put(UUID.randomUUID().toString(), doc);

		writeOps++;
		if (writeOps > 10_000) {
			try {
				writer.commit();
			} catch (final IOException e) {
				throw new IndexException(String.format("Failed to index classification changes for ID '%s'.", classificationId), e);
			}
			writeOps = 0;
		}
	}
}
