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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.collect.Maps;

/**
 * Represents a release file validator that validates the concept release file.
 */
public class SnomedConceptValidator extends AbstractSnomedValidator {
	
	private final Map<String, Integer> requiredModules = Maps.newHashMap();
	private final Map<String, Integer> requiredDefinitionStatuses = Maps.newHashMap();
	
	public SnomedConceptValidator(final ImportConfiguration configuration, final SnomedValidationContext context) throws IOException {
		super(configuration, configuration.toURL(configuration.getConceptsFile()), ComponentImportType.CONCEPT, context, SnomedRf2Headers.CONCEPT_HEADER);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		checkConceptIds(DefectType.MODULE_CONCEPT_NOT_EXIST, requiredModules);
		checkConceptIds(DefectType.CONCEPT_DEFINITION_STATUS_NOT_EXIST, requiredDefinitionStatuses);
		requiredModules.clear();
		requiredDefinitionStatuses.clear();
	}
	
	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		final String id = row.get(0); // concept ID
		final boolean active = "1".equals(row.get(2));
		
		registerComponent(ComponentCategory.CONCEPT, id, active);
		
		final String module = row.get(3);
		final String definitionStatus = row.get(4);
		
		if (requiredModules.containsKey(module)) {
			requiredModules.put(module, -1);
		} else {
			requiredModules.put(module, lineNumber);
		}
		
		if (requiredDefinitionStatuses.containsKey(definitionStatus)) {
			requiredDefinitionStatuses.put(definitionStatus, -1);
		} else {
			requiredDefinitionStatuses.put(definitionStatus, lineNumber);
		}
	}
	
	private void checkConceptIds(DefectType type, final Map<String, Integer> requiredConceptIdsInEffectiveTime) {
		final Collection<String> errorMessages = newHashSet();
		for (final String componentId : requiredConceptIdsInEffectiveTime.keySet()) {
			if (!isComponentExists(componentId, ReleaseComponentType.CONCEPT)) {
				final String message;
				
				if (requiredConceptIdsInEffectiveTime.get(componentId) == -1) {
					message = MessageFormat.format("In multiple lines in the ''{0}'' file with concept ID {1}", releaseFileName, componentId);
				} else {
					message = MessageFormat.format("Line number {0} in the ''{1}'' file with concept ID {2}", requiredConceptIdsInEffectiveTime.get(componentId), releaseFileName, componentId);
				}
				
				errorMessages.add(message);
			}
		}
		addDefect(type, errorMessages);
	}
}
