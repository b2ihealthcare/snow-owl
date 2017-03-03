/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.equivalence;

import java.util.List;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationSettings;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetEquivalentConceptsResponse;
import com.b2international.snowowl.snomed.reasoner.classification.operation.ClassifyOperation;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerException;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;

/**
 * Utility class that uses Snow Owl's current reasoner settings to run an equivalency check on the supplied concepts and offer replacement concept IDs
 * if an equivalency is detected.
 */
public class EquivalencyChecker extends ClassifyOperation<LongKeyLongMap> {

	public EquivalencyChecker(ClassificationSettings settings) {
		super(settings);
	}

	@Override
	protected LongKeyLongMap processResults(String classificationId) {

		List<ConceptDefinition> additionalDefinitions = settings.getAdditionalDefinitions();
		LongSet conceptIdsToCheck = collectConceptIds(additionalDefinitions);
		LongKeyLongMap equivalentConceptMap = PrimitiveMaps.newLongKeyLongOpenHashMap();
		GetEquivalentConceptsResponse response = getReasonerService().getEquivalentConcepts(classificationId);

		if (Type.NOT_AVAILABLE == response.getType()) {
			throw new ReasonerException("Selected reasoner could not start or failed to finish its job.");
		}

		List<AbstractEquivalenceSet> equivalentConcepts = response.getEquivalenceSets();
		registerEquivalentConcepts(equivalentConcepts, conceptIdsToCheck, equivalentConceptMap);
		return equivalentConceptMap;
	}

	private void registerEquivalentConcepts(List<AbstractEquivalenceSet> equivalentConcepts, 
			LongSet conceptIdsToCheck,
			LongKeyLongMap equivalentConceptMap) {

		for (AbstractEquivalenceSet equivalenceSet : equivalentConcepts) {

			if (equivalenceSet.isUnsatisfiable()) {
				continue;
			}

			SnomedConcept suggestedConcept = ((EquivalenceSet) equivalenceSet).getSuggestedConcept();
			registerEquivalentConcepts(suggestedConcept, equivalenceSet.getConcepts(), equivalentConceptMap, conceptIdsToCheck);
		}
	}

	private LongSet collectConceptIds(List<ConceptDefinition> conceptDefinitions) {

		LongSet conceptIds = PrimitiveSets.newLongOpenHashSet();

		for (ConceptDefinition definition : conceptDefinitions) {
			conceptIds.add(definition.getConceptId());
		}

		return conceptIds;
	}

	private void registerEquivalentConcepts(SnomedConcept suggestedConcept, 
			List<SnomedConcept> equivalentConcepts,
			LongKeyLongMap equivalentConceptMap, 
			LongSet conceptIdsToCheck) {

		long replacementConceptId = getConceptId(suggestedConcept);
		boolean registerAll = conceptIdsToCheck.isEmpty();

		for (SnomedConcept equivalentConcept : equivalentConcepts) {
			long equivalentConceptId = getConceptId(equivalentConcept);
			if (registerAll || conceptIdsToCheck.contains(equivalentConceptId)) {
				equivalentConceptMap.put(equivalentConceptId, replacementConceptId);
			}
		}
	}

	private Long getConceptId(SnomedConcept conceptIndexEntry) {
		return Long.valueOf(conceptIndexEntry.getId());
	}
}
