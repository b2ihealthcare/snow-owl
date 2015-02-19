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
 * Represents a release file validator that validates the concrete data type reference set.
 * 
 */
public class SnomedConcreteDataTypeRefSetValidator extends SnomedRefSetValidator {
	
	private final boolean withLabel;
	
	private Set<String> unitConceptNotExist;
	private Set<String> operatorConceptNotExist;
	private Set<String> valueIsEmpty;
	
	private static String[] getConcreteDataTypeHeader(final boolean withLabel) {
		return withLabel ? SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL : SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER;
	}
	
	public SnomedConcreteDataTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil, final boolean withLabel) {
		super(configuration, releaseUrl, ComponentImportType.CONCRETE_DOMAIN_REFSET, defects, validationUtil, getConcreteDataTypeHeader(withLabel).length);
		this.withLabel = withLabel;
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		super.doValidate(row, lineNumber);
		
		validateUnitConcept(row, lineNumber);
		validateOperatorConcept(row, lineNumber);
		validateValue(row, lineNumber);
	}

	@Override
	protected void addDefects() {
		super.addDefects();
		
		addDefects(new SnomedValidationDefect(DefectType.CONCRETE_DOMAIN_UNIT_CONCEPT_NOT_EXIST, unitConceptNotExist),
				new SnomedValidationDefect(DefectType.CONCRETE_DOMAIN_OPERATOR_CONCEPT_NOT_EXIST, operatorConceptNotExist),
				new SnomedValidationDefect(DefectType.CONCRETE_DOMAIN_VALUE_IS_EMPTY, valueIsEmpty));
	}

	@Override
	protected String getName() {
		return "concrete domain";
	}
	
	@Override
	protected String[] getExpectedHeader() {
		return getConcreteDataTypeHeader(withLabel);
	}
	
	@Override
	protected void validateReferencedComponent(List<String> row, int lineNumber) {
		if (isComponentNotExist(row.get(5), ReleaseComponentType.CONCEPT) && isComponentNotExist(row.get(5), ReleaseComponentType.RELATIONSHIP)) {
			if (null == referencedComponentNotExist) {
				referencedComponentNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(referencedComponentNotExist, lineNumber, row.get(5));
		}
	}

	private void validateUnitConcept(final List<String> row, final int lineNumber) {
		if (!row.get(6).isEmpty()) {
			if (isComponentNotExist(row.get(6), ReleaseComponentType.CONCEPT)) {
				if (null == unitConceptNotExist) {
					unitConceptNotExist = Sets.newHashSet();
				}
				
				addDefectDescription(unitConceptNotExist, lineNumber);
			}
		}
	}
	
	private void validateOperatorConcept(final List<String> row, final int lineNumber) {
		if (isComponentNotExist(row.get(7), ReleaseComponentType.CONCEPT)) {
			if (null == operatorConceptNotExist) {
				operatorConceptNotExist = Sets.newHashSet();
			}
			
			addDefectDescription(operatorConceptNotExist, lineNumber, row.get(7));
		}
	}

	private void validateValue(final List<String> row, final int lineNumber) {
		String value;
		
		if (withLabel) {
			value = row.get(9);
		} else {
			value = row.get(8);
		}
		
		if (value.isEmpty()) {
			if (null == valueIsEmpty) {
				valueIsEmpty = Sets.newHashSet();
			}
			
			addDefectDescription(valueIsEmpty, lineNumber);
		}
	}

}