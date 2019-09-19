/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.rest.config;

import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolder;
import com.b2international.commons.options.MetadataHolderMixin;
import com.b2international.commons.options.MetadataMixin;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchMixin;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewMixin;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.rest.AntPathWildcardMatcher;
import com.b2international.snowowl.rest.ModelAttributeParameterExpanderExt;
import com.b2international.snowowl.rest.auth.AuthorizationTokenInterceptor;
import com.b2international.snowowl.rest.auth.AuthorizationTokenThreadLocal;
import com.b2international.snowowl.rest.util.CsvMessageConverter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import io.micrometer.core.instrument.MeterRegistry;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Spring configuration class for Snow Owl's internal REST services module.
 *
 * @since 1.0
 */
@Order(1)
@EnableSwagger2
@EnableWebMvc
@Configuration
@ComponentScan({"com.b2international.snowowl.rest", "org.springdoc.core"})
@Import({ SnowOwlSecurityConfig.class })
@PropertySource("classpath:com/b2international/snowowl/rest/config/service_configuration.properties")
public class SnowOwlApiConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthorizationTokenInterceptor());
	}
	
//	@Bean
//	public OpenApiResource openApiResource(
//			@Autowired OpenAPIBuilder openApiBuilder,
//			@Autowired AbstractRequestBuilder requestBuilder,
//			@Autowired AbstractResponseBuilder responseBuilder,
//			@Autowired OperationBuilder operationParser,
//			@Autowired GeneralInfoBuilder infoBuilder,
//			@Autowired RequestBodyBuilder requestBodyBuilder,
//			@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping
//			) {
//		return new OpenApiResource(
//			openApiBuilder, 
//			requestBuilder, 
//			responseBuilder, 
//			operationParser, 
//			infoBuilder, 
//			requestBodyBuilder, 
//			requestMappingHandlerMapping
//		);
//	}
	
//	@Bean
//	public OpenAPI openAPI(@Autowired IdentityProvider identityProvider) {
//		OpenAPI api = new OpenAPI();
//		if (IdentityProvider.NOOP != identityProvider) {
//			api.components(
//				new Components()
//					.addSecuritySchemes("basic", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic").in(In.HEADER))
//					.addSecuritySchemes("bearer", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("jwt").in(In.HEADER))
//			)
//			.addSecurityItem(new SecurityRequirement().addList("basic").addList("bearer"));
//		}
//		return api.info(new Info()
//					.title(apiTitle)
//					.version(apiVersion)
//					.description(readApiDescription())
//					.termsOfService(apiTermsOfServiceUrl)
//					.contact(new Contact().email(apiContact))
//					.license(new License().name(apiLicense).url(apiLicenseUrl)));
//	}
	
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		final StdDateFormat dateFormat = new StdDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(dateFormat);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.addMixIn(Branch.class, BranchMixin.class);
		objectMapper.addMixIn(Metadata.class, MetadataMixin.class);
		objectMapper.addMixIn(MetadataHolder.class, MetadataHolderMixin.class);
		objectMapper.addMixIn(Review.class, ReviewMixin.class);
		return objectMapper;
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public ModelAttributeParameterExpander modelAttributeParameterExpander(
			@Autowired FieldProvider fieldProvider, 
			@Autowired EnumTypeDeterminer enumTypeDeterminer) {
		return new ModelAttributeParameterExpanderExt(fieldProvider, enumTypeDeterminer);
	}

	@Bean
	public IdentityProvider identityProvider() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IdentityProvider.class);
	}
	
	@Bean
	public IEventBus eventBus() {
		return new AuthorizedEventBus(ApplicationContext.getInstance().getServiceChecked(IEventBus.class), () -> ImmutableMap.of("Authorization", Strings.nullToEmpty(AuthorizationTokenThreadLocal.get())));
	}
	
	@Bean
	public AttachmentRegistry fileRegistry() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(AttachmentRegistry.class);
	}
	
	@Bean
	public MeterRegistry registry() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(MeterRegistry.class);
	}
	
	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
		final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charsets.UTF_8);
		stringConverter.setWriteAcceptCharset(false);
		converters.add(stringConverter);

		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new CsvMessageConverter());

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