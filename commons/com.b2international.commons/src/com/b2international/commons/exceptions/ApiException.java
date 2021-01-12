/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.exceptions;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents a high-level API call exception. This exception should be thrown in all cases when a service would like to indicate problems with the
 * request/event itself or with the processing of the request/event.
 * 
 * @since 4.1
 */
public abstract class ApiException extends FormattedRuntimeException {

	private static final long serialVersionUID = 960919521211109447L;
	
	private String developerMessage;
	
	private Map<String, Object> additionalInfo;

	public ApiException(String template, Object... args) {
		super(template, args);
		this.developerMessage = getMessage();
	}

	/**
	 * Creates an ApiError representation from this exception. Useful when the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand serialized Java class and object byte sequences.
	 * 
	 * @return {@link ApiError} representation of this {@link ApiException}, never <code>null</code>.
	 */
	public final ApiError toApiError() {
		return ApiError.builder(getMessage())
				.code(getCode())
				.status(getStatus())
				.developerMessage(getDeveloperMessage())
				.addInfos(getAdditionalInfo())
				.build();
	}

	/**
	 * @return the HTTP status code associated with this exception, or 0 if the HTTP status code is not supported.
	 */
	protected abstract Integer getStatus();
	
	/**
	 * @return the custom application specific code for {@link ApiError} conversion. Subclasses may override.
	 */
	protected Integer getCode() {
		return 0;
	}

	/**
	 * @return the developer message associated with this exception.
	 */
	public final String getDeveloperMessage() {
		return developerMessage;
	}
	
	/**
	 * @return additional information about the {@link ApiException}
	 */
	public final Map<String, Object> getAdditionalInfo() {
		return additionalInfo;
	}
	
	/**
	 * Set the developer message associated with this exception.
	 * 
	 * @param message
	 * @param args
	 * @return this instance for method chaining
	 */
	public final ApiException withDeveloperMessage(String message, Object...args) {
		this.developerMessage = String.format(message, args);
		return this;
	}

	/**
	 * Supply additional information next to the error message.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ApiException withAdditionalInfo(String key, Object value) {
		if (additionalInfo == null) {
			additionalInfo = Maps.newHashMap();
		}
		additionalInfo.put(key, value);
		return this;
	}
	
	/**
	 * Supply additional information next to the error message.
	 * 
	 * @param additionalInfo
	 * @return
	 */
	public ApiException withAdditionalInfo(Map<String, Object> additionalInfo) {
		if (additionalInfo == null) {
			additionalInfo = Maps.newHashMap();
		}
		additionalInfo.putAll(additionalInfo);
		return this;
	}

}
