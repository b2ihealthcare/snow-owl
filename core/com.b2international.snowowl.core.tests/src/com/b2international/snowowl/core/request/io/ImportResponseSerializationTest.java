/*******************************************************************************
 * Copyright (c) 2020-2021 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.request.io;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.13.0
 */
public class ImportResponseSerializationTest {

	private static ImportResponse response;
	private static ObjectMapper mapper;
	private static String serializedResponse;

	@BeforeClass
	public static void before() {
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		ComponentURI componentURI = ComponentURI.of("codesystem/SNOMEDCT/100/138875005");
		ImportDefect defect = ImportDefect.info("file", "location", "message");
		response = ImportResponse.success(Set.of(componentURI), List.of(defect));
		
		serializedResponse = "{\"visitedComponents\":[\"codesystem/SNOMEDCT/100/138875005\"],\"defects\":[{\"file\":\"file\",\"location\":\"location\",\"message\":\"message\",\"type\":\"INFO\"}],\"success\":true}";
		
	}
	
	@Test
	public void testSerialization() throws Exception {
		String result = mapper.writeValueAsString(response);
		assertEquals(serializedResponse, result);
	}
	
	@Test
	public void testDeserialization() throws Exception {
		ImportResponse newResponse = mapper.readValue(serializedResponse, ImportResponse.class);
		assertEquals(response, newResponse);
	}
	
}
