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
package com.b2international.index.os.client;

import java.io.IOException;

import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.refresh.RefreshRequest;
import org.opensearch.action.admin.indices.refresh.RefreshResponse;
import org.opensearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.opensearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.indices.*;

/**
 * @since 6.11
 */
public interface IndicesClient {

	boolean exists(String...indices) throws IOException;

	CreateIndexResponse create(CreateIndexRequest req) throws IOException;

	AcknowledgedResponse delete(DeleteIndexRequest req) throws IOException;

	RefreshResponse refresh(RefreshRequest req) throws IOException;

	GetMappingsResponse getMapping(GetMappingsRequest req) throws IOException;
	
	AcknowledgedResponse updateMapping(PutMappingRequest req) throws IOException;
	
	GetSettingsResponse settings(GetSettingsRequest req) throws IOException;
	
	AcknowledgedResponse updateSettings(UpdateSettingsRequest req) throws IOException;
	
}
