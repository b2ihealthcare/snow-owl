/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.migrate;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 5.11
 */
public final class AsyncReplicationContext implements CDOReplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger("migrate");
	
	private static final int NUMBER_OF_COMMITS_IN_QUEUE = 20;
	
	private final CDOReplicationContext delegate;
	private final ThreadPoolExecutor commitProcessor;

	public AsyncReplicationContext(CDOReplicationContext delegate) {
		this.delegate = delegate;
		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(NUMBER_OF_COMMITS_IN_QUEUE) {
			@Override
			public boolean offer(Runnable e) {
				try {
					put(e);
					return true;
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
				return false;
			}
		};
		this.commitProcessor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);
	}
	
	@Override
	public int getLastReplicatedBranchID() {
		return delegate.getLastReplicatedBranchID();
	}

	@Override
	public long getLastReplicatedCommitTime() {
		return delegate.getLastReplicatedCommitTime();
	}

	@Override
	public String[] getLockAreaIDs() {
		return delegate.getLockAreaIDs();
	}

	@Override
	public void handleBranch(CDOBranch branch) {
		delegate.handleBranch(branch);
	}

	@Override
	public void handleCommitInfo(CDOCommitInfo commitInfo) {
		// handle commits on a separate thread to let producer load the next batch earlier
		commitProcessor.submit(() -> delegate.handleCommitInfo(commitInfo));
		LOGGER.info("Submitted commit: " + commitInfo.getComment() + " at " + commitInfo.getBranch().getName() + "@" + commitInfo.getTimeStamp());
		LOGGER.info("Awaiting commits {}/{}", commitProcessor.getQueue().size(), NUMBER_OF_COMMITS_IN_QUEUE);
	}

	@Override
	public boolean handleLockArea(LockArea area) {
		return delegate.handleLockArea(area);
	}
	
	public void await(long timeout, TimeUnit unit) {
		try {
			commitProcessor.shutdown();
			commitProcessor.awaitTermination(timeout, unit);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}