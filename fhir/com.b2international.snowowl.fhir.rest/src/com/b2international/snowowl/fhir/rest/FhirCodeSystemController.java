/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.core.rest.OpenAPIExtensions.*;

import java.time.LocalDate;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Code system resource REST endpoint.
 * @see <a href="https://www.hl7.org/fhir/codesystems.html">FHIR:CodeSystem</a>
 * 
 * @since 8.0
 */
@Tag(description = "CodeSystem", name = "CodeSystem", extensions = 
@Extension(name = B2I_OPENAPI_X_NAME, properties = {
	@ExtensionProperty(name = B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/CodeSystem")
}))
@RestController
@RequestMapping(value="/CodeSystem", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemController extends AbstractFhirController {
	
	private static final String X_EFFECTIVE_DATE = "X-Effective-Date";
	private static final String X_AUTHOR_PROFILE_NAME = "X-Author-Profile-Name";
	private static final String X_BUNDLE_ID = "X-Bundle-Id";

	/**
	 * HTTP PUT /CodeSystem
	 * @param codeSystem - the new or updated code system to add
	 */
	@Operation(
		summary="Create/Update a code system",
		description="Create new or Update existing FHIR code system.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_CREATE, value = "Create/Update a code system"),
			}),
		}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@PutMapping(consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> createOrUpdateCodeSystem(
		@RequestBody 
		final ResourceRequest<CodeSystem> codeSystem,
		
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@RequestHeader(value = X_AUTHOR_PROFILE_NAME, required = false)
		final String authorProfileName,
		
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {

		FhirRequests.codeSystems()
			.prepareUpdate()
			.setFhirCodeSystem(codeSystem.getChange())
			.setOwner(author)
			.setOwnerProfileName(authorProfileName)
			.setBundleId(bundleId)
			.setDefaultEffectiveDate(defaultEffectiveDate)
			.buildAsync()
			.execute(getBus())
			.getSync();
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * HTTP GET /CodeSystem
	 * @param params - request parameters
	 * @return bundle of code systems
	 */
	@Operation(
		summary="Retrieve all code systems",
		description="Returns a collection of the supported code systems.", 
	
		extensions = {
				@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
					@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_READ, value = "Read code systems"),
				}),
			}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@GetMapping
	public Promise<Bundle> getCodeSystems(@ParameterObject FhirCodeSystemSearchParameters params) {
		//TODO: replace this with something more general as described in
		//https://docs.spring.io/spring-hateoas/docs/current/reference/html/
//		String uri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class).build().toString();
		
		return FhirRequests.codeSystems().prepareSearch()
				.filterByIds(asList(params.get_id()))
				.filterByNames(asList(params.getName()))
				.filterByTitle(params.getTitle())
//				.filterByContent(params.get_content())
				.filterByLastUpdated(params.get_lastUpdated())
				.filterByUrls(Collections3.intersection(params.getUrl(), params.getSystem())) // values defined in both url and system match the same field, compute intersection to simulate ES behavior here
				.filterByVersions(params.getVersion())
				.setSearchAfter(params.get_after())
				.setCount(params.get_count())
				// XXX _summary=count may override the default _count=10 value, so order of method calls is important here
				.setSummary(params.get_summary())
				.setElements(params.get_elements())
				.sortByFields(params.get_sort())
				.buildAsync()
				.execute(getBus());
		// TODO convert returned Bundle entries to have a fullUrl, either here or supply current url to request via header param
//				String resourceUrl = String.join("/", uri, codeSystem.getId().getIdValue());
//				Entry entry = new Entry(new Uri(resourceUrl), codeSystem);
	}
	
	/**
	 * HTTP GET for retrieving a code system by its logical identifier
	 * @param id
	 * @param selectors
	 * @return
	 */
	@Operation(
		summary = "Retrieve the code system by id",
		description = "Retrieves the code system specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Code system not found")
	})
	@GetMapping(value="/{id:**}")
	public Promise<CodeSystem> getCodeSystem(
			@Parameter(description = "The identifier of the Code System resource")
			@PathVariable(value = "id") 
			final String id,
			
			@ParameterObject
			final FhirResourceSelectors selectors) {
		
		return FhirRequests.codeSystems().prepareGet(id)
				.setSummary(selectors.get_summary())
				.setElements(selectors.get_elements())
				.buildAsync()
				.execute(getBus());
	}
	
	/**
	 * HTTP DELETE for deleting a code system by its logical identifier
	 * @param id
	 * @return
	 */
	@Operation(
		summary = "Delete the code system specified by the id",
		description = "Delete the code system specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Deletion successful"),
		@ApiResponse(responseCode = "404", description = "Not found"),
		@ApiResponse(responseCode = "409", description = "Code system cannot be deleted")
	})
	@DeleteMapping(value="/{id:**}")
	public ResponseEntity<Void> deleteCodeSystem(
			@Parameter(description = "The identifier of the Code System resource")
			@PathVariable(value = "id") 
			final String id,
			
			@Parameter(description = "Force deletion flag")
			@RequestParam(defaultValue="false", required=false)
			final Boolean force,

			@RequestHeader(value = X_AUTHOR, required = true)
			final String author) {
		try {
			FhirRequests.codeSystems().prepareDelete(id)
				.force(force)
				.build(author, String.format("Deleting code system %s", id))
				.execute(getBus())
				.getSync();
			return ResponseEntity.noContent().build();
		} catch (NotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
			
		}
	}
	
}
