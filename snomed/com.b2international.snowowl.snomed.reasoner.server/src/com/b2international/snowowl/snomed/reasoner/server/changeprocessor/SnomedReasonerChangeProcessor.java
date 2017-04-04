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
package com.b2international.snowowl.snomed.reasoner.server.changeprocessor;

import static com.b2international.snowowl.snomed.reasoner.server.SnomedReasonerServerActivator.CONSTRAINED_HEAP;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.DelegatingIndexCommitChangeSet;
import com.b2international.snowowl.datastore.index.ImmutableIndexCommitChangeSet;
import com.b2international.snowowl.datastore.index.IndexCommitChangeSet;
import com.b2international.snowowl.datastore.server.snomed.index.AbstractReasonerTaxonomyBuilder.Type;
import com.b2international.snowowl.datastore.server.snomed.index.DeltaReasonerTaxonomyBuilder;
import com.b2international.snowowl.datastore.server.snomed.index.InitialReasonerTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.b2international.snowowl.snomed.reasoner.model.ConcreteDomainDefinition;
import com.b2international.snowowl.snomed.reasoner.model.LongConcepts;
import com.b2international.snowowl.snomed.reasoner.model.RelationshipDefinition;
import com.b2international.snowowl.snomed.reasoner.server.ontology.SnomedOntologyService;
import com.google.common.base.Stopwatch;
import com.google.common.primitives.Longs;

/**
 * Change processor implementation for the OWL representation of the SNOMED&nbsp;CT ontology.
 *
 *
 * @see ICDOChangeProcessor
 * @see ICDOCommitChangeSet
 */
public class SnomedReasonerChangeProcessor implements ICDOChangeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedReasonerChangeProcessor.class);

	private final IBranchPath branchPath;
	private final RevisionIndex index;

	private final List<OWLOntologyChange> changes = newArrayList();

	private OWLOntology ontology;

	public SnomedReasonerChangeProcessor(final IBranchPath branchPath, RevisionIndex index) {
		this.branchPath = branchPath;
		this.index = index;
	}

	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {

		final SnomedOntologyService ontologyService = ApplicationContext.getInstance().getService(SnomedOntologyService.class);
		ontology = ontologyService.getOntologyIfExists(branchPath);

		if (!CONSTRAINED_HEAP && null != ontology) {
			index.read(branchPath.getPath(), new RevisionIndexRead<Void>() {
				@Override
				public Void execute(RevisionSearcher index) throws IOException {
					LOGGER.info(">>> Processing OWL ontology changes");
					final Stopwatch changeProcessorStopwatch = Stopwatch.createStarted();
					
					final InitialReasonerTaxonomyBuilder oldReasonerTaxonomy = new InitialReasonerTaxonomyBuilder(index, Type.REASONER);
					final DeltaReasonerTaxonomyBuilder newReasonerTaxonomy = new DeltaReasonerTaxonomyBuilder(oldReasonerTaxonomy, Type.REASONER, commitChangeSet);
					
					for (final LongIterator itr = newReasonerTaxonomy.getConceptIdsToRemove().iterator(); itr.hasNext(); /* empty */) {
						final long conceptId = itr.next();
						final ConceptDefinition definitionToRemove = createConceptDefinitionToRemove(oldReasonerTaxonomy, newReasonerTaxonomy, conceptId);
						changes.addAll(definitionToRemove.remove(ontology));
					}
					
					for (final LongIterator itr = newReasonerTaxonomy.getConceptIdsToAdd().iterator(); itr.hasNext(); /* empty */) {
						final long conceptId = itr.next();
						final ConceptDefinition definitionToAdd = createConceptDefinitionToAdd(oldReasonerTaxonomy, newReasonerTaxonomy, conceptId);
						changes.addAll(definitionToAdd.add(ontology));
					}
					
					changeProcessorStopwatch.stop();
					LOGGER.info(MessageFormat.format("<<< Processing OWL ontology changes [{0}]", changeProcessorStopwatch));
					return null;
				}
			});
		}

	}

	private ConceptDefinition createConceptDefinitionToRemove(final InitialReasonerTaxonomyBuilder oldReasonerTaxonomy, final DeltaReasonerTaxonomyBuilder newReasonerTaxonomy, final long conceptId) {

		final boolean exhaustive = oldReasonerTaxonomy.isExhaustive(conceptId);
		final LongSet disjointUnionIds;

		if (exhaustive) {
			disjointUnionIds = PrimitiveSets.newLongOpenHashSet(oldReasonerTaxonomy.getSubTypeIds(conceptId));
		} else {
			disjointUnionIds = null;
		}

		final Collection<ConcreteDomainFragment> conceptDomainFragments = oldReasonerTaxonomy.getConceptConcreteDomainFragments(conceptId);
		final Set<ConcreteDomainDefinition> conceptDomainDefinitions = convertConcreteDomainFragments(conceptDomainFragments);

		final boolean primitive = oldReasonerTaxonomy.isPrimitive(conceptId);
		final ConceptDefinition result = new ConceptDefinition(conceptDomainDefinitions, conceptId, primitive, disjointUnionIds);

		final LongSet superTypeIds = oldReasonerTaxonomy.getSuperTypeIds(conceptId);
		for (final LongIterator itr = superTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long superTypeId = itr.next();
			final RelationshipDefinition definition = new RelationshipDefinition(superTypeId);
			result.addIsaDefinition(definition);
			if (oldReasonerTaxonomy.getAllSuperTypeIds(superTypeId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
				result.addRoleInclusionDefinition(definition);
			}
		}

		for (final StatementFragment statementFragment : oldReasonerTaxonomy.getNonIsAFragments(conceptId)) {

			final Collection<ConcreteDomainFragment> relationshipDomainFragments = oldReasonerTaxonomy.getStatementConcreteDomainFragments(statementFragment.getStatementId());
			final Set<ConcreteDomainDefinition> relationshipDomainDefinitions = convertConcreteDomainFragments(relationshipDomainFragments);

			final RelationshipDefinition relationshipDefinition = new RelationshipDefinition(relationshipDomainDefinitions,
					statementFragment.getTypeId(),
					statementFragment.getDestinationId(),
					statementFragment.isDestinationNegated(),
					statementFragment.isUniversal());

			if (Longs.contains(LongConcepts.NEVER_GROUPED_ROLE_IDS, statementFragment.getTypeId()) && 0 == statementFragment.getGroup()) {
				result.addNeverGroupedDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			} else {
				result.addGroupDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			}
		}

		return result;
	}

	private ConceptDefinition createConceptDefinitionToAdd(final InitialReasonerTaxonomyBuilder oldReasonerTaxonomy, final DeltaReasonerTaxonomyBuilder newReasonerTaxonomy, final long conceptId) {
		
		final boolean exhaustive = newReasonerTaxonomy.isExhaustive(conceptId);
		final LongSet disjointUnionIds;

		if (exhaustive) {
			disjointUnionIds = PrimitiveSets.newLongOpenHashSet(newReasonerTaxonomy.getSubTypeIds(conceptId));
		} else {
			disjointUnionIds = null;
		}

		final Collection<ConcreteDomainFragment> conceptDomainFragments = newReasonerTaxonomy.getConceptConcreteDomainFragments(conceptId);
		final Set<ConcreteDomainDefinition> conceptDomainDefinitions = convertConcreteDomainFragments(conceptDomainFragments);

		final boolean primitive = newReasonerTaxonomy.isPrimitive(conceptId);
		final ConceptDefinition result = new ConceptDefinition(conceptDomainDefinitions, conceptId, primitive, disjointUnionIds);

		final LongSet superTypeIds = newReasonerTaxonomy.getSuperTypeIds(conceptId);
		for (final LongIterator itr = superTypeIds.iterator(); itr.hasNext(); /* empty */) {
			final long superTypeId = itr.next();
			final RelationshipDefinition definition = new RelationshipDefinition(superTypeId);
			result.addIsaDefinition(definition);
			if (newReasonerTaxonomy.getAllSuperTypeIds(superTypeId).contains(LongConcepts.CONCEPT_MODEL_ATTRIBUTE_ID)) {
				result.addRoleInclusionDefinition(definition);
			}
		}

		for (final StatementFragment statementFragment : newReasonerTaxonomy.getNonIsAFragments(conceptId)) {

			final Collection<ConcreteDomainFragment> relationshipDomainFragments = newReasonerTaxonomy.getStatementConcreteDomainFragments(statementFragment.getStatementId());
			final Set<ConcreteDomainDefinition> relationshipDomainDefinitions = convertConcreteDomainFragments(relationshipDomainFragments);

			final RelationshipDefinition relationshipDefinition = new RelationshipDefinition(relationshipDomainDefinitions,
					statementFragment.getTypeId(),
					statementFragment.getDestinationId(),
					statementFragment.isDestinationNegated(),
					statementFragment.isUniversal());

			if (Longs.contains(LongConcepts.NEVER_GROUPED_ROLE_IDS, statementFragment.getTypeId()) && 0 == statementFragment.getGroup()) {
				result.addNeverGroupedDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			} else {
				result.addGroupDefinition(relationshipDefinition, statementFragment.getGroup(), statementFragment.getUnionGroup());
			}
		}

		return result;

	}

	private Set<ConcreteDomainDefinition> convertConcreteDomainFragments(final Collection<ConcreteDomainFragment> concreteDomainFragments) {
		final Set<ConcreteDomainDefinition> concreteDomainDefinitions = newHashSet();
	
		for (final ConcreteDomainFragment concreteDomainFragment : concreteDomainFragments) {
			concreteDomainDefinitions.add(new ConcreteDomainDefinition(concreteDomainFragment));
		}
	
		return concreteDomainDefinitions;
	}

	@Override
	public IndexCommitChangeSet commit() throws SnowowlServiceException {
		if (!changes.isEmpty()) {
			final SnomedOntologyService ontologyService = ApplicationContext.getInstance().getService(SnomedOntologyService.class);
			ontologyService.applyChanges(ontology, changes);
		}
		return new DelegatingIndexCommitChangeSet(ImmutableIndexCommitChangeSet.builder().build()) {
			@Override
			public String getDescription() {
				return "Successfully processed " + changes.size() + " changes of the OWL representation.";
			}
		};
	}

	@Override
	public void afterCommit() {
		// No-op
	}

	@Override
	public void rollback() throws SnowowlServiceException {
		changes.clear();
	}

	@Override
	public String getName() {
		return "SNOMED CT OWL Ontology";
	}
}