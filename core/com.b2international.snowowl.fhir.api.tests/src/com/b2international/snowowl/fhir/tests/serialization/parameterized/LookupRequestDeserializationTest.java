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
package com.b2international.snowowl.fhir.tests.serialization.parameterized;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Parameter;
import com.b2international.snowowl.fhir.core.model.dt.Parameters;
import com.b2international.snowowl.fhir.core.model.dt.Parameters.Fhir;

/**
 * Lookup request deserialization test
 * @since 6.6
 */
public class LookupRequestDeserializationTest {

	@Test
	public void testDeserialization() {

		Coding coding = Coding.builder()
				.system("http://hl7.org/fhir/issue-severity")
				.code("fatal")
				.build();

		LookupRequest request = LookupRequest.builder().coding(coding).build();

		Fhir fhirParameters = new Parameters.Fhir(request);
		fhirParameters.getParameters().forEach(p -> System.out.println(p));
		Optional<Parameter> findFirst = fhirParameters.getParameters().stream()
				.filter(p -> {
					Coding pCoding = (Coding) p.getValue();
					return pCoding.getSystemValue().equals("http://hl7.org/fhir/issue-severity");
				})
				.findFirst();

		assertTrue(findFirst.isPresent());
	}

}
