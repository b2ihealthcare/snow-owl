/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
 * @since 8.9
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
