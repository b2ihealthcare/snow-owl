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
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect.DefectType;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ValidationUtil;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the simple map type reference set.
 * 
 */
public class SnomedSimpleMapTypeRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> mapTargetIsEmpty;
	private boolean extended;

	public SnomedSimpleMapTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil, final boolean extended) {
		super(configuration, releaseUrl, ComponentImportType.SIMPLE_MAP_TYPE_REFSET, defects, 
				validationUtil, extended? SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION.length : SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER.length);
		this.extended = extended;
	}
	
	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		super.doValidate(row, lineNumber);
		
		validateMapTarget(row, lineNumber);
	}
	
	@Override
	protected void addDefects() {
		super.addDefects();
		
		addDefects(new SnomedValidationDefect(DefectType.SIMPLE_MAP_TARGET_IS_EMPTY, mapTargetIsEmpty));
	}

	@Override
	protected String getName() {
		return "simple map type";
	}
	
	@Override
	protected String[] getExpectedHeader() {
		return extended? SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION : SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER;
	}
	
	@Override
	protected void validateReferencedComponent(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(5), ReleaseComponentType.CONCEPT) && isComponentNotExist(row.get(5), ReleaseComponentType.DESCRIPTION)) {
			if (null == referencedComponentNotExist) {
				referencedComponentNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(referencedComponentNotExist, lineNumber, row.get(5));
		}
	}
	
	private void validateMapTarget(final List<String> row, final int lineNumber) {
		final String value = row.get(6);
		
		if (value.isEmpty()) {
			if (null == mapTargetIsEmpty) {
				mapTargetIsEmpty = Sets.newHashSet();
			}
			
			addDefectDescription(mapTargetIsEmpty, lineNumber);
		}
	}

}