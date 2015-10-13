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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
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
	
	public SnomedRelationshipValidator(final ImportConfiguration configuration, final SnomedValidationContext context) throws IOException {
		super(configuration, configuration.toURL(configuration.getRelationshipsFile()), ComponentImportType.RELATIONSHIP, context, SnomedRf2Headers.RELATIONSHIP_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		final String componentId = row.get(0);
		final boolean active = "1".equals(row.get(2));
		final String sourceConcept = row.get(4);
		
		registerComponent(ComponentCategory.RELATIONSHIP, componentId, active);
		
		validateComponentUnique(row, relationshipIdsWithEffectivetimeStatus, relationshipIdNotUnique, lineNumber);
		
		if (row.get(7).equals(Concepts.IS_A) && sourceConcept.equals(row.get(5))) {
			relationshipSourceAndDestinationAreEqual.add(MessageFormat.format("Line number {0} in the ''{1}'' file with relationship ID {2}",
					lineNumber, releaseFileName, componentId));
		}

		validateComponentExists(sourceConcept, sourceConcept, ReleaseComponentType.CONCEPT, sourceConceptNotExist, lineNumber);
		validateComponentExists(row.get(5), sourceConcept, ReleaseComponentType.CONCEPT, destinationConceptNotExist, lineNumber);
		validateComponentExists(row.get(7), sourceConcept, ReleaseComponentType.CONCEPT, typeConceptNotExist, lineNumber);
		validateComponentExists(row.get(8), sourceConcept, ReleaseComponentType.CONCEPT, characteristicTypeConceptNotExist, lineNumber);
		validateComponentExists(row.get(9), sourceConcept, ReleaseComponentType.CONCEPT, modifierConceptNotExist, lineNumber);
	}

	@Override
	protected void doValidate(IProgressMonitor monitor) {
		super.doValidate(monitor);
		addDefect(DefectType.NOT_UNIQUE_RELATIONSHIP_ID, relationshipIdNotUnique);
		addDefect(DefectType.RELATIONSHIP_SOURCE_DESTINATION_EQUALS, relationshipSourceAndDestinationAreEqual);
		addDefect(DefectType.RELATIONSHIP_SOURCE_NOT_EXIST, sourceConceptNotExist);
		addDefect(DefectType.RELATIONSHIP_DESTINATION_NOT_EXIST, destinationConceptNotExist);
		addDefect(DefectType.RELATIONSHIP_TYPE_NOT_EXIST, typeConceptNotExist);
		addDefect(DefectType.RELATIONSHIP_CHARACTERISTIC_TYPE_NOT_EXIST, characteristicTypeConceptNotExist);
		addDefect(DefectType.RELATIONSHIP_MODIFIER_NOT_EXIST, modifierConceptNotExist);
	}
	
}