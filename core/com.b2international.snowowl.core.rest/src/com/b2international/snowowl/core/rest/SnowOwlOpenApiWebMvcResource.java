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
package com.b2international.snowowl.core.rest;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.*;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.RouterFunctionProvider;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Exposes the {@link OpenApiWebMvcResource#getOpenApi getOpenApi()} method on the superclass for programmatic
 * access to generated OpenAPI metadata within Snow Owl.
 * 
 * @since 8.0
 */
public class SnowOwlOpenApiWebMvcResource extends OpenApiWebMvcResource {

	public SnowOwlOpenApiWebMvcResource(
			final ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
			final AbstractRequestService requestBuilder, 
			final GenericResponseService responseBuilder,
			final OperationService operationParser, 
			final RequestMappingInfoHandlerMapping requestMappingHandlerMapping,
			final Optional<ActuatorProvider> actuatorProvider, 
			final Optional<List<OperationCustomizer>> operationCustomizers,
			final Optional<List<OpenApiCustomiser>> openApiCustomisers,
			final SpringDocConfigProperties springDocConfigProperties,
			final Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
			final Optional<RouterFunctionProvider> routerFunctionProvider,
			final Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider) {

		super(openAPIBuilderObjectFactory, 
			requestBuilder, 
			responseBuilder, 
			operationParser, 
			requestMappingHandlerMapping,
			actuatorProvider, 
			operationCustomizers, 
			openApiCustomisers, 
			springDocConfigProperties,
			springSecurityOAuth2Provider, 
			routerFunctionProvider, 
			repositoryRestResourceProvider);
	}

	@Override
	public OpenAPI getOpenApi() {
		return super.getOpenApi();
	}
}
