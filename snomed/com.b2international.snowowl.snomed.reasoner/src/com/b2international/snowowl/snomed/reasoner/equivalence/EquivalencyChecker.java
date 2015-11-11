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
package com.b2international.snowowl.snomed.reasoner.equivalence;

import java.util.List;
import java.util.UUID;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractEquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.AbstractResponse.Type;
import com.b2international.snowowl.snomed.reasoner.classification.ClassificationRequest;
import com.b2international.snowowl.snomed.reasoner.classification.EquivalenceSet;
import com.b2international.snowowl.snomed.reasoner.classification.GetEquivalentConceptsResponse;
import com.b2international.snowowl.snomed.reasoner.classification.operation.ClassifyOperation;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerException;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;

/**
 * Utility class that uses Snow Owl's current reasoner settings to run an equivalency check on the supplied concepts and offer replacement concept IDs
 * if an equivalency is detected.
 * 
 */
public class EquivalencyChecker extends ClassifyOperation<LongKeyLongMap> {

	public EquivalencyChecker(final ClassificationRequest classificationRequest) {
		super(classificationRequest);
	}

	@Override
	protected LongKeyLongMap processResults(final UUID classificationId) {

		final List<ConceptDefinition> additionalDefinitions = classificationRequest.getAdditionalDefinitions();
		final LongSet conceptIdsToCheck = collectConceptIds(additionalDefinitions);
		final LongKeyLongMap equivalentConceptMap = new LongKeyLongOpenHashMap();
		final GetEquivalentConceptsResponse response = getReasonerService().getEquivalentConcepts(classificationId);

		if (Type.NOT_AVAILABLE == response.getType()) {
			throw new ReasonerException("Selected reasoner could not start or failed to finish its job.");
		}
		
		final List<AbstractEquivalenceSet> equivalentConcepts = response.getEquivalenceSets();
		registerEquivalentConcepts(equivalentConcepts, conceptIdsToCheck, equivalentConceptMap);
		return equivalentConceptMap;
	}

	private void registerEquivalentConcepts(final List<AbstractEquivalenceSet> equivalentConcepts, 
			final LongSet conceptIdsToCheck,
			final LongKeyLongMap equivalentConceptMap) {

		for (final AbstractEquivalenceSet equivalenceSet : equivalentConcepts) {

			if (equivalenceSet.isUnsatisfiable()) {
				continue;
			}

			final SnomedConceptIndexEntry suggestedConcept = ((EquivalenceSet) equivalenceSet).getSuggestedConcept();
			registerEquivalentConcepts(suggestedConcept, equivalenceSet.getConcepts(), equivalentConceptMap, conceptIdsToCheck);
		}
	}

	private LongSet collectConceptIds(final List<ConceptDefinition> conceptDefinitions) {

		final LongSet conceptIds = new LongOpenHashSet();

		for (final ConceptDefinition definition : conceptDefinitions) {
			conceptIds.add(definition.getConceptId());
		}

		return conceptIds;
	}

	private void registerEquivalentConcepts(final SnomedConceptIndexEntry suggestedConcept, 
			final List<SnomedConceptIndexEntry> equivalentConcepts,
			final LongKeyLongMap equivalentConceptMap, 
			final LongSet conceptIdsToCheck) {

		final long replacementConceptId = getConceptId(suggestedConcept);
		final boolean registerAll = conceptIdsToCheck.isEmpty();
		
		for (final SnomedConceptIndexEntry equivalentConcept : equivalentConcepts) {
			final long equivalentConceptId = getConceptId(equivalentConcept);
			if (registerAll || conceptIdsToCheck.contains(equivalentConceptId)) {
				equivalentConceptMap.put(equivalentConceptId, replacementConceptId);
			}
		}
	}

	private Long getConceptId(final SnomedConceptIndexEntry conceptIndexEntry) {
		return Long.valueOf(conceptIndexEntry.getId());
	}
}