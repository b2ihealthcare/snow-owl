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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.codesystems.RestfulCapabilityMode;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.CapabilityStatement;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Rest;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.Rest.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.operationdefinition.OperationDefinition;
import com.b2international.snowowl.fhir.core.model.operationdefinition.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;

/**
 * REST end-point for capabilities.
 * 
 * @since 8.0.0
 */
//@Api(value = "Bundle", description="Bundle Resource and batch operations", tags = { "Bundle" })
@Tag(description="Metadata Test", name = "Metadata test")
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
	
	@Autowired
	private ObjectMapper objectMapper;
	
	
	@Autowired
	private SnowOwlOpenApiWebMvcResource openApiResource;
	
	//RestController is singleton scoped, only a single instances of these objects are 'cached'
	private CapabilityStatement capabilityStatement;
	private Map<String, OperationDefinition> operationMap = Maps.newHashMap();

	private synchronized void initCache(final HttpServletRequest request) {
		
		OpenAPI openAPI = openApiResource.getOpenApi();
		
		Paths paths = openAPI.getPaths();
		
		Set<String> fhirOperationKeys = paths.keySet().stream()
				.filter(k -> k.startsWith("/fhir"))
				.filter(k -> k.contains("$"))
				.collect(Collectors.toSet());
		
		Builder restBuilder = Rest.builder()
				.mode(RestfulCapabilityMode.SERVER);
		
		for (String fhirKey : fhirOperationKeys) {
			
			OperationDefinition operationDefinition = buildOperationDefinition(fhirKey, paths.get(fhirKey));
			
//			try {
//				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(operationDefinition));
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
			
			operationDefinition.getResources().stream().forEach(c -> {
				operationMap.put(c.getCodeValue() + operationDefinition.getName(), operationDefinition);
				restBuilder.addOperation(com.b2international.snowowl.fhir.core.model.capabilitystatement.Operation.builder()
						.name(operationDefinition.getName())
						.definition(buildOperationUrl(request, c, operationDefinition))
						.build());
			});
		}
		
		capabilityStatement = CapabilityStatement.builder()
				.status(PublicationStatus.ACTIVE)
				.kind("instance")
				.addFormat(new Code(AbstractFhirResourceController.APPLICATION_FHIR_JSON))
				.addRest(restBuilder.build())
				.build();
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
		
		OperationDefinition.Builder operationDefinitionBuilder = OperationDefinition.builder()
				.name(operationName)
				.code(operationName)
				.kind("operation")
				.affectState(false)
				.status(PublicationStatus.ACTIVE)
				.system(false)
				.instance(false)
				.type(true);
		
		io.swagger.v3.oas.models.Operation operation = pathItem.getGet();
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
						parameterBuilder.max("*");
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

	@Operation(
			summary="metadata summary", 
			description="Metadata description."
	)
	@RequestMapping(value="/metadata", method=RequestMethod.GET)
	public Promise<CapabilityStatement> metadata(HttpServletRequest request) {
		
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
			summary="OPD  summary", 
			description="OPD description."
	)
	@GetMapping(value="/OperationDefinition/{operation}")
	public Promise<OperationDefinition> operationDefinition(
			@PathVariable(value = "operation") final String operation,
			HttpServletRequest request) {
		
		if (capabilityStatement == null) {
			initCache(request);
		}
		
		//get the definition from the cached map
		OperationDefinition operationDefinition = operationMap.get(operation);
		return Promise.immediate(operationDefinition);
	}

	@Override
	protected Class<CapabilityStatement> getModelClass() {
		return CapabilityStatement.class;
	}
	
}
