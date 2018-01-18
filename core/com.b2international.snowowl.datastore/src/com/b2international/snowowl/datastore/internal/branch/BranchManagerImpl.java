/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.b2international.index.BulkUpdate;
import com.b2international.index.DocSearcher;
import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Writer;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.exceptions.RequestTimeoutException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 4.1
 */
public abstract class BranchManagerImpl implements BranchManager {

	private final Index branchStore;
	
	private final LoadingCache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
			.expireAfterAccess(5L, TimeUnit.MINUTES)
			.build(new CacheLoader<String, ReentrantLock>() {
				@Override
				public ReentrantLock load(String key) throws Exception {
					return new ReentrantLock();
				}
			});

	public BranchManagerImpl(final Index branchStore) {
		this.branchStore = branchStore;
	}
	
	protected final void initBranchStore(final MainBranchImpl main) {
    	try {
    		getMainBranch();
    	} catch (NotFoundException e) {
    		doInitBranchStore(main);
    	}
	}

	private void doInitBranchStore(MainBranchImpl main) {
		branchStore.admin().clear(Branch.class);
		main.setBranchManager(this);
		commit(create(main));
	}

	final InternalBranch createBranch(final InternalBranch parent, final String name, final Metadata metadata) {
		if (parent.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parent.path());
		}
		final String path = toAbsolutePath(parent.path(), name);
		Branch existingBranch = getBranchFromStore(path);
		if (existingBranch != null && !existingBranch.isDeleted()) {
			// throw AlreadyExistsException if exists before trying to enter the sync block
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		} else {
			return create(parent, name, metadata);
		}
	}
	
	private InternalBranch create(final InternalBranch parent, final String name, final Metadata metadata) {
		// prevents problematic branch creation from multiple threads, but allows them 
		// to respond back successfully if branch did not exist before creation and it does now
		final String parentPath = parent.path();
		return locked(parentPath, new Callable<InternalBranch>() {
			@Override
			public InternalBranch call() throws Exception {
				// check again and return if exists, otherwise open the child branch
				final Branch existingBranch = getBranchFromStore(toAbsolutePath(parentPath, name));
				if (existingBranch != null && !existingBranch.isDeleted()) {
					return (InternalBranch) existingBranch;
				} else {
					final InternalBranch createdBranch = doReopen(parent, name, metadata);
					sendChangeEvent(createdBranch.path()); // Explicit notification (creation)
					return createdBranch;
				}
			}
		});
	}
	
	final InternalBranch reopen(final InternalBranch parent, final String name, final Metadata metadata) {
		return locked(parent.path(), new Callable<InternalBranch>() {
			@Override
			public InternalBranch call() throws Exception {
				return doReopen(parent, name, metadata);
			}
		});
	}
	
	private InternalBranch locked(final String lockPath, Callable<InternalBranch> callable) {
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
		} catch (Exception e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	protected final String toAbsolutePath(final String parentPath, final String name) {
		return parentPath.concat(Branch.SEPARATOR).concat(name);
	}

	protected abstract InternalBranch doReopen(InternalBranch parent, String name, Metadata metadata);

	@Override
	public final Branch getMainBranch() {
		return getBranch(MainBranchImpl.MAIN_PATH);
	}

	@Override
	public final Branch getBranch(final String path) {
		final Branch branch = getBranchFromStore(path);
		if (branch == null) {
			throw new NotFoundException(Branch.class.getSimpleName(), path);
		}
		return branch;
	}

	protected final Branch getBranchFromStore(final AfterWhereBuilder<BranchDocument> query) {
		query.limit(1);
		final InternalBranch branch = Iterables.getOnlyElement(search(query.build()), null);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}
	
	private final Branch getBranchFromStore(final String path) {
		final InternalBranch branch = get(path);
		if (branch != null) {
			branch.setBranchManager(this);
		}
		return branch;
	}

	@Override
	public final Collection<Branch> getBranches() {
		final Collection<InternalBranch> values = search(Query.select(BranchDocument.class)
				.where(Expressions.matchAll())
				.limit(Integer.MAX_VALUE)
				.build());
		initialize(values);
		return ImmutableList.copyOf(values);
	}

	private void initialize(final Collection<InternalBranch> values) {
		for (final InternalBranch branch : values) {
			branch.setBranchManager(this);
		}
	}

	final InternalBranch merge(final InternalBranch from, final InternalBranch to, final String commitMessage) {
		final InternalBranch mergedTo = applyChangeSet(from, to, false, false, commitMessage); // Implicit notification (commit)
		// reopen only if the to branch is a direct parent of the from branch, otherwise these are unrelated branches 
		if (from.parent().equals(mergedTo)) {
			final InternalBranch reopenedFrom = reopen(mergedTo, from.name(), from.metadata());
			sendChangeEvent(reopenedFrom.path()); // Explicit notification (reopen)
			return reopenedFrom;
		}
		return mergedTo;
	}

	public InternalBranch rebase(final InternalBranch branch, final InternalBranch onTopOf, final String commitMessage, final Runnable postReopen) {
		applyChangeSet(branch, onTopOf, true, true, commitMessage);
		final InternalBranch rebasedBranch = reopen(onTopOf, branch.name(), branch.metadata());
		postReopen.run();
		
		if (branch.headTimestamp() > branch.baseTimestamp()) {
			return applyChangeSet(branch, rebasedBranch, false, true, commitMessage); // Implicit notification (reopen & commit)
		} else {
			sendChangeEvent(rebasedBranch.path()); // Explicit notification (reopen)
			return rebasedBranch;
		}
	}

	protected abstract InternalBranch applyChangeSet(InternalBranch from, InternalBranch to, boolean dryRun, boolean isRebase, String commitMessage);

	/*package*/ final InternalBranch delete(final InternalBranch branchImpl) {
		for (Branch child : branchImpl.children()) {
			doDelete((InternalBranch) child);
		}
		return doDelete(branchImpl);
	}

	private InternalBranch doDelete(final InternalBranch branch) {
		final String path = branch.path();
		commit(update(path, BranchDocument.Scripts.WITH_DELETED, Collections.emptyMap()));
		sendChangeEvent(path); // Explicit notification (delete)
		return (InternalBranch) getBranch(path);
	}
	
	public final InternalBranch handleCommit(final InternalBranch branch, final long timestamp) {
		final String path = branch.path();
		commit(update(path, BranchDocument.Scripts.WITH_HEADTIMESTAMP, ImmutableMap.of("headTimestamp", timestamp)));
		sendChangeEvent(path); // Explicit notification (commit)
		return (InternalBranch) getBranch(path);
	}

	/**
	 * Subclasses should override this method if they want to broadcast notifications of changed branches.
	 * @param branchPath the subject of the notification (may not be {@code null})
	 * @return {@code branch} (for convenience)
	 */
	void sendChangeEvent(final String branchPath) {
	}

	/*package*/ final Collection<Branch> getChildren(BranchImpl branchImpl) {
		final Collection<InternalBranch> values = search(Query.select(BranchDocument.class)
				.where(Expressions.prefixMatch("path", branchImpl.path() + Branch.SEPARATOR))
				.limit(Integer.MAX_VALUE)
				.build());
		initialize(values);
		return ImmutableList.copyOf(values);
	}

	private Collection<InternalBranch> search(final Query<BranchDocument> query) {
		return ImmutableList.copyOf(branchStore.read(new IndexRead<Iterable<InternalBranch>>() {
			@Override
			public Iterable<InternalBranch> execute(DocSearcher index) throws IOException {
				return index.search(query).stream().map(BranchDocument::toBranch).collect(Collectors.toList());
			}
		}));
	}
	
	private InternalBranch get(final String path) {
		return branchStore.read(new IndexRead<InternalBranch>() {
			@Override
			public InternalBranch execute(DocSearcher index) throws IOException {
				BranchDocument doc = index.get(BranchDocument.class, path);
				return doc == null ? null : doc.toBranch();
			}
		});
	}
	
	public final <T> T commit(final IndexWrite<T> changes) {
		return branchStore.write(new IndexWrite<T>() {
			@Override
			public T execute(Writer index) throws IOException {
				final T result = changes.execute(index);
				index.commit();
				return result;
			}
		});
	}
	
	public final IndexWrite<Void> update(final String path, final String script, final Map<String, Object> params) {
		return new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.bulkUpdate(new BulkUpdate<>(BranchDocument.class, DocumentMapping.matchId(path), DocumentMapping._ID, script, params));
				return null;
			}
		};
	}
	
	public final IndexWrite<InternalBranch> create(final InternalBranch branch) {
		return new IndexWrite<InternalBranch>() {
			@Override
			public InternalBranch execute(Writer index) throws IOException {
				index.put(branch.path(), branch.toDocument().build());
				branch.setBranchManager(BranchManagerImpl.this);
				return branch;
			}
		};
	}
	
}
