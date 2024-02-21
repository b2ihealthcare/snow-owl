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
package com.b2international.snowowl.core.rest;

import java.util.Locale;

import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Exposes the {@link OpenApiWebMvcResource#getOpenApi(Locale) getOpenApi(Locale)} method on the superclass for programmatic access to generated
 * OpenAPI metadata within Snow Owl.
 * 
 * @since 8.0
 */
public class SnowOwlOpenApiWebMvcResource extends OpenApiWebMvcResource {

	public SnowOwlOpenApiWebMvcResource(
			ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, 
			AbstractRequestService requestBuilder,
			GenericResponseService responseBuilder, 
			OperationService operationParser, 
			SpringDocConfigProperties springDocConfigProperties,
			SpringDocProviders springDocProviders, 
			SpringDocCustomizers springDocCustomizers) {
		super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, springDocConfigProperties, springDocProviders,
				springDocCustomizers);
	}

	@Override
	public OpenAPI getOpenApi(Locale locale) {
		return super.getOpenApi(locale);
	}

}
