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
package com.b2international.snowowl.snomed.importer.rf2.util;

import java.net.URL;
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedRefSetValidator;
import com.google.common.collect.Sets;

/**
 * RF2 file validator for SNOMED&nbsp;CT extended maps.
 *
 */
public class SnomedExtendedMapTypeRefSetValidator extends SnomedRefSetValidator {

	private Set<String> mapCategoryConceptNotExist;
	private Set<String> correlationConceptNotExist;
	
	public SnomedExtendedMapTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) {
		super(configuration, releaseUrl, ComponentImportType.EXTENDED_MAP_TYPE_REFSET, defects, validationUtil, SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER.length);
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		super.doValidate(row, lineNumber);
		validateCorrelationConcept(row, lineNumber);
		validateMapCategory(row, lineNumber);
	}

	@Override
	protected String getName() {
		return "extended map type";
	}
	
	@Override
	protected String[] getExpectedHeader() {
		return SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER;
	}
	
	private void validateMapCategory(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(12), ReleaseComponentType.CONCEPT)) {
			if (null == mapCategoryConceptNotExist) {
				mapCategoryConceptNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(mapCategoryConceptNotExist, lineNumber, row.get(12));
		}
	}
	
	private void validateCorrelationConcept(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(11), ReleaseComponentType.CONCEPT)) {
			if (null == correlationConceptNotExist) {
				correlationConceptNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(correlationConceptNotExist, lineNumber, row.get(11));
		}
	}

	
}