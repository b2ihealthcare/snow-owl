/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es8;

import java.io.Closeable;
import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;

/**
 * Special Elasticsearch 8 compatible Java Client that creates its own Java HTTP client and a {@link ElasticsearchClient} and makes it available to
 * the index services.
 * 
 * @since 8.5
 */
public class Es8Client implements Closeable {

	/*
	 * Customize the HTTP response consumer factory to allow processing greater than the default 100 MB of data (currently 1 GB) as the input.
	 */
	private static final int BUFFER_LIMIT = 1024 * 1024 * 1024;
	
	private final HttpHost host;
	
	private final ElasticsearchTransport transport;
	private final ElasticsearchClient client;

	public Es8Client(String clusterName, String clusterUrl, String username, String password, int connectTimeout, int socketTimeout, SSLContext sslContext, ObjectMapper mapper) {
		this.host = HttpHost.create(clusterUrl);
		
		final RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout);
		
		final RestClientBuilder restClientBuilder = RestClient.builder(host)
			.setRequestConfigCallback(requestConfigCallback);
		
		final boolean isProtected = !Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password);
		if (isProtected) {
			
			final HttpClientConfigCallback httpClientConfigCallback = httpClientConfigBuilder -> {
				final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY, 
						new UsernamePasswordCredentials(username, password));
				return httpClientConfigBuilder
						.setDefaultCredentialsProvider(credentialsProvider)
						.setSSLContext(sslContext);
			};
			
			restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
			
		}

		// Create the transport with a Jackson mapper
		this.transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper(mapper));
		// override DEFAULT transport options from transport with a client with increased HTTP response buffer limit
		TransportOptions transportOptions = new RestClientOptions.Builder(((RestClientOptions) this.transport.options()).restClientRequestOptions().toBuilder()
				.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(BUFFER_LIMIT)))
				.build();
		this.client = new ElasticsearchClient(transport, transportOptions);
	}
	
	public ElasticsearchClient client() {
		return client;
	}

	@Override
	public void close() throws IOException {
		if (this.transport != null) {
			this.transport.close();
		}
	}
	
}
