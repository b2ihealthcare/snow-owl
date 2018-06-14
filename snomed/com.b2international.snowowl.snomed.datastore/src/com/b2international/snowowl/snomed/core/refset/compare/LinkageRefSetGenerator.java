/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.refset.compare;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.refset.compare.ReferencedComponentDelta.DeltaKind;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Generates a simple type reference set containing the transitive closure relationships of a list of {@link ReferencedComponentDelta referenced component deltas}. Used to generate
 * <em>linkage sets</em> from the results of a reference set relation comparison.
 * 
 */
public class LinkageRefSetGenerator {

	private final SnomedRefSetEditingContext editingContext;
	private final String moduleId;
	private final RelationTester relationTester;

	public LinkageRefSetGenerator(final SnomedRefSetEditingContext editingContext) {
		this.editingContext = editingContext;
		this.relationTester = new RelationTester();
		this.moduleId = this.editingContext.getSnomedEditingContext().getDefaultModuleConcept().getModule().getId();
	}

	/**
	 * Generates a reference set containing all the transitive closure relationships between the subsuming and subsumed concepts in the {@link ReferencedComponentDelta referenced
	 * component deltas}.
	 * 
	 * @param fullySpecifiedName
	 *            the fully specified name of the reference set
	 * @param referencedComponentDeltas
	 *            the referenced component deltas to process
	 * @param monitor
	 *            the progress monitor
	 * @return the transitive closure relationship reference set
	 */
	public SnomedRegularRefSet generate(final String fullySpecifiedName,
			final List<ReferencedComponentDelta> referencedComponentDeltas, final IProgressMonitor monitor) {
		checkNotNull(fullySpecifiedName, "Reference set fully specified name must not be null.");
		checkNotNull(referencedComponentDeltas, "Referenced component delta list must not be null.");
		checkNotNull(monitor, "Progress monitor must not be null.");

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Generating linkage reference set...", 3);
		final SnomedRegularRefSet refSet = editingContext.createSnomedSimpleTypeRefSet(
				fullySpecifiedName, 
				SnomedTerminologyComponentConstants.RELATIONSHIP,
				Concepts.REFSET_LANGUAGE_TYPE_UK);
		final Collection<ReferencedComponentDelta> subsumedDeltas = Collections2.filter(referencedComponentDeltas, new SubsumedDeltaPredicate());
		final Collection<ReferencedComponentDelta> relatedDeltas = Collections2.filter(referencedComponentDeltas, new RelatedDeltaPredicate());

		SubMonitor transitiveClosureMonitor = subMonitor.newChild(1);
		transitiveClosureMonitor.setWorkRemaining(subsumedDeltas.size() + relatedDeltas.size());
		transitiveClosureMonitor.setTaskName("Calculating transitive closure...");
		Iterator<ReferencedComponentDelta> subsumedDeltasIterator = subsumedDeltas.iterator();
		Iterator<ReferencedComponentDelta> relatedDeltasIterator = relatedDeltas.iterator();

		List<List<SnomedRelationship>> transitiveClosure = Lists.newArrayList();
		TransitiveClosureExtractorFunction transitiveClosureExtractorFunction = new TransitiveClosureExtractorFunction();
		TransitiveRelationExtractorFunction transitiveRelationExtractorFunction = new TransitiveRelationExtractorFunction();

		while (subsumedDeltasIterator.hasNext()) {
			transitiveClosure.add(transitiveClosureExtractorFunction.apply(subsumedDeltasIterator.next()));
			transitiveClosureMonitor.worked(1);
		}
		while (relatedDeltasIterator.hasNext()) {
			List<SnomedRelationship> list = transitiveRelationExtractorFunction.apply(relatedDeltasIterator.next());
			transitiveClosure.add(list);
			transitiveClosureMonitor.worked(1);
		}
		SubMonitor redundantRelationshipFilterMonitor = subMonitor.newChild(1);
		redundantRelationshipFilterMonitor.setWorkRemaining(transitiveClosure.size());
		redundantRelationshipFilterMonitor.setTaskName("Filtering redundant relationships...");
		Set<SnomedRelationship> statementSet = Sets.newLinkedHashSet();
		for (List<SnomedRelationship> relationshipMiniList : transitiveClosure) {
			statementSet.addAll(relationshipMiniList);
			redundantRelationshipFilterMonitor.worked(1);
		}

		Iterator<SnomedRelationship> statementSetIterator = statementSet.iterator();
		StatementToRelationshipRefSetMemberTransformerFunction function = new StatementToRelationshipRefSetMemberTransformerFunction(refSet);
		List<SnomedRefSetMember> refSetMembers = Lists.newArrayList();
		SubMonitor addMembersMonitor = subMonitor.newChild(1);
		addMembersMonitor.setWorkRemaining(statementSet.size());
		addMembersMonitor.setTaskName("Adding reference set members to linkage set...");
		while (statementSetIterator.hasNext()) {
			refSetMembers.add(function.apply(statementSetIterator.next()));
			addMembersMonitor.worked(1);
		}
		refSet.getMembers().addAll(ImmutableList.copyOf(refSetMembers));
		return refSet;
	}

	private final class SubsumedDeltaPredicate implements Predicate<ReferencedComponentDelta> {
		@Override
		public boolean apply(final ReferencedComponentDelta input) {
			return DeltaKind.SUBSUMED.equals(input.getDeltaKind());
		}
	}

	private final class RelatedDeltaPredicate implements Predicate<ReferencedComponentDelta> {
		@Override
		public boolean apply(final ReferencedComponentDelta input) {
			return DeltaKind.RELATED.equals(input.getDeltaKind());
		}
	}

	private final class TransitiveClosureExtractorFunction implements Function<ReferencedComponentDelta, List<SnomedRelationship>> {

		@Override
		public List<SnomedRelationship> apply(final ReferencedComponentDelta input) {
			return collectSuperTypeStatements(input.getReferencedComponent(), input.getRelatedTo());
		}

		private List<SnomedRelationship> collectSuperTypeStatements(String conceptId, String subsumingConceptId) {
			if (conceptId.equals(subsumingConceptId)) {
				return Collections.emptyList();
			}

			final List<SnomedRelationship> statements = Lists.newArrayList();
			for (SnomedRelationship outboundStatement : getActiveOutboundIsaStatements(editingContext.getBranch(), conceptId)) {
				if (subsumingConceptId.equals(outboundStatement.getDestinationId())) {
					statements.add(outboundStatement);
				} else {
					List<SnomedRelationship> temporaryStatements = collectSuperTypeStatements(outboundStatement.getDestinationId(), subsumingConceptId);
					if (!temporaryStatements.isEmpty()){
						statements.addAll(temporaryStatements);
						statements.add(outboundStatement);
					}
				}
			}
			return statements;
		}
	}

	private final class TransitiveRelationExtractorFunction implements Function<ReferencedComponentDelta, List<SnomedRelationship>> {

		@Override
		public List<SnomedRelationship> apply(ReferencedComponentDelta input) {
			String conceptId = input.getReferencedComponent();
			String relatedConceptId = input.getRelatedTo();

			Set<SnomedRelationship> conceptOutboundRelationships = Sets.newHashSet();
			conceptOutboundRelationships.addAll(getOutboundStatementsById(editingContext.getBranch(), conceptId).getItems());

			Set<SnomedRelationship> relatedConceptInboundRelationships = Sets.newHashSet();
			relatedConceptInboundRelationships.addAll(getInboundStatementsById(editingContext.getBranch(), relatedConceptId).getItems());
			Set<SnomedRelationship> commonRelationships = Sets.union(conceptOutboundRelationships, relatedConceptInboundRelationships);

			List<SnomedRelationship> statements = Lists.newArrayList();

			for (SnomedRelationship relationshipMini : commonRelationships) {
				final String typeId = relationshipMini.getTypeId();
				if (relationshipMini.isActive() && !Concepts.IS_A.equals(relationshipMini.getTypeId()) && relationTester.isRelated(relatedConceptId, conceptId, typeId)) {
					List<SnomedRelationship> list = collectRelationshipTargetStatements(relatedConceptId, conceptId, typeId, new HashSet<String>());
					statements.addAll(list);
				}
			}
			return statements;
		}

		private List<SnomedRelationship> collectRelationshipTargetStatements(String relatedConceptId, String conceptId, String typeId, Set<String> visited) {
			if (!visited.add(conceptId)){
				return Collections.emptyList();
			}
			
			final List<SnomedRelationship> statements = Lists.newArrayList();
			for (SnomedRelationship outboundRelationship : getActiveOutboundStatements(editingContext.getBranch(), conceptId, typeId)) {
				final String relationshipTargetId = outboundRelationship.getDestinationId();
				if (relatedConceptId.equals(relationshipTargetId)) {
					statements.add(outboundRelationship);
				} else {
					final List<SnomedRelationship> temporaryStatements = collectRelationshipTargetStatements(relatedConceptId, relationshipTargetId, typeId, visited);
					if (!temporaryStatements.isEmpty()){
						statements.addAll(temporaryStatements);
						statements.add(outboundRelationship);
					}
				}
			}
			return statements;
		}

	}

	private final class StatementToRelationshipRefSetMemberTransformerFunction implements Function<SnomedRelationship, SnomedRefSetMember> {

		private final SnomedRegularRefSet refSet;

		public StatementToRelationshipRefSetMemberTransformerFunction(final SnomedRegularRefSet refSet) {
			this.refSet = refSet;
		}

		@Override
		public SnomedRefSetMember apply(SnomedRelationship input) {
			return editingContext.createSimpleTypeRefSetMember(input.getId(), moduleId, refSet);
		}
	}

	public static SnomedRelationships getActiveOutboundIsaStatements(String branch, String conceptId) {
		return getActiveOutboundStatements(branch, conceptId, Concepts.IS_A);
	}
	
	public static SnomedRelationships getActiveOutboundStatements(String branch, String conceptId, String typeId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByType(typeId)
				.filterBySource(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	public static SnomedRelationships getInboundStatementsById(String branch, String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByDestination(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}

	public static SnomedRelationships getOutboundStatementsById(String branch, String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterBySource(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
	}
}