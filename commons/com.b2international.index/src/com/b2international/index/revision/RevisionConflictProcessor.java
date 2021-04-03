/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.b2international.commons.CompareUtils;
import com.b2international.index.IndexException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

/**
 * @since 7.0
 */
public interface RevisionConflictProcessor {
	
	/**
	 * Checks if the specified {@link RevisionPropertyDiff} from the source change set conflicts with the corresponding {@code RevisionPropertyDiff} on the target.
	 * @param revisionId - the affected revision identifier
	 * @param mapping - the document mapping to access type related information
	 * @param sourceChange - the single-value change on the source
	 * @param targetChange - the single-value change on the target
	 * @param mapper - mapper to use to read serialized nested arrays and objects 
	 * @return <ul>
	 * <li>{@code null} if a conflict should be reported;
	 * <li>a {@link RevisionPropertyDiff} containing the "winning" change otherwise.
	 * </ul>
	 */
	RevisionPropertyDiff handleChangedInSourceAndTarget(String revisionId, DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper);

	/**
	 * @param objectId
	 * @param sourceChanges
	 * @return
	 */
	Conflict handleChangedInSourceDetachedInTarget(ObjectId objectId, List<RevisionPropertyDiff> sourceChanges);
	
	/**
	 * Maps a raw revision property value to a human readable String version.
	 * 
	 * @param property
	 * @param value
	 * @return
	 */
	default String convertPropertyValue(String property, String value) {
		return value;
	}
	
	/**
	 * Alter the internals of the conflict to make it more domain specific. 
	 * 
	 * @param conflict
	 * @return
	 */
	default Conflict convertConflict(Conflict conflict) {
		return conflict;
	}
	
	/**
	 * Post-processes the resulting staging area and the change sets before committing the changeset.
	 * 
	 * @param staging - the current state of the staging area
	 * @return - a list of additional conflicts to report or empty collection if there are no domain specific conflicts, never <code>null</code>.
	 */
	List<Conflict> checkConflicts(StagingArea staging, RevisionBranchChangeSet fromChanges, RevisionBranchChangeSet toChanges);
	
	/**
	 * Filters conflicts when certain conditions are met (eg. donated content is reported as a conflict, which can be resolved automatically during upgrades).
	 * 
	 * @param staging - the current state of the staging area
	 * @param conflicts - the conflicts to filter
	 * @return the remaining conflicts after detecting and/or removing automatically resolved ones
	 */
	default List<Conflict> filterConflicts(StagingArea staging, List<Conflict> conflicts) {
		return conflicts;
	}
	
	/**
	 * @since 7.0
	 */
	class Default implements RevisionConflictProcessor {
		
		@Override
		public RevisionPropertyDiff handleChangedInSourceAndTarget(String revisionId, DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper) {
			String property = sourceChange.getProperty();
			if (Objects.equals(sourceChange.getNewValue(), targetChange.getNewValue())) {
				// apply source change if the new value is exactly the same as the target change
				return sourceChange;
			} else if (mapping.isCollection(property)) {
				// in case of Collection/Array properties, allow subsets to be merged together 
				// eg. [1,2] -> [1,2,3] vs. [1,2] -> [1,2,4] should not produce conflicts, but merge the array into [1,2,3,4]
				return handleCollectionConflict(mapping, sourceChange, targetChange, mapper, mapping.isSet(property));
			} else {
				return null; 
			}
		}
		
		protected RevisionPropertyDiff handleCollectionConflict(DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper, boolean isSet) {
			try {
				ArrayNode oldArray = CompareUtils.isEmpty(sourceChange.getOldValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(sourceChange.getOldValue());
				ArrayNode sourceArray = CompareUtils.isEmpty(sourceChange.getNewValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(sourceChange.getNewValue());
				ArrayNode targetArray = CompareUtils.isEmpty(targetChange.getNewValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(targetChange.getNewValue());
				
				List<JsonNode> sourceChanges = getChanges(oldArray, sourceArray);
				List<JsonNode> targetChanges = getChanges(oldArray, targetArray);
				
				if (sourceChanges == null || targetChanges == null) {
					// if either source or target array modified the old array in a destructive way, report conflict
					return null;
				} else {
					// take all source changes
					oldArray.addAll(sourceChanges);
					if (isSet) {
						// filter out duplicates that have been added on both sides if this collection should be handled as a Set
						targetChanges.removeIf(sourceChanges::contains);
					}
					oldArray.addAll(targetChanges);
					return sourceChange.withNewValue(oldArray.toString());
				}
			} catch (JsonProcessingException e) {
				throw new IndexException("Couldn't parse json", e);
			}
		}

		private List<JsonNode> getChanges(ArrayNode oldArray, ArrayNode newArray) {
			Iterator<JsonNode> oldItems = oldArray.iterator();
			Iterator<JsonNode> newItems = newArray.iterator();
			
			while (oldItems.hasNext() && newItems.hasNext()) {
				JsonNode oldItem = oldItems.next();
				JsonNode sourceItem = newItems.next();
				// if common part is not equal, then report conflict
				if (!oldItem.equals(sourceItem)) {
					return null;
				}
			}
			
			// if oldArray has more items then newArray removed them, report destructive change
			if (oldItems.hasNext()) {
				return null;
			}
			
			// construct new items list that have been added to the old array 
			return Lists.newArrayList(newItems);
		}

		@Override
		public Conflict handleChangedInSourceDetachedInTarget(ObjectId objectId, List<RevisionPropertyDiff> sourceChanges) {
			return null; // by default do not report conflict and omit the changes
		}
		
		@Override
		public List<Conflict> checkConflicts(StagingArea staging, RevisionBranchChangeSet fromChanges, RevisionBranchChangeSet toChanges) {
			return Collections.emptyList();
		}
		
	}

}
