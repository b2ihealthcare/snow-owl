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
 * Represents a release file validator that validates the relationship release file.
 * 
 */
public class SnomedRelationshipValidator extends AbstractSnomedValidator {
	
	private final Map<String, List<String>> relationshipIdsWithEffectivetimeStatus = Maps.newHashMap();
	private Set<String> relationshipIdNotUnique = Sets.newHashSet();
	private Set<String> relationshipSourceAndDestinationAreEqual = Sets.newHashSet();
	private Set<String> sourceConceptNotExist = Sets.newHashSet();
	private Set<String> destinationConceptNotExist = Sets.newHashSet();
	private Set<String> typeConceptNotExist = Sets.newHashSet();
	private Set<String> characteristicTypeConceptNotExist = Sets.newHashSet();
	private Set<String> modifierConceptNotExist = Sets.newHashSet();
	
	public SnomedRelationshipValidator(final ImportConfiguration configuration, final File releaseRelativePath, final Set<SnomedValidationDefect> defects, final ValidationUtil validationUtil) throws IOException {
		super(configuration, configuration.toURL(releaseRelativePath), ComponentImportType.RELATIONSHIP, defects, validationUtil, SnomedRf2Headers.RELATIONSHIP_HEADER.length);
	}

	@Override
	public void checkReleaseFileHeader(final String[] actualHeader) {
		if (!StringUtils.equalsIgnoreCase(actualHeader, SnomedRf2Headers.RELATIONSHIP_HEADER)) {
			final Set<String> headerDifference = Sets.newHashSet();
			headerDifference.add(MessageFormat.format("In the ''{0}'' relationship file", releaseFileName));
			addDefects(new SnomedValidationDefect(DefectType.HEADER_DIFFERENCES, headerDifference));
		}
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		collectIfInvalid(row.get(0), SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER);
		
		validateComponentUnique(row, relationshipIdsWithEffectivetimeStatus, relationshipIdNotUnique, lineNumber);
		validationUtil.getRelationshipIds().add(row.get(0));
		
		if (row.get(7).equals(Concepts.IS_A) && row.get(4).equals(row.get(5))) {
			relationshipSourceAndDestinationAreEqual.add(MessageFormat.format("Line number {0} in the ''{1}'' file with relationship ID {2}",
					lineNumber, releaseFileName, row.get(0)));
		}

		validateComponentExists(row.get(4), row.get(4), ReleaseComponentType.CONCEPT, sourceConceptNotExist, lineNumber);
		validateComponentExists(row.get(5), row.get(4), ReleaseComponentType.CONCEPT, destinationConceptNotExist, lineNumber);
		validateComponentExists(row.get(7), row.get(4), ReleaseComponentType.CONCEPT, typeConceptNotExist, lineNumber);
		validateComponentExists(row.get(8), row.get(4), ReleaseComponentType.CONCEPT, characteristicTypeConceptNotExist, lineNumber);
		validateComponentExists(row.get(9), row.get(4), ReleaseComponentType.CONCEPT, modifierConceptNotExist, lineNumber);
	}

	@Override
	protected void addDefects() {
		addDefects(new SnomedValidationDefect(DefectType.NOT_UNIQUE_RELATIONSHIP_ID, relationshipIdNotUnique),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_SOURCE_DESTINATION_EQUALS, relationshipSourceAndDestinationAreEqual),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_SOURCE_NOT_EXIST, sourceConceptNotExist),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_DESTINATION_NOT_EXIST, destinationConceptNotExist),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_TYPE_NOT_EXIST, typeConceptNotExist),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_CHARACTERISTIC_TYPE_NOT_EXIST, characteristicTypeConceptNotExist),
				new SnomedValidationDefect(DefectType.RELATIONSHIP_MODIFIER_NOT_EXIST, modifierConceptNotExist));
	}
}