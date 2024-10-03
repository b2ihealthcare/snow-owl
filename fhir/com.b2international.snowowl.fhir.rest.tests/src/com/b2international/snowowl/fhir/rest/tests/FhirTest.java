/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest.tests;

import org.hl7.fhir.r5.formats.JsonParser;
import org.hl7.fhir.r5.model.Resource;

/**
 * Superclass for common test functionality
 * @since 6.3
 */
public class FhirTest {
	
	protected final String toJson(Resource resource) throws Exception {
		return new JsonParser().composeString(resource);
	}
	
	protected final <T> T fromJson(String resource) throws Exception {
		return (T) new JsonParser().parse(resource);
	}

}
