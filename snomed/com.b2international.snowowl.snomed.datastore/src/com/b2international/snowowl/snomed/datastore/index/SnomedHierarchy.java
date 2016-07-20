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
package com.b2international.snowowl.snomed.datastore.index;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.google.common.base.Preconditions;

/**
 * Holds a snapshot of parent-child relationships within the SNOMED CT terminology.
 */
public class SnomedHierarchy {

	/**
	 * Matrix for storing ancestor information.
	 * <ul>
	 * <li>Keys are internal concept IDs.</li>
	 * <li>Values integer hash set instances.</li>
	 */
	private final int[][] superTypes;
	
	/**
	 * Matrix for storing descendant information.
	 * <ul>
	 * <li>Keys are internal concept IDs.</li>
	 * <li>Values integer hash set instances.</li>
	 */
	private final int[][] subTypes;

	/**
	 * Stores the internal identifier of root concepts (concepts with no supertypes).
	 */
	private final int[] roots;
	
	private final LongKeyIntMap conceptIdToInternalId;

	private final long[] conceptIds;
	
	/**
	 * Creates a view of the concept hierarchy for the currently activated branch.
	 * @return the new incremental taxonomy builder instance.
	 */
	public static SnomedHierarchy forActiveBranch() {
		return forBranch(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE));
	}
	
	/**
	 * Creates a view of the concept hierarchy for the specified branch.
	 * @param branchPath the branch path.
	 * @return the new incremental taxonomy builder instance.
	 */
	public static SnomedHierarchy forBranch(final IBranchPath branchPath) {
		throw new UnsupportedOperationException("Unsupported API, make it deprecated or remove it if not required by other services");
//		final AtomicReference<long[]> conceptIdsReference = new AtomicReference<long[]>();
//		final AtomicReference<IsAStatement[]> statementsReference = new AtomicReference<IsAStatement[]>();
//		
//		final Runnable getConceptsRunnable = new Runnable() {
//			@Override public void run() {
//				final LongCollection conceptIds = getAllActiveConceptIds(branchPath);
//				conceptIds.trimToSize();
//				conceptIdsReference.set(conceptIds.toArray());
//			}
//		};
//		
//		final Runnable getStatementsRunnable = new Runnable() {
//			@Override public void run() {
//				final IsAStatement[] statements = getActiveStatements(branchPath, StatementCollectionMode.NO_IDS);
//				statementsReference.set(statements);
//			}
//		};
//		
//		ForkJoinUtils.runInParallel(getStatementsRunnable, getConceptsRunnable);
//		return new SnomedHierarchy(conceptIdsReference.get(), statementsReference.get());
	}
	
	public SnomedHierarchy(final long[] conceptIds, final IsAStatement[] statements) {
		
		Preconditions.checkNotNull(conceptIds, "Array of concept IDs argument cannot be null.");
		Preconditions.checkNotNull(statements, "Array of statements argument cannot be null.");
	
		this.conceptIdToInternalId = PrimitiveMaps.newLongKeyIntOpenHashMapWithExpectedSize(conceptIds.length);
		this.conceptIds = Arrays.copyOf(conceptIds, conceptIds.length);
		
		for (int i = 0; i < conceptIds.length; i++) {
			conceptIdToInternalId.put(conceptIds[i], i);
		}
		
		final int conceptCount = conceptIds.length;

		// allocate data
		final int[] numberOfSuperTypes = new int[conceptCount];
		final int[] numberOfSubTypes = new int[conceptCount];

		superTypes = new int[conceptCount][];
		subTypes = new int[conceptCount][];

		// only for calculating bit set initial size, could be removed sometime
		for (int i = 0; i < statements.length; i++) {
			
			final long sourceId = statements[i].getSourceId();
			final int sourceConceptInternalId = conceptIdToInternalId.get(sourceId);
			numberOfSuperTypes[sourceConceptInternalId]++;

			final long destinationId = statements[i].getDestinationId();
			final int destinationConceptInternalId = conceptIdToInternalId.get(destinationId);
			numberOfSubTypes[destinationConceptInternalId]++;
		}

		int numberOfRootConcepts = 0;
		
		for (int i = 0; i < conceptCount; i++) {

			superTypes[i] = new int[numberOfSuperTypes[i]];
			subTypes[i] = new int[numberOfSubTypes[i]];

			if (numberOfSuperTypes[i] < 1) {
				numberOfRootConcepts++;
			}
		}
		
		roots = new int[numberOfRootConcepts];

		// create index matrices for relationships
		final int[] nextSuperTypeIndex = new int[conceptCount];
		final int[] nextSubTypeIndex = new int[conceptCount];
		
		for (int i = 0; i < statements.length; i++) {

			final int sourceId = conceptIdToInternalId.get(statements[i].getSourceId());
			final int destinationId = conceptIdToInternalId.get(statements[i].getDestinationId());

			superTypes[sourceId][nextSuperTypeIndex[sourceId]++] = destinationId;
			subTypes[destinationId][nextSubTypeIndex[destinationId]++] = sourceId;
		}

		int nextRootIndex = 0;
		
		for (int i = 0; i < conceptCount; i++) {

			if (numberOfSuperTypes[i] < 1) {
				roots[nextRootIndex++] = i;
			}
		}
		
		Arrays.sort(roots);
	}
	
	public LongSet getRootConceptIds() {
		final LongSet result = PrimitiveSets.newLongOpenHashSetWithExpectedSize(roots.length);
		for (int i = 0; i < roots.length; i++) {
			result.add(getConceptId(roots[i]));
		}
		return result;
	}
	
	public boolean isRoot(final long conceptId) {
		return Arrays.binarySearch(roots, getInternalId(conceptId)) >= 0;
	}
	
	public boolean isActive(final long conceptId) {
		return conceptIdToInternalId.containsKey(conceptId);
	}
	
	/**
	 * Returns with a set of IDs of the direct descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct descendants of a concept.
	 */
	public LongSet getSubTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final int id = getInternalId(conceptId);

		final int[] subtypes = subTypes[id];

		if (CompareUtils.isEmpty(subtypes)) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}

		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(subtypes.length);

		for (int i = 0; i < subtypes.length; i++) {
			$.add(getConceptId(subtypes[i]));
		}

		return $;
	}

	/**
	 * Returns with a set of IDs of the direct ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing the direct ancestors of a concept.
	 */
	public LongSet getSuperTypeIds(final long conceptId) {

		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final int id = getInternalId(conceptId);

		final int[] supertypes = superTypes[id];

		if (CompareUtils.isEmpty(supertypes)) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}

		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(supertypes.length);

		for (int i = 0; i < supertypes.length; i++) {
			$.add(getConceptId(supertypes[i]));
		}

		return $;
	}
	
	/**
	 * Returns with a set of IDs of all descendants of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all descendants of a concept.
	 */
	public LongSet getAllSubTypesIds(final long conceptId) {
		
		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final int conceptCount = conceptIdToInternalId.size();
		final int id = getInternalId(conceptId);
	
		final BitSet subTypeMap = new BitSet(conceptCount);
	
		collectSubTypes(id, subTypeMap);
		final int count = subTypeMap.cardinality();
	
		if (0 == count) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}
	
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(count);
		for (int i = subTypeMap.nextSetBit(0); i >= 0; i = subTypeMap.nextSetBit(i + 1)) {
			$.add(getConceptId(i));
	
		}
	
		return $;
	}
	
	/**
	 * Returns with a set of IDs of all ancestors of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept.
	 * @return a set of concept IDs representing all ancestors of a concept.
	 */
	public LongSet getAllSuperTypeIds(final long conceptId) {
		
		if (!isActive(conceptId)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final int conceptCount = conceptIdToInternalId.size();
		final int id = getInternalId(conceptId);
	
		final BitSet superTypeMap = new BitSet(conceptCount);
	
		collectSuperTypes(id, superTypeMap);
		final int count = superTypeMap.cardinality();
	
		if (0 == count) { //guard against lower bound cannot be negative: 0
			return PrimitiveSets.newLongOpenHashSet();
		}
	
		final LongSet $ = PrimitiveSets.newLongOpenHashSetWithExpectedSize(count);
		for (int i = superTypeMap.nextSetBit(0); i >= 0; i = superTypeMap.nextSetBit(i + 1)) {
			$.add(getConceptId(i));
	
		}
	
		return $;
		
	}
	
	/*returns with the unique SNOMED&nbsp;CT ID of a concept given by its internal ID.*/
	private long getConceptId(final int internalId) {
		return conceptIds[internalId];
	}
	
	/*returns with the internal ID of a concept specified by the unique SNOMED&nbsp;CT ID.*/
	private int getInternalId(final long conceptId) {
		return conceptIdToInternalId.get(conceptId);
	}
	
	private void collectSuperTypes(final int type, final BitSet superTypes) {

		final int[] relationships = this.superTypes[type];
		
		if (relationships != null) {
			
			for (int i = 0; i < relationships.length; i++) {
				if (!superTypes.get(relationships[i])) {
					superTypes.set(relationships[i]); //set to true
					collectSuperTypes(relationships[i], superTypes);
				}
			}
			
		}
		
	}

	private void collectSubTypes(final int type, final BitSet subTypes) {

		final int[] relationships = this.subTypes[type];
		
		if (relationships != null) {
		
			for (int i = 0; i < relationships.length; i++) {
				if (!subTypes.get(relationships[i])) {
					subTypes.set(relationships[i]); //set to true
					collectSubTypes(relationships[i], subTypes);
				}
			}
			
		}
		
	}

	
}