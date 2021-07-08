/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.classmate.TypeResolver;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

/**
 * Abstract configuration superclass to aid in providing Swagger documentation
 * for a fragment, and extend Spring's context with additional beans (eg. REST
 * controllers).
 * <p>
 * When creating a new REST fragment, create a subclass in package
 * {@code com.b2international.snowowl.core.rest} and add a {@link Configuration}
 * annotation to it, so it will be picked up by Spring; further packages can be
 * registered by including a {@link ComponentScan} annotation on the subclass.
 * <p>
 * To provide automatically generated documentation, add a public method
 * annotated with {@link Bean} that returns an instance of {@link Docket}. The
 * {@link #docs(String, String, String, String, String, String, String, String, String)}
 * method can be used for instantiation.
 * 
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
	protected final GroupedOpenApi docs(
			final String apiBaseUrl,
			final String apiGroup,
			final String apiVersion,
			final String apiTitle,
			final String apiTermsOfServiceUrl,
			final String apiContact,
			final String apiLicense,
			final String apiLicenseUrl,
			final String apiDescription) {
		return GroupedOpenApi.builder()
				.group(apiGroup)
				.pathsToMatch(apiBaseUrl.endsWith("/") ? apiBaseUrl + "**" : apiBaseUrl + "/**")
				.addOpenApiCustomiser(api -> {
					Info apiInfo = api.getInfo();
					apiInfo.setTitle(apiTitle);
					apiInfo.setDescription(apiDescription);
					apiInfo.setVersion(apiVersion);
					apiInfo.setTermsOfService(apiTermsOfServiceUrl);
					Contact contact = new Contact();
					contact.setName("B2i Healthcare");
					contact.setEmail(apiContact);
					contact.setUrl(apiLicenseUrl);
					apiInfo.setContact(contact);
					License license = new License();
					license.setName(apiLicense);
					license.setUrl(apiLicenseUrl);
					apiInfo.setLicense(license);

					
					// configure global security
					api.getComponents()
						.addSecuritySchemes("basic", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))
						.addSecuritySchemes("bearer", new SecurityScheme().type(SecurityScheme.Type.APIKEY).scheme("bearer").in(In.HEADER).bearerFormat("JWT"));
					
					// disable servers prop
					api.setServers(List.of()); 
				})
				.build();
//				.useDefaultResponseMessages(false)
//				.alternateTypeRules(getAlternateTypeRules(resolver));
	}

//	protected AlternateTypeRule[] getAlternateTypeRules(TypeResolver resolver) {
//		return new AlternateTypeRule[] {
//			newRule(resolver.resolve(UUID.class), resolver.resolve(String.class)),
//			newRule(resolver.resolve(ResourceURI.class), resolver.resolve(String.class)),
//			newRule(resolver.resolve(ComponentIdentifier.class), resolver.resolve(String.class)),
//			newRule(resolver.resolve(ExtendedLocale.class), resolver.resolve(String.class)),
//			newRule(
//				resolver.resolve(List.class, resolver.resolve(ResourceURI.class)),
//				resolver.resolve(List.class, resolver.resolve(String.class))
//	        ),
//			newRule(
//				resolver.resolve(List.class, resolver.resolve(ExtendedLocale.class)),
//				resolver.resolve(List.class, resolver.resolve(String.class))
//	        ),
//			newRule(
//				resolver.resolve(Promise.class, WildcardType.class),
//	            resolver.resolve(WildcardType.class)
//	        ),
//			newRule(
//				resolver.resolve(Promise.class, resolver.resolve(ResponseEntity.class, WildcardType.class)),
//	            resolver.resolve(WildcardType.class)
//	        )
//		};
//	}
	
}
