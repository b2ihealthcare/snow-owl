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
package com.b2international.snowowl.snomed.cis.model;

import java.util.Collection;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class BulkGenerationData extends PartitionIdData {

	private int quantity;
	private String generateLegacyIds = "false";

	private Collection<String> systemIds = Lists.newArrayList();

	public BulkGenerationData(
			final String namespace, 
			final String software, 
			final ComponentCategory category, 
			final int quantity) {
		super(namespace, software, category);
		this.quantity = quantity;
	}
	
	@JsonCreator
	public BulkGenerationData(
			@JsonProperty("namespace") final int namespace, 
			@JsonProperty("software") final String software, 
			@JsonProperty("partitionId") final String partitionId, 
			@JsonProperty("quantity") final int quantity) {
		super(namespace, software, partitionId);
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getGenerateLegacyIds() {
		return generateLegacyIds;
	}

	public void setGenerateLegacyIds(String generateLegacyIds) {
		this.generateLegacyIds = generateLegacyIds;
	}

	public Collection<String> getSystemIds() {
		return systemIds;
	}

	public void setSystemIds(Collection<String> systemIds) {
		this.systemIds = systemIds;
	}

}
