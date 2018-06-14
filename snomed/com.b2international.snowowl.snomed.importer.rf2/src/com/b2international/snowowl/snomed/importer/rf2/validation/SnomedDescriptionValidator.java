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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the description release file.
 */
public class SnomedDescriptionValidator extends AbstractSnomedValidator {
	
	private final Map<String, List<String>> descriptionIdsWithEffectivetimeStatus = Maps.newHashMap();
	private final Multimap<String, String> fullySpecifiedNames = HashMultimap.create();
	private final Map<String, String> fullySpecifiedNameNotUnique = Maps.newHashMap();
	private final Set<String> descriptionIdNotUnique = Sets.newHashSet();
	private final Set<String> descriptionConceptNotExist = Sets.newHashSet();
	private final Set<String> typeConceptNotExist = Sets.newHashSet();
	private final Set<String> caseSignificanceConceptNotExist = Sets.newHashSet();

	public SnomedDescriptionValidator(final ImportConfiguration configuration, final SnomedValidationContext context, File descriptionFile) throws IOException {
		super(configuration, configuration.toURL(descriptionFile), ComponentImportType.DESCRIPTION, context, SnomedRf2Headers.DESCRIPTION_HEADER);
	}

	@Override
	protected void doValidate(final List<String> row) {
		final String componentId = row.get(0);
		final String effectiveTime = row.get(1);
		final boolean active = "1".equals(row.get(2));
		final String concept = row.get(4);
		final String type = row.get(6);
		final String caseSignificance = row.get(8);
		
		registerComponent(ComponentCategory.DESCRIPTION, componentId, active);
		
		
		final boolean descriptionConceptExists = validateComponentExists(effectiveTime, concept, concept, ReleaseComponentType.CONCEPT, descriptionConceptNotExist);
		final boolean typeConceptExists = validateComponentExists(effectiveTime, type, concept, ReleaseComponentType.CONCEPT, typeConceptNotExist);
		final boolean caseSignificanceConceptExists = validateComponentExists(effectiveTime, caseSignificance, concept, ReleaseComponentType.CONCEPT, caseSignificanceConceptNotExist);
		
		validateComponentUnique(row, descriptionIdsWithEffectivetimeStatus, descriptionIdNotUnique);
		
		if (Concepts.FULLY_SPECIFIED_NAME.equals(type)) {
			final String term = row.get(7);
			if (descriptionConceptExists && typeConceptExists && caseSignificanceConceptExists && active) {
				fullySpecifiedNames.put(term, concept);
			} else {
				fullySpecifiedNames.remove(term, concept);
			}
		}
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		
		for (String term : fullySpecifiedNames.keySet()) {
			final Collection<String> concepts = fullySpecifiedNames.get(term);
			if (concepts.size() > 1) {
				int activeConcepts = 0;
				for (String concept : concepts) {
					if (isComponentActive(concept)) {
						activeConcepts++;
					}
				}
				if (activeConcepts > 1) {
					fullySpecifiedNameNotUnique.put(term, String.format("Term '%s' is not unique in effective time '%s' (file: %s)", term, effectiveTime, releaseFileName));
				}
			}
		}
		
		addDefect(DefectType.NOT_UNIQUE_DESCRIPTION_ID, descriptionIdNotUnique);
		addDefect(DefectType.NOT_UNIQUE_FULLY_SPECIFIED_NAME, fullySpecifiedNameNotUnique.values());
		addDefect(DefectType.DESCRIPTION_CONCEPT_NOT_EXIST, descriptionConceptNotExist);
		addDefect(DefectType.DESCRIPTION_TYPE_NOT_EXIST, typeConceptNotExist);
		addDefect(DefectType.DESCRIPTION_CASE_SIGNIFICANCE_NOT_EXIST, caseSignificanceConceptNotExist);
		
		descriptionIdsWithEffectivetimeStatus.clear();
		fullySpecifiedNames.clear();
		descriptionIdNotUnique.clear();
		fullySpecifiedNameNotUnique.clear();
		descriptionConceptNotExist.clear();
		typeConceptNotExist.clear();
		caseSignificanceConceptNotExist.clear();
	}
	
}
