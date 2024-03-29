/*
 * Copyright 2011-2020 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * @since 1.0
 * @param <T> - the actual resource request body
 */
public final class SnomedResourceRequest<T> extends ResourceRequest<T> {

	private String defaultModuleId;

	public String getDefaultModuleId() {
		return defaultModuleId;
	}

	public void setDefaultModuleId(final String defaultModuleId) {
		this.defaultModuleId = defaultModuleId;
	}

	@Override
	protected void doToString(ToStringHelper toStringHelper) {
		toStringHelper.add("defaultModuleId", defaultModuleId);
	}
}