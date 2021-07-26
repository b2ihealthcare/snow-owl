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
package com.b2international.snowowl.fhir.tests.dt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.exceptions.ValidationException;
import com.b2international.snowowl.fhir.core.model.Issue;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.SimpleQuantity;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.tests.FhirExceptionIssueMatcher;
import com.b2international.snowowl.fhir.tests.FhirTest;

import io.restassured.path.json.JsonPath;

/**
 * Tests for {@link SimpleQuantity}
 * @since 8.0.0
 */
public class SimpleQuantityTest extends FhirTest {
	
	private SimpleQuantity quantity;
	
	@Before
	public void setup() throws Exception {
		
		quantity = SimpleQuantity.builder()
				.value(12.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code")
				.build();
	}
	
	@Test
	public void build() throws Exception {
		
		assertEquals(Double.valueOf(12.3), quantity.getValue());
		assertEquals("mg", quantity.getUnit());
		assertEquals(new Uri("uri:LOINC"), quantity.getSystem());
		assertEquals(new Code("code"), quantity.getCode());
		
	}
	
	@Test
	public void invalidSimpleQuantity() throws Exception {
		
		Issue expectedIssue = validationErrorIssueBuilder.addLocation("SimpleQuantity.comparator")
				.detailsWithDisplay(OperationOutcomeCode.MSG_PARAM_INVALID, "Parameter 'comparator' content is invalid [Code [codeValue=>=]]. Violation: must be null.")
				.build();
		
		exception.expect(ValidationException.class);
		exception.expectMessage("1 validation error");
		exception.expect(FhirExceptionIssueMatcher.issue(expectedIssue));
		
		SimpleQuantity.builder()
			.value(12.3)
			.unit("mg")
			.system("uri:LOINC")
			.code("code")
			.comparator(QuantityComparator.GREATER_OR_EQUAL_TO)
			.build();
	}
	
	@Test
	public void serialize() throws Exception {
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(quantity));
		assertThat(jsonPath.getDouble("value"), equalTo(12.3));
		assertThat(jsonPath.getString("unit"), equalTo("mg"));
		assertThat(jsonPath.getString("system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("code"), equalTo("code"));
	}
	
	@Test
	public void deserialize() throws Exception {
		
		SimpleQuantity readQuantity = objectMapper.readValue(objectMapper.writeValueAsString(quantity), SimpleQuantity.class);
		assertEquals(Double.valueOf(12.3), readQuantity.getValue());
		assertEquals("mg", readQuantity.getUnit());
		assertEquals(new Uri("uri:LOINC"), readQuantity.getSystem());
		assertEquals(new Code("code"), readQuantity.getCode());
		
	}

}
