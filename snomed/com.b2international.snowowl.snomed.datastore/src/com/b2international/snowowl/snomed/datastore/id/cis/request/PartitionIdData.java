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

/**
 * @since 4.5
 */
abstract class PartitionIdData extends RequestData {

	private String partitionId;

	public PartitionIdData(final String namespace, final String software, final ComponentCategory category) {
		super(namespace, software);
		setPartitionId(buildPartitionId(category));
	}
	
	public PartitionIdData(final int namespace, final String software, final String partitionId) {
		super(namespace, software);
		setPartitionId(partitionId);
	}

	private String buildPartitionId(final ComponentCategory category) {
		final StringBuilder builder = new StringBuilder();

		if (getNamespace() == 0) {
			builder.append('0');
		} else {
			builder.append('1');
		}

		// append the second part of the partition-identifier
		builder.append(category.ordinal());
		return builder.toString();
	}

	public String getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
	}
	
	public ComponentCategory getComponentCategory() {
		int componentIdentifier = Integer.parseInt(getPartitionId().substring(1, 2));
		return ComponentCategory.getByOrdinal(componentIdentifier);
	}

}
