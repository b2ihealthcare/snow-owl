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
package com.b2international.snowowl.snomed.datastore.id.cis;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.datastore.config.SnomedIdentifierConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Client to communicate with the CIS.
 * 
 * @since 4.5
 */
class CisClient {

	private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
	private static final Logger LOGGER = LoggerFactory.getLogger(CisClient.class);

	private final String baseUrl;
	private final String contextRoot;
	private final String username;
	private final String password;
	private final ObjectMapper mapper;

	private final HttpClient client;

	public CisClient(final SnomedIdentifierConfiguration conf, final ObjectMapper mapper) {
		this.baseUrl = conf.getCisBaseUrl();
		this.contextRoot = conf.getCisContextRoot();
		this.username = conf.getCisUserName();
		this.password = conf.getCisPassword();
		this.mapper = mapper;
		final PoolingClientConnectionManager conman = new PoolingClientConnectionManager();
		conman.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
		conman.setMaxTotal(conf.getCisMaxConnections());
		this.client = new DefaultHttpClient(conman);
	}

	public HttpGet httpGet(final String suffix) {
		return new HttpGet(String.format("%s/%s", getServiceUrl(), suffix));
	}

	public HttpPost httpPost(final String suffix, final Object data) throws IOException {
		final HttpPost httpPost = new HttpPost(String.format("%s/%s", getServiceUrl(), suffix));
		httpPost.setEntity(new StringEntity(mapper.writeValueAsString(data), ContentType.create("application/json")));

		return httpPost;
	}

	public HttpPut httpPut(final String suffix, final Object data) throws IOException {
		final HttpPut httpPut = new HttpPut(String.format("%s/%s", getServiceUrl(), suffix));
		httpPut.setEntity(new StringEntity(mapper.writeValueAsString(data), ContentType.create("application/json")));

		return httpPut;
	}

	public String execute(final HttpRequestBase request) {
		try {
			final HttpResponse response = client.execute(request);
			checkResponseStatus(response);

			final HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (IOException e) {
			LOGGER.error("Exception while executing HTTP request.", e);
			throw new SnowowlRuntimeException("Exception while executing HTTP request.", e);
		}
	}

	public void release(final HttpRequestBase request) {
		request.releaseConnection();
	}

	private String getServiceUrl() {
		return String.format("%s/%s", baseUrl, contextRoot);
	}

	private void checkResponseStatus(final HttpResponse response) {
		final int statusCode = response.getStatusLine().getStatusCode();

		switch (statusCode) {
		case 200:
			break;
		default:
			LOGGER.error(response.getStatusLine().getReasonPhrase());
			// TODO check other status codes
			throw new BadRequestException(response.getStatusLine().getReasonPhrase());
		}
	}

	public void close() {
		if (null != client)
			client.getConnectionManager().shutdown();
	}

	public String login() {
		LogUtils.logUserAccess(LOGGER, username, "Logging in to Component Identifier service.");

		HttpPost request = null;

		try {
			final Credentials credentials = new Credentials(username, password);
			request = httpPost("login", credentials);

			final String response = execute(request);
			final JsonNode node = mapper.readValue(response, JsonNode.class);
			
			return node.get("token").asText();
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while logging in.", e);
		} finally {
			if (null != request)
				release(request);
		}
	}

	public void logout(final String token) {
		LogUtils.logUserAccess(LOGGER, username, "Logging out from Component Identifier service.");

		HttpPost request = null;

		try {
			final JsonNodeFactory factory = JsonNodeFactory.instance;
		    final JsonNode node = factory.objectNode().set("token", factory.textNode(token));
		    
			request = httpPost("logout", mapper.writeValueAsString(node));
			client.execute(request);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Exception while logging out.", e);
		} finally {
			if (null != request)
				release(request);
		}
	}
}
