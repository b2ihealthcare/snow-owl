/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.export;

import java.util.UUID;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;

/**
 * @since 6.3
 */
public final class ContentExportRequestBuilder extends BaseRequestBuilder<ContentExportRequestBuilder, RepositoryContext, UUID> {

	private UUID id = UUID.randomUUID();
	private ContentEntry rootEntry;

	public ContentExportRequestBuilder setId(final UUID id) {
		this.id = id;
		return getSelf();
	}

	public ContentExportRequestBuilder setRootEntry(final ContentEntry rootEntry) {
		this.rootEntry = rootEntry;
		return getSelf();
	}

	@Override
	protected ContentExportRequest doBuild() {
		final ContentExportRequest request = new ContentExportRequest();
		request.setId(id);
		request.setRootEntry(rootEntry);
		return request;
	}
}
