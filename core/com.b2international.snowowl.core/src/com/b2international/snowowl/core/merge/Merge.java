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
package com.b2international.snowowl.core.merge;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collection;

import com.b2international.commons.exceptions.ApiError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 4.6
 */
@JsonDeserialize(builder=Merge.Builder.class)
public interface Merge extends Serializable {

	String getSource();

	String getTarget();

	ApiError getApiError();
	
	Collection<MergeConflict> getConflicts();
	
	Merge start();

	Merge completed();

	Merge failed(ApiError newApiError);

	Merge failedWithConflicts(Collection<MergeConflict> newConflicts);

	Merge cancelRequested();
	
	@JsonPOJOBuilder(buildMethodName="build", withPrefix = "")
	static class Builder {

		private final String source;
		private final String target;
		
		private ApiError apiError;
		private Collection<MergeConflict> conflicts = newArrayList();

		@JsonCreator
		public Builder(@JsonProperty("source") String source, @JsonProperty("target") String target) { 
			this.source = source;
			this.target = target;
		}


		public void apiError(ApiError apiError) {
			this.apiError = apiError;
		}

		public void conflicts(Collection<MergeConflict> conflicts) {
			this.conflicts.addAll(conflicts);
		}

		public Merge build() {
			return new MergeImpl(source, target, apiError, conflicts);
		}
	}
}
