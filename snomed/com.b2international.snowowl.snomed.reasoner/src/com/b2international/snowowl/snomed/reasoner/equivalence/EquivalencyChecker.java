/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.reasoner.classification.ClassifyOperation;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSet;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerApiException;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;

/**
 * Utility class that uses Snow Owl's current reasoner settings to run an
 * equivalency check on the supplied concepts and offer replacement concept IDs
 * if an equivalency is detected.
 * 
 * @since
 */
public final class EquivalencyChecker extends ClassifyOperation<LongKeyLongMap> {

	private EquivalencyChecker(final String reasonerId, final String userId, final List<SnomedConcept> additionalConcepts,
			final String repositoryId, final String branch) {

		super(reasonerId, userId, additionalConcepts, repositoryId, branch);
	}

	@Override
	protected LongKeyLongMap processResults(final String classificationId) {

		final Set<String> conceptIdsToCheck = additionalConcepts.stream()
				.map(SnomedConcept::getId)
				.collect(Collectors.toSet());

		final LongKeyLongMap equivalentConceptMap = PrimitiveMaps.newLongKeyLongOpenHashMap();

		final ClassificationTask classificationTask = ClassificationRequests.prepareGetClassification(classificationId)
				.setExpand("equivalentConceptSets()")
				.build(repositoryId)
				.execute(getEventBus())
				.getSync();

		if (!ClassificationStatus.COMPLETED.equals(classificationTask.getStatus())) {
			throw new ReasonerApiException("Selected reasoner could not start or failed to finish its job.");
		}

		if (!classificationTask.getEquivalentConceptsFound()) {
			return equivalentConceptMap;
		}

		final EquivalentConceptSets equivalentConceptSets = classificationTask.getEquivalentConceptSets();
		registerEquivalentConcepts(equivalentConceptSets, conceptIdsToCheck, equivalentConceptMap);
		return equivalentConceptMap;
	}

	private void registerEquivalentConcepts(final EquivalentConceptSets equivalentConceptSets, 
			final Set<String> conceptIdsToCheck,
			final LongKeyLongMap equivalentConceptMap) {

		for (final EquivalentConceptSet equivalenceSet : equivalentConceptSets) {
			if (equivalenceSet.isUnsatisfiable()) {
				continue;
			}

			final String suggestedConceptId = equivalenceSet.getEquivalentConcepts()
					.first()
					.map(SnomedConcept::getId)
					.get();

			final Set<String> equivalentIds = equivalenceSet.getEquivalentConcepts()
					.stream()
					.map(SnomedConcept::getId)
					.collect(Collectors.toSet());

			equivalentIds.remove(suggestedConceptId);

			if (!conceptIdsToCheck.isEmpty()) {
				equivalentIds.retainAll(conceptIdsToCheck);
			}

			registerEquivalentConcepts(suggestedConceptId, 
					equivalentIds, 
					equivalentConceptMap, 
					conceptIdsToCheck);
		}
	}

	private void registerEquivalentConcepts(final String suggestedConceptId, 
			final Set<String> equivalentIds,
			final LongKeyLongMap equivalentConceptMap, 
			final Set<String> conceptIdsToCheck) {

		final long replacementConceptId = Long.parseLong(suggestedConceptId);

		for (final String equivalentConcept : equivalentIds) {
			final long equivalentConceptId = Long.parseLong(equivalentConcept);
			equivalentConceptMap.put(equivalentConceptId, replacementConceptId);
		}
	}
}
