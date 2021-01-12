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
package com.b2international.index.es.client.tcp;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesAdminClient;

import com.b2international.index.es.client.IndicesClient;

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
		return EsTcpClient.execute(client.create(req));
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
		return EsTcpClient.execute(client.getMappings(req));
	}
	
	@Override
	public AcknowledgedResponse updateMapping(PutMappingRequest req) throws IOException {
		return EsTcpClient.execute(client.putMapping(req));
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
