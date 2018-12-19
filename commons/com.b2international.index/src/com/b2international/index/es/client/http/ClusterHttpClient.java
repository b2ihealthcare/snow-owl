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
package com.b2international.index.es.client.http;

import java.io.IOException;
import java.util.Arrays;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import com.b2international.index.IndexException;
import com.b2international.index.es.client.ClusterClient;

/**
 * @since 6.11
 */
public class ClusterHttpClient implements ClusterClient {

	private final RestHighLevelClient client;

	public ClusterHttpClient(RestHighLevelClient client) {
		this.client = client;
	}
	
	@Override
	public ClusterHealthResponse health(ClusterHealthRequest req) {
		try {
			return client.cluster().health(req, RequestOptions.DEFAULT);
		} catch (IOException e) {
			throw new IndexException("Couldn't retrieve cluster health for index(es) " + Arrays.toString(req.indices()), e);
		}
	}

}
