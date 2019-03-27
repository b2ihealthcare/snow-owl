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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Map;

import com.b2international.snowowl.core.exceptions.ApiError;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * {@link RestApiError} represents a generic multi purpose user AND developer friendly error/exception representation, which should be used in all
 * cases when mapping various exceptions to responses.
 * 
 * @since 3.7
 */
@ApiModel("Error Response")
public class RestApiError implements ApiError {

	@ApiModelProperty(required = true)
	private int status;

	@ApiModelProperty(required = false)
	private Integer code;

	@ApiModelProperty(required = true)
	private String message = "Request failed";

	@ApiModelProperty(required = true)
	private String developerMessage;
	
	@ApiModelProperty(required = false, hidden = true)
	private Map<String, Object> additionalInformation;

	private RestApiError() {
		// intentionally ignored, use the builder
	}

	/**
	 * Returns an HTTP status code associated with this error.
	 * 
	 * @return
	 */
	@Override
	public Integer getStatus() {
		return status;
	}

	/**
	 * Custom application specific error code associated with the response.
	 * 
	 * @return
	 */
	@Override
	public Integer getCode() {
		return code;
	}

	/**
	 * Returns a user-friendly error message, meaning it can be used in User interfaces to show the error to the end-user. It should never contain any
	 * kind of technical information, that should go to the {@link #getDeveloperMessage()}.
	 * 
	 * @return
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * Returns a developer-friendly (more verbose than the {@link #getMessage()}) error message. It can be used to investigate problems and the cause
	 * of them. Usually can contain technical information, parameter values, etc.
	 * 
	 * @return
	 */
	@Override
	public String getDeveloperMessage() {
		return developerMessage;
	}
	
	@JsonAnyGetter
	@Override
	public Map<String, Object> getAdditionalInfo() {
		return additionalInformation;
	}

	private void setStatus(int status) {
		this.status = status;
	}

	private void setCode(int code) {
		this.code = code;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	private void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	private void setAdditionalInformation(Map<String, Object> additionalInfo) {
		this.additionalInformation = additionalInfo;
	}

	/**
	 * Return a new {@link Builder} to build a new {@link RestApiError} representation based on the given {@link ApiError}.
	 * 
	 * @param error
	 * @return
	 */
	public static Builder of(ApiError error) {
		return new Builder(error);
	}

	/**
	 * Builder responsible for building {@link RestApiError} in a fluent way.
	 * 
	 * @since 3.7
	 */
	public static final class Builder {

		private RestApiError error;

		private Builder(ApiError error) {
			this.error = new RestApiError();
			this.error.setCode(error.getCode());
			this.error.setMessage(error.getMessage());
			this.error.setDeveloperMessage(error.getDeveloperMessage());
			this.error.setAdditionalInformation(error.getAdditionalInfo());
		}

		public RestApiError build(int httpStatus) {
			this.error.setStatus(httpStatus);
			return this.error;
		}

	}

}