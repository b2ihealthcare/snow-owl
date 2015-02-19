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
 * Represents a release file validator that validates the attribute value reference set.
 * 
 */
public class SnomedAttributeValueRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> refsetMemberValueNotExist;

	public SnomedAttributeValueRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) {
		super(configuration, releaseUrl, ComponentImportType.ATTRIBUTE_VALUE_REFSET, defects, validationUtil, SnomedRf2Headers.ATTRIBUTE_VALUE_TYPE_HEADER.length);
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		super.doValidate(row, lineNumber);
		
		isValueComponentExist(row, lineNumber);
	}

	@Override
	protected void addDefects() {
		super.addDefects();
		
		addDefects(new SnomedValidationDefect(DefectType.ATTRIBUTE_REFSET_VALUE_CONCEPT_NOT_EXIST, refsetMemberValueNotExist));
	}
	
	@Override
	protected String getName() {
		return "attribute value";
	}
	
	@Override
	protected String[] getExpectedHeader() {
		return SnomedRf2Headers.ATTRIBUTE_VALUE_TYPE_HEADER;
	}
	
	@Override
	protected void validateReferencedComponent(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(5))) {
			if (null == referencedComponentNotExist) {
				referencedComponentNotExist = Sets.newHashSet();
			}
			
			
			addDefectDescription(referencedComponentNotExist, lineNumber, row.get(5));
		}
	}
	
	private void isValueComponentExist(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(6), ReleaseComponentType.CONCEPT)) {
			if (null == refsetMemberValueNotExist) {
				refsetMemberValueNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(refsetMemberValueNotExist, lineNumber, row.get(6));
		}
	}
	
}