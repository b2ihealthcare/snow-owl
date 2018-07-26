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
 * Represents a release file validator that validates the attribute value reference set.
 * 
 */
public class SnomedAttributeValueRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> refsetMemberValueNotExist = newHashSet();

	public SnomedAttributeValueRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.ATTRIBUTE_VALUE_REFSET, context, SnomedRf2Headers.ATTRIBUTE_VALUE_TYPE_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		validateValueComponent(row);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.ATTRIBUTE_REFSET_VALUE_CONCEPT_NOT_EXIST, refsetMemberValueNotExist);
	}
	
	@Override
	protected String getName() {
		return "attribute value";
	}
	
	private void validateValueComponent(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String valueConcept = row.get(6);
		if (!isComponentExists(valueConcept, ReleaseComponentType.CONCEPT)) {
			refsetMemberValueNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "value concept", valueConcept));
		}
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		refsetMemberValueNotExist = newHashSet();
	}
}