/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.0 
 */
public final class Rf2ImportConfiguration {
	
	private final boolean createVersions;
	private final Rf2ReleaseType releaseType;

	@JsonCreator
	public Rf2ImportConfiguration(
			@JsonProperty("releaseType") Rf2ReleaseType releaseType, 
			@JsonProperty("createVersions") boolean createVersions) {
		this.releaseType = releaseType;
		this.createVersions = createVersions;
	}
	
	public boolean isCreateVersions() {
		return createVersions;
	}

	public Rf2ReleaseType getReleaseType() {
		return releaseType;
	}

}
