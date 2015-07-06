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
package com.b2international.snowowl.datastore.server.internal.review;

import com.b2international.snowowl.datastore.server.branch.Branch;
import com.b2international.snowowl.datastore.server.review.Review;
import com.b2international.snowowl.datastore.server.review.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.0
 */
public abstract class ReviewImplMixin {

	@JsonCreator
	ReviewImplMixin(@JsonProperty("name") String name, 
			@JsonProperty("parentPath") String parentPath, 
			@JsonProperty("baseTimestamp") long baseTimestamp,
			@JsonProperty("headTimestamp") long headTimestamp, 
			@JsonProperty("deleted") boolean deleted) {

	}

	@JsonProperty
	public abstract String id();

	@JsonProperty
	public abstract ReviewStatus status();

	@JsonProperty
	public abstract Branch source();

	@JsonProperty
	public abstract Branch target();

	@JsonProperty
	public abstract Review delete();

	@JsonProperty
	public abstract boolean isDeleted();

	@JsonProperty
	public abstract int getSourceCdoBranchId();

	@JsonProperty
	public abstract int getTargetCdoBranchId();
}
