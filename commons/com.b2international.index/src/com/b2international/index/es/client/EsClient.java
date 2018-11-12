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

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Activator;
import com.b2international.index.es.EsClientConfiguration;
import com.b2international.index.es.client.http.EsHttpClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @since 6.11
 */
public interface EsClient extends AutoCloseable {

	static final Logger LOG = LoggerFactory.getLogger("elastic-snowowl");
	
	IndicesClient indices();
	
	ClusterClient cluster();
	
	GetResponse get(GetRequest req);
	
	SearchResponse search(SearchRequest req);
	
	SearchResponse scroll(SearchScrollRequest req);
	
	ClearScrollResponse clearScroll(ClearScrollRequest req);
	
	BulkProcessor.Builder bulk(BulkProcessor.Listener listener);
	
	BulkByScrollResponse updateByQuery(String index, String type, int batchSize, Script script, int numberOfSlices, QueryBuilder query) throws IOException;
	
	static EsClient create(final EsClientConfiguration configuration) {
		return ClientPool.create(configuration);
	}
	
	static void closeAll() {
		ClientPool.closeAll();
	}
	
	/**
	 * @since 6.11
	 */
	static final class ClientPool {
		
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
		
		static EsClient onAdd(final EsClientConfiguration configuration) {
			return new EsHttpClient(configuration);
		}
		
		static void onRemove(final RemovalNotification<EsClientConfiguration, EsClient> notification) {
			Activator.withTccl(() -> {
				closeClient(notification.getKey(), notification.getValue());
			});
		}
		
		static void closeClient(final EsClientConfiguration configuration, EsClient client) {
			try {
				client.close();
				LOG.info("Closed ES client connected to '{}'", configuration.getHost().toURI());
			} catch (final Exception e) {
				LOG.error("Unable to close ES client connected to '{}'", configuration.getHost().toURI(), e);
			}
		}
		
	}

}
