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
 * 
 * Includes portions of Elasticsearch high-level REST client classes, 
 * also licensed under the Apache 2.0 license:
 * 
 * - org.elasticsearch.client.RestHighLevelClient
 * - org.elasticsearch.client.Request
 */
package com.b2international.index.es.client.http;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Builder;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientExt;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;

import com.b2international.index.Activator;
import com.b2international.index.es.EsClientConfiguration;
import com.b2international.index.es.client.ClusterClient;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.client.IndicesClient;

/**
 * @since 6.11
 */
public final class EsHttpClient implements EsClient {

	private final RestHighLevelClient client;
	private final RestHighLevelClientExt clientExt;
	private final IndicesClient indicesClient;
	private final ClusterClient clusterClient;
	
	public EsHttpClient(final EsClientConfiguration configuration) {
		// XXX: Adjust the thread context classloader while ES client is initializing 
		this.client = Activator.withTccl(() -> {
			
			final HttpHost host = HttpHost.create(configuration.getClusterUrl());

			final RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
					.setConnectTimeout(configuration.getConnectTimeout())
					.setSocketTimeout(configuration.getSocketTimeout());
			
			final RestClientBuilder restClientBuilder = RestClient.builder(host)
				.setRequestConfigCallback(requestConfigCallback)
				.setMaxRetryTimeoutMillis(configuration.getSocketTimeout()); // retry timeout should match socket timeout
			
			if (configuration.isProtected()) {
				
				final HttpClientConfigCallback httpClientConfigCallback = httpClientConfigBuilder -> {
					final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY, 
							new UsernamePasswordCredentials(configuration.getUserName(), configuration.getPassword()));
					return httpClientConfigBuilder.setDefaultCredentialsProvider(credentialsProvider);
				};
				
				restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
				
			}
			
			final RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
			
			try {
				checkState(client.ping(RequestOptions.DEFAULT), "The cluster at '%s' is not available.", host.toURI());
			} catch (Exception e) {
				if (e instanceof ElasticsearchStatusException && ((ElasticsearchStatusException) e).status() == RestStatus.UNAUTHORIZED) {
					EsClient.LOG.error("Unable to authenticate with remote cluster '{}' using the given credentials", host.toURI());
				}
				close();
				throw e;
			}
			
			return client;
		});
		
		this.clientExt = new RestHighLevelClientExt(client);
		this.indicesClient = new IndicesHttpClient(client);
		this.clusterClient = new ClusterHttpClient(client);
	}

	@Override
	public final void close() throws IOException {
		client.close();
	}
	
	@Override
	public Builder bulk(Listener listener) {
		return BulkProcessor.builder((req, actionListener) -> clientExt.bulkAsync(req, RequestOptions.DEFAULT, actionListener), listener);
	}


	@Override
	public final IndicesClient indices() {
		return indicesClient;
	}

	@Override
	public final ClusterClient cluster() {
		return clusterClient;
	}

	@Override
	public GetResponse get(GetRequest req) throws IOException {
		return client.get(req, RequestOptions.DEFAULT);
	}

	@Override
	public SearchResponse search(SearchRequest req) throws IOException {
		return client.search(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public SearchResponse scroll(SearchScrollRequest req) throws IOException {
		return client.scroll(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public final ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException {
		return clientExt.clearScroll(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public BulkByScrollResponse updateByQuery(String index, String type, int batchSize, Script script, int numberOfSlices, QueryBuilder query)
			throws IOException {
		UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(new SearchRequest());
		
		updateByQueryRequest.getSearchRequest()
			.indices(index)
			.types(type)
			.source()
			.size(batchSize)
			.query(query);
		
		updateByQueryRequest
			.setScript(script)
			.setSlices(numberOfSlices);
		
		return clientExt.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
	}

}
