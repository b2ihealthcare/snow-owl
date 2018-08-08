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
package com.b2international.index.es;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.org.apache.lucene.Activator;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @since 6.6
 */
public final class EsClient {

	private static final Logger LOG = LoggerFactory.getLogger("elastic-snowowl");
	
	private static final LoadingCache<HttpHost, RestHighLevelClient> CLIENTS_BY_HOST = CacheBuilder.newBuilder()
			.removalListener(new RemovalListener<HttpHost, RestHighLevelClient>() {
				@Override
				public void onRemoval(RemovalNotification<HttpHost, RestHighLevelClient> notification) {
					Activator.withTccl(() -> {
						try {
							notification.getValue().close();
							LOG.info("Closed ES REST client for '{}'", notification.getKey().toURI());
						} catch (IOException e) {
							LOG.error("Unable to close ES REST client", e);
						}
					});
				}
			})
			.build(new CacheLoader<HttpHost, RestHighLevelClient>() {
				@Override
				public RestHighLevelClient load(HttpHost host) throws Exception {
					return Activator.withTccl(() -> {
						final RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder(host));
						checkState(esClient.ping(), "The cluster at '%s' is not available.", host.toURI());
						LOG.info("ES REST client is connecting to '{}'.", host.toURI());
						return esClient;
					});
				}
				
			});
	
	public static final RestHighLevelClient create(final HttpHost host) {
		return CLIENTS_BY_HOST.getUnchecked(host);
	}
	
	public static final void stop() {
		CLIENTS_BY_HOST.invalidateAll();
		CLIENTS_BY_HOST.cleanUp();
	}
	
}
