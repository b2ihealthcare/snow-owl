/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.12.0
 */
public class ComponentURITest {
	
	@Test(expected = IllegalArgumentException.class)
	public void missingParts() {
		ComponentURI.of("codesystem/LCS1/1542");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void malformedCodeSystemURIPart() {
		ComponentURI.of("/750/1542");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nonNumberTerminologyComponentIdPart() {
		ComponentURI.of("codesystem/LCS1/xyz/1542");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void malformedIdentifierPart() {
		ComponentURI.of("codesystem/LCS1/750/");
	}
	
	@Test
	public void unspecified() throws Exception {
		assertTrue(ComponentURI.of("").isUnspecified());
	}
	
	@Test
	public void create() {
		final ResourceURI codeSystemURI = CodeSystem.uri("SNOMEDCT/2019-09-30");
		final String componentType = "concept";
		final String identifier = "123456789";
		ComponentURI componentURI = ComponentURI.of(codeSystemURI, componentType, identifier);
		assertEquals("SNOMEDCT", componentURI.resourceId());
		assertEquals("codesystems/SNOMEDCT/2019-09-30", componentURI.resourceUri().toString());
		assertEquals(150, componentURI.componentType());
		assertEquals(identifier, componentURI.identifier());
		assertEquals(ComponentIdentifier.of(componentType, identifier), componentURI.toComponentIdentifier());
		
		// verify interner
		assertTrue(componentURI == ComponentURI.of(codeSystemURI, ComponentIdentifier.of(componentType, identifier)));
		assertTrue(componentURI == ComponentURI.of(codeSystemURI.toString(), componentType, identifier));
	}
	
	@Test
	public void toStringTest() {
		String uri = "codesystem/SNOMEDCT/2019-09-30/150/59524001";
		ComponentURI componentURI = ComponentURI.of(uri);
		assertEquals("SNOMEDCT", componentURI.resourceId());
		assertEquals("codesystem/SNOMEDCT/2019-09-30", componentURI.resourceUri().toString());
		assertEquals(150, componentURI.componentType());
		assertEquals("59524001", componentURI.identifier());
		assertEquals(uri, componentURI.toString());
	}
	
	@Test
	public void serialization() throws Exception {
		final String uri = "codesystem/CODESYSTEM/100/1";
		assertEquals("\""+uri+"\"", new ObjectMapper().writeValueAsString(ComponentURI.of(uri)));
	}
	
	@Test
	public void deserialization() throws Exception {
		assertEquals(ComponentURI.of("codesystem/CODESYSTEM/100/1"), new ObjectMapper().readValue("\"codesystem/CODESYSTEM/100/1\"", ComponentURI.class));
	}
	
	@Test
	public void isValid() throws Exception {
		assertTrue(ComponentURI.isValid("codesystem/SNOMEDCT/100/1"));
		assertFalse(ComponentURI.isValid("codesystem/SNOMEDCT/100/"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void numberOfPartsTest() {
		final String incompleteURI = "codesystem/LCS1/1542";
		ComponentURI.of(incompleteURI); //Attempt to parse incomplete component URI
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void missingCodeSystemTest() {
		final String malformedURI = "/750/1542";
		ComponentURI.of(malformedURI);
	}
	
	@Test(expected = NumberFormatException.class)
	public void incorrectTerminologyComponentIdTest() {
		final String uri = "codesystem/LCS1/xyz/1542";
		ComponentURI.of(uri);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void incorrectIdTest() {
		final String uri = "codesystem/LCS1/750/";
		ComponentURI.of(uri);
	}
	
}
