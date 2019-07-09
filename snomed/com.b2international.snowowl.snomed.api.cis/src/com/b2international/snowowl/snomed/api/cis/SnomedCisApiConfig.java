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
package com.b2international.snowowl.snomed.api.cis;

import static springfox.documentation.builders.PathSelectors.regex;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.IdentityProvider;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @since 6.18
 */
@EnableWebMvc
@EnableSwagger2
@Configuration
@ComponentScan("com.b2international.snowowl.snomed.api.cis")
@Import({ SnomedCisApiSecurityConfig.class })
@PropertySource("classpath:com/b2international/snowowl/snomed/api/cis/service_configuration.properties")
public class SnomedCisApiConfig extends WebMvcConfigurerAdapter {

	@Value("${api.version}")
	private String apiVersion;

	@Value("${api.title}")
	private String apiTitle;
	
	@Value("${api.termsOfServiceUrl}")
	private String apiTermsOfServiceUrl;
	
	@Value("${api.contact}")
	private String apiContact;
	
	@Value("${api.license}")
	private String apiLicense;
	
	@Value("${api.licenseUrl}")
	private String apiLicenseUrl;

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		final ISO8601DateFormat dateFormat = new ISO8601DateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(dateFormat);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public Docket customDocket() {
		final TypeResolver resolver = new TypeResolver();
		return new Docket(DocumentationType.SWAGGER_2)
            .select().paths(regex("/.*")).build()
            .useDefaultResponseMessages(false)
            .ignoredParameterTypes(Principal.class)
            .genericModelSubstitutes(ResponseEntity.class, DeferredResult.class)
            .alternateTypeRules(new AlternateTypeRule(resolver.resolve(UUID.class), resolver.resolve(String.class)))
            .apiInfo(new ApiInfo(apiTitle, readApiDescription(), apiVersion, apiTermsOfServiceUrl, new Contact("B2i Healthcare", apiLicenseUrl, apiContact), apiLicense, apiLicenseUrl, Collections.emptyList()));
	}
	
	private String readApiDescription() {
		try {
			final File apiDesc = PlatformUtil.toAbsolutePath(SnomedCisApiConfig.class, "api-description.mkd").toFile();
			return Joiner.on("\n").join(Files.readLines(apiDesc, Charsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read api-description.html file", e);
		}
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new CisAuthenticationProvider(identityProvider(), UUID.randomUUID().toString());
	}
	
	@Bean
	public IEventBus eventBus() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
	}
	
	@Bean
	public IdentityProvider identityProvider() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IdentityProvider.class); 
	}
	
	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public SnomedCisAuthenticationService cisService() {
		return new SnomedCisAuthenticationService();
	}
	
	@Override
	public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charsets.UTF_8);
		stringConverter.setWriteAcceptCharset(false);
		converters.add(stringConverter);

		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new ResourceHttpMessageConverter());

		final MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
		jacksonConverter.setObjectMapper(objectMapper());
		converters.add(jacksonConverter);
	}
	
	@Override
	public void configurePathMatch(final PathMatchConfigurer configurer) {
		configurer.setUseRegisteredSuffixPatternMatch(true);
	}
	
}
