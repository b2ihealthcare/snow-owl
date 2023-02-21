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
package com.b2international.snowowl.core.domain;

import static org.junit.Assert.assertEquals;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9.0
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
