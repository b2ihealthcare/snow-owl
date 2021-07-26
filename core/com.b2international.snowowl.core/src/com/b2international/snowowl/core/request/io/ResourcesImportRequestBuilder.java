/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 8.0
 */
public abstract class ResourcesImportRequestBuilder<RB extends ResourcesImportRequestBuilder<RB, R>, R extends Resource>
		extends BaseRequestBuilder<RB, ServiceProvider, ImportResponse> 
		implements SystemRequestBuilder<ImportResponse> {

	private final Attachment sourceFile;

	protected ResourcesImportRequestBuilder(final Attachment sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	@Override
	protected ResourcesImportRequest<R> doBuild() {
		final ResourcesImportRequest<R> req = create();
		req.setSourceFile(sourceFile);
		return req;
	}

	protected abstract ResourcesImportRequest<R> create();
}
