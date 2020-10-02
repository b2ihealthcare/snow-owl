/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

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

import io.restassured.path.json.JsonPath;

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
		
//		printPrettyJson(usageContext);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("code.display")).isEqualTo("codingDisplay");
		assertThat(jsonPath.getString("valueCodeableConcept.text")).isEqualTo("codingText");
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("valueCodeableConcept.coding[0].display")).isEqualTo("codingDisplay");
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
		
//		printPrettyJson(usageContext);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("code.display")).isEqualTo("codingDisplay");
		assertThat(jsonPath.getDouble("valueQuantity.value")).isEqualTo(12.3);
		assertThat(jsonPath.getString("valueQuantity.comparator")).isEqualTo(">=");
		assertThat(jsonPath.getString("valueQuantity.unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("valueQuantity.system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("valueQuantity.code")).isEqualTo("code");
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
		
//		printPrettyJson(usageContext);
		
		JsonPath jsonPath = JsonPath.from(objectMapper.writeValueAsString(usageContext));
		
		assertThat(jsonPath.getString("code.code")).isEqualTo("codingCode");
		assertThat(jsonPath.getString("code.display")).isEqualTo("codingDisplay");
		
		jsonPath.setRoot("valueRange");
		
		assertThat(jsonPath.getDouble("low.value")).isEqualTo(12.3);
		assertThat(jsonPath.getString("low.unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("low.system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("low.code")).isEqualTo("code1");
		
		assertThat(jsonPath.getDouble("high.value")).isEqualTo(120.3);
		assertThat(jsonPath.getString("high.unit")).isEqualTo("mg");
		assertThat(jsonPath.getString("high.system")).isEqualTo("uri:LOINC");
		assertThat(jsonPath.getString("high.code")).isEqualTo("code1");
		
	}

}
