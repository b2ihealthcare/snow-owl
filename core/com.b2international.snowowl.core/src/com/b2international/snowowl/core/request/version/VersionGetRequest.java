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
package com.b2international.snowowl.core.request.version;

import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.core.version.Version;

/**
 * @since 8.0
 */
public final class VersionGetRequest extends
		GetResourceRequest<VersionSearchRequestBuilder, com.b2international.snowowl.core.ServiceProvider, Version> {

	private static final long serialVersionUID = 1L;

	public VersionGetRequest(String versionId) {
		super(versionId);
	}

	@Override
	protected VersionSearchRequestBuilder createSearchRequestBuilder() {
		return new VersionSearchRequestBuilder();
	}

}
