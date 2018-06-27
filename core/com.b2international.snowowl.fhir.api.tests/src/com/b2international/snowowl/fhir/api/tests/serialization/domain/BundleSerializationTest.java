/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.tests.serialization.domain;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.b2international.snowowl.fhir.api.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.api.tests.FhirTest;
import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemHierarchyMeaning;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;
import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.codesystems.IssueSeverity;
import com.b2international.snowowl.fhir.core.codesystems.IssueType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.codesystem.Filter;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.model.property.BooleanConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.CodingConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.DateTimeConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.IntegerConceptProperty;
import com.b2international.snowowl.fhir.core.model.property.StringConceptProperty;

/**
 * Test for checking the serialization from model->JSON.
 * @since 6.3
 */
public class BundleSerializationTest extends FhirTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void bundleTest() throws Exception {
		
		CodeSystem codeSystem = CodeSystem.builder("repo/shortName")
			.status(PublicationStatus.ACTIVE)
			.name("Local code system")
			.content(CodeSystemContentMode.COMPLETE)
			.url(new Uri("code system uri"))
			.build();
		
		Entry entry = new Entry(new Uri("full Url"), codeSystem);
		
		Bundle bundle = Bundle.builder("bundle_Id?")
			.language("en")
			.total(1)
			.type(BundleType.SEARCHSET)
			.addLink("self", "http://localhost:8080/snowowl/CodeSystem")
			.addEntry(entry)
			.build();
		
		applyFilter(bundle);
		
		printPrettyJson(bundle);
		
		String expectedJson = "{\"resourceType\":\"Bundle\","
				+ "\"id\":\"bundle_Id?\","
				+ "\"language\":\"en\","
				+ "\"type\":\"searchset\","
				+ "\"total\":1,"
				+ "\"link\":"
					+ "[{\"relation\":\"self\","
					+ "\"url\":\"http://localhost:8080/snowowl/CodeSystem\"}],"
					+ "\"entry\":[{\"fullUrl\":\"full Url\",\"resource\":"
						+ "{\"resourceType\":\"CodeSystem\","
						+ "\"id\":\"repo/shortName\","
						+ "\"url\":\"code system uri\","
						+ "\"name\":\"Local code system\","
						+ "\"status\":\"active\","
						+ "\"content\":\"complete\"}"
					+ "}]"
				+ "}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(bundle));
	}
	

}
