/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.index;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.index.es.reindex.ReindexResult;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 8.9
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
				.build();

		simpleReindexResult = ReindexResult.builder()
				.took("123 ns")
				.totalDocuments(1L)
				.sourceIndex("sourceIndex")
				.destinationIndex("destinationIndex")
				.remoteAddress("remoteAddress")
				.refresh(false)
				.build();


		serializedResponse = "{\"took\":\"123 ns\",\"createdDocuments\":1,\"updatedDocuments\":1,\"deletedDocuments\":1,\"noops\":1,\"versionConflicts\":2,\"totalDocuments\":1,\"sourceIndex\":\"sourceIndex\",\"destinationIndex\":\"destinationIndex\",\"remoteAddress\":\"remoteAddress\",\"refresh\":true}";
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
