/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es.client.http;

import java.io.IOException;
import java.util.Arrays;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import com.b2international.index.IndexException;
import com.b2international.index.es.client.IndicesClient;

/**
 * @since 6.11
 */
public final class IndicesHttpClient implements IndicesClient {

	private final EsHttpClient client;
	private final RestHighLevelClient esClient;

	public IndicesHttpClient(EsHttpClient client) {
		this.client = client;
		this.esClient = client.client();
	}
	
	@Override
	public CreateIndexResponse create(CreateIndexRequest req) {
		client.checkAvailable();
		try {
			return esClient.indices().create(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to create index '%s' for type '%s'", req.index(), req.mappings().entrySet().iterator().next().getKey()), e);
		}
	}
	
	@Override
	public boolean exists(String... indices) {
		client.checkAvailable();
		try {
			return esClient.indices().exists(new GetIndexRequest().indices(indices), RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException("Couldn't check the existence of ES indices " + Arrays.toString(indices), e);
		}
	}

	@Override
	public AcknowledgedResponse delete(DeleteIndexRequest req) {
		client.checkAvailable();
		try {
			return esClient.indices().delete(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to delete all ES indices for '%s'.", Arrays.toString(req.indices())), e);
		}
	}

	@Override
	public RefreshResponse refresh(RefreshRequest req) {
		client.checkHealthy(req.indices());
		try {
			return esClient.indices().refresh(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to refresh ES indexes '%s'.", Arrays.toString(req.indices())), e);
		}
	}
	
	@Override
	public GetMappingsResponse getMapping(GetMappingsRequest req) {
		client.checkHealthy(req.indices());
		try {
			return esClient.indices().getMapping(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to get mapping '%s' of types %s.", Arrays.toString(req.indices()), Arrays.toString(req.types())), e);
		}
	}
	
	@Override
	public AcknowledgedResponse updateMapping(PutMappingRequest req) throws IOException {
		client.checkHealthy(req.indices());
		try {
			return esClient.indices().putMapping(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to put mapping '%s' of types %s.", Arrays.toString(req.indices()), req.type()), e);
		}
	}
	
}
