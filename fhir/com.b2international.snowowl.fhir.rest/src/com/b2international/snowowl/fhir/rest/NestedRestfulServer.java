/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletConfigAware;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.rest.FhirApiConfig;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.*;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

/**
 * @since 9.0
 */
public class NestedRestfulServer extends RestfulServer implements HttpRequestHandler, ServletConfigAware, DisposableBean {

	private static final long serialVersionUID = 1L;

	@Autowired
	private List<IResourceProvider> resourceProviders = List.of();

	@Autowired
	private SearchAfterPagingProvider pagingProvider;
	
	public NestedRestfulServer() {
		super(FhirContext.forR5Cached());
	}

	@Override
	protected void initialize() throws ServletException {
		final IncomingRequestAddressStrategy strategy = new IncomingRequestAddressStrategy();
		strategy.setServletPath(FhirApiConfig.getApiBaseUrl());
		setServerAddressStrategy(strategy);

		setDefaultResponseEncoding(EncodingEnum.JSON);
		setDefaultPrettyPrint(true);
		setCopyright("Copyright 2023 B2i Healthcare Kft");
		setServerName("Snow Owl");
		setServerVersion("9.0");

		registerInterceptor(new ResponseHighlighterInterceptor());
		setResourceProviders(resourceProviders);
		setPagingProvider(pagingProvider);
	}

	/*
	 * Overridden to remove the context portion (eg. "http://localhost:8080/snowowl") 
	 * and the API base path (ie. "/fhir"), but not the rest of the request path. 
	 * The default implementation consumes it entirely for some reason.
	 */
	@Override
	protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {
		return requestFullPath.substring(escapedLength(servletContextPath) + escapedLength(FhirApiConfig.getApiBaseUrl()));
	}

	// Spring's DispatcherServlet calls this method on beans that implement HttpRequestHandler
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.service(request, response);
	}

	// Propagate servlet configuration, called when a bean implements ServletConfigAware
	@Override
	public void setServletConfig(final ServletConfig servletConfig) {
		try {
			super.init(servletConfig);
		} catch (final ServletException e) {
			throw new SnowowlRuntimeException("Caught exception while initializing FHIR servlet", e);
		}
	}

	/*
	 * Lifecycle binding: called on shutdown for beans that implement DisposableBean.
	 * Method is just added for clarity, as another one with the same signature
	 * already exists.
	 */
	@Override
	public void destroy() {
		super.destroy();
	}
}
