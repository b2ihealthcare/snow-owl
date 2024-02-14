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

import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_INTERACTION_CREATE;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_INTERACTION_UPDATE;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_X_INTERACTION;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.core.rest.domain.ResourceRequest;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * A concept map defines a mapping from a set of concepts defined in a code system to one or more concepts defined in other code systems. 
 * Mappings are one way - from the source to the destination.
 *  
 * @see <a href="https://www.hl7.org/fhir/conceptmap.html">ConceptMap</a>
 * @since 7.0
 */
@Tag(description = "ConceptMap", name = FhirApiConfig.CONCEPTMAP)
@RestController
@RequestMapping(value="/ConceptMap", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirConceptMapController extends AbstractFhirController {

	/**
	 * <code><b>POST /ConceptMap</b></code>
	 * <p>
	 * Creates the initial revision of the specified concept map. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param conceptMap - the concept map to create
	 */
	@Operation(
		summary = "Create new concept map", 
		description = "Create a new FHIR concept map.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_CREATE, value = "Create a concept map"),
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
		@Parameter(description = "The concept map resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<ConceptMapWithUriMapping> conceptMap,
			
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
				+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for committing the change")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The resource owner (if not set it will fall back to the X-Author header then to the current authenticated user id)")
		@RequestHeader(value = X_OWNER, required = false)
		final String owner,
		
		@Parameter(description = "The user profile name to add to resource settings for client purposes")
		@RequestHeader(value = X_OWNER_PROFILE_NAME, required = false)
		final String ownerProfileName,
		
		@Parameter(description = "The parent bundle's identifier (defaults to root if not present)")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {
		
		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		
		final ConceptMapWithUriMapping fhirConceptMapWithUriMapping = conceptMap.getChange();
		final ConceptMap fhirConceptMap = fhirConceptMapWithUriMapping.getConceptMap();
		final ConceptMap fhirConceptMapWithId = ConceptMap.builder(generatedId)
			.contacts(fhirConceptMap.getContacts())
			.copyright(fhirConceptMap.getCopyright())
			.date(fhirConceptMap.getDate())
			.description(fhirConceptMap.getDescription())
			.experimental(fhirConceptMap.getExperimental())
			.extensions(fhirConceptMap.getExtensions())
			.groups(fhirConceptMap.getGroups())
			.identifiers(fhirConceptMap.getIdentifiers())
			.implicitRules(fhirConceptMap.getImplicitRules())
			.jurisdictions(fhirConceptMap.getJurisdictions())
			.language(fhirConceptMap.getLanguage())
			.meta(fhirConceptMap.getMeta())
			.name(fhirConceptMap.getName())
			// .narrative(...) is a special version of .text(...)
			.publisher(fhirConceptMap.getPublisher())
			.purpose(fhirConceptMap.getPurpose())
			.resourceType(fhirConceptMap.getResourceType())
			.sourceCanonical(fhirConceptMap.getSourceCanonical())
			.sourceUri(fhirConceptMap.getSourceUri())
			.status(fhirConceptMap.getStatus())
			.targetCanonical(fhirConceptMap.getTargetCanonical())
			.targetUri(fhirConceptMap.getTargetUri())
			.text(fhirConceptMap.getText())
			.title(fhirConceptMap.getTitle())
			// .toolingId(...) is ignored, we will not see it on the input side
			.url(fhirConceptMap.getUrl())
			.usageContexts(fhirConceptMap.getUsageContexts())
			.version(fhirConceptMap.getVersion())
			.build();
		
		fhirConceptMapWithUriMapping.setConceptMap(fhirConceptMapWithId);
		return update(generatedId, conceptMap, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
		
	}
	
	/**
	 * <code><b>PUT /ConceptMap/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing concept map or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the concept map identifier
	 * @param conceptMap - the new or updated concept map (identifier must match the
	 * path parameter)
	 */
	@Operation(
		summary = "Create/update a concept map with identifier", 
		description = "Create a new or update an existing FHIR concept map.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_UPDATE, value = "Create/update a concept map"),
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
		@Parameter(description = "The identifier of the concept map")
		@PathVariable(value = "id") 
		final String id,
		
		@Parameter(description = "The concept map resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<ConceptMapWithUriMapping> conceptMap,
		
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
			+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for committing the change")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The resource owner (if not set it will fall back to the X-Author header then to the current authenticated user id)")
		@RequestHeader(value = X_OWNER, required = false)
		final String owner,
		
		@Parameter(description = "The user profile name to add to resource settings for client purposes")
		@RequestHeader(value = X_OWNER_PROFILE_NAME, required = false)
		final String ownerProfileName,
		
		@Parameter(description = "The parent bundle's identifier (defaults to root if not present)")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {

		final ConceptMapWithUriMapping fhirConceptMapWithUriMapping = conceptMap.getChange();
		final ConceptMap fhirConceptMap = fhirConceptMapWithUriMapping.getConceptMap();
		final Map<String, ResourceURI> systemUriOverrides = fhirConceptMapWithUriMapping.getSystemUriOverrides();
		
		if (fhirConceptMap.getId() == null) {
			throw new BadRequestException("Concept map resource did not contain an id element.");
		}
		
		final String idInResource = fhirConceptMap.getId().getIdValue();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Concept map resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirResourceUpdateResult updateResult = FhirRequests.conceptMaps()
			.prepareUpdate()
			.setFhirConceptMap(fhirConceptMap)
			.setSystemUriOverrides(systemUriOverrides)
			.setAuthor(author)
			.setOwner(owner)
			.setOwnerProfileName(ownerProfileName)
			.setBundleId(bundleId)
			.setDefaultEffectiveDate(defaultEffectiveDate)
			.buildAsync()
			.execute(getBus())
			.getSync();
		
		switch (updateResult.getAction()) {
			case CREATED:
				final URI locationUri = MvcUriComponentsBuilder.fromController(FhirConceptMapController.class)
					.pathSegment(updateResult.getId())
					.build()
					.toUri();
				
				return ResponseEntity.created(locationUri).build();
			default:
				return ResponseEntity.ok().build();
		}
	}
	
	/**
	 * @param params - request parameters
	 * @return bundle of concept maps
	 */
	@Operation(
		summary="Retrieve all concept maps",
		description="Returns a collection of the supported concept maps."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping
	public Promise<Bundle> getConceptMaps(@ParameterObject FhirConceptMapSearchParameters params) {
		return FhirRequests.conceptMaps().prepareSearch()
				.filterByIds(asList(params.get_id()))
				.filterByNames(asList(params.getName()))
				.filterByTitle(params.getTitle())
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
	}
	
	/**
	 * HTTP GET endpoint for retrieving a concept map by its logical identifier
	 * @param id
	 * @param selectors - request selectors
	 * @return {@link ConceptMap}
	 */
	@Operation(
		summary="Retrieve the concept map by id",
		description="Retrieves the concept map specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Concept map not found")
	})
	@GetMapping(value="/{id:**}")
	public Promise<ConceptMap> getConceptMap(
			@Parameter(description = "The identifier of the ConceptMap resource")
			@PathVariable(value = "id") 
			final String id,
	
			@ParameterObject
			final FhirResourceSelectors selectors) {
		return FhirRequests.conceptMaps().prepareGet(id)
				.setElements(selectors.get_elements())
				.setSummary(selectors.get_summary())
				.buildAsync()
				.execute(getBus());
	}
	
}
