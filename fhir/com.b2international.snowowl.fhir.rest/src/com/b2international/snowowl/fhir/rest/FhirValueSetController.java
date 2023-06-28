/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springdoc.api.annotations.ParameterObject;
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
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
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
 * Value Set contains codes from one or more code systems.
 *  
 * @see <a href="https://www.hl7.org/fhir/valueset.html">FHIR:ValueSet</a>
 * @since 6.4
 */
@Tag(description = "ValueSet", name = FhirApiConfig.VALUESET)
@RestController
@RequestMapping(value="/ValueSet", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
public class FhirValueSetController extends AbstractFhirController {
	
	/**
	 * <code><b>POST /ValueSet</b></code>
	 * <p>
	 * Creates the initial revision of the specified value set. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param valueSet - the value set to create
	 */
	@Operation(
		summary = "Create new value set", 
		description = "Create a new FHIR value set.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_CREATE, value = "Create a value set"),
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
		@Parameter(description = "The value set resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<ValueSetWithUriMapping> valueSet,
			
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
				+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for committing the change")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The resource owner (if not set it will fall back to the current authenticated user id)")
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
		
		final ValueSetWithUriMapping fhirValueSetWithUriMapping = valueSet.getChange();
		final ValueSet fhirValueSet = fhirValueSetWithUriMapping.getValueSet();
		final ValueSet fhirValueSetWithId = ValueSet.builder(generatedId)
			.compose(fhirValueSet.getCompose())
			.contacts(fhirValueSet.getContacts())
			.copyright(fhirValueSet.getCopyright())
			.date(fhirValueSet.getDate())
			.description(fhirValueSet.getDescription())
			.expansion(fhirValueSet.getExpansion())
			.experimental(fhirValueSet.getExperimental())
			.extensions(fhirValueSet.getExtensions())
			.identifiers(fhirValueSet.getIdentifiers())
			.immutable(fhirValueSet.getImmutable())
			.implicitRules(fhirValueSet.getImplicitRules())
			.jurisdictions(fhirValueSet.getJurisdictions())
			.language(fhirValueSet.getLanguage())
			.meta(fhirValueSet.getMeta())
			.name(fhirValueSet.getName())
			// .narrative(...) is a special version of .text(...)
			.publisher(fhirValueSet.getPublisher())
			.purpose(fhirValueSet.getPurpose())
			.resourceType(fhirValueSet.getResourceType())
			.status(fhirValueSet.getStatus())
			.text(fhirValueSet.getText())
			.title(fhirValueSet.getTitle())
			// .toolingId(...) is ignored, we will not see it on the input side
			.url(fhirValueSet.getUrl())
			.usageContexts(fhirValueSet.getUsageContexts())
			.version(fhirValueSet.getVersion())
			.build();
		
		fhirValueSetWithUriMapping.setValueSet(fhirValueSetWithId);
		return update(generatedId, valueSet, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
		
	}
	
	/**
	 * <code><b>PUT /ValueSet/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing value set or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the value set identifier
	 * @param valueSet - the new or updated value set (identifier must match the
	 * path parameter)
	 */
	@Operation(
		summary = "Create/update a value set with identifier", 
		description = "Create a new or update an existing FHIR value set.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_UPDATE, value = "Create/update a value set"),
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
		@Parameter(description = "The identifier of the value set")
		@PathVariable(value = "id") 
		final String id,
		
		@Parameter(description = "The value set resource, with optional commit comment")
		@RequestBody 
		final ResourceRequest<ValueSetWithUriMapping> valueSet,
		
		@Parameter(description = "The effective date to use if a version identifier is present in the resource "
			+ "without a corresponding effective date value")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = "The user identifier used for committing the change")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = "The resource owner (if not set it will fall back to the current authenticated user id)")
		@RequestHeader(value = X_OWNER, required = false)
		final String owner,
		
		@Parameter(description = "The user profile name to add to resource settings for client purposes")
		@RequestHeader(value = X_OWNER_PROFILE_NAME, required = false)
		final String ownerProfileName,
		
		@Parameter(description = "The parent bundle's identifier (defaults to root if not present)")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId) {

		final ValueSetWithUriMapping fhirValueSetWithUriMapping = valueSet.getChange();
		final ValueSet fhirValueSet = fhirValueSetWithUriMapping.getValueSet();
		final Map<String, ResourceURI> systemUriOverrides = fhirValueSetWithUriMapping.getSystemUriOverrides();
		
		if (fhirValueSet.getId() == null) {
			throw new BadRequestException("Value set resource did not contain an id element.");
		}
		
		final String idInResource = fhirValueSet.getId().getIdValue();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Value set resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirResourceUpdateResult updateResult = FhirRequests.valueSets()
			.prepareUpdate()
			.setFhirValueSet(fhirValueSet)
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
				final URI locationUri = MvcUriComponentsBuilder.fromController(FhirValueSetController.class)
					.pathSegment(updateResult.getId())
					.build()
					.toUri();
				
				return ResponseEntity.created(locationUri).build();
			default:
				return ResponseEntity.ok().build();
		}
	}
	
	@Operation(
		summary="Retrieve all value sets",
		description="Returns a collection of the supported value sets."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping
	public Promise<Bundle> getValueSets(@ParameterObject FhirValueSetSearchParameters params) {
		return FhirRequests.valueSets().prepareSearch()
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
	 * HTTP GET endpoint for retrieving a value set by its logical identifier.
	 * @param id
	 * @param selectors
	 * @return
	 */
	@Operation(
		summary="Retrieve the value set by id",
		description="Retrieves the value set specified by its logical id."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad request"),
		@ApiResponse(responseCode = "404", description = "Value set not found")
	})
	@GetMapping(value="/{id:**}")
	public Promise<ValueSet> getValueSet(
			@Parameter(description = "The identifier of the ValueSet resource")
			@PathVariable(value = "id") 
			final String id,
	
			@ParameterObject
			final FhirResourceSelectors selectors) {
		return FhirRequests.valueSets().prepareGet(id)
				.setElements(selectors.get_elements())
				.setSummary(selectors.get_summary())
				.buildAsync()
				.execute(getBus());
	}
	
}
