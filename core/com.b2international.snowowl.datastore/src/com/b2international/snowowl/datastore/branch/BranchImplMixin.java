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
package com.b2international.snowowl.datastore.branch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mixin interface to use for {@link Branch} JSON serialization.
 * 
 * @since 4.1
 */
public abstract class BranchImplMixin implements Branch {

	@JsonCreator
	BranchImplMixin(@JsonProperty("name") String name, @JsonProperty("parentPath") String parentPath, @JsonProperty("baseTimestamp") long baseTimestamp,
			@JsonProperty("headTimestamp") long headTimestamp, @JsonProperty("deleted") boolean deleted) {
	}
	
	@JsonProperty
	@Override
	public abstract long baseTimestamp();
	
	@JsonProperty
	@Override
	public abstract long headTimestamp();
	
	@JsonProperty
	@Override
	public abstract String name();
	
	@JsonProperty
	@Override
	public abstract String parentPath();
	
}
