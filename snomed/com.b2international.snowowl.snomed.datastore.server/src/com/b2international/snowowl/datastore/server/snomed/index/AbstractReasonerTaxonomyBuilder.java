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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.ArrayIntIterator;
import com.b2international.commons.pcj.BitSetIntIterator;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import bak.pcj.IntIterator;
import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;
import bak.pcj.map.LongKeyIntMap;
import bak.pcj.map.LongKeyIntOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

/**
 * Abstract superclass for reasoner taxonomy builders. Subclasses should gather and/or modify information contained here about the ontology.
 *
 */
public abstract class AbstractReasonerTaxonomyBuilder {
	
	protected static final long IS_A_ID = Long.parseLong(Concepts.IS_A);
	protected static final long STATED_RELATIONSHIP = Long.parseLong(Concepts.STATED_RELATIONSHIP);
	protected static final long INFERRED_RELATIONSHIP = Long.parseLong(Concepts.INFERRED_RELATIONSHIP);
	protected static final long DEFINING_RELATIONSHIP = Long.parseLong(Concepts.DEFINING_RELATIONSHIP);
	protected static final long ADDITIONAL_RELATIONSHIP = Long.parseLong(Concepts.ADDITIONAL_RELATIONSHIP);
	
	/** Matrix for storing concept ancestors by internal IDs. */
	protected int[][] superTypes;

	/** Matrix for storing concept descendants by internal IDs. */
	protected int[][] subTypes;

	/** A set containing all exhaustive concept IDs. */
	protected LongSet exhaustiveConceptIds;

	/** A set containing all fully defined concept IDs. */
	protected LongSet fullyDefinedConceptIds;

	/** Mapping between concept IDs and the associated active stated outbound relationships. */
	protected LongKeyMap/*<StatementFragment>*/ conceptIdToStatedStatements;
	
	/** Mapping between concept IDs and the associated active inferred outbound relationships. */
	protected LongKeyMap/*<StatementFragment>*/ conceptIdToInferredStatements;
	
	/** Mapping between component IDs and the associated active stated concrete domain members. */
	protected LongKeyMap/*<ConcreteDomainFragment>*/ componentIdToStatedConcreteDomains;
	
	/** Mapping between component IDs and the associated active inferred concrete domain members. */
	protected LongKeyMap/*<ConcreteDomainFragment>*/ componentIdToInferredConcreteDomains;

	/** Maps internal IDs to SNOMED&nbsp;CT concept IDs. */
	protected LongList internalIdToconceptId;

	/** Maps SNOMED&nbsp;CT concept IDs to internal IDs. */
	protected LongKeyIntMap conceptIdToInternalId;

	/**
	 * Default constructor; subclasses should initialize fields.
	 */
	protected AbstractReasonerTaxonomyBuilder() { }

	/**
	 * Copy constructor; fields are initialized from the given source builder.
	 *
	 * @param source the builder to copy (may not be {@code null})
	 */
	protected AbstractReasonerTaxonomyBuilder(final AbstractReasonerTaxonomyBuilder source) {
		checkNotNull(source, "source");

		// We can rebuild these; we have the technology
		this.superTypes = null; 
		this.subTypes = null;
		
		this.exhaustiveConceptIds = (LongOpenHashSet) ((LongOpenHashSet) source.exhaustiveConceptIds).clone();
		this.fullyDefinedConceptIds = (LongOpenHashSet) ((LongOpenHashSet) source.fullyDefinedConceptIds).clone();
		this.conceptIdToStatedStatements = (LongKeyOpenHashMap) ((LongKeyOpenHashMap) source.conceptIdToStatedStatements).clone();
		this.conceptIdToInferredStatements = (LongKeyOpenHashMap) ((LongKeyOpenHashMap) source.conceptIdToInferredStatements).clone();
		this.componentIdToStatedConcreteDomains = (LongKeyOpenHashMap) ((LongKeyOpenHashMap) source.componentIdToStatedConcreteDomains).clone();
		this.componentIdToInferredConcreteDomains = (LongKeyOpenHashMap) ((LongKeyOpenHashMap) source.componentIdToInferredConcreteDomains).clone();
		this.internalIdToconceptId = (LongArrayList) ((LongArrayList) source.internalIdToconceptId).clone();
		this.conceptIdToInternalId = (LongKeyIntOpenHashMap) ((LongKeyIntOpenHashMap) source.conceptIdToInternalId).clone();
	}

	/**
	 * Returns with all the active source relationships of a concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return the active source relationships.
	 */
	@SuppressWarnings("unchecked")
	public Collection<StatementFragment> getStatedStatementFragments(final long conceptId) {
		final Object statements = conceptIdToStatedStatements.get(conceptId);
		return (Collection<StatementFragment>) (null == statements ? Collections.emptySet() : statements);
	}

	@SuppressWarnings("unchecked")
	public Collection<StatementFragment> getInferredStatementFragments(final long conceptId) {
		final Object statements = conceptIdToInferredStatements.get(conceptId);
		return (Collection<StatementFragment>) (null == statements ? Collections.emptySet() : statements);
	}
	
	/**
	 * Returns with all *NON* IS_A stated active source relationships of a concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return the active *NON* IS_A source relationships.
	 */
	public Collection<StatementFragment> getStatedNonIsAFragments(final long conceptId) {
		return Collections2.filter(getStatedStatementFragments(conceptId), new Predicate<StatementFragment>() {
			@Override public boolean apply(final StatementFragment statementFragment) {
				return IS_A_ID != statementFragment.getTypeId();
			}
		});
	}
	
	/**
	 * Returns with all *NON* IS_A inferred active source relationships of a concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return the active *NON* IS_A source relationships.
	 */
	public Collection<StatementFragment> getInferredNonIsAFragments(final long conceptId) {
		return Collections2.filter(getInferredStatementFragments(conceptId), new Predicate<StatementFragment>() {
			@Override public boolean apply(final StatementFragment statementFragment) {
				return IS_A_ID != statementFragment.getTypeId();
			}
		});
	}

	public boolean isActive(final long conceptId) {
		return conceptIdToInternalId.containsKey(conceptId);
	}

	/**
	 * Returns {@code true} if the concept given by its ID is exhaustive, otherwise returns with {@code false}.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept to check.
	 * @return {@code true} if the concept is exhaustive, otherwise {@code false}.
	 */
	public boolean isExhaustive(final long conceptId) {
		if (exhaustiveConceptIds.isEmpty()) {
			return false;
		}

		return exhaustiveConceptIds.contains(conceptId);
	}

	/**
	 * Returns {@code true} if the concept given by its ID is primitive, otherwise returns with {@code false}.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept to check.
	 * @return {@code true} if the concept is primitive, otherwise {@code false}.
	 */
	public boolean isPrimitive(final long conceptId) {
		if (fullyDefinedConceptIds.isEmpty()) {
			return true;
		}

		return !fullyDefinedConceptIds.contains(conceptId);
	}

	/**
	 * Returns with all the stated concrete domains of the component given by its unique ID.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT component.
	 * @return the concrete domains associated with the component, if any.
	 */
	@SuppressWarnings("unchecked")
	public Collection<ConcreteDomainFragment> getStatedConcreteDomainFragments(final long componentId) {
		final Object concreteDomains = componentIdToStatedConcreteDomains.get(componentId);
		return (Collection<ConcreteDomainFragment>) (null == concreteDomains ? Collections.emptySet() : concreteDomains);
	}

	/**
	 * Returns with all the inferred concrete domains of a component given by its unique ID.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT component.
	 * @return the concrete domains associated with the component, if any.
	 */
	@SuppressWarnings("unchecked")
	public Collection<ConcreteDomainFragment> getInferredConcreteDomainFragments(final long componentId) {
		final Object concreteDomains = componentIdToInferredConcreteDomains.get(componentId);
		return (Collection<ConcreteDomainFragment>) (null == concreteDomains ? Collections.emptySet() : concreteDomains);
	}

	/**
	 * Returns with a set of active SNOMED&nbsp;CT concept IDs.
	 * @return a set of concept IDs.
	 */
	public LongSet getConceptIdSet() {
		return conceptIdToInternalId.keySet();
	}

	/**
	 * Returns {@code true} if the current builder instance does not contain any concepts.
	 * Other wise returns {@code false}.
	 * @return {@code true} if the current instance is empty. Otherwise {@code false}.
	 */
	public boolean isEmpty() {
		return 0 == conceptIdToInternalId.size();
	}

	/**
	 * Returns with a set of IDs of the direct descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct descendants of a concept.
	 */
	public LongSet getSubTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return new LongOpenHashSet();
		}

		final int id = getInternalId(conceptId);

		final int[] subtypes = subTypes[id];

		if (CompareUtils.isEmpty(subtypes)) { //guard against lower bound cannot be negative: 0
			return new LongOpenHashSet();
		}

		return convertToConceptIds(new ArrayIntIterator(subtypes));
	}

	/**
	 * Returns with a set of IDs of the direct ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct ancestors of a concept.
	 */
	public LongSet getSuperTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return new LongOpenHashSet();
		}

		final int id = getInternalId(conceptId);

		final int[] supertypes = superTypes[id];

		if (CompareUtils.isEmpty(supertypes)) { //guard against lower bound cannot be negative: 0
			return new LongOpenHashSet();
		}

		return convertToConceptIds(new ArrayIntIterator(supertypes));
	}

	/**
	 * Returns with a set of IDs of all descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all descendants of a concept.
	 */
	public LongSet getAllSubTypesIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return new LongOpenHashSet();
		}

		final int conceptCount = internalIdToconceptId.size();
		final int id = getInternalId(conceptId);

		final BitSet subTypeMap = new BitSet(conceptCount);

		collectSubTypes(id, subTypeMap);
		return convertToConceptIds(new BitSetIntIterator(subTypeMap));
	}

	/**
	 * Returns with a set of IDs of all ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all ancestors of a concept.
	 */
	public LongSet getAllSuperTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return new LongOpenHashSet();
		}

		final int conceptCount = internalIdToconceptId.size();
		final int id = getInternalId(conceptId);

		final BitSet superTypeMap = new BitSet(conceptCount);

		collectSuperTypes(id, superTypeMap);
		return convertToConceptIds(new BitSetIntIterator(superTypeMap));

	}

	private void collectSuperTypes(final int internalId, final BitSet superTypes) {
		final int[] relationships = this.superTypes[internalId];

		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!superTypes.get(relationships[i])) {
					superTypes.set(relationships[i]); //set to true
					collectSuperTypes(relationships[i], superTypes);
				}
			}
		}
	}

	private void collectSubTypes(final int internalId, final BitSet subTypes) {
		final int[] relationships = this.subTypes[internalId];

		if (relationships != null) {
			for (int i = 0; i < relationships.length; i++) {
				if (!subTypes.get(relationships[i])) {
					subTypes.set(relationships[i]); //set to true
					collectSubTypes(relationships[i], subTypes);
				}
			}
		}
	}

	private long getConceptId(final int internalId) {
		return internalIdToconceptId.get(internalId);
	}

	protected int getInternalId(final long conceptId) {
		return conceptIdToInternalId.get(conceptId);
	}

	private LongSet convertToConceptIds(final IntIterator it) {
		final LongSet result = new LongOpenHashSet();

		while (it.hasNext()) {
			result.add(getConceptId(it.next()));
		}

		return result;
	}
}
