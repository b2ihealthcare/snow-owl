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
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.parser.FHIRParser;
import org.linuxforhealth.fhir.model.r5.resource.Bundle;
import org.linuxforhealth.fhir.model.r5.resource.Parameters;
import org.linuxforhealth.fhir.model.r5.resource.Resource;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.linuxforhealth.fhir.model.r5.visitor.Visitable;
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
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.OperationOutcome;
import com.b2international.snowowl.fhir.core.model.converter.BundleConverter_50;
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

	protected static Format getFormat(final String accept, final String _format) {
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
			return Format.JSON;
		}
	}

	private static Format getFormat(final String mediaType) {
		switch (mediaType) {
		
			case FORMAT_JSON: //$FALL-THROUGH$
			case TEXT_JSON_VALUE: //$FALL-THROUGH$
			case APPLICATION_JSON_VALUE: //$FALL-THROUGH$
			case ALL_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_JSON_VALUE:
				return Format.JSON;
				
			case FORMAT_XML: //$FALL-THROUGH$
			case TEXT_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_XML_VALUE:
				return Format.XML;
				
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
			final FHIRParser fhirParser = FHIRParser.parser(getFormat(contentType, null));
			final var fhirResource = fhirParser.parse(requestBody);
	
			if (!fhirResource.is(resourceClass)) {
				throw new BadRequestException(String.format("Expected a complete %s resource as the request body, got %s.",
					resourceClass.getSimpleName(), fhirResource.getClass().getSimpleName()));
			}
			
			return fhirResource.as(resourceClass);
		
		} catch (FHIRParserException e) {
			throw new BadRequestException(String.format("Failed to parse request body as a complete %s resource: %s", resourceClass.getSimpleName(), e.getMessage()));
		}
	}
	
	protected static Parameters toFhirParameters(final InputStream requestBody, final String contentType) {
		return toFhirResource(requestBody, contentType, org.linuxforhealth.fhir.model.r5.resource.Parameters.class);
	}
	
	protected static ResponseEntity<byte[]> toResponseEntity(
		final Visitable fhirResult, 
		final String accept, 
		final String _format,
		final Boolean _pretty
	) {
		final boolean prettyPrinting = (_pretty != null) && _pretty; 
		final FHIRGenerator fhirGenerator = FHIRGenerator.generator(getFormat(accept, _format), prettyPrinting);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			fhirGenerator.generate(fhirResult, baos);
		} catch (FHIRGeneratorException e) {
			throw new BadRequestException(String.format("Failed to serialize FHIR resource to a response body.",
				fhirResult.getClass().getSimpleName()));
		}

		return ResponseEntity.ok()
			.contentType(getResponseType(accept, _format))
			.body(baos.toByteArray());
	}	
	
	protected static ResponseEntity<byte[]> toResponseEntity(
		final com.b2international.snowowl.fhir.core.model.Bundle soBundle, 
		final UriComponentsBuilder fullUrlBuilder, 
		final String accept,
		final String _format,
		final Boolean _pretty
	) {
		var fhirBundle = BundleConverter_50.INSTANCE.fromInternal(soBundle);
		
		// FIXME: Temporary measure to add "fullUrl" to returned bundle entries
		final var entries = fhirBundle.getEntry();
		
		if (!entries.isEmpty()) {
			// Clear entries in builder
			final Bundle.Builder builder = fhirBundle.toBuilder();
			builder.entry(List.of());
			
			// Add "fullUrl" to original entries, add to builder
			for (var entry : entries) {
				if (entry.getResource() != null) {
					final String resourceId = entry.getResource().getId();
					final String fullUrl = fullUrlBuilder.buildAndExpand(Map.of("id", resourceId)).toString();
					
					final var entryWithUrl = entry.toBuilder()
						.fullUrl(Uri.of(fullUrl))
						.build();
					
					builder.entry(entryWithUrl);
				}
			}
			
			fhirBundle = builder.build();
		}
		
		return toResponseEntity(fhirBundle, accept, _format, _pretty);
	}

	/**
	 * Generic <b>Internal Server Error</b> exception handler, serving as a fallback for RESTful client calls.
	 * 
	 * @param ex
	 * @return {@link OperationOutcome} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody OperationOutcome handle(final Exception ex) {
		if ("broken pipe".equals(Strings.nullToEmpty(Throwables.getRootCause(ex).getMessage()).toLowerCase())) {
	        return null; // socket is closed, cannot return any response    
	    } else {
	    	LOG.error("Exception during processing of a request", ex);
	    	FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_BAD_SYNTAX);
	    	return fhirException.toOperationOutcome();
	    }
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final SyntaxException ex) {
    	FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_BAD_SYNTAX);
    	fhirException.withAdditionalInfo(ex.getAdditionalInfo());
    	return fhirException.toOperationOutcome();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final ValidationException ex) {
		FhirException error = FhirException.createFhirError("Validation error", OperationOutcomeCode.MSG_BAD_SYNTAX);
		error.withAdditionalInfo(ex.getAdditionalInfo());
    	return error.toOperationOutcome();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public @ResponseBody ResponseEntity<OperationOutcome> handle(final UnauthorizedException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_AUTH_REQUIRED);
		OperationOutcome body = fhirException.toOperationOutcome();
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Basic");
		headers.add("WWW-Authenticate", "Bearer");
		return new ResponseEntity<>(body, headers, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody OperationOutcome handle(final BadRequestException ex) {
		return ex.toOperationOutcome();
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public @ResponseBody OperationOutcome handle(final ForbiddenException ex) {
		return OperationOutcome.builder()
			.addIssue(Issue.builder()
				.severity(IssueSeverity.ERROR)
				.code(IssueType.FORBIDDEN)
				.diagnostics(ex.getMessage())
				.build())
			.build();
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
		LOG.trace("Exception during processing of a JSON document", ex);
		FhirException fhirException = FhirException.createFhirError(GENERIC_USER_MESSAGE + " Exception: " + ex.getMessage(), OperationOutcomeCode.MSG_CANT_PARSE_CONTENT);
		return fhirException.toOperationOutcome();
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
	public @ResponseBody OperationOutcome handle(final NotFoundException ex) {
		return OperationOutcome.builder()
				.addIssue(
					Issue.builder()
						.severity(IssueSeverity.ERROR)
						.code(IssueType.NOT_FOUND)
						.detailsWithDisplayArgs(OperationOutcomeCode.MSG_NO_EXIST, ex.getKey())
						.diagnostics(ex.getMessage())
						.addLocation(ex.getKey())
					.build()
				)
				.build();
	}

	/**
	 * Exception handler to return <b>Not Implemented</b> when an {@link UnsupportedOperationException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	public @ResponseBody OperationOutcome handle(NotImplementedException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_UNKNOWN_OPERATION);
		return fhirException.toOperationOutcome();
	}

	/**
	 * Exception handler to return <b>Bad Request</b> when an {@link BadRequestException} is thrown from the underlying system.
	 * 
	 * @param ex
	 * @return {@link RestApiError} instance with detailed messages
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public @ResponseBody OperationOutcome handle(final ConflictException ex) {
		FhirException fhirException = FhirException.createFhirError(ex.getMessage(), OperationOutcomeCode.MSG_LOCAL_FAIL);
		return fhirException.toOperationOutcome();
	}
	
}
