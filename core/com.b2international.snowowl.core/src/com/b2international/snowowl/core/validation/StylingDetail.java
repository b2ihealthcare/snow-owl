/*
 * Copyright 2019 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.16
 */
public final class StylingDetail {
	
	private final int index;
	private final int length;
	
	@JsonCreator
	public StylingDetail(
			@JsonProperty("index") int index,
			@JsonProperty("length") int length) {
		this.index = index;
		this.length = length;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getLength() {
		return length;
	}
	
}
