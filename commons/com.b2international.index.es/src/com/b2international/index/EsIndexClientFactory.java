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
package com.b2international.index;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.http.HttpHost;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.es.EsClient;
import com.b2international.index.es.EsNode;
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
		final Object dataSetting = persistent ? settings.get(DATA_DIRECTORY) : DEFAULT_PATH;
		final Object configSetting = settings.containsKey(CONFIG_DIRECTORY) ? settings.get(CONFIG_DIRECTORY) : DEFAULT_PATH;
		final Path dataDirectory = dataSetting instanceof Path ? (Path) dataSetting : Paths.get((String) dataSetting);
		final Path configDirectory = configSetting instanceof Path ? (Path) configSetting : Paths.get((String) configSetting);
		
		final HttpHost host;
		if (settings.containsKey(CLUSTER_URL)) {
			host = HttpHost.create((String) settings.get(CLUSTER_URL));
		} else {
			// Start an embedded ES node only if a client URL is not set
			EsNode.getInstance(configDirectory, dataDirectory, persistent);
			host = HttpHost.create(DEFAULT_CLUSTER_URL);
		}
		
		final EsClient client = EsClient.create(host);
		return new EsIndexClient(new EsIndexAdmin(client, host.toURI(), name, mappings, settings, mapper), mapper);
	}
}
