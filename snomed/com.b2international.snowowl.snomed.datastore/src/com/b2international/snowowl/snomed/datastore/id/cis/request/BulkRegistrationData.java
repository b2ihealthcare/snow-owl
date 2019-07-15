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

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
public class BulkRegistrationData extends RequestData {

	private Collection<Record> records = Lists.newArrayList();

	public BulkRegistrationData(final String namespace, final String software, final Collection<Record> records) {
		super(namespace, software);
		this.records = records;
	}
	
	@JsonCreator
	public BulkRegistrationData(
			@JsonProperty("namespace") final int namespace, 
			@JsonProperty("software") final String software, 
			@JsonProperty("records") final Collection<Record> records) {
		super(namespace, software);
		this.records = records;
	}

	public Collection<Record> getRecords() {
		return records;
	}

	public void setRecords(Collection<Record> records) {
		this.records = records;
	}

}