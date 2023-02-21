/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.info;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.index.es.client.EsIndexStatus;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9.0
 */
public class RepositoryInfoSerializationTest {

	private static RepositoryInfo info;
	private static ObjectMapper mapper;
	private static String serializedResponse;

	@BeforeClass
	public static void before() {

		mapper = JsonSupport.getDefaultObjectMapper();

		info = RepositoryInfo.of(
			"repoid",
			RepositoryInfo.Health.GREEN,
			"diagnosis",
			List.of(new EsIndexStatus("index", ClusterHealthStatus.GREEN, "diagnosis"))
		);

		serializedResponse = "{\"id\":\"repoid\",\"health\":\"GREEN\",\"diagnosis\":\"diagnosis\",\"indices\":[{\"index\":\"index\",\"status\":\"GREEN\",\"diagnosis\":\"diagnosis\"}]}";

	}

	@Test
	public void testSerialization() throws Exception {
		final String result = mapper.writeValueAsString(info);
		assertEquals(serializedResponse, result);
	}

	@Test
	public void testDeserialization() throws Exception {
		final RepositoryInfo newInfo = mapper.readValue(serializedResponse, RepositoryInfo.class);
		assertEquals(info, newInfo);
	}

}
