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

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.exceptions.RequestTimeoutException;
import com.b2international.commons.options.Metadata;
import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexWrite;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * @since 6.5
 */
public abstract class BaseRevisionBranching {

	private final RevisionIndex index;
	private final TimestampProvider timestampProvider;
	private final ObjectMapper mapper;
	private final List<Consumer<String>> onBranchChange = newArrayListWithCapacity(1);
	
	private final LoadingCache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
			.expireAfterAccess(5L, TimeUnit.MINUTES)
			.build(new CacheLoader<String, ReentrantLock>() {
				@Override
				public ReentrantLock load(String key) throws Exception {
					return new ReentrantLock();
				}
			});

	public BaseRevisionBranching(RevisionIndex index, TimestampProvider timestampProvider, ObjectMapper mapper) {
		this.index = index;
		this.timestampProvider = timestampProvider;
		this.mapper = mapper;
	}
	
	public long currentTime() {
		return timestampProvider.getTimestamp();
	}
	
	/**
	 * Reopens the branch with the same name and parent, on the parent head.
	 * 
	 * @return the reopened branch
	 */
	public final RevisionBranch reopen(final RevisionBranch parentBranch, final String name, final Metadata metadata) {
		return locked(parentBranch.getPath(), () -> doReopen(parentBranch, name, metadata));
	}

	private <T> T locked(final String lockPath, Callable<T> callable) {
		final ReentrantLock lock = locks.getUnchecked(lockPath);
		try {
			if (lock.tryLock(1L, TimeUnit.MINUTES)) {
				try {
					return callable.call();
				} finally {
					lock.unlock();
				}
			} else {
				throw new RequestTimeoutException();
			}
		} catch (RequestTimeoutException e) {
			throw e;
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	final void init() {
		RevisionBranch mainBranch = get(RevisionBranch.MAIN_PATH);
		if (mainBranch == null) {
			final long branchId = getMainBranchId();
			final long baseTimestamp = getMainBaseTimestamp();
			final long headTimestamp = getMainHeadTimestamp();
			RevisionBranch main = RevisionBranch.builder()
				.id(branchId)
				.parentPath("")
				.name(RevisionBranch.MAIN_PATH)
				.segments(ImmutableSortedSet.<RevisionSegment>naturalOrder()
						.add(new RevisionSegment(branchId, baseTimestamp, headTimestamp))
						.build())
				.build();
			commit(create(main));
		}
	}
	
	protected abstract long getMainHeadTimestamp();

	protected abstract long getMainBaseTimestamp();

	protected abstract long getMainBranchId();

	public RevisionBranch getBranch(String branchPath) {
		final RevisionBranch branch = get(branchPath);
		if (branch == null) {
			throw new NotFoundException("Branch", branchPath);
		}
		return branch;
	}
	
	/**
	 * Returns the revision branch for the given branchPath.
	 * 
	 * @param branchPath
	 * @return
	 */
	protected RevisionBranch get(String branchPath) {
		return index().read(searcher -> searcher.get(RevisionBranch.class, branchPath));
	}

	/**
	 * @return the raw index to access raw documents
	 */
	protected final Index index() {
		return index.index();
	}
	
	/**
	 * @return the revision index to access revision controlled documents
	 */
	protected final InternalRevisionIndex revisionIndex() {
		return (InternalRevisionIndex) index;
	}
	
	/**
	 * Search branches with the given query and return the results.
	 * @param query
	 * @return
	 */
	public Hits<RevisionBranch> search(Query<RevisionBranch> query) {
		return index().read(searcher -> searcher.search(query));
	}
	
	public <T> T commit(IndexWrite<T> changes) {
		return index().write(writer -> {
			T result = changes.execute(writer);
			writer.commit();
			return result;
		});
	}
	
	protected final String toAbsolutePath(final String parentPath, final String name) {
		return parentPath.concat(RevisionBranch.SEPARATOR).concat(name);
	}
	
	/**
	 * Creates a new child branch with the given name and metadata under the specified parent branch.
	 * 
	 * @param parent - the path of the parent branch where the new branch should be created, may not be <code>null</code>
	 * @param name
	 *            - the name of the new child branch, may not be <code>null</code>
	 * @param metadata
	 *            - optional metadata map
	 * @return
	 * @throws AlreadyExistsException
	 *             - if the child branch already exists
	 */
	public final String createBranch(final String parent, final String name, final Metadata metadata) {
		RevisionBranch parentBranch = getBranch(parent);
		if (parentBranch.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parentBranch.getPath());
		}
		final String path = toAbsolutePath(parent, name);
		RevisionBranch existingBranch = get(path);
		if (existingBranch != null && !existingBranch.isDeleted()) {
			// throw AlreadyExistsException if exists before trying to enter the sync block
			throw new AlreadyExistsException("Branch", path);
		} else {
			return create(parentBranch, name, metadata);
		}
	}
	
	private String create(final RevisionBranch parent, final String name, final Metadata metadata) {
		// prevents problematic branch creation from multiple threads, but allows them 
		// to respond back successfully if branch did not exist before creation and it does now
		final String parentPath = parent.getPath();
		return locked(parentPath, () -> {
			// check again and return if exists, otherwise open the child branch
			final RevisionBranch existingBranch = get(toAbsolutePath(parentPath, name));
			if (existingBranch != null && !existingBranch.isDeleted()) {
				return existingBranch.getPath();
			} else {
				final RevisionBranch createdBranch = doReopen(parent, name, metadata);
				sendChangeEvent(createdBranch.getPath()); // Explicit notification (creation)
				return createdBranch.getPath();
			}
		});
	}
	
	protected abstract RevisionBranch doReopen(RevisionBranch parent, String child, Metadata metadata);

	public final void delete(String branchPath) {
		if (RevisionBranch.MAIN_PATH.equals(branchPath)) {
			throw new BadRequestException("MAIN cannot be deleted");
		}
		for (RevisionBranch child : getChildren(branchPath)) {
			doDelete(child.getPath());
		}
		doDelete(branchPath);
	}
	
	private void doDelete(final String branchPath) {
		commit(update(branchPath, RevisionBranch.Scripts.WITH_DELETED, Collections.emptyMap()));
		sendChangeEvent(branchPath); // Explicit notification (delete)
	}
	
	public BranchState getBranchState(String branchPath) {
		return getBranchState(getBranch(branchPath));
	}
	
	public BranchState getBranchState(String branchPath, String compareWith) {
		return getBranch(branchPath).state(getBranch(compareWith));
	}
	
	public BranchState getBranchState(RevisionBranch branch) {
		if (RevisionBranch.MAIN_PATH.equals(branch.getPath())) {
			return BranchState.UP_TO_DATE;
		} else {
			return branch.state(getBranch(branch.getParentPath()));
		}
	}

	public Commit merge(String fromPath, String toPath, String commitMessage) {
		return merge(fromPath, toPath, commitMessage, new RevisionConflictProcessor.Default());
	}
	
	public Commit merge(String fromPath, String toPath, String commitMessage, RevisionConflictProcessor conflictProcessor) {
		return merge(fromPath, toPath, commitMessage, conflictProcessor, false);
	}
	
	/**
	 * Merges changes to the toPath branch by squashing the change set of the specified fromPath into a single commit.
	 * 
	 * @param fromPath - the branch to take changes from 
	 * @param toPath - the branch to push the changes to
	 * @param commitMessage
	 *            - the commit message
	 * @param squash 
	 * @return the commit object representing the merge commit or <code>null</code> if there is nothing to merge
	 */
	public Commit merge(String fromPath, String toPath, String commitMessage, RevisionConflictProcessor conflictProcessor, boolean squash) {
		if (toPath.equals(fromPath)) {
			throw new BadRequestException(String.format("Can't merge branch '%s' onto itself.", toPath));
		}
		RevisionBranch to = getBranch(toPath);
		RevisionBranch from = getBranch(fromPath);
		
		BranchState changesFromState = from.state(to);

		// do nothing if from state compared to toBranch is either UP_TO_DATE or BEHIND
		if (changesFromState == BranchState.UP_TO_DATE || changesFromState == BranchState.BEHIND) {
			return null;
		}
		
		final InternalRevisionIndex index = revisionIndex();
		final StagingArea staging = index.prepareCommit(to.getPath());
		
		// TODO add conflict processing
		long fastForwardCommitTimestamp = staging.merge(from.ref(), to.ref(), squash, conflictProcessor);
		// skip fast forward if the tobranch has a later commit than the returned fastForwardCommitTimestamp
		if (to.getHeadTimestamp() >= fastForwardCommitTimestamp) {
			fastForwardCommitTimestamp = -1L;
		}
		final boolean isFastForwardMerge = fastForwardCommitTimestamp != -1L && !squash;
		return staging.commit(isFastForwardMerge ? fastForwardCommitTimestamp : currentTime(), "TODO", commitMessage);
	}
	
	protected final IndexWrite<Void> update(final String path, final String script, final Map<String, Object> params) {
		return index -> {
			index.bulkUpdate(new BulkUpdate<>(RevisionBranch.class, DocumentMapping.matchId(path), DocumentMapping._ID, script, params));
			return null;
		};
	}
	
	protected final IndexWrite<RevisionBranch> create(final RevisionBranch branch) {
		return index -> {
			index.put(branch.getPath(), branch);
			return branch;
		};
	}
	
	/**
	 * Broadcast notifications of changed branches.
	 * 
	 * @param branchPath the subject of the notification (may not be {@code null})
	 */
	protected final void sendChangeEvent(final String branchPath) {
		onBranchChange.forEach(c -> c.accept(branchPath));
	}
	
	/**
	 * Listen on branch changes
	 * 
	 * @param onBranchChange - the listener to add
	 */
	public final void addBranchChangeListener(Consumer<String> onBranchChange) {
		if (!this.onBranchChange.contains(onBranchChange)) {
			this.onBranchChange.add(onBranchChange);
		}
	}
	
	/**
	 * Remove branch change listener
	 * 
	 * @param onBranchChange - the listener to remove
	 */
	public final void removeBranchChangeListener(Consumer<String> onBranchChange) {
		this.onBranchChange.remove(onBranchChange);
	}
	
	/**
	 * Returns all child branches created under the specified parent path (including transitively created branches).
	 * 
	 * @return a {@link Collection} of child {@link RevisionBranch} instances or an empty collection, never <code>null</code>.
	 */
	public final List<RevisionBranch> getChildren(String parentPath) {
		return ImmutableList.copyOf(search(Query.select(RevisionBranch.class)
				.where(Expressions.prefixMatch(RevisionBranch.Fields.PATH, parentPath + RevisionBranch.SEPARATOR))
				.limit(Integer.MAX_VALUE)
				.build()));
	}
	
	protected final RevisionBranch getBranchFromStore(final AfterWhereBuilder<RevisionBranch> query) {
		return Iterables.getOnlyElement(search(query.limit(1).build()), null);
	}
	
	protected final IndexWrite<Void> prepareReplace(final String path, final RevisionBranch value) {
		return update(path, RevisionBranch.Scripts.REPLACE, ImmutableMap.of("replace", mapper.convertValue(value, Map.class)));
	}

	
	/**
	 * Updates the branch with the specified properties. Currently {@link Metadata} supported only.
	 *
	 * @param branchPath - the branch to update with new metadata
	 * @param metadata - the metadata instance to set on the branch
	 */
	public final void updateMetadata(String branchPath, Metadata metadata) {
		commit(update(branchPath, RevisionBranch.Scripts.WITH_METADATA, ImmutableMap.of("metadata", metadata)));
	}
	
}
