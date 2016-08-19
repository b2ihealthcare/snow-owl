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
package com.b2international.snowowl.snomed.refset.core.compare;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.refset.core.compare.ReferencedComponentDelta.DeltaKind;
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
	private final SnomedClientStatementBrowser statementBrowser;
	private final String moduleId;
	private final RelationTester relationTester;

	public LinkageRefSetGenerator(final SnomedRefSetEditingContext editingContext, final SnomedClientStatementBrowser statementBrowser) {
		this.editingContext = editingContext;
		this.statementBrowser = statementBrowser;
		this.relationTester = new RelationTester(statementBrowser);
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
				fullySpecifiedName, SnomedTerminologyComponentConstants.RELATIONSHIP);
		final Collection<ReferencedComponentDelta> subsumedDeltas = Collections2.filter(referencedComponentDeltas, new SubsumedDeltaPredicate());
		final Collection<ReferencedComponentDelta> relatedDeltas = Collections2.filter(referencedComponentDeltas, new RelatedDeltaPredicate());

		SubMonitor transitiveClosureMonitor = subMonitor.newChild(1);
		transitiveClosureMonitor.setWorkRemaining(subsumedDeltas.size() + relatedDeltas.size());
		transitiveClosureMonitor.setTaskName("Calculating transitive closure...");
		Iterator<ReferencedComponentDelta> subsumedDeltasIterator = subsumedDeltas.iterator();
		Iterator<ReferencedComponentDelta> relatedDeltasIterator = relatedDeltas.iterator();

		List<List<SnomedRelationshipIndexEntry>> transitiveClosure = Lists.newArrayList();
		TransitiveClosureExtractorFunction transitiveClosureExtractorFunction = new TransitiveClosureExtractorFunction();
		TransitiveRelationExtractorFunction transitiveRelationExtractorFunction = new TransitiveRelationExtractorFunction();

		while (subsumedDeltasIterator.hasNext()) {
			transitiveClosure.add(transitiveClosureExtractorFunction.apply(subsumedDeltasIterator.next()));
			transitiveClosureMonitor.worked(1);
		}
		while (relatedDeltasIterator.hasNext()) {
			List<SnomedRelationshipIndexEntry> list = transitiveRelationExtractorFunction.apply(relatedDeltasIterator.next());
			transitiveClosure.add(list);
			transitiveClosureMonitor.worked(1);
		}
		SubMonitor redundantRelationshipFilterMonitor = subMonitor.newChild(1);
		redundantRelationshipFilterMonitor.setWorkRemaining(transitiveClosure.size());
		redundantRelationshipFilterMonitor.setTaskName("Filtering redundant relationships...");
		Set<SnomedRelationshipIndexEntry> statementSet = Sets.newLinkedHashSet();
		for (List<SnomedRelationshipIndexEntry> relationshipMiniList : transitiveClosure) {
			statementSet.addAll(relationshipMiniList);
			redundantRelationshipFilterMonitor.worked(1);
		}

		Iterator<SnomedRelationshipIndexEntry> statementSetIterator = statementSet.iterator();
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

	private final class TransitiveClosureExtractorFunction implements Function<ReferencedComponentDelta, List<SnomedRelationshipIndexEntry>> {

		@Override
		public List<SnomedRelationshipIndexEntry> apply(final ReferencedComponentDelta input) {
			return collectSuperTypeStatements(input.getReferencedComponent(), input.getRelatedTo());
		}

		private List<SnomedRelationshipIndexEntry> collectSuperTypeStatements(String conceptId, 
				String subsumingConceptId) {
			if (conceptId.equals(subsumingConceptId)) {
				return Collections.emptyList();
			}

			final List<SnomedRelationshipIndexEntry> statements = Lists.newArrayList();
			for (SnomedRelationshipIndexEntry outboundStatement : statementBrowser.getOutboundStatementsById(conceptId)) {
				if (outboundStatement.isActive() && Concepts.IS_A.equals(outboundStatement.getAttributeId())) {
					if (subsumingConceptId.equals(outboundStatement.getValueId())) {
						statements.add(outboundStatement);
					} else {
						List<SnomedRelationshipIndexEntry> temporaryStatements = collectSuperTypeStatements(outboundStatement.getValueId(), subsumingConceptId);
						if (!temporaryStatements.isEmpty()){
							statements.addAll(temporaryStatements);
							statements.add(outboundStatement);
						}
					}
				}
			}
			return statements;
		}
	}

	private final class TransitiveRelationExtractorFunction implements Function<ReferencedComponentDelta, List<SnomedRelationshipIndexEntry>> {

		@Override
		public List<SnomedRelationshipIndexEntry> apply(ReferencedComponentDelta input) {
			String conceptId = input.getReferencedComponent();
			String relatedConceptId = input.getRelatedTo();

			Set<SnomedRelationshipIndexEntry> conceptOutboundRelationships = Sets.newHashSet();
			conceptOutboundRelationships.addAll(statementBrowser.getOutboundStatementsById(conceptId));

			Set<SnomedRelationshipIndexEntry> relatedConceptInboundRelationships = Sets.newHashSet();
			relatedConceptInboundRelationships.addAll(statementBrowser.getInboundStatementsById(relatedConceptId));
			Set<SnomedRelationshipIndexEntry> commonRelationships = Sets.union(conceptOutboundRelationships, relatedConceptInboundRelationships);

			List<SnomedRelationshipIndexEntry> statements = Lists.newArrayList();

			for (SnomedRelationshipIndexEntry relationshipMini : commonRelationships) {
				final String typeId = relationshipMini.getAttributeId();
				if (relationshipMini.isActive() && !Concepts.IS_A.equals(relationshipMini.getAttributeId()) && relationTester.isRelated(relatedConceptId, conceptId, typeId)) {
					List<SnomedRelationshipIndexEntry> list = collectRelationshipTargetStatements(relatedConceptId, conceptId, typeId, new HashSet<String>());
					statements.addAll(list);
				}
			}
			return statements;
		}

		private List<SnomedRelationshipIndexEntry> collectRelationshipTargetStatements(String relatedConceptId, String conceptId, String typeId, Set<String> visited) {
			if (!visited.add(conceptId)){
				return Collections.emptyList();
			}
			
			final List<SnomedRelationshipIndexEntry> statements = Lists.newArrayList();
			for (SnomedRelationshipIndexEntry outboundRelationship : statementBrowser.getOutboundStatementsById(conceptId)) {
				if (outboundRelationship.isActive() && typeId.equals(outboundRelationship.getAttributeId())) {

					String relationshipTargetId = outboundRelationship.getValueId();
					if (relatedConceptId.equals(relationshipTargetId)) {
						statements.add(outboundRelationship);
					} else {
						final List<SnomedRelationshipIndexEntry> temporaryStatements = collectRelationshipTargetStatements(relatedConceptId, relationshipTargetId, typeId, visited);
						if (!temporaryStatements.isEmpty()){
							statements.addAll(temporaryStatements);
							statements.add(outboundRelationship);
						}
					}
				}
			}
			return statements;
		}

	}

	private final class StatementToRelationshipRefSetMemberTransformerFunction implements Function<SnomedRelationshipIndexEntry, SnomedRefSetMember> {

		private final SnomedRegularRefSet refSet;

		public StatementToRelationshipRefSetMemberTransformerFunction(final SnomedRegularRefSet refSet) {
			this.refSet = refSet;
		}

		@Override
		public SnomedRefSetMember apply(SnomedRelationshipIndexEntry input) {
			return editingContext.createSimpleTypeRefSetMember(ComponentIdentifierPair.create(
					SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, input.getId()), moduleId, refSet);
		}
	}
}