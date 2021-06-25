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
package com.b2international.snowowl.snomed.core.uri;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.uri.ResourceURLSchemaSupport;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * TODO move SnomedUri from FHIR implementation and use that to provide full SNOMED CT URI support
 * 
 * @since 8.0
 */
public class SnomedURLSchemaSupport implements ResourceURLSchemaSupport {

	@Override
	public void validate(String uri) throws BadRequestException {
		// very basic validation to ensure that all tests use proper URLs
		if (!uri.startsWith(SnomedTerminologyComponentConstants.SNOMED_URI_BASE)) {
			throw new BadRequestException("SNOMED CT URIs must start with '%s'. Got '%s'", SnomedTerminologyComponentConstants.SNOMED_URI_BASE, uri);
		}
	}
	
	@Override
	public String withVersion(String uri, String version, LocalDate effectiveTime) {
		return String.join("/version/", uri, effectiveTime.format(DateTimeFormatter.BASIC_ISO_DATE));
	}
	
}
