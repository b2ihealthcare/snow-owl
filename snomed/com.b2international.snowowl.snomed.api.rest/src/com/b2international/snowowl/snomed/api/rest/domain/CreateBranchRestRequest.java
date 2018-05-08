/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.domain;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolderImpl;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 4.1
 */
public class CreateBranchRestRequest extends MetadataHolderImpl {

	@NotEmpty
	private final String parent;

	@NotEmpty
	private final String name;

	@JsonCreator
	public CreateBranchRestRequest(@JsonProperty("parent") final String parent, @JsonProperty("name") final String name, @JsonProperty("metadata") final Metadata metadata) {
		super(metadata);
		this.parent = Strings.isNullOrEmpty(parent) ? "MAIN" : parent;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getParent() {
		return parent;
	}
	
	public String path() {
		return parent + Branch.SEPARATOR + name;
	}
	
}
