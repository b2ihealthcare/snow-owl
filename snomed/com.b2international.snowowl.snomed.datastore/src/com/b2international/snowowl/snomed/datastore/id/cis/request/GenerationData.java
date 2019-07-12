/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.cis.request;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public class GenerationData extends PartitionIdData {

	private String systemId = "";
	private String generateLegacyIds = "false";

	public GenerationData(final String namespace, final String software, final ComponentCategory category) {
		super(namespace, software, category);
	}
	
	@JsonCreator
	public GenerationData(
			@JsonProperty("namespace") final int namespace, 
			@JsonProperty("software") final String software, 
			@JsonProperty("partitionId") final String partitionId) {
		super(namespace, software, partitionId);
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getGenerateLegacyIds() {
		return generateLegacyIds;
	}

	public void setGenerateLegacyIds(String generateLegacyIds) {
		this.generateLegacyIds = generateLegacyIds;
	}

}
