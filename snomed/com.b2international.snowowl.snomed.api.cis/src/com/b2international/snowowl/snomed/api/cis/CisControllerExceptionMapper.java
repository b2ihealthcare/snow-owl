/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.cis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.snomed.api.cis.exceptions.UnauthorizedException;
import com.b2international.snowowl.snomed.api.cis.model.CisError;

/**
 * @since 6.18
 */
@ControllerAdvice
public class CisControllerExceptionMapper {

	private static final Logger LOG = LoggerFactory.getLogger(CisControllerExceptionMapper.class);
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";

	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody CisError handle(final Exception ex) {
		LOG.error("Exception during request processing", ex);
		return new CisError(HttpStatus.INTERNAL_SERVER_ERROR.value(), GENERIC_USER_MESSAGE);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody CisError handle(final UnauthorizedException ex) {
		final ApiError err = ex.toApiError();
		return new CisError(err.getStatus(), err.getMessage());
	}

}
