/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.uri;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * @since 7.12.0
 */
public class ComponentURITest {

	@Test
	public void constructorTest() {
		final String branchPath = "SNOMEDCT/2019-09-30/SNOMEDCT-SE/2020-07-30/SNOMEDCT-EXT";
		final CodeSystemURI uri = new CodeSystemURI(branchPath);
		short terminologyComponentId = 150;
		final String componentId = "123456789";
		ComponentURI componentURI = ComponentURI.of(uri, terminologyComponentId, componentId);
		assertEquals(componentURI.codeSystem(), ComponentURI.SLASH_SPLITTER.split(branchPath).iterator().next());
		assertEquals(componentURI.terminologyComponentId(), terminologyComponentId);
		assertEquals(componentURI.identifier(), componentId);
	}
	
	@Test
	public void serializationTest() {
		final String uri = "LCS1/1542/750/SO";
		ComponentURI componentURI = ComponentURI.of(uri);
		assertEquals(componentURI.codeSystem(), "LCS1");
		assertEquals(componentURI.terminologyComponentId(), 750);
		assertEquals(componentURI.identifier(), "SO");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void numberOfPartsTest() {
		final String incompleteURI = "LCS1/1542";
		ComponentURI.of(incompleteURI); //Attempt to parse incomplete component URI
	}
	
	@Test(expected = BadRequestException.class)
	public void missingCodeSystemTest() {
		final String malformedURI = "/750/1542";
		ComponentURI.of(malformedURI);
	}
	
	@Test(expected = NumberFormatException.class)
	public void incorrectTerminologyComponentIdTest() {
		final String uri = "LCS1/xyz/1542";
		ComponentURI.of(uri);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void incorrectIdTest() {
		final String uri = "LCS1/750/";
		ComponentURI.of(uri);
	}
	
}
