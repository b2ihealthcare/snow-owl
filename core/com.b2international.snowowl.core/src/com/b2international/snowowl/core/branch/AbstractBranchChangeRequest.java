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
package com.b2international.snowowl.core.branch;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiException;
import com.b2international.commons.exceptions.ConflictException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.index.revision.AddedInSourceAndTargetConflict;
import com.b2international.index.revision.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.index.revision.BranchMergeConflictException;
import com.b2international.index.revision.ChangedInSourceAndDetachedInTargetConflict;
import com.b2international.index.revision.ChangedInSourceAndTargetConflict;
import com.b2international.index.revision.Conflict;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.branch.review.BranchState;
import com.b2international.snowowl.core.branch.review.Review;
import com.b2international.snowowl.core.branch.review.ReviewManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.ConflictingAttributeImpl;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.6
 */
public abstract class AbstractBranchChangeRequest implements Request<RepositoryContext, Merge>, RepositoryAccessControl {

	@JsonProperty
	@NotEmpty
	protected final String sourcePath;
	
	@JsonProperty
	@NotEmpty
	protected final String targetPath;
	
	@JsonProperty
	private final String userId;
	
	@JsonProperty
	@NotEmpty
	protected final String commitMessage;
	
	@JsonProperty
	protected final String reviewId;
	
	@JsonProperty
	protected final String parentLockContext;

	protected AbstractBranchChangeRequest(String sourcePath, String targetPath, String userId, String commitMessage, String reviewId, String parentLockContext) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.userId = userId;
		this.commitMessage = commitMessage;
		this.reviewId = reviewId;
		this.parentLockContext = parentLockContext;
	}

	@Override
	public Merge execute(RepositoryContext context) {
		
		try {
			final Branch source = RepositoryRequests.branching().prepareGet(sourcePath).build().execute(context);
			final Branch target = RepositoryRequests.branching().prepareGet(targetPath).build().execute(context);
			
			if (reviewId != null) {
				final ReviewManager reviewManager = context.service(ReviewManager.class);
				final Review review = reviewManager.getReview(reviewId);
				final BranchState sourceState = review.source();
				final BranchState targetState = review.target();
				
				if (!sourceState.matches(source)) {
					throw new ConflictException("Source branch '%s' did not match with stored state on review identifier '%s'.", source.path(), reviewId);
				}
				
				if (!targetState.matches(target)) {
					throw new ConflictException("Target branch '%s' did not match with stored state on review identifier '%s'.", target.path(), reviewId);
				}
			}
			
			Merge merge = Merge.builder()
					.source(sourcePath)
					.target(targetPath)
					.build();
			
			try {
				applyChanges(context, source, target);
				return merge.completed();
			} catch (BranchMergeConflictException e) {
				return merge.failedWithConflicts(e.getMessage(), toMergeConflicts(e.getConflicts()));
			} catch (ApiException e) {
				return merge.failed(e.toApiError());
			} catch (RuntimeException e) {
				context.log().error("Failed to merge {} into {}", sourcePath, targetPath, e);
				return merge.failed(ApiError.of(e.getMessage()));
			}
			
		} catch (NotFoundException e) {
			throw e.toBadRequestException();
		}
	}
	
	protected final String userId(RepositoryContext context) {
		return !Strings.isNullOrEmpty(userId) ? userId : context.service(User.class).getUsername();
	}

	protected abstract void applyChanges(RepositoryContext context, Branch source, Branch target);
	
	private Collection<MergeConflict> toMergeConflicts(List<Conflict> conflicts) {
		final Multimap<ObjectId, Conflict> conflictsByObjectId = Multimaps.index(conflicts, Conflict::getObjectId);
		
		return conflictsByObjectId.keySet().stream().map(objectId -> toMergeConflict(objectId, conflictsByObjectId.get(objectId))).collect(Collectors.toList());
	}
	
	private MergeConflict toMergeConflict(ObjectId objectId, Collection<Conflict> conflicts) {
		final MergeConflict.Builder conflict = MergeConflict.builder()
			.componentId(objectId.id())
			.componentType(objectId.type())
			.type(ConflictType.CONFLICTING_CHANGE);
		
		final List<ConflictingAttribute> conflictingAttributes = conflicts.stream()
			.filter(ChangedInSourceAndTargetConflict.class::isInstance)
			.map(ChangedInSourceAndTargetConflict.class::cast)
			.map(ChangedInSourceAndTargetConflict::getSourceChange)
			.map(this::toConflictingAttribute)
			.collect(Collectors.toList());
		
		if (!conflictingAttributes.isEmpty()) {
			 return conflict.conflictingAttributes(conflictingAttributes).build();
		} else {
			// XXX multiple conflicts are not expected here for a single object
			Conflict c = Iterables.getFirst(conflicts, null);
			if (c instanceof AddedInSourceAndTargetConflict) {
				return conflict
						.message(c.getMessage())
						.conflictingAttribute(ConflictingAttributeImpl.builder().property(Revision.Fields.ID).build())
						.build();
			} else if (c instanceof ChangedInSourceAndDetachedInTargetConflict) {
				return conflict
						.message(c.getMessage())
						.type(ConflictType.DELETED_WHILE_CHANGED)
						.conflictingAttributes(
							((ChangedInSourceAndDetachedInTargetConflict) c).getChanges()
								.stream()
								.map(this::toConflictingAttribute)
								.collect(Collectors.toList())
						)
						.build();
			} else if (c instanceof AddedInSourceAndDetachedInTargetConflict) {
				return conflict.message(c.getMessage())
						.type(ConflictType.CAUSES_MISSING_REFERENCE)
						.componentId(((AddedInSourceAndDetachedInTargetConflict) c).getDetachedOnTarget().id())
						.componentType(((AddedInSourceAndDetachedInTargetConflict) c).getDetachedOnTarget().type())
						.build();
			} else if (c instanceof AddedInTargetAndDetachedInSourceConflict) {
				return conflict.message(c.getMessage())
						.type(ConflictType.HAS_MISSING_REFERENCE)
						.componentId(((AddedInTargetAndDetachedInSourceConflict) c).getAddedOnTarget().id())
						.componentType(((AddedInTargetAndDetachedInSourceConflict) c).getAddedOnTarget().type())
						.conflictingAttribute(ConflictingAttributeImpl.builder()
								.property(((AddedInTargetAndDetachedInSourceConflict) c).getFeatureName())
								.value(((AddedInTargetAndDetachedInSourceConflict) c).getDetachedOnSource().id())
								.build())
						.build();
			} else {
				return conflict.message("Not implemented conflict mapping").build();
			}
		}
	}
	
	private ConflictingAttribute toConflictingAttribute(RevisionPropertyDiff diff) {
		return ConflictingAttributeImpl.builder()
			.property(diff.getProperty())
			.oldValue(diff.getOldValue())
			.value(diff.getNewValue())
			.build();
	}
	
	@Override
	public final String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}
