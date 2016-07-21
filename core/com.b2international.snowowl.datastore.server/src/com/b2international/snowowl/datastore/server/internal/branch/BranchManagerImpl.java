/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal.branch;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.index.Index;
import com.b2international.index.IndexRead;
import com.b2international.index.IndexWrite;
import com.b2international.index.Searcher;
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
	
	protected final void initBranchStore(final InternalBranch main) {
    	try {
    		getMainBranch();
    	} catch (NotFoundException e) {
    		doInitBranchStore(main);
    	}
	}

	private void doInitBranchStore(InternalBranch main) {
		branchStore.admin().clear(Branch.class);
		registerBranch(main);
	}

	void registerBranch(final InternalBranch branch) {
		branch.setBranchManager(this);
		put(branch);
	}

	final InternalBranch createBranch(final InternalBranch parent, final String name, final Metadata metadata) {
		if (parent.isDeleted()) {
			throw new BadRequestException("Cannot create '%s' child branch under deleted '%s' parent.", name, parent.path());
		}
		final String path = parent.path().concat(Branch.SEPARATOR).concat(name);
		Branch existingBranch = getBranchFromStore(path);
		if (existingBranch != null) {
			// throw AlreadyExistsException if exists before trying to enter the sync block
			throw new AlreadyExistsException(Branch.class.getSimpleName(), path);
		} else {
			// prevents problematic branch creation from multiple threads, but allows them 
			// to respond back successfully if branch did not exist before creation and it does now
			final ReentrantLock lock = locks.getUnchecked(path);
			try {
				if (lock.tryLock(1L, TimeUnit.MINUTES)) {
					existingBranch = getBranchFromStore(path);
					if (existingBranch != null) {
						return (InternalBranch) existingBranch;
					} else {
						return sendChangeEvent(reopen(parent, name, metadata)); // Explicit notification (creation)
					}
				} else {
					throw new RequestTimeoutException();
				}
			} catch (InterruptedException e) {
				throw new SnowowlRuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
	}

	abstract InternalBranch reopen(InternalBranch parent, String name, Metadata metadata);

	@Override
	public Branch getMainBranch() {
		return getBranch(MainBranchImpl.MAIN_PATH);
	}

	@Override
	public Branch getBranch(final String path) {
		final Branch branch = getBranchFromStore(path);
		if (branch == null) {
			throw new NotFoundException(Branch.class.getSimpleName(), path);
		}
		return branch;
	}

	protected final Branch getBranchFromStore(final AfterWhereBuilder<InternalBranch> query) {
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
	public Collection<? extends Branch> getBranches() {
		final Collection<InternalBranch> values = search(Query.select(InternalBranch.class).where(Expressions.matchAll()).limit(Integer.MAX_VALUE).build());
		initialize(values);
		return values;
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
			sendChangeEvent(reopenedFrom); // Explicit notification (reopen)
		}
		return mergedTo;
	}

	InternalBranch rebase(final InternalBranch branch, final InternalBranch onTopOf, final String commitMessage, final Runnable postReopen) {
		applyChangeSet(branch, onTopOf, true, true, commitMessage);
		final InternalBranch rebasedBranch = reopen(onTopOf, branch.name(), branch.metadata());
		postReopen.run();
		
		if (branch.headTimestamp() > branch.baseTimestamp()) {
			return applyChangeSet(branch, rebasedBranch, false, true, commitMessage); // Implicit notification (reopen & commit)
		} else {
			return sendChangeEvent(rebasedBranch); // Explicit notification (reopen)
		}
	}

	abstract InternalBranch applyChangeSet(InternalBranch from, InternalBranch to, boolean dryRun, boolean isRebase, String commitMessage);

	/*package*/ final InternalBranch delete(final InternalBranch branchImpl) {
		for (Branch child : branchImpl.children()) {
			doDelete((InternalBranch) child);
		}
		return doDelete(branchImpl);
	}

	private InternalBranch doDelete(final InternalBranch branchImpl) {
		final InternalBranch deleted = branchImpl.withDeleted();
		put(deleted);
		return sendChangeEvent(deleted); // Explicit notification (delete)
	}
	
	/*package*/ final InternalBranch handleCommit(final InternalBranch branch, final long timestamp) {
		final InternalBranch branchAfterCommit = branch.withHeadTimestamp(timestamp);
		registerBranch(branchAfterCommit);
		return sendChangeEvent(branchAfterCommit); // Explicit notification (commit)
	}

	/**
	 * Subclasses should override this method if they want to broadcast notifications of changed branches.
	 * @param branch the subject of the notification (may not be {@code null})
	 * @return {@code branch} (for convenience)
	 */
	@OverridingMethodsMustInvokeSuper
	InternalBranch sendChangeEvent(final InternalBranch branch) {
		return branch;
	}

	/*package*/ final Collection<? extends Branch> getChildren(BranchImpl branchImpl) {
		final Collection<InternalBranch> values = search(Query.select(InternalBranch.class).where(Expressions.prefixMatch(DocumentMapping._ID, branchImpl.path() + Branch.SEPARATOR)).limit(Integer.MAX_VALUE).build());
		initialize(values);
		return values;
	}

	private Collection<InternalBranch> search(final Query<InternalBranch> query) {
		return ImmutableList.copyOf(branchStore.read(new IndexRead<Iterable<InternalBranch>>() {
			@Override
			public Iterable<InternalBranch> execute(Searcher index) throws IOException {
				return index.search(query);
			}
		}));
	}
	
	private InternalBranch get(final String path) {
		return branchStore.read(new IndexRead<InternalBranch>() {
			@Override
			public InternalBranch execute(Searcher index) throws IOException {
				return index.get(InternalBranch.class, path);
			}
		});
	}
	
	private void put(final InternalBranch branch) {
		branchStore.write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.put(branch.path(), branch);
				index.commit();
				return null;
			}
		});
	}
}
