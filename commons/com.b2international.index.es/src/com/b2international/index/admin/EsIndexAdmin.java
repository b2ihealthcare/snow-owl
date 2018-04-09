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
package com.b2international.index.admin;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Analyzers;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.Keyword;
import com.b2international.index.Text;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.util.NumericClassUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public final class EsIndexAdmin implements IndexAdmin {

	private final String name;
	private final Mappings mappings;
	private final Map<String, Object> settings;
	private final Logger log;
	private final Client client;

	public EsIndexAdmin(Client client, String name, Mappings mappings, Map<String, Object> settings) {
		this.client = client;
		this.name = name.toLowerCase();
		this.mappings = mappings;
		this.settings = newHashMap(settings);
		this.log = LoggerFactory.getLogger(String.format("index.%s", this.name));
		this.settings.putIfAbsent(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL, IndexClientFactory.DEFAULT_COMMIT_CONCURRENCY_LEVEL);
		this.settings.putIfAbsent(IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.DEFAULT_RESULT_WINDOW);
		this.settings.putIfAbsent(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, IndexClientFactory.DEFAULT_TRANSLOG_SYNC_INTERVAL);
	}
	
	@Override
	public Logger log() {
		return log;
	}

	@Override
	public boolean exists() {
		return client().admin().indices().prepareExists(getAllIndexes().toArray(new String[]{})).get().isExists();
	}

	private boolean exists(DocumentMapping mapping) {
		return client().admin().indices().prepareExists(getTypeIndex(mapping)).get().isExists();
	}

	@Override
	public void create() {
		log.info("Preparing '{}' indexes...", name);
		if (!exists()) {
			// create number of indexes based on number of types
	 		for (DocumentMapping mapping : mappings.getMappings()) {
	 			if (exists(mapping)) {
	 				continue;
	 			}
	 			final String indexName = getTypeIndex(mapping);
				final CreateIndexRequestBuilder req = client().admin().indices().prepareCreate(indexName);
				final String type = mapping.typeAsString();
				Map<String, Object> typeMapping = ImmutableMap.of(type,
					ImmutableMap.builder()
						.put("date_detection", "false")
						.put("numeric_detection", "false")
						.putAll(toProperties(mapping))
						.build()
				);
				req.addMapping(type, typeMapping);
				try {
					final Map<String, Object> indexSettings = createIndexSettings();
					log.info("Configuring '{}' index with settings: {}", indexName, indexSettings);
					req.setSettings(indexSettings);
				} catch (IOException e) {
					throw new IndexException("Couldn't prepare settings for index " + indexName, e);
				}
				CreateIndexResponse response = req.get();
				checkState(response.isAcknowledged(), "Failed to create index '%s' for type '%s'", name, mapping.typeAsString());
	 		}
		}
		
 		// wait until the cluster processes each index create request
		waitForYellowHealth(getAllIndexes().toArray(new String[]{}));
		log.info("'{}' indexes are ready.", name);
	}

	private Set<String> getAllIndexes() {
		return mappings.getMappings().stream().map(this::getTypeIndex).collect(Collectors.toSet());
	}

	private Map<String, Object> createIndexSettings() throws IOException {
		return ImmutableMap.<String, Object>builder()
				.put("analysis", Settings.builder().loadFromStream("analysis.json", Resources.getResource(getClass(), "analysis.json").openStream()).build().getAsStructuredMap())
				.put("number_of_shards", String.valueOf(settings().getOrDefault(IndexClientFactory.NUMBER_OF_SHARDS, "1")))
				.put("number_of_replicas", "0")
				// disable es refresh, we will do it manually on each commit
				.put("refresh_interval", "-1")
				.put(IndexClientFactory.RESULT_WINDOW_KEY, settings().get(IndexClientFactory.RESULT_WINDOW_KEY))
				.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, settings().get(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY))
				.put("translog.durability", "async")
				.build();
	}
	
	private void waitForYellowHealth(String...indices) {
		if (!CompareUtils.isEmpty(indices)) {
			ClusterHealthResponse clusterHealthResponse = client().admin().cluster()
				.prepareHealth(indices)
				.setWaitForYellowStatus()
				.setTimeout("3m") // wait 3 minutes for yellow status
				.get();
			if (clusterHealthResponse.isTimedOut()) {
				throw new IndexException("Failed to wait for yellow health status of index " + name, null);
			}
		}
	}

	private Map<String, Object> toProperties(DocumentMapping mapping) {
		Map<String, Object> properties = newHashMap();
		for (Field field : mapping.getFields()) {
			final String property = field.getName();
			if (DocumentMapping._ID.equals(property)) continue;
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			checkState(fieldType != Object.class, "Dynamic mappings are not supported with Object type fields");
			if (Map.class.isAssignableFrom(fieldType)) {
				// allow dynamic mappings for dynamic objects like field using Map or Object
				final Map<String, Object> prop = newHashMap();
				prop.put("type", "object");
				prop.put("dynamic", "true");
				properties.put(property, prop);
				continue;
			} else if (mapping.isNestedMapping(fieldType)) {
				// this is a nested document type create a nested mapping
				final Map<String, Object> prop = newHashMap();
				prop.put("type", "nested");
				prop.putAll(toProperties(mapping.getNestedMapping(fieldType)));
				properties.put(property, prop);
			} else {
				final Map<String, Object> prop = newHashMap();
				
				final Map<String, Text> textFields = mapping.getTextFields(property);
				final Map<String, Keyword> keywordFields = mapping.getKeywordFields(property);
				
				if (textFields.isEmpty() && keywordFields.isEmpty()) {
					final String esType = toEsType(fieldType);
					if (!Strings.isNullOrEmpty(esType)) {
						prop.put("type", esType);
						properties.put(property, prop);
					}
				} else {
					checkState(String.class.isAssignableFrom(fieldType), "Only String fields can have Text and Keyword annotation. Found them on '%s'", property);
					
					final Text textMapping = textFields.get(property);
					final Keyword keywordMapping = keywordFields.get(property);
					checkState(textMapping == null || keywordMapping == null, "Cannot declare both Text and Keyword annotation on same field '%s'", property);
					
					if (textMapping != null) {
						prop.put("type", "text");
						prop.put("analyzer", EsTextAnalysis.getAnalyzer(textMapping.analyzer()));
						if (textMapping.searchAnalyzer() != Analyzers.INDEX) {
							prop.put("search_analyzer", EsTextAnalysis.getAnalyzer(textMapping.searchAnalyzer()));
						}
					}
					
					if (keywordMapping != null) {
						prop.put("type", "keyword");
						String normalizer = EsTextAnalysis.getNormalizer(keywordMapping.normalizer());
						if (!Strings.isNullOrEmpty(normalizer)) {
							prop.put("normalizer", normalizer);
						}
						prop.put("index", keywordMapping.index());
						prop.put("doc_values", keywordMapping.index());
					}
					
					// put extra text fields into fields object
					final Map<String, Object> fields = newHashMapWithExpectedSize(textFields.size() + keywordFields.size());
					for (Entry<String, Text> analyzer : textFields.entrySet()) {
						final String extraField = analyzer.getKey();
						final String[] extraFieldParts = extraField.split(Pattern.quote(DocumentMapping.DELIMITER));
						if (extraFieldParts.length > 1) {
							final Text analyzed = analyzer.getValue();
							final Map<String, Object> fieldProps = newHashMap();
							fieldProps.put("type", "text");
							fieldProps.put("analyzer", EsTextAnalysis.getAnalyzer(analyzed.analyzer()));
							if (analyzed.searchAnalyzer() != Analyzers.INDEX) {
								fieldProps.put("search_analyzer", EsTextAnalysis.getAnalyzer(analyzed.searchAnalyzer()));
							}
							fields.put(extraFieldParts[1], fieldProps);
						}
					}
					
					// put extra keyword fields into fields object
					for (Entry<String, Keyword> analyzer : keywordFields.entrySet()) {
						final String extraField = analyzer.getKey();
						final String[] extraFieldParts = extraField.split(Pattern.quote(DocumentMapping.DELIMITER));
						if (extraFieldParts.length > 1) {
							final Keyword analyzed = analyzer.getValue();
							final Map<String, Object> fieldProps = newHashMap();
							fieldProps.put("type", "keyword");
							String normalizer = EsTextAnalysis.getNormalizer(analyzed.normalizer());
							if (!Strings.isNullOrEmpty(normalizer)) {
								fieldProps.put("normalizer", normalizer);
							}
							fieldProps.put("index", analyzed.index());
							fields.put(extraFieldParts[1], fieldProps);
						}
					}
					
					if (!fields.isEmpty()) {
						prop.put("fields", fields);
					}
					properties.put(property, prop);
				}
				
			}
		}
		
		// Add system field "_hash", if there is at least a single field to hash
		if (!mapping.getHashedFields().isEmpty()) {
			final Map<String, Object> prop = newHashMap();
			prop.put("type", "keyword");
			prop.put("index", false);
			properties.put(DocumentMapping._HASH, prop);
		}
		
		return ImmutableMap.of("properties", properties);
	}

	private String toEsType(Class<?> fieldType) {
		if (Enum.class.isAssignableFrom(fieldType) || NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
			return "keyword";
		} else if (NumericClassUtils.isFloat(fieldType)) {
			return "float";
		} else if (NumericClassUtils.isInt(fieldType)) {
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
		if (exists()) {
			DeleteIndexResponse response = client().admin().indices().prepareDelete(name+"*").get();
			checkState(response.isAcknowledged(), "Failed to delete index %s", name);
		}
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
//		client().admin().indices().prepareForceMerge(name).setMaxNumSegments(maxSegments).get();
//		waitForYellowHealth();
	}
	
	public String getTypeIndex(DocumentMapping mapping) {
		if (mapping.getParent() != null) {
			return String.format("%s-%s", name, mapping.getParent().typeAsString());
		} else {
			return String.format("%s-%s", name, mapping.typeAsString());
		}
	}
	
	public Client client() {
		return client;
	}
	
	public void refresh(Set<DocumentMapping> typesToRefresh) {
		if (!CompareUtils.isEmpty(typesToRefresh)) {
			String[] indicesToRefresh = typesToRefresh.stream().map(this::getTypeIndex).collect(toSet()).toArray(new String[0]);
			log.trace("Refreshing indexes '{}'", Arrays.toString(indicesToRefresh));
			client().admin()
			        .indices()
			        .prepareRefresh(indicesToRefresh)
			        .get();
			waitForYellowHealth();
		}
	}
}
