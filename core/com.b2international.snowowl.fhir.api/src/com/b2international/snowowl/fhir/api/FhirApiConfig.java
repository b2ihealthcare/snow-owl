/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api;

import static springfox.documentation.builders.PathSelectors.regex;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
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
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
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
 * The Spring configuration class for Snow Owl's FHIR REST API.
 * 
 * @since 6.4
 */
@EnableWebMvc
@EnableSwagger2
@Configuration
@ComponentScan
@Import({ FhirApiSecurityConfig.class })
@PropertySource(value="classpath:com/b2international/snowowl/fhir/api/service_configuration.properties")
public class FhirApiConfig extends WebMvcConfigurerAdapter {

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
	public AuthenticationProvider authenticationProvider() {
		return new SnowOwlAuthenticationProvider();
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

	@Bean
	public IEventBus eventBus() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
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
			final File apiDesc = new File(PlatformUtil.toAbsolutePath(FhirApiConfig.class, "api-description.md"));
			return Joiner.on("\n").join(Files.readLines(apiDesc, Charsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read api-description.md file", e);
		}
	}

	/*
	 * Add properties filter.
	 * TODO: https://github.com/krishna81m/jackson-nested-prop-filter
	 * @return
	 */
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new GuavaModule());
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		final SimpleDateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(df);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return objectMapper;
	}

	@Override
	public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
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
		configurer.setPathMatcher(new AntPathWildcardMatcher());
	}
}
