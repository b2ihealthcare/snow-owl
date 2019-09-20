/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import io.swagger.v3.oas.models.security.SecurityScheme.In;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @since 7.2
 */
public abstract class BaseApiConfig {

	/**
	 * @return the api base url for all services grouped by this configuration class
	 */
	public abstract String getApiBaseUrl();
	
	/**
	 * @return the base packages where all controllers classes can be found for this API group
	 */
	public final String[] getApiBasePackages() {
		ComponentScan scan = AnnotationUtils.findAnnotation(getClass(), ComponentScan.class);
		if (scan != null) {
			return scan.value();
		} else {
			return new String[]{getClass().getPackageName()};
		}
	}
	
	/**
	 * Expose this as @Bean annotated component in the implementation configuration class.
	 * @return a configured docket for this API module
	 */
	protected final Docket docs(
			final String apiBaseUrl,
			final String apiGroup,
			final String apiVersion,
			final String apiTitle,
			final String apiTermsOfServiceUrl,
			final String apiContact,
			final String apiLicense,
			final String apiLicenseUrl,
			final String apiDescription) {
		final TypeResolver resolver = new TypeResolver();
		final Predicate<String> paths = PathSelectors.regex(apiBaseUrl + "/.*");
		return new Docket(DocumentationType.SWAGGER_2)
				.securitySchemes(ImmutableList.of(
					new BasicAuth("basic"),
					new ApiKey("bearer", HttpHeaders.AUTHORIZATION, In.HEADER.name())
				))
				.securityContexts(ImmutableList.of(
					SecurityContext.builder()
						.forPaths(paths)
						.securityReferences(ImmutableList.of(
							new SecurityReference("basic", new AuthorizationScope[0]),
							new SecurityReference("bearer", new AuthorizationScope[0])
						))
						.build()
				))
				.useDefaultResponseMessages(false)
				.ignoredParameterTypes(Principal.class)
				.genericModelSubstitutes(ResponseEntity.class, DeferredResult.class)
				.alternateTypeRules(new AlternateTypeRule(resolver.resolve(UUID.class), resolver.resolve(String.class)))
				.groupName(apiGroup)
	            .select().paths(paths).build()
	            .apiInfo(new ApiInfo(apiTitle, apiDescription, apiVersion, apiTermsOfServiceUrl, new Contact("B2i Healthcare", apiLicenseUrl, apiContact), apiLicense, apiLicenseUrl, Collections.emptyList()));
	}
	
}
