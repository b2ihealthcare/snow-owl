/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es.admin;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest.Level;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.RemoteInfo;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.ReflectionUtils;
import com.b2international.commons.json.Json;
import com.b2international.commons.time.TimeUtil;
import com.b2international.index.*;
import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.es.EsDocumentSearcher;
import com.b2international.index.es.EsDocumentWriter;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.query.EsQueryBuilder;
import com.b2international.index.es.reindex.ReindexResult;
import com.b2international.index.es8.Es8Client;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.migrate.DocumentMappingMigrator;
import com.b2international.index.migrate.SchemaRevision;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.util.JsonDiff;
import com.b2international.index.util.NumericClassUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;

/**
 * @since 5.10
 */
public final class EsIndexAdmin implements IndexAdmin {

	private static final Pattern FIELD_ALIAS_CHANGE_PROPERTY_PATTERN = Pattern.compile("properties(/[a-zA-Z0-9_]+)+/fields(/[a-zA-Z0-9_]+)?");
	
	/**
	 * List of Elasticsearch supported dynamic settings.
	 */
	private static final Set<String> DYNAMIC_SETTINGS = Set.of(
		IndexClientFactory.RESULT_WINDOW_KEY
	);
	/**
	 * Local Settings are Snow Owl index client only configuration, not actual Elasticsearch supported configuration, they are implicitly dynamic.
	 */
	private static final Set<String> LOCAL_SETTINGS = Set.of(
		IndexClientFactory.CLUSTER_URL,
		IndexClientFactory.CLUSTER_USERNAME,
		IndexClientFactory.CLUSTER_PASSWORD,
		IndexClientFactory.CLUSTER_SSL_CONTEXT,
		IndexClientFactory.COMMIT_WATERMARK_LOW_KEY,
		IndexClientFactory.COMMIT_WATERMARK_HIGH_KEY
	);
	
	private static final int DEFAULT_MAX_NUMBER_OF_VERSION_CONFLICT_RETRIES = 5;
	
	private final Random random = new Random();
	private final EsClient client;
	private final ObjectMapper mapper;
	private final String name;
	private final Map<String, Object> settings;
	
	
	private final Logger log;
	private final String prefix;

	// dynamically changeable index mappings
	private IndexMapping indexMapping;
	
	// optionally available Elasticsearch 8 client API
	private Es8Client es8Client;

	public EsIndexAdmin(EsClient client, ObjectMapper mapper, String name, Mappings mappings, Map<String, Object> settings) {
		this.client = client;
		this.mapper = mapper;
		this.name = name.toLowerCase();
		this.settings = newHashMap(settings);
		
		this.log = IndexAdmin.createIndexLogger(name);
		
		// configuration settings for ES index
		this.settings.putIfAbsent(IndexClientFactory.NUMBER_OF_SHARDS, IndexClientFactory.DEFAULT_NUMBER_OF_SHARDS);
		this.settings.putIfAbsent(IndexClientFactory.NUMBER_OF_REPLICAS, IndexClientFactory.DEFAULT_NUMBER_OF_REPLICAS);
		this.settings.putIfAbsent(IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.DEFAULT_RESULT_WINDOW);
		this.settings.putIfAbsent(IndexClientFactory.MAX_TERMS_COUNT_KEY, ""+IndexClientFactory.DEFAULT_MAX_TERMS_COUNT);
		this.settings.putIfAbsent(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, IndexClientFactory.DEFAULT_TRANSLOG_SYNC_INTERVAL);
		
		// local configuration settings for bulk writes, monitoring, etc.
		this.settings.putIfAbsent(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL, IndexClientFactory.DEFAULT_COMMIT_CONCURRENCY_LEVEL);
		this.settings.putIfAbsent(IndexClientFactory.INDEX_BY_QUERY_CONCURRENCY_LEVEL, IndexClientFactory.DEFAULT_INDEX_BY_QUERY_CONCURRENCY_LEVEL);
		this.settings.putIfAbsent(IndexClientFactory.BULK_ACTIONS_SIZE, IndexClientFactory.DEFAULT_BULK_ACTIONS_SIZE);
		this.settings.putIfAbsent(IndexClientFactory.BULK_ACTIONS_SIZE_IN_MB, IndexClientFactory.DEFAULT_BULK_ACTIONS_SIZE_IN_MB);
		this.settings.putIfAbsent(IndexClientFactory.COMMIT_WATERMARK_LOW_KEY, IndexClientFactory.DEFAULT_COMMIT_WATERMARK_LOW_VALUE);
		this.settings.putIfAbsent(IndexClientFactory.COMMIT_WATERMARK_HIGH_KEY, IndexClientFactory.DEFAULT_COMMIT_WATERMARK_HIGH_VALUE);
		
		final String prefix = (String) settings.getOrDefault(IndexClientFactory.INDEX_PREFIX, IndexClientFactory.DEFAULT_INDEX_PREFIX);
		this.prefix = prefix.isEmpty() ? "" : prefix + ".";
		
		this.indexMapping = new IndexMapping(mappings);
	}

	public EsIndexAdmin withEs8Client(Es8Client es8Client) {
		this.es8Client = es8Client;
		return this;
	}
	
	@Override
	public IndexMapping getIndexMapping() {
		return indexMapping;
	}
	
	@Override
	public Logger log() {
		return log;
	}

	@Override
	public boolean exists() {
		try {
			return client().indices().exists(indices());
		} catch (Exception e) {
			throw new IndexException("Couldn't check the existence of all ES indices.", e);
		}
	}

	private boolean exists(String indexName) {
		try {
			return client().indices().exists(indexName);
		} catch (Exception e) {
			throw new IndexException("Couldn't check the existence of ES index '" + indexName + "'.", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void create() {
		log.info("Preparing '{}' indexes...", name);
		// register any type that requires a refresh at the end of the index create/open
		Set<DocumentMapping> mappingsToRefresh = Sets.newHashSet();
		
		// create number of indexes based on number of types
		for (DocumentMapping mapping : this.indexMapping.getMappings().getMappings()) {
			// generate index name using configured names and prefixes
			final String index = generateTypeIndexName(mapping);
			
			// generate mapping based on Java type
			Map<String, Object> typeMapping = ImmutableMap.<String, Object>builder()
					.put(DocumentMapping._META, mapping.getMeta())
					.put("date_detection", false)
					.put("numeric_detection", false)
					.put("dynamic_templates", List.of(stringsAsKeywords()))
					.putAll(toProperties(mapping))
					.build();
			
			// allow override of mappings via index specific custom configuration
			Map<String, Object> additionalTypeIndexConfiguration = new HashMap<>((Map<String, Object>) settings.getOrDefault(mapping.typeAsString(), Map.of()));
			Map<String, Object> typeMappingOverrides = (Map<String, Object>) additionalTypeIndexConfiguration.getOrDefault(IndexClientFactory.MAPPINGS, Map.of());
			if (!typeMappingOverrides.isEmpty()) {
				typeMapping = Json.merge(typeMapping, typeMappingOverrides);
			}
			
			// make sure we remove mappings when using any additional type index config
			additionalTypeIndexConfiguration.remove(IndexClientFactory.MAPPINGS);
			
			// check if index is present
			if (exists(index)) {
				
				// update mapping if required
				final MappingMetadata currentIndexMapping;
				
				try {
					final GetMappingsRequest getMappingsRequest = new GetMappingsRequest().indices(index);
					currentIndexMapping = client.indices().getMapping(getMappingsRequest)
						.mappings()
						.get(index);
				} catch (Exception e) {
					throw new IndexException(String.format("Failed to get mapping of '%s' for type '%s'", name, mapping.typeAsString()), e);
				}
				
				final ObjectNode newTypeMapping = mapper.valueToTree(typeMapping);
				final ObjectNode currentTypeMapping = mapper.valueToTree(currentIndexMapping.getSourceAsMap());
				
				// first check the _meta.version field value
				final long oldVersion = currentTypeMapping.path(DocumentMapping._META).path(DocumentMapping.Meta.VERSION).longValue();
				final long newVersion = newTypeMapping.path(DocumentMapping._META).path(DocumentMapping.Meta.VERSION).longValue();
				checkState(oldVersion <= newVersion, "Current model version should never be greater than the new model version. In case of '%s' got old '%s' vs new '%s'.", mapping.type().getSimpleName(), oldVersion, newVersion);
				
				// always perform a schema diff just in case to detect compatible and incompatible changes and report them when needed
				SortedSet<String> compatibleChanges = Sets.newTreeSet();
				SortedSet<String> incompatibleChanges = Sets.newTreeSet();
				final JsonDiff schemaChanges = JsonDiff.diff(currentTypeMapping, newTypeMapping);
				schemaChanges.forEach(change -> {
					// ignore _meta changes
					if (change.getFieldPath().startsWith(DocumentMapping._META)) {
						return;
					}
					
					if (change.isAdd()) {
						
						// XXX object type is the default type, so if the current mapping does not contain this node, we shouldn't trigger an update
						if (change.getFieldPath().endsWith("/type") && "object".equals(change.serializeValue())) {
							return;
						}
						
						compatibleChanges.add(change.getFieldPath());
						
					} else if (change.isMove() || change.isReplace()) {
						compatibleChanges.add(change.getFieldPath());
					} else if (change.isRemove()) {
						
						// XXX while remove is bad it is hard to detect true incompatibility where we try to support dynamic fields (like Maps)
						// throw the incompatibility error only when a root field is being reported, not a nested property under the root property
						if (change.getFieldPath().contains("/properties")) {
							return;
						}
						
						compatibleChanges.add(change.getFieldPath());
					}
					
				});
				
				if (oldVersion < newVersion) {

					long currentVersion = oldVersion;
					
					// perform migration to the new schema using the migrators starting from oldVersion up until the current latest version
					// run one migrator at a time (TODO optimize later for schema versions that can be executed together)
					for (SchemaRevision schema : mapping.getSchemaRevisionsFrom(oldVersion)) {
						String migrationTaskDescription = String.format("Migrating index '%s' from schema version %s to %s. Changes: '%s'", index, currentVersion, schema.version(), schema.description());
						log.info(migrationTaskDescription);
						
						switch (schema.strategy()) {
						case NO_REINDEX:
							// just apply the schema changes by modifying the mapping and we are good to go
							putIndexMapping(index, typeMapping);
							break;
						case REINDEX_INPLACE:
							// apply the schema changes first, there should be only compatible changes here
							putIndexMapping(index, typeMapping);
							// then reindex all documents in place to pick up mapping changes, mostly for 
							if (bulkIndexByScroll(client, mapping, index, Expressions.matchAll(), "update", null /*no script, in place update of docs to pick up mapping changes*/, migrationTaskDescription)) {
								mappingsToRefresh.add(mapping);
							}
							break;
						case REINDEX_SCRIPT:
							// wait until yellow health is reached for this specific index
							waitForYellowHealth(index);

							// create an index mapping configuration for the previous index
							IndexMapping previousIndexMapping = new IndexMapping(getIndexMapping().getMappings());
							previousIndexMapping.register(mapping, index);
							
							// create a searcher on the old index mapping to access previous data
							EsDocumentSearcher previousIndexSearcher = new EsDocumentSearcher(this, previousIndexMapping, mapper);
							
							// create a new temporary index to transform documents into the new schema
							final String temporaryIndex = String.join("---", index, Long.toString(newVersion));
							doCreateIndex(temporaryIndex, mapping, typeMappingOverrides, additionalTypeIndexConfiguration);
							waitForYellowHealth(temporaryIndex);
							
							// create a temporary index mapping where the temporary index replaces the current index
							IndexMapping temporaryIndexMapping = new IndexMapping(getIndexMapping().getMappings());
							temporaryIndexMapping.register(mapping, temporaryIndex);
							
							// init searchers and writers
							EsDocumentSearcher temporaryIndexSearcher = new EsDocumentSearcher(this, temporaryIndexMapping, mapper);
							EsDocumentWriter temporaryIndexWriter = new EsDocumentWriter(this, temporaryIndexMapping, temporaryIndexSearcher, mapper);
							
							final DocumentMappingMigrator migrator;
							try {
								migrator = schema.migrator().getDeclaredConstructor().newInstance();
							} catch (Exception e) {
								throw new IndexException(String.format("Couldn't instantiate schema migrator '%s'", schema.migrator().getName()), e);
							}
							
							migrator.init(previousIndexSearcher);
							for (Hits<JsonNode> hits : readAllRaw(previousIndexSearcher, mapping, getBatchSize())) {
								hits.forEach(hit -> {
									temporaryIndexWriter.put(mapping, Objects.requireNonNull(migrator.migrate((ObjectNode) hit), "Migrator should never return null as migrated JSON object"));
								});
								try {
									temporaryIndexWriter.commit();
								} catch (IOException e) {
									throw new IndexException(String.format("Failed to migrate batch of index '%s' to mapping schema version '%s'.", index, schema.version()), e);
								}
							}
							
							// complete migration
							doDeleteIndexes(index);
							
							// recreate original index
							doCreateIndex(index, mapping, typeMappingOverrides, additionalTypeIndexConfiguration);
							waitForYellowHealth(index);
							
							// copy content back by running a reindex operation from tmp index to the newly recreated original
							try {
								reindex(temporaryIndex, index, null, true);
							} catch (IOException e) {
								throw new IndexException(String.format("Failed to copy back contents from '%s' to original index '%s' via reindex operation", temporaryIndex, index), e);
							}
							
							// delete temporary index
							doDeleteIndexes(temporaryIndex);
							
							break;
						default:
							throw new UnsupportedOperationException("Unknown schema migration strategy " + schema.strategy());
						}
						
						currentVersion = schema.version();
						log.info(migrationTaskDescription.replace("Migrating", "Migrated"));	
					}
					
				} else if (!compatibleChanges.isEmpty() || !incompatibleChanges.isEmpty()) {
					// same schema version, but there are field changes, report as error, as any schema change requires an explicit schema version to be registered in order to update the mapping and the content
					throw new IndexException(String.format("New schema version is required when changing the mapping of an existing index to another. '%s' has the following field changes '%s'. ", index, ImmutableSortedSet.<String>naturalOrder().addAll(compatibleChanges).addAll(incompatibleChanges).build()));
				} else {
					// no schema version changes, and no actual schema changes, good to go
				}
			} else {
				// does not exist, create it
				doCreateIndex(index, mapping, typeMapping, additionalTypeIndexConfiguration);
			}

			// as last step, register the index in the mapping registry so that it can be used by downstream modules
			this.indexMapping.register(mapping, index);
		}
		
		// wait until the cluster processes each index create request
		waitForYellowHealth(getIndexMapping().indices());
		if (!mappingsToRefresh.isEmpty()) {
			refresh(mappingsToRefresh, getIndexMapping());
		}
		
		log.info("'{}' indexes are ready.", name);
	}

	private static Iterable<Hits<JsonNode>> readAllRaw(EsDocumentSearcher previousIndexSearcher, DocumentMapping mapping, int batchSize) {
		return Query.select(JsonNode.class)
				.from(mapping.type())
				.where(Expressions.matchAll())
				.limit(batchSize)
				.build()
				.stream(previousIndexSearcher)
				::iterator;
	}

	private void putIndexMapping(final String index, Map<String, Object> typeMapping) {
		try {
			PutMappingRequest putMappingRequest = new PutMappingRequest(index).source(typeMapping);
			AcknowledgedResponse response = client.indices().updateMapping(putMappingRequest);
			checkState(response.isAcknowledged(), "Failed to update mapping of index '%s'", index);
		} catch (IOException e) {
			throw new IndexException(String.format("Failed to update mapping '%s'", index), e);
		}
	}

	private void doCreateIndex(String index, DocumentMapping mapping, Map<String, Object> typeMapping, Map<String, Object> additionalTypeIndexConfiguration) {
		// create index
		final Map<String, Object> indexSettings;
		try {
			indexSettings = createIndexSettings(additionalTypeIndexConfiguration);
			log.info("Configuring '{}' index with settings: {}", index, indexSettings);
		} catch (IOException e) {
			throw new IndexException("Couldn't prepare settings for index " + index, e);
		}
		
		final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index)
			.mapping(typeMapping)
			.settings(indexSettings);
		
		try {
			final CreateIndexResponse response = client.indices().create(createIndexRequest);
			checkState(response.isAcknowledged(), "Failed to create index '%s' for type '%s'", name, mapping.typeAsString());
		} catch (Exception e) {
			throw new IndexException(String.format("Failed to create index '%s' for type '%s'", name, mapping.typeAsString()), e);
		}		
	}
	
	private void doDeleteIndexes(final String...indexesToDelete) {
		final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest().indices(indexesToDelete);
		try {
			final AcknowledgedResponse deleteIndexResponse = client()
					.indices()
					.delete(deleteIndexRequest);
			checkState(deleteIndexResponse.isAcknowledged(), "Failed to delete all ES indices for '%s'.", name);
		} catch (Exception e) {
			throw new IndexException(String.format("Failed to delete all ES indices for '%s'.", name), e);
		}
	}

	private Map<String, Object> stringsAsKeywords() {
		return Map.of(
			"strings_as_keywords", Map.of(
				"match_mapping_type", "string",
				"mapping", Map.of(
					"type", "keyword"
				)
			)
		);
	}

	private Map<String, Object> createIndexSettings(Map<String, Object> additionalTypeIndexSettings) throws IOException {
		InputStream analysisStream = getClass().getResourceAsStream("analysis.json");
		Settings analysisSettings = Settings.builder()
				.loadFromStream("analysis.json", analysisStream, true)
				.build();
		
		// FIXME: Is XContent a good alternative to a Map? getAsStructureMap is now private
		Map<String, Object> analysisMap = ReflectionUtils.callMethod(Settings.class, analysisSettings, "getAsStructuredMap");
		
		final Map<String, Object> settings = new LinkedHashMap<>();
		
		// put defaults in for shards and replicas, can be configured externally via index specific settings
		settings.put(IndexClientFactory.NUMBER_OF_SHARDS, settings().get(IndexClientFactory.NUMBER_OF_SHARDS));
		settings.put(IndexClientFactory.NUMBER_OF_REPLICAS, settings().get(IndexClientFactory.NUMBER_OF_REPLICAS));
		settings.put(IndexClientFactory.RESULT_WINDOW_KEY, settings().get(IndexClientFactory.RESULT_WINDOW_KEY));
		settings.put(IndexClientFactory.MAX_TERMS_COUNT_KEY, settings().get(IndexClientFactory.MAX_TERMS_COUNT_KEY));
		settings.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, settings().get(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY));
		
		// add external config
		settings.putAll(additionalTypeIndexSettings);
		
		// override any external or missing configuration with hardcoded defaults, these are required for a correctly working revision index
		settings.put("analysis", analysisMap);
		// disable es refresh, we will do it manually on each commit
		// XXX we intentionally disallow the configuration of the refresh_interval via configuration (required for consistent writes)
		settings.put("refresh_interval", "-1");
		// use async durability for the translog
		// XXX we intentionally disallow the configuration of the translog.durability via configuration (required for consistent writes)
		settings.put("translog.durability", "async");
		// wait all shards during writes
		// XXX we intentionally disallow the configuration of the write.wait_for_active_shards via configuration (required for consistent writes)
		settings.put("write.wait_for_active_shards", "all");
		
		return settings;
	}
	
	private void waitForYellowHealth(String... indices) {
		if (!CompareUtils.isEmpty(indices)) {
			/*
			 * See https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cluster-health.html 
			 * for the low-level structure of the cluster health request.
			 */
			final Object clusterTimeoutSetting = settings.getOrDefault(IndexClientFactory.CLUSTER_HEALTH_TIMEOUT, IndexClientFactory.DEFAULT_CLUSTER_HEALTH_TIMEOUT);
			final Object socketTimeoutSetting = settings.getOrDefault(IndexClientFactory.SOCKET_TIMEOUT, IndexClientFactory.DEFAULT_SOCKET_TIMEOUT);
			final int clusterTimeout = clusterTimeoutSetting instanceof Integer ? (int) clusterTimeoutSetting : Integer.parseInt((String) clusterTimeoutSetting);
			final int socketTimeout = socketTimeoutSetting instanceof Integer ? (int) socketTimeoutSetting : Integer.parseInt((String) socketTimeoutSetting);
			final int pollTimeout = socketTimeout / 2;
			
			final ClusterHealthRequest req = new ClusterHealthRequest(indices)
					.waitForYellowStatus() // Wait until yellow status is reached
					.timeout(String.format("%sms", pollTimeout)); // Poll interval is half the socket timeout
			req.level(Level.INDICES); // Detail level should be concerned with the indices in the path
			
			final long startTime = System.currentTimeMillis();
			final long endTime = startTime + clusterTimeout; // Polling finishes when the cluster timeout is reached
			long currentTime = startTime;
			
			ClusterHealthResponse response = null;
			
			do {
				
				try {
					response = client().cluster().health(req);
					currentTime = System.currentTimeMillis();
					if (response != null && !response.isTimedOut()) {
						break; 
					}
				} catch (Exception e) {
					throw new IndexException("Couldn't retrieve cluster health for index " + name, e);
				}
				
			} while (currentTime < endTime);
			
			if (response == null || response.isTimedOut()) {
				throw new IndexException(String.format("Cluster health did not reach yellow status for '%s' indexes after %s ms.", name, currentTime - startTime), null);
			} else {
				log.info("Cluster health for '{}' indexes reported as '{}' after {} ms.", name, response.getStatus(), currentTime - startTime);
			}
		}
	}

	private Map<String, Object> toProperties(DocumentMapping mapping) {
		Map<String, Object> properties = newHashMap();
		for (Field field : mapping.getFields()) {
			// skip transient fields
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			
			com.b2international.index.mapping.Field fieldAnnotation = field.getAnnotation(com.b2international.index.mapping.Field.class);
			final String property = field.getName();
			final Class<?> fieldType = NumericClassUtils.unwrapCollectionType(field);
			
			if (Map.class.isAssignableFrom(fieldType)) {
				// allow dynamic mappings for dynamic objects like field using Map
				final Map<String, Object> prop = newHashMap();
				prop.put("type", "object");
				if (fieldAnnotation != null && !fieldAnnotation.index()) {
					prop.put("enabled", false);
				} else {
					prop.put("dynamic", "true");			
				}
				properties.put(property, prop);
				continue;
			} else if (fieldType.isAnnotationPresent(Doc.class)) {
				Doc annotation = fieldType.getAnnotation(Doc.class);
				// this is a nested document type create a nested mapping
				final Map<String, Object> prop = newHashMap();
				// XXX type: object is the default for nested objects, ES won't store it in the mapping and will default to object even if explicitly set, which would cause unnecessary mapping update during boot
				if (annotation.nested()) {
					prop.put("type", "nested");
				}
				// disable indexing/doc_values for the field
				// XXX enabled: true is the default, ES won't store it in the mapping and will default to true even if explicitly set, which would cause unnecessary mapping update during boot
				if (!annotation.index() || (fieldAnnotation != null && !fieldAnnotation.index())) {
					prop.put("enabled", false);
				}
				prop.putAll(toProperties(new DocumentMapping(fieldType, true /*nested*/)));
				properties.put(property, prop);
			} else {
				final Map<String, Object> prop = newHashMap();
				addFieldProperties(prop, fieldType);
				
				// add aliases
				final Map<String, FieldAlias> fieldAliases = mapping.getFieldAliases(property);
				if (!fieldAliases.isEmpty()) {
					final Map<String, Object> fields = newHashMapWithExpectedSize(fieldAliases.size());
					for (FieldAlias fieldAlias : fieldAliases.values()) {
						final Map<String, Object> fieldAliasProps = newHashMap();
						// only keywords can have normalizers
						switch (fieldAlias.type()) {
						case KEYWORD:
							fieldAliasProps.put("type", "keyword");
							String normalizer = fieldAlias.normalizer().getNormalizer();
							if (!Strings.isNullOrEmpty(normalizer)) {
								fieldAliasProps.put("normalizer", normalizer);
							}
							// XXX index: true is the default, ES won't store it in the mapping and will default to true even if explicitly set, which would cause unnecessary mapping update during boot
							// XXX doc_values: true is the default, ES won't store it in the mapping and will default to true even if explicitly set, which would cause unnecessary mapping update during boot
							if (!fieldAlias.index()) {
								fieldAliasProps.put("index", false);
								fieldAliasProps.put("doc_values", false);
							}
							break;
						case TEXT:
							fieldAliasProps.put("type", "text");
							fieldAliasProps.put("analyzer", fieldAlias.analyzer().getAnalyzer());
							if (fieldAlias.searchAnalyzer() != Analyzers.INDEX) {
								fieldAliasProps.put("search_analyzer", fieldAlias.searchAnalyzer().getAnalyzer());
							}
							// XXX index: true is the default, ES won't store it in the mapping and will default to true even if explicitly set, which would cause unnecessary mapping update during boot
							if (!fieldAlias.index()) {
								fieldAliasProps.put("index", false);
							}
							break;
						default: throw new UnsupportedOperationException("Unknown field alias type: " + fieldAlias.type());
						}
						fields.put(fieldAlias.name(), fieldAliasProps);
					}
					prop.put("fields", fields);
				}
				
				// disable indexing/doc_values for the field
				// XXX enabled: true is the default, ES won't store it in the mapping and will default to true even if explicitly set, which would cause unnecessary mapping update during boot
				if (fieldAnnotation != null && !fieldAnnotation.index()) {
					prop.put("index", false);
					prop.put("doc_values", false);
				}
				
				// register mapping
				properties.put(property, prop);
			}
		}
		
		return ImmutableMap.of("properties", properties);
	}

	private void addFieldProperties(Map<String, Object> fieldProperties, Class<?> fieldType) {
		if (Enum.class.isAssignableFrom(fieldType) || NumericClassUtils.isBigDecimal(fieldType) || String.class.isAssignableFrom(fieldType)) {
			fieldProperties.put("type", "keyword");
		} else if (NumericClassUtils.isDouble(fieldType)) {
			fieldProperties.put("type", "double");
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
		} else if (fieldType.isAnnotationPresent(IP.class)) {
			fieldProperties.put("type", "ip");
		} else if (hasJsonValue(fieldType)) {
			fieldProperties.put("type", "keyword"); // FIXME for now consider only String based @JsonValue annotations
		} else {
			// Any other type will result in a sub-object that only appears in _source
			fieldProperties.put("type", "object");
			fieldProperties.put("enabled", false);
		}
	}

	// returns true if one of the methods of the type has the @JsonValue annotation
	private boolean hasJsonValue(Class<?> fieldType) {
		for (Method method : fieldType.getMethods()) {
			if (method.isAnnotationPresent(JsonValue.class) && String.class == method.getReturnType()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void delete() {
		if (exists()) {
			doDeleteIndexes(getIndexMapping().indices());
		}
	}

	@Override
	public void clear(Collection<Class<?>> types) {
		if (CompareUtils.isEmpty(types)) {
			return;
		}
		
		final Set<DocumentMapping> typesToRefresh = Collections.synchronizedSet(newHashSetWithExpectedSize(types.size()));
		
		for (Class<?> type : types) {
			final DocumentMapping mapping = this.indexMapping.getMapping(type);
			final String index = getIndexMapping().getTypeIndex(mapping);
			if (exists(index)) {
				if (bulkDelete(new BulkDelete<>(type, Expressions.matchAll()))) {
					typesToRefresh.add(mapping);
				}
			}
		}
		
		refresh(typesToRefresh, getIndexMapping());
	}

	@Override
	public Map<String, Object> settings() {
		return settings;
	}
	
	private int getBatchSize() {
		return Integer.parseInt((String) settings.get(IndexClientFactory.RESULT_WINDOW_KEY));
	}
	
	@Override
	public void updateSettings(Map<String, Object> newSettings) {
		if (CompareUtils.isEmpty(newSettings)) {
			return;
		}

		final Set<String> typeIndexNames = this.indexMapping.getMappings().getTypeIndexNames();
		
		// ignore allowed dynamic settings
		Set<String> unsupportedDynamicSettings = Sets.difference(newSettings.keySet(), DYNAMIC_SETTINGS);
		// ignore local only settings
		unsupportedDynamicSettings = Sets.difference(unsupportedDynamicSettings, LOCAL_SETTINGS);
		// ignore type specific configurations
		unsupportedDynamicSettings = Sets.difference(unsupportedDynamicSettings, typeIndexNames);
		
		if (!unsupportedDynamicSettings.isEmpty()) {
			throw new IndexException(String.format("Settings [%s] are not dynamically updateable.", unsupportedDynamicSettings), null);
		}
		
		boolean shouldUpdate = false;
		for (String settingKey : newSettings.keySet()) {
			Object currentValue = settings.get(settingKey);
			Object newValue = newSettings.get(settingKey);
			if (!Objects.equals(currentValue, newValue)) {
				shouldUpdate = true;
			}
		}
		
		if (!shouldUpdate) {
			return;
		}
		
		Map<String, Object> esSettings = new HashMap<>(newSettings);
		// remove any local settings from esSettings
		esSettings.keySet().removeAll(LOCAL_SETTINGS);
		// also remove type index specific mapping settings, those are dynamically not adjustable in the remote ES cluster
		esSettings.keySet().removeAll(typeIndexNames);
		
		// if some settings are local only, update only the local settings object
		if (!esSettings.isEmpty()) {
			// update only the known indices, everything that's currently non-existent will be created with the right setting when create is called
			for (final String index : this.indexMapping.indices()) {
				// if this index is still present update the settings based on the new settings
				if (exists(index)) {
					
					// construct a type specific setting based on external configuration
					Map<String, Object> typeIndexSettings = new HashMap<>(esSettings);
					
					try {
						log.info("Applying settings '{}' changes in index {}...", esSettings, index);
						AcknowledgedResponse response = client.indices().updateSettings(new UpdateSettingsRequest().indices(index).settings(typeIndexSettings));
						checkState(response.isAcknowledged(), "Failed to update index settings '%s'.", index);
					} catch (IOException e) {
						throw new IndexException(String.format("Couldn't update settings of index '%s'", index), e);
					}
				}
			}
		}
		
		// update both local and es settings
		settings.putAll(newSettings);
	}

	@Override
	public void updateMappings(Mappings mappings) {
		this.indexMapping.updateMappings(mappings);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void optimize(int maxSegments) {
//		client().admin().indices().prepareForceMerge(name).setMaxNumSegments(maxSegments).get();
//		waitForYellowHealth();
	}
	
	private String generateTypeIndexName(DocumentMapping mapping) {
		if (mapping.getParent() != null) {
			return String.format("%s%s-%s", this.prefix, this.name, mapping.getParent().typeAsString());
		} else {
			return String.format("%s%s-%s", this.prefix, this.name, mapping.typeAsString());
		}
	}
	
	@Override
	public EsClient client() {
		return client;
	}
	
	@Override
	public Es8Client es8Client() throws UnsupportedOperationException {
		if (es8Client == null) {
			throw new UnsupportedOperationException("Elasticsearch high-level client with new ES8 features is not available.");
		}
		return es8Client;
	}
	
	@Override
	public RefreshResponse refresh(String...indices) {
		if (log.isTraceEnabled()) {
			log.trace("Refreshing indexes '{}'", Arrays.toString(indices));
		}
		
		try {
		
			final RefreshResponse refreshResponse = client().indices().refresh(new RefreshRequest(indices));
			if (RestStatus.OK != refreshResponse.getStatus() && log.isErrorEnabled()) {
				log.error("Index refresh request of '{}' returned with status {}", Arrays.toString(indices), refreshResponse.getStatus());
			}
			return refreshResponse;
		} catch (Exception e) {
			throw new IndexException(String.format("Failed to refresh ES indexes '%s'.", Arrays.toString(indices)), e);
		}
	}
	
	@Override
	public ReindexResult reindex(String sourceIndex, String destinationIndex, RemoteInfo remoteInfo, boolean refresh) throws IOException {
		
		String remoteAddress = getRemoteAddress(remoteInfo);
		
		AtomicInteger retries = new AtomicInteger(1);
		
		BulkByScrollResponse response = executeReindex(
			sourceIndex,
			destinationIndex,
			remoteInfo,
			refresh,
			org.elasticsearch.index.reindex.AbstractBulkByScrollRequest.DEFAULT_SCROLL_SIZE,
			retries
		);
		
		if (response.isTimedOut()) {
			throw new IndexException(
					String.format(
						"Reindex operation of source index: '%s' and destination index '%s' timed out at host: '%s'",
						sourceIndex,
						destinationIndex,
						remoteAddress
					), null);
		}
		
		if (response.getSearchFailures().size() > 0) {
			
			response.getSearchFailures().forEach(failure -> {
				log().error(
					"There were search failures during reindex operation. Index: '{}', Status: '{}', Cause: {}",
					failure.getIndex(),
					failure.getStatus().getStatus(),
					failure.getReason()
				);
			});
			
			throw new IndexException(String.format("There were search failures during the reindex operation (index '%s'). See logs for more details.", sourceIndex), null);
			
		}
		
		if (response.getBulkFailures().size() > 0) {
			
			response.getBulkFailures().forEach(failure -> {
				log().error(
					"There were bulk failures during reindex operation. Index: '{}', Message: '{}', Status: '{}', Cause: {}",
					failure.getIndex(),
					failure.getMessage(),
					failure.getStatus(),
					failure.getCause()
				);
			});

			throw new IndexException(String.format("There were bulk failures during the reindex operation (index '%s'). See logs for more details.", destinationIndex), null);
			
		}
		
		return ReindexResult.builder()
			.took(TimeUtil.nanoToReadableString(response.getTook().nanos()))
			.createdDocuments(response.getCreated())
			.updatedDocuments(response.getUpdated())
			.deletedDocuments(response.getDeleted())
			.noops(response.getNoops())
			.versionConflicts(response.getVersionConflicts())
			.totalDocuments(response.getTotal())
			.sourceIndex(sourceIndex)
			.destinationIndex(destinationIndex)
			.remoteAddress(remoteAddress)
			.refresh(refresh)
			.retries(retries.get() > 1 ? Long.valueOf(retries.get()) : null) // do not track successful first attempts
			.build();
		
	}

	private String getRemoteAddress(RemoteInfo remoteInfo) {
		return Optional.ofNullable(remoteInfo).map(info -> String.format("%s://%s:%s", info.getScheme(), info.getHost(), info.getPort())).orElse(null);
	}

	private BulkByScrollResponse executeReindex(String sourceIndex, String destinationIndex, RemoteInfo remoteInfo, boolean refresh, int batchSize, AtomicInteger retries) throws IOException {
		
		BulkByScrollResponse response = null;
		
		try {
			
			response = client().reindex(sourceIndex, destinationIndex, remoteInfo, refresh, batchSize);
			
		} catch (IllegalArgumentException /* thrown by the transport client */ | ElasticsearchStatusException /* thrown by the http client */ e) {
			
			if (!Strings.isNullOrEmpty(e.getMessage()) && e.getMessage().contains("Remote responded with a chunk that was too large. Use a smaller batch size.")) {
				
				if (batchSize == 1) {
					throw new IndexException(e.getMessage(), e); // cannot minimize batch size any further
				}
				
				log.info("Retrying reindex request of '{}' with smaller batch size '{}'", sourceIndex, batchSize / 2);

				retries.incrementAndGet();
				
				return executeReindex(sourceIndex, destinationIndex, remoteInfo, refresh, batchSize / 2, retries);
				
			} else {
				throw new IndexException(
						String.format("Reindex operation of source index: '%s' and destination index '%s' failed at host: '%s'",
							sourceIndex,
							destinationIndex,
							getRemoteAddress(remoteInfo)
						), e);
			}
			
		} catch (Exception e) {
			
			throw new IndexException(
				String.format("Reindex operation of source index: '%s' and destination index '%s' failed at host: '%s'",
					sourceIndex,
					destinationIndex,
					getRemoteAddress(remoteInfo)
				), e);
			
		}
		
		return response;
		
	}
	
	public void refresh(Set<DocumentMapping> typesToRefresh, IndexMapping indexMapping) {
		if (!CompareUtils.isEmpty(typesToRefresh)) {
			refresh(typesToRefresh.stream().map(indexMapping::getTypeIndex).toArray(String[]::new));
		}
	}
	
	public boolean bulkUpdate(final BulkUpdate<?> update) {
		final DocumentMapping mapping = this.indexMapping.getMapping(update.getType());
		final String index = getIndexMapping().getTypeIndex(mapping);
		final String rawScript = mapping.getScript(update.getScript()).script();
		org.elasticsearch.script.Script script = new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", rawScript, Map.copyOf(update.getParams()));
		return bulkIndexByScroll(client, mapping, index, update.getFilter(), "update", script, update.toString());
	}

	public boolean bulkDelete(final BulkDelete<?> delete) {
		final DocumentMapping mapping = this.indexMapping.getMapping(delete.getType());
		final String index = getIndexMapping().getTypeIndex(mapping);
		return bulkIndexByScroll(client, mapping, index, delete.getFilter(), "delete", null, delete.toString());
	}
	
	public <T> T updateImmediately(final Update<T> update, final ObjectMapper documentMapper) {
		final DocumentMapping mapping = this.indexMapping.getMapping(update.getType());
		final String rawScript = mapping.getScript(update.getScript()).script();
		org.elasticsearch.script.Script script = new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", rawScript, Map.copyOf(update.getParams()));
		
		final String typeIndex = getIndexMapping().getTypeIndex(mapping);
		var req = new org.elasticsearch.action.update.UpdateRequest()
			.index(typeIndex)
			.id(update.getKey())
			.script(script)
			// .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
			.retryOnConflict(5)
			.fetchSource(true);
		
		try {
			
			final T upsert = update.getUpsert();
			if (upsert != null) {
				final byte[] _source = documentMapper.writeValueAsBytes(upsert);
				req.upsert(_source, XContentType.JSON);
			}
		
			var resp = client.update(req);
			final byte[] source = resp.getGetResult().source();
			return documentMapper.readValue(source, update.getType());
			
		} catch (IOException e) {
			throw new IndexException("Could not execute immediate update.", e);
		}
	}

	private boolean bulkIndexByScroll(final EsClient client,
			final DocumentMapping mapping,
			final String index,
			final Expression filter,
			final String command, 
			final org.elasticsearch.script.Script script,
			final String operationDescription) {
		
		final QueryBuilder query = new EsQueryBuilder(mapping, settings, log).build(filter);
		
		boolean needsRefresh = false;
		long versionConflicts = 0;
		int attempts = DEFAULT_MAX_NUMBER_OF_VERSION_CONFLICT_RETRIES;
		
		do {

			try {
				
				final BulkByScrollResponse response;
				final int batchSize = getBatchSize();
				if ("update".equals(command)) {
					response = client.updateByQuery(index, batchSize, script, query);
				} else if ("delete".equals(command)) {
					response = client.deleteByQuery(index, batchSize, query);
				} else {
					throw new UnsupportedOperationException("Not implemented command: " + command);
				}
				
				final long updateCount = response.getUpdated();
				final long deleteCount = response.getDeleted();
				final long noops = response.getNoops();
				final List<Failure> failures = response.getBulkFailures();
				
				versionConflicts = response.getVersionConflicts();
				
				boolean updated = updateCount > 0;
				if (updated) {
					log().info("Updated {} {} documents with bulk {}", updateCount, mapping.typeAsString(), operationDescription);
					needsRefresh = true;
				}
				
				boolean deleted = deleteCount > 0;
				if (deleted) {
					log().info("Deleted {} {} documents with bulk {}", deleteCount, mapping.typeAsString(), operationDescription);
					needsRefresh = true;
				}
				
				if (!updated && !deleted) {
					log().warn("Bulk {} could not be applied to {} documents, no-ops ({}), conflicts ({})",
							operationDescription,
							mapping.typeAsString(), 
							noops, 
							versionConflicts);
				}
				
				if (failures.size() > 0) {
					boolean versionConflictsOnly = true;
					for (Failure failure : failures) {
						final String failureMessage = failure.getCause().getMessage();
						final int failureStatus = failure.getStatus().getStatus();
						
						if (failureStatus != RestStatus.CONFLICT.getStatus()) {
							versionConflictsOnly = false;
							log().error("Index failure during bulk update: {}", failureMessage);
						} else {
							log().warn("Version conflict reason: {}", failureMessage);
						}
					}

					if (!versionConflictsOnly) {
						throw new IllegalStateException("There were indexing failures during bulk updates. See logs for all failures.");
					}
				}
				
				if (attempts <= 0) {
					throw new IndexException("There were indexing failures during bulk updates. See logs for all failures.", null);
				}
				
				if (versionConflicts > 0) {
					--attempts;
					try {
						Thread.sleep(100 + random.nextInt(900));
						refresh(index);
					} catch (InterruptedException e) {
						throw new IndexException("Interrupted", e);
					}
				}
			} catch (IOException e) {
				throw new IndexException("Could not execute bulk update.", e);
			}
		} while (versionConflicts > 0);
		
		return needsRefresh;
	}

}
