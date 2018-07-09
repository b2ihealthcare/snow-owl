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
package com.b2international.snowowl.datastore.server.snomed.index.taxonomy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;

/**
 * Represents a snapshot of the ontology for reasoner input and normal form generation.
 * 
 * @since 7.0
 */
public final class ReasonerTaxonomy {

	private final InternalIdMap conceptMap;

	private final InternalIdEdges statedAncestors;
	private final InternalIdEdges statedDescendants;

	private final InternalSctIdSet fullyDefinedConcepts;
	private final InternalSctIdSet exhaustiveConcepts;

	private final InternalIdMultimap<StatementFragment> statedNonIsARelationships;
	private final InternalIdMultimap<StatementFragment> inferredNonIsARelationships;
	private final InternalIdMultimap<ConcreteDomainFragment> statedConcreteDomainMembers;
	private final InternalIdMultimap<ConcreteDomainFragment> inferredConcreteDomainMembers;

	private final InternalIdEdges inferredAncestors;
	private final InternalSctIdSet unsatisfiableConcepts;
	private final InternalSctIdMultimap equivalentConcepts;

	/*package*/ ReasonerTaxonomy(final InternalIdMap conceptMap, 
			final InternalIdEdges statedAncestors,
			final InternalIdEdges statedDescendants, 
			final InternalSctIdSet fullyDefinedConcepts,
			final InternalSctIdSet exhaustiveConcepts, 
			final InternalIdMultimap<StatementFragment> statedNonIsARelationships,
			final InternalIdMultimap<StatementFragment> inferredNonIsARelationships,
			final InternalIdMultimap<ConcreteDomainFragment> statedConcreteDomainMembers,
			final InternalIdMultimap<ConcreteDomainFragment> inferredConcreteDomainMembers,
			final InternalIdEdges inferredAncestors,
			final InternalSctIdSet unsatisfiableConcepts,
			final InternalSctIdMultimap equivalentConcepts) {

		this.conceptMap = conceptMap;
		this.statedAncestors = statedAncestors;
		this.statedDescendants = statedDescendants;
		this.fullyDefinedConcepts = fullyDefinedConcepts;
		this.exhaustiveConcepts = exhaustiveConcepts;
		this.statedNonIsARelationships = statedNonIsARelationships;
		this.inferredNonIsARelationships = inferredNonIsARelationships;
		this.statedConcreteDomainMembers = statedConcreteDomainMembers;
		this.inferredConcreteDomainMembers = inferredConcreteDomainMembers;
		this.inferredAncestors = inferredAncestors;
		this.unsatisfiableConcepts = unsatisfiableConcepts;
		this.equivalentConcepts = equivalentConcepts;
	}

	public InternalIdMap getConceptMap() {
		return conceptMap;
	}

	public InternalIdEdges getStatedAncestors() {
		return statedAncestors;
	}

	public InternalIdEdges getStatedDescendants() {
		return statedDescendants;
	}

	public InternalIdEdges getInferredAncestors() {
		return checkNotNull(inferredAncestors, "Inferred ancestors are unset on this taxonomy.");
	}

	public InternalSctIdSet getUnsatisfiableConcepts() {
		return checkNotNull(unsatisfiableConcepts, "Unsatisfiable concept IDs are unset on this taxonomy.");
	}

	public InternalSctIdMultimap getEquivalentConcepts() {
		return checkNotNull(equivalentConcepts, "Inferred equivalences are unset on this taxonomy.");
	}

	public InternalSctIdSet getFullyDefinedConcepts() {
		return fullyDefinedConcepts;
	}

	public InternalSctIdSet getExhaustiveConcepts() {
		return exhaustiveConcepts;
	}

	public InternalIdMultimap<StatementFragment> getStatedNonIsARelationships() {
		return statedNonIsARelationships;
	}

	public InternalIdMultimap<StatementFragment> getInferredNonIsARelationships() {
		return inferredNonIsARelationships;
	}

	public InternalIdMultimap<ConcreteDomainFragment> getStatedConcreteDomainMembers() {
		return statedConcreteDomainMembers;
	}

	public InternalIdMultimap<ConcreteDomainFragment> getInferredConcreteDomainMembers() {
		return inferredConcreteDomainMembers;
	}

	public ReasonerTaxonomy withInferences(final InternalIdEdges newInferredAncestors, 
			final InternalSctIdSet newUnsatisfiableConcepts,
			final InternalSctIdMultimap newEquivalentConcepts) {

		checkNotNull(newInferredAncestors, "Inferred ancestors may not be null.");
		checkNotNull(newUnsatisfiableConcepts, "Inferred unsatisfiable concepts may not be null.");
		checkNotNull(newEquivalentConcepts, "Inferred equivalent concept sets may not be null.");

		checkState(this.inferredAncestors == null, "Inferred ancestors are already present in this taxonomy.");
		checkState(this.unsatisfiableConcepts == null, "Inferred unsatisfiable concepts are already present in this taxonomy.");
		checkState(this.equivalentConcepts == null, "Inferred equivalent concept sets are already present in this taxonomy.");

		return new ReasonerTaxonomy(conceptMap, 
				statedAncestors, 
				statedDescendants, 
				fullyDefinedConcepts, 
				exhaustiveConcepts, 
				statedNonIsARelationships, 
				inferredNonIsARelationships, 
				statedConcreteDomainMembers, 
				inferredConcreteDomainMembers,
				newInferredAncestors, 
				newUnsatisfiableConcepts,
				newEquivalentConcepts);
	}
}
