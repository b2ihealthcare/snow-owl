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
package com.b2international.snowowl.datastore.request;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.events.MergeRequest;

/**
 * @since 4.5
 */
public final class BranchMergeRequestBuilder {
	
	private String source;
	private String target;
	private String commitComment;
	private String reviewId;
	
	private String repositoryId;
	
	BranchMergeRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public BranchMergeRequestBuilder setSource(String source) {
		this.source = source;
		return this;
	}
	
	public BranchMergeRequestBuilder setTarget(String target) {
		this.target = target;
		return this;
	}
	
	public BranchMergeRequestBuilder setCommitComment(String commitComment) {
		this.commitComment = commitComment;
		return this;
	}
	
	public BranchMergeRequestBuilder setReviewId(String reviewId) {
		this.reviewId = reviewId;
		return this;
	}
	
	public Request<ServiceProvider, Branch> build() {
		return RepositoryRequests.wrap(repositoryId, new MergeRequest(source, target, commitComment, reviewId));
	}
	
}
