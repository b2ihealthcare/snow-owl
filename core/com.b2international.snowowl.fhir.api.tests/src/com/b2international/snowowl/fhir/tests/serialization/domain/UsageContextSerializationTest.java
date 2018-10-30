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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
import com.jayway.restassured.path.json.JsonPath;

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
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("code.display"), equalTo("codingDisplay"));
		assertThat(jsonPath.getString("valueCodeableConcept.text"), equalTo("codingText"));
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].display"), equalTo("codingDisplay"));
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
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("code.display"), equalTo("codingDisplay"));
		assertThat(jsonPath.getDouble("valueQuantity.value"), equalTo(12.3));
		assertThat(jsonPath.getString("valueQuantity.comparator"), equalTo(">="));
		assertThat(jsonPath.getString("valueQuantity.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("valueQuantity.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("valueQuantity.code"), equalTo("code"));
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
				.display("codingDisplay")
				.build())
			.value(range)
			.build();
		
		printPrettyJson(usageContext);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code"), equalTo("codingCode"));
		assertThat(jsonPath.getString("code.display"), equalTo("codingDisplay"));
		
		jsonPath.setRoot("valueRange");
		
		assertThat(jsonPath.getDouble("low.value"), equalTo(12.3));
		assertThat(jsonPath.getString("low.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("low.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("low.code"), equalTo("code1"));
		
		assertThat(jsonPath.getDouble("high.value"), equalTo(120.3));
		assertThat(jsonPath.getString("high.unit"), equalTo("mg"));
		assertThat(jsonPath.getString("high.system"), equalTo("uri:LOINC"));
		assertThat(jsonPath.getString("high.code"), equalTo("code1"));
		
	}

}
