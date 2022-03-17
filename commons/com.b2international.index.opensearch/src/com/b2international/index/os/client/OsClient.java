/*
 * Copyright 2018-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.os.client;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.opensearch.action.bulk.BulkProcessor;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.search.*;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.transport.TransportAddress;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.script.Script;
import org.opensearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.ClusterStatus;
import com.b2international.index.os.OsClientConfiguration;
import com.b2international.index.os.OsIndexClientFactory;
import com.b2international.index.os.client.http.OsHttpClient;
import com.b2international.index.os.client.tcp.OsTcpClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 6.11
 */
public interface OsClient extends AutoCloseable {

	Logger LOG = LoggerFactory.getLogger("opensearch-snowowl");
	
	ClusterStatus status(String...indices);
	
	IndicesClient indices();
	
	ClusterClient cluster();
	
	GetResponse get(GetRequest req) throws IOException;
	
	SearchResponse search(SearchRequest req) throws IOException;
	
	SearchResponse scroll(SearchScrollRequest req) throws IOException;
	
	ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException;
	
	BulkProcessor.Builder bulk(BulkProcessor.Listener listener);
	
	BulkByScrollResponse updateByQuery(String index, int batchSize, Script script, int numberOfSlices, QueryBuilder query) throws IOException;
	
	BulkByScrollResponse deleteByQuery(String index, int batchSize, int numberOfSlices, QueryBuilder query) throws IOException;
	
	static OsClient create(final OsClientConfiguration configuration) {
		return ClientPool.create(configuration);
	}
	
	static void closeAll() {
		ClientPool.closeAll();
	}
	
	/**
	 * @since 6.11
	 */
	final class ClientPool {
		
		private static final LoadingCache<OsClientConfiguration, OsClient> CLIENTS_BY_HOST = CacheBuilder.newBuilder()
				.removalListener(ClientPool::onRemove)
				.build(CacheLoader.from(ClientPool::onAdd));
		
		private ClientPool() {}
		
		static OsClient create(OsClientConfiguration configuration) {
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
		static OsClient onAdd(final OsClientConfiguration configuration) {
			LOG.info("Connecting to OpenSearch cluster at '{}'{}, connect timeout: {} ms, socket timeout: {} ms.", 
					configuration.getClusterUrl(),
					configuration.isProtected() ? " using basic authentication" : "",
					configuration.getConnectTimeout(),
					configuration.getSocketTimeout());
			
			if (configuration.isHttp()) {
				return new OsHttpClient(configuration);
			} else {
				checkState(configuration.isTcp(), "Only TCP and HTTP clients are supported");
				checkState(!configuration.isProtected(), "TCP connection scheme does not yet support security configuration. Consider switching to HTTP instead.");
				HostAndPort hostAndPort = HostAndPort.fromString(configuration.getClusterUrl().replaceAll(OsClientConfiguration.TCP_SCHEME, ""));
				Settings settings = Settings.builder()
				        .put("cluster.name", configuration.getClusterName())
				        .build();
				return new OsTcpClient(new PreBuiltTransportClient(settings)
						.addTransportAddress(new TransportAddress(new InetSocketAddress(hostAndPort.getHost(), hostAndPort.getPort()))));
			}
		}
		
		static void onRemove(final RemovalNotification<OsClientConfiguration, OsClient> notification) {
			OsIndexClientFactory.withTccl(() -> {
				closeClient(notification.getKey(), notification.getValue());
			});
		}
		
		static void closeClient(final OsClientConfiguration configuration, OsClient client) {
			try {
				client.close();
				LOG.info("Closed OS client connected to '{}'", configuration.getClusterUrl());
			} catch (final Exception e) {
				LOG.error("Unable to close OS client connected to '{}'", configuration.getClusterUrl(), e);
			}
		}
		
	}

}
