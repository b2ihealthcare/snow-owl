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
package com.b2international.index.es.client.tcp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
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
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;

import com.b2international.index.IndexException;
import com.b2international.index.es.client.ClusterClient;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.client.IndicesClient;

/**
 * @since 6.11
 */
public final class EsTcpClient implements EsClient {

	private Client client;
	private IndicesClient indicesClient;
	private ClusterClient clusterClient;

	public EsTcpClient(Client client) {
		this.client = client;
		this.indicesClient = new IndicesTcpClient(client.admin().indices());
		this.clusterClient = new ClusterTcpClient(client.admin().cluster());
	}
	
	@Override
	public void close() throws Exception {
		client.close();
	}

	@Override
	public IndicesClient indices() {
		return indicesClient;
	}

	@Override
	public ClusterClient cluster() {
		return clusterClient;
	}

	@Override
	public GetResponse get(GetRequest req) throws IOException {
		return execute(client.get(req));
	}

	@Override
	public SearchResponse search(SearchRequest req) throws IOException {
		return execute(client.search(req));
	}

	@Override
	public SearchResponse scroll(SearchScrollRequest req) throws IOException {
		return execute(client.searchScroll(req));
	}

	@Override
	public ClearScrollResponse clearScroll(ClearScrollRequest req) throws IOException {
		return execute(client.clearScroll(req));
	}

	@Override
	public Builder bulk(Listener listener) {
		return BulkProcessor.builder(client, listener);
	}

	@Override
	public BulkByScrollResponse updateByQuery(String index, String type, int batchSize, Script script, int numberOfSlices, 
			QueryBuilder query) throws IOException {
		UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
		
		ubqrb.source()
			.setIndices(index)
			.setTypes(type)
			.setSize(batchSize)
			.setQuery(query);
		
		return ubqrb
			.script(script)
			.setSlices(numberOfSlices)
			.get();
	}
	
	@Override
	public BulkByScrollResponse deleteByQuery(String index, String type, int batchSize, int numberOfSlices,
			QueryBuilder query) throws IOException {
		DeleteByQueryRequestBuilder dbqrb = DeleteByQueryAction.INSTANCE.newRequestBuilder(client);
		
		dbqrb.source()
			.setIndices(index)
			.setTypes(type)
			.setSize(batchSize)
			.setQuery(query);
	
		return dbqrb
			.setSlices(numberOfSlices)
			.get();
	}
	
	static final <T> T execute(ActionFuture<T> future) throws IOException {
		try {
			return future.get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw new IOException(e.getCause());
			}
		} catch (InterruptedException e) {
			throw new IndexException("Interrupted execution of Elasticsearch request", e);
		}
	}

}
