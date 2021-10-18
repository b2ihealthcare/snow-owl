/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreActivator;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.rest.FhirApiConfig;
import com.b2international.snowowl.core.rest.SnowOwlOpenApiWebMvcResource;
import com.b2international.snowowl.fhir.core.codesystems.CapabilityStatementKind;
import com.b2international.snowowl.fhir.core.codesystems.OperationKind;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.RestfulCapabilityMode;
import com.b2international.snowowl.fhir.core.model.StringExtension;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.*;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Parameter;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;

/**
 * REST end-point for serving content describing the server's capabilities.
 * @since 8.0.0
 */
@Tag(description="CapabilityStatement", name = "CapabilityStatement", extensions = { 
	@Extension(name = B2I_OPENAPI_X_NAME, properties = { 
		@ExtensionProperty(name = B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/CapabilityStatement")
	})
})
@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class FhirMetadataController extends AbstractFhirResourceController<CapabilityStatement> {
	
	@Autowired
	private OpenApiWebMvcResource openApiWebMvcResource;
	
	@Autowired
	private FhirApiConfig config; 
	
	private static class Holder {
		CapabilityStatement capabilityStatement;
		Map<String, OperationDefinition> operationMap;
	}
	
	private final Supplier<Holder> capabilityStatementSupplier = Suppliers.memoize(this::initCache);

	@Operation(
		summary="Retrieve the capability statement", 
		description="Retrieves this server's capability statement.",
		extensions = {
			@Extension(name = B2I_OPENAPI_X_INTERACTION, properties = {
				@ExtensionProperty(name = B2I_OPENAPI_INTERACTION_READ, value = "Read the capability statement"),
			}),
		}
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping(value="/metadata")
	public CapabilityStatement metadata() {
		return capabilityStatementSupplier.get().capabilityStatement;
	}
	
	/**
	 * Returns the {@link OperationDefinition} for a given operation.
	 * @param operation
	 * @return
	 */
	@Operation(
		summary="Retrieve an operation definition by its name", 
		description="Retrieves an operation definition by its compound name (resource$operation)."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "Operation definition not found")
	})
	@GetMapping(value="/OperationDefinition/{operation}")
	public OperationDefinition operationDefinition(
		@PathVariable(value = "operation") final String operation) {
		
		final Map<String, OperationDefinition> operationMap = capabilityStatementSupplier.get().operationMap;
		final OperationDefinition operationDefinition = operationMap.get(operation);
		if (operationDefinition == null) {
			throw new NotFoundException("OperationDefinition", operation);
		}
		
		return operationDefinition;
	}
	
	private Holder initCache() {
		final Holder holder = new Holder();
		// XXX: we know that the subclass is instantiated (in SnowOwlApiConfig)
		final OpenAPI openAPI = ((SnowOwlOpenApiWebMvcResource) openApiWebMvcResource).getOpenApi();
		final Collection<Resource> resources = collectResources(openAPI);
		final Collection<OperationDefinition> operationDefinitions = collectOperationDefinitions(openAPI);
		
		final Rest.Builder restBuilder = Rest.builder()
			.mode(RestfulCapabilityMode.SERVER)
			.resources(resources);

		holder.operationMap = newHashMap();

		for (final OperationDefinition operationDefinition : operationDefinitions) {
			for (final Code code : operationDefinition.getResources()) {
				final String key = code.getCodeValue() + operationDefinition.getName();
				holder.operationMap.put(key, operationDefinition);
				
				restBuilder.addOperation(com.b2international.snowowl.fhir.core.model.capabilitystatement.Operation.builder()
					.name(operationDefinition.getName())
					.definition(buildOperationUrl(code, operationDefinition))
					.build());
			}
		}

		final String serverVersion = Platform.getBundle(CoreActivator.PLUGIN_ID)
			.getVersion()
			.toString();
		
		String description = ApplicationContext.getServiceForClass(SnowOwlConfiguration.class).getDescription();
		
		holder.capabilityStatement = CapabilityStatement.builder()
			.name("FHIR Capability statement for Snow Owl© Terminology Server")
			.status(PublicationStatus.ACTIVE)
			.experimental(false)
			.kind(CapabilityStatementKind.INSTANCE.getCode())
			.addInstantiate(new Uri("http://hl7.org/fhir/CapabilityStatement/terminology-server"))
			.software(Software.builder()
				.name("Snow Owl©")
				.version(serverVersion)
				.addExtension(StringExtension.builder()
					.url("http://b2i.sg/profiles/snowowl-server-API-extensions")
					.addExtension(StringExtension.builder()
						.url("apiVersion")
						.value("R4.0.1")
						.build()) // inner StringExtension
					.build()) // outer StringExtension
				.build()) // software
			.implementation(Implementation.builder()
				.url("https://b2i.sg")
				.description(description)
				.build()) // implementation
			.addFormat(new Code(AbstractFhirResourceController.APPLICATION_FHIR_JSON))
			.addRest(restBuilder.build())
			.build();
		
		return holder;
	}
	
	private Collection<Resource> collectResources(final OpenAPI openAPI) {
		final Paths paths = openAPI.getPaths();
		final List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
		
		return tags.stream()
			// Class-level tags with extensions indicate a resource
			.filter(t -> t.getExtensions() != null && t.getExtensions().containsKey(B2I_OPENAPI_X_NAME))
			.map(t -> {
				final Map<?, ?> nameExtensionMap = (Map<?, ?>) t.getExtensions().get(B2I_OPENAPI_X_NAME);
				final String profile = (String) nameExtensionMap.get(B2I_OPENAPI_PROFILE);
				
				final Resource.Builder resourceBuilder = Resource.builder()
					.type(t.getName())
					.profile(profile);
			
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
							final Interaction.Builder interactionBuilder = Interaction.builder()
								.code((String) e.getKey());
							
							final String value = (String) e.getValue();
							if (!StringUtils.isEmpty(value)) {
								interactionBuilder.documentation((String) value);
							}
							
							resourceBuilder.addInteraction(interactionBuilder.build());
						});
					});

				return resourceBuilder.build();
			})
			.sorted(Comparator.comparing(r -> r.getType().getCodeValue()))
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

	private String buildOperationUrl(final Code code, final OperationDefinition operationDefinition) {
		return MvcUriComponentsBuilder.fromController(FhirMetadataController.class)
			.pathSegment(code.getCodeValue(), operationDefinition.getName())
			.build()
			.toUriString();
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
			.code(operationName)
			.kind(OperationKind.OPERATION.getCode())
			.affectState(false)
			.status(PublicationStatus.ACTIVE)
			.system(false)
			.instance(isInstance)
			.type(true);
		
		getOperation.getTags().forEach(t -> operationDefinitionBuilder.addResource(new Code(t)));
		
		// Only interested in 'query' type parameters
		getOperation.getParameters()
			.stream()
			.filter(p -> p.getIn().equals("query"))
			.forEach(p -> {
				final Schema<?> schema = p.getSchema();
				final Parameter.Builder parameterBuilder = Parameter.builder()
					.name(p.getName())
					.use("in")
					.type(new Code(schema.getType()))
					.documentation(p.getDescription());
				
				if ("array".equals(schema.getType())) {
					if (schema.getMinProperties() == null) {
						parameterBuilder.min(0);
					} else {
						parameterBuilder.min(schema.getMinProperties());
					}
					
					if (schema.getMaxProperties() == null) {
						parameterBuilder.maxInfinite();
					} else {
						parameterBuilder.max(schema.getMaxProperties());
					}
				} else {
					if (p.getRequired() == true) {
						parameterBuilder.min(1).max(1);
					} else {
						parameterBuilder.min(0).max(1);
					}
				}
				
				operationDefinitionBuilder.addParameter(parameterBuilder.build());
		});
		
		return operationDefinitionBuilder.build();
	}

	@Override
	protected Class<CapabilityStatement> getModelClass() {
		return CapabilityStatement.class;
	}
}
