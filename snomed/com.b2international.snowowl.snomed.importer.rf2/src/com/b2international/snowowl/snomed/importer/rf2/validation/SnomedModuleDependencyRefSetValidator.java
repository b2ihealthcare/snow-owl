/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * Represents a release file validator that validates the module dependency reference set.
 * 
 * @since Snow Owl 2.9
 */
public class SnomedModuleDependencyRefSetValidator extends SnomedRefSetValidator {
	
	public SnomedModuleDependencyRefSetValidator(ImportConfiguration configuration, URL releaseUrl, SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.MODULE_DEPENDENCY_REFSET, context, SnomedRf2Headers.MODULE_DEPENDENCY_HEADER);
	}

	@Override
	protected String getName() {
		return "module dependency";
	}

}