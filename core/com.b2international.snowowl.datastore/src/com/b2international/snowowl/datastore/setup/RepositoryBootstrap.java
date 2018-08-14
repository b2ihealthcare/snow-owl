/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.setup;

import java.util.Map;

import com.b2international.index.IndexClientFactory;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.DefaultBootstrapFragment;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.ModuleConfig;
import com.b2international.snowowl.core.setup.ModuleConfigs;
import com.b2international.snowowl.datastore.config.IndexConfiguration;
import com.b2international.snowowl.datastore.config.IndexSettings;
import com.b2international.snowowl.datastore.config.RepositoryConfiguration;
import com.b2international.snowowl.rpc.RpcConfiguration;
import com.google.common.collect.ImmutableMap;

/**
 * @since 3.4
 */
@ModuleConfigs({
	@ModuleConfig(fieldName = "repository", type = RepositoryConfiguration.class),
	@ModuleConfig(fieldName = "rpc", type = RpcConfiguration.class)
})
public class RepositoryBootstrap extends DefaultBootstrapFragment {
	
	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final IndexSettings indexSettings = new IndexSettings();
		indexSettings.putAll(initIndexSettings(env));
		env.services().registerService(IndexSettings.class, indexSettings);
	}
	
	private Map<String, Object> initIndexSettings(Environment env) {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put(IndexClientFactory.DATA_DIRECTORY, env.getDataDirectory().toPath().resolve("indexes").toString());
		builder.put(IndexClientFactory.CONFIG_DIRECTORY, env.getConfigDirectory().toPath().toString());
		
		final RepositoryConfiguration repositoryConfig = env.service(SnowOwlConfiguration.class)
				.getModuleConfig(RepositoryConfiguration.class);
		builder.put(IndexClientFactory.INDEX_PREFIX, repositoryConfig.getDeploymentId());
		
		final IndexConfiguration indexConfig = repositoryConfig.getIndexConfiguration();
		if (indexConfig.getClusterUrl() != null) {
			builder.put(IndexClientFactory.CLUSTER_URL, indexConfig.getClusterUrl());
		}
		
		builder.put(IndexClientFactory.TRANSLOG_SYNC_INTERVAL_KEY, indexConfig.getCommitInterval());
		builder.put(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL, indexConfig.getCommitConcurrencyLevel());
		builder.put(IndexClientFactory.CONNECT_TIMEOUT, indexConfig.getConnectTimeout());
		builder.put(IndexClientFactory.SOCKET_TIMEOUT, indexConfig.getSocketTimeout());
		
		final SlowLogConfig slowLog = createSlowLogConfig(indexConfig);
		builder.put(IndexClientFactory.SLOW_LOG_KEY, slowLog);
		
		return builder.build();
	}

	private SlowLogConfig createSlowLogConfig(final IndexConfiguration config) {
		final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder();
		builder.put(SlowLogConfig.FETCH_DEBUG_THRESHOLD, config.getFetchDebugThreshold());
		builder.put(SlowLogConfig.FETCH_INFO_THRESHOLD, config.getFetchInfoThreshold());
		builder.put(SlowLogConfig.FETCH_TRACE_THRESHOLD, config.getFetchTraceThreshold());
		builder.put(SlowLogConfig.FETCH_WARN_THRESHOLD, config.getFetchWarnThreshold());
		builder.put(SlowLogConfig.QUERY_DEBUG_THRESHOLD, config.getQueryDebugThreshold());
		builder.put(SlowLogConfig.QUERY_INFO_THRESHOLD, config.getQueryInfoThreshold());
		builder.put(SlowLogConfig.QUERY_TRACE_THRESHOLD, config.getQueryTraceThreshold());
		builder.put(SlowLogConfig.QUERY_WARN_THRESHOLD, config.getQueryWarnThreshold());
		
		return new SlowLogConfig(builder.build());
	}
}
