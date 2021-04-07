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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.Pair;
import com.b2international.index.BulkUpdate;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Hooks.Hook;
import com.b2international.index.revision.Hooks.PostCommitHook;
import com.b2international.index.revision.Hooks.PreCommitHook;
import com.b2international.index.util.JsonDiff;
import com.b2international.index.util.JsonDiff.JsonChange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonPatch;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

/**
 * A place that stores information about what will go into your next commit.
 * 
 * @since 7.0
 * @see RevisionIndex#prepareCommit()
 */
public final class StagingArea {

	private final DefaultRevisionIndex index;
	private final String branchPath;
	private final ObjectMapper mapper;
	private final int maxTermsCount;
	private final int commitWatermarkLow;
	private final int commitWatermarkHigh;

	private Map<ObjectId, StagedObject> stagedObjects;

	private SortedSet<RevisionBranchPoint> mergeSources;
	private RevisionBranchRef mergeFromBranchRef;
	private boolean squashMerge;
	private SetMultimap<Class<?>, String> revisionsToReviseOnMergeSource;
	private SetMultimap<Class<?>, String> externalRevisionsToReviseOnMergeSource;
	private Object context;

	StagingArea(DefaultRevisionIndex index, String branchPath, ObjectMapper mapper) {
		this.index = index;
		this.branchPath = branchPath;
		this.mapper = mapper;
		this.maxTermsCount = Integer.parseInt((String) index.admin().settings().get(IndexClientFactory.MAX_TERMS_COUNT_KEY));
		this.commitWatermarkLow = (int) index.admin().settings().get(IndexClientFactory.COMMIT_WATERMARK_LOW_KEY);
		this.commitWatermarkHigh = (int) index.admin().settings().get(IndexClientFactory.COMMIT_WATERMARK_HIGH_KEY);
		rollback();
	}
	
	/**
	 * @return the underlying {@link RevisionIndex} instance.
	 */
	public RevisionIndex getIndex() {
		return index;
	}
	
	/**
	 * @return the branch where this {@link StagingArea} will commit its staged changes during {@link #commit(long, String, String)}
	 */
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
	 * Reads from the underlying index using the branch that is currently being merged into this {@link StagingArea}'s branch.
	 * 
	 * @param read
	 * @return
	 * @throws IllegalStateException - if the method is called during standard commits
	 */
	public <T> T readFromMergeSource(RevisionIndexRead<T> read) {
		Preconditions.checkState(isMerge(), "Cannot read revisions from mergeSource branch in non-merge scenarios. Perform a merge() before calling this method.");
		return index.read(mergeFromBranchRef, read);
	}
	
	/**
	 * @param revision
	 * @return <code>true</code> if the given revision is staged in this {@link StagingArea}
	 */
	public boolean isStaged(Revision revision) {
		return stagedObjects.containsKey(revision.getObjectId());
	}
	
	/**
	 * @param revision
	 * @return whether the given {@link Revision} instance is registered as NEW object in this staging area.
	 */
	public boolean isNew(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isAdded();
	}
	
	/**
	 * @param revision
	 * @return whether the given {@link Revision} instance is registered as CHANGED object in this staging area.
	 */
	public boolean isChanged(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isChanged();
	}
	
	/**
	 * @param revision
	 * @return whether the given {@link Revision} instance is registered as REMOVED object in this staging area.
	 */
	public boolean isRemoved(Revision revision) {
		StagedObject so = stagedObjects.get(revision.getObjectId());
		return so != null && so.isRemoved();
	}
	
	/**
	 * @return the number of staged objects registered in this staging area
	 */
	public int getNumberOfStagedObjects() {
		return stagedObjects.size();
	}
	
	/**
	 * @return a {@link Stream} of objects that are registered as NEW in this staging area.
	 */
	public Stream<Object> getNewObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isAdded())
				.map(e -> e.getValue().getObject());
	}

	/**
	 * @param <T>
	 * @param type - the requested object type
	 * @return a {@link Stream} of objects of type T that are registered as NEW in this staging area.
	 */
	public <T> Stream<T> getNewObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isAdded())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}

	/**
	 * @param <T>
	 * @param type
	 * @param key
	 * @return an Object of T type registered as NEW under the given key identifier, or <code>null</code> if no such object is registered.
	 */
	public <T> T getNewObject(Class<T> type, String key) {
		StagedObject stagedObject = stagedObjects.get(ObjectId.of(type, key));
		return stagedObject == null ? null : type.cast(stagedObject.getObject());
	}
	
	/**
	 * @return a {@link Stream} of objects that are registered as CHANGED in this staging area.
	 */
	public Stream<Object> getChangedObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(e -> e.getValue().getObject());
	}
	
	/**
	 * @param <T>
	 * @param type - the requested object type
	 * @return a {@link Stream} of objects of type T that are registered as CHANGED in this staging area.
	 */
	public <T> Stream<T> getChangedObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}
	
	/**
	 * @return a {@link Stream} of objects that are registered as REMOVED in this staging area.
	 */
	public Stream<Object> getRemovedObjects() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isRemoved())
				.map(e -> e.getValue().getObject());
	}
	
	/**
	 * @param <T>
	 * @param type - the requested object type
	 * @return a {@link Stream} of objects of type T that are registered as REMOVED in this staging area.
	 */
	public <T> Stream<T> getRemovedObjects(Class<T> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isRemoved())
				.map(entry -> entry.getValue().getObject())
				.filter(type::isInstance)
				.map(type::cast);
	}
	
	/**
	 * @return a {@link Map} of changed revision diffs keyed by their {@link ObjectId}.
	 * @see RevisionDiff
	 */
	public Map<ObjectId, RevisionDiff> getChangedRevisions() {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getDiff()));
	}
	
	/**
	 * @param type - the requested object type
	 * @return a {@link Stream} of {@link RevisionDiff} objects registered for the given type.
	 */
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getDiff())
				.filter(diff -> type.isAssignableFrom(diff.newRevision.getClass()));
	}
	
	/**
	 * 
	 * @param type - the requested object type
	 * @param changedPropertyNames - the requested property names
	 * @return a {@link Stream} of {@link RevisionDiff} objects registered for the given type that have property changes for the given {@link Set} of property names.
	 */
	public Stream<RevisionDiff> getChangedRevisions(Class<? extends Revision> type, Set<String> changedPropertyNames) {
		return stagedObjects.entrySet()
				.stream()
				.filter(entry -> entry.getValue().isChanged())
				.map(entry -> entry.getValue().getDiff())
				.filter(diff -> type.isAssignableFrom(diff.newRevision.getClass()))
				.filter(diff -> diff.hasRevisionPropertyChanges(changedPropertyNames));
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
		if (!isDirty() && !isMerge()) {
			return null;
		}
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

		stagedObjects.entrySet().forEach( entry -> {
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
		stagedObjects.entrySet().forEach( entry -> {
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
		final Multimap<JsonChange, ObjectId> revisionsByChange = HashMultimap.create();
		stagedObjects.entrySet().forEach( entry -> {
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
					changedComponentsByContainer.put(containerId, objectId);
					
					if (revisionDiff.diff() != null) {
						// register actual difference between revisions to commit
						revisionDiff.diff().forEach(change -> {
							revisionsByChange.put(change, objectId);
						});
					}
					
				} else {
					writer.put(key.id(), object);
					changedComponentsByContainer.put(ObjectId.rootOf(DocumentMapping.getType(object.getClass())), key);
				}
			}
		});
		
		// apply revised flag on merge source branch
		if (isMerge()) {
			for (Class<?> type : revisionsToReviseOnMergeSource.keySet()) {
				writer.setRevised(type, ImmutableSet.copyOf(revisionsToReviseOnMergeSource.get(type)), mergeFromBranchRef);
			}
		}
		
		// register detail changes only if this is a regular commit, or is it a squash merge commit 
		final List<CommitDetail> details;
		
		if (!isMerge() || squashMerge) {
			details = Lists.newArrayList();
			// collect property changes
			revisionsByChange.asMap().forEach((change, objects) -> {
				ListMultimap<String, String> objectIdsByType = ArrayListMultimap.create();
				objects.forEach(objectId -> objectIdsByType.put(objectId.type(), objectId.id()));
				
				final String prop = change.getFieldPath();
				final String value = change.serializeValue();
				if (change.isRemove()) { // fully clear property
					// split by object type
					objectIdsByType.keySet().forEach(type -> {
						// in case of property removal/clear use empty String as toValue and the original value as fromValue 
						details.add(CommitDetail.changedProperty(prop, value, "", type, objectIdsByType.get(type)));
					});
				} else if (change.isAdd() || change.isReplace()) { // set or replace value
					final String from = change.serializeFromValue();
					// split by object type
					objectIdsByType.keySet().forEach(type -> {
						// standard property add/replace should use the JSON diffs, from/to values as is
						details.add(CommitDetail.changedProperty(prop, from, value, type, objectIdsByType.get(type)));
					});
				} else {
					throw new UnsupportedOperationException("Unknown change: " + change);
				}
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
			stagedObjects.entrySet().forEach( entry -> {
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
		} else {
			details = Collections.emptyList();
		}
		
		// free up memory before committing 
		clear();
		newComponentsByContainer.clear();
		changedComponentsByContainer.clear();
		removedComponentsByContainer.clear();
		deletedIdsByType.clear();
		
		// nothing to commit, break
		if (writer.isEmpty() && CompareUtils.isEmpty(mergeSources)) {
			return null;
		}
		
		// raise watermark logs if above thresholds
		reportWarningIfCommitWatermarkExceeded(details, author, commitComment);
		
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
				Expressions.exactMatch(RevisionBranch.Fields.PATH, branchPath),
				RevisionBranch.Scripts.COMMIT,
				toBranchUpdateParams.build()
			)
		);
		
		writer.commit();

		// clear remaining state
		mergeSources = null;
		
		return commitDoc;
	}

	private void reportWarningIfCommitWatermarkExceeded(final List<CommitDetail> details, String author, String commitComment) {
		int numberOfCommitDetails = calculateCommitDetails(details);
		if (numberOfCommitDetails > commitWatermarkHigh) {
			index.admin().log().warn("high commit watermark [{}] exceeded in commit [{} - {} - {}] number of changes: {}", commitWatermarkHigh, branchPath, author, commitComment, numberOfCommitDetails);
		} else if (numberOfCommitDetails > commitWatermarkLow) {
			index.admin().log().warn("low commit watermark [{}] exceeded in commit [{} - {} - {}] number of changes: {}", commitWatermarkLow, branchPath, author, commitComment, numberOfCommitDetails);
		}
	}

	private int calculateCommitDetails(List<CommitDetail> details) {
		int i = 0;
		for (CommitDetail detail : details) {
			i += calculateCommitDetails(detail);
		}
		return i;
	}
	
	private int calculateCommitDetails(CommitDetail detail) {
		if (detail.isPropertyChange()) {
			return detail.getObjects().size();
		} else {
			return detail.getComponents().stream().mapToInt(Set::size).sum();
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
	 * @return the branch path that's content is currently being merged into this {@link StagingArea}'s branch path.
	 * @throws IllegalStateException - if trying to get the branch in non-merge scenarios
	 */
	public String getMergeFromBranchPath() {
		Preconditions.checkState(isMerge(), "Cannot get merge from branch path in non-merge scenarios. Start a merge() before calling this method.");
		return mergeFromBranchRef.path();
	}
	
	/**
	 * Roll back staging area to an empty state, removing all staged changes, merge states, everything.
	 */
	public void rollback() {
		clear();
		this.mergeFromBranchRef = null;
		this.mergeSources = null;
	}
	
	private void clear() {
		stagedObjects = newHashMap();
		revisionsToReviseOnMergeSource = HashMultimap.create();
		externalRevisionsToReviseOnMergeSource = HashMultimap.create();
	}

	/**
	 * Stages the given {@link Revision} as NEW in this staging area for commit.
	 * 
	 * @param newRevision - the revision to register
	 * @return - this staging area for chaining
	 * @see #stageNew(Revision, boolean)
	 */
	public StagingArea stageNew(Revision newRevision) {
		return stageNew(newRevision, true);
	}
	
	/**
	 * Stages the given {@link Revision} as NEW in this staging area either for commit (commit=true) or for change processing purposes only (commit=false).
	 *  
	 * @param newRevision - the revision to register
	 * @param commit - to register the revision as a NEW object to commit, or as a NEW object to process by commit hooks
	 * @return - this staging area for chaining
	 */
	public StagingArea stageNew(Revision newRevision, boolean commit) {
		return stageNew(newRevision.getId(), newRevision, commit);
	}
	
	/**
	 * Stages the given {@link Object} as NEW in this staging area for commit under the given key.
	 * 
	 * @param key - the identifier to use when staging the object
	 * @param newDocument - the new object to register
	 * @return - this staging area for chaining
	 * @see #stageNew(String, Object, boolean)
	 */
	public StagingArea stageNew(String key, Object newDocument) {
		return stageNew(key, newDocument, true);
	}
	
	/**
	 * Stages the given {@link Object} as NEW in this staging area either for commit (commit=true) or for change processing purposes only (commit=false) under the given key.
	 * 
	 * @param key - the identifier to use when staging the object
	 * @param newDocument - the new object to register
	 * @param commit - to register the object as a NEW object to commit, or as a NEW object to process by commit hooks
	 * @return - this staging area for chaining
	 */
	public StagingArea stageNew(String key, Object newDocument, boolean commit) {
		ObjectId objectId = ObjectId.toObjectId(newDocument, key);
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

	/**
	 * Stages the given {@link Revision} as CHANGED in this staging area for commit. 
	 * @param oldRevision - the revision's old state (current state in the index)
	 * @param changedRevision - the revision's new state with potentially indexable changes
	 * @return - this staging area for chaining
	 */
	public StagingArea stageChange(Revision oldRevision, Revision changedRevision) {
		return stageChange(oldRevision, changedRevision, true);
	}
	
	/**
	 * Stages the given {@link Revision} as CHANGED in this staging area either for commit (commit=true) or for change processing purposes only
	 * (commit=false). In case the object is already registered in this staging area via an early stageX method call this method will use the
	 * currently staged state as oldRevision instead of the given argument and compute the change compared to that state.
	 * 
	 * @param oldRevision
	 *            - the revision's old state (current state in the index)
	 * @param changedRevision
	 *            - the revision's new state with potentially indexable changes
	 * @param commit
	 *            - to register the object as a CHANGED object to commit, or as a CHANGED object to process by commit hooks only
	 * @return - this staging area for chaining
	 */
	public StagingArea stageChange(Revision oldRevision, Revision changedRevision, boolean commit) {
		checkArgument(Objects.equals(oldRevision.getId(), changedRevision.getId()), "IDs of oldRevision and changedRevision must match");
		ObjectId id = ObjectId.toObjectId(changedRevision, changedRevision.getId());
		if (stagedObjects.containsKey(id)) {
			StagedObject currentObject = stagedObjects.get(id);
			stagedObjects.put(id, currentObject.withObject(changedRevision, commit));
		} else {
			stagedObjects.put(id, changed(changedRevision, new RevisionDiff(oldRevision, changedRevision), commit));
		}
		return this;
	}
	
	/**
	 * Stages the given {@link Revision} as REMOVED object in this staging area for commit.
	 * @param removedRevision - the revision to register
	 * @return - this staging area for chaining
	 */
	public StagingArea stageRemove(Revision removedRevision) {
		return stageRemove(removedRevision, true);
	}
	
	/**
	 * Stages the given {@link Revision} as REMOVED object in this staging area for commit.
	 * @param removedRevision - the revision to register
	 * @param commit
	 *            - to register the object as a REMOVED object to commit, or as a REMOVED object to process by commit hooks only
	 * @return - this staging area for chaining
	 */
	public StagingArea stageRemove(Revision removedRevision, boolean commit) {
		return stageRemove(removedRevision.getId(), removedRevision, commit);
	}
	
	/**
	 * Stages the given {@link Object} as REMOVED object in this staging area for commit. 
	 * 
	 * @param key - the identifier to use when staging the object
	 * @param removed - the removed object to register
	 * @return - this staging area for chaining
	 */
	public StagingArea stageRemove(String key, Object removed) {
		return stageRemove(key, removed, true);
	}
	
	/**
	 * Stages the given {@link Object} as REMOVED in this staging area either for commit (commit=true) or for change processing purposes only (commit=false).
	 * 
	 * @param key - the identifier to use when staging the object
	 * @param removed - the removed object to register
	 * @param commit
	 *            - to register the object as a REMOVED object to commit, or as a REMOVED object to process by commit hooks only
	 * @return - this staging area for chaining
	 */
	public StagingArea stageRemove(String key, Object removed, boolean commit) {
		ObjectId objectId = ObjectId.toObjectId(removed, key);
		StagedObject stagedObject = stagedObjects.get(objectId);
		if (stagedObject != null && stagedObject.isChanged()) {
			stagedObjects.put(objectId, removed(stagedObject.getDiff().oldRevision, null, commit));			
		} else {
			stagedObjects.put(ObjectId.toObjectId(removed, key), removed(removed, null, commit));			
		}
		return this;
	}
	
	/**
	 * Mark the object registered with the given type and ID revised on the current merge source.
	 * 
	 * @param type - the type of the object to revise
	 * @param id - the identifier of the object to revise
	 */
	public void reviseOnMergeSource(Class<?> type, String id) {
		externalRevisionsToReviseOnMergeSource.put(type, id);
	}
	
	/*package*/ void merge(RevisionBranchRef fromRef, RevisionBranchRef toRef, boolean squash, RevisionConflictProcessor conflictProcessor, Set<String> exclusions) {
		checkArgument(this.mergeSources == null, "Already merged another ref to this StagingArea. Commit staged changes to apply them.");
		this.mergeFromBranchRef = fromRef.difference(toRef);
		this.mergeSources = this.mergeFromBranchRef
				.segments()
				.stream()
				.filter(segment -> segment.branchId() != toRef.branchId())
				.map(RevisionSegment::getEndPoint)
				.collect(Collectors.toCollection(TreeSet::new));
		this.squashMerge = squash;
		
		List<RevisionCompareDetail> fromChangeDetails = index.compare(toRef, fromRef, Integer.MAX_VALUE).getDetails();
		
		if (!CompareUtils.isEmpty(exclusions)) {
			// Exclude items from change details of the "from" branch, so they do not participate in conflict processing
			fromChangeDetails = fromChangeDetails
				.stream()
				.filter(d -> !exclusions.contains(d.isPropertyChange() 
						? d.getObject().id() 
						: d.getComponent().id()))
				.collect(Collectors.toList());
		}
		
		// in case of nothing to merge, then just proceed to commit
		if (fromChangeDetails.isEmpty()) {
			return;
		}
		
		List<RevisionCompareDetail> toChangeDetails = index.compare(fromRef, toRef, Integer.MAX_VALUE).getDetails();
		
		// in case of fast-forward merge only check conflicts when there are changes on the to branch
		if (toChangeDetails.isEmpty() && !squash) {
			return;
		}
		
		RevisionBranchChangeSet fromChangeSet = new RevisionBranchChangeSet(index, fromRef, fromChangeDetails);
		RevisionBranchChangeSet toChangeSet = new RevisionBranchChangeSet(index, toRef, toChangeDetails);
		
		final List<Conflict> conflictsToReport = Lists.newArrayList();
		final Map<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> propertyUpdatesToApply = Maps.newHashMap();
		
		// check conflicts and commit only the resolved conflicts
		collectConflicts(fromChangeSet, fromChangeDetails, toChangeSet, toChangeDetails, conflictsToReport, propertyUpdatesToApply, conflictProcessor);
		
		if (!conflictsToReport.isEmpty()) {
			throw new BranchMergeConflictException(conflictsToReport.stream().map(conflictProcessor::convertConflict).collect(Collectors.toList()));
		}
		
		// we can remove toChangeDetails and fromChangeDetails
		// extract info from changeset and then null out to free up memory
		SetMultimap<Class<? extends Revision>, String> added = fromChangeSet.getAdded();
		SetMultimap<Class<? extends Revision>, String> changed = fromChangeSet.getChanged();
		SetMultimap<Class<? extends Revision>, String> removed = fromChangeSet.getRemoved();
		fromChangeSet = null;
		fromChangeDetails = null;
		toChangeSet = null;
		toChangeDetails = null;
		
		applyPropertyUpdates(toRef, propertyUpdatesToApply);
		
		// apply new objects
		applyNewObjects(added, fromRef, toRef, squash);
		
		// apply changed objects
		applyChangedObjects(changed, fromRef, toRef, squash);
		
		// always apply deleted objects, they set the revised timestamp properly without introducing any new document
		applyRemovedObjects(removed, fromRef, toRef, squash);
		
		// any externally marked revised revisions should be applied here
		revisionsToReviseOnMergeSource.putAll(externalRevisionsToReviseOnMergeSource);
	}

	private void applyPropertyUpdates(final RevisionBranchRef toRef, final Map<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> propertyUpdatesToApply) {
		// apply property changes, conflicts in all cases, so merge commits will have the actual conflict resolutions
		if (!propertyUpdatesToApply.isEmpty()) {
			for (Entry<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> entry : propertyUpdatesToApply.entrySet()) {
				final Class<? extends Revision> type = entry.getKey();
				final Multimap<String, RevisionPropertyDiff> propertyUpdatesByObject = entry.getValue();
				// if already marked as revised due to donation, skip loading it and handling it
				final Set<String> updatedIds = Sets.difference(propertyUpdatesByObject.keySet(), externalRevisionsToReviseOnMergeSource.get(type));
				final Iterable<JsonNode> objectsToUpdate = index.read(toRef, searcher -> {
					return searcher.search(Query.select(JsonNode.class).from(type).where(Expressions.matchAny(Revision.Fields.ID, updatedIds)).limit(updatedIds.size()).build());
				});
				for (JsonNode objectToUpdate : objectsToUpdate) {
					// read into revision object first
					Revision oldRevision = mapper.convertValue(objectToUpdate, type);
					
					// apply the JSON patch from the updates in place on the same JSON tree
					ArrayNode patch = mapper.createArrayNode();
					for (RevisionPropertyDiff diff : propertyUpdatesByObject.get(oldRevision.getId())) {
						patch.add(diff.asPatch(mapper, objectToUpdate));
					}
					JsonPatch.applyInPlace(patch, objectToUpdate);
					
					// convert it to Revision again to get the new object
					// FIXME for the future, figure out how to reduce the number of ser/deser during merge
					stageChange(oldRevision, mapper.convertValue(objectToUpdate, type));
					revisionsToReviseOnMergeSource.put(type, oldRevision.getId());
				}
			}
		}
	}

	private void collectConflicts(RevisionBranchChangeSet fromChangeSet, List<RevisionCompareDetail> fromChangeDetails, RevisionBranchChangeSet toChangeSet, List<RevisionCompareDetail> toChangeDetails, List<Conflict> conflictsToReport,
			Map<Class<? extends Revision>, Multimap<String, RevisionPropertyDiff>> propertyUpdatesToApply, RevisionConflictProcessor conflictProcessor) {
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
		Set<String> changedRevisionIdsToCheck = newHashSet(toChangeSet.getChangedIds());
		Set<String> removedRevisionIdsToCheck = newHashSet(toChangeSet.getRemovedIds());
		for (Class<? extends Revision> type : fromChangeSet.getChangedTypes()) {
			final DocumentMapping mapping = index.admin().mappings().getMapping(type);
			final String docType = mapping.typeAsString();
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
									mapping,
									sourceChangeDiff,
									targetChangeDiff,
									mapper
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
						fromChangeSet.removeChanged(type, changedInSourceAndTargetId);
					}
					
					// this object has changed on both sides either by tracked field changes or due to some cascading derived field change
					// revise the revision on source, since we already have one on this branch already
					revisionsToReviseOnMergeSource.put(type, changedInSourceAndTargetId);
				}
			}
		}
		
		// after generic conflict processing execute domain specific merge rules via conflict processor
		conflictProcessor.checkConflicts(this, fromChangeSet, toChangeSet).forEach(conflicts::add);
		
		// handle domain-specific conflict filtering, like donated content, etc.
		// and add all reported conflicts to conflictsToReport
		conflictsToReport.addAll(conflictProcessor.filterConflicts(this, conflicts));		
	}

	private void applyRemovedObjects(SetMultimap<Class<? extends Revision>, String> removed, RevisionBranchRef fromRef, RevisionBranchRef toRef,
			boolean squash) {
		for (Class<? extends Revision> type : ImmutableSet.copyOf(removed.keySet())) {
			final Collection<String> removedRevisionIds = removed.removeAll(type);
			for (List<String> currentRemovedRevisionIds : Iterables.partition(removedRevisionIds, maxTermsCount)) {
				index.read(toRef, searcher -> searcher.get(type, currentRemovedRevisionIds)).forEach(this::stageRemove);
			}
		}
	}

	private void applyChangedObjects(SetMultimap<Class<? extends Revision>, String> changed, RevisionBranchRef fromRef, RevisionBranchRef toRef,
			boolean squash) {
		for (Class<? extends Revision> type : ImmutableSet.copyOf(changed.keySet())) {
			final Collection<String> changedRevisionIds = changed.removeAll(type);
			
			for (List<String> currentChangedRevisionIds : Iterables.partition(changedRevisionIds, maxTermsCount)) {
				
				final Iterable<? extends Revision> oldRevisions = index.read(toRef, searcher -> searcher.get(type, currentChangedRevisionIds));
				final Map<String, ? extends Revision> oldRevisionsById = FluentIterable.from(oldRevisions).uniqueIndex(Revision::getId);
				final Iterable<? extends Revision> updatedRevisions = index.read(fromRef, searcher -> searcher.get(type, currentChangedRevisionIds));
				final Map<String, ? extends Revision> updatedRevisionsById = FluentIterable.from(updatedRevisions).uniqueIndex(Revision::getId);
			
				for (String updatedId : updatedRevisionsById.keySet()) {
					if (oldRevisionsById.containsKey(updatedId)) {
						stageChange(oldRevisionsById.get(updatedId), updatedRevisionsById.get(updatedId), squash);
					} else {
						stageNew(updatedRevisionsById.get(updatedId), squash);
					}
				}
				
			}
		}
	}

	private void applyNewObjects(final SetMultimap<Class<? extends Revision>, String> added, RevisionBranchRef fromRef, RevisionBranchRef toRef, boolean squash) {
		for (Class<? extends Revision> type : ImmutableSet.copyOf(added.keySet())) {
			final Set<String> addedIds = added.removeAll(type);
			// skip new objects that are already marked as revised on merge source, content that is present on target should take place instead
			final Set<String> newRevisionIds = Sets.difference(addedIds, externalRevisionsToReviseOnMergeSource.get(type));
			
			for (List<String> currentNewRevisionIds : Iterables.partition(newRevisionIds, maxTermsCount)) {
				final Iterable<? extends Revision> oldRevisions = index.read(toRef, searcher -> searcher.get(type, currentNewRevisionIds));
				final Iterable<? extends Revision> newRevisions = index.read(fromRef, searcher -> searcher.get(type, currentNewRevisionIds));
				final Map<String, ? extends Revision> oldRevisionsById = FluentIterable.from(oldRevisions).uniqueIndex(Revision::getId);
				
				newRevisions.forEach(rev -> {
					if (oldRevisionsById.containsKey(rev.getId())) {
						stageChange(oldRevisionsById.get(rev.getId()), rev, squash);
					} else {
						stageNew(rev, squash);
					}
				});
			}
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
		
		private JsonDiff rawDiff;
		private JsonDiff diff;
		private Map<String, RevisionPropertyDiff> propertyChanges;
		
		private RevisionDiff(Revision oldRevision, Revision newRevision) {
			this.oldRevision = oldRevision;
			this.newRevision = newRevision;
		}

		public boolean hasChanges() {
			return rawDiff().hasChanges();
		}
		
		public DocumentMapping getMapping() {
			return index.admin().mappings().getMapping(newRevision.getClass());
		}

		private JsonDiff rawDiff() {
			if (rawDiff == null) {
				final DocumentMapping mapping = getMapping();
				ObjectNode oldRevisionSource = mapper.valueToTree(oldRevision);
				ObjectNode newRevisionSource = mapper.valueToTree(newRevision);
				final JsonDiff diff = JsonDiff.diff(oldRevisionSource, newRevisionSource);
				final ArrayNode filteredRawDiff = mapper.createArrayNode();
				final Iterator<JsonChange> elements = diff.iterator();
				Set<String> fieldsToSkip = null; 
				while (elements.hasNext()) {
					JsonChange change = elements.next();
					ObjectNode rawChange = change.getRawChange();
					final String property = change.getRootFieldPath();
					
					if (Revision.isRevisionField(property) || (fieldsToSkip != null && fieldsToSkip.contains(property))) {
						continue;
					}
					
					// in case of a collection-like tracked property, convert the first diff to a full prop diff and ignore the rest of the prop changes on the same property
					if (mapping.getTrackedRevisionFields().contains(property) && mapping.isCollection(property)) {
						// construct a replacement JSON array patch node
						rawChange = new RevisionPropertyDiff(property, JsonDiff.serialize(oldRevisionSource, property), JsonDiff.serialize(newRevisionSource, property))
								.asPatch(mapper, oldRevisionSource, true /* includeFromValue */);
						if (fieldsToSkip == null) {
							fieldsToSkip = Sets.newHashSetWithExpectedSize(1); // expect a single array like property per type
						}
						fieldsToSkip.add(property);
					}
					
					filteredRawDiff.add(rawChange);
				}
				this.rawDiff = new JsonDiff(filteredRawDiff);
			}
			return this.rawDiff;
		}
		
		public JsonDiff diff() {
			if (diff == null) {
				final DocumentMapping mapping = getMapping();
				final Set<String> revisionFields = mapping.getTrackedRevisionFields();
				if (revisionFields.isEmpty()) {
					return null; // in case of no fields to revision control, do NOT try to compute the diff
				}
				
				final ArrayNode filteredDiff = mapper.createArrayNode();
				for (JsonChange change : rawDiff()) {
					// Remove trailing segments in nested property paths (we are only interested in the top property)
					String property = change.getRootFieldPath();

					// Keep revision fields only
					if (revisionFields.contains(property)) {
						filteredDiff.add(change.getRawChange());
					}					
				}
				this.diff = new JsonDiff(filteredDiff);
			}
			return this.diff;
		}

		public RevisionPropertyDiff getRevisionPropertyDiff(String property) {
			return getRevisionPropertyDiffs().get(property);
		}

		private Map<String, RevisionPropertyDiff> getRevisionPropertyDiffs() {
			if (propertyChanges == null) {
				propertyChanges = newHashMapWithExpectedSize(2);
				for (JsonChange change : diff()) {
					String prop = change.getFieldPath();
					final String from = change.serializeFromValue();
					final String to = change.serializeValue();
					propertyChanges.put(prop, new RevisionPropertyDiff(prop, from, to));
				}
			}
			return propertyChanges;
		}
		
		public boolean hasRevisionPropertyChanges(String property) {
			return hasRevisionPropertyChanges(Set.of(property));
		}
		
		public boolean hasRevisionPropertyChanges(Set<String> propertyNames) {
			if (CompareUtils.isEmpty(propertyNames)) return false;
			final Set<String> knownPropertyDiffs = getRevisionPropertyDiffs().keySet();
			return propertyNames.stream().filter(knownPropertyDiffs::contains).findFirst().isPresent();
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

		public ObjectNode asPatch(ObjectMapper mapper, JsonNode objectToUpdate) {
			return asPatch(mapper, objectToUpdate, false);
		}
		
		public ObjectNode asPatch(ObjectMapper mapper, JsonNode objectToUpdate, boolean includeFromValue) {
			ObjectNode patch = mapper.createObjectNode();
			patch.set("op", mapper.valueToTree(objectToUpdate.path(property).isMissingNode() ? "add" : "replace"));
			patch.set("path", mapper.valueToTree("/".concat(property)));
			setField(mapper, patch, "value", newValue);
			if (includeFromValue) {
				setField(mapper, patch, "fromValue", oldValue);
			}
			return patch; 
		}

		private static void setField(ObjectMapper mapper, ObjectNode patch, String property, String value) {
			if (value == null) {
				patch.set(property, mapper.nullNode());
			} else {
				try {
					patch.set(property, mapper.readTree(value));
					// if it is unable to convert the newValue to a JSON value, then it is either an array or object, read it as tree
				} catch (JsonProcessingException e) {
					try {
						patch.set(property, mapper.valueToTree(value));
					} catch (IllegalArgumentException ex) {
						ex.addSuppressed(e);
						throw new IndexException("Unable to read value to JSON. Value: " + value, ex);
					}
				}
			}
		}
		
		public RevisionPropertyDiff withNewValue(String newValue) {
			return new RevisionPropertyDiff(property, oldValue, newValue);
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
			} else if (isAdded()) {
				return new StagedObject(stageKind, newObject, null, commit);
			} else {
				return this;
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
	
	private StagedObject added(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.ADDED, object, diff, commit);
	}
	
	private StagedObject changed(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.CHANGED, object, diff, commit);
	}

	private StagedObject removed(Object object, RevisionDiff diff, boolean commit) {
		return new StagedObject(StageKind.REMOVED, object, diff, commit);
	}
	
}
