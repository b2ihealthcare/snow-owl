/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.index.BulkUpdate;
import com.b2international.index.IndexException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Hooks.Hook;
import com.b2international.index.revision.Hooks.PostCommitHook;
import com.b2international.index.revision.Hooks.PreCommitHook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.google.common.collect.*;

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

	private Map<ObjectId, StagedObject> stagedObjects;
	private Set<String> exclusions;

	private SortedSet<RevisionBranchPoint> mergeSources;
	private RevisionBranchRef mergeFromBranchRef;
	private boolean squashMerge;
	private Multimap<Class<?>, String> revisionsToReviseOnMergeSource;
	private Object context;

	StagingArea(DefaultRevisionIndex index, String branchPath, ObjectMapper mapper) {
		this.index = index;
		this.branchPath = branchPath;
		this.mapper = mapper;
		this.exclusions = Collections.emptySet();
		reset();
	}
	
	public RevisionIndex getIndex() {
		return index;
	}
	
	public String getBranchPath() {
		return branchPath;
	}

	/**
	 * @param context - the context to set
	 * @return this class for method chaining
	 */
	public StagingArea withContext(Object context) {
		this.context = context;
		return this;
	}
	
	/**
	 * @return a context object passed during {@link RevisionIndex#prepareCommit(String, Object)}
	 */
	public Object getContext() {
		return context;
	}
	
	/**
	 * Reads from the underlying index using the branch where this {@link StagingArea} has been opened.
	 * @param read
	 * @return
	 */
	public <T> T read(RevisionIndexRead<T> read) {
		return index.read(branchPath, read);
	}
	
	/**
	 * @param revision
	 * @return <code>true</code> if the given revision is staged in this {@link StagingArea}
	 */
	public boolean isStaged(Revision revision) {
		return stagedObjects.containsKey(revision.getObjectId());
	}
	
	public boolean isNew(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isAdded();
	}
	
	public boolean isChanged(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isChanged();
	}
	
	public boolean isRemoved(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isRemoved();
	}
	
	public Stream<Object> getNewObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isAdded())
				.map(e -> e.getValue().getObject());
	}
	
	public <T> T getNewObject(Class<T> type, String key) {
		StagedObject stagedObject = stagedObjects.get(ObjectId.of(type, key));
		return stagedObject == null ? null : type.cast(stagedObject.getObject());
	}
	
	public <T> Stream<T> getNewObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isAdded())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}
	
	public Stream<Object> getChangedObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(e -> e.getValue().getObject());
	}
	
	public <T> Stream<T> getChangedObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}
	
	public Stream<Object> getRemovedObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isRemoved())
				.map(e -> e.getValue().getObject());
	}
	
	public <T> Stream<T> getRemovedObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isRemoved())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}
	
	public Map<ObjectId, RevisionDiff> getChangedRevisions() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getDiff()));
	}
	
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getDiff())
				.filter(diff -> type.isAssignableFrom(diff.newRevision.getClass()));
	}
	
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type, Set<String> changedPropertyNames) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getDiff())
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

		getFilteredStagedObjects().entrySet().forEach( entry -> {
			ObjectId key = entry.getKey();
			StagedObject value = entry.getValue();
			
			if (value.isRemoved() && value.isCommit()) {
				Object object = value.getObject();
				deletedIdsByType.put(object.getClass(), key.id());
				if (object instanceof Revision) {
					Revision rev = (Revision) object;
					removedComponentsByContainer.put(rev.getContainerId(), key);
				} else {
					removedComponentsByContainer.put(ObjectId.rootOf(DocumentMapping.getType(object.getClass())), key);
				}
			}
		});

		// apply removals first
		for (Class<?> type : deletedIdsByType.keySet()) {
			final Set<String> deletedDocIds = ImmutableSet.copyOf(deletedIdsByType.get(type));
			writer.remove(type, deletedDocIds);
			if (isMerge()) {
				revisionsToReviseOnMergeSource.putAll(type, deletedDocIds);
			}
		}
		
		// then new documents and revisions
		getFilteredStagedObjects().entrySet().forEach( entry -> {
			ObjectId key = entry.getKey();
			StagedObject value = entry.getValue();
			if (value.isAdded() && value.isCommit()) {
				Object document = value.getObject();
				writer.put(key.id(), document);
				if (document instanceof Revision) {
					Revision rev = (Revision) document;
					newComponentsByContainer.put(checkNotNull(rev.getContainerId(), "Missing containerId for revision: %s", rev), rev.getObjectId());
					if (isMerge()) {
						revisionsToReviseOnMergeSource.put(document.getClass(), key.id());
					}
				} else {
					newComponentsByContainer.put(ObjectId.rootOf(DocumentMapping.getType(document.getClass())), key);
				}
			}
		});
		
		// and changed documents/revisions
		final Multimap<ObjectNode, ObjectId> revisionsByChange = HashMultimap.create();
		getFilteredStagedObjects().entrySet().forEach( entry -> {
			ObjectId key = entry.getKey();
			StagedObject value = entry.getValue();
			if (value.isChanged() && value.isCommit()) {
				Object object = value.getObject();
				if (object instanceof Revision) {
					RevisionDiff revisionDiff = value.getDiff();
					final Revision rev = revisionDiff.newRevision;
					
					if (!revisionDiff.hasChanges()) {
						return;
					}
					
					if (isMerge()) {
						revisionsToReviseOnMergeSource.put(rev.getClass(), rev.getId());
					}
					
					writer.put(key.id(), rev);
					
					// register component as changed in commit doc
					ObjectId containerId = checkNotNull(rev.getContainerId(), "Missing containerId for revision: %s", rev);
					ObjectId objectId = rev.getObjectId();
					if (!containerId.isRoot()) { // XXX register only sub-components in the changed objects
						changedComponentsByContainer.put(containerId, objectId);
					}
					
					if (revisionDiff.diff() != null) {
						// register actual difference between revisions to commit
						revisionDiff.diff().forEach(node -> {
							if (node instanceof ObjectNode) {
								revisionsByChange.put((ObjectNode) node, objectId);
							}
						});
					}
					
				} else {
					writer.put(key.id(), object);
					changedComponentsByContainer.put(ObjectId.rootOf(DocumentMapping.getType(object.getClass())), key);
				}
			}
		});
		
		// apply revised flag on merge source branch
		for (Class<?> type : revisionsToReviseOnMergeSource.keySet()) {
			writer.setRevised(type, ImmutableSet.copyOf(revisionsToReviseOnMergeSource.get(type)), mergeFromBranchRef);
		}
		
		final List<CommitDetail> details = newArrayList();
		
		// collect property changes
		revisionsByChange.asMap().forEach((change, objects) -> {
			final String prop = change.get("path").asText().substring(1); // XXX removes the forward slash from the beginning
			final String from;
			if (change.has("fromValue")) {
				from = serializeToCommitDetailValue(change.get("fromValue"));
			} else  {
				from = "";
			}
			final String to = serializeToCommitDetailValue(change.get("value"));
			ListMultimap<String, String> objectIdsByType = ArrayListMultimap.create();
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
		
		// add non-revision components as new/changed/removed as well
		getFilteredStagedObjects().entrySet().forEach( entry -> {
			ObjectId key = entry.getKey();
			StagedObject value = entry.getValue();
			if (!(value.getObject() instanceof Revision)) {
				String componentType = key.type();
				switch (value.stageKind) {
				case ADDED:
					details.add(CommitDetail.added(componentType, componentType)
							.objects(ObjectId.ROOT)
							.components(Collections.singleton(key.id()))
							.build());
					break;
				case CHANGED:
					details.add(CommitDetail.changed(componentType, componentType)
							.objects(ObjectId.ROOT)
							.components(Collections.singleton(key.id()))
							.build());
					break;
				case REMOVED:
					details.add(CommitDetail.removed(componentType, componentType)
							.objects(ObjectId.ROOT)
							.components(Collections.singleton(key.id()))
							.build());
					break;
				}
			}
		});
		
		// free up memory before committing 
		reset();
		newComponentsByContainer.clear();
		changedComponentsByContainer.clear();
		removedComponentsByContainer.clear();
		deletedIdsByType.clear();
		
		// nothing to commit, break
		if (writer.isEmpty() && CompareUtils.isEmpty(mergeSources)) {
			return null;
		}
		
		// generate a commit entry that marks the end of the commit and contains all changes in a details property
		Commit commitDoc = commit
				.id(UUID.randomUUID().toString())
				.groupId(commitGroupId)
				.author(author)
				.branch(branchPath)
				.comment(commitComment)
				.timestamp(timestamp)
				.details(details)
				.mergeSource(!CompareUtils.isEmpty(mergeSources) ? mergeSources.last() : null)
				.squashMerge(!CompareUtils.isEmpty(mergeSources) ? squashMerge : null)
				.build();
		writer.put(commitDoc.getId(), commitDoc);
		
		// update branch document(s)
		ImmutableMap.Builder<String, Object> toBranchUpdateParams = ImmutableMap.builder();
		if (mergeSources != null && !mergeSources.isEmpty()) {
			toBranchUpdateParams.put("headTimestamp", timestamp);
			toBranchUpdateParams.put("mergeSources", mergeSources.stream().map(RevisionBranchPoint::toIpAddress).collect(Collectors.toList()));
			toBranchUpdateParams.put("squash", squashMerge);
		} else {
			toBranchUpdateParams.put("headTimestamp", timestamp); 
		}
		
		writer.bulkUpdate(
			new BulkUpdate<>(
				RevisionBranch.class, 
				DocumentMapping.matchId(branchPath), 
				DocumentMapping._ID, 
				RevisionBranch.Scripts.COMMIT,
				toBranchUpdateParams.build()
			)
		);
		
		writer.commit();

		// clear remaining state
		mergeSources = null;
		
		return commitDoc;
	}

	private String serializeToCommitDetailValue(JsonNode value) {
		if (value.isNull()) {
			return null;
		} else if (value.isArray()) {
			return value.toString();
		} else {
			return value.asText();
		}
	}

	/**
	 * Dirty staging area if at least one object has been staged.
	 * @return <code>true</code> if the staging area is dirty and can be committed via {@link #commit(String, long, String, String)}
	 */
	public boolean isDirty() {
		return !stagedObjects.isEmpty();
	}
	
	/**
	 * @return <code>true</code> if the staging area is merging content from another branch into the current branch
	 */
	public boolean isMerge() {
		return mergeFromBranchRef != null;
	}
	
	/**
	 * Reset staging area to empty.
	 */
	private void reset() {
		stagedObjects = newHashMap();
		exclusions = Sets.newHashSet();
		revisionsToReviseOnMergeSource = HashMultimap.create();
	}

	public StagingArea stageNew(Revision newRevision) {
		return stageNew(newRevision, true);
	}
	
	public StagingArea stageNew(Revision newRevision, boolean commit) {
		return stageNew(newRevision.getId(), newRevision, commit);
	}
	
	public StagingArea stageNew(String key, Object newDocument) {
		return stageNew(key, newDocument, true);
	}
	
	public StagingArea stageNew(String key, Object newDocument, boolean commit) {
		ObjectId objectId = toObjectId(newDocument, key);
		if (stagedObjects.containsKey(objectId)) {
			StagedObject currentStagedObject = stagedObjects.get(objectId);
			if (!currentStagedObject.isCommit() && currentStagedObject.getObject() instanceof Revision && newDocument instanceof Revision) {
				stagedObjects.put(objectId, changed(newDocument, new RevisionDiff((Revision) currentStagedObject.getObject(), (Revision) newDocument), commit));
			} else {
				stagedObjects.put(objectId, added(newDocument, null, commit));
			}
		} else {
			stagedObjects.put(objectId, added(newDocument, null, commit));
		}
		return this;
	}

	public StagingArea stageChange(Revision oldRevision, Revision changedRevision) {
		return stageChange(oldRevision, changedRevision, true);
	}
	
	public StagingArea stageChange(Revision oldRevision, Revision changedRevision, boolean commit) {
		checkArgument(Objects.equals(oldRevision.getId(), changedRevision.getId()), "IDs of oldRevision and changedRevision must match");
		ObjectId id = toObjectId(changedRevision, changedRevision.getId());
		if (stagedObjects.containsKey(id)) {
			StagedObject currentObject = stagedObjects.get(id);
			stagedObjects.put(id, currentObject.withObject(changedRevision, commit));
		} else {
			stagedObjects.put(id, changed(changedRevision, new RevisionDiff(oldRevision, changedRevision), commit));
		}
		return this;
	}
	
	public StagingArea stageChange(String key, Object changed) {
		checkArgument(!(changed instanceof Revision), "Use the other stageChange method properly track changes for revision documents.");
		ObjectId id = toObjectId(changed, key);
		if (stagedObjects.containsKey(id)) {
			stagedObjects.put(id, stagedObjects.get(id).withObject(changed, true));
		} else {
			stagedObjects.put(id, changed(changed, null, true));
		}
		return this;
	}
	
	public StagingArea stageRemove(Revision removedRevision) {
		return stageRemove(removedRevision, true);
	}
	
	public StagingArea stageRemove(Revision removedRevision, boolean commit) {
		return stageRemove(removedRevision.getId(), removedRevision, commit);
	}
	
	public StagingArea stageRemove(String key, Object removed) {
		return stageRemove(key, removed, true);
	}
	
	public StagingArea stageRemove(String key, Object removed, boolean commit) {
		stagedObjects.put(toObjectId(removed, key), removed(removed, null, commit));
		return this;
	}
	
	private ObjectId toObjectId(Object obj, String id) {
		if (obj instanceof Revision) {
			return ((Revision) obj).getObjectId();
		} else {
			return ObjectId.of(obj.getClass(), id);
		}
	}
	
	void merge(RevisionBranchRef fromRef, RevisionBranchRef toRef, boolean squash, RevisionConflictProcessor conflictProcessor, Set<String> exclusions) {
		checkArgument(this.mergeSources == null, "Already merged another ref to this StagingArea. Commit staged changes to apply them.");
		this.mergeFromBranchRef = fromRef.difference(toRef);
		this.mergeSources = this.mergeFromBranchRef
				.segments()
				.stream()
				.filter(segment -> segment.branchId() != toRef.branchId())
				.map(RevisionSegment::getEndPoint)
				.collect(Collectors.toCollection(TreeSet::new));
		this.squashMerge = squash;
		if (exclusions != null) {
			this.exclusions.addAll(exclusions);
		}
		
		final RevisionCompare fromChanges = index.compare(toRef, fromRef, Integer.MAX_VALUE);
		
		final List<RevisionCompareDetail> fromChangeDetails = fromChanges.getDetails();
		
		// in case of nothing to merge, then just proceed to commit
		if (fromChangeDetails.isEmpty()) {
			return;
		}
		
		final RevisionCompare toChanges = index.compare(fromRef, toRef, Integer.MAX_VALUE);
		// check conflicts and commit only the resolved conflicts
		final List<RevisionCompareDetail> toChangeDetails = toChanges.getDetails();
		
		// in case of fast-forward merge only check conflicts when there are changes on the to branch
		if (toChangeDetails.isEmpty() && !squash) {
			return;
		}
		
		final RevisionBranchChangeSet fromChangeSet = new RevisionBranchChangeSet(index, fromRef, fromChanges);
		final RevisionBranchChangeSet toChangeSet = new RevisionBranchChangeSet(index, toRef, toChanges);
		
		List<Conflict> conflicts = newArrayList();
		
		for (Class<? extends Revision> type : ImmutableSet.copyOf(Iterables.concat(fromChangeSet.getAddedTypes(), toChangeSet.getAddedTypes()))) {
			final Set<String> newRevisionIdsOnSource = fromChangeSet.getAddedIds(type);
			final Set<String> newRevisionIdsOnTarget = toChangeSet.getAddedIds(type);
			final Set<String> addedInSourceAndTarget = Sets.intersection(newRevisionIdsOnSource, newRevisionIdsOnTarget);
			// check for added in both source and target conflicts
			if (!addedInSourceAndTarget.isEmpty()) {
				addedInSourceAndTarget.forEach(revisionId -> {
					conflicts.add(new AddedInSourceAndTargetConflict(ObjectId.of(type, revisionId)));
				});
			}
			// check deleted containers on target and report them as conflicts
			newRevisionIdsOnSource.forEach(newRevisionOnSource -> {
				ObjectId newRevisionOnSourceId = ObjectId.of(type, newRevisionOnSource);
				ObjectId requiredContainer = fromChangeSet.getContainerId(newRevisionOnSourceId);
				if (requiredContainer != null && toChangeSet.isRemoved(requiredContainer)) {
					conflicts.add(new AddedInSourceAndDetachedInTargetConflict(newRevisionOnSourceId, requiredContainer));
				}
			});
			
			// check deleted containers on source and report them as conflicts
			newRevisionIdsOnTarget.forEach(newRevisionOnTarget -> {
				ObjectId newRevisionOnTargetId = ObjectId.of(type, newRevisionOnTarget);
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
			Set<String> changedInSourceDetachedInTargetIds = Sets.newHashSet(Sets.intersection(changedRevisionIdsToMerge, removedRevisionIdsToCheck));
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
				final Map<String, Map<String, RevisionCompareDetail>> sourcePropertyChangesByObject = indexPropertyChangesByObject(fromChangeDetails);
				final Map<String, Map<String, RevisionCompareDetail>> targetPropertyChangesByObject = indexPropertyChangesByObject(toChangeDetails);
				for (String changedInSourceAndTargetId : changedInSourceAndTargetIds) {
					// take the prop changes from both paths
					final Map<String, RevisionCompareDetail> sourcePropertyChanges = sourcePropertyChangesByObject.remove(changedInSourceAndTargetId);
					final Map<String, RevisionCompareDetail> targetPropertyChanges = targetPropertyChangesByObject.remove(changedInSourceAndTargetId);
					
					if (sourcePropertyChanges != null) {
						for (Entry<String, RevisionCompareDetail> sourceChange : sourcePropertyChanges.entrySet()) {
							final String changedProperty = sourceChange.getKey();
							final RevisionCompareDetail sourcePropertyChange = sourceChange.getValue();
							
							final RevisionPropertyDiff sourceChangeDiff = new RevisionPropertyDiff(changedProperty, sourcePropertyChange.getFromValue(), sourcePropertyChange.getValue());
							final RevisionCompareDetail targetPropertyChange = targetPropertyChanges == null ? null : targetPropertyChanges.get(changedProperty);
							
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
									conflicts.add(new ChangedInSourceAndTargetConflict(sourcePropertyChange.getObject(), sourceChangeDiff.convert(conflictProcessor), targetChangeDiff.convert(conflictProcessor)));
								} else {
									if (!propertyUpdatesToApply.containsKey(type)) {
										propertyUpdatesToApply.put(type, HashMultimap.create());
									}
									propertyUpdatesToApply.get(type).put(changedInSourceAndTargetId, resolution);
								}
								fromChangeSet.removeChanged(type, changedInSourceAndTargetId);
							}
						}
					} else {
						// this object has changed on both sides probably due to some cascading change, revise the revision on source, since we already have one on this branch
						revisionsToReviseOnMergeSource.put(type, changedInSourceAndTargetId);
						fromChangeSet.removeChanged(type, changedInSourceAndTargetId);
					}
				}
			}
		}
		
		// after generic conflict processing execute domain specific merge rules via conflict processor
		conflictProcessor.checkConflicts(this, fromChangeSet, toChangeSet).forEach(conflicts::add);
		
		if (!conflicts.isEmpty()) {
			throw new BranchMergeConflictException(conflicts.stream().map(conflictProcessor::convertConflict).collect(Collectors.toList()));
		}
		
		// apply property changes, conflicts in all cases, so merge commits will have the actual conflict resolutions
		if (!propertyUpdatesToApply.isEmpty()) {
			for (Entry<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> entry : propertyUpdatesToApply.entrySet()) {
				final Class<? extends Revision> type = entry.getKey();
				final Multimap<String, RevisionPropertyDiff> propertyUpdatesByObject = entry.getValue();
				final Iterable<JsonNode> objectsToUpdate = index.read(toRef, searcher -> {
					return searcher.search(Query.select(JsonNode.class).from(type).where(Expressions.matchAny(Revision.Fields.ID, propertyUpdatesByObject.keySet())).limit(propertyUpdatesByObject.keySet().size()).build());
				});
				for (JsonNode objectToUpdate : objectsToUpdate) {
					// read into revision object first
					Revision oldRevision = mapper.convertValue(objectToUpdate, type);
					
					// apply the JSON patch from the updates in place on the same JSON tree
					ArrayNode patch = mapper.createArrayNode();
					for (RevisionPropertyDiff diff : propertyUpdatesByObject.get(oldRevision.getId())) {
						patch.add(diff.asPatch(mapper));
					}
					JsonPatch.applyInPlace(patch, objectToUpdate);
					
					// convert it to Revision again to get the new object
					// FIXME for the future, figure out how to reduce the number of ser/deser during merge
					stageChange(oldRevision, mapper.convertValue(objectToUpdate, type));
					revisionsToReviseOnMergeSource.put(type, oldRevision.getId());
				}
			}
		}
		
		// apply new objects
		for (Class<? extends Revision> type : fromChangeSet.getAddedTypes()) {
			final Collection<String> newRevisionIds = fromChangeSet.getAddedIds(type);
			final Iterable<? extends Revision> oldRevisions = index.read(toRef, searcher -> searcher.get(type, newRevisionIds));
			final Iterable<? extends Revision> newRevisions = index.read(fromRef, searcher -> searcher.get(type, newRevisionIds));
			final Map<String, ? extends Revision> oldRevisionsById = FluentIterable.from(oldRevisions).uniqueIndex(Revision::getId);
			
			newRevisions.forEach(rev -> {
				if (oldRevisionsById.containsKey(rev.getId())) {
					stageChange(oldRevisionsById.get(rev.getId()), rev, squash);
				} else {
					stageNew(rev, squash);
				}
			});
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
					stageChange(oldRevisionsById.get(updatedId), updatedRevisionsById.get(updatedId), squash);
				} else {
					stageNew(updatedRevisionsById.get(updatedId), squash);
				}
			}
		}
		
		// always apply deleted objects, they set the revised timestamp properly without introducing any new document
		for (Class<? extends Revision> type : fromChangeSet.getRemovedTypes()) {
			final Collection<String> removedRevisionIds = fromChangeSet.getRemovedIds(type);
			index.read(toRef, searcher -> searcher.get(type, removedRevisionIds)).forEach(this::stageRemove);
		}
	}
	
	private Map<String, Map<String, RevisionCompareDetail>> indexPropertyChangesByObject(List<RevisionCompareDetail> changeDetails) {
		final Map<String, Map<String, RevisionCompareDetail>> propertyChangesByObject = newHashMap();
		for (RevisionCompareDetail changeDetail : changeDetails) {
			if (changeDetail.isPropertyChange()) {
				final String changedObjectId = changeDetail.getObject().id();
				if (!propertyChangesByObject.containsKey(changedObjectId)) {
					propertyChangesByObject.put(changedObjectId, newHashMap());
				}
				propertyChangesByObject.get(changedObjectId).put(changeDetail.getProperty(), changeDetail);
			}
		}
		return propertyChangesByObject;
	}

	public final class RevisionDiff {
		
		public final Revision oldRevision;
		public final Revision newRevision;
		
		private ArrayNode rawDiff;
		private ArrayNode diff;
		private Map<String, RevisionPropertyDiff> propertyChanges;
		
		private RevisionDiff(Revision oldRevision, Revision newRevision) {
			this.oldRevision = oldRevision;
			this.newRevision = newRevision;
		}

		public boolean hasChanges() {
			return rawDiff().size() > 0;
		}

		private ArrayNode rawDiff() {
			if (rawDiff == null) {
				ObjectNode oldRevisionSource = mapper.valueToTree(oldRevision);
				ObjectNode newRevisionSource = mapper.valueToTree(newRevision);
				final JsonNode diff = JsonDiff.asJson(oldRevisionSource, newRevisionSource, DIFF_FLAGS);
				final ArrayNode rawDiff = ClassUtils.checkAndCast(diff, ArrayNode.class);
				final ArrayNode filteredRawDiff = mapper.createArrayNode();
				final Iterator<JsonNode> elements = rawDiff.elements();
				while (elements.hasNext()) {
					JsonNode node = elements.next();
					final ObjectNode change = ClassUtils.checkAndCast(node, ObjectNode.class);
					final String property = change.get("path").asText().substring(1);
					
					// Remove administrative revision fields from diff, but keep all other ones
					if (!Revision.Fields.CREATED.equals(property) && !Revision.Fields.REVISED.equals(property)) {
						filteredRawDiff.add(change);
					}
				}
				this.rawDiff = filteredRawDiff;
			}
			return this.rawDiff;
		}
		
		public ArrayNode diff() {
			if (diff == null) {
				final DocumentMapping mapping = index.admin().mappings().getMapping(newRevision.getClass());
				final Set<String> diffFields = mapping.getHashedFields();
				if (diffFields.isEmpty()) {
					return null; // in case of no hash fields, do NOT try to compute the diff
				}
				
				final ArrayNode diff = mapper.createArrayNode();
				final Iterator<JsonNode> elements = rawDiff().elements();
				while (elements.hasNext()) {
					JsonNode node = elements.next();
					final ObjectNode change = ClassUtils.checkAndCast(node, ObjectNode.class);
					
					// Remove trailing segments in nested property paths (we are only interested in the top property)
					String property = change.get("path").asText().substring(1);
					final int nextSegmentIdx = property.indexOf("/");
					if (nextSegmentIdx >= 0) {
						property = property.substring(0, nextSegmentIdx);
					}

					// Keep hashed fields only
					if (diffFields.contains(property)) {
						diff.add(change);
					}
				}
				this.diff = diff;
			}
			return this.diff;
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
			return String.join(" -> ", getOldValue(), getNewValue());
		}
		
		public RevisionPropertyDiff convert(RevisionConflictProcessor processor) {
			return new RevisionPropertyDiff(property, processor.convertPropertyValue(property, oldValue), processor.convertPropertyValue(property, newValue));
		}

		public JsonNode asPatch(ObjectMapper mapper) {
			ObjectNode patch = mapper.createObjectNode();
			patch.set("op", mapper.valueToTree("replace"));
			patch.set("path", mapper.valueToTree("/".concat(property)));
			try {
				patch.set("value", mapper.readTree(newValue));
				// if it is unable to convert the newValue to a JSON value, then it is either an array or object, read it as tree
			} catch (JsonProcessingException e) {
				try {
					patch.set("value", mapper.valueToTree(newValue));
				} catch (IllegalArgumentException ex) {
					ex.addSuppressed(e);
					throw new IndexException("Unable to read value to JSON. Value: " + newValue, ex);
				}
			}
			return patch; 
		}
		
		@Override
		public String toString() {
			return String.format("%s[%s]", getProperty(), toValueChangeString());
		}
		
	}
	
	private enum StageKind {
		ADDED, CHANGED, REMOVED
	}
	
	private final class StagedObject {
		
		private final Object object;
		private final StageKind stageKind;
		private final boolean commit;
		private final RevisionDiff diff;
		
		private StagedObject(StageKind stageKind, Object object, RevisionDiff diff, boolean commit) {
			this.stageKind = stageKind;
			this.object = object;
			this.diff = diff;
			this.commit = commit;
		}
		
		public StagedObject withObject(Object newObject, boolean commit) {
			if (isChanged()) {
				return new StagedObject(stageKind, newObject, diff != null ? new RevisionDiff(diff.oldRevision, (Revision) newObject) : null, commit);
			} else {
				return new StagedObject(stageKind, newObject, null, commit);
			}
		}
		
		public boolean isAdded() {
			return StageKind.ADDED == stageKind;
		}
		
		public boolean isChanged() {
			return StageKind.CHANGED == stageKind;
		}
		
		public boolean isRemoved() {
			return StageKind.REMOVED == stageKind;
		}

		public Object getObject() {
			return object;
		}
		
		public boolean isCommit() {
			return commit;
		}
		
		public RevisionDiff getDiff() {
			return diff;
		}
		
	}
	
	public StagedObject added(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.ADDED, object, diff, commit);
	}
	
	public StagedObject changed(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.CHANGED, object, diff, commit);
	}

	public StagedObject removed(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.REMOVED, object, diff, commit);
	}
	
	private Map<ObjectId, StagedObject> getFilteredStagedObjects() {
		return Maps.filterEntries(stagedObjects, entry -> !exclusions.contains(entry.getKey().id()));
	}

}
