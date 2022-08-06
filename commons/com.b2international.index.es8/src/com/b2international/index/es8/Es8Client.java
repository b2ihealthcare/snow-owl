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

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.es.EsClientConfiguration;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.UncheckedExecutionException;

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

	private static final Logger LOG = LoggerFactory.getLogger("elastic-snowowl");
	
	/*
	 * Customize the HTTP response consumer factory to allow processing greater than the default 100 MB of data (currently 1 GB) as the input.
	 */
	private static final int BUFFER_LIMIT = 1024 * 1024 * 1024;
	
	private final HttpHost host;
	
	private final ElasticsearchTransport transport;
	private final ElasticsearchClient client;

	private Es8Client(EsClientConfiguration config) {
		this.host = HttpHost.create(config.getClusterUrl());
		
		final RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
				.setConnectTimeout(config.getConnectTimeout())
				.setSocketTimeout(config.getSocketTimeout());
		
		final RestClientBuilder restClientBuilder = RestClient.builder(host)
			.setRequestConfigCallback(requestConfigCallback);
		
		final boolean isProtected = !Strings.isNullOrEmpty(config.getUserName()) && !Strings.isNullOrEmpty(config.getPassword());
		if (isProtected) {
			
			final HttpClientConfigCallback httpClientConfigCallback = httpClientConfigBuilder -> {
				final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY, 
						new UsernamePasswordCredentials(config.getUserName(), config.getPassword()));
				return httpClientConfigBuilder
						.setDefaultCredentialsProvider(credentialsProvider)
						.setSSLContext(config.getSslContext());
			};
			
			restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
			
		}

		// Create the transport with a Jackson mapper
		this.transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper(config.getMapper()));
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
	
	public static Es8Client create(EsClientConfiguration config) {
		return ClientPool.create(config);
	}
	
	final class ClientPool {
		
		private static final LoadingCache<EsClientConfiguration, Es8Client> CLIENTS_BY_HOST = CacheBuilder.newBuilder()
				.removalListener(ClientPool::onRemove)
				.build(CacheLoader.from(ClientPool::onAdd));
		
		private ClientPool() {}
		
		static Es8Client create(EsClientConfiguration configuration) {
			try {
				return CLIENTS_BY_HOST.getUnchecked(configuration);
			} catch (UncheckedExecutionException e) {
				if (e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				} else {
					throw new RuntimeException(e.getCause());
				}
			}
		}

		static void closeAll() {
			CLIENTS_BY_HOST.invalidateAll();
			CLIENTS_BY_HOST.cleanUp();
		}
		
		static Es8Client onAdd(final EsClientConfiguration configuration) {
			Preconditions.checkArgument(configuration.isHttp(), "Only HTTP connection is allowed for Elasticsearch 8 clusters");
			LOG.info("Connecting to Elasticsearch cluster with ES8 client at '{}'{}, connect timeout: {} ms, socket timeout: {} ms.", 
					configuration.getClusterUrl(),
					configuration.isProtected() ? " using basic authentication" : "",
					configuration.getConnectTimeout(),
					configuration.getSocketTimeout());
			return new Es8Client(configuration);
		}
		
		static void onRemove(final RemovalNotification<EsClientConfiguration, Es8Client> notification) {
			closeClient(notification.getKey(), notification.getValue());
		}
		
		static void closeClient(final EsClientConfiguration configuration, Es8Client client) {
			try {
				client.close();
				LOG.info("Closed ES client connected to '{}'", configuration.getClusterUrl());
			} catch (final Exception e) {
				LOG.error("Unable to close ES client connected to '{}'", configuration.getClusterUrl(), e);
			}
		}
		
	}
	
}
