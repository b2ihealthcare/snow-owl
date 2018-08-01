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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.ReflectionUtils;
import com.b2international.index.Analyzers;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.Keyword;
import com.b2international.index.Text;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.util.NumericClassUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public final class EsIndexAdmin implements IndexAdmin {

	private final RestHighLevelClient client;
	private final String name;
	private final Mappings mappings;
	private final Map<String, Object> settings;
	private final ObjectMapper mapper;
	
	private final Logger log;

	public EsIndexAdmin(RestHighLevelClient client, String name, Mappings mappings, Map<String, Object> settings, ObjectMapper mapper) {
		this.client = client;
		this.name = name.toLowerCase();
		this.mappings = mappings;
		this.settings = newHashMap(settings);
		this.mapper = mapper;
		
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
		final String[] indices = getAllIndexes();
		final GetIndexRequest getIndexRequest = new GetIndexRequest().indices(indices);

		try {
			return client().indices().exists(getIndexRequest);
		} catch (IOException e) {
			throw new IndexException("Couldn't check the existence of all ES indices.", e);
		}
	}

	private boolean exists(DocumentMapping mapping) {
		final String index = getTypeIndex(mapping);
		final GetIndexRequest getIndexRequest = new GetIndexRequest().indices(index);

		try {
			return client().indices().exists(getIndexRequest);
		} catch (IOException e) {
			throw new IndexException("Couldn't check the existence of ES index '" + index + "'.", e);
		}
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
	 			
	 			final String index = getTypeIndex(mapping);
				final String type = mapping.typeAsString();
				final Map<String, Object> typeMapping = ImmutableMap.of(type,
					ImmutableMap.builder()
						.put("date_detection", "false")
						.put("numeric_detection", "false")
						.putAll(toProperties(mapping))
						.build());
				
				final Map<String, Object> indexSettings;
				try {
					indexSettings = createIndexSettings();
					log.info("Configuring '{}' index with settings: {}", index, indexSettings);
				} catch (IOException e) {
					throw new IndexException("Couldn't prepare settings for index " + index, e);
				}
				
				final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
				createIndexRequest.mapping(type, typeMapping);
				createIndexRequest.settings(indexSettings);
				
				try {
					final CreateIndexResponse response = client.indices()
							.create(createIndexRequest);
					checkState(response.isAcknowledged(), "Failed to create index '%s' for type '%s'", name, mapping.typeAsString());
				} catch (IOException e) {
					throw new IndexException(String.format("Failed to create index '%s' for type '%s'", name, mapping.typeAsString()), e);
				}
				
	 		}
		}
		
 		// wait until the cluster processes each index create request
		waitForYellowHealth(getAllIndexes());
		log.info("'{}' indexes are ready.", name);
	}

	private String[] getAllIndexes() {
		return mappings.getMappings()
				.stream()
				.map(this::getTypeIndex)
				.distinct()
				.toArray(String[]::new);
	}

	private Map<String, Object> createIndexSettings() throws IOException {
		InputStream analysisStream = getClass().getResourceAsStream("analysis.json");
		Settings analysisSettings = Settings.builder()
				.loadFromStream("analysis.json", analysisStream, true)
				.build();
		
		// FIXME: Is XContent a good alternative to a Map? getAsStructureMap is now private
		Map<String, Object> analysisMap = ReflectionUtils.callMethod(Settings.class, analysisSettings, "getAsStructuredMap");
		
		return ImmutableMap.<String, Object>builder()
				.put("analysis", analysisMap)
				.put("number_of_shards", String.valueOf(settings().getOrDefault(IndexClientFactory.NUMBER_OF_SHARDS, "1")))
				.put("number_of_replicas", "0")
				// disable es refresh, we will do it manually on each commit
				.put("refresh_interval", "-1")
				.put(IndexClientFactory.RESULT_WINDOW_KEY, settings().get(IndexClientFactory.RESULT_WINDOW_KEY))
				.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, settings().get(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY))
				.put("translog.durability", "async")
				.build();
	}
	
	private void waitForYellowHealth(String... indices) {
		if (!CompareUtils.isEmpty(indices)) {
			/*
			 * See https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html 
			 * for the low-level structure of the cluster health request.
			 */
			
			// GET /_cluster/health/test1,test2
			final StringBuilder endpoint = new StringBuilder("_cluster/health");
			for (int i = 0; i < indices.length; i++) {
				endpoint.append(i == 0 ? "/" : ",");
				endpoint.append(indices[i]);
			}
			
			// https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html#request-params
			final Map<String, String> parameters = ImmutableMap.<String, String>builder()
					.put("wait_for_status", "yellow")
					.put("timeout", "3m") // wait 3 minutes for yellow status
					.build(); 
			
			try {
				
				final Response clusterHealthResponse = client().getLowLevelClient()
						.performRequest(HttpGet.METHOD_NAME, endpoint.toString(), parameters);
				final InputStream responseStream = clusterHealthResponse.getEntity()
						.getContent();
				final JsonNode responseNode = mapper.readTree(responseStream);
				
				if (responseNode.get("timed_out").asBoolean()) {
					throw new IndexException("Request timed out waiting for yellow health status of index " + name, null);
				}
				
			} catch (IOException e) {
				throw new IndexException("Couldn't retrieve cluster health for index " + name, e);
			}
		}
	}

	private Map<String, Object> toProperties(DocumentMapping mapping) {
		Map<String, Object> properties = newHashMap();
		for (Field field : mapping.getFields()) {
			final String property = field.getName();
			if (DocumentMapping._ID.equals(property)) continue;
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			if (Map.class.isAssignableFrom(fieldType)) {
				// allow dynamic mappings for dynamic objects like field using Map
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
					addFieldProperties(prop, fieldType);
					properties.put(property, prop);
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

	private void addFieldProperties(Map<String, Object> fieldProperties, Class<?> fieldType) {
		if (Enum.class.isAssignableFrom(fieldType) || NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
			fieldProperties.put("type", "keyword");
		} else if (NumericClassUtils.isFloat(fieldType)) {
			fieldProperties.put("type", "float");
		} else if (NumericClassUtils.isInt(fieldType)) {
			fieldProperties.put("type", "integer");
		} else if (NumericClassUtils.isShort(fieldType)) {
			fieldProperties.put("type", "short");
		} else if (NumericClassUtils.isDate(fieldType) || NumericClassUtils.isLong(fieldType)) {
			fieldProperties.put("type", "long");
		} else if (Boolean.class.isAssignableFrom(Primitives.wrap(fieldType))) {
			fieldProperties.put("type", "boolean");
		} else {
			// Any other type will result in a sub-object that only appears in _source
			fieldProperties.put("type", "object");
			fieldProperties.put("enabled", false);
		}
	}

	@Override
	public void delete() {
		if (exists()) {
			final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(name + "*");
			try {
				final DeleteIndexResponse deleteIndexResponse = client()
						.indices()
						.delete(deleteIndexRequest);
				checkState(deleteIndexResponse.isAcknowledged(), "Failed to delete all ES indices for '%s'.", name);
			} catch (IOException e) {
				throw new IndexException(String.format("Failed to delete all ES indices for '%s'.", name), e);
			}
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
		try {
			client().close();
		} catch (IOException e) {
			log.error("Caught exception while closing high-level REST client.", e);
		}
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
	
	public RestHighLevelClient client() {
		return client;
	}
	
	public void refresh(Set<DocumentMapping> typesToRefresh) {
		if (!CompareUtils.isEmpty(typesToRefresh)) {
			final String[] indicesToRefresh;
			
			synchronized (typesToRefresh) {
				indicesToRefresh = typesToRefresh.stream()
						.map(this::getTypeIndex)
						.distinct()
						.toArray(String[]::new);
			}
			
			log.trace("Refreshing indexes '{}'", Arrays.toString(indicesToRefresh));
			
			try {
				
				final RefreshRequest refreshRequest = new RefreshRequest(indicesToRefresh);
				final RefreshResponse refreshResponse = client()
						.indices()
						.refresh(refreshRequest);
				if (RestStatus.OK != refreshResponse.getStatus()) {
					log.error("Index refresh request of '{}' returned with status {}", Joiner.on(", ").join(indicesToRefresh), refreshResponse.getStatus());
				}
				
			} catch (IOException e) {
				throw new IndexException(String.format("Failed to refresh ES indexes '%s'.", Arrays.toString(indicesToRefresh)), e);
			}
			
			waitForYellowHealth(indicesToRefresh);
		}
	}
}
