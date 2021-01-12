/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.exceptions.ApiError;
import com.b2international.snowowl.core.jobs.RemoteJobState;
import com.b2international.snowowl.core.request.io.ImportResponse;

/**
 * @since 7.5
 */
public final class SnomedRf2ImportConfiguration {

	private final String id;
	private final RemoteJobState status;
	private final ApiError error;
	private final ImportResponse response;
	
	public SnomedRf2ImportConfiguration(final String id, final RemoteJobState status, final ApiError error, final ImportResponse response) {
		this.id = id;
		this.status = status;
		this.error = error;
		this.response = response;
	}
	
	public String getId() {
		return id;
	}
	
	public RemoteJobState getStatus() {
		return status;
	}
	
	public ApiError getError() {
		return error;
	}
	
	public ImportResponse getResponse() {
		return response;
	}
	
}
