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
	 * Configuration key to use when creating file system based index.
	 */
	String DIRECTORY = "directory";

	/**
	 * Configuration key to use to specify the hard commit interval of an index.
	 */
	String COMMIT_INTERVAL_KEY = "commitInterval";
	
	String TRANSLOG_SYNC_INTERVAL_KEY = "translogSyncInterval";

	/**
	 * Configuration key to specify the slf4j Logger instance to use for the index.
	 */
	String LOG_KEY = "slf4j.logger";
	
	/**
	 * Configuration key to specify slow log related configuration.
	 */
	String SLOW_LOG_KEY = "slowlog";
	
	/**
	 * Configuration key to specify the maximum number of documents to retrieve in a single call from the index.
	 */
	String RESULT_WINDOW_KEY = "max_result_window";

	/**
	 * The default commit interval is 15 seconds.
	 */
	long DEFAULT_COMMIT_INTERVAL = TimeUnit.SECONDS.toMillis(15L);
	
	long DEFAULT_TRANSLOG_SYNC_INTERVAL = TimeUnit.SECONDS.toMillis(5L);
	
	/**
	 * The default result window (~10k items)
	 */
	int DEFAULT_RESULT_WINDOW = 10_099;

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
