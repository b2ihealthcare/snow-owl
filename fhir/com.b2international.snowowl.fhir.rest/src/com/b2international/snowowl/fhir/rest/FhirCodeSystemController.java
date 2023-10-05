/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.parser.exception.FHIRParserException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.parser.FHIRParser;
import org.linuxforhealth.fhir.model.r5.resource.Bundle;
import org.linuxforhealth.fhir.model.r5.type.Uri;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.converter.BundleConverter_50;
import com.b2international.snowowl.fhir.core.model.converter.CodeSystemConverter_50;
import com.b2international.snowowl.fhir.core.request.FhirRequests;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
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
	
	private static CodeSystem fromRequestBody(final InputStream requestBody) {
		try {
			final var fhirResource = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!fhirResource.is(org.linuxforhealth.fhir.model.r5.resource.CodeSystem.class)) {
				throw new BadRequestException("Expected a complete CodeSystem resource as the request body, got '" 
					+ fhirResource.getClass().getSimpleName() + "'.");
			}
			
			final var fhirCodeSystem = fhirResource.as(org.linuxforhealth.fhir.model.r5.resource.CodeSystem.class);
			return CodeSystemConverter_50.INSTANCE.toInternal(fhirCodeSystem);
		} catch (FHIRParserException e) {
			throw new BadRequestException("Failed to parse request body as a complete CodeSystem resource.");
		}
	}

	/**
	 * <code><b>POST /CodeSystem</b></code>
	 * <p>
	 * Creates the initial revision of the specified code system. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to a FHIR code system resource
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
	@PostMapping(consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> create(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The code system resource", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,
		
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
		
		final CodeSystem soCodeSystem = fromRequestBody(requestBody);

		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		final CodeSystem soCodeSystemWithId = CodeSystem.builder(generatedId)
			.caseSensitive(soCodeSystem.getCaseSensitive())
			.compositional(soCodeSystem.getCompositional())
			.concepts(soCodeSystem.getConcepts())
			.contacts(soCodeSystem.getContacts())
			.content(soCodeSystem.getContent())
			.copyright(soCodeSystem.getCopyright())
			.count(soCodeSystem.getCount())
			.date(soCodeSystem.getDate())
			.description(soCodeSystem.getDescription())
			.experimental(soCodeSystem.getExperimental())
			.extensions(soCodeSystem.getExtensions())
			.filters(soCodeSystem.getFilters())
			.hierarchyMeaning(soCodeSystem.getHierarchyMeaning())
			.identifiers(soCodeSystem.getIdentifiers())
			.implicitRules(soCodeSystem.getImplicitRules())
			.jurisdictions(soCodeSystem.getJurisdictions())
			.language(soCodeSystem.getLanguage())
			.meta(soCodeSystem.getMeta())
			.name(soCodeSystem.getName())
			// .narrative(...) is a special version of .text(...)
			.properties(soCodeSystem.getProperties())
			.publisher(soCodeSystem.getPublisher())
			.purpose(soCodeSystem.getPurpose())
			.resourceType(soCodeSystem.getResourceType())
			.status(soCodeSystem.getStatus())
			.supplements(soCodeSystem.getSupplements())
			.text(soCodeSystem.getText())
			.title(soCodeSystem.getTitle())
			// .toolingId(...) is ignored, we will not see it on the input side
			.url(soCodeSystem.getUrl())
			.usageContexts(soCodeSystem.getUsageContexts())
			.valueSet(soCodeSystem.getValueSet())
			.version(soCodeSystem.getVersion())
			.versionNeeded(soCodeSystem.getVersionNeeded())
			.build();
		
		return createOrUpdate(generatedId, soCodeSystemWithId, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}

	/**
	 * <code><b>PUT /CodeSystem/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing code system or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the code system identifier
	 * @param requestBody - the new or updated code system (resource ID must match the path parameter)
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
	@PutMapping(value = "/{id:**}", consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> update(
			
		@Parameter(description = """
			The identifier of the code system""")
		@PathVariable(value = "id") 
		final String id,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The code system resource", content = { 
			@Content(mediaType = AbstractFhirController.APPLICATION_FHIR_JSON, schema = @Schema(type = "object"))
		})
		final InputStream requestBody,

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
		
		final CodeSystem soCodeSystem = fromRequestBody(requestBody);
		return createOrUpdate(id, soCodeSystem, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}
	
	private ResponseEntity<Void> createOrUpdate(
		String id, 
		CodeSystem soCodeSystem, 
		LocalDate defaultEffectiveDate, 
		String author, 
		String owner, 
		String ownerProfileName, 
		String bundleId
	) {
		
		if (soCodeSystem.getId() == null) {
			throw new BadRequestException("Code system resource did not contain an id element.");
		}
		
		final String idInResource = soCodeSystem.getId().getIdValue();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Code system resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirResourceUpdateResult updateResult = FhirRequests.codeSystems()
			.prepareUpdate()
			.setFhirCodeSystem(soCodeSystem)
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
	@GetMapping(produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> getCodeSystems(
			
		@ParameterObject 
		FhirCodeSystemSearchParameters params,

		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false) 
		final String acceptLanguage
	) {
			
		final UriComponentsBuilder uriBuilder = MvcUriComponentsBuilder.fromMethodName(FhirCodeSystemController.class, "getCodeSystem", "{id}", params, acceptLanguage);
		
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
			.then(soBundle -> {
				var fhirBundle = BundleConverter_50.INSTANCE.fromInternal(soBundle);
				
				// FIXME: Temporary measure to add "fullUrl" to returned bundle entries
				var entries = fhirBundle.getEntry();
				if (!entries.isEmpty()) {
					Bundle.Builder builder = fhirBundle.toBuilder();
					builder.entry(List.of());
					
					for (var entry : entries) {
						if (entry.getResource() != null) {
							final String resourceId = entry.getResource().getId();
							final String fullUrl = uriBuilder.buildAndExpand(Map.of("id", resourceId)).toString();
							
							var entryWithUrl = entry.toBuilder()
								.fullUrl(Uri.of(fullUrl))
								.build();
							
							builder.entry(entryWithUrl);
						}
					}
					
					fhirBundle = builder.build();
				}
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirBundle, baos);
				} catch (FHIRGeneratorException e) {
					throw new BadRequestException("Failed to convert response body to a Bundle resource.");
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
	
	/**
	 * <code><b>GET /CodeSystem/{id}</b></code>
	 * <p>
	 * Retrieves a single code system by its logical identifier or URL.
	 * 
	 * @param id
	 * @param selectors
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
	@GetMapping(value = "/{id:**}", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> getCodeSystem(
			
		@Parameter(description = """
			The identifier of the Code System resource""")
		@PathVariable(value = "id") 
		final String id,
			
		@ParameterObject
		final FhirResourceSelectors selectors,
			
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
			.then(soCodeSystem -> {
				var fhirCodeSystem = CodeSystemConverter_50.INSTANCE.fromInternal(soCodeSystem);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirCodeSystem, baos);
				} catch (FHIRGeneratorException e) {
					throw new BadRequestException("Failed to convert response body to a CodeSystem resource.");
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
	
	/**
	 * <code><b>DELETE /CodeSystem/{id}</b></code>
	 * <p>
	 * Deletes a single code system using the specified identifier.
	 * 
	 * @param id
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
		@RequestParam(defaultValue="false", required=false)
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
