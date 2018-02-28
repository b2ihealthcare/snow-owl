/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the relationship release file.
 * 
 */
public class SnomedRelationshipValidator extends AbstractSnomedValidator {
	
	private final Map<String, List<String>> relationshipIdsWithEffectivetimeStatus = Maps.newHashMap();
	private Collection<String> relationshipIdNotUnique = Sets.newHashSet();
	private Collection<String> relationshipSourceAndDestinationAreEqual = Sets.newHashSet();
	
	public SnomedRelationshipValidator(final ImportConfiguration configuration, final SnomedValidationContext context, final File relationshipsFile) throws IOException {
		super(configuration, configuration.toURL(relationshipsFile), ComponentImportType.RELATIONSHIP, context, SnomedRf2Headers.RELATIONSHIP_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		
		final String componentId = row.get(0);
		final String effectiveTime = row.get(1);
		final boolean active = "1".equals(row.get(2));
		final String source = row.get(4);
		
		registerComponent(ComponentCategory.RELATIONSHIP, componentId, active);
		
		validateComponentUnique(row, relationshipIdsWithEffectivetimeStatus, relationshipIdNotUnique);
		
		final String destination = row.get(5);
		final String type = row.get(7);
		final String characteristicType = row.get(8);
		final String modifier = row.get(9);
		
		if (type.equals(Concepts.IS_A) && source.equals(destination)) {
			relationshipSourceAndDestinationAreEqual.add(String.format("'%s' IS A relationship has same source and destination concept '%s' in effective time '%s'", componentId, source, effectiveTime));
		}

		for (String referencedConcept : ImmutableList.of(source, destination, type, characteristicType, modifier)) {
			if (!isComponentExists(referencedConcept)) {
				final String missingConceptMessage = String.format("'%s' relationship refers to a non-existent concept '%s' in effective time '%s'", componentId, referencedConcept, effectiveTime);
				addDefect(DefectType.RELATIONSHIP_REFERENCED_NONEXISTENT_CONCEPT, missingConceptMessage);
			} else if (active && !isComponentActive(referencedConcept)) {
				final String inactiveConceptMessage = String.format("'%s' relationship refers to an inactive concept '%s' in effective time '%s'", componentId, referencedConcept, effectiveTime);
				addDefect(DefectType.RELATIONSHIP_REFERENCED_INACTIVE_CONCEPT, inactiveConceptMessage);
			}
		}
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		
		addDefect(DefectType.NOT_UNIQUE_RELATIONSHIP_ID, relationshipIdNotUnique);
		addDefect(DefectType.RELATIONSHIP_SOURCE_DESTINATION_EQUALS, relationshipSourceAndDestinationAreEqual);
		
		relationshipIdsWithEffectivetimeStatus.clear();
		relationshipIdNotUnique.clear();
		relationshipSourceAndDestinationAreEqual.clear();
	}
	
}