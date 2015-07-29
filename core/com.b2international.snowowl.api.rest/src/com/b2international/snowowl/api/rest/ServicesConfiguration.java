/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest;

import java.security.Principal;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersionProperties;
import com.b2international.snowowl.api.rest.domain.ICodeSystemVersionPropertiesMixin;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.alternates.AlternateTypeRule;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;

/**
 * The Spring configuration class for Snow Owl's internal REST services module.
 *
 * @since 1.0
 */
@Configuration
@EnableSwagger
@EnableWebMvc
public class ServicesConfiguration extends WebMvcConfigurerAdapter {

	private SpringSwaggerConfig springSwaggerConfig;
	private ServletContext servletContext;

	private String apiVersion;

	private String apiTitle;
	private String apiDescription;
	private String apiTermsOfServiceUrl;
	private String apiContact;
	private String apiLicense;
	private String apiLicenseUrl;
	
	@Autowired
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Autowired
	public void setSpringSwaggerConfig(final SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Autowired
	@Value("${api.version}")
	public void setApiVersion(final String apiVersion) {
		this.apiVersion = apiVersion;
	}

	@Autowired
	@Value("${api.title}")
	public void setApiTitle(final String apiTitle) {
		this.apiTitle = apiTitle;
	}

	@Autowired
	@Value("${api.description}")
	public void setApiDescription(final String apiDescription) {
		this.apiDescription = apiDescription;
	}

	@Autowired
	@Value("${api.termsOfServiceUrl}")
	public void setApiTermsOfServiceUrl(final String apiTermsOfServiceUrl) {
		this.apiTermsOfServiceUrl = apiTermsOfServiceUrl;
	}

	@Autowired
	@Value("${api.contact}")
	public void setApiContact(final String apiContact) {
		this.apiContact = apiContact;
	}

	@Autowired
	@Value("${api.license}")
	public void setApiLicense(final String apiLicense) {
		this.apiLicense = apiLicense;
	}

	@Autowired
	@Value("${api.licenseUrl}")
	public void setApiLicenseUrl(final String apiLicenseUrl) {
		this.apiLicenseUrl = apiLicenseUrl;
	}

	@Bean
	public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin() {
		final SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(springSwaggerConfig);
		swaggerSpringMvcPlugin.apiInfo(new ApiInfo(apiTitle, apiDescription, apiTermsOfServiceUrl, apiContact, apiLicense, apiLicenseUrl));
		swaggerSpringMvcPlugin.apiVersion(apiVersion);
		swaggerSpringMvcPlugin.pathProvider(new RelativeSwaggerPathProvider(servletContext));
		swaggerSpringMvcPlugin.useDefaultResponseMessages(false);
		swaggerSpringMvcPlugin.ignoredParameterTypes(Principal.class, Void.class);
		final TypeResolver resolver = new TypeResolver();
		swaggerSpringMvcPlugin.genericModelSubstitutes(ResponseEntity.class);
		swaggerSpringMvcPlugin.alternateTypeRules(new AlternateTypeRule(resolver.resolve(UUID.class), resolver.resolve(String.class)));

		return swaggerSpringMvcPlugin;
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new DefaultScalaModule());
		objectMapper.registerModule(new GuavaModule());
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		final ISO8601DateFormat df = new ISO8601DateFormat();
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(df);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.addMixInAnnotations(ICodeSystemVersionProperties.class, ICodeSystemVersionPropertiesMixin.class);
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