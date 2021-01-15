/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.BuilderConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataHolder;
import com.b2international.commons.options.MetadataHolderMixin;
import com.b2international.commons.options.MetadataMixin;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.branch.review.Review;
import com.b2international.snowowl.core.branch.review.ReviewMixin;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.rate.ApiConfiguration;
import com.b2international.snowowl.core.rate.HttpConfig;
import com.b2international.snowowl.core.rest.util.AntPathWildcardMatcher;
import com.b2international.snowowl.core.rest.util.CsvMessageConverter;
import com.b2international.snowowl.core.rest.util.ModelAttributeParameterExpanderExt;
import com.b2international.snowowl.core.rest.util.PromiseMethodReturnValueHandler;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

import io.micrometer.core.instrument.MeterRegistry;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Spring configuration class for Snow Owl's internal REST services module.
 *
 * @since 1.0
 */
@EnableSwagger2
@Configuration
@ComponentScan({"com.b2international.snowowl.core.rest"})
@Import({ SnowOwlSecurityConfig.class })
@PropertySource("classpath:com/b2international/snowowl/core/rest/service_configuration.properties")
public class SnowOwlApiConfig extends WebMvcConfigurationSupport {

	@Autowired
	private org.springframework.context.ApplicationContext ctx;
	
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		final StdDateFormat dateFormat = new StdDateFormat();
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		objectMapper.setDateFormat(dateFormat);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.addMixIn(Metadata.class, MetadataMixin.class);
		objectMapper.addMixIn(MetadataHolder.class, MetadataHolderMixin.class);
		objectMapper.addMixIn(Review.class, ReviewMixin.class);
		return objectMapper;
	}
	
	@Override
	protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		returnValueHandlers.add(new PromiseMethodReturnValueHandler());
	}
	
	@Override
	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEnumConverterFactory());
	}
	
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		final HttpConfig httpConfig = ApplicationContext.getInstance().getService(SnowOwlConfiguration.class).getModuleConfig(ApiConfiguration.class).getHttp();
	    final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSizePerFile(httpConfig.getMaxFileSizeBytes());
	    multipartResolver.setMaxUploadSize(httpConfig.getMaxRequestSizeBytes());
	    multipartResolver.setMaxInMemorySize(httpConfig.getMaxInMemorySizeBytes());
	    return multipartResolver;
	}
	
	@Bean
	public ModelAttributeParameterExpander modelAttributeParameterExpander(
			@Autowired FieldProvider fieldProvider, 
			@Autowired AccessorsProvider accessorsProvider,
			@Autowired EnumTypeDeterminer enumTypeDeterminer) {
		return new ModelAttributeParameterExpanderExt(fieldProvider, accessorsProvider, enumTypeDeterminer);
	}

	@Bean
	public IdentityProvider identityProvider() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IdentityProvider.class);
	}
	
	@Bean
	@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
	public Provider<IEventBus> eventBus(@Autowired HttpServletRequest request) {
		final String authorization = extractAuthorizationToken(request);
		return () -> new AuthorizedEventBus(ApplicationContext.getInstance().getServiceChecked(IEventBus.class), ImmutableMap.of(HttpHeaders.AUTHORIZATION, authorization));
	}
	
	/*
	 * Prefer Authorization header content, but allow token query parameter as well.
	 */
	private String extractAuthorizationToken(HttpServletRequest request) {
		String authorizationToken = request.getHeader("Authorization");
		if (Strings.isNullOrEmpty(authorizationToken)) {
			authorizationToken = request.getParameter("token");
		}
		return Strings.nullToEmpty(authorizationToken);
	}

	@Bean
	public AttachmentRegistry fileRegistry() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(AttachmentRegistry.class);
	}
	
	@Bean
	public MeterRegistry registry() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(MeterRegistry.class);
	}
	
	@Bean
	public JWTVerifier jwtVerifier() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(JWTVerifier.class);
	}
	
	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	protected void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		super.configureAsyncSupport(configurer);
		final HttpConfig httpConfig = ApplicationContext.getInstance().getService(SnowOwlConfiguration.class).getModuleConfig(ApiConfiguration.class).getHttp();
		configurer.setDefaultTimeout(httpConfig.getRequestTimeoutInMillis());
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
		ctx.getBeansWithAnnotation(Configuration.class)
			.values()
			.stream()
			.filter(BaseApiConfig.class::isInstance)
			.map(BaseApiConfig.class::cast)
			.forEach(conf -> {
				configurer.addPathPrefix(
					conf.getApiBaseUrl(), 
					HandlerTypePredicate.builder()
						.basePackage(conf.getApiBasePackages())
						.build()
				);
			});
	}
	
	@Override
	protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
		return new RequestMappingHandlerMapping() {
			
			private StringValueResolver embeddedValueResolver;
			
			@Override
			public void setEmbeddedValueResolver(StringValueResolver resolver) {
				super.setEmbeddedValueResolver(resolver);
				this.embeddedValueResolver = resolver;
			}
			
			@Override
			protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
				RequestMappingInfo info = createRequestMappingInfo(method);
				if (info != null) {
					RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
					if (typeInfo != null) {
						info = typeInfo.combine(info);
					}
					String prefix = getPrefix(handlerType);
					if (prefix != null) {
						BuilderConfiguration config = new BuilderConfiguration();
						config.setPathMatcher(getPathMatcher());
						config.setSuffixPatternMatch(false);
						info = RequestMappingInfo.paths(prefix).options(config).build().combine(info);
					}
				}
				return info;
			}

			@Nullable
			private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
				RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
				RequestCondition<?> condition = (element instanceof Class ?
						getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
				return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition) : null);
			}
			
			private String getPrefix(Class<?> handlerType) {
				for (Map.Entry<String, Predicate<Class<?>>> entry : getPathPrefixes().entrySet()) {
					if (entry.getValue().test(handlerType)) {
						String prefix = entry.getKey();
						if (this.embeddedValueResolver != null) {
							prefix = this.embeddedValueResolver.resolveStringValue(prefix);
						}
						return prefix;
					}
				}
				return null;
			}
			
		};
	}

}