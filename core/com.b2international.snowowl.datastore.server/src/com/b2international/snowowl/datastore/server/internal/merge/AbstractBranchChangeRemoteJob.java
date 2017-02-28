/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal.merge;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;

import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.core.exceptions.MergeConflictException;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.remotejobs.BranchExclusiveRule;

/**
 * @since 4.6
 */
public abstract class AbstractBranchChangeRemoteJob extends Job {

	public static AbstractBranchChangeRemoteJob create(Repository repository, String source, String target, String commitMessage, String reviewId) {
		final IBranchPath sourcePath = BranchPathUtils.createPath(source);
		final IBranchPath targetPath = BranchPathUtils.createPath(target);
		
		if (targetPath.getParent().equals(sourcePath)) {
			return new BranchRebaseJob(repository, source, target, commitMessage, reviewId);
		} else {
			return new BranchMergeJob(repository, source, target, commitMessage, reviewId);
		}
	}

	private final AtomicReference<Merge> merge = new AtomicReference<>();
	
	protected Repository repository;
	protected String commitComment;
	protected String reviewId;

	protected AbstractBranchChangeRemoteJob(final Repository repository, final String sourcePath, final String targetPath, final String commitComment, final String reviewId) {
		super(commitComment);
		
		this.repository = repository;
		this.commitComment = commitComment;
		this.reviewId = reviewId;
		
		merge.set(MergeImpl.builder(sourcePath, targetPath).build());
		
		setSystem(true);
		
		setRule(MultiRule.combine(
				new BranchExclusiveRule(repository.id(), BranchPathUtils.createPath(sourcePath)), 
				new BranchExclusiveRule(repository.id(), BranchPathUtils.createPath(targetPath))));
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		merge.getAndUpdate(m -> m.start());
		
		try {
			applyChanges();
			merge.getAndUpdate(m -> m.completed());
		} catch (MergeConflictException e) {
			merge.getAndUpdate(m -> m.failedWithConflicts(e.getConflicts(), e.toApiError()));
		} catch (ApiException e) {
			merge.getAndUpdate(m -> m.failed(e.toApiError()));
		} catch (RuntimeException e) {
			merge.getAndUpdate(m -> m.failed(ApiError.Builder.of(e.getMessage()).build()));
		}
		
		return Statuses.ok();
	}
	
	protected abstract void applyChanges();

	@Override
	protected void canceling() {
		merge.getAndUpdate(m -> m.cancelRequested());
		super.canceling();
	}

	public Merge getMerge() {
		return merge.get();
	}
}
