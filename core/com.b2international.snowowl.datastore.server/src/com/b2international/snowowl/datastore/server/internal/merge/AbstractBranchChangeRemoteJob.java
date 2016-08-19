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
package com.b2international.snowowl.datastore.server.internal.merge;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;

import com.b2international.commons.status.Statuses;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.core.exceptions.ApiException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * @since 4.6
 */
public abstract class AbstractBranchChangeRemoteJob extends Job {

	public static AbstractBranchChangeRemoteJob create(Repository repository, String source, String target, String commitMessage, String reviewId) {
		final IBranchPath sourcePath = BranchPathUtils.createPath(source);
		final IBranchPath targetPath = BranchPathUtils.createPath(target);
		
		if (sourcePath.getParent().equals(targetPath)) {
			return new BranchMergeJob(repository, source, target, commitMessage, reviewId);
		} else if (targetPath.getParent().equals(sourcePath)) {
			return new BranchRebaseJob(repository, source, target, commitMessage, reviewId);
		} else {
			throw new BadRequestException("Branches '%s' and '%s' can only be merged or rebased if one branch is the direct parent of the other.", source, target);
		}
	}

	protected MergeImpl merge;
	
	protected Repository repository;
	protected String commitComment;
	protected String reviewId;

	protected AbstractBranchChangeRemoteJob(final Repository repository, final String sourcePath, final String targetPath, final String commitComment, final String reviewId) {
		super(commitComment);
		
		this.repository = repository;
		this.merge = new MergeImpl(sourcePath, targetPath);
		this.commitComment = commitComment;
		this.reviewId = reviewId;
		
		setSystem(true);
		
		// TODO: Make reasoner remote job rule conflicting and add it as additional items below 
		setRule(MultiRule.combine(
				new BranchChangeRemoteJobKey(repository.id(), sourcePath), 
				new BranchChangeRemoteJobKey(repository.id(), targetPath)));
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		merge.start();
		try {
			applyChanges();
			merge.completed();
		} catch (ApiException e) {
			merge.failed(e.toApiError());
		} catch (RuntimeException e) {
			merge.failed(ApiError.Builder.of(e.getMessage()).build());
		}
		
		return Statuses.ok();
	}
	
	protected abstract void applyChanges();

	@Override
	protected void canceling() {
		merge.cancelRequested();
		super.canceling();
	}

	public Merge getMerge() {
		return merge;
	}
}
