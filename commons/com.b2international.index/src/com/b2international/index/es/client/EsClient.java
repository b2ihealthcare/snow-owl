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
package com.b2international.index.es.client;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Activator;
import com.b2international.index.es.EsClientConfiguration;
import com.b2international.index.es.client.http.EsHttpClient;
import com.b2international.index.es.client.tcp.EsTcpClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 6.11
 */
public interface EsClient extends AutoCloseable {

	Logger LOG = LoggerFactory.getLogger("elastic-snowowl");
	
	IndicesClient indices();
	
	ClusterClient cluster();
	
	GetResponse get(GetRequest req) throws IOException;
	
	SearchResponse search(SearchRequest req) throws IOException;
	
	SearchResponse scroll(SearchScrollRequest req) throws IOException;
	
	ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException;
	
	BulkProcessor.Builder bulk(BulkProcessor.Listener listener);
	
	BulkByScrollResponse updateByQuery(String index, String type, int batchSize, Script script, int numberOfSlices, QueryBuilder query) throws IOException;
	
	BulkByScrollResponse deleteByQuery(String index, String type, int batchSize, int numberOfSlices, QueryBuilder query) throws IOException;
	
	static EsClient create(final EsClientConfiguration configuration) {
		return ClientPool.create(configuration);
	}
	
	static void closeAll() {
		ClientPool.closeAll();
	}
	
	/**
	 * @since 6.11
	 */
	final class ClientPool {
		
		private static final LoadingCache<EsClientConfiguration, EsClient> CLIENTS_BY_HOST = CacheBuilder.newBuilder()
				.removalListener(ClientPool::onRemove)
				.build(CacheLoader.from(ClientPool::onAdd));
		
		private ClientPool() {}
		
		static EsClient create(EsClientConfiguration configuration) {
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
		
		@SuppressWarnings("resource")
		static EsClient onAdd(final EsClientConfiguration configuration) {
			LOG.info("Connecting to Elasticsearch cluster at '{}'{}, connect timeout: {} ms, socket timeout: {} ms.", 
					configuration.getClusterUrl(),
					configuration.isProtected() ? " using basic authentication" : "",
					configuration.getConnectTimeout(),
					configuration.getSocketTimeout());
			
			if (configuration.isHttp()) {
				return new EsHttpClient(configuration);
			} else {
				checkState(configuration.isTcp(), "Only TCP and HTTP clients are supported");
				checkState(!configuration.isProtected(), "TCP connection scheme does not yet support security configuration. Consider switching to HTTP instead.");
				HostAndPort hostAndPort = HostAndPort.fromString(configuration.getClusterUrl().replaceAll(EsClientConfiguration.TCP_SCHEME, ""));
				Settings settings = Settings.builder()
				        .put("cluster.name", configuration.getClusterName())
				        .build();
				return new EsTcpClient(new PreBuiltTransportClient(settings)
						.addTransportAddress(new TransportAddress(new InetSocketAddress(hostAndPort.getHostText(), hostAndPort.getPort()))));
			}
		}
		
		static void onRemove(final RemovalNotification<EsClientConfiguration, EsClient> notification) {
			Activator.withTccl(() -> {
				closeClient(notification.getKey(), notification.getValue());
			});
		}
		
		static void closeClient(final EsClientConfiguration configuration, EsClient client) {
			try {
				client.close();
				LOG.info("Closed ES client connected to '{}'", configuration.getClusterUrl());
			} catch (final Exception e) {
				LOG.error("Unable to close ES client connected to '{}'", configuration.getClusterUrl(), e);
			}
		}
		
	}

}
