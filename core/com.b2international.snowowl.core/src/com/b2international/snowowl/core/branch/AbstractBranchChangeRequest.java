/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.*;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.merge.ConflictingAttribute;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.core.merge.MergeConflict.ConflictType;
import com.b2international.snowowl.core.repository.RepositoryCommitNotificationSender;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 4.6
 */
public abstract class AbstractBranchChangeRequest implements Request<RepositoryContext, Merge>, AccessControl {

	private static final long serialVersionUID = 1L;

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
	protected final String parentLockContext;

	protected AbstractBranchChangeRequest(String sourcePath, String targetPath, String userId, String commitMessage, String parentLockContext) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.userId = userId;
		this.commitMessage = commitMessage;
		this.parentLockContext = parentLockContext;
	}

	@Override
	public Merge execute(RepositoryContext context) {
		
		try {
			final Branch source = RepositoryRequests.branching().prepareGet(sourcePath).build().execute(context);
			final Branch target = RepositoryRequests.branching().prepareGet(targetPath).build().execute(context);
			
			Merge merge = Merge.builder()
					.source(sourcePath)
					.target(targetPath)
					.build();
			
			try {
				Commit commit = applyChanges(context, source, target);
				if (commit != null) {
					context.service(RepositoryCommitNotificationSender.class).publish(context, commit);
				}
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

	protected abstract Commit applyChanges(RepositoryContext context, Branch source, Branch target);
	
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
			.map(c -> ConflictingAttribute.builder()
					.property(c.getSourceChange().getProperty())
					.oldValue(c.getSourceChange().getOldValue())
					.sourceValue(c.getSourceChange().getNewValue())
					.targetValue(c.getTargetChange().getNewValue())
					.build())
			.collect(Collectors.toList());
		
		if (!conflictingAttributes.isEmpty()) {
			 return conflict.conflictingAttributes(conflictingAttributes).build();
		} else {
			// XXX multiple conflicts are not expected here for a single object
			Conflict c = Iterables.getFirst(conflicts, null);
			if (c instanceof AddedInSourceAndTargetConflict) {
				return conflict
						.message(c.getMessage())
						.conflictingAttribute(ConflictingAttribute.builder().property(Revision.Fields.ID).build())
						.build();
			} else if (c instanceof ChangedInSourceAndDetachedInTargetConflict) {
				return conflict
						.message(c.getMessage())
						.type(ConflictType.DELETED_WHILE_CHANGED)
						.conflictingAttributes(
							((ChangedInSourceAndDetachedInTargetConflict) c).getChanges()
								.stream()
								.map(attributeChange -> ConflictingAttribute.builder()
									.property(attributeChange.getProperty())
									.oldValue(attributeChange.getOldValue())
									.sourceValue(attributeChange.getNewValue())
									.build())
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
						.conflictingAttribute(ConflictingAttribute.builder()
								.property(((AddedInTargetAndDetachedInSourceConflict) c).getFeatureName())
								.targetValue(((AddedInTargetAndDetachedInSourceConflict) c).getDetachedOnSource().id())
								.build())
						.build();
			} else {
				return conflict.message("Not implemented conflict mapping").build();
			}
		}
	}
	
	@Override
	public final String getOperation() {
		return Permission.OPERATION_EDIT;
	}
	
}
