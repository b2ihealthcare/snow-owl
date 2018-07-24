/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.config;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.api.IAuthenticationService;
import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.ICodeSystemVersionService;
import com.b2international.snowowl.api.impl.AuthenticationServiceImpl;
import com.b2international.snowowl.api.impl.codesystem.CodeSystemServiceImpl;
import com.b2international.snowowl.api.impl.codesystem.CodeSystemVersionServiceImpl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.datastore.file.FileRegistry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.ISnomedConceptHistoryService;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.api.ISnomedReferenceSetHistoryService;
import com.b2international.snowowl.snomed.api.ISnomedRf2ImportService;
import com.b2international.snowowl.snomed.api.browser.ISnomedBrowserService;
import com.b2international.snowowl.snomed.api.impl.SnomedBrowserService;
import com.b2international.snowowl.snomed.api.impl.SnomedConceptHistoryServiceImpl;
import com.b2international.snowowl.snomed.api.impl.SnomedExportService;
import com.b2international.snowowl.snomed.api.impl.SnomedReferenceSetHistoryServiceImpl;
import com.b2international.snowowl.snomed.api.impl.SnomedRf2ImportService;
import com.b2international.snowowl.snomed.api.rest.domain.BranchMixin;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
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
@PropertySource("classpath:com/b2international/snowowl/snomed/api/rest/config/service_configuration.properties")
@ComponentScan("com.b2international.snowowl.snomed.api.rest")
public class ServicesConfiguration {

	private SpringSwaggerConfig springSwaggerConfig;
	private ServletContext servletContext;

	private String apiVersion;

	private String apiTitle;
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
		swaggerSpringMvcPlugin.apiInfo(new ApiInfo(apiTitle, readApiDescription(), apiTermsOfServiceUrl, apiContact, apiLicense, apiLicenseUrl));
		swaggerSpringMvcPlugin.apiVersion(apiVersion);
		swaggerSpringMvcPlugin.pathProvider(new RelativeSwaggerPathProvider(servletContext));
		swaggerSpringMvcPlugin.useDefaultResponseMessages(false);
		swaggerSpringMvcPlugin.ignoredParameterTypes(Principal.class, Void.class);
		final TypeResolver resolver = new TypeResolver();
		swaggerSpringMvcPlugin.genericModelSubstitutes(ResponseEntity.class);
		swaggerSpringMvcPlugin.genericModelSubstitutes(DeferredResult.class);
		swaggerSpringMvcPlugin.alternateTypeRules(new AlternateTypeRule(resolver.resolve(UUID.class), resolver.resolve(String.class)));
		swaggerSpringMvcPlugin.directModelSubstitute(Branch.class, BranchMixin.class);

		return swaggerSpringMvcPlugin;
	}

	private String readApiDescription() {
		try {
			final File apiDesc = new File(PlatformUtil.toAbsolutePath(ServicesConfiguration.class, "api-description.html"));
			return Joiner.on("\n").join(Files.readLines(apiDesc, Charsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read api-description.html file", e);
		}
	}
	
	@Bean
	public IEventBus eventBus() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
	}
	
	@Bean
	public FileRegistry fileRegistry() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance().getServiceChecked(FileRegistry.class);
	}
	
	@Bean
	public Integer maxReasonerRuns() {
		return com.b2international.snowowl.core.ApplicationContext.getInstance()
				.getServiceChecked(SnowOwlConfiguration.class)
				.getModuleConfig(SnomedCoreConfiguration.class)
				.getMaxReasonerRuns();
	}
	
	@Bean
	public IAuthenticationService authenticationService() {
		return new AuthenticationServiceImpl();
	}
	
	@Bean
	public ICodeSystemService codeSystemService() {
		return new CodeSystemServiceImpl();
	}
	
	@Bean
	public ICodeSystemVersionService codeSystemVersionService() {
		return new CodeSystemVersionServiceImpl();
	}
	
	@Bean
	public ISnomedConceptHistoryService conceptHistoryService() {
		return new SnomedConceptHistoryServiceImpl();
	}
	
	@Bean
	public ISnomedReferenceSetHistoryService referenceSetHistoryService() {
		return new SnomedReferenceSetHistoryServiceImpl();
	}
	
	@Bean
	public ISnomedExportService exportService() {
		return new SnomedExportService();
	}
	
	@Bean
	public ISnomedRf2ImportService importService() {
		return new SnomedRf2ImportService();
	}
	
	@Bean
	public ISnomedBrowserService browserService() {
		return new SnomedBrowserService();
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer ppc() throws IOException {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
