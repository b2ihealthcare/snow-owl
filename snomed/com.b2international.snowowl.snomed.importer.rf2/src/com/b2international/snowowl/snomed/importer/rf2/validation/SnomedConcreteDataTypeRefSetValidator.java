/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;

/**
 * Represents a release file validator that validates the concrete domain reference set.
 * 
 */
public class SnomedConcreteDataTypeRefSetValidator extends SnomedRefSetValidator {
	
	private final boolean withLabel;
	
	private Set<String> unitConceptNotExist = newHashSet();
	private Set<String> operatorConceptNotExist = newHashSet();
	private Set<String> valueIsEmpty = newHashSet();
	
	private static String[] getConcreteDataTypeHeader(final boolean withLabel) {
		return withLabel ? SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL : SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER;
	}
	
	public SnomedConcreteDataTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context, final boolean withLabel) {
		super(configuration, releaseUrl, ComponentImportType.CONCRETE_DOMAIN_REFSET, context, getConcreteDataTypeHeader(withLabel));
		this.withLabel = withLabel;
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		
		validateUnitConcept(row);
		validateOperatorConcept(row);
		validateValue(row);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.CONCRETE_DOMAIN_UNIT_CONCEPT_NOT_EXIST, unitConceptNotExist);
		addDefect(DefectType.CONCRETE_DOMAIN_OPERATOR_CONCEPT_NOT_EXIST, operatorConceptNotExist);
		addDefect(DefectType.CONCRETE_DOMAIN_VALUE_IS_EMPTY, valueIsEmpty);
	}

	@Override
	protected String getName() {
		return "concrete domain";
	}
	
	private void validateUnitConcept(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String unit = row.get(6);
		if (!unit.isEmpty()) {
			if (!isComponentExists(unit, ReleaseComponentType.CONCEPT)) {
				operatorConceptNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "unit", unit));
			}
		}
	}
	
	private void validateOperatorConcept(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String operator = row.get(7);
		if (!isComponentExists(operator, ReleaseComponentType.CONCEPT)) {
			operatorConceptNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "operator", operator));
		}
	}

	private void validateValue(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String value = withLabel ? row.get(9) : row.get(8);
		if (value.isEmpty()) {
			valueIsEmpty.add(String.format("Reference set member '%s''s value property is empty in effective time '%s'", uuid, effectiveTime));
		}
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		unitConceptNotExist = newHashSet();
		operatorConceptNotExist = newHashSet();
		valueIsEmpty = newHashSet();
	};

}