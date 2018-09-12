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
 * Represents a release file validator that validates the association type reference set.
 * 
 */
public class SnomedAssociationRefSetValidator extends SnomedRefSetValidator {
	
	private Set<String> targetComponentNotExist = newHashSet();

	public SnomedAssociationRefSetValidator(ImportConfiguration configuration, URL releaseUrl, SnomedValidationContext context) {
		super(configuration, releaseUrl, ComponentImportType.ASSOCIATION_TYPE_REFSET, context, SnomedRf2Headers.ASSOCIATION_TYPE_HEADER);
	}
	
	@Override
	protected void doValidate(List<String> row) {
		super.doValidate(row);
		validateTargetComponent(row);
	}


	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.ASSOCIATION_REFSET_TARGET_COMPONENT_NOT_EXIST, targetComponentNotExist);
	}
	
	@Override
	protected String getName() {
		return "association type";
	}

	private void validateTargetComponent(List<String> row) {
		final String uuid = row.get(0);
		final String effectiveTime = row.get(1);
		final String targetComponent = row.get(6);
		if (!isComponentExists(targetComponent, ReleaseComponentType.CONCEPT)) {
			targetComponentNotExist.add(getMissingComponentMessage(uuid, effectiveTime, "target component", targetComponent));
		}
	}
	
	@Override
	protected void clearCaches() {
		super.clearCaches();
		targetComponentNotExist = newHashSet();
	}

}