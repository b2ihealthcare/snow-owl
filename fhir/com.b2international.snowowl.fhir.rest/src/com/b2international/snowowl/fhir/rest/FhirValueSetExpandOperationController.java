/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.converter.ValueSetConverter_50;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.expansion.Expansion;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="http://hl7.org/fhir/R5/valueset-operation-expand.html">4.9.18.1 Operation $expand on ValueSet</a>
 * @since 8.0
 */
@Tag(description = "ValueSet", name = FhirApiConfig.VALUESET)
@RestController
@RequestMapping(value = "/ValueSet")
public class FhirValueSetExpandOperationController extends AbstractFhirController {

	/**
	 * <code><b>GET /ValueSet/$expand</b></code>
	 * 
	 * @param url
	 * @param filter
	 * @param activeOnly
	 * @param displayLanguage
	 * @param includeDesignations
	 * @param withHistorySupplements
	 * @param count
	 * @param after
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Expand a value set",
		description = "Expand a value set specified by its URL."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Value set not found")
	@GetMapping(value = "/$expand", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> expandType(

		@Parameter(description = "Canonical URL of the value set") 
		@RequestParam(value = "url", required = true) 
		final String url,
		
		@Parameter(description = "Textual filter value to use") 
		@RequestParam(value = "filter", required = false)
		final String filter,
		
		@Parameter(description = "Return only active codes or not (default: return both)") 
		@RequestParam(value = "activeOnly", required = false)
		final Boolean activeOnly,
		
		@Parameter(description = "Specify the display language for the returned codes") 
		@RequestParam(value = "displayLanguage", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false)
		final String displayLanguage,
		
		@Parameter(description = "Controls whether concept designations are to be included or excluded in value set expansions") 
		@RequestParam(value = "includeDesignations", required = false)
		final Boolean includeDesignations,
		
		@Parameter(description = "Include historical association components when generating the Value Set expansion response") 
		@RequestParam(value = "withHistorySupplements", required = false)
		final Boolean withHistorySupplements,
		
		@Parameter(description = "The number of codes to return in a page") 
		@RequestParam(value = "count", required = false, defaultValue = "10")
		final Integer count,
		
		@Parameter(description = "Specify the search after value to return the next page") 
		@RequestParam(value = "after", required = false)
		final String after,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {
		
		final UriComponentsBuilder nextUriBuilder = MvcUriComponentsBuilder.fromMethodName(FhirValueSetExpandOperationController.class, "expandType", 
			url, 
			filter, 
			activeOnly, 
			displayLanguage, 
			includeDesignations, 
			withHistorySupplements, 
			count, 
			after,
			accept,
			_format,
			_pretty);
		
		final ExpandValueSetRequest expandRequest = ExpandValueSetRequest.builder()
			.url(url)
			.filter(filter)
			.after(after)
			.activeOnly(activeOnly)
			.count(count)
			.displayLanguage(displayLanguage == null ? null : new Code(displayLanguage))
			.withHistorySupplements(withHistorySupplements)
			.build();
		
		return expand(expandRequest, nextUriBuilder, accept, _format, _pretty);
	}
	
	/**
	 * <code><b>POST /ValueSet/$expand</b></code>
	 * 
	 * @param requestBody
	 * @param contentType
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary="Expand a value set",
		description="Expand a value set specified by a request body."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Value set not found")
	@PostMapping(
		value="/$expand", 
		consumes = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		},
		produces = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		}
	)
	public Promise<ResponseEntity<byte[]>> expand(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The operation's input parameters", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
			
	) {
		
		final var fhirParameters = toFhirParameters(requestBody, contentType);
		final ExpandValueSetRequest request = ValueSetConverter_50.INSTANCE.toExpandRequest(fhirParameters);
		
		if (request.getUrl() == null && request.getValueSet() == null) {
			throw new BadRequestException("Both URL and ValueSet parameters are null.", "ExpandValueSetRequest");
		}

		if (request.getUrl() == null || request.getUrl().getUriValue() == null) {
			throw new BadRequestException("Expand request URL is not defined.", "ExpandValueSetRequest");
		}
		
		if (request.getUrl() != null && 
			request.getValueSet() != null && 
			request.getUrl().getUriValue() != null &&
			request.getValueSet().getUrl().getUriValue() != null &&
			!request.getUrl().getUriValue().equals(request.getValueSet().getUrl().getUriValue())) {
			
			throw new BadRequestException("URL and ValueSet.URL parameters are different.", "ExpandValueSetRequest");
		}
		
		// The "next" parameter will re-use request parameters in query parameter form
		final UriComponentsBuilder nextUriBuilder = MvcUriComponentsBuilder.fromMethodName(FhirValueSetExpandOperationController.class, "expandType", 
			request.getUrl().getUriValue(), 
			request.getFilter(), 
			request.getActiveOnly(), 
			request.getDisplayLanguage().getCodeValue(), 
			request.getIncludeDesignations(), 
			request.getWithHistorySupplements(), 
			request.getCount(), 
			request.getAfter(),
			accept,
			_format,
			_pretty);

		return expand(request, nextUriBuilder, accept, _format, _pretty);
	}

	/**
	 * <code><b>GET /ValueSet/{id}/$expand</b></code>
	 * 
	 * @param id
	 * @param filter
	 * @param activeOnly
	 * @param displayLanguage
	 * @param includeDesignations
	 * @param withHistorySupplements
	 * @param count
	 * @param after
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Expand a value set",
		description = "Expand a value set specified by its logical id."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@ApiResponse(responseCode = "404", description = "Not Found")
	@GetMapping(value = "/{id:**}/$expand", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> expandInstance(
			
		@Parameter(description = "The logical id of the value set to expand") 
		@PathVariable(value = "id", required = true) 
		final String id,
		
		@Parameter(description = "Textual filter value to use") 
		@RequestParam(value = "filter", required = false)
		final String filter,
		
		@Parameter(description = "Return only active codes or not (default: return both)") 
		@RequestParam(value = "activeOnly", required = false)
		final Boolean activeOnly,
		
		@Parameter(description = "Specify the display language for the returned codes") 
		@RequestParam(value = "displayLanguage", defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false)
		final String displayLanguage,
		
		@Parameter(description = "Controls whether concept designations are to be included or excluded in value set expansions") 
		@RequestParam(value = "includeDesignations", required = false)
		final Boolean includeDesignations,
		
		@Parameter(description = "Include historical association components when generating the Value Set expansion response") 
		@RequestParam(value = "withHistorySupplements", required = false)
		final Boolean withHistorySupplements,
		
		@Parameter(description = "The number of codes to return in a page") 
		@RequestParam(value = "count", required = false, defaultValue = "10")
		final Integer count,
		
		@Parameter(description = "Specify the search after value to return the next page") 
		@RequestParam(value = "after", required = false)
		final String after,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", array = @ArraySchema(schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			TEXT_JSON_VALUE,
			TEXT_XML_VALUE,
			APPLICATION_JSON_VALUE,
			APPLICATION_XML_VALUE
		})))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty
		
	) {
		
		final UriComponentsBuilder nextUriBuilder = MvcUriComponentsBuilder.fromMethodName(FhirValueSetExpandOperationController.class, "expandInstance", 
			id, 
			filter, 
			activeOnly, 
			displayLanguage, 
			includeDesignations, 
			withHistorySupplements, 
			count, 
			after,
			accept,
			_format,
			_pretty);
		
		final ExpandValueSetRequest expandRequest = ExpandValueSetRequest.builder()
			// XXX: We use the resource IDs as the URL here 
			.url(id)
			.filter(filter)
			.after(after)
			.activeOnly(activeOnly)
			.count(count)
			.displayLanguage(displayLanguage == null ? null : new Code(displayLanguage))
			.withHistorySupplements(withHistorySupplements)
			.build();
		
		return expand(expandRequest, nextUriBuilder, accept, _format, _pretty);
	}

	private Promise<ResponseEntity<byte[]>> expand(
		final ExpandValueSetRequest expandRequest, 
		final UriComponentsBuilder nextUriBuilder,
		final String accept,
		final String _format,
		final Boolean _pretty
	) {
		return FhirRequests.valueSets().prepareExpand()
			.setRequest(expandRequest)
			.buildAsync()
			.execute(getBus())
			.then(soValueSet -> {
				
				final Expansion soExpansion = soValueSet.getExpansion();
				final Expansion soExpansionWithNext = soExpansion.withNext(searchAfter -> {
					final String next = nextUriBuilder.replaceQueryParam("after", searchAfter)
						.build()
						.toString();
					
					return new Uri(next);
				});
				
				final ValueSet soValueSetWithNext = soValueSet.withExpansion(soExpansionWithNext);
				var fhirValueSet = ValueSetConverter_50.INSTANCE.fromInternal(soValueSetWithNext);
				return toResponseEntity(fhirValueSet, accept, _format, _pretty);
			});
	}
}
