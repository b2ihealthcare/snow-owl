/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.validation;

import java.net.URL;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;

/**
 * Represents a release file validator that validates the simple map type
 * reference set with included map target description.
 * 
 * @since 6.5
 */
public class SnomedSimpleMapWithDescriptionRefSetValidator extends SnomedSimpleMapTypeRefSetValidator {
	
	public SnomedSimpleMapWithDescriptionRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.SIMPLE_MAP_TYPE_REFSET_WITH_DESCRIPTION, context, SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION);
	}
	
	@Override
	protected String getName() {
		return "simple map type with map target description";
	}
}
