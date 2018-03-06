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
package com.b2international.snowowl.snomed.core.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds the suggested filename for an export result, along with the identifier
 * in the file registry.
 */
public final class Rf2ExportResult {

	private final String name;
	private final UUID registryId;

	@JsonCreator
	public Rf2ExportResult(
			@JsonProperty("name") final String name, 
			@JsonProperty("registryId") final UUID registryId) {

		this.name = name;
		this.registryId = registryId;
	}

	public String getName() {
		return name;
	}

	public UUID getRegistryId() {
		return registryId;
	}
}
