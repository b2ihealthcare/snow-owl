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
package com.b2international.snowowl.api.rest.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;

/**
 */
public abstract class AbstractAdminRestService {

	/** Displayed when the caught exception contains no message. */
	private static final String DEFAULT_MESSAGE = "An error occurred while processing the request.";

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody String handleNotFoundException(final NotFoundException e) {
		return handleException(e);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleIllegalArgumentException(final IllegalArgumentException e) {
		return handleException(e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleException(final Exception e) {
		return toExceptionMessage(e);
	}

	private String toExceptionMessage(final Exception e) {
		return Optional.fromNullable(e.getMessage()).or(DEFAULT_MESSAGE);
	}

	protected String joinStrings(final List<String> stringList) {
		return Joiner.on("\n").join(stringList);
	}
}