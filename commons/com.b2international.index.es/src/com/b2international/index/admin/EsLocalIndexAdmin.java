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

import java.lang.reflect.Field;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.EmbeddedNode;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.index.util.NumericClassUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public class EsLocalIndexAdmin implements EsIndexAdmin {

	private final String name;
	private final Mappings mappings;
	private final Map<String, Object> settings;
	private final Logger log;
	private final EmbeddedNode node;

	public EsLocalIndexAdmin(EmbeddedNode node, String name, Mappings mappings, Map<String, Object> settings) {
		this.node = node;
		this.name = name.toLowerCase();
		this.mappings = mappings;
		this.settings = newHashMap(settings);
		this.log = LoggerFactory.getLogger(String.format("index.%s", this.name));

		// init slow log config
		new SlowLogConfig(this.settings);
		
		if (!this.settings.containsKey(IndexClientFactory.RESULT_WINDOW_KEY)) {
			this.settings.put(IndexClientFactory.RESULT_WINDOW_KEY, IndexClientFactory.DEFAULT_RESULT_WINDOW);
		}
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
		final Multimap<String, Map<String, Object>> esMappings = HashMultimap.create();
 		for (DocumentMapping mapping : mappings.getMappings()) {
			final String type = mapping.typeAsString();
			Map<String, Object> typeMapping = ImmutableMap.of(type, toProperties(mapping));
			esMappings.put(type, typeMapping);
 		}
 		
		for (String type : esMappings.keySet()) {
			final Map<String, Object> typeMapping = esMappings.get(type).stream().sorted((m1, m2) -> -1 * Ints.compare(m1.toString().length(), m2.toString().length())).findFirst().get();
			req.addMapping(type, typeMapping);
		}

		CreateIndexResponse response = req.get();
		checkState(response.isAcknowledged(), "Failed to create index %s", name);
		node.awaitPendingTasks();
	}

	private Map<String, Object> toProperties(DocumentMapping mapping) {
		Map<String, Object> properties = newHashMap();
		for (Field field : mapping.getFields()) {
			final String property = field.getName();
			if (DocumentMapping._ID.equals(property)) continue;
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			if (Map.class.isAssignableFrom(fieldType) || Object.class == fieldType) {
				// allow dynamic mappings for dynamic objects like field using Map or Object
				continue;
			} else if (mapping.isNestedMapping(fieldType)) {
				// this is a nested document type create a nested mapping
				final Map<String, Object> prop = newHashMap();
				prop.put("type", "nested");
				prop.putAll(toProperties(mapping.getNestedMapping(fieldType)));
				properties.put(property, prop);
			} else {
				final String esType = toEsType(fieldType);
				if (!Strings.isNullOrEmpty(esType)) {
					final Map<String, Object> prop = newHashMap();
					prop.put("type", esType);
					if (!mapping.isAnalyzed(field.getName())) {
						prop.put("index", "not_analyzed");
					}
					properties.put(property, prop);
				}
			}
		}
		return ImmutableMap.of("properties", properties);
	}

	private String toEsType(Class<?> fieldType) {
		if (NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
			return "string";
		} else if (NumericClassUtils.isFloat(fieldType)) {
			return "float";
		} else if (Enum.class.isAssignableFrom(fieldType) || NumericClassUtils.isInt(fieldType)) {
			return "integer";
		} else if (NumericClassUtils.isShort(fieldType)) {
			return "short";
		} else if (NumericClassUtils.isDate(fieldType) || NumericClassUtils.isLong(fieldType)) {
			return "long";
		} else if (Boolean.class.isAssignableFrom(Primitives.wrap(fieldType))) {
			return "boolean";
		}
		return null;
	}

	@Override
	public void delete() {
		DeleteIndexResponse response = client().admin().indices().prepareDelete(name).get();
		checkState(response.isAcknowledged(), "Failed to delete index %s", name);
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
		// nothing to do, ES will close itself on shutdown hook
	}

	@Override
	public void optimize(int maxSegments) {
		// TODO implement me
	}
	
	@Override
	public Client client() {
		return node.client();
	}

}
