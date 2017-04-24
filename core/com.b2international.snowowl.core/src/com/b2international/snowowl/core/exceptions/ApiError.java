/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.exceptions;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.b2international.snowowl.core.internal.exceptions.ApiErrorImpl;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Represents a common error object create because of a failed API call, and can be derived mostly from {@link ApiException} subclasses.
 * 
 * @since 4.1
 */
@JsonDeserialize(builder = ApiError.Builder.class)
public interface ApiError {

	/**
	 * Returns a user-friendly error message, which can be directly used in User interfaces to show the error to the end-user. It should never contain
	 * any kind of technical information, that should go to the {@link #getDeveloperMessage()}.
	 * 
	 * @return
	 */
	String getMessage();

	/**
	 * Returns a developer-friendly (more verbose than the {@link #getMessage()}) error message. It can be used to investigate problems and the cause
	 * of them. Usually can contain technical information, parameter values, etc.
	 * 
	 * @return
	 */
	String getDeveloperMessage();

	/**
	 * Custom application specific error code associated with the error.
	 * 
	 * @return
	 */
	Integer getCode();
	
	/**
	 * Additional information about the error.
	 * @return
	 */
	Map<String, Object> getAdditionalInfo();

	/**
	 * Builds {@link ApiError} representations.
	 * 
	 * @since 4.1
	 */
	@JsonPOJOBuilder(buildMethodName="build", withPrefix = "")
	static class Builder {

		private String message;
		private String developerMessage;
		private int code;
		private Map<String, Object> additionalInfo = newHashMap();

		@JsonCreator
		public Builder(@JsonProperty("message") String message) {
			this.message = message;
		}
		
		public Builder code(int code) {
			this.code = code;
			return this;
		}
		
		public Builder developerMessage(String developerMessage) {
			this.developerMessage = developerMessage;
			return this;
		}
		
		@JsonAnySetter
		public Builder addInfo(String property, Object value) {
			this.additionalInfo.put(property, value);
			return this;
		}

		@JsonProperty("additionalInfo")
		public Builder addInfos(Map<String, Object> additionalInformation) {
			this.additionalInfo.putAll(additionalInformation);
			return this;
		}
		
		public ApiError build() {
			return new ApiErrorImpl(this.message, this.developerMessage, this.code, this.additionalInfo);
		}
		
		public static Builder of(String message) {
			return new Builder(message);
		}
		
	}

}
