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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect.DefectType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ValidationUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the concept release file.
 */
public class SnomedConceptValidator extends AbstractSnomedValidator {
	
	private final Map<String, List<String>> conceptIdsWithEffectivetimeStatus;
	private final Map<String, Integer> moduleIds;
	private final Map<String, Integer> definitionStatusIds;
	private final Set<String> moduleIdNotExist;
	private final Set<String> definitionStatusIdNotExist;
	
	public SnomedConceptValidator(final ImportConfiguration configuration, final File releaseRelativePath, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) throws IOException {
		super(configuration, configuration.toURL(releaseRelativePath), ComponentImportType.CONCEPT, defects, validationUtil, SnomedRf2Headers.CONCEPT_HEADER.length);
		conceptIdsWithEffectivetimeStatus = Maps.newHashMap();
		moduleIds = Maps.newHashMap();
		definitionStatusIds = Maps.newHashMap();
		moduleIdNotExist = Sets.newHashSet();
		definitionStatusIdNotExist = Sets.newHashSet();
	}

	@Override
	public void checkReleaseFileHeader(final String[] actualHeader) {
		if (!StringUtils.equalsIgnoreCase(actualHeader, SnomedRf2Headers.CONCEPT_HEADER)) {
			final Set<String> headerDifference = Sets.newHashSet();
			headerDifference.add(MessageFormat.format("In the ''{0}'' concept file", releaseFileName));
			
			addDefects(new SnomedValidationDefect(DefectType.HEADER_DIFFERENCES, headerDifference));
		}
	}
	
	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		collectIfInvalid(row.get(0), SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		
		if (!conceptIdsWithEffectivetimeStatus.containsKey(row.get(0))) {
			final List<String> statusEffectiveTime = Lists.newArrayList();
			statusEffectiveTime.add(row.get(2));
			statusEffectiveTime.add(row.get(1));
			conceptIdsWithEffectivetimeStatus.put(row.get(0), statusEffectiveTime);
		} else {
			if (0 > conceptIdsWithEffectivetimeStatus.get(row.get(0)).get(1).compareTo(row.get(1))) {
				conceptIdsWithEffectivetimeStatus.get(row.get(0)).set(1, row.get(1));
			}
		}
		
		if (moduleIds.containsKey(row.get(3))) {
			moduleIds.put(row.get(3), -1);
		} else {
			moduleIds.put(row.get(3), lineNumber);
		}
		
		if (definitionStatusIds.containsKey(row.get(4))) {
			definitionStatusIds.put(row.get(4), -1);
		} else {
			definitionStatusIds.put(row.get(4), lineNumber);
		}
	}

	@Override
	protected void addDefects() {
		super.addDefects();
		addDefects(new SnomedValidationDefect(DefectType.MODULE_CONCEPT_NOT_EXIST, moduleIdNotExist));
		addDefects(new SnomedValidationDefect(DefectType.CONCEPT_DEFINITION_STATUS_NOT_EXIST, definitionStatusIdNotExist));
	}
	
	@Override
	protected void postConceptValidation() {

		checkConceptIds(moduleIds, moduleIdNotExist);
		checkConceptIds(definitionStatusIds, definitionStatusIdNotExist);
		
		final Iterator<Entry<String, List<String>>> iterator = conceptIdsWithEffectivetimeStatus.entrySet().iterator();
		while (iterator.hasNext()) {
			final Entry<String, List<String>> entry = iterator.next();
			if (entry.getValue().get(1).equals("1")) {
				validationUtil.getActiveConceptIds().add(entry.getKey());
			} else {
				validationUtil.getInactiveConceptIds().add(entry.getKey());
			}
		}
	}

	private void checkConceptIds(final Map<String, Integer> idAndLineNumber, final Set<String> errorMessages) {
		
		for (final String conceptId : idAndLineNumber.keySet()) {
				
			if (!conceptIdsWithEffectivetimeStatus.containsKey(conceptId) 
					&& !conceptLookupService.exists(createActivePath(), conceptId)) {
				
				final String message;
				
				if (idAndLineNumber.get(conceptId) == -1) {
					message = MessageFormat.format("In multiple lines in the ''{0}'' file with concept ID {1}", releaseFileName, conceptId);
				} else {
					message = MessageFormat.format("Line number {0} in the ''{1}'' file with concept ID {2}", idAndLineNumber.get(conceptId), releaseFileName, conceptId);
				}
				
				errorMessages.add(message);
			}
		}
	}
}
