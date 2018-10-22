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
package com.b2international.snowowl.fhir.tests.serialization.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.dt.Range;
import com.b2international.snowowl.fhir.core.model.dt.SimpleQuantity;
import com.b2international.snowowl.fhir.core.model.usagecontext.CodeableConceptUsageContext;
import com.b2international.snowowl.fhir.core.model.usagecontext.QuantityUsageContext;
import com.b2international.snowowl.fhir.core.model.usagecontext.RangeUsageContext;
import com.b2international.snowowl.fhir.tests.FhirTest;

/**
 * Usage context serialization tests.
 * @since 6.6
 */
public class UsageContextSerializationTest extends FhirTest {
	
	@Test
	public void codeableConceptUsageContextTest() throws Exception {
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.build();
		
		CodeableConcept codeableConcept = CodeableConcept.builder()
				.addCoding(coding)
				.text("codingText")
				.build();
		
		CodeableConceptUsageContext usageContext = CodeableConceptUsageContext.builder()
			.code(coding)
			.value(codeableConcept)
			.build();
		
		printPrettyJson(usageContext);
		
		String expectedJson = "{\"code\":"
					+ "{\"code\":\"codingCode\","
					+ "\"display\":\"codingDisplay\"},"
				+ "\"valueCodeableConcept\":{\"text\":\"codingText\","
				+ "\"coding\":[{\"code\":\"codingCode\","
					+ "\"display\":\"codingDisplay\"}]"
				+ "}}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(usageContext));
	}
	
	@Test
	public void quantityUsageContextTest() throws Exception {
		
		Coding coding = Coding.builder()
				.code("codingCode")
				.display("codingDisplay")
				.build();
		
		Quantity quantity = Quantity.builder()
				.value(12.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code")
				.comparator(QuantityComparator.GREATER_OR_EQUAL_TO)
				.build(); 
		
		QuantityUsageContext usageContext = QuantityUsageContext.builder()
			.code(coding)
			.value(quantity)
			.build();
		
		printPrettyJson(usageContext);
		
		String expectedJson = "{\"code\":"
					+ "{\"code\":\"codingCode\","
					+ "\"display\":\"codingDisplay\"},"
				+ "\"valueQuantity\":"
					+ "{\"value\":12.3,"
					+ "\"comparator\":\">=\","
					+ "\"unit\":\"mg\","
					+ "\"system\":\"uri:LOINC\","
					+ "\"code\":\"code\"}"
					+ "}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(usageContext));
	}
	
	@Test
	public void rangeUsageContextTest() throws Exception {
		
		SimpleQuantity low = SimpleQuantity.builder()
				.value(12.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code1")
				.build();
			
		SimpleQuantity high = SimpleQuantity.builder()
				.value(120.3)
				.unit("mg")
				.system("uri:LOINC")
				.code("code1")
				.build();
				
			
		Range range = new Range(low, high);
		
		RangeUsageContext usageContext = RangeUsageContext.builder()
			.code(Coding.builder()
					.code("codingCode")
					.display("display")
					.build())
			.value(range)
			.build();
		
		printPrettyJson(usageContext);
		
		String expectedJson = "{\"code\":{\"code\":\"codingCode\",\"display\":\"display\"},"
				+ "\"valueRange\":{\"low\":{\"value\":12.3,\"unit\":\"mg\",\"system\":\"uri:LOINC\",\"code\":\"code1\"},"
				+ "\"high\":{\"value\":120.3,\"unit\":\"mg\",\"system\":\"uri:LOINC\",\"code\":\"code1\"}}}";
		
		assertEquals(expectedJson, objectMapper.writeValueAsString(usageContext));
	}
	

}
