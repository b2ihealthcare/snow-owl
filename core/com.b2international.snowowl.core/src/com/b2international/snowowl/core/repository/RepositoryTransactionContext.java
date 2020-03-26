/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.LockedException;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.CommitDetail;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.TimestampProvider;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.codesystem.CodeSystemEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.b2international.snowowl.core.internal.locks.DatastoreOperationLockException;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * @since 4.5
 */
public final class RepositoryTransactionContext extends DelegatingBranchContext implements TransactionContext {

	private final String author;
	private final String commitComment;
	private final String parentContextDescription;
	
	private boolean isNotificationEnabled = true;
	
	@JsonIgnore
	private transient final Map<Pair<String, Class<?>>, Object> resolvedObjectsById = newHashMap();
	
	@JsonIgnore
	private transient final StagingArea staging;

	RepositoryTransactionContext(BranchContext context, String author, String commitComment, String parentContextDescription) {
		super(context);
		this.author = author;
		this.commitComment = commitComment;
		this.parentContextDescription = parentContextDescription;
		this.staging = context.service(RevisionIndex.class).prepareCommit(branchPath()).withContext(this);
		bind(StagingArea.class, this.staging);
	}
	
	@Override
	public boolean isDirty() {
		return staging.isDirty();
	}
	
	public <T> T getResolvedObjectById(String componentId, Class<T> type) {
		return type.cast(resolvedObjectsById.get(createComponentKey(componentId, type)));
	}
	
	@Override
	public String author() {
		return !Strings.isNullOrEmpty(author) ? author : service(User.class).getUsername();
	}
	
	@Override
	public <T> T lookup(String componentId, Class<T> type) {
		final T component = lookupIfExists(componentId, type);
		if (null == component) {
			throw new ComponentNotFoundException(type.getSimpleName(), componentId);
		}
		return component;
	}
	
	@Override
	public <T> T lookupIfExists(String componentId, Class<T> type) {
		return lookup(Collections.singleton(componentId), type).get(componentId);
	}
	
	@Override
	public <T> Map<String, T> lookup(Collection<String> componentIds, Class<T> type) {
		final Map<String, T> resolvedComponentsById = newHashMap();
		final Set<String> unresolvedComponentIds = newHashSet(componentIds);
		
		// check already resolved components first
		for (Iterator<String> it = unresolvedComponentIds.iterator(); it.hasNext();) {
			String componentId = it.next();
			Pair<String, Class<?>> key = createComponentKey(componentId, type);
			if (resolvedObjectsById.containsKey(key)) {
				resolvedComponentsById.put(componentId, type.cast(resolvedObjectsById.get(key)));
				it.remove();
			}
		}
		
		// as last resort, query the index for the storageKey to be able to resolve the class
		if (!unresolvedComponentIds.isEmpty()) {
			for (T object : fetchComponents(unresolvedComponentIds, type)) {
				final String componentId = getObjectId(object);
				resolvedComponentsById.put(componentId, object);
				resolvedObjectsById.put(createComponentKey(componentId, type), object);
			}
		}
		
		// TODO remove detached components???
		
		return resolvedComponentsById;
	}
	
	private String getObjectId(Object component) {
		if (component instanceof CodeSystemEntry) {
			return ((CodeSystemEntry) component).getShortName();
		} else if (component instanceof CodeSystemVersionEntry) { 
			return ((CodeSystemVersionEntry) component).getVersionId();
		} else if (component instanceof Revision) {
			return ((Revision) component).getId();
		}
		throw new UnsupportedOperationException("Cannot get objectId for " + component);
	}

	private <T> Pair<String, Class<?>> createComponentKey(final String componentId, Class<T> type) {
		return Tuples.<String, Class<?>>pair(componentId, type);
	}
	
	private <T> Iterable<T> fetchComponents(Collection<String> componentIds, Class<T> type) {
		try {
			if (Revision.class.isAssignableFrom(type)) {
				return service(RevisionSearcher.class).get(type, componentIds);
			} else {
				return service(Searcher.class).get(type, componentIds);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public String add(Object o) {
		if (o instanceof CodeSystemEntry) {
			final CodeSystemEntry cs = (CodeSystemEntry) o;
			staging.stageNew(cs.getShortName(), cs);
			resolvedObjectsById.put(createComponentKey(cs.getShortName(), cs.getClass()), cs);
			return cs.getShortName();
		} else if (o instanceof CodeSystemVersionEntry) { 
			final CodeSystemVersionEntry cs = (CodeSystemVersionEntry) o;
			staging.stageNew(cs.getVersionId(), cs);
			resolvedObjectsById.put(createComponentKey(cs.getVersionId(), cs.getClass()), cs);
			return cs.getVersionId();
		} else if (o instanceof Revision) {
			Revision rev = (Revision) o;
			staging.stageNew(rev);
			resolvedObjectsById.put(createComponentKey(rev.getId(), rev.getClass()), rev);
			return rev.getId();
		} else {
			throw new UnsupportedOperationException("Cannot add object to this repository: " + o);
		}
	}
	
	@Override
	public void update(Revision oldRevision, Revision changedRevision) {
		// check if the oldRevision is present as new object in the staging area and update the new object, otherwise stage an update
		if (staging.isNew(oldRevision)) {
			staging.stageNew(changedRevision);
		} else {
			staging.stageChange(oldRevision, changedRevision);
		}
		resolvedObjectsById.put(createComponentKey(changedRevision.getId(), changedRevision.getClass()), changedRevision);
	}
	
	@Override
	public void delete(Object o) {
		delete(o, false);
	}
	
	@Override
	public void delete(Object o, boolean force) {
		RevisionDocument doc = ClassUtils.checkAndCast(o, RevisionDocument.class);
		
		if (force || service(ComponentDeletionPolicy.class).canDelete(doc)) {
			staging.stageRemove(doc);
		} else {
			throw new ConflictException("'%s' '%s' cannot be deleted.", DocumentMapping.getType(doc.getClass()), doc.getId());
		}
	}
	
	@Override
	public void close() throws Exception {
		// TODO is it always okay to clear when closing tx???
		clear();
	}

	@Override
	public long commit() {
		return commit(author(), commitComment, parentContextDescription);
	}
	
	@Override
	public long commit(String commitComment) {
		return commit(author(), commitComment, parentContextDescription);
	}
	
	@Override
	public long commit(String commitComment, String parentContextDescription) {
		return commit(author(), commitComment, parentContextDescription);
	}
	
	@Override
	public long commit(String userId, String commitComment, String parentContextDescription) {
		if (!isDirty()) {
			return -1L;
		}
		final DatastoreLockContext lockContext = createLockContext(userId, parentContextDescription);
		final DatastoreLockTarget lockTarget = createLockTarget(id(), branchPath());
		IOperationLockManager locks = service(IOperationLockManager.class);
		Commit commit = null;
		try {
			acquireLock(locks, lockContext, lockTarget);
			final long timestamp = service(TimestampProvider.class).getTimestamp();
			log().info("Persisting changes to {}@{}", branchPath(), timestamp);
			commit = staging.commit(null, timestamp, userId, commitComment);
			log().info("Changes have been successfully persisted to {}@{}.", branchPath(), timestamp);
			return commit.getTimestamp();
		} catch (final IndexException e) {
			Throwable rootCause = Throwables.getRootCause(e);
			if (rootCause instanceof CycleDetectedException) {
				throw (CycleDetectedException) rootCause;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		} finally {
			locks.unlock(lockContext, lockTarget);
			// send a commit notification
			if (commit != null) {
				new RepositoryCommitNotification(id(),
						commit.getId(),						
						commit.getGroupId(),
						commit.getBranch(),
						commit.getTimestamp(),
						commit.getAuthor(),
						commit.getComment(),
						getNewObjects(commit),
						getChangedObjects(commit),
						getRemovedObjects(commit),
						commit.getMergeSource())
				.publish(service(IEventBus.class));
			}
			clear();
		}
	}

	private Collection<ComponentIdentifier> getNewObjects(Commit commit) {
		return commit.getDetails().stream()
			.filter(CommitDetail::isAdd)
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(detail.getComponentType());
				return detail.getComponents().stream().flatMap(Set::stream).map(id -> ComponentIdentifier.of(terminologyComponentId, id));
			})
			.collect(Collectors.toSet());
	}

	/* From all commit detail object, extract both component level changes and container related add/change/remove and mark them as CHANGED components */
	private Collection<ComponentIdentifier> getChangedObjects(Commit commit) {
		return commit.getDetails().stream()
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(detail.getObjectType());
				return detail.getObjects().stream().map(id -> ComponentIdentifier.of(terminologyComponentId, id)); 
			})
			.collect(Collectors.toSet());
	}
	
	private Collection<ComponentIdentifier> getRemovedObjects(Commit commit) {
		return commit.getDetails().stream()
			.filter(CommitDetail::isRemove)
			.flatMap(detail -> {
				final short terminologyComponentId = getTerminologyComponentId(detail.getComponentType());
				return detail.getComponents().stream().flatMap(Set::stream).map(id -> ComponentIdentifier.of(terminologyComponentId, id));
			})
			.collect(Collectors.toSet());
	}
	
	private short getTerminologyComponentId(String componentType) {
		try {
			return service(TerminologyComponents.class).getTerminologyComponentId(DocumentMapping.getClass(componentType));
		} catch (IllegalArgumentException e) {
			// return unspecified terminology component for each unknown components committed to the repo
			return TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT;
		}
	}

	private void clear() {
		resolvedObjectsById.clear();
	}

	private void acquireLock(IOperationLockManager locks, DatastoreLockContext lockContext, DatastoreLockTarget lockTarget) {
		try {
			locks.lock(lockContext, 1000L, lockTarget);
		} catch (final DatastoreOperationLockException e) {
			final DatastoreLockContext lockOwnerContext = e.getContext(lockTarget);
			throw new LockedException(MessageFormat.format("Write access to {0} was denied because {1} is {2}. Please try again later.", 
					lockTarget,
					lockOwnerContext.getUserId(), 
					lockOwnerContext.getDescription()));
		} catch (InterruptedException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	@Override
	public boolean isNotificationEnabled() {
		return isNotificationEnabled;
	}
	
	@Override
	public void setNotificationEnabled(boolean isNotificationEnabled) {
		this.isNotificationEnabled = isNotificationEnabled;
	}

	@Override
	public void clearContents() {
		final Index index = service(Index.class);
		final IndexAdmin indexAdmin = index.admin();
		final Mappings mappings = indexAdmin.mappings();
		
		final Stream<Class<?>> revisionTypes = mappings.getTypes()
			.stream()
			.filter(t -> Revision.class.isAssignableFrom(t));
		
		revisionTypes.forEach(type -> {

			final Query<String> idQuery = Query.select(String.class)
				.from(type)
				.fields(Revision.Fields.ID)
				.where(Expressions.matchAll())
				.scroll()
				.build();
			
			final RevisionSearcher revisionSearcher = service(RevisionSearcher.class);
			final Iterable<Hits<String>> batches = revisionSearcher.scroll(idQuery);
			
			for (final Hits<String> ids : batches) {
				final Iterable<?> revisions = fetchComponents(ids.getHits(), type);
				revisions.forEach(rev -> {
					final String revisionId = ((Revision) rev).getId();
					staging.stageRemove(revisionId, rev);	
				});
			}
		});
	}
	
	private static DatastoreLockContext createLockContext(String userId, String parentContextDescription) {
		return new DatastoreLockContext(userId, DatastoreLockContextDescriptions.COMMIT, parentContextDescription);
	}
	
	private static DatastoreLockTarget createLockTarget(String repositoryId, String branch) {
		return new DatastoreLockTarget(repositoryId, branch);
	}

}
