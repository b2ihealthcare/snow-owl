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
package com.b2international.snowowl.core.exceptions;

import java.util.Collections;
import java.util.Map;

import com.b2international.commons.exceptions.FormattedRuntimeException;

/**
 * Represents a high-level API call exception. This exception should be thrown in all cases when a service would like to indicate problems with the
 * request/event itself or with the processing of the request/event.
 * 
 * @since 4.1
 */
public abstract class ApiException extends FormattedRuntimeException {

	private static final long serialVersionUID = 960919521211109447L;

	public ApiException(String template, Object... args) {
		super(template, args);
	}

	/**
	 * Creates an ApiError representation from this exception. Useful when the exception must be propagated through protocols where Java serialization
	 * cannot be used (eg. HTTP), or the possible receiver cannot understand serialized Java class and object byte sequences.
	 * 
	 * @return {@link ApiError} representation of this {@link ApiException}, never <code>null</code>.
	 */
	public final ApiError toApiError() {
		return ApiError.Builder.of(getMessage())
				.code(getCode())
				.status(getStatus())
				.developerMessage(getDeveloperMessage())
				.addInfos(getAdditionalInfo())
				.build();
	}

	/**
	 * Returns the HTTP status code associated with this exception.
	 * @return
	 */
	protected abstract Integer getStatus();
	
	/**
	 * Returns the custom application specific code for {@link ApiError} conversion. Subclasses may override.
	 * 
	 * @return
	 */
	protected Integer getCode() {
		return 0;
	}

	/**
	 * Returns the developer message associated with this exception. Subclasses may override.
	 * 
	 * @return
	 */
	protected String getDeveloperMessage() {
		return getMessage();
	}

	/**
	 * Returns additional information about the {@link ApiException}.
	 * 
	 * @return
	 */
	protected Map<String, Object> getAdditionalInfo() {
		return Collections.emptyMap();
	}

}
