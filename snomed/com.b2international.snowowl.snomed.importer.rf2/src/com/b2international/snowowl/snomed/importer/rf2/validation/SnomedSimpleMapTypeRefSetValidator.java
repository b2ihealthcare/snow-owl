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
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;

/**
 * Represents a release file validator that validates the simple map type reference set.
 */
public class SnomedSimpleMapTypeRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> mapTargetIsEmpty = newHashSet();

	public SnomedSimpleMapTypeRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.SIMPLE_MAP_TYPE_REFSET, context, SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER);
	}
	
	protected SnomedSimpleMapTypeRefSetValidator(ImportConfiguration configuration, URL releaseUrl, ComponentImportType importType, SnomedValidationContext validationUtil, String[] expectedHeader) {
		super(configuration, releaseUrl, importType, validationUtil, expectedHeader);
	}

	@Override
	protected void doValidate(final List<String> row) {
		super.doValidate(row);
		validateMapTarget(row);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.SIMPLE_MAP_TARGET_IS_EMPTY, mapTargetIsEmpty);
		mapTargetIsEmpty.clear();
	}
	
	@Override
	protected String getName() {
		return "simple map type";
	}
	
	private void validateMapTarget(final List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String mapTarget = row.get(6);
		if (mapTarget.isEmpty()) {
			mapTargetIsEmpty.add(String.format("Reference set member '%s''s map target is empty", uuid, effectiveTime));
		}
	}
}
