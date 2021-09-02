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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springdoc.core.*;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.RouterFunctionProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.CapabilityStatementKind;
import com.b2international.snowowl.fhir.core.codesystems.OperationKind;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.RestfulCapabilityMode;
import com.b2international.snowowl.fhir.core.model.StringExtension;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.*;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Rest.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

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
@Tag(description="CapabilityStatement", name = "CapabilityStatement", extensions = 
	@Extension(name = MetadataController.B2I_OPENAPI_X_NAME, properties = { 
		  @ExtensionProperty(name = MetadataController.B2I_OPENAPI_PROFILE, value = "http://hl7.org/fhir/StructureDefinition/CapabilityStatement")
	}))
@RestController
@RequestMapping(value="/", produces = { AbstractFhirResourceController.APPLICATION_FHIR_JSON })
public class MetadataController extends AbstractFhirResourceController<CapabilityStatement> {
	
	/*
	 * Class to expose the getOpenApi() method on the superclass.
	 */
	@Component
	public static class SnowOwlOpenApiWebMvcResource extends OpenApiWebMvcResource {
		
		@Autowired
		public SnowOwlOpenApiWebMvcResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
				AbstractRequestService requestBuilder, GenericResponseService responseBuilder,
				OperationService operationParser, RequestMappingInfoHandlerMapping requestMappingHandlerMapping,
				Optional<ActuatorProvider> actuatorProvider, Optional<List<OperationCustomizer>> operationCustomizers,
				Optional<List<OpenApiCustomiser>> openApiCustomisers,
				SpringDocConfigProperties springDocConfigProperties,
				Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
				Optional<RouterFunctionProvider> routerFunctionProvider,
				Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider) {
			super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, requestMappingHandlerMapping,
					actuatorProvider, operationCustomizers, openApiCustomisers, springDocConfigProperties,
					springSecurityOAuth2Provider, routerFunctionProvider, repositoryRestResourceProvider);
		}
		
		@Override
		public synchronized OpenAPI getOpenApi() {
			return super.getOpenApi();
		}
	}
	
	//TODO: Move these to some root-level class
	public static final String B2I_OPENAPI_X_NAME = "x-b2i-fhir";
	public static final String B2I_OPENAPI_PROFILE = "profile";
	
	public static final String B2I_OPENAPI_X_INTERACTION = "x-interaction";
	public static final String B2I_OPENAPI_INTERACTION_READ = "read";
		
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@Autowired
	private SnowOwlOpenApiWebMvcResource openApiResource;
	
	//RestController is singleton scoped, only a single instances of these objects are 'cached'
	private CapabilityStatement capabilityStatement;
	private Map<String, OperationDefinition> operationMap = Maps.newHashMap();

	/**
	 * Returns the server's capability statement
	 * @param request the injected http servlet request
	 * @return
	 */
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
	public Promise<CapabilityStatement> metadata(
			//@io.swagger.v3.oas.annotations.Parameter(extensions = @Extension(name = "dsd", 
			//properties = @ExtensionProperty(name = "type", value = "String")), 
			//description = "Canonical URL of the value set")
			
			HttpServletRequest request) {
		
		if (capabilityStatement == null) {
			initCache(request);
		}
		return Promise.immediate(capabilityStatement);
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
	public Promise<OperationDefinition> operationDefinition(
			@PathVariable(value = "operation") final String operation,
			HttpServletRequest request) {
		
		if (capabilityStatement == null) {
			initCache(request);
		}
		
		if (!operationMap.containsKey(operation)) {
			throw new NotFoundException("OperationDefinition", operation);
		}
		
		//get the definition from the cached map
		OperationDefinition operationDefinition = operationMap.get(operation);
		return Promise.immediate(operationDefinition);
	}
	
	
	private synchronized void initCache(final HttpServletRequest request) {
		
		OpenAPI openAPI = openApiResource.getOpenApi();
		
		Paths paths = openAPI.getPaths();
		
		Set<String> fhirOperationKeys = paths.keySet().stream()
				.filter(k -> k.startsWith("/fhir"))
				.filter(k -> k.contains("$"))
				.collect(Collectors.toSet());
		
		Collection<Resource> resources = collectResources(openAPI);
		
		Builder restBuilder = Rest.builder()
				.mode(RestfulCapabilityMode.SERVER)
				.resources(resources);
		
		for (String fhirKey : fhirOperationKeys) {
			
			PathItem pathItem = paths.get(fhirKey);
			
			//Skip non-GET type of operations
			if (pathItem.getGet() == null) continue;
			
			OperationDefinition operationDefinition = buildOperationDefinition(fhirKey, pathItem);
			
			operationDefinition.getResources().stream().forEach(c -> {
				operationMap.put(c.getCodeValue() + operationDefinition.getName(), operationDefinition);
				restBuilder.addOperation(com.b2international.snowowl.fhir.core.model.capabilitystatement.Operation.builder()
						.name(operationDefinition.getName())
						.definition(buildOperationUrl(request, c, operationDefinition))
						.build());
			});
		}
		
		capabilityStatement = CapabilityStatement.builder()
				.name("FHIR Capability statement for Snow Owl© Terminology Server")
				.status(PublicationStatus.ACTIVE)
				.experimental(false)
				.kind(CapabilityStatementKind.INSTANCE.getCode())
				.addInstantiate(new Uri("http://hl7.org/fhir/CapabilityStatement/terminology-server"))
				.software(Software.builder()
						.name("Snow Owl©")
						.version("8.0.0")
						.addExtension(StringExtension.builder()
								.url("http://b2i.sg/profiles/snowowl-server-API-extensions")
								.addExtension(StringExtension.builder()
									.url("apiVersion")
									.value("Snow Owl API Version comes here")
									.build())
								.build())
						.build())
				.implementation(Implementation.builder()
						.url("b2i.sg")
						.description("Demo server")
						.build())
				.addFormat(new Code(AbstractFhirResourceController.APPLICATION_FHIR_JSON))
				.addRest(restBuilder.build())
				.build();
		
	}
	
	private Collection<Resource> collectResources(OpenAPI openAPI) {
		
		Paths paths = openAPI.getPaths();
		
		List<io.swagger.v3.oas.models.tags.Tag> tags = openAPI.getTags();
		
		//Class-level tags with extensions indicate a resource
		Set<io.swagger.v3.oas.models.tags.Tag> fhirTags = tags.stream()
				.filter(t -> t.getExtensions() != null && t.getExtensions().containsKey(B2I_OPENAPI_X_NAME))
				.collect(Collectors.toSet());
		
		@SuppressWarnings("unchecked")
		List<Resource> resources = fhirTags.stream().map(t -> {
			Map<String, String> nameExtensionMap = (Map<String, String>) t.getExtensions().get(B2I_OPENAPI_X_NAME);
			
			Resource.Builder resourceBuilder = Resource.builder()
				.type(t.getName())
				.profile((String)nameExtensionMap.get(B2I_OPENAPI_PROFILE));
			
			//Collect the operations that belong to the same tagged class resource
			Set<io.swagger.v3.oas.models.Operation> commonOperations = paths.values().stream()
					.flatMap(pi -> pi.readOperations().stream())
					.filter(o -> o.getTags().contains(t.getName()))
					.collect(Collectors.toSet());
				
			for (io.swagger.v3.oas.models.Operation op : commonOperations) {
				Map<String, Object> operationExtensionMap = op.getExtensions();
				
				//skip the operations with no B2i extensions
				if (operationExtensionMap == null || !operationExtensionMap.containsKey(B2I_OPENAPI_X_INTERACTION)) continue;
				
				Map<String, String> interactionMap = (Map<String, String>) operationExtensionMap.get(B2I_OPENAPI_X_INTERACTION);
				
				List<Interaction> interactions = interactionMap.keySet().stream().map(k -> {
				
					Interaction.Builder interactionBuilder = Interaction.builder()
						.code((String) k);
					
					Object value = interactionMap.get(k);
						
					if (value != null && !((String) value).isEmpty()) {
						interactionBuilder.documentation((String) value);
					}
					return interactionBuilder.build();
				}).collect(Collectors.toList());
			
				resourceBuilder.interactions(interactions);
			}
				
			return resourceBuilder.build();
		}).collect(Collectors.toList());
		
		resources.sort((r1, r2) -> r1.getType().getCodeValue().compareTo(r2.getType().getCodeValue()));
		return resources;
	}

	private String buildOperationUrl(HttpServletRequest request, Code code, OperationDefinition operationDefinition) {
		
		try {
			URL url = new URL(request.getRequestURL().toString());
			StringBuilder sb = new StringBuilder(url.getProtocol())
					.append("://")
					.append(url.getHost())
					.append(":")
					.append(url.getPort())
					.append(java.nio.file.Paths.get(url.getPath()).getParent().toString())
					.append("/")
					.append(code.getCodeValue())
					.append(operationDefinition.getName());
			
			return sb.toString();
		} catch (MalformedURLException e1) {
			throw new RuntimeException();
		}
	}

	private OperationDefinition buildOperationDefinition(String fhirKey, PathItem pathItem) {
		
		String operationName = fhirKey.substring(fhirKey.lastIndexOf("/")+1);
		
		io.swagger.v3.oas.models.Operation operation = pathItem.getGet();
		
		boolean isInstance = operation.getParameters().stream()
				.filter(p -> "path".equals(p.getIn()))
				.findFirst()
				.isPresent();

		OperationDefinition.Builder operationDefinitionBuilder = OperationDefinition.builder()
				.name(operationName)
				.code(operationName)
				.kind(OperationKind.OPERATION.getCode())
				.affectState(false)
				.status(PublicationStatus.ACTIVE)
				.system(false)
				.instance(isInstance)
				.type(true);
		
		operation.getTags().forEach(t -> operationDefinitionBuilder.addResource(new Code(t)));
		
		//Only interested in 'query' type parameters
		operation.getParameters()
			.stream()
			.filter(p -> p.getIn().equals("query"))
			.forEach(p -> {
			
				@SuppressWarnings("rawtypes")
				Schema schema = p.getSchema();
				
				Parameter.Builder parameterBuilder = Parameter.builder()
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
