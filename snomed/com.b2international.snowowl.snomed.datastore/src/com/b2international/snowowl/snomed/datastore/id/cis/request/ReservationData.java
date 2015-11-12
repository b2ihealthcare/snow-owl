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

import com.b2international.snowowl.core.terminology.ComponentCategory;

/**
 * @since 4.5
 */
public class ReservationData extends RequestData {

	private String systemId = "";
	private String partitionId;
	private String expirationDate;

	public ReservationData(final int namespace, final String software, final String expirationDate, final ComponentCategory category) {
		super(namespace, software);
		this.expirationDate = expirationDate;
		buildPartitionId(category);
	}

	private void buildPartitionId(ComponentCategory category) {
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

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getPartitionId() {
		return partitionId;
	}

	public void setPartitionId(String partitionId) {
		this.partitionId = partitionId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

}
