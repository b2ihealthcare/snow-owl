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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_INTERACTION_READ;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_PROFILE;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_X_INTERACTION;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_X_NAME;
import static com.google.common.collect.Maps.newHashMap;

import java.io.ByteArrayOutputStream;
import java.lang.String;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.linuxforhealth.fhir.model.format.Format;
import org.linuxforhealth.fhir.model.generator.exception.FHIRGeneratorException;
import org.linuxforhealth.fhir.model.r5.generator.FHIRGenerator;
import org.linuxforhealth.fhir.model.r5.resource.CapabilityStatement;
import org.linuxforhealth.fhir.model.r5.resource.OperationDefinition;
import org.linuxforhealth.fhir.model.r5.resource.Resource;
import org.linuxforhealth.fhir.model.r5.resource.TerminologyCapabilities;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.code.*;
import org.osgi.framework.Version;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.core.rest.SnowOwlOpenApiWebMvcResource;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

/**
 * REST end-point for serving content describing the server's capabilities.
 * 
 * @since 8.0.0
 */
@Tag(description="CapabilityStatement", name = FhirApiConfig.CAPABILITY_STATEMENT, extensions = { 
	@Extension(name = B2I_OPENAPI_X_NAME, properties = { 
		@ExtensionProperty(name = B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/CapabilityStatement")
	})
})
@RestController
public class FhirMetadataController extends AbstractFhirController {
	
	@Autowired
	private OpenApiWebMvcResource openApiWebMvcResource;
	
	@Autowired
	private FhirApiConfig config; 
	
	private static record Metadata (
		CapabilityStatement capabilityStatement,
		TerminologyCapabilities terminologyCapabilities,
		Map<String, OperationDefinition> operationMap
	) { 
		// Empty record body
	}
	
	private final Supplier<Metadata> metadataSupplier = Suppliers.memoize(this::initMetadata);

	@Operation(
		summary="Retrieve the capability statement", 
		description="Retrieves this server's capability statement.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_READ, value = "Read the capability statement"),
			}),
		}
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@GetMapping(value = "/metadata", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<byte[]> metadata(
	
		@RequestParam(value = "mode", required = false)
		final String mode 
			
	) {
		final String resourceUrl = MvcUriComponentsBuilder.fromMethodName(FhirMetadataController.class, "metadata", "{mode}")
			.buildAndExpand(Map.of("mode", Strings.nullToEmpty(mode)))
			.toUriString();
		
		final String baseUrl = MvcUriComponentsBuilder.fromController(FhirMetadataController.class)
			.toUriString();
		
		final Resource resourceToReturn;
		
		if ("terminology".equals(mode)) {
			resourceToReturn = metadataSupplier.get().terminologyCapabilities();
		} else {
			var capabilityStatement = metadataSupplier.get().capabilityStatement();
			var implementation = capabilityStatement.getImplementation();
			
			resourceToReturn = capabilityStatement.toBuilder()
				.url(org.linuxforhealth.fhir.model.r5.type.Uri.of(resourceUrl))
				.implementation(implementation.toBuilder()
					.url(org.linuxforhealth.fhir.model.r5.type.Url.of(baseUrl))
					.build())
				.build();
		}
		
		final Format format = Format.JSON;
		final boolean prettyPrinting = true;
		final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			generator.generate(resourceToReturn, baos);
		} catch (FHIRGeneratorException e) {
			throw new BadRequestException("Failed to convert response body to a capability resource.");
		}

		return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
	}
	
	/**
	 * Returns the {@link OperationDefinition} for a given operation.
	 * 
	 * @param operation
	 * @return
	 */
	@Operation(
		summary = "Retrieve an operation definition by its name", 
		description = "Retrieves an operation definition by its compound name (resource$operation)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Operation definition not found")
	@GetMapping(value = "/OperationDefinition/{operation}", produces = { AbstractFhirController.APPLICATION_FHIR_JSON })
	public ResponseEntity<byte[]> operationDefinition(
			
		@PathVariable(value = "operation") 
		final String operation
		
	) {
		
		final Map<String, OperationDefinition> operationMap = metadataSupplier.get().operationMap();
		final OperationDefinition operationDefinition = operationMap.get(operation);
		if (operationDefinition == null) {
			throw new NotFoundException("OperationDefinition", operation);
		}
		
		final Format format = Format.JSON;
		final boolean prettyPrinting = true;
		final FHIRGenerator generator = FHIRGenerator.generator(format, prettyPrinting);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			generator.generate(operationDefinition, baos);
		} catch (FHIRGeneratorException e) {
			throw new BadRequestException("Failed to convert response body to a capability resource.");
		}

		return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
	}
	
	private Metadata initMetadata() {
		// XXX: we know that the subclass is instantiated (in SnowOwlApiConfig)
		final OpenAPI openAPI = ((SnowOwlOpenApiWebMvcResource) openApiWebMvcResource).getOpenApi();
		final Collection<CapabilityStatement.Rest.Resource> resources = collectResources(openAPI);
		final Collection<OperationDefinition> operationDefinitions = collectOperationDefinitions(openAPI);
		
		final CapabilityStatement.Rest.Builder restBuilder = CapabilityStatement.Rest.builder()
			.mode(RestfulCapabilityMode.SERVER)
			.resource(resources);

		final Version bundleVersion = Platform.getBundle(CoreActivator.PLUGIN_ID).getVersion();
		final String softwareVersion = bundleVersion.toString();
		final String qualifier = bundleVersion.getQualifier();
		final Date date = getDateFromQualifier(qualifier);

		final String description = getServiceForClass(SnowOwlConfiguration.class).getDescription();

		final Map<String, OperationDefinition> operationMap = indexOperationDefinitions(operationDefinitions, restBuilder);
		final CapabilityStatement capabilityStatement = createCapabilityStatement(restBuilder, softwareVersion, date, description);
		final TerminologyCapabilities terminologyCapabilities = createTerminologyCapabilities(softwareVersion, date, description);
		
		return new Metadata(capabilityStatement, terminologyCapabilities, operationMap);
	}

	private Collection<CapabilityStatement.Rest.Resource> collectResources(final OpenAPI openAPI) {
		final Paths paths = openAPI.getPaths();
		final List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
		
		return tags.stream()
			// Class-level tags with extensions indicate a resource
			.filter(t -> t.getExtensions() != null && t.getExtensions().containsKey(B2I_OPENAPI_X_NAME))
			.map(t -> {
				final Map<?, ?> nameExtensionMap = (Map<?, ?>) t.getExtensions().get(B2I_OPENAPI_X_NAME);
				final String profile = (String) nameExtensionMap.get(B2I_OPENAPI_PROFILE);
				
				final CapabilityStatement.Rest.Resource.Builder resourceBuilder = CapabilityStatement.Rest.Resource.builder()
					.type(ResourceTypeCode.of(t.getName()))
					.profile(Canonical.of(profile));
			
				// Collect the operations that belong to the same tagged class resource
				paths.values().stream()
					.flatMap(pi -> pi.readOperations().stream())
					.filter(o -> o.getTags().contains(t.getName())
						&& o.getExtensions() != null
						&& o.getExtensions().containsKey(B2I_OPENAPI_X_INTERACTION))
					.forEachOrdered(op -> {
						final Map<String, Object> operationExtensionMap = op.getExtensions();
						final Map<?, ?> interactionMap = (Map<?, ?>) operationExtensionMap.get(B2I_OPENAPI_X_INTERACTION);
				
						interactionMap.entrySet().forEach(e -> {
							final var interactionBuilder = CapabilityStatement.Rest.Resource.Interaction.builder()
								.code(TypeRestfulInteraction.of((String) e.getKey()));
							
							final String value = (String) e.getValue();
							if (!StringUtils.isEmpty(value)) {
								interactionBuilder.documentation(Markdown.of(value));
							}
							
							resourceBuilder.interaction(interactionBuilder.build());
						});
					});
	
				return resourceBuilder.build();
			})
			.sorted(Comparator.comparing(r -> r.getType().getValue()))
			.collect(Collectors.toList());
	}

	private Collection<OperationDefinition> collectOperationDefinitions(final OpenAPI openAPI) {
		final Paths paths = openAPI.getPaths();
	
		// Collect GET requests within the FHIR services hierarchy where the request path contains a '$' character
		return paths.entrySet().stream()
			.filter(e -> {
				final String key = e.getKey();
				final PathItem value = e.getValue();
				
				return key.startsWith(config.getApiBaseUrl())
					&& key.contains("$")
					&& (value.getGet() != null);
			})
			.map(e -> buildOperationDefinition(e.getKey(), e.getValue()))
			.collect(Collectors.toList());
	}

	private Map<String, OperationDefinition> indexOperationDefinitions(
		final Collection<OperationDefinition> operationDefinitions,
		final CapabilityStatement.Rest.Builder restBuilder
	) {
		final Map<String, OperationDefinition> operationMap = newHashMap();

		for (final OperationDefinition operationDefinition : operationDefinitions) {
			for (final Code code : operationDefinition.getResource()) {
				// The "$" separator is already built in
				final String key = code.getValue() + operationDefinition.getName().getValue();
				operationMap.put(key, operationDefinition);
				
				restBuilder.operation(CapabilityStatement.Rest.Resource.Operation.builder()
					.name(operationDefinition.getName())
					.definition(buildOperationUrl(code, operationDefinition))
					.build());
			}
		}
		
		return operationMap;
	}

	private CapabilityStatement createCapabilityStatement(final CapabilityStatement.Rest.Builder restBuilder, String softwareVersion, Date date, String description) {
		
		return CapabilityStatement.builder()
			// .url(...) is added later
			.version(softwareVersion)
			.name("snow-owl-capability-statement")
			.title("FHIR Capability statement for Snow Owl© Terminology Server")
			.description(Markdown.of("This statement describes FHIR resource types and operations supported by the terminology server."))
			.status(PublicationStatus.ACTIVE)
			.date(DateTime.of(Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC)))
			.kind(CapabilityStatementKind.INSTANCE)
			.fhirVersion(FHIRVersion.VERSION_5_0_0)
			.experimental(false)
			.instantiates(Canonical.of("http://hl7.org/fhir/CapabilityStatement/terminology-server"))
			.software(CapabilityStatement.Software.builder()
				.name("Snow Owl©")
				.version(softwareVersion)
				.build()) 
			.implementation(CapabilityStatement.Implementation.builder()
				.url(Url.of("https://b2ihealthcare.com"))
				.description(Markdown.of(description))
				.build())
			.format(Code.of("json"), Code.of(AbstractFhirController.APPLICATION_FHIR_JSON))
			.rest(restBuilder.build())
			.build();
	}
	
	private Date getDateFromQualifier(final String qualifier) {
		if (StringUtils.isEmpty(qualifier) || "qualifier".equals(qualifier)) {
			return Dates.todayGmt();
		}
			
		try {
			return Dates.parse(qualifier, "yyyyMMddHHmm");
		} catch (IllegalArgumentException e) {
			return Dates.todayGmt();
		}
	}

	private Canonical buildOperationUrl(final Code code, final OperationDefinition operationDefinition) {
		
		final String resourceUrl = MvcUriComponentsBuilder.fromMethodName(FhirMetadataController.class, "operationDefinition", "{operation}")
			.buildAndExpand(Map.of("operation", code.getValue() + operationDefinition.getName().getValue()))
			.toUriString();

		return Canonical.of(resourceUrl);
	}

	private OperationDefinition buildOperationDefinition(String key, PathItem pathItem) {
		final String operationName = Iterables.getLast(UriComponentsBuilder.fromPath(key)
			.build()
			.getPathSegments());
		
		final io.swagger.v3.oas.models.Operation getOperation = pathItem.getGet();
		final boolean isInstance = getOperation.getParameters().stream()
			.filter(p -> "path".equals(p.getIn()))
			.findFirst()
			.isPresent();

		final OperationDefinition.Builder operationDefinitionBuilder = OperationDefinition.builder()
			.name(operationName)
			.code(Code.of(operationName))
			.kind(OperationKind.OPERATION)
			.affectsState(false)
			.status(PublicationStatus.ACTIVE)
			.system(false)
			.instance(isInstance)
			.type(true);
		
		getOperation.getTags().forEach(t -> operationDefinitionBuilder.resource(FHIRTypes.of(t)));
		
		// Only interested in 'query' type parameters
		getOperation.getParameters()
			.stream()
			.filter(p -> p.getIn().equals("query"))
			.forEach(p -> {
				final Schema<?> schema = p.getSchema();
				final OperationDefinition.Parameter.Builder parameterBuilder = OperationDefinition.Parameter.builder()
					.name(Code.of(p.getName()))
					.use(OperationParameterUse.IN)
					.documentation(Markdown.of(p.getDescription()));
				
				if ("array".equals(schema.getType())) {
					parameterBuilder.type(FHIRAllTypes.of(((ArraySchema) schema).getItems().getType()));
					
					if (schema.getMinProperties() == null) {
						parameterBuilder.min(0);
					} else {
						parameterBuilder.min(schema.getMinProperties());
					}
					
					if (schema.getMaxProperties() == null) {
						parameterBuilder.max("*");
					} else {
						parameterBuilder.max(schema.getMaxProperties().toString());
					}
				} else {
					parameterBuilder.type(FHIRAllTypes.of(schema.getType()));
					
					if (p.getRequired() == true) {
						parameterBuilder.min(1).max("1");
					} else {
						parameterBuilder.min(0).max("1");
					}
				}
				
				operationDefinitionBuilder.parameter(parameterBuilder.build());
		});
		
		return operationDefinitionBuilder.build();
	}

	private TerminologyCapabilities createTerminologyCapabilities(String softwareVersion, Date date, String description) {
		return TerminologyCapabilities.builder()
			// .url(...) is added later
			.version(softwareVersion)
			.name("snow-owl-terminology-capabilities")
			.title("FHIR Terminology Capabilities of Snow Owl© Terminology Server")
			.description(Markdown.of("This statement describes additional details about supported FHIR terminology resource operations."))
			.status(PublicationStatus.ACTIVE)
			.date(DateTime.of(Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC)))
			.kind(CapabilityStatementKind.INSTANCE)
			.experimental(false)
			.software(TerminologyCapabilities.Software.builder()
				.name("Snow Owl©")
				.version(softwareVersion)
				.build()) 
			.implementation(TerminologyCapabilities.Implementation.builder()
				.description(Markdown.of(description))
				.url(Url.of("https://b2ihealthcare.com"))
				.build())
			// XXX: Not adding any information on code system operations here as it is tied to the resource
			// .codeSystem(...)
			.expansion(TerminologyCapabilities.Expansion.builder()
				.hierarchical(false)
				.paging(true)
				.build())
			.validateCode(TerminologyCapabilities.ValidateCode.builder()
				.translations(false)
				.build())
			.translation(TerminologyCapabilities.Translation.builder()
				.needsMap(true)
				.build())
			// XXX: Not adding statement related to closure maintenance as it is unsupported entirely
			// .closure(...)
			.build();
	}
}
