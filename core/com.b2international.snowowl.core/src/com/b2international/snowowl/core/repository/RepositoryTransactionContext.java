/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.index.Index;
import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.*;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.locks.IOperationLockManager;
import com.b2international.snowowl.core.locks.Lockable;
import com.b2international.snowowl.core.locks.Locks;
import com.b2international.snowowl.core.version.VersionDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class RepositoryTransactionContext extends DelegatingBranchContext implements TransactionContext, CommitSubjectSupplier {

	private final String author;
	private final String commitComment;
	private final String parentLockContext;
	
	private boolean isNotificationEnabled = true;
	
	@JsonIgnore
	private transient final Map<Pair<String, Class<?>>, Object> resolvedObjectsById = newHashMap();
	
	@JsonIgnore
	private transient final StagingArea staging;
	
	private transient final Multimap<Class<? extends Revision>, String> ensureUnique = LinkedHashMultimap.create();
	private transient final Multimap<Class<? extends Revision>, String> ensurePresent = LinkedHashMultimap.create();

	public RepositoryTransactionContext(BranchContext context, String author, String commitComment, String parentLockContext) {
		super(context);
		this.author = author;
		this.commitComment = commitComment;
		this.parentLockContext = parentLockContext;
		this.staging = context.service(RevisionIndex.class).prepareCommit(path()).withContext(this);
		bind(StagingArea.class, this.staging);
	}
	
	@Override
	public Set<String> getSubjectIds() {
		return optionalService(ResourceURI.class).map(ResourceURI::toString).map(Set::of).orElse(Set.of());
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
		return !Strings.isNullOrEmpty(author) ? author : service(User.class).getUserId();
	}
	
	@Override
	public String parentLock() {
		return parentLockContext;
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
		return componentId == null ? null : lookup(Collections.singleton(componentId), type).get(componentId);
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
		if (component instanceof Revision) {
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
		if (o instanceof VersionDocument) { 
			final VersionDocument version = (VersionDocument) o;
			staging.stageNew(version.getId(), version);
			resolvedObjectsById.put(createComponentKey(version.getId(), version.getClass()), version);
			return version.getId();
		} else if (o instanceof Revision) {
			Revision rev = (Revision) o;
			staging.stageNew(rev);
			resolvedObjectsById.put(createComponentKey(rev.getId(), rev.getClass()), rev);
			return rev.getId();
		} else {
			throw new UnsupportedOperationException("Unrecognized objects cannot be added to this repository: " + o);
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
		if (o instanceof RevisionDocument) {
			RevisionDocument doc = (RevisionDocument) o;
			
			if (force || optionalService(ComponentDeletionPolicy.class).map(cdp -> cdp.canDelete(doc)).orElse(Boolean.TRUE)) {
				staging.stageRemove(doc);
			} else {
				throw new ConflictException("'%s' '%s' cannot be deleted.", service(Mappings.class).getType(doc.getClass()), doc.getId());
			}
		} else {
			throw new UnsupportedOperationException("Unrecognized objects cannot be removed from this repository: " + o);
		}
		
	}
	
	@Override
	public void close() throws Exception {
		// TODO is it always okay to clear when closing tx???
		clear();
	}

	@Override
	public Optional<Commit> commit() {
		return commit(author(), commitComment, parentLockContext);
	}
	
	@Override
	public Optional<Commit> commit(String commitComment) {
		return commit(author(), commitComment, parentLockContext);
	}
	
	@Override
	public Optional<Commit> commit(String commitComment, String parentLockContext) {
		return commit(author(), commitComment, parentLockContext);
	}
	
	@Override
	public Optional<Commit> commit(String author, String commitComment, String parentLockContext) {
		if (!isDirty()) {
			return Optional.empty();
		}
		
		// XXX it would be great to use Locks.on(...) here as well
		// fall back to the current lock context or ROOT if none is present
		if (Strings.isNullOrEmpty(parentLockContext)) {
			parentLockContext = optionalService(Locks.class).map(Locks::lockContext).orElse(DatastoreLockContextDescriptions.ROOT);
		}
		final DatastoreLockContext lockContext = createLockContext(service(User.class).getUserId(), parentLockContext);
		final Lockable lockTarget = new Lockable(info().id(), path());
		IOperationLockManager locks = service(IOperationLockManager.class);
		Commit commit = null;
		try {
			locks.lock(lockContext, 1000L, lockTarget);
			final long timestamp = service(TimestampProvider.class).getTimestamp();
			log().info("Checking transaction content before commit to {}@{}", path(), timestamp);
			checkTransaction();
			log().info("Persisting changes to {}@{}", path(), timestamp);
			commit = staging.commit(null, timestamp, author, commitComment);
			log().info("Changes have been successfully persisted to {}@{}.", path(), timestamp);
			return Optional.ofNullable(commit);
		} catch (final IndexException e) {
			Throwable rootCause = Throwables.getRootCause(e);
			if (rootCause instanceof CycleDetectedException) {
				throw (CycleDetectedException) rootCause;
			}
			throw new SnowowlRuntimeException(e.getMessage(), e);
		} finally {
			locks.unlock(lockContext, lockTarget);
			if (commit != null && isNotificationEnabled()) {
				service(RepositoryCommitNotificationSender.class).publish(this, commit);
			}
			clear();
		}
	}

	private void checkTransaction() {
		if (!ensureUnique.isEmpty()) {
			// check if any of the listed IDs are already present in the index and if so, report AlreadyExistsException for the first registered ID (same behavior as before with explicit checks)
			for (Class<? extends Revision> type : ensureUnique.keySet()) {
				final Collection<String> idsToCheck = ensureUnique.get(type).stream().filter(id -> !IComponent.ROOT_ID.equals(id)).toList();
				final Map<String, ?> existingComponents = Maps.uniqueIndex(fetchComponents(idsToCheck, type), Revision::getId);
				for (String idToCheck : idsToCheck) {
					if (existingComponents.containsKey(idToCheck)) {
						throw new AlreadyExistsException(DocumentMapping.getDocType(type), idToCheck);
					}
				}
			}
		}
		
		if (!ensurePresent.isEmpty()) {
			// check if any of the listed IDs are already present in the index and if NOT, report ComponentNotFoundException for the first registered ID (same behavior as before with explicit checks)
			for (Class<? extends Revision> type : ensurePresent.keySet()) {
				final Set<String> idsToCheck = ensurePresent.get(type).stream().filter(id -> !IComponent.ROOT_ID.equals(id)).collect(Collectors.toSet());
				
				// first check if there are any deletion that are present in the ID set, if yes, then those IDs won't be available anymore, report a transaction error
				var deletedIds = staging.getRemovedObjects(Revision.class)
					.map(Revision::getId)
					.filter(idsToCheck::contains)
					.collect(Collectors.toSet());
				
				if (!deletedIds.isEmpty()) {
					throw new BadRequestException("Transaction would delete components that are still referenced by other components.")
						.withAdditionalInfo(Map.of("ids", deletedIds));
				}
				
				// then remove all new/changed objects that are already present in the tx staging area, those can be considered ensured
				staging.getNewObjects(Revision.class)
					.map(Revision::getId)
					.forEach(idsToCheck::remove);
				staging.getChangedObjects(Revision.class)
					.map(Revision::getId)
					.forEach(idsToCheck::remove);
				
				// then if there are any remaining IDs to check, fetch them from the store
				if (!idsToCheck.isEmpty()) {
					final Map<String, ?> existingComponents = Maps.uniqueIndex(fetchComponents(idsToCheck, type), Revision::getId);				
					for (String idToCheck : idsToCheck) {
						if (!existingComponents.containsKey(idToCheck)) {
							throw new ComponentNotFoundException(DocumentMapping.getDocType(type), idToCheck).toBadRequestException();
						}
					}
				}
			}
		}
	}

	@Override
	public void rollback() {
		staging.rollback();
		clear();
	}
	
	private void clear() {
		resolvedObjectsById.clear();
		ensureUnique.clear();
		ensurePresent.clear();
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
		final RevisionSearcher revisionSearcher = service(RevisionSearcher.class);
		final IndexAdmin indexAdmin = index.admin();
		final Mappings mappings = indexAdmin.getIndexMapping().getMappings();
		
		final Stream<Class<?>> revisionTypes = mappings.getTypes()
			.stream()
			.filter(t -> Revision.class.isAssignableFrom(t));
		
		revisionTypes.forEach(type -> {
			Query.select(String.class)
				.from(type)
				.fields(Revision.Fields.ID)
				.where(Expressions.matchAll())
				.build()
				.stream(revisionSearcher)
				.forEachOrdered(ids -> {
					final Iterable<?> revisions = fetchComponents(ids.getHits(), type);
					revisions.forEach(rev -> {
						final String revisionId = ((Revision) rev).getId();
						staging.stageRemove(revisionId, rev);	
					});
				});
		});
	}
	
	@Override
	public <T extends Revision> void ensureUnique(Class<T> documentType, String id) {
		ensureUnique.put(documentType, id);
	}
	
	@Override
	public <T extends Revision> void ensureUnique(Class<T> documentType, Iterable<String> ids) {
		if (!CompareUtils.isEmpty(ids)) {
			ensureUnique.putAll(documentType, ids);
		}
	}
	
	@Override
	public <T extends Revision> void ensurePresent(Class<T> documentType, String id) {
		ensurePresent.put(documentType, id);
	}
	
	@Override
	public <T extends Revision> void ensurePresent(Class<T> documentType, Iterable<String> ids) {
		if (!CompareUtils.isEmpty(ids)) {
			ensurePresent.putAll(documentType, ids);
		}
	}
	
	private static DatastoreLockContext createLockContext(String userId, String parentContextDescription) {
		return new DatastoreLockContext(userId, DatastoreLockContextDescriptions.COMMIT, parentContextDescription);
	}
	
}
