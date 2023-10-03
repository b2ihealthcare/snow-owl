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
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.converter.BundleConverter_50;
import com.b2international.snowowl.fhir.core.model.converter.ValueSetConverter_50;
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
 * Value set resource REST endpoint.
 * 
 * @see <a href="https://hl7.org/fhir/valueset.html">FHIR documentation: ValueSet</a>
 * @see <a href="https://hl7.org/fhir/http.html">FHIR documentation: RESTful API</a>
 * 
 * @since 8.0
 */
@Tag(description = "ValueSet", name = "ValueSet", extensions = { 
	@Extension(name = B2I_OPENAPI_X_NAME, properties = { 
		@ExtensionProperty(name = B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/ValueSet"
	)}
)})
@RestController
@RequestMapping(value = "/ValueSet")
public class FhirValueSetController extends AbstractFhirController {
	
	private static ValueSet fromRequestBody(final InputStream requestBody) {
		try {
			final var fhirResource = FHIRParser.parser(Format.JSON).parse(requestBody);
		
			if (!fhirResource.is(org.linuxforhealth.fhir.model.r5.resource.ValueSet.class)) {
				throw new BadRequestException("Expected a complete ValueSet resource as the request body, got '" 
					+ fhirResource.getClass().getSimpleName() + "'.");
			}
			
			final var fhirValueSet = fhirResource.as(org.linuxforhealth.fhir.model.r5.resource.ValueSet.class);
			return ValueSetConverter_50.INSTANCE.toInternal(fhirValueSet);
		} catch (FHIRParserException e) {
			throw (BadRequestException) new BadRequestException("Failed to parse request body as a complete ValueSet resource.")
				.initCause(e);
		}
	}

	/**
	 * <code><b>POST /ValueSet</b></code>
	 * <p>
	 * Creates the initial revision of the specified value set. The identifier is randomly assigned and ignored if
	 * present in the input resource.
	 * 
	 * @param requestBody - an {@link InputStream} whose contents can be deserialized to a FHIR value set resource
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
	@ApiResponse(responseCode = "200", description = "Resource updated")
	@ApiResponse(responseCode = "201", description = "Resource created")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@PostMapping(consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> create(
			
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The value set resource", content = { 
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
		
		final ValueSet soValueSet = fromRequestBody(requestBody);

		// Ignore the input identifier on purpose and assign one locally
		final String generatedId = IDs.base62UUID();
		final ValueSet soValueSetWithId = ValueSet.builder(generatedId)
			.compose(soValueSet.getCompose())
			.contacts(soValueSet.getContacts())
			.copyright(soValueSet.getCopyright())
			.date(soValueSet.getDate())
			.description(soValueSet.getDescription())
			.expansion(soValueSet.getExpansion())
			.experimental(soValueSet.getExperimental())
			.extensions(soValueSet.getExtensions())
			.identifiers(soValueSet.getIdentifiers())
			.immutable(soValueSet.getImmutable())
			.implicitRules(soValueSet.getImplicitRules())
			.jurisdictions(soValueSet.getJurisdictions())
			.language(soValueSet.getLanguage())
			.meta(soValueSet.getMeta())
			.name(soValueSet.getName())
			// .narrative(...) is a special version of .text(...)
			.publisher(soValueSet.getPublisher())
			.purpose(soValueSet.getPurpose())
			.resourceType(soValueSet.getResourceType())
			.status(soValueSet.getStatus())
			.text(soValueSet.getText())
			.title(soValueSet.getTitle())
			// .toolingId(...) is ignored, we will not see it on the input side
			.url(soValueSet.getUrl())
			.usageContexts(soValueSet.getUsageContexts())
			.version(soValueSet.getVersion())
			.build();
		
		return createOrUpdate(generatedId, soValueSetWithId, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}

	/**
	 * <code><b>PUT /ValueSet/{id}</b></code>
	 * <p>
	 * Creates a new revision for an existing value set or creates the initial
	 * revision if it did not already exist.
	 * 
	 * @param id - the value set identifier
	 * @param requestBody - the new or updated value set (resource ID must match the path parameter)
	 */
	@Operation(
		summary = "Create a new or update an existing value set with identifier", 
		description = "Create a new or update an existing FHIR value set.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_UPDATE, value = "Create new or update existing value set"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "Resource updated")
	@ApiResponse(responseCode = "201", description = "Resource created")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@PutMapping(value = "/{id:**}", consumes = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<Void> update(
			
		@Parameter(description = """
			The identifier of the value set""")
		@PathVariable(value = "id") 
		final String id,
		
		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The value set resource", content = { 
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
		
		final ValueSet soValueSet = fromRequestBody(requestBody);
		return createOrUpdate(id, soValueSet, defaultEffectiveDate, author, owner, ownerProfileName, bundleId);
	}
	
	private ResponseEntity<Void> createOrUpdate(
		String id, 
		ValueSet soValueSet, 
		LocalDate defaultEffectiveDate, 
		String author, 
		String owner, 
		String ownerProfileName, 
		String bundleId
	) {
		
		if (soValueSet.getId() == null) {
			throw new BadRequestException("Value set resource did not contain an id element.");
		}
		
		final String idInResource = soValueSet.getId().getIdValue();
		if (!id.equals(idInResource)) {
			throw new BadRequestException("Value set resource ID '" + idInResource + "' disagrees with '" + id + "' provided in the request URL.");
		}
		
		final FhirResourceUpdateResult updateResult = FhirRequests.valueSets()
			.prepareUpdate()
			.setFhirValueSet(soValueSet)
			.setAuthor(author)
			.setOwner(owner)
			.setOwnerProfileName(ownerProfileName)
			.setBundleId(bundleId)
			.setDefaultEffectiveDate(defaultEffectiveDate)
			// .setSystemUriOverrides(systemUriOverrides)
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
	
	/**
	 * <code><b>GET /ValueSet</b></code>
	 * <p>
	 * Returns a bundle containing all value sets that match the specified search criteria.
	 * 
	 * @param params - request parameters
	 * @return bundle of value sets
	 */
	@Operation(
		summary="Retrieve all value sets",
		description="Returns a collection of the supported value sets.", 
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_SEARCH_TYPE, value = "Search value sets based on some filter criteria"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@GetMapping(produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> getValueSets(
			
		@ParameterObject 
		FhirValueSetSearchParameters params,

		@Parameter(description = "Accepted language tags, in order of preference", example = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER)
		@RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = AcceptLanguageHeader.DEFAULT_ACCEPT_LANGUAGE_HEADER, required = false) 
		final String acceptLanguage
	) {
			
		final UriComponentsBuilder uriBuilder = MvcUriComponentsBuilder.fromMethodName(FhirValueSetController.class, "getValueSet", "{id}", params, acceptLanguage);
		
		return FhirRequests.valueSets()
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
					e.printStackTrace();
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
	
	/**
	 * <code><b>GET /ValueSet/{id}</b></code>
	 * <p>
	 * Retrieves a single value set by its logical identifier or URL.
	 * 
	 * @param id
	 * @param selectors
	 * @return
	 */
	@Operation(
		summary = "Retrieve the value set by id",
		description = "Retrieves the value set specified by its logical id.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_READ, value = "Read the current state of value sets"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "400", description = "Bad request")
	@ApiResponse(responseCode = "404", description = "Value set not found")
	@GetMapping(value = "/{id:**}", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public Promise<ResponseEntity<byte[]>> getValueSet(
			
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

		return FhirRequests.valueSets()
			.prepareGet(id)
			.setSummary(selectors.get_summary())
			.setElements(selectors.get_elements())
			.setLocales(acceptLanguage)
			.buildAsync()
			.execute(getBus())
			.then(soValueSet -> {
				var fhirValueSet = ValueSetConverter_50.INSTANCE.fromInternal(soValueSet);
				
				final Format format = Format.JSON;
				final boolean prettyPrinting = true;
				final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					generator.generate(fhirValueSet, baos);
				} catch (FHIRGeneratorException e) {
					e.printStackTrace();
				}

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			});
	}
	
	/**
	 * <code><b>DELETE /ValueSet/{id}</b></code>
	 * <p>
	 * Deletes a single value set using the specified identifier.
	 * 
	 * @param id
	 * @return
	 */
	@Operation(
		summary = "Delete the value set specified by the id",
		description = "Delete the value set specified by its logical id.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_DELETE, value = "Delete a value set"),
			}),
		}
	)
	@ApiResponse(responseCode = "204", description = "Deletion successful")
	@ApiResponse(responseCode = "404", description = "Not found")
	@ApiResponse(responseCode = "409", description = "Value set cannot be deleted")
	@DeleteMapping(value = "/{id:**}")
	public ResponseEntity<Void> deleteValueSet(
			
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
			
			FhirRequests.valueSets()
				.prepareDelete(id)
				.force(force)
				.build(author, String.format("Deleting value set %s", id))
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
