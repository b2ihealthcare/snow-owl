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
package com.b2international.snowowl.datastore.review;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.2
 */
public class ReviewConfiguration {

	@Min(1)
	private int keepCurrentMins = 15;

	@Min(1)
	private int keepOtherMins = 5;

	@JsonProperty
	public int getKeepCurrentMins() {
		return keepCurrentMins;
	}

	@JsonProperty
	public void setKeepCurrentMins(final int keepCurrentMins) {
		this.keepCurrentMins = keepCurrentMins;
	}

	@JsonProperty
	public int getKeepOtherMins() {
		return keepOtherMins;
	}

	@JsonProperty
	public void setKeepOtherMins(final int keepOtherMins) {
		this.keepOtherMins = keepOtherMins;
	}
}
