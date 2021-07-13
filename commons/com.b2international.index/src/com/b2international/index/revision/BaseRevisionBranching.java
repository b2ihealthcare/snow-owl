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

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @since 6.5
 */
public abstract class BaseRevisionBranching {

	private final RevisionIndex index;
	private final TimestampProvider timestampProvider;
	private final List<Consumer<String>> onBranchChange = newArrayListWithCapacity(1);
	
	private final LoadingCache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
			.expireAfterAccess(5L, TimeUnit.MINUTES)
			.build(new CacheLoader<String, ReentrantLock>() {
				@Override
				public ReentrantLock load(String key) throws Exception {
					return new ReentrantLock();
				}
			});

	public BaseRevisionBranching(RevisionIndex index, TimestampProvider timestampProvider) {
		this.index = index;
		this.timestampProvider = timestampProvider;
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
				throw new RequestTimeoutException("Couldn't lock path '%s' in 1 minute.", lockPath);
			}
		} catch (ApiException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void init() {
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
	
	public RevisionBranch getBranch(long branchId) {
		return index().read(searcher -> searcher.search(Query.select(RevisionBranch.class)
				.where(Expressions.exactMatch(RevisionBranch.Fields.ID, branchId))
				.limit(1)
				.build())
				.stream()
				.findFirst()
				.orElseThrow(() -> new NotFoundException("RevisionBranch", Long.toString(branchId))));
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
	
	/**
	 * Returns <code>true</code> if the given branch has any actual changes.
	 * An actual change is a commit that has been made on the given branch between the given timeframe (from -> to) and it is not a fast forward merge commit from the given mergeBranch.
	 *     
	 * @param branch
	 * @param from
	 * @param to
	 * @param mergeBranch
	 * @return
	 */
	protected final boolean hasChanges(Set<String> branches, long from, long to, long mergeBranch) {
		return index().read(searcher -> searcher.search(Query.select(Commit.class)
				.where(
					Expressions.builder()
						.filter(Commit.Expressions.branches(branches))
						.filter(Commit.Expressions.timestampRange(from, to))
						.mustNot(Commit.Expressions.mergeFrom(mergeBranch, from, to, false))
					.build()
				)
				.limit(0).build()))
				.getTotal() > 0;
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
		return getBranchState(getBranch(branchPath), getBranch(compareWith));
	}
	
	public BranchState getBranchState(RevisionBranch branch) {
		if (RevisionBranch.MAIN_PATH.equals(branch.getPath())) {
			return BranchState.UP_TO_DATE;
		} else {
			return getBranchState(branch, getBranch(branch.getParentPath()));
		}
	}

	/**
	 * Returns the {@link BranchState} of the left {@link RevisionBranch} argument compared to
	 * the given right {@link RevisionBranch} argument.
	 * 
	 * <ul>
	 *  <li>FORWARD: no commits on right branch since base timestamp, commits on left branch since base timestamp
	 * <pre>
	 *              b    h
	 * left         o----&#x25CF;
	 * right ----&#x25CF;
	 *            h
	 * </pre>
	 * </li>
	 * <li>BEHIND: no commits on left branch since base timestamp, commits on right
	 * branch since base timestamp
	 * <pre>
	 *             b = h
	 * left         o&#x25CF;
	 * right -------------&#x25CF;
	 *                     h
	 * </pre>
	 * </li> 
	 * <li>DIVERGED: commits on both branches since base timestamp
	 * <pre>
	 *              b    h
	 * left         o----&#x25CF;
	 * right -------------&#x25CF;
	 *                     h
	 * </pre>
	 * </li> 
	 * <li>UP_TO_DATE: no commits on either branch since base timestamp
	 * <pre>
	 *             b = h
	 * left         o&#x25CF;
	 * right ------&#x25CF;
	 *              h
	 * </pre>
	 * </li>
	 * </ul>
	 * <p>
	 * Branch base and head timestamps gathered from left branch are adjusted before
	 * doing the comparison, according to the following rules:
	 * <ul>
	 * <li>If the right branch has been merged into left branch, the most recent of
	 * such points is used as the base timestamp:
	 * <pre>
	 *              b   b'   h
	 * left         o---o----&#x25CF;
	 *                 /
	 * right --------&#x25CF;------&#x25CF;
	 *                ms     h
	 * </pre>
	 * </li>
	 * <li>If left branch was merged into the right branch, the most recent of such
	 * points is used as:
	 * <ul>
	 * <li>the base timestamp, if it is greater than the currently held base
	 * timestamp (pictured)</li>
	 * <li>the head timestamp, it it is greater than the currently held head
	 * timestamp</li>
	 * </ul>
	 * <pre>
	 *              b  ms = b' h
	 * left         o---&#x25CF; o----&#x25CF;
	 *                   \|
	 * right ------------&#x25CF;----&#x25CF;
	 *                         h
	 * </pre>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public final BranchState getBranchState(RevisionBranch left, RevisionBranch right) {
		final RevisionBranchRef leftDiff = left.ref().difference(right.ref());
		final RevisionBranchRef rightDiff = right.ref().difference(left.ref());
    	long leftBaseTimestamp = leftDiff.segments().isEmpty() ? left.getBaseTimestamp() : leftDiff.segments().first().start();
    	long leftHeadTimestamp = leftDiff.segments().isEmpty() ? left.getHeadTimestamp() : leftDiff.segments().last().end();
    	long rightBaseTimestamp = rightDiff.segments().isEmpty() ? right.getBaseTimestamp() : rightDiff.segments().first().start();
    	long rightHeadTimestamp = rightDiff.segments().isEmpty() ? right.getHeadTimestamp() : rightDiff.segments().last().end();
    	
    	final RevisionBranchPoint latestMergeOfRight = left.getLatestMergeSource(right.getId(), true);
    	if (latestMergeOfRight != null && latestMergeOfRight.getTimestamp() > rightBaseTimestamp) {
   			rightBaseTimestamp = latestMergeOfRight.getTimestamp();
    	}
    	
    	final RevisionBranchPoint latestMergeOfLeft = right.getLatestMergeSource(left.getId(), true);
    	if (latestMergeOfLeft != null) {
    		if (latestMergeOfLeft.getTimestamp() > leftBaseTimestamp) {
    			leftBaseTimestamp = latestMergeOfLeft.getTimestamp();
    		}
    		if (latestMergeOfLeft.getTimestamp() > leftHeadTimestamp) {
    			leftHeadTimestamp = latestMergeOfLeft.getTimestamp();
    		}
    	}
    	
    	if (leftHeadTimestamp > leftBaseTimestamp && rightHeadTimestamp <= rightBaseTimestamp) {
    		final Set<String> branchesToCheckForChanges = Sets.newHashSet(left.getPath());
    		if (!leftDiff.segments().isEmpty()) {
    			leftDiff.segments().stream()
					.map(RevisionSegment::branchId)
					.filter(branchId -> left.getId() != branchId)
					.map(this::getBranch)
					.map(RevisionBranch::getPath)
					.forEach(branchesToCheckForChanges::add);
    		}
    		if (hasChanges(branchesToCheckForChanges, leftBaseTimestamp, leftHeadTimestamp, right.getId())) {
    			return BranchState.FORWARD;
    		} else {
    			return BranchState.UP_TO_DATE;
    		}
        } else if (leftHeadTimestamp <= leftBaseTimestamp && rightHeadTimestamp > rightBaseTimestamp) {
        	final Set<String> branchesToCheckForChanges = Sets.newHashSet(right.getPath());
    		if (!rightDiff.segments().isEmpty()) {
    			rightDiff.segments().stream()
					.map(RevisionSegment::branchId)
					.filter(branchId -> right.getId() != branchId)
					.map(this::getBranch)
					.map(RevisionBranch::getPath)
					.forEach(branchesToCheckForChanges::add);
    		}
        	if (hasChanges(branchesToCheckForChanges, rightBaseTimestamp, rightHeadTimestamp, left.getId())) {
    			return BranchState.BEHIND;
    		} else {
    			return BranchState.UP_TO_DATE;
    		}
        } else if (leftHeadTimestamp > leftBaseTimestamp && rightHeadTimestamp > rightBaseTimestamp) {
        	return BranchState.DIVERGED;
        } else {
    	    return BranchState.UP_TO_DATE;
        }
	}

	/**
	 * Prepares a merge operation with the given fromPath and toPath arguments.
	 * 
	 * @param fromPath - the branch to take changes from 
	 * @param toPath - the branch to push the changes to
	 * @return {@link BranchMergeOperation} to actually perform the merge or configure it even further
	 */
	public BranchMergeOperation prepareMerge(String fromPath, String toPath) {
		return new BranchMergeOperation(this, fromPath, toPath);
	}
	
	Commit doMerge(BranchMergeOperation operation) {
		String source = operation.fromPath;
		String target = operation.toPath;
		if (target.equals(source)) {
			throw new BadRequestException(String.format("Can't merge branch '%s' onto itself.", target));
		}

		RevisionBranch from = getBranch(source);
		RevisionBranch to = getBranch(target);
		
		BranchState changesFromState = getBranchState(from, to);

		// do nothing if from state compared to toBranch is either UP_TO_DATE or BEHIND
		if (changesFromState == BranchState.UP_TO_DATE || changesFromState == BranchState.BEHIND) {
			return null;
		}
		
		final InternalRevisionIndex index = revisionIndex();
		final StagingArea staging = index.prepareCommit(to.getPath()).withContext(operation.context);
		
		// apply changes from source ref
		staging.merge(from.ref(), to.ref(), operation.squash, operation.conflictProcessor, operation.exclusions);
		
		// commit changes to index
		final String commitMessage = !Strings.isNullOrEmpty(operation.commitMessage) ? operation.commitMessage : String.format("Merge %s into %s", source, target);
		return staging.commit(currentTime(), operation.author, commitMessage);
	}
	
	protected final IndexWrite<Void> update(final String path, final String script, final Map<String, Object> params) {
		return index -> {
			index.bulkUpdate(new BulkUpdate<>(RevisionBranch.class, Expressions.exactMatch(RevisionBranch.Fields.PATH, path), script, params));
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
