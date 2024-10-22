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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_PROFILE;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_X_INTERACTION;
import static com.b2international.snowowl.core.rest.OpenAPIExtensions.B2I_OPENAPI_X_NAME;
import static com.b2international.snowowl.fhir.rest.FhirMediaType.*;
import static com.google.common.collect.Maps.newHashMap;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.CapabilityStatement.RestfulCapabilityMode;
import org.hl7.fhir.r5.model.CapabilityStatement.TypeRestfulInteraction;
import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.model.Enumerations.*;
import org.hl7.fhir.r5.model.OperationDefinition.OperationKind;
import org.osgi.framework.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.core.rest.SnowOwlOpenApiWebMvcResource;
import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

/**
 * REST end-point for serving content describing the server's capabilities.
 * 
 * @since 8.0.0
 */
@Tag(description="CapabilityStatement", name = FhirApiConfig.CAPABILITY_STATEMENT)
@RestController
public class FhirMetadataController extends AbstractFhirController {
	
	private static final String OPERATION_SEPARATOR = "-it-";

	@Autowired
	private SnowOwlOpenApiWebMvcResource openApiResource;
	
	@Autowired
	private FhirApiConfig config; 
	
	private static record Metadata (
		Map<FHIRVersion, CapabilityStatement> capabilityStatementMap,
		TerminologyCapabilities terminologyCapabilities,
		Map<String, OperationDefinition> operationMap
	) { 
		// Empty record body
	}
	
	private final Supplier<Metadata> metadataSupplier = Suppliers.memoize(this::initMetadata);

	/**
	 * @param mode
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@ApiResponse(responseCode = "200", description = "OK")
	@GetMapping(value = "/metadata", produces = {
		APPLICATION_FHIR_JSON_5_0_0_VALUE,
		APPLICATION_FHIR_JSON_4_3_0_VALUE,
		APPLICATION_FHIR_JSON_4_0_1_VALUE,
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_JSON_VALUE,
		TEXT_JSON_VALUE,
		
		APPLICATION_FHIR_XML_5_0_0_VALUE,
		APPLICATION_FHIR_XML_4_3_0_VALUE,
		APPLICATION_FHIR_XML_4_0_1_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		APPLICATION_XML_VALUE,
		TEXT_XML_VALUE
	})
	public ResponseEntity<byte[]> metadata(
	
		@RequestParam(value = "mode", required = false)
		final String mode,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
			
	) {
		final String resourceUrl = MvcUriComponentsBuilder.fromMethodName(FhirMetadataController.class, "metadata", 
			StringUtils.isEmpty(mode) ? null : "{mode}", 
			accept, 
			_format, 
			_pretty
		)
		.buildAndExpand(Map.of("mode", Strings.nullToEmpty(mode)))
		.toUriString();
		
		final String baseUrl = MvcUriComponentsBuilder.fromController(FhirMetadataController.class)
			.toUriString();
		
		if ("terminology".equals(mode)) {
			var terminologyCapabilities = metadataSupplier.get().terminologyCapabilities();
			return toResponseEntity(terminologyCapabilities, accept, _format, _pretty); 
		} else {
			FhirMediaType fhirMediaType = FhirMediaType.parse(accept, _format);
			FHIRVersion fhirVersion = fhirMediaType.getFhirVersion();
			
			var capabilityStatement = metadataSupplier.get()
				.capabilityStatementMap()
				.get(fhirVersion);
			
			// override/set URL values
			capabilityStatement.setUrl(resourceUrl);
			capabilityStatement.getImplementation().setUrl(baseUrl);
			
			return toResponseEntity(capabilityStatement, accept, _format, _pretty);
		}
	}
	
	/**
	 * Returns the {@link OperationDefinition} for a given operation.
	 * 
	 * @param operation
	 * @param accept
	 * @param _format
	 * @param _pretty
	 * @return
	 */
	@Operation(
		summary = "Retrieve an operation definition by its name", 
		description = "Retrieves an operation definition by its compound name (resource$operation)."
	)
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiResponse(responseCode = "404", description = "Operation definition not found")
	@GetMapping(value = "/OperationDefinition/{operation}", produces = {
		APPLICATION_FHIR_JSON_5_0_0_VALUE,
		APPLICATION_FHIR_JSON_4_3_0_VALUE,
		APPLICATION_FHIR_JSON_4_0_1_VALUE,
		APPLICATION_FHIR_JSON_VALUE,
		APPLICATION_JSON_VALUE,
		TEXT_JSON_VALUE,
		
		APPLICATION_FHIR_XML_5_0_0_VALUE,
		APPLICATION_FHIR_XML_4_3_0_VALUE,
		APPLICATION_FHIR_XML_4_0_1_VALUE,
		APPLICATION_FHIR_XML_VALUE,
		APPLICATION_XML_VALUE,
		TEXT_XML_VALUE
	})
	public ResponseEntity<byte[]> operationDefinition(
			
		@PathVariable(value = "operation") 
		final String operation,
		
		@Parameter(hidden = true)
		@RequestHeader(value = HttpHeaders.ACCEPT)
		final String accept,

		@Parameter(description = "Alternative response format", schema = @Schema(allowableValues = {
			APPLICATION_FHIR_JSON_5_0_0_VALUE,
			APPLICATION_FHIR_JSON_4_3_0_VALUE,
			APPLICATION_FHIR_JSON_4_0_1_VALUE,
			APPLICATION_FHIR_JSON_VALUE,
			APPLICATION_JSON_VALUE,
			TEXT_JSON_VALUE,
			
			APPLICATION_FHIR_XML_5_0_0_VALUE,
			APPLICATION_FHIR_XML_4_3_0_VALUE,
			APPLICATION_FHIR_XML_4_0_1_VALUE,
			APPLICATION_FHIR_XML_VALUE,
			APPLICATION_XML_VALUE,
			TEXT_XML_VALUE
		}))
		@RequestParam(value = "_format", required = false)
		final String _format,
		
		@Parameter(description = "Controls pretty-printing of response")
		@RequestParam(value = "_pretty", required = false)
		final Boolean _pretty		
		
	) {
		
		final Map<String, OperationDefinition> operationMap = metadataSupplier.get().operationMap();
		final var fhirOperationDefinition = operationMap.get(operation);
		if (fhirOperationDefinition == null) {
			throw new NotFoundException("OperationDefinition", operation);
		}
		
		return toResponseEntity(fhirOperationDefinition, accept, _format, _pretty);
	}
	
	private Metadata initMetadata() {
		// get the ENGLISH version of the OpenAPI and use it to populate the FHIR metadata
		final OpenAPI openAPI = openApiResource.getOpenApi(Locale.ENGLISH);
		
		final Collection<OperationDefinition> operationDefinitions = collectOperationDefinitions(openAPI);
		final Map<String, OperationDefinition> operationMap = indexOperationDefinitions(operationDefinitions);
		final List<CapabilityStatement.CapabilityStatementRestResourceComponent> resources = collectResources(openAPI, operationMap);
		
		final CapabilityStatement.CapabilityStatementRestComponent rest = new CapabilityStatement.CapabilityStatementRestComponent()
			.setMode(RestfulCapabilityMode.SERVER)
			.setResource(resources);

		final Version bundleVersion = Platform.getBundle(CoreActivator.PLUGIN_ID).getVersion();
		final String softwareVersion = bundleVersion.toString();
		final String qualifier = bundleVersion.getQualifier();
		final Date date = getDateFromQualifier(qualifier);

		final String description = getServiceForClass(SnowOwlConfiguration.class).getDescription();

		final TerminologyCapabilities terminologyCapabilities = createTerminologyCapabilities(softwareVersion, date, description);
		final CapabilityStatement capabilityStatement = createCapabilityStatement(rest, softwareVersion, date, description);

		// Set the fhirVersion property in each instance accordingly 
		final Map<FHIRVersion, CapabilityStatement> capabilityStatementMap = FhirMediaType.SUPPORTED_FHIR_VERSIONS
			.stream()
			.collect(Collectors.toMap(v -> v, v -> {
				return capabilityStatement.copy().setFhirVersion(v);
			}));
		
		return new Metadata(capabilityStatementMap, terminologyCapabilities, operationMap);
	}

	private List<CapabilityStatement.CapabilityStatementRestResourceComponent> collectResources(final OpenAPI openAPI, final Map<String, OperationDefinition> operationMap) {
		final Paths paths = openAPI.getPaths();
		final List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
		
		return tags.stream()
			// Class-level tags with extensions indicate a resource
			.filter(t -> t.getExtensions() != null && t.getExtensions().containsKey(B2I_OPENAPI_X_NAME))
			.map(tag -> convertToCapabilityRestResource(tag, paths, operationMap))
			.sorted(Comparator.comparing(r -> r.getType()))
			.toList();
	}

	private CapabilityStatement.CapabilityStatementRestResourceComponent convertToCapabilityRestResource(io.swagger.v3.oas.models.tags.Tag tag, Paths paths, Map<String, OperationDefinition> operationMap) {
		final String resourceType = tag.getName();
		final Map<?, ?> nameExtensionMap = (Map<?, ?>) tag.getExtensions().get(B2I_OPENAPI_X_NAME);
		final String profile = (String) nameExtensionMap.get(B2I_OPENAPI_PROFILE);
		
		final CapabilityStatement.CapabilityStatementRestResourceComponent resource = new CapabilityStatement.CapabilityStatementRestResourceComponent()
			.setType(resourceType)
			.setProfile(profile);
	
		// Collect the interactions that belong to the same tagged class resource
		paths.values().stream()
			.flatMap(pi -> pi.readOperations().stream())
			.filter(o -> o.getTags().contains(resourceType)
				&& o.getExtensions() != null
				&& o.getExtensions().containsKey(B2I_OPENAPI_X_INTERACTION))
			.forEachOrdered(op -> {
				final Map<String, Object> operationExtensionMap = op.getExtensions();
				final Map<?, ?> interactionMap = (Map<?, ?>) operationExtensionMap.get(B2I_OPENAPI_X_INTERACTION);
		
				interactionMap.entrySet().forEach(e -> {
					final var interaction = new CapabilityStatement.ResourceInteractionComponent()
						.setCode(TypeRestfulInteraction.fromCode((String) e.getKey()));
					
					final String value = (String) e.getValue();
					if (!StringUtils.isEmpty(value)) {
						interaction.setDocumentation(value);
					}
					
					resource.addInteraction(interaction);
				});
			});
		
		// Collect operations for the resource as well
		paths.entrySet().stream()
			.filter(e -> {
				final String key = e.getKey();
				final PathItem value = e.getValue();

				return key.startsWith(config.getApiBaseUrl())
					&& key.contains("$")
					&& (value.getGet() != null);
			})
			.map(e -> String.join(OPERATION_SEPARATOR, resourceType, getOperationName(e.getKey())))
			.distinct()
			.forEachOrdered(operation -> {
				final OperationDefinition operationDefinition = operationMap.get(operation);
				if (operationDefinition != null) {
					resource.addOperation(new CapabilityStatement.CapabilityStatementRestResourceOperationComponent()
						.setName(operationDefinition.getName())
						.setDefinition(buildOperationUrl(operation))
					);
				}
			});

		return resource;
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

	private Map<String, OperationDefinition> indexOperationDefinitions(final Collection<OperationDefinition> operationDefinitions) {
		final Map<String, OperationDefinition> operationMap = newHashMap();

		for (final OperationDefinition operationDefinition : operationDefinitions) {
			for (final Enumeration<VersionIndependentResourceTypesAll> code : operationDefinition.getResource()) {
				final String key = String.join(OPERATION_SEPARATOR, code.getCode(), operationDefinition.getName());
				operationMap.put(key, operationDefinition);
			}
		}
		
		return operationMap;
	}

	private CapabilityStatement createCapabilityStatement(final CapabilityStatement.CapabilityStatementRestComponent rest, String softwareVersion, Date date, String description) {
		
		return new CapabilityStatement()
			// .url(...) is added later
			.setVersion(softwareVersion)
			.setName("snow-owl-capability-statement")
			.setTitle("FHIR Capability statement for Snow Owl© Terminology Server")
			.setDescription("This statement describes FHIR resource types and operations supported by the terminology server.")
			.setStatus(PublicationStatus.ACTIVE)
			.setDate(date)
			.setKind(CapabilityStatementKind.INSTANCE)
			.setFhirVersion(FHIRVersion._5_0_0)
			.setExperimental(false)
			.setInstantiates(List.of(new CanonicalType("http://hl7.org/fhir/CapabilityStatement/terminology-server")))
			.setSoftware(new CapabilityStatement.CapabilityStatementSoftwareComponent()
				.setName("Snow Owl©")
				.setVersion(softwareVersion)
			) 
			.setImplementation(new CapabilityStatement.CapabilityStatementImplementationComponent()
				.setUrl("https://b2ihealthcare.com")
				.setDescription(description)
			)
			.setFormat(List.of(
				new CodeType(APPLICATION_FHIR_JSON_VALUE),
				new CodeType(APPLICATION_JSON_VALUE),
				new CodeType(TEXT_JSON_VALUE),
				new CodeType(APPLICATION_FHIR_XML_VALUE),
				new CodeType(APPLICATION_XML_VALUE),
				new CodeType(TEXT_XML_VALUE)
			))
			.setRest(List.of(rest));
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

	private String buildOperationUrl(final String operation) {
		return MvcUriComponentsBuilder.fromMethodName(FhirMetadataController.class, "operationDefinition", "{operation}", null, null, null)
			.buildAndExpand(Map.of("operation", operation))
			.toUriString();
	}

	private OperationDefinition buildOperationDefinition(String endpoint, PathItem pathItem) {
		final io.swagger.v3.oas.models.Operation getOperation = pathItem.getGet();
		final boolean isInstance = getOperation.getParameters().stream()
			.filter(p -> "path".equals(p.getIn()))
			.findFirst()
			.isPresent();

		String operationName = getOperationName(endpoint);
		final OperationDefinition operationDefinition = new OperationDefinition()
			.setName(operationName)
			.setCode(operationName)
			.setKind(OperationKind.OPERATION)
			.setAffectsState(false)
			.setStatus(PublicationStatus.ACTIVE)
			.setSystem(false)
			.setInstance(isInstance)
			.setType(true);
		
		getOperation.getTags().forEach(tag -> operationDefinition.addResource(VersionIndependentResourceTypesAll.fromCode(tag)));
		
		// add base definition reference
		String resourceType = Iterables.getFirst(operationDefinition.getResource(), null).getCode();
		operationDefinition.setBase(String.format("http://hl7.org/fhir/OperationDefinition/%s-%s", resourceType, operationName));
		
		// Only interested in 'query' type parameters
		getOperation.getParameters()
			.stream()
			.filter(p -> p.getIn().equals("query") 
				&& !p.getName().equals("_format")
				&& !p.getName().equals("_pretty"))
			.forEach(p -> {
				final io.swagger.v3.oas.models.media.Schema<?> schema = p.getSchema();
				final OperationDefinition.OperationDefinitionParameterComponent parameter = new OperationDefinition.OperationDefinitionParameterComponent()
					.setName(p.getName())
					.setUse(OperationParameterUse.IN)
					.setDocumentation(p.getDescription());
				
				if ("array".equals(schema.getType())) {
					parameter.setType(FHIRTypes.fromCode(((io.swagger.v3.oas.models.media.ArraySchema) schema).getItems().getType()));
					
					if (schema.getMinProperties() == null) {
						parameter.setMin(0);
					} else {
						parameter.setMin(schema.getMinProperties());
					}
					
					if (schema.getMaxProperties() == null) {
						parameter.setMax("*");
					} else {
						parameter.setMax(schema.getMaxProperties().toString());
					}
				} else {
					parameter.setType(FHIRTypes.fromCode(schema.getType()));
					
					if (p.getRequired() == true) {
						parameter.setMin(1).setMax("1");
					} else {
						parameter.setMin(0).setMax("1");
					}
				}
				
				operationDefinition.addParameter(parameter);
		});
		
		return operationDefinition;
	}

	private String getOperationName(String key) {
		final UriComponents uriComponents = UriComponentsBuilder.fromPath(key).build();
		return Iterables.getLast(uriComponents.getPathSegments()).replace("$", "");
	}

	private TerminologyCapabilities createTerminologyCapabilities(String softwareVersion, Date date, String description) {
		return new TerminologyCapabilities()
			// .url(...) is added later
			.setVersion(softwareVersion)
			.setName("snow-owl-terminology-capabilities")
			.setTitle("FHIR Terminology Capabilities of Snow Owl© Terminology Server")
			.setDescription("This statement describes additional details about supported FHIR terminology resource operations.")
			.setStatus(PublicationStatus.ACTIVE)
			.setDate(date)
			.setKind(CapabilityStatementKind.INSTANCE)
			.setExperimental(false)
			.setSoftware(new TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent()
				.setName("Snow Owl©")
				.setVersion(softwareVersion)
			) 
			.setImplementation(new TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent()
				.setDescription(description)
				.setUrl("https://b2ihealthcare.com")
			)
			// XXX: Not adding any information on code system operations here as it is tied to the resource
			// .codeSystem(...)
			.setExpansion(new TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent()
				.setHierarchical(false)
				.setPaging(true)
			)
			.setValidateCode(new TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent()
				.setTranslations(false)
			)
			.setTranslation(new TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent()
				.setNeedsMap(true)
			)
			// XXX: Not adding statement related to closure maintenance as it is unsupported entirely
			// .closure(...)
			;
	}
}
