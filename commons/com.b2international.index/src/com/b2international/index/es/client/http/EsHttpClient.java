/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Builder;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.*;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;

import com.b2international.index.Activator;
import com.b2international.index.es.EsClientConfiguration;
import com.b2international.index.es.client.ClusterClient;
import com.b2international.index.es.client.EsClientBase;
import com.b2international.index.es.client.IndicesClient;

/**
 * @since 6.11
 */
public final class EsHttpClient extends EsClientBase {

	/* 
	 * Customize the HTTP response consumer factory to allow processing greater than 
	 * the default 100 MB of data (currently 1 GB) as the input.
	 */
	private static final int BUFFER_LIMIT = 1024 * 1024 * 1024;

	private static final RequestOptions EXTENDED_DEFAULT;
	
	static {
		final RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		// XXX: this method call returns void, not an instance of Builder, so calls can not be chained (unlike all other setters on Builder)
		builder.setHttpAsyncResponseConsumerFactory(new HeapBufferedResponseConsumerFactory(BUFFER_LIMIT));
		EXTENDED_DEFAULT = builder.build();
	}

	private final RestHighLevelClient client;
	private final RestHighLevelClientExt clientExt;
	private final IndicesClient indicesClient;
	private final ClusterClient clusterClient;
	
	public EsHttpClient(final EsClientConfiguration configuration) {
		super(configuration.getClusterUrl());
		// XXX: Adjust the thread context classloader while ES client is initializing
		this.client = Activator.withTccl(() -> {
			
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
		return client.ping(EXTENDED_DEFAULT);
	}
	
	@Override
	public Builder bulk(Listener listener) {
		checkAvailable();
		return BulkProcessor.builder((req, actionListener) -> clientExt.bulkAsync(req, EXTENDED_DEFAULT, actionListener), listener);
	}

	@Override
	public GetResponse get(GetRequest req) throws IOException {
		checkAvailable();
		return client.get(req, EXTENDED_DEFAULT);
	}

	@Override
	public SearchResponse search(SearchRequest req) throws IOException {
		checkAvailable();
		return client.search(req, EXTENDED_DEFAULT);
	}
	
	@Override
	public SearchResponse scroll(SearchScrollRequest req) throws IOException {
		checkAvailable();
		return client.scroll(req, EXTENDED_DEFAULT);
	}
	
	@Override
	public final ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException {
		checkAvailable();
		// XXX use special client to handle 404 Bad Request on missing search context errors
		return clientExt.clearScroll(req, EXTENDED_DEFAULT);
	}
	
	@Override
	public BulkByScrollResponse updateByQuery(String index, int batchSize, Script script, QueryBuilder query) throws IOException {
		checkHealthy(index);
		UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index)
			.setBatchSize(batchSize)
			.setQuery(query)
			.setScript(script)
			.setSlices(UpdateByQueryRequest.AUTO_SLICES)
			.setAbortOnVersionConflict(false);
		
		return client.updateByQuery(updateByQueryRequest, EXTENDED_DEFAULT);
	}
	
	@Override
	public BulkByScrollResponse deleteByQuery(String index, int batchSize, QueryBuilder query) throws IOException {
		checkHealthy(index);
		DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index)
				.setBatchSize(batchSize)
				.setQuery(query)
				.setSlices(DeleteByQueryRequest.AUTO_SLICES)
				.setAbortOnVersionConflict(false);
			
		return client.deleteByQuery(deleteByQueryRequest, EXTENDED_DEFAULT);
	}
}
