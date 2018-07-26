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
 * Represents a release file validator that validates the complex map type reference set.
 * 
 */
public class SnomedComplexMapTypeRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> correlationConceptNotExist = newHashSet();

	public SnomedComplexMapTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.COMPLEX_MAP_TYPE_REFSET, context, SnomedRf2Headers.COMPLEX_MAP_TYPE_HEADER);
	}
	
	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		validateCorrelationConcept(row);
	}
	
	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.COMPLEX_MAP_REFERENCED_INVALID_CONCEPT, correlationConceptNotExist);
	}
	
	@Override
	protected String getName() {
		return "complex map type";
	}
	
	private void validateCorrelationConcept(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String correlation = row.get(11);
		if (!isComponentExists(correlation, ReleaseComponentType.CONCEPT)) {
			correlationConceptNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "correlation", correlation));
		}
	}

	@Override
	protected void clearCaches() {
		super.clearCaches();
		correlationConceptNotExist = newHashSet();
	}
	
}