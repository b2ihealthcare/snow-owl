/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class BulkGenerationData extends RequestData {

	private int quantity;
	private String partitionId;
	private String generateLegacyIds = "false";

	private Collection<String> systemIds = Lists.newArrayList();

	public BulkGenerationData(final int namespace, final String software, final ComponentCategory category, final int quantity) {
		super(namespace, software);
		this.quantity = quantity;
		buildPartitionId(category);
	}

	private void buildPartitionId(final ComponentCategory category) {
		final StringBuilder builder = new StringBuilder();

		if (getNamespace() == 0) {
			builder.append('0');
		} else {
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());

		setPartitionId(builder.toString());
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
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
