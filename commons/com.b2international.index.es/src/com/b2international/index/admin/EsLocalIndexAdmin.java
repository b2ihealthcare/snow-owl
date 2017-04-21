/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.admin;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.PendingClusterTask;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.script.groovy.GroovyPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.FileUtils;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.util.NumericClassUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public class EsLocalIndexAdmin implements EsIndexAdmin {

	private static final int PENDING_CLUSTER_TASKS_RETRY_INTERVAL = 100;
	
	private final File directory;
	private final String name;
	private final Mappings mappings;
	private final Map<String, Object> settings;
	private final Logger log;
	private final Node esNode;

	public EsLocalIndexAdmin(File directory, String name, Mappings mappings, Map<String, Object> settings) {
		this.directory = directory;
		this.name = name;
		this.mappings = mappings;
		this.settings = newHashMap(settings);
		this.log = LoggerFactory.getLogger(String.format("index.%s", name));
		
		if (!this.settings.containsKey(IndexClientFactory.SLOW_LOG_KEY)) {
			this.settings.put(IndexClientFactory.SLOW_LOG_KEY, new SlowLogConfig(this.settings));
		}
		
		final Builder esSettings = Settings.builder();
		// disable es refresh, we will do it manually on each commit
		esSettings.put("refresh_interval", "-1");
		// configure es home directory
		esSettings.put("path.home", directory.getAbsolutePath());
		esSettings.put("node.name", name);
		esSettings.put("index.translog.flush_threshold_period", "30m");
		esSettings.put("index.number_of_shards", 1);
		esSettings.put("index.number_of_replicas", 0);
		esSettings.put("cluster.name", name);
		esSettings.put("node.client", false);
		esSettings.put("node.local", true);
		esSettings.put("script.inline", true);
		esSettings.put("script.indexed", true);
		
		this.esNode = new EmbeddedNode(esSettings.build(), GroovyPlugin.class, ReindexPlugin.class, DeleteByQueryPlugin.class);
		this.esNode.start();
		awaitPendingTasks();
		log.info("Index is up and running.");
	}
	
	private void awaitPendingTasks() {
		int pendingTaskCount = 0;
		do {
			log.info("Waiting for pending cluster tasks to finish.");
			List<PendingClusterTask> pendingTasks = client().admin().cluster().preparePendingClusterTasks().get()
					.getPendingTasks();
			pendingTaskCount = pendingTasks.size();
			try {
				Thread.sleep(PENDING_CLUSTER_TASKS_RETRY_INTERVAL);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} while (pendingTaskCount > 0);
	}
	
	@Override
	public Logger log() {
		return log;
	}

	@Override
	public boolean exists() {
		return false;
//		return client().admin().indices().prepareGetIndex().setIndices(name);
	}

	@Override
	public void create() {
		final CreateIndexRequestBuilder req = client().admin().indices().prepareCreate(name);
		
		// add mappings
		for (DocumentMapping mapping : mappings.getMappings()) {
			final String type = mapping.typeAsString();
			Map<String, Object> typeMapping = ImmutableMap.of(type, toProperties(mapping));
			req.addMapping(type, typeMapping);
		}
		
		CreateIndexResponse response = req.get();
		checkState(response.isAcknowledged(), "Failed to create index %s", name);
		awaitPendingTasks();
	}

	private Map<String, Object> toProperties(DocumentMapping mapping) {
		Map<String, Object> properties = newHashMap();
		for (Field field : mapping.getFields()) {
			final String property = field.getName();
			if (DocumentMapping._ID.equals(property)) continue;
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			if (Map.class.isAssignableFrom(fieldType)) {
				// allow dynamic mappings for dynamic objects like field using Map
				continue;
			} else if (mapping.isNestedMapping(fieldType)) {
				// this is a nested document type create a nested mapping
				final Map<String, Object> prop = newHashMap();
				prop.put("type", "nested");
				prop.putAll(toProperties(mapping.getNestedMapping(fieldType)));
				properties.put(property, prop);
			} else {
				final Map<String, Object> prop = newHashMap();
				prop.put("type", toEsType(fieldType));
				if (!mapping.isAnalyzed(field.getName())) {
					prop.put("index", "not_analyzed");
				}
				properties.put(property, prop);
			}
		}
		return ImmutableMap.of("properties", properties);
	}

	private String toEsType(Class<?> fieldType) {
		if (NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
			return "string";
		} else if (NumericClassUtils.isFloat(fieldType)) {
			return "float";
		} else if (NumericClassUtils.isInt(fieldType)) {
			return "integer";
		} else if (NumericClassUtils.isShort(fieldType)) {
			return "short";
		} else if (NumericClassUtils.isLong(fieldType)) {
			return "long";
		} else if (Boolean.class.isAssignableFrom(Primitives.wrap(fieldType))) {
			return "boolean";
		}
		throw new UnsupportedOperationException("Unsupported mapping of primitive type: " + fieldType);
	}

	@Override
	public void delete() {
		DeleteIndexResponse response = client().admin().indices().prepareDelete(name).get();
		checkState(response.isAcknowledged(), "Failed to delete index %s", name);
		close();
		FileUtils.deleteDirectory(directory);
	}

	@Override
	public <T> void clear(Class<T> type) {
		// TODO remove all documents matching the given type, based on mappings
	}

	@Override
	public Map<String, Object> settings() {
		return settings;
	}

	@Override
	public Mappings mappings() {
		return mappings;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void close() {
		client().close();
		this.esNode.close();
	}

	@Override
	public void optimize(int maxSegments) {
		// TODO implement me
	}
	
	@Override
	public Client client() {
		return esNode.client();
	}

}
