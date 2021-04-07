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
import com.b2international.index.util.ArrayDiff.ArrayItemDiff;
import com.b2international.index.util.JsonDiff;
import com.b2international.index.util.JsonDiff.JsonChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
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
				
				// report conflict if any change differs in any way on any property
				if (hasConflictingField(sourceDiff, targetDiff)) {
					return null;
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

		private boolean hasConflictingField(JsonDiff sourceDiff, JsonDiff targetDiff) {
			Map<String, JsonChange> sourceChanges = Maps.uniqueIndex(sourceDiff.getChanges(), JsonChange::getRootFieldPath);
			Map<String, JsonChange> targetChanges = Maps.uniqueIndex(targetDiff.getChanges(), JsonChange::getRootFieldPath);
			
			return Sets.intersection(sourceChanges.keySet(), targetChanges.keySet())
				.stream()
				.filter(conflictingField -> {
					JsonChange sourceJsonChange = sourceChanges.get(conflictingField);
					JsonChange targetJsonChange = targetChanges.get(conflictingField);
					return !Objects.equals(sourceJsonChange, targetJsonChange);
				})
				.findAny()
				.isPresent();
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
				
				Function<JsonNode, String> idFunction = null; 
				// if nested and has a valid ID field then use that to identify same objects in a list
				if (mapping.isNestedMapping(sourceChange.getProperty())) {
					DocumentMapping nestedMapping = mapping.getNestedMapping(sourceChange.getProperty());
					String idField = nestedMapping.getIdField();
					if (!DocumentMapping._ID.equals(idField)) {
						idFunction = item -> item.get(idField).toString();
					}
				}
				
				ArrayDiff sourceDiff = ArrayDiff.diff(oldArray, sourceArray, idFunction);
				ArrayDiff targetDiff = ArrayDiff.diff(oldArray, targetArray, idFunction);
				
				// first apply changed objects and check potential conflicts
				for (String changedId : Sets.union(sourceDiff.getChangedItemsById().keySet(), targetDiff.getChangedItemsById().keySet())) {
					ArrayItemDiff sourceItemDiff = sourceDiff.getChangedItemsById().get(changedId);
					ArrayItemDiff targetItemDiff = targetDiff.getChangedItemsById().get(changedId);
					
					if (sourceItemDiff == null && targetItemDiff != null) {
						// target only change, apply directly on from value, which should be old object from old array
						targetItemDiff.diff().applyInPlace(targetItemDiff.getFromValue());
					} else if (sourceItemDiff != null && targetItemDiff == null) {
						// source only change, apply directly on from value, which should be old object from old array
						sourceItemDiff.diff().applyInPlace(sourceItemDiff.getFromValue());
					} else if (sourceItemDiff != null && targetItemDiff != null) {
						// changed on both sides
						if (hasConflictingField(sourceItemDiff.diff(), targetItemDiff.diff())) {
							return null; // report conflict for this tracked array property
						}
						// otherwise apply diff to the old object from the two diffs
						JsonNode oldObject = sourceItemDiff.getFromValue();
						
						// otherwise apply both changes on old object to merge them
						sourceItemDiff.diff().applyInPlace(oldObject);
						targetItemDiff.diff().applyInPlace(oldObject);
						
						// XXX no need to add the oldObject to any lists since it is already part of the oldArray, updated in-place
					} else {
						// should not happen, but if in a parallel universe it does, then nothing to do
					}
				}

				// create a big union of all changes, treat collection as set if needed
				// this eliminates most of the unnecessary conflicts between two collection properties
				if (idFunction != null) {
					// add new items with IDs using a Map to check if they have added twice and raise conflict if needed
					Map<String, JsonNode> newItemsById = Maps.newHashMap();
					for (JsonNode newItem : Iterables.concat(sourceDiff.getAddedItems(), targetDiff.getAddedItems())) {
						final String id = idFunction.apply(newItem);
						// if the item is already present check the value added by already and raise conflict if it is not the same
						if (newItemsById.containsKey(id)) {
							if (newItem.equals(newItemsById.get(id))) {
								continue; // skip, same value already present
							} else {
								return null; // different value, report conflict
							}
						} else {
							newItemsById.put(id, newItem);
						}
						oldArray.add(newItem);
					}
				} else {
					// simply add new items without IDs using a backing Set
					Set<JsonNode> oldArraySet = Sets.newHashSet(oldArray);
					Streams.concat(sourceDiff.getAddedItems().stream(), targetDiff.getAddedItems().stream())
						.forEach(newItem -> {
							if (!isSet || oldArraySet.add(newItem)) {
								oldArray.add(newItem);
							}
						});
				}
				
				// lastly remove all items indicated by the diffs
				if (!sourceDiff.getRemovedItems().isEmpty() || !targetDiff.getRemovedItems().isEmpty()) {
					Iterator<JsonNode> oldItems = oldArray.iterator();
					while (oldItems.hasNext()) {
						JsonNode oldItem = oldItems.next();
						if (sourceDiff.getRemovedItems().contains(oldItem) || targetDiff.getRemovedItems().contains(oldItem)) {
							oldItems.remove();
						}
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
