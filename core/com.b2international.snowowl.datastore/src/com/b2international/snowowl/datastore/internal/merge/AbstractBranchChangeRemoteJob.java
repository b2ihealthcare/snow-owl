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
package com.b2international.snowowl.datastore.internal.merge;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.slf4j.Logger;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.status.Statuses;
import com.b2international.index.revision.BranchMergeConflictException;
import com.b2international.index.revision.ChangedInSourceAndTargetConflict;
import com.b2international.index.revision.Conflict;
import com.b2international.index.revision.ObjectId;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.merge.MergeConflictImpl;
import com.b2international.snowowl.core.merge.MergeImpl;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.remotejobs.BranchExclusiveRule;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

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
		} catch (BranchMergeConflictException e) {
			merge.getAndUpdate(m -> m.failedWithConflicts(toMergeConflicts(e.getConflicts())));
		} catch (ApiException e) {
			merge.getAndUpdate(m -> m.failed(e.toApiError()));
		} catch (RuntimeException e) {
			repository.service(Logger.class).error(e.getMessage(), e);
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
	
	private Collection<MergeConflict> toMergeConflicts(List<Conflict> conflicts) {
		final Multimap<ObjectId, Conflict> conflictsByObjectId = Multimaps.index(conflicts, Conflict::getObjectId);
		
		return conflictsByObjectId.keySet().stream().map(objectId -> toMergeConflict(objectId, conflictsByObjectId.get(objectId))).collect(Collectors.toList());
	}
	
	private MergeConflict toMergeConflict(ObjectId objectId, Collection<Conflict> conflicts) {
		
		final MergeConflictImpl.Builder conflict = MergeConflictImpl.builder()
			.componentId(objectId.id())
			.componentType(objectId.type());
		
		final List<ConflictingAttribute> conflictingAttributes = conflicts.stream()
			.filter(ChangedInSourceAndTargetConflict.class::isInstance)
			.map(ChangedInSourceAndTargetConflict.class::cast)
			.map(changedInSourceAndTargetConflict -> ConflictingAttributeImpl.builder()
					.property(changedInSourceAndTargetConflict.getSourceChange().getProperty())
					.oldValue(changedInSourceAndTargetConflict.getSourceChange().getOldValue())
					.value(changedInSourceAndTargetConflict.getSourceChange().getNewValue())
					.build())
			.collect(Collectors.toList());
		
		if (!conflictingAttributes.isEmpty()) {
			 return conflict.type(ConflictType.CONFLICTING_CHANGE).conflictingAttributes(conflictingAttributes).build();
		} else {
			throw new UnsupportedOperationException("TODO implement");
		}
	}
	
}
