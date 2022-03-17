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
 * 
 * Includes portions of Opensearch high-level REST client classes, 
 * also licensed under the Apache 2.0 license:
 * 
 * - org.opensearch.client.RestHighLevelClient
 * - org.opensearch.client.Request
 */
package com.b2international.index.os.client.http;

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.action.bulk.BulkProcessor;
import org.opensearch.action.bulk.BulkProcessor.Builder;
import org.opensearch.action.bulk.BulkProcessor.Listener;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.search.*;
import org.opensearch.client.*;
import org.opensearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.opensearch.client.RestClientBuilder.RequestConfigCallback;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.index.reindex.DeleteByQueryRequest;
import org.opensearch.index.reindex.UpdateByQueryRequest;
import org.opensearch.script.Script;

import com.b2international.index.os.OsClientConfiguration;
import com.b2international.index.os.OsIndexClientFactory;
import com.b2international.index.os.client.ClusterClient;
import com.b2international.index.os.client.IndicesClient;
import com.b2international.index.os.client.OsClientBase;

/**
 * @since 6.11
 */
public final class OsHttpClient extends OsClientBase {

	private final RestHighLevelClient client;
	private final RestHighLevelClientExt clientExt;
	private final IndicesClient indicesClient;
	private final ClusterClient clusterClient;
	
	public OsHttpClient(final OsClientConfiguration configuration) {
		super(configuration.getClusterUrl());
		// XXX: Adjust the thread context classloader while OS client is initializing
		this.client = OsIndexClientFactory.withTccl(() -> {
			
			final RequestConfigCallback requestConfigCallback = requestConfigBuilder -> requestConfigBuilder
					.setConnectTimeout(configuration.getConnectTimeout())
					.setSocketTimeout(configuration.getSocketTimeout());
			
			final RestClientBuilder restClientBuilder = RestClient.builder(host())
				.setRequestConfigCallback(requestConfigCallback);
			
			if (configuration.isProtected()) {
				
				final HttpClientConfigCallback httpClientConfigCallback = httpClientConfigBuilder -> {
					final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY, 
							new UsernamePasswordCredentials(configuration.getUserName(), configuration.getPassword()));
					return httpClientConfigBuilder.setDefaultCredentialsProvider(credentialsProvider);
				};
				
				restClientBuilder.setHttpClientConfigCallback(httpClientConfigCallback);
				
			}
			
			return new RestHighLevelClient(restClientBuilder);
		});
		
		this.clientExt = new RestHighLevelClientExt(client);
		this.indicesClient = new IndicesHttpClient(this);
		this.clusterClient = new ClusterHttpClient(this);
	}
	
	final RestHighLevelClient client() {
		return client;
	}
	
	@Override
	public final void close() throws IOException {
		client.close();
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
	protected boolean ping() throws IOException {
		return client.ping(RequestOptions.DEFAULT);
	}
	
	@Override
	public Builder bulk(Listener listener) {
		checkAvailable();
		return BulkProcessor.builder((req, actionListener) -> clientExt.bulkAsync(req, RequestOptions.DEFAULT, actionListener), listener);
	}

	@Override
	public GetResponse get(GetRequest req) throws IOException {
		checkAvailable();
		return client.get(req, RequestOptions.DEFAULT);
	}

	@Override
	public SearchResponse search(SearchRequest req) throws IOException {
		checkAvailable();
		return client.search(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public SearchResponse scroll(SearchScrollRequest req) throws IOException {
		checkAvailable();
		return client.scroll(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public final ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException {
		checkAvailable();
		// XXX use special client to handle 404 Bad Request on missing search context errors
		return clientExt.clearScroll(req, RequestOptions.DEFAULT);
	}
	
	@Override
	public BulkByScrollResponse updateByQuery(String index, int batchSize, Script script, int numberOfSlices, 
			QueryBuilder query) throws IOException {
		checkHealthy(index);
		UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index)
			.setBatchSize(batchSize)
			.setQuery(query)
			.setScript(script)
			.setSlices(numberOfSlices)
			.setAbortOnVersionConflict(false);
		
		return client.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
	}
	
	@Override
	public BulkByScrollResponse deleteByQuery(String index, int batchSize, int numberOfSlices,
			QueryBuilder query) throws IOException {
		checkHealthy(index);
		DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index)
				.setBatchSize(batchSize)
				.setQuery(query)
				.setSlices(numberOfSlices)
				.setAbortOnVersionConflict(false);
			
		return client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
	}

}
