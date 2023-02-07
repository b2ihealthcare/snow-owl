/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.domain;

import static org.junit.Assert.assertEquals;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9
 */
public class ReindexQuerySerializationTest {

	private static ReindexQuery query;
	private static ObjectMapper mapper;
	private static String serializedResponse;

	@BeforeClass
	public static void before() {

		mapper = JsonSupport.getDefaultObjectMapper();

		query = ReindexQuery.of("index", QueryBuilders.boolQuery());

		serializedResponse = "{\"index\":\"index\",\"query\":\"{\\\"query\\\":{\\\"bool\\\":{\\\"adjust_pure_negative\\\":true,\\\"boost\\\":1.0}}}\"}";

	}

	@Test
	public void testSerialization() throws Exception {
		final String result = mapper.writeValueAsString(query);
		assertEquals(serializedResponse, result);
	}

	@Test
	public void testDeserialization() throws Exception {
		final ReindexQuery newQuery = mapper.readValue(serializedResponse, ReindexQuery.class);
		assertEquals(query.getIndex(), newQuery.getIndex());
		assertEquals(query.getQuery(), newQuery.getQuery());
	}

}
