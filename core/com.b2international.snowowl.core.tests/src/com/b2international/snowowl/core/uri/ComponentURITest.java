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

import static org.junit.Assert.*;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.12.0
 */
public class ComponentURITest {
	
	@Test(expected = IllegalArgumentException.class)
	public void missingParts() {
		ComponentURI.of("LCS1/1542");
	}
	
	@Test(expected = BadRequestException.class)
	public void malformedCodeSystemURIPart() {
		ComponentURI.of("/750/1542");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nonNumberTerminologyComponentIdPart() {
		ComponentURI.of("LCS1/xyz/1542");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void malformedIdentifierPart() {
		ComponentURI.of("LCS1/750/");
	}
	
	@Test
	public void unspecified() throws Exception {
		assertTrue(ComponentURI.of("").isUnspecified());
	}
	
	@Test
	public void create() {
		final CodeSystemURI codeSystemURI = new CodeSystemURI("SNOMEDCT/2019-09-30");
		final short terminologyComponentId = 150;
		final String identifier = "123456789";
		ComponentURI componentURI = ComponentURI.of(codeSystemURI, terminologyComponentId, identifier);
		assertEquals("SNOMEDCT", componentURI.codeSystem());
		assertEquals("SNOMEDCT/2019-09-30", componentURI.codeSystemUri().toString());
		assertEquals(150, componentURI.terminologyComponentId());
		assertEquals(identifier, componentURI.identifier());
		assertEquals(ComponentIdentifier.of(terminologyComponentId, identifier), componentURI.toComponentIdentifier());
		
		// verify interner
		assertTrue(componentURI == ComponentURI.of(codeSystemURI, ComponentIdentifier.of(terminologyComponentId, identifier)));
		assertTrue(componentURI == ComponentURI.of(codeSystemURI.toString(), terminologyComponentId, identifier));
	}
	
	@Test
	public void toStringTest() {
		String uri = "SNOMEDCT/2019-09-30/150/59524001";
		ComponentURI componentURI = ComponentURI.of(uri);
		assertEquals("SNOMEDCT", componentURI.codeSystem());
		assertEquals("SNOMEDCT/2019-09-30", componentURI.codeSystemUri().toString());
		assertEquals(150, componentURI.terminologyComponentId());
		assertEquals("59524001", componentURI.identifier());
		assertEquals(uri, componentURI.toString());
	}
	
	@Test
	public void serialization() throws Exception {
		final String uri = "CODESYSTEM/100/1";
		assertEquals("\""+uri+"\"", new ObjectMapper().writeValueAsString(ComponentURI.of(uri)));
	}
	
	@Test
	public void deserialization() throws Exception {
		assertEquals(ComponentURI.of("CODESYSTEM/100/1"), new ObjectMapper().readValue("\"CODESYSTEM/100/1\"", ComponentURI.class));
	}
	
	@Test
	public void isValid() throws Exception {
		assertTrue(ComponentURI.isValid("SNOMEDCT/100/1"));
		assertFalse(ComponentURI.isValid("SNOMEDCT/100/"));
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
