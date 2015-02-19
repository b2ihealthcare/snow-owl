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
 * Represents a release file validator that validates the association type reference set.
 * 
 */
public class SnomedAssociationRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> targetComponentNotExist;

	public SnomedAssociationRefSetValidator(ImportConfiguration configuration, URL releaseUrl, Set<SnomedValidationDefect> defects, ValidationUtil validationUtil) {
		super(configuration, releaseUrl, ComponentImportType.ASSOCIATION_TYPE_REFSET, defects, validationUtil, SnomedRf2Headers.ASSOCIATION_TYPE_HEADER.length);
	}
	
	@Override
	protected void doValidate(List<String> row, int lineNumber) {
		super.doValidate(row, lineNumber);
		
		validateTargetComponent(row, lineNumber);
	}

	@Override
	protected void addDefects() {
		super.addDefects();
		
		addDefects(new SnomedValidationDefect(DefectType.ASSOCIATION_REFSET_TARGET_COMPONENT_NOT_EXIST, targetComponentNotExist));
	}

	@Override
	protected String getName() {
		return "association type";
	}

	@Override
	protected String[] getExpectedHeader() {
		return SnomedRf2Headers.ASSOCIATION_TYPE_HEADER;
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
	
	private void validateTargetComponent(List<String> row, int lineNumber) {
		if (isComponentNotExist(row.get(6), ReleaseComponentType.CONCEPT)) {
			if (null == targetComponentNotExist) {
				targetComponentNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(targetComponentNotExist, lineNumber, row.get(6));
		}
	}

}