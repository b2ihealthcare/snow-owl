/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.b2international.index.IndexClient;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.es.admin.EsIndexAdmin;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.client.tcp.EsTcpClient;
import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 5.10
 */
public final class EsIndexClientFactory implements IndexClientFactory {

	private static final Path DEFAULT_PATH = Paths.get("target", "resources", "indexes");

	@Override
	public IndexClient createClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings) {
		final boolean persistent = settings.containsKey(DATA_DIRECTORY);
		final Object dataSetting = settings.getOrDefault(DATA_DIRECTORY, DEFAULT_PATH);
		final Object configSetting = settings.getOrDefault(CONFIG_DIRECTORY, DEFAULT_PATH);
		final Path dataDirectory = dataSetting instanceof Path ? (Path) dataSetting : Paths.get((String) dataSetting);
		final Path configDirectory = configSetting instanceof Path ? (Path) configSetting : Paths.get((String) configSetting);
		final String clusterName = (String) settings.getOrDefault(CLUSTER_NAME, DEFAULT_CLUSTER_NAME);
		
		final EsClient client;
		if (settings.containsKey(CLUSTER_URL)) {
			final String clusterUrl = (String) settings.get(CLUSTER_URL);
			final Object connectTimeoutSetting = settings.getOrDefault(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
			final Object socketTimeoutSetting = settings.getOrDefault(SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
			final int connectTimeout = connectTimeoutSetting instanceof Integer ? (int) connectTimeoutSetting : Integer.parseInt((String) connectTimeoutSetting);
			final int socketTimeout = socketTimeoutSetting instanceof Integer ? (int) socketTimeoutSetting : Integer.parseInt((String) socketTimeoutSetting);
			final String username = (String) settings.getOrDefault(CLUSTER_USERNAME, "");
			final String password = (String) settings.getOrDefault(CLUSTER_PASSWORD, "");
			
			final EsClientConfiguration clientConfiguration = new EsClientConfiguration(clusterName, clusterUrl, username, password, connectTimeout, socketTimeout, mapper);
			
			client = EsClient.create(clientConfiguration);
		} else {
			// Start an embedded ES node only if a cluster URL is not set
			Client esClient = EsNode.getInstance(clusterName, configDirectory, dataDirectory, persistent).client();
			// and use the local NodeClient to communicate via the embedded node
			client = new EsTcpClient(esClient);
		}
		
		return new EsIndexClient(new EsIndexAdmin(client, name, mappings, settings), mapper);
	}
}
