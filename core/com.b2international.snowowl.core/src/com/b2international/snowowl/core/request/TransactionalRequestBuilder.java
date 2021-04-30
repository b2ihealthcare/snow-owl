/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.RequestBuilder;

/**
 * @since 5.7
 * @param <R>
 */
public interface TransactionalRequestBuilder<R> extends RequestBuilder<TransactionContext, R> {

	default AsyncRequest<CommitResult> build(String resourceUri, 
			String author,
			String commitComment) {
		return commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(resourceUri);
	}
	
	default AsyncRequest<CommitResult> build(ResourceURI resourceUri, 
			String author,
			String commitComment) {
		
		return build(resourceUri.getUri(), author, commitComment);
	}
	
	
	/**
	 * @param repositoryId
	 * @param branch
	 * @param author
	 * @param commitComment
	 * @return
	 * @deprecated - use {@link #build(String, String, String)} instead, this will be removed in 8.0
	 */
	default AsyncRequest<CommitResult> build(String repositoryId, 
			String branch, 
			String author,
			String commitComment) {
		return commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(repositoryId, branch);
	}
	
	default RepositoryCommitRequestBuilder commit() {
		return new RepositoryCommitRequestBuilder().setBody(build());
	}
	
}
