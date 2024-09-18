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
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.elementmodel.Manager.FhirFormat;
import org.hl7.fhir.r5.formats.IParser.OutputStyle;
import org.hl7.fhir.r5.formats.JsonParser;
import org.hl7.fhir.r5.formats.XmlParser;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.*;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

/**
 * Custom FHIR exception handling and configuration for all FHIR resources, operations.
 * 
 * @since 8.0
 */
public abstract class AbstractFhirController extends AbstractRestService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFhirController.class);
	
	// FHIR-specific media types should be supplied in "content-type" and "accept" headers
	protected static final String APPLICATION_FHIR_JSON_VALUE = "application/fhir+json";
	protected static final String APPLICATION_FHIR_XML_VALUE = "application/fhir+xml";

	// Short values are only admitted as _format parameters
	protected static final String FORMAT_JSON = "json";
	protected static final String FORMAT_XML = "xml";

	// More general media types are allowed both as a _format parameter as well as an "accept" header
	protected static final String TEXT_JSON_VALUE = "text/json";
	protected static final String TEXT_XML_VALUE = MediaType.TEXT_XML_VALUE;
	
	protected static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;
	protected static final String APPLICATION_XML_VALUE = MediaType.APPLICATION_XML_VALUE;

	// Keep parsed forms of our custom media types around as well
	private static final MediaType APPLICATION_FHIR_JSON = MediaType.parseMediaType(APPLICATION_FHIR_JSON_VALUE);
	private static final MediaType APPLICATION_FHIR_XML = MediaType.parseMediaType(APPLICATION_FHIR_XML_VALUE);
	private static final MediaType TEXT_JSON = MediaType.parseMediaType(TEXT_JSON_VALUE);

	// Last ditch effort: return JSON when "*/*" media type is accepted by the client
	private static final String ALL_VALUE = MediaType.ALL_VALUE;
	
	private static final List<MediaType> SUPPORTED_MEDIA_TYPES = ImmutableList.of(
		APPLICATION_FHIR_JSON,
		APPLICATION_FHIR_XML,
		TEXT_JSON,
		MediaType.TEXT_XML,
		MediaType.APPLICATION_JSON,
		MediaType.APPLICATION_XML,
		MediaType.ALL
	);
	
	private static final String GENERIC_USER_MESSAGE = "Something went wrong during the processing of your request.";

	// Headers used in resource creation/udpate requests 
	protected static final String X_COMMIT_COMMENT = "X-Commit-Comment";
	protected static final String X_EFFECTIVE_DATE = "X-Effective-Date";
	protected static final String X_OWNER = "X-Owner";
	protected static final String X_OWNER_PROFILE_NAME = "X-Owner-Profile-Name";
	protected static final String X_BUNDLE_ID = "X-Bundle-Id";
	
	private static List<MediaType> getMediaTypeCandidates(final String accept) {
		final List<MediaType> mediaTypeCandidates = MediaType.parseMediaTypes(accept);
		
		if (!mediaTypeCandidates.isEmpty()) {
			MediaType.sortBySpecificityAndQuality(mediaTypeCandidates);
			
			// Remove quality values and other (eg. charset) parameters once the list is sorted
			for (int i = 0; i < mediaTypeCandidates.size(); i++) {
				MediaType oldType = mediaTypeCandidates.get(i);
				mediaTypeCandidates.set(i, new MediaType(oldType, (Map<String, String>) null));
			}
			
			mediaTypeCandidates.retainAll(SUPPORTED_MEDIA_TYPES);
		}
		
		return mediaTypeCandidates;
	}

	protected static Manager.FhirFormat getFormat(final String accept, final String _format) {
		/*
		 * The _format query parameter allows overriding whatever comes in as the "accept"
		 * header value (for scenarios where the client has no control over the header).
		 */
		if (!StringUtils.isEmpty(_format)) {
			return getFormat(_format);
		} else if (!StringUtils.isEmpty(accept)) {
			List<MediaType> mediaTypeCandidates = getMediaTypeCandidates(accept);
			
			if (!mediaTypeCandidates.isEmpty()) {
				return getFormat(mediaTypeCandidates.get(0).toString());
			} else {
				return getFormat(accept);
			}
		} else {
			return Manager.FhirFormat.JSON;
		}
	}

	private static Manager.FhirFormat getFormat(final String mediaType) {
		switch (mediaType) {
		
			case FORMAT_JSON: //$FALL-THROUGH$
			case TEXT_JSON_VALUE: //$FALL-THROUGH$
			case APPLICATION_JSON_VALUE: //$FALL-THROUGH$
			case ALL_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_JSON_VALUE:
				return Manager.FhirFormat.JSON;
				
			case FORMAT_XML: //$FALL-THROUGH$
			case TEXT_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_XML_VALUE:
				return Manager.FhirFormat.XML;
				
			default:
				throw new NotAcceptableStatusException(SUPPORTED_MEDIA_TYPES);
		}
	}
	
	protected static MediaType getResponseType(final String accept, final String _format) {
		if (!StringUtils.isEmpty(_format)) {
			return getResponseType(_format);
		} else if (!StringUtils.isEmpty(accept)) {
			List<MediaType> mediaTypeCandidates = getMediaTypeCandidates(accept);
			
			if (!mediaTypeCandidates.isEmpty()) {
				return getResponseType(mediaTypeCandidates.get(0).toString());
			} else {
				return getResponseType(accept);
			}

		} else {
			return APPLICATION_FHIR_JSON;
		}
	}

	private static MediaType getResponseType(final String mediaType) {
		switch (mediaType) {
			case TEXT_JSON_VALUE:
				return TEXT_JSON;
				
			case APPLICATION_JSON_VALUE:
				return MediaType.APPLICATION_JSON;
				
			case FORMAT_JSON: //$FALL-THROUGH$
			case ALL_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_JSON_VALUE:
				return APPLICATION_FHIR_JSON;
				
			case TEXT_XML_VALUE:
				return MediaType.TEXT_XML;
				
			case APPLICATION_XML_VALUE:
				return MediaType.APPLICATION_XML;
				
			case FORMAT_XML: //$FALL-THROUGH$
			case APPLICATION_FHIR_XML_VALUE:
				return APPLICATION_FHIR_XML;
				
			default:
				// Any other media type should have been rejected with a 406 Not Acceptable earlier
				throw new IllegalStateException("Unexpected media type '" + mediaType + "' after content negotiation");
		}
	}
	
	protected static <T extends Resource> T toFhirResource(
		final InputStream requestBody, 
		final String contentType, 
		final Class<T> resourceClass
	) {
		try {

			/*
			 * XXX: There is no overriding query parameter for "input" content types, but we still
			 * want to use JSON as the default if no type was specified
			 */
			final FhirFormat format = getFormat(contentType, null);
			
			final Resource fhirResource; 
			switch (format) {
			case JSON:
				fhirResource = new JsonParser().parse(requestBody);
				break;
			case XML:
				fhirResource = new XmlParser().parse(requestBody);
				break;
			default:
				throw new IllegalStateException("Should be already handled in getFormat(contentType, null)");
			}
			
	
			if (!resourceClass.isAssignableFrom(fhirResource.getClass())) {
				throw new BadRequestException(String.format("Expected a complete %s resource as the request body, got %s.",
					resourceClass.getSimpleName(), fhirResource.getClass().getSimpleName()));
			}
			
			return resourceClass.cast(fhirResource);
		
		} catch (IOException e) {
			throw new BadRequestException(String.format("Failed to parse request body as a complete %s resource: %s", resourceClass.getSimpleName(), e.getMessage()));
		}
	}
	
	protected static Parameters toFhirParameters(final InputStream requestBody, final String contentType) {
		return toFhirResource(requestBody, contentType, Parameters.class);
	}
	
	protected static ResponseEntity<byte[]> toResponseEntity(
			final Resource resource, 
			final String accept, 
			final String _format,
			final Boolean _pretty) {
		return toResponseEntity(HttpStatus.OK, resource, accept, _format, _pretty);
	}
	
	protected static ResponseEntity<byte[]> toResponseEntity(
		final HttpStatus httpStatus,
		final Resource resource, 
		final String accept, 
		final String _format,
		final Boolean _pretty
	) {
		final byte[] body = writeToBytes(resource, accept, _format, _pretty);

		return ResponseEntity.status(httpStatus)
			.contentType(getResponseType(accept, _format))
			.body(body);
	}

	private static byte[] writeToBytes(final Resource resource, final String accept, final String _format, final Boolean _pretty) {
		final boolean prettyPrinting = (_pretty != null) && _pretty;
		final FhirFormat format = getFormat(accept, _format);
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			
			switch (format) {
			case JSON:
				new JsonParser().setOutputStyle(prettyPrinting ? OutputStyle.PRETTY : OutputStyle.NORMAL).compose(baos, resource);
				break;
			case XML:
				new XmlParser().setOutputStyle(prettyPrinting ? OutputStyle.PRETTY : OutputStyle.NORMAL).compose(baos, resource);
				break;
			default:
				throw new IllegalStateException("Should be already handled in getFormat(accept, _format)");
			}
		
		} catch (IOException e) {
			throw new BadRequestException(String.format("Failed to serialize FHIR resource to a response body.",
				resource.getClass().getSimpleName()));
		}
		return baos.toByteArray();
	}	
	
	protected static ResponseEntity<byte[]> toResponseEntity(
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
	public @ResponseBody ResponseEntity<byte[]> handle(final Exception ex) {
		if ("broken pipe".equals(Strings.nullToEmpty(Throwables.getRootCause(ex).getMessage()).toLowerCase())) {
	        return null; // socket is closed, cannot return any response    
	    } else {
	    	LOG.error("Exception during processing of a request", ex);
	    	FhirException fhirException = new FhirException(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
	    	return toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, fhirException.toOperationOutcome(), null, null, true);
	    }
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final SyntaxException ex) {
    	FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
    	fhirException.withAdditionalInfo(ex.getAdditionalInfo());
    	return toResponseEntity(HttpStatus.BAD_REQUEST, fhirException.toOperationOutcome(), null, null, true);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final ValidationException ex) {
		FhirException error = new FhirException("Validation error", org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGBADSYNTAX);
		error.withAdditionalInfo(ex.getAdditionalInfo());
		return toResponseEntity(HttpStatus.BAD_REQUEST, error.toOperationOutcome(), null, null, true);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ResponseEntity<byte[]> handle(final UnauthorizedException ex) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGAUTHREQUIRED);
		byte[] body = writeToBytes(fhirException.toOperationOutcome(), null, null, true);
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Basic");
		headers.add("WWW-Authenticate", "Bearer");
		return new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ResponseEntity<byte[]> handle(final BadRequestException ex) {
		return toResponseEntity(HttpStatus.BAD_REQUEST, ex.toOperationOutcome(), null, null, true);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public @ResponseBody ResponseEntity<byte[]> handle(final ForbiddenException ex) {
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
	public @ResponseBody ResponseEntity<byte[]> handle(HttpMessageNotReadableException ex) {
		LOG.trace("Exception during processing of a JSON document", ex);
		FhirException fhirException = new FhirException(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGCANTPARSECONTENT);
		return toResponseEntity(HttpStatus.BAD_REQUEST, fhirException.toOperationOutcome(), null, null, true);
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
	public @ResponseBody ResponseEntity<byte[]> handle(final NotFoundException ex) {
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
	public @ResponseBody ResponseEntity<byte[]> handle(NotImplementedException ex) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGUNKNOWNOPERATION);
		return toResponseEntity(HttpStatus.NOT_IMPLEMENTED, fhirException.toOperationOutcome(), null, null, true);
	}

	/**
	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody ResponseEntity<byte[]> handle(final ConflictException ex) {
		FhirException fhirException = new FhirException(ex.getMessage(), org.hl7.fhir.r4.model.codesystems.OperationOutcome.MSGLOCALFAIL);
		return toResponseEntity(HttpStatus.CONFLICT, fhirException.toOperationOutcome(), null, null, true);
	}
	
}
