/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;

import javax.net.ssl.SSLContext;

import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public interface IndexClientFactory {

	//
	// Configuration keys
	//
	
	/**
	 * Configuration key to use when specifying the configuration directory
	 * where index specific configuration can be found.
	 */
	String CONFIG_DIRECTORY = "configDirectory";

	/**
	 * Configuration key to use when creating file system based index.
	 */
	String DATA_DIRECTORY = "dataDirectory";

	/**
	 * Configuration key to specify the synchronization interval of the underlying transaction log.
	 */
	String TRANSLOG_SYNC_INTERVAL_KEY = "translog.sync_interval";

	/**
	 * Configuration key to specify the maximum number of documents to retrieve
	 * in a single call from the index.
	 */
	String RESULT_WINDOW_KEY = "max_result_window";
	
	/**
	 * Configuration key to specify the maximum number of term values in a terms query. By default it is set to {@value #DEFAULT_MAX_TERMS_COUNT}.
	 */
	String MAX_TERMS_COUNT_KEY = "max_terms_count";
	
	/**
	 * Configuration key to specify the number of shards for the index.
	 */
	String NUMBER_OF_SHARDS = "number_of_shards";
	
	/**
	 * Configuration key to specify the number of replicas for the index.
	 */
	String NUMBER_OF_REPLICAS = "number_of_replicas";

	/**
	 * Configuration key to specify the concurrency level for bulk commit operations.
	 */
	String COMMIT_CONCURRENCY_LEVEL = "concurrencyLevel";

	/**
	 * Configuration key to specify the name of the embedded or TCP based Elasticsearch cluster to connect to.
	 */
	String CLUSTER_NAME = "clusterName";
	
	/**
	 * Configuration key to specify the URL of the Elasticsearch cluster to connect to.
	 */
	String CLUSTER_URL = "clusterUrl";
	
	/**
	 * Configuration key to specify the user name for authenticating with the Elasticsearch cluster.
	 */
	String CLUSTER_USERNAME = "clusterUsername";
	
	/**
	 * Configuration key to specify the password for authenticating with the Elasticsearch cluster.
	 */
	String CLUSTER_PASSWORD = "clusterPassword";
	
	/**
	 * Configuration key to specify the cluster {@link SSLContext} instance to use secure connections when communicating with external Elasticsearch instances.
	 */
	String CLUSTER_SSL_CONTEXT = "clusterSslContext";
	
	/**
	 * Configuration key to specify the string each index name should be prefixed with (used in multi-tenant deployments). 
	 */
	String INDEX_PREFIX = "indexPrefix";

	/**
	 * Configuration key to specify the REST client connection timeout in milliseconds.
	 */
	String CONNECT_TIMEOUT = "connectTimeout";
	
	/**
	 * Configuration key to specify the REST client communication timeout in milliseconds.
	 */
	String SOCKET_TIMEOUT = "socketTimeout";
	
	/**
	 * Configuration key to specify the time to wait until yellow cluster health state is reached in milliseconds.
	 */
	String CLUSTER_HEALTH_TIMEOUT = "clusterHealthTimeout";
	
	/**
	 * Configuration key to specify the number of bulk action items to send to the Elasticsearch cluster when performing bulk commit operations
	 */
	String BULK_ACTIONS_SIZE = "bulk_action_size";
	
	/**
	 * Configuration key to specify the number of bulk action items in MB to send to the Elasticsearch cluster when performing bulk commit operations
	 */
	String BULK_ACTIONS_SIZE_IN_MB = "bulk_action_size_in_mb";

	// Non-elasticsearch configuration keys
	
	/**
	 * Configuration key to specify the low watermark of commits and raise a log entry with commit properties indicating a minor problem.  
	 */
	String COMMIT_WATERMARK_LOW_KEY = "commit.watermark.low";
	
	/**
	 * Configuration key to specify the high watermark of commits and raise a log entry with commit properties indicating a major problem.
	 */
	String COMMIT_WATERMARK_HIGH_KEY = "commit.watermark.high";
	
	/**
	 * Configuration key to override type mappings in an index if required for customization, external configuration, etc. It allows to partially
	 * update (patch) the type mapping provided by Snow Owl to override certain features, experiment, etc.
	 * 
	 * Example via snowowl.yml:
	 * 
	 * repository:
	 *   index:
	 *     snomed:
	 *       concept: 
	 *         mappings:
	 *           properties:
	 *             similarity:
	 *               type: object
	 *               properties:
	 *                 predicted_value:
	 *                   type: half_float
	 */
	String MAPPINGS = "mappings";
	
	//
	// Default values
	//
	
	/**
	 * The default translog sync interval is 5 seconds.
	 */
	String DEFAULT_TRANSLOG_SYNC_INTERVAL = "5s";

	/**
	 * The default max result window (from Elasticsearch default),
	 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html#dynamic-index-settings">more details</a>
	 */
	int DEFAULT_RESULT_WINDOW = 10_000;
	
	/**
	 * The default max terms count (from Elasticsearch default), 
	 * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html">more details</a>
	 */
	int DEFAULT_MAX_TERMS_COUNT = 65_536;
	
	/**
	 * The default concurrency level for the bulk operations depends on the number of cores you have <code>max(1, cores / 4)</code>.
	 * Elasticsearch module only configuration key.
	 */
	int DEFAULT_COMMIT_CONCURRENCY_LEVEL = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
	
	/**
	 * The default index prefix is empty
	 */
	String DEFAULT_INDEX_PREFIX = "";

	/**
	 * The default connection timeout is 1s
	 */
	int DEFAULT_CONNECT_TIMEOUT = 1_000;

	/**
	 * The default socket timeout (sending requests, waiting for a response) is 30s
	 */
	int DEFAULT_SOCKET_TIMEOUT = 30_000;

	/**
	 * The default timeout for waiting until yellow cluster health is reached is 30s
	 */
	int DEFAULT_CLUSTER_HEALTH_TIMEOUT = 30_000;
	
	/**
	 * The default cluster.name value for embedded nodes and tcp based clients.
	 */
	String DEFAULT_CLUSTER_NAME = "elastic-snowowl";
	
	/**
	 * Default number of bulk action items to send in a single outgoing bulk request.
	 */
	int DEFAULT_BULK_ACTIONS_SIZE = 10_000;
	
	/**
	 * Default size in megabytes to limit all outgoing bulk requests.
	 */
	int DEFAULT_BULK_ACTIONS_SIZE_IN_MB = 49;
	
	/**
	 * Default amount of commit details indicating low watermark
	 */
	int DEFAULT_COMMIT_WATERMARK_LOW_VALUE = 10_000;
	
	/**
	 * Default amount of commit details indicating high watermark
	 */
	int DEFAULT_COMMIT_WATERMARK_HIGH_VALUE = 50_000;

	/**
	 * Default number_of_shards value for all indices.
	 */
	String DEFAULT_NUMBER_OF_SHARDS = "1";

	/**
	 * Default number_of_replicas value for all indices. 
	 */
	String DEFAULT_NUMBER_OF_REPLICAS = "0";

	/**
	 * Page size when streaming query result should not exceed this maximum value.
	 */
	int MAX_PAGE_SIZE = 10_000;

	/**
	 * Create a new {@link IndexClient} with the given name.
	 * 
	 * @param name
	 * @param mapper
	 * @param mappings
	 * @param settings
	 * @return
	 */
	IndexClient createClient(String name, ObjectMapper mapper, Mappings mappings, Map<String, Object> settings);
}
