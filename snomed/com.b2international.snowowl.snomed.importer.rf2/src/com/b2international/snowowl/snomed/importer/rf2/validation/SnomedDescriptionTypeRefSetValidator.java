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
 * Represents a release file validator that validates the description type reference set.
 * 
 */
public class SnomedDescriptionTypeRefSetValidator extends SnomedRefSetValidator {

	private Set<String> descriptionFormatNotExist = newHashSet();
	private Set<String> descriptionLengthIsEmpty = newHashSet();

	public SnomedDescriptionTypeRefSetValidator(ImportConfiguration configuration, URL releaseUrl, SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.DESCRIPTION_TYPE_REFSET, context, SnomedRf2Headers.DESCRIPTION_TYPE_HEADER);
	}
	
	@Override
	protected void doValidate(List<String> row) {
		super.doValidate(row);
		
		validateDescriptionFormat(row);
		validateDescriptionLength(row);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.DESCRIPTION_TYPE_DESCRIPTION_FORMAT_NOT_EXIST, descriptionFormatNotExist);
		addDefect(DefectType.DESCRIPTION_TYPE_DESCRIPTION_LENGTH_IS_EMPTY, descriptionLengthIsEmpty);
	}
	
	@Override
	protected String getName() {
		return "description type";
	}

	private void validateDescriptionFormat(List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String descriptionFormat = row.get(6);
		if (!isComponentExists(descriptionFormat, ReleaseComponentType.CONCEPT)) {
			descriptionFormatNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "description format", descriptionFormat));
		}
	}
	
	private void validateDescriptionLength(List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String length = row.get(7);
		if (length.isEmpty()) {
			descriptionLengthIsEmpty.add(String.format("Reference set member '%s' description length property is empty in effective time '%s'", uuid, effectiveTime));
		}
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		descriptionFormatNotExist = newHashSet();
		descriptionLengthIsEmpty = newHashSet();
	}

}