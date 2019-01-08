/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
 */
public class SnomedConcreteDataTypeRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> valueIsEmpty = newHashSet();
	private Set<String> typeConceptNotExist = newHashSet();
	private Set<String> characteristicTypeConceptNotExist = newHashSet();
	
	public SnomedConcreteDataTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.CONCRETE_DOMAIN_REFSET, context, SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String value = row.get(6);
		final String typeId = row.get(8);
		final String characteristicTypeId = row.get(9);
		
		if (value.isEmpty()) {
			valueIsEmpty.add(String.format("Reference set member '%s''s value property is empty in effective time '%s'", uuid, effectiveTime));
		}
		
		if (!isComponentExists(typeId, ReleaseComponentType.CONCEPT)) {
			typeConceptNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "typeId", typeId));
		}
		
		if (!isComponentExists(characteristicTypeId, ReleaseComponentType.CONCEPT)) {
			characteristicTypeConceptNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "characteristicTypeId", characteristicTypeId));
		}
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.CONCRETE_DOMAIN_VALUE_IS_EMPTY, valueIsEmpty);
		addDefect(DefectType.CONCRETE_DOMAIN_TYPE_CONCEPT_NOT_EXIST, typeConceptNotExist);
		addDefect(DefectType.CONCRETE_DOMAIN_CHARACTERISTIC_TYPE_CONCEPT_NOT_EXIST, characteristicTypeConceptNotExist);
	}

	@Override
	protected String getName() {
		return "concrete domain";
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		valueIsEmpty = newHashSet();
		typeConceptNotExist = newHashSet();
		characteristicTypeConceptNotExist = newHashSet();
	}
}
