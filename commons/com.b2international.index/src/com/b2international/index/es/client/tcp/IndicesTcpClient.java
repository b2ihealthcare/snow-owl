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
 */
package com.b2international.index.es.client.tcp;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.cluster.metadata.MappingMetadata;

import com.b2international.index.es.client.IndicesClient;
import com.b2international.index.mapping.DocumentMapping;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

/**
 * @since 6.11
 */
public final class IndicesTcpClient implements IndicesClient {

	private final IndicesAdminClient client;

	public IndicesTcpClient(IndicesAdminClient client) {
		this.client = client;
	}

	@Override
	public boolean exists(String...indices) throws IOException {
		return EsTcpClient.execute(client.prepareExists(indices).execute()).isExists();
	}

	@Override
	public CreateIndexResponse create(CreateIndexRequest req) throws IOException {
		// Convert mappings using the generic "_doc" mapping type; include index settings
		var tcpReq = new org.elasticsearch.action.admin.indices.create.CreateIndexRequest(req.index())
			.mapping(DocumentMapping._DOC, req.mappings().utf8ToString(), req.mappingsXContentType())
			.settings(req.settings());
		var tcpResp = EsTcpClient.execute(client.create(tcpReq));
		
		// Convert acknowledgment flags and index name back to a client response
		return new CreateIndexResponse(tcpResp.isAcknowledged(), tcpResp.isShardsAcknowledged(), tcpResp.index());
	}

	@Override
	public AcknowledgedResponse delete(DeleteIndexRequest req) throws IOException {
		return EsTcpClient.execute(client.delete(req));
	}

	@Override
	public RefreshResponse refresh(RefreshRequest req) throws IOException {
		return EsTcpClient.execute(client.refresh(req));
	}
	
	@Override
	public GetMappingsResponse getMapping(GetMappingsRequest req) throws IOException {
		// Propagate index name(s) to the non-client get mapping request
		var tcpReq = new org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest()
			.indices(req.indices());
		var tcpResp = EsTcpClient.execute(client.getMappings(tcpReq));
		
		// Unpack inner Map containing the mapping for the single/only document type
		final ImmutableMap.Builder<String, MappingMetadata> mappings = ImmutableMap.builder();
		tcpResp.mappings().forEach(cursor -> {
			mappings.put(cursor.key, Iterators.getOnlyElement(cursor.value.valuesIt()));
		});
		
		return new GetMappingsResponse(mappings.build());
	}
	
	@Override
	public AcknowledgedResponse updateMapping(PutMappingRequest req) throws IOException {
		/*
		 * Propagate index name(s) and request source; add "_doc" as the mapping type name, which is not 
		 * contained in the original request, but is required here.
		 */
		var tcpReq = new org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest(req.indices())
			.source(req.source(), req.xContentType())
			.type(DocumentMapping._DOC);
		var tcpResp = EsTcpClient.execute(client.putMapping(tcpReq));
		
		// Response can be used directly
		return tcpResp;
	}
	
	@Override
	public GetSettingsResponse settings(GetSettingsRequest req) throws IOException {
		return EsTcpClient.execute(client.getSettings(req));
	}
	
	@Override
	public AcknowledgedResponse updateSettings(UpdateSettingsRequest req) throws IOException {
		return EsTcpClient.execute(client.updateSettings(req));
	}
}
