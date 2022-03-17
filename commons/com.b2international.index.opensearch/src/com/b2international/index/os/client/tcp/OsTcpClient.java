/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.os.client.tcp;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.opensearch.action.ActionFuture;
import org.opensearch.action.bulk.BulkProcessor;
import org.opensearch.action.bulk.BulkProcessor.Builder;
import org.opensearch.action.bulk.BulkProcessor.Listener;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.search.ClearScrollRequest;
import org.opensearch.action.search.ClearScrollResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchScrollRequest;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.index.reindex.DeleteByQueryAction;
import org.opensearch.index.reindex.DeleteByQueryRequestBuilder;
import org.opensearch.index.reindex.UpdateByQueryAction;
import org.opensearch.index.reindex.UpdateByQueryRequestBuilder;
import org.opensearch.script.Script;
import org.opensearch.client.Client;
import org.opensearch.client.transport.TransportClient;

import com.b2international.index.IndexException;
import com.b2international.index.os.client.ClusterClient;
import com.b2international.index.os.client.OsClientBase;
import com.b2international.index.os.client.IndicesClient;

/**
 * @since 6.11
 * @deprecated
 */
public final class OsTcpClient extends OsClientBase {

	private Client client;
	private IndicesClient indicesClient;
	private ClusterClient clusterClient;

	public OsTcpClient(Client client) {
		super(client instanceof TransportClient ? ((TransportClient) client).transportAddresses().stream().findFirst().get().address().toString() : "localhost:9300");
		this.client = client;
		this.indicesClient = new IndicesTcpClient(client.admin().indices());
		this.clusterClient = new ClusterTcpClient(client.admin().cluster());
	}
	
	@Override
	protected boolean ping() throws IOException {
		return true; // always returns true
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
		return BulkProcessor.builder(client::bulk, listener);
	}

	@Override
	public BulkByScrollResponse updateByQuery(String index, int batchSize, Script script, int numberOfSlices, 
			QueryBuilder query) throws IOException {
		UpdateByQueryRequestBuilder ubqrb = new UpdateByQueryRequestBuilder(client, UpdateByQueryAction.INSTANCE);
		
		ubqrb.source()
			.setIndices(index)
			.setSize(batchSize)
			.setQuery(query);
		
		return ubqrb
			.script(script)
			.setSlices(numberOfSlices)
			.get();
	}
	
	@Override
	public BulkByScrollResponse deleteByQuery(String index, int batchSize, int numberOfSlices,
			QueryBuilder query) throws IOException {
		DeleteByQueryRequestBuilder dbqrb = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
		
		dbqrb.source()
			.setIndices(index)
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
			throw new IndexException("Interrupted execution of Opensearch request", e);
		}
	}

}
