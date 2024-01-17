/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

/**
 * @since 8.5
 */
public class ShardConfigurationTest extends BaseIndexTest {

	@Doc
	private static final class ShardConfig {
		
		@ID
		private String id;
		
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(ShardConfig.class);
	}
	
	@Override
	protected Map<String, Object> getIndexSettings() {
		return Map.of("shardconfig", Map.of(
			IndexClientFactory.NUMBER_OF_SHARDS, "2"
		));
	}
	
	@Test
	public void shardReplicaConfigTest() throws Exception {
		String typeIndex = index().admin().getIndexMapping().getTypeIndex(ShardConfig.class);
		Settings settings = index().admin().client().indices().settings(new GetSettingsRequest().indices(typeIndex)).getIndexToSettings().get(typeIndex).getAsSettings("index");
		assertThat(settings.get(IndexClientFactory.NUMBER_OF_SHARDS)).isEqualTo("2");
	}

}
