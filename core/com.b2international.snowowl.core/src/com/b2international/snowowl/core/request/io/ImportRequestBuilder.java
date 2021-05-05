/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.io;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.CommitResult;
import com.b2international.snowowl.core.request.TerminologyResourceCommitRequestBuilder;

/**
 * @since 7.12
 * @param <T>
 */
public abstract class ImportRequestBuilder<T extends ImportRequestBuilder<T>> 
		extends BaseRequestBuilder<T, TransactionContext, ImportResponse> {

	private final Attachment attachment;
	
	public ImportRequestBuilder(Attachment attachment) {
		this.attachment = attachment;
	}
	
	@Override
	protected final Request<TransactionContext, ImportResponse> doBuild() {
		ImportRequest req = create();
		init(req);
		return req;
	}

	@OverridingMethodsMustInvokeSuper
	protected void init(ImportRequest req) {
		req.setAttachment(attachment);
	}

	protected abstract ImportRequest create();
	
	public AsyncRequest<CommitResult> build(String codeSystemUri) {
		return build(new ResourceURI(codeSystemUri));
	}
	
	public AsyncRequest<CommitResult> build(ResourceURI codeSystemUri) {
		return new TerminologyResourceCommitRequestBuilder()
				.setBody(build())
				.setCommitComment(String.format("Imported components from source file '%s'", attachment.getFileName()))
				.build(codeSystemUri);
	}
	
}
