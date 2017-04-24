/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.TimeUnit;

import com.b2international.index.mapping.Mappings;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public interface IndexClientFactory {

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
	 * Configuration key to use to specify the hard commit interval of an index.
	 * Lucene-only configuration. For the elasticsearch module use the appropriate elasticsearch.yml config property.
	 */
	String COMMIT_INTERVAL_KEY = "commitInterval";

	/**
	 * Configuration key to specify the synchronization interval of the underlying transaction log.
	 * Lucene-only configuration. For the elasticsearch module use the appropriate elasticsearch.yml config property.
	 */
	String TRANSLOG_SYNC_INTERVAL_KEY = "translogSyncInterval";

	/**
	 * Configuration key to specify slow log related configuration.
	 * Lucene-only configuration. For the elasticsearch module use the appropriate elasticsearch.yml config properties.
	 */
	String SLOW_LOG_KEY = "slowlog";

	/**
	 * Configuration key to specify the maximum number of documents to retrieve
	 * in a single call from the index.
	 */
	String RESULT_WINDOW_KEY = "max_result_window";
	
	/**
	 * Configuration key to specify the number of shards for the index.
	 * Currently only the index.es fragment supports.
	 */
	String NUMBER_OF_SHARDS = "number_of_shards";

	/**
	 * Configuration key to specify the concurrency level for bulk commit operations.
	 */
	String COMMIT_CONCURRENCY_LEVEL = "concurrencyLevel";

	/**
	 * The default commit interval is 15 seconds.
	 */
	long DEFAULT_COMMIT_INTERVAL = TimeUnit.SECONDS.toMillis(15L);

	/**
	 * The default translog sync interval is 5 seconds.
	 */
	long DEFAULT_TRANSLOG_SYNC_INTERVAL = TimeUnit.SECONDS.toMillis(5L);

	/**
	 * The default result window (~10k items)
	 */
	int DEFAULT_RESULT_WINDOW = 10_099;
	
	/**
	 * The default concurrency level for the bulk operations depends on the number of cores you have <code>max(1, cores / 4)</code>.
	 * Elasticsearch module only configuration key. The Lucene-based implementation supports single-threaded commits.
	 */
	int DEFAULT_COMMIT_CONCURRENCY_LEVEL = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);

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
