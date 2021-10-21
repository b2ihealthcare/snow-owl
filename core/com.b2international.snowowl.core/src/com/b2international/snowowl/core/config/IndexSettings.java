/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.config;

import java.util.HashMap;
import java.util.Map;

import com.b2international.commons.options.HashMapOptions;

/**
 * Globally available immutable index settings based on all configuration files (snowowl_config.yml, elasticsearch.yml).
 * 
 * @since 5.10
 */
public final class IndexSettings extends HashMapOptions {

	private static final long serialVersionUID = -8695509362236134666L;

	/**
	 * Configures and returns an index specific settings map to use when initializing the given index.
	 * 
	 * @param config - the main index configuration object
	 * @param indexName - the index to configure
	 * @param defaultIndexSettings - default settings if there is no customized configuration available in the configuration file
	 * @return
	 */
	public Map<String, Object> forIndex(IndexConfiguration config, String indexName, Map<String, Object> defaultIndexSettings) {
		Object customIndexSettings = config.getCustomIndexConfigurations().getOrDefault(indexName, defaultIndexSettings);
		Map<String,Object> settings = new HashMap<>(this);
		if (customIndexSettings instanceof Map<?, ?>) {
			settings.putAll((Map<String, Object>) customIndexSettings);
		}
		return settings;
	}

}
