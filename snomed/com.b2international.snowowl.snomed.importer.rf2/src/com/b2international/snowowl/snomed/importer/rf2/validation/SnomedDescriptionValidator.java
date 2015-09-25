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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect.DefectType;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.b2international.snowowl.snomed.importer.rf2.util.ValidationUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the description release file.
 */
public class SnomedDescriptionValidator extends AbstractSnomedValidator {
	
	private final Map<String, List<String>> descriptionIdsWithEffectivetimeStatus = Maps.newHashMap();
	private final Map<String, List<String>> fullySpecifiedNames = Maps.newHashMap();
	private final Map<String, String> fullySpecifiedNameNotUnique = Maps.newHashMap();
	private final Set<String> descriptionIdNotUnique = Sets.newHashSet();
	private final Set<String> descriptionConceptNotExist = Sets.newHashSet();
	private final Set<String> typeConceptNotExist = Sets.newHashSet();
	private final Set<String> caseSignificanceConceptNotExist = Sets.newHashSet();

	public SnomedDescriptionValidator(final ImportConfiguration configuration, final File releaseRelativePath, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) throws IOException {
		super(configuration, configuration.toURL(releaseRelativePath), ComponentImportType.DESCRIPTION, defects, validationUtil, SnomedRf2Headers.DESCRIPTION_HEADER.length);
	}

	@Override
	public void checkReleaseFileHeader(final String[] actualHeader) {
		if (!StringUtils.equalsIgnoreCase(actualHeader, SnomedRf2Headers.DESCRIPTION_HEADER)) {
			final Set<String> headerDifference = Sets.newHashSet();
			headerDifference.add(MessageFormat.format("In the ''{0}'' description file", releaseFileName));
			
			addDefects(new SnomedValidationDefect(DefectType.HEADER_DIFFERENCES, headerDifference));
		}
	}
	
	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		collectIfInvalid(row.get(0), SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER);
		
		validateComponentExists(row.get(4), row.get(4), ReleaseComponentType.CONCEPT, descriptionConceptNotExist, lineNumber);
		validateComponentExists(row.get(6), row.get(4), ReleaseComponentType.CONCEPT, typeConceptNotExist, lineNumber);
		validateComponentExists(row.get(8), row.get(4), ReleaseComponentType.CONCEPT, caseSignificanceConceptNotExist, lineNumber);
		
		validateComponentUnique(row, descriptionIdsWithEffectivetimeStatus, descriptionIdNotUnique, lineNumber);
		validateFullySpecifiedName(row, fullySpecifiedNames, fullySpecifiedNameNotUnique, lineNumber);
		
		validationUtil.getDescriptionIds().add(row.get(0));
	}
	
	@Override
	protected void addDefects() {
		super.addDefects();
		addDefects(new SnomedValidationDefect(DefectType.NOT_UNIQUE_DESCRIPTION_ID, descriptionIdNotUnique),
				new SnomedValidationDefect(DefectType.NOT_UNIQUE_FULLY_SPECIFIED_NAME, new HashSet<String>(fullySpecifiedNameNotUnique.values())),
				new SnomedValidationDefect(DefectType.DESCRIPTION_CONCEPT_NOT_EXIST, descriptionConceptNotExist),
				new SnomedValidationDefect(DefectType.DESCRIPTION_TYPE_NOT_EXIST, typeConceptNotExist),
				new SnomedValidationDefect(DefectType.DESCRIPTION_CASE_SIGNIFICANCE_NOT_EXIST, caseSignificanceConceptNotExist));
	}
	
	private void validateFullySpecifiedName(final List<String> row, final Map<String, List<String>> fullySpecifiedNames, final Map<String, String> fullySpecifiedNameIsNotUnique, final int lineNumber) {
		if (Concepts.FULLY_SPECIFIED_NAME.equals(row.get(6))) {
			if (fullySpecifiedNames.containsKey(row.get(7))) {
				if (fullySpecifiedNames.get(row.get(7)).get(0).equals(row.get(4))) {
					fullySpecifiedNames.get(row.get(7)).set(1, row.get(2));
				} else if (!fullySpecifiedNames.get(row.get(7)).get(1).equals("0")) {
					if (row.get(2).equals("0")) {
						fullySpecifiedNameIsNotUnique.remove(row.get(7));
					} else {
						String conceptStatus;
						if (validationUtil.getActiveConceptIds().contains(row.get(4))) {
							conceptStatus = "1";
						} else if (validationUtil.getInactiveConceptIds().contains(row.get(4))) {
							conceptStatus = "0";
						} else {
							conceptStatus = true == conceptLookupService.getComponent(createActivePath(), row.get(4)).isActive() ? "1" : "0";
						}
						
						if (null != conceptStatus && conceptStatus.equals("1")) {
							fullySpecifiedNameIsNotUnique.put(row.get(7), MessageFormat.format("Line number {0} in the ''{1}'' file with term {2}", 
									lineNumber, releaseFileName, row.get(7)));
						}
					}
				}
			} else {
				if (validationUtil.getActiveConceptIds().contains(row.get(4))) {
					fullySpecifiedNames.put(row.get(7), createConceptIdStatusList(row));
				}
			}
		}
	}

}
