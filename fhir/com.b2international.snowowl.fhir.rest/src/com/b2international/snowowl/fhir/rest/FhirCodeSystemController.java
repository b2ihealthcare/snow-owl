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

import static com.b2international.snowowl.core.rest.OpenAPIExtensions.*;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;

import org.hl7.fhir.r5.model.CodeSystem;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.AcceptLanguageHeader;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping(value = "/CodeSystem")
public class FhirCodeSystemController extends AbstractFhirController {
	
	/**
	 * <code><b>POST /CodeSystem</b></code>
	 * <p>
	 * Creates the initial revision of the specified code system. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to a FHIR code system resource
	 * @param contentType
	 * @param defaultEffectiveDate
	 * @param author
	 * @param owner
	 * @param ownerProfileName
	 * @param bundleId
	 * @return
	 */
	@Operation(
		summary = "Create new code system", 
		description = "Create a new FHIR code system.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_CREATE, value = "Create a code system"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "Resource updated")
	@ApiResponse(responseCode = "201", description = "Resource created")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@PostMapping(consumes =	{
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public ResponseEntity<Void> create(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The code system resource", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,
		
		@Parameter(description = """
			The effective date to use if a version identifier is present in the resource without 
			a corresponding effective date value""")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = """
			The user identifier used for committing the change""")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = """
			The resource owner (if not set it will fall back to the X-Author header then to the 
			current authenticated user id)""")
		@RequestHeader(value = X_OWNER, required = false)
		final String owner,
		
		@Parameter(description = """
			The user profile name to add to resource settings for client purposes""")
		@RequestHeader(value = X_OWNER_PROFILE_NAME, required = false)
		final String ownerProfileName,
		
		@Parameter(description = """
			The parent bundle's identifier (defaults to root if not present)""")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId
		
	) {
		
		final var codeSystem = toFhirResource(requestBody, contentType, CodeSystem.class);

		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		codeSystem.setId(generatedId);
		
		return createOrUpdate(generatedId, codeSystem, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}

	/**
	 * <code><b>PUT /CodeSystem/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing code system or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the code system identifier
	 * @param requestBody - the new or updated code system (resource ID must match the path parameter)
	 * @param contentType
	 * @param defaultEffectiveDate
	 * @param author
	 * @param owner
	 * @param ownerProfileName
	 * @param bundleId
	 * @return
	 */
	@Operation(
		summary = "Create a new or update an existing code system with identifier", 
		description = "Create a new or update an existing FHIR code system.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_UPDATE, value = "Create new or update existing code system"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "Resource updated")
	@ApiResponse(responseCode = "201", description = "Resource created")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@PutMapping(value = "/{id:**}", consumes = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public ResponseEntity<Void> update(
			
		@Parameter(description = """
			The identifier of the code system""")
		@PathVariable(value = "id") 
		final String id,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The code system resource", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON_VALUE, schema = @Schema(type = "object")),
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_XML_VALUE, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.CONTENT_TYPE)
		final String contentType,

		@Parameter(description = """
			The effective date to use if a version identifier is present in the resource without 
			a corresponding effective date value""")
		@RequestHeader(value = X_EFFECTIVE_DATE, required = false)
		@DateTimeFormat(iso = ISO.DATE)
		final LocalDate defaultEffectiveDate,
		
		@Parameter(description = """
			The user identifier used for committing the change""")
		@RequestHeader(value = X_AUTHOR, required = false)
		final String author,
		
		@Parameter(description = """
			The resource owner (if not set it will fall back to the X-Author header then to the 
			current authenticated user id)""")
		@RequestHeader(value = X_OWNER, required = false)
		final String owner,
		
		@Parameter(description = """
			The user profile name to add to resource settings for client purposes""")
		@RequestHeader(value = X_OWNER_PROFILE_NAME, required = false)
		final String ownerProfileName,
		
		@Parameter(description = """
			The parent bundle's identifier (defaults to root if not present)""")
		@RequestHeader(value = X_BUNDLE_ID, required = false)
		final String bundleId
		
	) {
		
		final var fhirCodeSystem = toFhirResource(requestBody, contentType, CodeSystem.class);
		
		return createOrUpdate(id, fhirCodeSystem, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}
	
	private ResponseEntity<Void> createOrUpdate(
		String id, 
		CodeSystem codeSystem, 
		LocalDate defaultEffectiveDate, 
		String author, 
		String owner, 
		String ownerProfileName, 
		String bundleId
	) {
		
		if (codeSystem.getId() == null) {
			throw new BadRequestException("Code system resource did not contain an id element.");
		}
		
		final String idInResource = codeSystem.getId();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirResourceUpdateResult updateResult = FhirRequests.codeSystems()
			.prepareUpdate()
			.setFhirCodeSystem(codeSystem)
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
				final URI locationUri = MvcUriComponentsBuilder.fromController(FhirCodeSystemController.class)
					.pathSegment(updateResult.getId())
					.build()
					.toUri();
				
				return ResponseEntity.created(locationUri).build();
				
			default:
				return ResponseEntity.ok().build();
		}
	}
	
	/**
	 * <code><b>GET /CodeSystem</b></code>
	 * <p>
	 * Returns a bundle containing all code systems that match the specified search criteria.
	 * 
	 * @param params - request parameters
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @param acceptLanguage
	 * @return bundle of code systems
	 */
	@Operation(
		summary="Retrieve all code systems",
		description="Returns a collection of the supported code systems.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_SEARCH_TYPE, value = "Search code systems based on some filter criteria"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@GetMapping(produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> getCodeSystems(
			
		@ParameterObject 
		FhirCodeSystemSearchParameters params,
		
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
		final Boolean _pretty,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false) 
		final String acceptLanguage
	) {
			
		// XXX: We are using "{id}" as the placeholder for the "id" path parameter and expand it later
		final UriComponentsBuilder fullUrlBuilder = MvcUriComponentsBuilder.fromMethodName(FhirCodeSystemController.class, "getCodeSystem", 
			"{id}", 
			(FhirResourceSelectors) params,
			accept,
			_format, 
			_pretty, 
			acceptLanguage);
		
		return FhirRequests.codeSystems()
			.prepareSearch()
			.filterByIds(asList(params.get_id()))
			.filterByNames(asList(params.getName()))
			.filterByTitle(params.getTitle())
//			.filterByContent(params.get_content())
			.filterByLastUpdated(params.get_lastUpdated())
			.filterByUrls(Collections3.intersection(params.getUrl(), params.getSystem())) // values defined in both url and system match the same field, compute intersection to simulate ES behavior here
			.filterByVersions(params.getVersion())
			.filterByStatus(params.getStatus())
			.setSearchAfter(params.get_after())
			.setCount(params.get_count())
			// XXX _summary=count may override the default _count=10 value, so order of method calls is important here
			.setSummary(params.get_summary())
			.setElements(params.get_elements())
			.sortByFields(params.get_sort())
			.setLocales(acceptLanguage)
			.buildAsync()
			.execute(getBus())
			.then(soBundle -> toResponseEntity(soBundle, fullUrlBuilder, accept, _format, _pretty));
	}
	
	/**
	 * <code><b>GET /CodeSystem/{id}</b></code>
	 * <p>
	 * Retrieves a single code system by its logical identifier or URL.
	 * 
	 * @param id
	 * @param selectors
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @param acceptLanguage
	 * @return
	 */
	@Operation(
		summary = "Retrieve the code system by id",
		description = "Retrieves the code system specified by its logical id.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_READ, value = "Read the current state of code systems"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Code system not found")
	@GetMapping(value = "/{id:**}", produces = {
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		TEXT_JSON_VALUE,
		TEXT_XML_VALUE,
		APPLICATION_JSON_VALUE,
		APPLICATION_XML_VALUE
	})
	public Promise<ResponseEntity<byte[]>> getCodeSystem(
			
		@Parameter(description = """
			The identifier of the Code System resource""")
		@PathVariable(value = "id") 
		final String id,
			
		@ParameterObject
		final FhirResourceSelectors selectors,
			
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
		final Boolean _pretty,
		
		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false) 
		final String acceptLanguage
		
	) {

		return FhirRequests.codeSystems()
			.prepareGet(id)
			.setSummary(selectors.get_summary())
			.setElements(selectors.get_elements())
			.setLocales(acceptLanguage)
			.buildAsync()
			.execute(getBus())
			.then(codeSystem -> {
				return toResponseEntity(codeSystem, accept, _format, _pretty);
			});
	}
	
	/**
	 * <code><b>DELETE /CodeSystem/{id}</b></code>
	 * <p>
	 * Deletes a single code system using the specified identifier.
	 * 
	 * @param id
	 * @param force
	 * @param author
	 * @return
	 */
	@Operation(
		summary = "Delete the code system specified by the id",
		description = "Delete the code system specified by its logical id.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_DELETE, value = "Delete a code system"),
			}),
		}
	)
	@ApiResponse(responseCode = "204", description = "Deletion successful")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "409", description = "Code system cannot be deleted")
	@DeleteMapping(value = "/{id:**}")
	public ResponseEntity<Void> deleteCodeSystem(
			
		@Parameter(description = """
				The identifier of the Code System resource""")
		@PathVariable(value = "id") 
		final String id,
			
		@Parameter(description = """
				Force deletion flag""")
		@RequestParam(value = "force", defaultValue="false", required=false)
		final Boolean force,

		@Parameter(description = """
			The user identifier used for committing the change""")
		@RequestHeader(value = X_AUTHOR, required = true)
		final String author
		
	) {
		
		try {
			
			FhirRequests.codeSystems()
				.prepareDelete(id)
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
