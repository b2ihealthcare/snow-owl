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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.5
 */
public class BulkDeprecationData extends RequestData {

	@JsonProperty("sctids")
	private Collection<String> componentIds;

	public BulkDeprecationData(final String namespace, final String software, final Collection<String> componentIds) {
		super(namespace, software);
		this.componentIds = componentIds;
	}
	
	@JsonCreator
	public BulkDeprecationData(
			@JsonProperty("namespace") final int namespace, 
			@JsonProperty("software") final String software, 
			@JsonProperty("sctids") final Collection<String> componentIds) {
		super(namespace, software);
		this.componentIds = componentIds;
	}

	public Collection<String> getComponentIds() {
		return componentIds;
	}

	public void setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
	}

}
