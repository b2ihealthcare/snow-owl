/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;
import org.hl7.fhir.r5.model.Parameters;
import org.hl7.fhir.r5.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.exceptions.*;
import com.b2international.fhir.r5.operations.BaseParameters;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

/**
 * Custom FHIR exception handling and configuration for all FHIR resources, operations.
 * 
 * @since 8.0
 */
public abstract class AbstractFhirController extends AbstractRestService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFhirController.class);
	
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";

	// Headers used in resource creation/udpate requests 
	protected static final String X_COMMIT_COMMENT = "X-Commit-Comment";
	protected static final String X_EFFECTIVE_DATE = "X-Effective-Date";
	protected static final String X_OWNER = "X-Owner";
	protected static final String X_OWNER_PROFILE_NAME = "X-Owner-Profile-Name";
	protected static final String X_BUNDLE_ID = "X-Bundle-Id";

	protected final <T extends Resource> T toFhirResource(
		final InputStream requestBody, 
		final String contentType, 
		final Class<T> resourceClass
	) {
		try {

			/*
			 * XXX: There is no overriding query parameter for "input" content types, but we still
			 * want to use JSON as the default if no type was specified
			 */
			final FhirMediaType mediaType = FhirMediaType.parse(contentType, null);
			
			final Resource fhirResource = mediaType.parseResource(requestBody);
	
			if (!resourceClass.isAssignableFrom(fhirResource.getClass())) {
				throw new BadRequestException(String.format("Expected a complete %s resource as the request body, got %s.",
					resourceClass.getSimpleName(), fhirResource.getClass().getSimpleName()));
			}
			
			return resourceClass.cast(fhirResource);
		
		} catch (IOException e) {
			throw new BadRequestException(String.format("Failed to parse request body as a complete %s resource: %s", resourceClass.getSimpleName(), e.getMessage()));
		}
	}
	
	protected final Parameters toFhirParameters(final InputStream requestBody, final String contentType) {
		return toFhirResource(requestBody, contentType, Parameters.class);
	}
	
	protected final ResponseEntity<byte[]> toResponseEntity(
			final BaseParameters parameters,
			final String accept, 
			final String _format,
			final Boolean _pretty) {
		
		final FhirMediaType mediaType = FhirMediaType.parse(accept, _format);
		
		final boolean prettyPrinting = (_pretty != null) && _pretty;
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			
			mediaType.writeParameters(baos, parameters, prettyPrinting);
		} catch (IOException e) {
			throw new BadRequestException(String.format("Failed to serialize FHIR operation parameters to a response body.",
				parameters.getClass().getSimpleName()));
		}

		return ResponseEntity.status(HttpStatus.OK)
			.contentType(mediaType.getMediaType())
			.body(baos.toByteArray());		
	}
	
	protected final ResponseEntity<byte[]> toResponseEntity(
			final Resource resource, 
			final String accept, 
			final String _format,
			final Boolean _pretty) {
		return toResponseEntity(HttpStatus.OK, resource, accept, _format, _pretty);
	}
	
	protected final ResponseEntity<byte[]> toResponseEntity(
		final HttpStatus httpStatus,
		final Resource resource,
		final WebRequest request
	) {
		// Extract the original request's Accept header and query parameters to determine the response format
		final String accept = request.getHeader(HttpHeaders.ACCEPT);
		final String _format = request.getParameter("_format");
		final boolean _pretty = Boolean.parseBoolean(request.getParameter("_pretty"));
	
		return toResponseEntity(httpStatus, resource, accept, _format, _pretty);
	}
	
	protected final ResponseEntity<byte[]> toResponseEntity(
		final HttpStatus httpStatus,
		final Resource resource, 
		final String accept, 
		final String _format,
		final Boolean _pretty
	) {
		final FhirMediaType mediaType = FhirMediaType.parse(accept, _format);
		final byte[] body = writeToBytes(resource, mediaType, _pretty);

		return ResponseEntity.status(httpStatus)
			.contentType(mediaType.getMediaType())
			.body(body);
	}

	private byte[] writeToBytes(final Resource resource, final FhirMediaType mediaType, final Boolean _pretty) {
		final boolean prettyPrinting = (_pretty != null) && _pretty;
				
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			
			mediaType.writeResource(baos, resource, prettyPrinting);
		} catch (IOException e) {
			throw new BadRequestException(String.format("Failed to serialize FHIR resource to a response body.",
				resource.getClass().getSimpleName()));
		}
		return baos.toByteArray();
	}
	
	protected final ResponseEntity<byte[]> toResponseEntity(
		final Bundle bundle, 
		final UriComponentsBuilder fullUrlBuilder, 
		final String accept,
		final String _format,
		final Boolean _pretty
	) {
		// FIXME: Temporary measure to add "fullUrl" to returned bundle entries
		final var entries = bundle.getEntry();
		
		if (!entries.isEmpty()) {
			// Add "fullUrl" to original entries, add to builder
			for (var entry : entries) {
				if (entry.getResource() != null) {
					final String resourceId = entry.getResource().getId();
					final String fullUrl = fullUrlBuilder.buildAndExpand(Map.of("id", resourceId)).toString();
					entry.setFullUrl(fullUrl);
				}
			}
		}
		
		return toResponseEntity(bundle, accept, _format, _pretty);
	}

	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ResponseEntity<byte[]> handle(final Exception ex, final WebRequest request) {
		if ("broken pipe".equals(Strings.nullToEmpty(Throwables.getRootCause(ex).getMessage()).toLowerCase())) {
	        return null; // socket is closed, cannot return any response    
	    } else {
	    	LOG.error("Exception during processing of a request", ex);
	    	FhirException fhirException = new FhirException(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
	    	return toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, fhirException.toOperationOutcome(), request);
	    }
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final SyntaxException ex, final WebRequest request) {
    	FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
    	fhirException.withAdditionalInfo(ex.getAdditionalInfo());
    	return toResponseEntity(HttpStatus.BAD_REQUEST, fhirException.toOperationOutcome(), request);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final ValidationException ex, final WebRequest request) {
		FhirException error = new FhirException("Validation error", org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
		error.withAdditionalInfo(ex.getAdditionalInfo());
		return toResponseEntity(HttpStatus.BAD_REQUEST, error.toOperationOutcome(), request);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ResponseEntity<byte[]> handle(final UnauthorizedException ex, final WebRequest request) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGAUTHREQUIRED);
		ResponseEntity<byte[]> responseEntityWithoutAuth = toResponseEntity(HttpStatus.UNAUTHORIZED, fhirException.toOperationOutcome(), request);
		
		return ResponseEntity.status(responseEntityWithoutAuth.getStatusCode())
			.headers(responseEntityWithoutAuth.getHeaders())
			.header("WWW-Authenticate", "Basic")
			.header("WWW-Authenticate", "Bearer")
			.body(responseEntityWithoutAuth.getBody());
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final BadRequestException ex, final WebRequest request) {
		return toResponseEntity(HttpStatus.BAD_REQUEST, ex.toOperationOutcome(), request);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public @ResponseBody ResponseEntity<byte[]> handle(final ForbiddenException ex, final WebRequest request) {
		return toResponseEntity(HttpStatus.FORBIDDEN, new OperationOutcome()
			.addIssue(new OperationOutcome.OperationOutcomeIssueComponent()
				.setSeverity(IssueSeverity.ERROR)
				.setCode(IssueType.FORBIDDEN)
				.setDiagnostics(ex.getMessage())), null, null, true);
	}
	
	/**
	 * Exception handler converting any {@link JsonMappingException} to an <em>HTTP 400</em>.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(HttpMessageNotReadableException ex, final WebRequest request) {
		LOG.trace("Exception during processing of a JSON document", ex);
		FhirException fhirException = new FhirException(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGCANTPARSECONTENT);
		return toResponseEntity(HttpStatus.BAD_REQUEST, fhirException.toOperationOutcome(), request);
	}

	/**
	 * <b>Not Found</b> exception handler. All {@link NotFoundException not found exception}s are mapped to {@link HttpStatus#NOT_FOUND
	 * <em>404 Not Found</em>} in case of the absence of an instance resource.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody ResponseEntity<byte[]> handle(final NotFoundException ex, final WebRequest request) {
		return toResponseEntity(HttpStatus.NOT_FOUND, new OperationOutcome()
				.addIssue(
					new OperationOutcome.OperationOutcomeIssueComponent()
						.setSeverity(IssueSeverity.ERROR)
						.setCode(IssueType.NOTFOUND)
						.setDiagnostics(ex.getMessage())
						.addLocation(ex.getKey())
						.setDetails(FhirException.toDetails(org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGNOEXIST, ex.getKey()))
				), null, null, true);
	}

	/**
	 * Exception handler to return <b>Not Implemented</b> when an {@link UnsupportedOperationException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	public @ResponseBody ResponseEntity<byte[]> handle(NotImplementedException ex, final WebRequest request) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGUNKNOWNOPERATION);
		return toResponseEntity(HttpStatus.NOT_IMPLEMENTED, fhirException.toOperationOutcome(), request);
	}

	/**
	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody ResponseEntity<byte[]> handle(final ConflictException ex, final WebRequest request) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGLOCALFAIL);
		return toResponseEntity(HttpStatus.CONFLICT, fhirException.toOperationOutcome(), request);
	}
	
}
