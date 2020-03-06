/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.datastore.request.CommitResult;
import com.b2international.snowowl.datastore.request.TransactionalRequestBuilder;

/**
 * @since
 */
public interface SnomedTransactionalRequestBuilder<R> extends TransactionalRequestBuilder<R> {

	@Override
	default AsyncRequest<CommitResult> build(String repositoryId, 
			String branch, 
			String userId,
			String commitComment) {
		
		return build(repositoryId, branch, userId, commitComment, null);
	}
	
	default AsyncRequest<CommitResult> build(String repositoryId, 
			String branch, 
			String author,
			String commitComment,
			String defaultModuleId) {
		
		return commit()
				.setDefaultModuleId(defaultModuleId)
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(repositoryId, branch);
	}
	
	@Override
	default SnomedRepositoryCommitRequestBuilder commit() {
		return (SnomedRepositoryCommitRequestBuilder) new SnomedRepositoryCommitRequestBuilder().setBody(build());
	}
}
