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

import java.net.URI;
import java.time.LocalDate;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemOperations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Code system resource REST endpoint.
 * 
 * @see <a href="https://www.hl7.org/fhir/codesystems.html">FHIR documentation: CodeSystem</a>
 * @see <a href="https://hl7.org/fhir/http.html">FHIR documentation: RESTful API</a>
 * 
 * @since 8.0
 */
@Tag(description = "CodeSystem", name = "CodeSystem", extensions = { 
	@Extension(name = B2I_OPENAPI_X_NAME, properties = { 
		@ExtensionProperty(name = B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/CodeSystem"
	)}
)})
@RestController
@RequestMapping(value = "/CodeSystem", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirCodeSystemController extends AbstractFhirController {
	
	private static final String X_EFFECTIVE_DATE = "X-Effective-Date";
	private static final String X_AUTHOR_PROFILE_NAME = "X-Author-Profile-Name";
	private static final String X_BUNDLE_ID = "X-Bundle-Id";

	/**
	 * <code><b>POST /CodeSystem</b></code>
	 * <p>
	 * Creates the initial revision of the specified code system. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param codeSystem - the code system to create
	 */
	@Operation(
		summary = "Create new code system", 
		description = "Create a new FHIR code system.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_CREATE, value = "Create/update a code system"),
			}),
		}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Resource updated"),
		@ApiResponse(responseCode = "201", description = "Resource created"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@PostMapping(consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> create(
		@Parameter(description = "The code system resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<CodeSystem> codeSystem,
			
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
				+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for commits and set as the resource owner")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The user profile name to add to resource settings")
		@RequestHeader(value = X_AUTHOR_PROFILE_NAME, required = false)
		final String authorProfileName,
		
		@Parameter(description = "The parent bundle's identifier (defaults to root if not present)")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {
		
		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		
		final CodeSystem fhirCodeSystem = codeSystem.getChange();
		final CodeSystem fhirCodeSystemWithId = CodeSystem.builder(generatedId)
			.caseSensitive(fhirCodeSystem.getCaseSensitive())
			.compositional(fhirCodeSystem.getCompositional())
			.concepts(fhirCodeSystem.getConcepts())
			.contacts(fhirCodeSystem.getContacts())
			.content(fhirCodeSystem.getContent())
			.copyright(fhirCodeSystem.getCopyright())
			.count(fhirCodeSystem.getCount())
			.date(fhirCodeSystem.getDate())
			.description(fhirCodeSystem.getDescription())
			.experimental(fhirCodeSystem.getExperimental())
			.extensions(fhirCodeSystem.getExtensions())
			.filters(fhirCodeSystem.getFilters())
			.hierarchyMeaning(fhirCodeSystem.getHierarchyMeaning())
			.identifiers(fhirCodeSystem.getIdentifiers())
			.implicitRules(fhirCodeSystem.getImplicitRules())
			.jurisdictions(fhirCodeSystem.getJurisdictions())
			.language(fhirCodeSystem.getLanguage())
			.meta(fhirCodeSystem.getMeta())
			.name(fhirCodeSystem.getName())
			// .narrative(...) is a special version of .text(...)
			.properties(fhirCodeSystem.getProperties())
			.publisher(fhirCodeSystem.getPublisher())
			.purpose(fhirCodeSystem.getPurpose())
			.resourceType(fhirCodeSystem.getResourceType())
			.status(fhirCodeSystem.getStatus())
			.supplements(fhirCodeSystem.getSupplements())
			.text(fhirCodeSystem.getText())
			.title(fhirCodeSystem.getTitle())
			// .toolingId(...) is ignored, we will not see it on the input side
			.url(fhirCodeSystem.getUrl())
			.usageContexts(fhirCodeSystem.getUsageContexts())
			.valueSet(fhirCodeSystem.getValueSet())
			.version(fhirCodeSystem.getVersion())
			.versionNeeded(fhirCodeSystem.getVersionNeeded())
			.build();
		
		codeSystem.setChange(fhirCodeSystemWithId);
		return update(generatedId, codeSystem, defaultEffectiveDate, author, authorProfileName, bundleId);
		
	}
	
	/**
	 * <code><b>PUT /CodeSystem/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing code system or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the code system identifier
	 * @param codeSystem - the new or updated code system (identifier must match the
	 * path parameter)
	 */
	@Operation(
		summary = "Create/update a code system with identifier", 
		description = "Create a new or update an existing FHIR code system.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_UPDATE, value = "Create/update a code system"),
			}),
		}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Resource updated"),
		@ApiResponse(responseCode = "201", description = "Resource created"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
	})
	@PutMapping(value = "/{id:**}", consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> update(
		@Parameter(description = "The identifier of the code system")
		@PathVariable(value = "id") 
		final String id,
		
		@Parameter(description = "The code system resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<CodeSystem> codeSystem,
		
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
			+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for commits and set as the resource owner")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The user profile name to add to resource settings")
		@RequestHeader(value = X_AUTHOR_PROFILE_NAME, required = false)
		final String authorProfileName,
		
		@Parameter(description = "The parent bundle's identifier (defaults to root if not present)")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {

		final CodeSystem fhirCodeSystem = codeSystem.getChange();
		
		if (fhirCodeSystem.getId() == null) {
			throw new BadRequestException("Code system resource did not contain an id element.");
		}
		
		final String idInResource = fhirCodeSystem.getId().getIdValue();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirCodeSystemOperations.UpdateResult updateResult = FhirRequests.codeSystems()
			.prepareUpdate()
			.setFhirCodeSystem(fhirCodeSystem)
			.setOwner(author)
			.setOwnerProfileName(authorProfileName)
			.setBundleId(bundleId)
			.setDefaultEffectiveDate(defaultEffectiveDate)
			.buildAsync()
			.execute(getBus())
			.getSync();
		
		switch (updateResult) {
			case CREATED:
				final URI locationUri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class)
					.pathSegment(id)
					.build()
					.toUri();
				
				return ResponseEntity.created(locationUri).build();
			default:
				return ResponseEntity.ok().build();
		}
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
	public Promise<Bundle> getCodeSystems(
			@ParameterObject 
			FhirCodeSystemSearchParameters params,

			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
			
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
				.setLocales(acceptLanguage)
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
			final FhirResourceSelectors selectors,
			
			@Parameter(description = "Accepted language tags, in order of preference", example = "en-US;q=0.8,en-GB;q=0.6")
			@RequestHeader(value=HttpHeaders.ACCEPT_LANGUAGE, defaultValue="en-US;q=0.8,en-GB;q=0.6", required=false) 
			final String acceptLanguage) {
		
		return FhirRequests.codeSystems().prepareGet(id)
				.setSummary(selectors.get_summary())
				.setElements(selectors.get_elements())
				.setLocales(acceptLanguage)
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
