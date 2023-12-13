/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.index.es.reindex.ReindexResult;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9.0
 */
public class ReindexResultSerializationTest {

	private static ReindexResult reindexResult;
	private static ReindexResult simpleReindexResult;

	private static ObjectMapper mapper;
	private static String serializedResponse;
	private static String simpleSerializedResponse;

	@BeforeClass
	public static void before() {

		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		reindexResult = ReindexResult.builder()
				.took("123 ns")
				.createdDocuments(1L)
				.updatedDocuments(1L)
				.deletedDocuments(1L)
				.totalDocuments(1L)
				.noops(1L)
				.versionConflicts(2L)
				.sourceIndex("sourceIndex")
				.destinationIndex("destinationIndex")
				.remoteAddress("remoteAddress")
				.refresh(true)
				.retries(Long.valueOf(2L))
				.build();

		simpleReindexResult = ReindexResult.builder()
				.took("123 ns")
				.totalDocuments(1L)
				.sourceIndex("sourceIndex")
				.destinationIndex("destinationIndex")
				.remoteAddress("remoteAddress")
				.refresh(false)
				.build();


		serializedResponse = "{\"took\":\"123 ns\",\"createdDocuments\":1,\"updatedDocuments\":1,\"deletedDocuments\":1,\"noops\":1,\"versionConflicts\":2,\"totalDocuments\":1,\"sourceIndex\":\"sourceIndex\",\"destinationIndex\":\"destinationIndex\",\"remoteAddress\":\"remoteAddress\",\"refresh\":true,\"retries\":2}";
		simpleSerializedResponse = "{\"took\":\"123 ns\",\"totalDocuments\":1,\"sourceIndex\":\"sourceIndex\",\"destinationIndex\":\"destinationIndex\",\"remoteAddress\":\"remoteAddress\",\"refresh\":false}";

	}

	@Test
	public void testSerialization() throws Exception {
		final String result = mapper.writeValueAsString(reindexResult);
		assertEquals(serializedResponse, result);
	}

	@Test
	public void testSimpleSerialization() throws Exception {
		final String result = mapper.writeValueAsString(simpleReindexResult);
		assertEquals(simpleSerializedResponse, result);
	}

	@Test
	public void testDeserialization() throws Exception {
		final ReindexResult newResult = mapper.readValue(serializedResponse, ReindexResult.class);
		assertEquals(reindexResult, newResult);
	}

	@Test
	public void testSimpleDeserialization() throws Exception {
		final ReindexResult newResult = mapper.readValue(simpleSerializedResponse, ReindexResult.class);
		assertEquals(simpleReindexResult, newResult);
	}

}
