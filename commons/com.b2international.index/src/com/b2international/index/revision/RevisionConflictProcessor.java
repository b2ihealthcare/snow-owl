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

import java.util.*;

import org.elasticsearch.common.util.set.Sets;

import com.b2international.commons.CompareUtils;
import com.b2international.index.IndexException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.index.util.ArrayDiff;
import com.b2international.index.util.JsonDiff;
import com.b2international.index.util.JsonDiff.JsonChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

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
			if (mapping.isObject(property)) {
				// in case of Object properties, allow changes touching different properties to be merged without reporting conflict 
				return handleObjectConflict(mapping, sourceChange, targetChange, mapper);
			} else if (mapping.isCollection(property)) {
				// in case of Collection/Array properties, allow subsets to be merged together 
				// eg. [1,2] -> [1,2,3] vs. [1,2] -> [1,2,4] should not produce conflicts, but merge the array into [1,2,3,4]
				return handleCollectionConflict(mapping, sourceChange, targetChange, mapper, mapping.isSet(property));
			} else if (Objects.equals(sourceChange.getNewValue(), targetChange.getNewValue())) {
				// apply source change if the new value is exactly the same as the target change
				return sourceChange;
			} else {
				return null;
			}
		}

		private RevisionPropertyDiff handleObjectConflict(DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper) {
			try {
				ObjectNode oldObject = CompareUtils.isEmpty(sourceChange.getOldValue()) ? mapper.createObjectNode() : (ObjectNode) mapper.readTree(sourceChange.getOldValue());
				ObjectNode sourceObject = CompareUtils.isEmpty(sourceChange.getNewValue()) ? mapper.createObjectNode() : (ObjectNode) mapper.readTree(sourceChange.getNewValue());
				ObjectNode targetObject = CompareUtils.isEmpty(targetChange.getNewValue()) ? mapper.createObjectNode() : (ObjectNode) mapper.readTree(targetChange.getNewValue());

				JsonDiff sourceDiff = JsonDiff.diff(oldObject, sourceObject).withoutNullAdditions(mapper);
				JsonDiff targetDiff = JsonDiff.diff(oldObject, targetObject).withoutNullAdditions(mapper);
				
				Map<String, JsonChange> sourceChanges = Maps.uniqueIndex(sourceDiff.getChanges(), JsonChange::getRootFieldPath);
				Map<String, JsonChange> targetChanges = Maps.uniqueIndex(targetDiff.getChanges(), JsonChange::getRootFieldPath);
				
				final Set<String> conflictingFields = Sets.intersection(sourceChanges.keySet(), targetChanges.keySet());
				
				for (String conflictingField : conflictingFields) {
					JsonChange sourceJsonChange = sourceChanges.get(conflictingField);
					JsonChange targetJsonChange = targetChanges.get(conflictingField);
					
					// report conflict if any change differs in any way on any property
					if (!Objects.equals(sourceJsonChange, targetJsonChange)) {
						return null;
					}
				}

				// otherwise apply both changes on old object to merge them
				sourceDiff.applyInPlace(oldObject);
				targetDiff.applyInPlace(oldObject);
				
				// if the resulting object would be empty, then null it out instead
				return sourceChange.withNewValue(isEmpty(oldObject) ? null : oldObject.toString());
			} catch (JsonProcessingException e) {
				throw new IndexException("Couldn't parse json", e);
			}
		}
		
		private boolean isEmpty(JsonNode node) {
			if (node == null || node.isNull()) {
				return true;
			} else if (node.isContainerNode()) {
				for (JsonNode child : node) {
					if (!isEmpty(child)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		protected RevisionPropertyDiff handleCollectionConflict(DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper, boolean isSet) {
			try {
				ArrayNode oldArray = CompareUtils.isEmpty(sourceChange.getOldValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(sourceChange.getOldValue());
				ArrayNode sourceArray = CompareUtils.isEmpty(sourceChange.getNewValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(sourceChange.getNewValue());
				ArrayNode targetArray = CompareUtils.isEmpty(targetChange.getNewValue()) ? mapper.createArrayNode() : (ArrayNode) mapper.readTree(targetChange.getNewValue());
				
				ArrayDiff sourceDiff = ArrayDiff.diff(oldArray, sourceArray);
				ArrayDiff targetDiff = ArrayDiff.diff(oldArray, targetArray);
				
				// TODO nested item conflict resolution???
				// create a big union of all changes, treat collection as set if needed
				// this eliminates most of the unnecessary conflicts between two collection properties
				Set<JsonNode> oldArraySet = Sets.newHashSet(oldArray);
				Streams.concat(sourceDiff.getAddedItems().stream(), targetDiff.getAddedItems().stream())
					.forEach(newItem -> {
						if (!isSet || oldArraySet.add(newItem)) {
							oldArray.add(newItem);
						}
					});

				Iterator<JsonNode> oldItems = oldArray.iterator();
				while (oldItems.hasNext()) {
					JsonNode oldItem = oldItems.next();
					if (sourceDiff.getRemovedItems().contains(oldItem) || targetDiff.getRemovedItems().contains(oldItem)) {
						oldItems.remove();
					}
				}

				return sourceChange.withNewValue(oldArray.toString());
			} catch (JsonProcessingException e) {
				throw new IndexException("Couldn't parse json", e);
			}
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
