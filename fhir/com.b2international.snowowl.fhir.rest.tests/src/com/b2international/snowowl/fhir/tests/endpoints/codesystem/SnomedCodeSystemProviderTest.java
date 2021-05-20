/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.tests.endpoints.codesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.fhir.SnomedCodeSystemApiProvider;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.ImmutableList;

/**
 * 
 * Test class for {@link SnomedCodeSystemApiProvider}
 * @since 7.17.0
 */
public class SnomedCodeSystemProviderTest {
	
	public static final String NHS_REALM_LANGUAGE_REFSET_ID = "999000671000001103";
	public static final ExtendedLocale NHS_REALM_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_LANGUAGE_REFSET_ID);

	public static final String NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID = "999001261000000100";
	public static final ExtendedLocale NHS_REALM_CLINICAL_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_CLINICAL_LANGUAGE_REFSET_ID);
	
	public static final String NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID = "999000691000001104";
	public static final ExtendedLocale NHS_REALM_PHARMACY_LOCALE = ExtendedLocale.valueOf("en-x-" +  NHS_REALM_PHARMACY_LANGUAGE_REFSET_ID);
	public static final ExtendedLocale INT_LOCALE = ExtendedLocale.valueOf("en-us");
	
	protected List<ExtendedLocale> locales = ImmutableList.of(INT_LOCALE, NHS_REALM_LOCALE, NHS_REALM_CLINICAL_LOCALE, NHS_REALM_PHARMACY_LOCALE);
	
	/*
	 * Extend the SNOMED CT provider to access protected methods
	 */
	final class TestProvider extends SnomedCodeSystemApiProvider {
		
		public TestProvider() {
			super(Services.bus(), locales);
		}
		
		//Change the method visibility for the test
		public ResourceURI getCodeSystemUri(String system, String version) {
			return super.getCodeSystemUri(system, version);
		}
		
	}

	@Test
	public void getCodeSystemUriTest() {
		
		TestProvider provider = new TestProvider();
		ResourceURI codeSystemUri = provider.getCodeSystemUri("http://snomed.info/sct", null);
		Assert.assertEquals(CodeSystem.uri("SNOMEDCT", "2020-02-04"), codeSystemUri);
		
		codeSystemUri = provider.getCodeSystemUri("http://snomed.info/sct/900000000000207008", null);
		Assert.assertEquals(CodeSystem.uri("SNOMEDCT", "2020-02-04"), codeSystemUri);
		
		codeSystemUri = provider.getCodeSystemUri("http://snomed.info/sct/900000000000207008/version/20180131", null);
		assertEquals(CodeSystem.uri("SNOMEDCT", "2018-01-31"), codeSystemUri);
	}
	
	@Test
	public void getCodeSystemUriWithInvalidModuleTest() {
		
		Exception exception = assertThrows(BadRequestException.class, () -> {
			TestProvider provider = new TestProvider();
			provider.getCodeSystemUri("http://snomed.info/sct/11111111", null);
	       
	    });

	    assertTrue(exception.getMessage().contains("Invalid SNOMED CT extension module ID"));
		
	}


}
