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
package com.b2international.index.revision;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.Pair;
import com.b2international.index.BulkUpdate;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Hooks.Hook;
import com.b2international.index.revision.Hooks.PostCommitHook;
import com.b2international.index.revision.Hooks.PreCommitHook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * A place that stores information about what will go into your next commit.
 * 
 * @since 7.0
 * @see RevisionIndex#prepareCommit()
 */
public final class StagingArea {

	private static final EnumSet<DiffFlags> DIFF_FLAGS = EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE, DiffFlags.OMIT_COPY_OPERATION, DiffFlags.OMIT_MOVE_OPERATION);
	
	private final DefaultRevisionIndex index;
	private final String branchPath;
	private final ObjectMapper mapper;

	private Map<String, Object> newObjects;
	private Map<String, Object> changedObjects;
	private Map<String, RevisionDiff> changedRevisions;
	private Map<String, Object> removedObjects;

	private SortedSet<RevisionBranchPoint> mergeSources;
	private RevisionBranchRef mergeFromBranchRef;
	private boolean squashMerge;
	private Multimap<Class<?>, String> revisionsToReviseOnMergeSource;
	
	StagingArea(DefaultRevisionIndex index, String branchPath, ObjectMapper mapper) {
		this.index = index;
		this.branchPath = branchPath;
		this.mapper = mapper;
		reset();
	}
	
	public RevisionIndex getIndex() {
		return index;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	/**
	 * Reads from the underlying index using the branch where this {@link StagingArea} has been opened.
	 * @param read
	 * @return
	 */
	public <T> T read(RevisionIndexRead<T> read) {
		return index.read(branchPath, read);
	}
	
	public boolean isNew(Revision revision) {
		return newObjects.containsKey(revision.getId());
	}
	
	public boolean isChanged(Revision revision) {
		return changedObjects.containsKey(revision.getId()) || changedRevisions.containsKey(revision.getId());
	}
	
	public boolean isRemoved(Revision revision) {
		return removedObjects.containsKey(revision.getId());
	}
	
	public Map<String, Object> getNewObjects() {
		return newObjects;
	}
	
	public <T> Stream<T> getNewObjects(Class<T> type) {
		return newObjects.values().stream().filter(type::isInstance).map(type::cast);
	}
	
	public Map<String, Object> getChangedObjects() {
		return changedObjects;
	}
	
	public <T> Stream<T> getChangedObjects(Class<T> type) {
		return changedObjects.values().stream().filter(type::isInstance).map(type::cast);
	}
	
	public Map<String, Object> getRemovedObjects() {
		return removedObjects;
	}
	
	public <T> Stream<T> getRemovedObjects(Class<T> type) {
		return removedObjects.values().stream().filter(type::isInstance).map(type::cast);
	}
	
	public Map<String, RevisionDiff> getChangedRevisions() {
		return changedRevisions;
	}
	
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type) {
		return changedRevisions.values().stream().filter(diff -> type.isAssignableFrom(diff.newRevision.getClass()));
	}
	
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type, Set<String> changedPropertyNames) {
		return changedRevisions.values()
				.stream()
				.filter(diff -> type.isAssignableFrom(diff.newRevision.getClass()))
				.filter(diff -> changedPropertyNames.stream().filter(diff::hasRevisionPropertyDiff).findFirst().isPresent());
	}
	
	/**
	 * Commits the changes so far staged to the staging area.
	 *  
	 * @param timestamp
	 * @param author
	 * @param commitComment
	 * @return
	 */
	public Commit commit(long timestamp, String author, String commitComment) {
		return commit(null, timestamp, author, commitComment);
	}
	
	/**
	 * Commits the changes so far staged to the staging area. Runs any commit {@link Hook}s registered  
	 *  
	 * @param commitGroupId - can be used to connect multiple commits and consider them as a single commit
	 * @param timestamp - long timestamp when the commit happened
	 * @param author - the author of the changes
	 * @param commitComment - short text about the changes
	 * @return
	 */
	public Commit commit(String commitGroupId, long timestamp, String author, String commitComment) {
		// run pre-commit hooks
		final List<Hook> hooks = index.getHooks(); // get a snapshot of the current hooks so we use the same hooks before and after commit
		hooks.stream()
			.filter(PreCommitHook.class::isInstance)
			.map(PreCommitHook.class::cast)
			.forEach(hook -> hook.run(this));
		
		// commit the registered changes
		final Commit commit = index.write(branchPath, timestamp, writer -> doCommit(commitGroupId, timestamp, author, commitComment, writer));
		
		// run post-commit hooks
		hooks.stream()
			.filter(PostCommitHook.class::isInstance)
			.map(PostCommitHook.class::cast)
			.forEach(hook -> hook.run(commit));
		
		return commit;
	}

	private Commit doCommit(String commitGroupId, long timestamp, String author, String commitComment, RevisionWriter writer) throws IOException {
		Commit.Builder commit = Commit.builder();

		final Multimap<ObjectId, ObjectId> newComponentsByContainer = HashMultimap.create();
		final Multimap<ObjectId, ObjectId> changedComponentsByContainer = HashMultimap.create();
		final Multimap<ObjectId, ObjectId> removedComponentsByContainer = HashMultimap.create();
		final Multimap<Class<?>, String> deletedIdsByType = HashMultimap.create();
		
		removedObjects.forEach((key, value) -> {
			deletedIdsByType.put(value.getClass(), key);
			if (value instanceof Revision) {
				Revision rev = (Revision) value;
				removedComponentsByContainer.put(rev.getContainerId(), rev.getObjectId());
			}
		});

		// apply removals first
		for (Class<?> type : deletedIdsByType.keySet()) {
			final Set<String> deletedDocIds = ImmutableSet.copyOf(deletedIdsByType.get(type));
			writer.remove(type, deletedDocIds);
			if (shouldSetRevisedOnMergeBranch()) {
				revisionsToReviseOnMergeSource.putAll(type, deletedDocIds);
			}
		}
		
		// then new documents and revisions
		for (Entry<String, Object> doc : newObjects.entrySet()) {
			if (!removedObjects.containsKey(doc.getKey())) {
				Object document = doc.getValue();
				writer.put(doc.getKey(), document);
				if (document instanceof Revision) {
					Revision rev = (Revision) document;
					newComponentsByContainer.put(checkNotNull(rev.getContainerId(), "Missing containerId for revision: %s", rev), rev.getObjectId());
					if (shouldSetRevisedOnMergeBranch()) {
						revisionsToReviseOnMergeSource.put(document.getClass(), doc.getKey());
					}
				}
			}
		}
		
		// and changed documents
		for (Entry<String, Object> doc : changedObjects.entrySet()) {
			if (!removedObjects.containsKey(doc.getKey())) {
				Object document = doc.getValue();
				writer.put(doc.getKey(), document);
			}
		}
		
		final Multimap<ObjectNode, ObjectId> revisionsByChange = HashMultimap.create();
		
		// and changed revisions
		for (Entry<String, RevisionDiff> changedRevision : changedRevisions.entrySet()) {
			final String changedRevisionId = changedRevision.getKey();
			if (!removedObjects.containsKey(changedRevisionId)) {
				RevisionDiff revisionDiff = changedRevision.getValue();
				final Revision rev = revisionDiff.newRevision;
				// XXX temporal coupling between writer.put() and revisionDiff.diff(mapper) call
				// first put the new revision into the writer so that created and revised fields will get their values properly
				// then call the diff method to calculate the diff and serialize the new node into a JsonNode with _changes and created, revised fields
				writer.put(changedRevisionId, rev);
				if (shouldSetRevisedOnMergeBranch()) {
					revisionsToReviseOnMergeSource.put(rev.getClass(), rev.getId());
				}
				ObjectId containerId = checkNotNull(rev.getContainerId(), "Missing containerId for revision: %s", rev);
				ObjectId objectId = rev.getObjectId();
				if (!containerId.isRoot()) { // XXX register only sub-components in the changed objects
					changedComponentsByContainer.put(containerId, objectId);
				}
				revisionDiff.diff().forEach(node -> {
					if (node instanceof ObjectNode) {
						revisionsByChange.put((ObjectNode) node, objectId);
					}
				});
			}
		}
		
		// apply revised flag on merge source branch
		for (Class<?> type : revisionsToReviseOnMergeSource.keySet()) {
			writer.setRevised(type, ImmutableSet.copyOf(revisionsToReviseOnMergeSource.get(type)), mergeFromBranchRef);
		}
		
		final List<CommitDetail> details = newArrayList();
		
		// collect property changes
		revisionsByChange.asMap().forEach((change, objects) -> {
			final String prop = change.get("path").asText().substring(1); // XXX removes the forward slash from the beginning
			final String from = change.get("fromValue").asText();
			final String to = change.get("value").asText();
			Multimap<String, String> objectIdsByType = HashMultimap.create();
			objects.forEach(objectId -> objectIdsByType.put(objectId.type(), objectId.id()));
			// split by object type
			objectIdsByType.keySet().forEach(type -> {
				details.add(CommitDetail.changedProperty(prop, from, to, type, objectIdsByType.get(type)));
			});
		});

		final List<Pair<Multimap<ObjectId, ObjectId>, BiFunction<String, String, CommitDetail.Builder>>> maps = ImmutableList.of(
			Pair.of(newComponentsByContainer, CommitDetail::added),
			Pair.of(changedComponentsByContainer, CommitDetail::changed),
			Pair.of(removedComponentsByContainer, CommitDetail::removed)
		);
		for (Pair<Multimap<ObjectId, ObjectId>, BiFunction<String, String, CommitDetail.Builder>> entry : maps) {
			final Multimap<ObjectId, ObjectId> multimap = entry.getA();
			if (!multimap.isEmpty()) {
				BiFunction<String, String, CommitDetail.Builder> builderFactory = entry.getB();
				Map<Pair<String, String>, CommitDetail.Builder> buildersByRelationship = newHashMap();
				// collect hierarchical changes and register them by container ID
				multimap.asMap().forEach((container, components) -> {
					Multimap<String, String> componentsByType = HashMultimap.create();
					components.forEach(c -> componentsByType.put(c.type(), c.id()));
					componentsByType.asMap().forEach((componentType, componentIds) -> {
						final Pair<String, String> typeKey = Pair.identicalPairOf(container.type(), componentType);
						if (!buildersByRelationship.containsKey(typeKey)) {
							buildersByRelationship.put(typeKey, builderFactory.apply(typeKey.getA(), typeKey.getB()));
						}
						buildersByRelationship.get(typeKey).putObjects(container.id(), componentIds);
					});
				});
				buildersByRelationship.values()
					.stream()
					.map(CommitDetail.Builder::build)
					.forEach(details::add);
			}
		}
		
		// free up memory before committing 
		reset();
		newComponentsByContainer.clear();
		changedComponentsByContainer.clear();
		removedComponentsByContainer.clear();
		deletedIdsByType.clear();
		
		// generate a commit entry that marks the end of the commit and contains all changes in a details property
		Commit commitDoc = commit
				.id(UUID.randomUUID().toString())
				.groupId(commitGroupId)
				.author(author)
				.branch(branchPath)
				.comment(commitComment)
				.timestamp(timestamp)
				.details(details)
				.mergeSource(mergeSources != null && !mergeSources.isEmpty() ? mergeSources.last() : null)
				.build();
		writer.put(commitDoc.getId(), commitDoc);
		
		// update branch document(s)
		Map<String, Object> toBranchUpdateParams;
		if (mergeSources != null && !mergeSources.isEmpty()) {
			toBranchUpdateParams = ImmutableMap.<String, Object>of(
				"headTimestamp", timestamp,
				"mergeSources", mergeSources.stream().map(RevisionBranchPoint::toIpAddress).collect(Collectors.toCollection(TreeSet::new)),
				"squash", squashMerge
			);
		} else {
			toBranchUpdateParams = ImmutableMap.of("headTimestamp", timestamp); 
		}
		
		writer.bulkUpdate(
			new BulkUpdate<>(
				RevisionBranch.class, 
				DocumentMapping.matchId(branchPath), 
				DocumentMapping._ID, 
				RevisionBranch.Scripts.COMMIT,
				toBranchUpdateParams
			)
		);
		
		writer.commit();

		// clear remaining state
		mergeSources = null;
		
		return commitDoc;
	}

	private boolean shouldSetRevisedOnMergeBranch() {
		return mergeFromBranchRef != null && squashMerge;
	}

	/**
	 * Reset staging area to empty.
	 */
	private void reset() {
		newObjects = newHashMap();
		changedObjects = newHashMap();
		changedRevisions = newHashMap();
		removedObjects = newHashMap();
		revisionsToReviseOnMergeSource = HashMultimap.create();
	}

	public StagingArea stageNew(Revision newRevision) {
		return stageNew(newRevision.getId(), newRevision);
	}
	
	public StagingArea stageNew(String key, Object newDocument) {
		newObjects.put(key, newDocument);
		return this;
	}
	
	public StagingArea stageChange(Revision oldRevision, Revision changedRevision) {
		checkArgument(Objects.equals(oldRevision.getId(), changedRevision.getId()), "IDs of oldRevision and changedRevision must match");
		changedRevisions.put(changedRevision.getId(), new RevisionDiff(oldRevision, changedRevision));
		return this;
	}
	
	public StagingArea stageChange(String key, Object changed) {
		checkArgument(!(changed instanceof Revision), "Use the other stageChange method properly track changes for revision documents.");
		changedObjects.put(key, changed);
		return this;
	}
	
	public StagingArea stageRemove(Revision removedRevision) {
		return stageRemove(removedRevision.getId(), removedRevision);
	}

	public StagingArea stageRemove(String key, Object removed) {
		removedObjects.put(key, removed);
		return this;
	}
	
	public final class RevisionDiff {
		
		public final Revision oldRevision;
		public final Revision newRevision;
		
		private ArrayNode changes;
		private Map<String, RevisionPropertyDiff> propertyChanges;
		
		private RevisionDiff(Revision oldRevision, Revision newRevision) {
			this.oldRevision = oldRevision;
			this.newRevision = newRevision;
		}

		public ArrayNode diff() {
			if (changes == null) {
				final DocumentMapping mapping = index.admin().mappings().getMapping(newRevision.getClass());
				final Set<String> diffFields = mapping.getHashedFields();
				if (diffFields.isEmpty()) {
					return null; // in case of no hash fields, do NOT try to compute the diff
				}
				ObjectNode oldRevisionSource = mapper.valueToTree(oldRevision);
				ObjectNode newRevisionSource = mapper.valueToTree(newRevision);
				final JsonNode diff = JsonDiff.asJson(oldRevisionSource, newRevisionSource, DIFF_FLAGS);
				// remove revision specific fields from diff
				final ArrayNode diffNode = ClassUtils.checkAndCast(diff, ArrayNode.class);
				final ArrayNode changes = mapper.createArrayNode();
				final Iterator<JsonNode> elements = diffNode.elements();
				while (elements.hasNext()) {
					JsonNode node = elements.next();
					final ObjectNode change = ClassUtils.checkAndCast(node, ObjectNode.class);
					final String property = change.get("path").asText().substring(1);
					if (diffFields.contains(property)) {
						changes.add(change);
					}
				}
				this.changes = changes;
			}
			return this.changes;
		}

		public RevisionPropertyDiff getRevisionPropertyDiff(String property) {
			if (propertyChanges == null) {
				propertyChanges = newHashMapWithExpectedSize(2);
			}
			for (ObjectNode change : Iterables.filter(diff(), ObjectNode.class)) {
				String prop = change.get("path").asText().substring(1);
				if (property.equals(prop)) {
					final String from = change.get("fromValue").asText();
					final String to = change.get("value").asText();
					propertyChanges.put(property, new RevisionPropertyDiff(property, from, to));
				}
			}
			return propertyChanges.get(property);
		}
		
		public boolean hasRevisionPropertyDiff(String property) {
			return getRevisionPropertyDiff(property) != null;
		}
		
	}
	
	public static final class RevisionPropertyDiff {
		
		private final String property;
		private final String oldValue;
		private final String newValue;
		
		private RevisionPropertyDiff(String property, String oldValue, String newValue) {
			this.property = property;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		public String getProperty() {
			return property;
		}
		
		public String getOldValue() {
			return oldValue;
		}
		
		public String getNewValue() {
			return newValue;
		}

		public String toValueChangeString() {
			return String.format("%s -> %s", getOldValue(), getNewValue());
		}
		
		public RevisionPropertyDiff convert(RevisionConflictProcessor processor) {
			return new RevisionPropertyDiff(property, processor.convertPropertyValue(property, oldValue), processor.convertPropertyValue(property, newValue));
		}
		
	}

	long merge(RevisionBranchRef fromRef, RevisionBranchRef toRef, boolean squash, RevisionConflictProcessor conflictProcessor) {
		checkArgument(this.mergeSources == null, "Already merged another ref to this StagingArea. Commit staged changes to apply them.");
		this.mergeSources = fromRef.difference(toRef)
				.segments()
				.stream()
				.filter(segment -> segment.branchId() != toRef.branchId())
				.map(RevisionSegment::getEndPoint)
				.collect(Collectors.toCollection(TreeSet::new));
		this.mergeFromBranchRef = fromRef;
		this.squashMerge = squash;
		
		final RevisionCompare fromChanges = index.compare(toRef, fromRef, Integer.MAX_VALUE);
		
		final List<RevisionCompareDetail> fromChangeDetails = fromChanges.getDetails();
		
		final long fastForwardCommitTimestamp = fromChanges.getCompare().segments().last().end();
		
		// in case of nothing to merge, then just commit the headtimestamp change, similar to fast forward in Git
		if (fromChangeDetails.isEmpty()) {
			return squash ? -1L : fastForwardCommitTimestamp;
		}
		
		final RevisionCompare toChanges = index.compare(fromRef, toRef, Integer.MAX_VALUE);
		// check conflicts and commit only the resolved conflicts
		final List<RevisionCompareDetail> toChangeDetails = toChanges.getDetails();
		
		// in case of fast-forward merge only check conflicts when there are changes on the to branch
		if (toChangeDetails.isEmpty() && !squash) {
			return fastForwardCommitTimestamp;
		}
		
		final RevisionBranchChangeSet fromChangeSet = new RevisionBranchChangeSet(index, fromRef, fromChanges);
		final RevisionBranchChangeSet toChangeSet = new RevisionBranchChangeSet(index, toRef, toChanges);
		
		List<Conflict> conflicts = newArrayList();
		
		for (Class<? extends Revision> type : Iterables.concat(fromChangeSet.getAddedTypes(), toChangeSet.getAddedTypes())) {
			final String docType = DocumentMapping.getType(type);
			final Set<String> newRevisionIdsOnSource = fromChangeSet.getAddedIds(type);
			final Set<String> newRevisionIdsOnTarget = toChangeSet.getAddedIds(type);
			final Set<String> addedInSourceAndTarget = Sets.intersection(newRevisionIdsOnSource, newRevisionIdsOnTarget);
			// check for added in both source and target conflicts
			if (!addedInSourceAndTarget.isEmpty()) {
				addedInSourceAndTarget.forEach(revisionId -> {
					conflicts.add(new AddedInSourceAndTargetConflict(ObjectId.of(docType, revisionId)));
				});
			}
			// check deleted containers on target and report them as conflicts
			newRevisionIdsOnSource.forEach(newRevisionOnSource -> {
				ObjectId newRevisionOnSourceId = ObjectId.of(docType, newRevisionOnSource);
				ObjectId requiredContainer = fromChangeSet.getContainerId(newRevisionOnSourceId);
				if (requiredContainer != null && toChangeSet.isRemoved(requiredContainer)) {
					conflicts.add(new AddedInSourceAndDetachedInTargetConflict(newRevisionOnSourceId, requiredContainer));
				}
			});
			
			// check deleted containers on source and report them as conflicts
			newRevisionIdsOnTarget.forEach(newRevisionOnTarget -> {
				ObjectId newRevisionOnTargetId = ObjectId.of(docType, newRevisionOnTarget);
				ObjectId requiredContainer = toChangeSet.getContainerId(newRevisionOnTargetId);
				if (requiredContainer != null && fromChangeSet.isRemoved(requiredContainer)) {
					conflicts.add(new AddedInTargetAndDetachedInSourceConflict(requiredContainer, newRevisionOnTargetId));
				}
			});
		}
		
		// check property conflicts
		final Map<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> propertyUpdatesToApply = newHashMap();
		
		Set<String> changedRevisionIdsToCheck = newHashSet(toChangeSet.getChangedIds());
		Set<String> removedRevisionIdsToCheck = newHashSet(toChangeSet.getRemovedIds());
		for (Class<? extends Revision> type : fromChangeSet.getChangedTypes()) {
			final String docType = DocumentMapping.getType(type);
			Set<String> changedRevisionIdsToMerge = newHashSet(fromChangeSet.getChangedIds(type));
			// first handle changed vs. removed
			Set<String> changedInSourceDetachedInTargetIds = Sets.intersection(changedRevisionIdsToMerge, removedRevisionIdsToCheck);
			if (!changedInSourceDetachedInTargetIds.isEmpty()) {
				// report any conflicts
				changedInSourceDetachedInTargetIds.forEach(changedInSourceDetachedInTargetId -> {
					List<RevisionPropertyDiff> sourceChanges = fromChangeDetails.stream()
							.filter(detail -> detail.getObject().id().equals(changedInSourceDetachedInTargetId))
							.filter(detail -> !detail.isComponentChange())
							.map(change -> new RevisionPropertyDiff(change.getProperty(), change.getFromValue(), change.getValue()))
							.collect(Collectors.toList());
					Conflict conflict = conflictProcessor.handleChangedInSourceDetachedInTarget(ObjectId.of(docType, changedInSourceDetachedInTargetId), sourceChanges);
					if (conflict != null) {
						conflicts.add(conflict);
					}
				});
				// register them as revised on source from the target branch point of view
				revisionsToReviseOnMergeSource.putAll(type, changedInSourceDetachedInTargetIds);
				changedInSourceDetachedInTargetIds.forEach(id -> fromChangeSet.removeChanged(type, id));
				changedRevisionIdsToMerge.removeAll(changedInSourceDetachedInTargetIds);
			}
			// then handle changed vs. changed with the conflict processor
			Set<String> changedInSourceAndTargetIds = Sets.intersection(changedRevisionIdsToMerge, changedRevisionIdsToCheck);
			if (!changedInSourceAndTargetIds.isEmpty()) {
				for (String changedInSourceAndTargetId : changedInSourceAndTargetIds) {
					Map<String, RevisionCompareDetail> sourcePropertyChanges = fromChangeDetails.stream()
							.filter(detail -> detail.getObject().id().equals(changedInSourceAndTargetId))
							.filter(detail -> !detail.isComponentChange())
							.collect(Collectors.toMap(RevisionCompareDetail::getProperty, d -> d));
					Map<String, RevisionCompareDetail> targetPropertyChanges = toChangeDetails.stream()
							.filter(detail -> detail.getObject().id().equals(changedInSourceAndTargetId))
							.filter(detail -> !detail.isComponentChange())
							.collect(Collectors.toMap(RevisionCompareDetail::getProperty, d -> d));
					
					for (Entry<String, RevisionCompareDetail> sourceChange : Iterables.consumingIterable(sourcePropertyChanges.entrySet())) {
						final RevisionPropertyDiff sourceChangeDiff = new RevisionPropertyDiff(sourceChange.getValue().getProperty(), sourceChange.getValue().getFromValue(), sourceChange.getValue().getValue());
						final RevisionCompareDetail targetPropertyChange = targetPropertyChanges.remove(sourceChange.getKey());
						if (targetPropertyChange == null) {
							// this property did not change in target, just apply directly on the target object via
							if (!propertyUpdatesToApply.containsKey(type)) {
								propertyUpdatesToApply.put(type, HashMultimap.create());
							}
							propertyUpdatesToApply.get(type).put(changedInSourceAndTargetId, sourceChangeDiff);
							fromChangeSet.removeChanged(type, changedInSourceAndTargetId);
						} else {
							RevisionPropertyDiff targetChangeDiff = new RevisionPropertyDiff(targetPropertyChange.getProperty(), targetPropertyChange.getFromValue(), targetPropertyChange.getValue());
							// changed on both sides, ask conflict processor to resolve the issue or raise conflict error
							RevisionPropertyDiff resolution = conflictProcessor.handleChangedInSourceAndTarget(
								changedInSourceAndTargetId, 
								sourceChangeDiff,
								targetChangeDiff
							);
							if (resolution == null) {
								conflicts.add(new ChangedInSourceAndTargetConflict(sourceChange.getValue().getObject(), sourceChangeDiff.convert(conflictProcessor), targetChangeDiff.convert(conflictProcessor)));
							} else {
								if (!propertyUpdatesToApply.containsKey(type)) {
									propertyUpdatesToApply.put(type, HashMultimap.create());
								}
								propertyUpdatesToApply.get(type).put(changedInSourceAndTargetId, resolution);
							}
							fromChangeSet.removeChanged(type, changedInSourceAndTargetId);
						}
					}
				}
			}
		}
		
		// after generic conflict processing execute domain specific merge rules via conflict processor
		conflictProcessor.checkConflicts(this, fromChangeSet, toChangeSet).forEach(conflicts::add);
		
		if (!conflicts.isEmpty()) {
			throw new BranchMergeConflictException(conflicts.stream().map(conflictProcessor::convertConflict).collect(Collectors.toList()));
		}
		
		boolean stagedChanges = false;
		// apply property changes, conflicts, etc.
		if (!propertyUpdatesToApply.isEmpty()) {
			// if there are property conflict resolutions, then we have staged changes and it does not matter if the merge is fast-forward
			for (Entry<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> entry : propertyUpdatesToApply.entrySet()) {
				final Class<? extends Revision> type = entry.getKey();
				final Multimap<String, RevisionPropertyDiff> propertyUpdatesByObject = entry.getValue();
				final DocumentMapping mapping = index.admin().mappings().getMapping(type);
				final Iterable<? extends Revision> objectsToUpdate = index.read(toRef, searcher -> searcher.get(type, propertyUpdatesByObject.keySet()));
				for (Revision objectToUpdate : objectsToUpdate) {
					stageChange(objectToUpdate, objectToUpdate.withUpdates(mapping, propertyUpdatesByObject.get(objectToUpdate.getId())));
					stagedChanges = true;
					revisionsToReviseOnMergeSource.put(type, objectToUpdate.getId());
				}
			}
		}
		
		if (squash) {
			// apply new objects
			for (Class<? extends Revision> type : fromChangeSet.getAddedTypes()) {
				final Collection<String> newRevisionIds = fromChangeSet.getAddedIds(type);
				index.read(fromRef, searcher -> searcher.get(type, newRevisionIds)).forEach(this::stageNew);
				stagedChanges = true;
			}
			
			// apply changed objects
			for (Class<? extends Revision> type : fromChangeSet.getChangedTypes()) {
				final Collection<String> changedRevisionIds = fromChangeSet.getChangedIds(type);
				final Iterable<? extends Revision> oldRevisions = index.read(toRef, searcher -> searcher.get(type, changedRevisionIds));
				final Map<String, ? extends Revision> oldRevisionsById = FluentIterable.from(oldRevisions).uniqueIndex(Revision::getId);
				
				final Iterable<? extends Revision> updatedRevisions = index.read(fromRef, searcher -> searcher.get(type, changedRevisionIds));
				final Map<String, ? extends Revision> updatedRevisionsById = FluentIterable.from(updatedRevisions).uniqueIndex(Revision::getId);
				for (String updatedId : updatedRevisionsById.keySet()) {
					if (oldRevisionsById.containsKey(updatedId)) {
						stageChange(oldRevisionsById.get(updatedId), updatedRevisionsById.get(updatedId));
					} else {
						stageNew(updatedRevisionsById.get(updatedId));
					}
					stagedChanges = true;
				}
			}
		}
		
		// always apply deleted objects, they set the revised timestamp properly without introducing any new document
		for (Class<? extends Revision> type : fromChangeSet.getRemovedTypes()) {
			final Collection<String> removedRevisionIds = fromChangeSet.getRemovedIds(type);
			index.read(toRef, searcher -> searcher.get(type, removedRevisionIds)).forEach(this::stageRemove);
			stagedChanges = true;
		}
		
		return stagedChanges ? -1L : fastForwardCommitTimestamp;
	}

}
