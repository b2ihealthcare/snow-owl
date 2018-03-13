/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @since 6.3
 */
@ControllerAdvice
public class FhirControllerExceptionMapper {

	private static final Logger LOG = LoggerFactory.getLogger(FhirControllerExceptionMapper.class);
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";
	
	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody OperationOutcome handle(final Exception ex) {
		LOG.error("Exception during processing of a request", ex);
		FhirException fhirException = new FhirException(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage());
		return fhirException.toOperationOutcome();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final BadRequestException ex) {
		return ex.toOperationOutcome();
	}

	
	/**
	 * Exception handler converting any {@link JsonMappingException} to an <em>HTTP 400</em>.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(HttpMessageNotReadableException ex) {
		LOG.error("Exception during processing of a JSON document", ex);
		FhirException fhirException = new FhirException("Invalid JSON representation" + " Exception: " + ex.getMessage());
		return fhirException.toOperationOutcome();
	}
//
//	/**
//	 * <b>Not Found</b> exception handler. All {@link NotFoundException not found exception}s are mapped to {@link HttpStatus#NOT_FOUND
//	 * <em>404 Not Found</em>} in case of the absence of an instance resource.
//	 * 
//	 * @param ex
//	 * @return {@link RestApiError} instance with detailed messages
//	 */
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	public @ResponseBody RestApiError handle(final NotFoundException ex) {
//		return RestApiError.of(ex.toApiError()).build(HttpStatus.NOT_FOUND.value());
//	}
//
//	/**
//	 * Exception handler to return <b>Not Implemented</b> when an {@link UnsupportedOperationException} is thrown from the underlying system.
//	 * 
//	 * @param ex
//	 * @return {@link RestApiError} instance with detailed messages
//	 */
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
//	public @ResponseBody RestApiError handle(NotImplementedException ex) {
//		return RestApiError.of(ex.toApiError()).build(HttpStatus.NOT_IMPLEMENTED.value());
//	}
//
//	/**
//	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
//	 * 
//	 * @param ex
//	 * @return {@link RestApiError} instance with detailed messages
//	 */
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.CONFLICT)
//	public @ResponseBody RestApiError handle(final ConflictException ex) {
//		return RestApiError.of(ex.toApiError()).build(HttpStatus.CONFLICT.value());
//	}
//	
}
