/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.fhir.SnomedUri;
import com.b2international.snowowl.snomed.fhir.SnomedUri.QueryPartDefinition;

/**
 * 
 * @since 6.7
 */
public class SnomedUriParsingTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testInvalidBase() {
		
		String uriString = "invalid uri";
		
		exception.expect(BadRequestException.class);
		exception.expectMessage("URI 'invalid uri' is not a valid SNOMED CT URI. It should start as 'http://snomed.info/sct'");

		SnomedUri.fromUriString(uriString, "uri");
	}
	
	@Test
	public void testInvalidModule() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/a";
		
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid extension module ID [a] defined.");

		SnomedUri.fromUriString(uriString, "uri");
	}
	
	@Test
	public void testInvalidVersionTag() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/900000000000207008/invalidTag";
		
		exception.expect(FhirException.class);
		exception.expectMessage("Invalid path segment [invalidTag], 'version' expected.");

		SnomedUri.fromUriString(uriString, "uri");
	}
	
	@Test
	public void testMissingVersionTag() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/900000000000207008/version";
		
		exception.expect(FhirException.class);
		exception.expectMessage("No version tag is specified after the 'version' parameter.");

		SnomedUri.fromUriString(uriString, "uri");
	}

	//correct paths
	@Test
	public void testIntEditionUri() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING;
		
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertNull(snomedUri.getExtensionModuleId());
		assertNull(snomedUri.getVersionTag());
		
	}
	
	@Test
	public void testIntEditionWithModuleUri() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/" + Concepts.MODULE_SCT_CORE;
		
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(Concepts.MODULE_SCT_CORE, snomedUri.getExtensionModuleId());
		assertNull(snomedUri.getVersionTag());
	}
	
	@Test
	public void testIntEditionWithVersionUri() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/" + Concepts.MODULE_SCT_CORE + "/version/20170131";
		
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(Concepts.MODULE_SCT_CORE, snomedUri.getExtensionModuleId());
		assertEquals("20170131", snomedUri.getVersionTag());
	}
	
	@Test
	public void testUriWithQuery() {
		
		String uriString = "http://snomed.info/sct?fhir_vs";
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(null, snomedUri.getExtensionModuleId());
		assertEquals(null, snomedUri.getVersionTag());
		assertEquals("fhir_vs", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.NONE, snomedUri.getQueryPart().getQueryPartDefinition());
		assertEquals(null, snomedUri.getQueryPart().getQueryValue());
		
		uriString = "http://snomed.info/sct?fhir_vs=isa/50697003";
		snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(null, snomedUri.getExtensionModuleId());
		assertEquals(null, snomedUri.getVersionTag());
		assertEquals("fhir_vs", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.ISA, snomedUri.getQueryPart().getQueryPartDefinition());
		assertEquals("50697003", snomedUri.getQueryPart().getQueryValue());
		
		uriString = "http://snomed.info/sct?fhir_vs=refset";
		snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(null, snomedUri.getExtensionModuleId());
		assertEquals(null, snomedUri.getVersionTag());
		assertEquals("fhir_vs", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.REFSETS, snomedUri.getQueryPart().getQueryPartDefinition());
		
		uriString = "http://snomed.info/sct?fhir_vs=refset/50697003";
		snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(null, snomedUri.getExtensionModuleId());
		assertEquals(null, snomedUri.getVersionTag());
		assertEquals("fhir_vs", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.REFSET, snomedUri.getQueryPart().getQueryPartDefinition());
		assertEquals("50697003", snomedUri.getQueryPart().getQueryValue());
		
		uriString = "http://snomed.info/sct?fhir_cm=50697003";
		snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(null, snomedUri.getExtensionModuleId());
		assertEquals(null, snomedUri.getVersionTag());
		assertEquals("fhir_cm", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.NONE, snomedUri.getQueryPart().getQueryPartDefinition());
		assertEquals("50697003", snomedUri.getQueryPart().getQueryValue());
		
	}
	
	@Test
	public void testComplexUriWithQueryPart() {
		
		String uriString = SnomedUri.SNOMED_BASE_URI_STRING + "/" + Concepts.MODULE_SCT_CORE + "/version/20170131?fhir_vs=isa/50697003";
		
		SnomedUri snomedUri = SnomedUri.fromUriString(uriString, "uri");
		
		assertEquals(Concepts.MODULE_SCT_CORE, snomedUri.getExtensionModuleId());
		assertEquals("20170131", snomedUri.getVersionTag());
		assertEquals("fhir_vs", snomedUri.getQueryPart().getQueryParameter());
		assertEquals(QueryPartDefinition.ISA, snomedUri.getQueryPart().getQueryPartDefinition());
		assertEquals("50697003", snomedUri.getQueryPart().getQueryValue());
	}
	
	@Test
	public void testToUri() {
		
		SnomedUri baseUri = SnomedUri.builder().build();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING, baseUri.toString());
		
		SnomedUri uri = SnomedUri.builder().version("20180131").build();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "/version/20180131", uri.toString());
		
		uri = SnomedUri.builder()
				.extensionModuleId(Concepts.MODULE_SCT_CORE)
				.version("20180131").build();
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "/" + Concepts.MODULE_SCT_CORE + "/version/20180131", uri.toString());
	}
	
	@Test
	public void testToUriWithQueryPart() {
		
		SnomedUri uri = SnomedUri.builder().conceptMapQuery(Concepts.ROOT_CONCEPT).build();
		
		System.out.println("URI: " + uri);
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "?fhir_cm=" + Concepts.ROOT_CONCEPT, uri.toString());
		
		uri = SnomedUri.builder().valueSetsQuery().build();
		
		System.out.println("URI: " + uri);
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "?fhir_vs", uri.toString());
		
		uri = SnomedUri.builder().isAQuery(Concepts.ROOT_CONCEPT).build();
		
		System.out.println("URI: " + uri);
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "?fhir_vs=isa/" + Concepts.ROOT_CONCEPT, uri.toString());
		
		uri = SnomedUri.builder().refsetsQuery().build();
		
		System.out.println("URI: " + uri);
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "?fhir_vs=refset", uri.toString());
		
		uri = SnomedUri.builder().refsetQuery(Concepts.ROOT_CONCEPT).build();
		
		System.out.println("URI: " + uri);
		assertEquals(SnomedUri.SNOMED_BASE_URI_STRING + "?fhir_vs=refset/" + Concepts.ROOT_CONCEPT, uri.toString());
		
	}
	
}
