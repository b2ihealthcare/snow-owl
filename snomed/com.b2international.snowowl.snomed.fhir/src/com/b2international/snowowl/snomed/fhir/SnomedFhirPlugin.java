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
package com.b2international.snowowl.snomed.fhir;

import java.util.Map;

import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.TerminologyRepositoryConfigurer;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemLookupConverter;
import com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * @since 8.0
 */
@Component
public class SnomedFhirPlugin extends Plugin implements TerminologyRepositoryConfigurer {

	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TOOLING_ID;
	}
	
	@Override
	public Map<Class<?>, Object> bindAdditionalServices(Environment env) {
		return Map.of(
			FhirCodeSystemResourceConverter.class, new SnomedFhirCodeSystemResourceConverter(),
			FhirCodeSystemLookupConverter.class, new SnomedFhirCodeSystemLookupConverter()
		);
	}

}
