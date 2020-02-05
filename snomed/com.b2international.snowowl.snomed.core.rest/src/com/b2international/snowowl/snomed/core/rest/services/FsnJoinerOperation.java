/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Given a set of {@link SnomedConcept concepts}, collects the corresponding "preferred" fully
 * specified names of each entry, and makes them available for converting to a response object.
 */
public abstract class FsnJoinerOperation<T> {

	private final String conceptId;
	private final List<ExtendedLocale> locales;
	private final DescriptionService descriptionService;
	
	// Requires a BranchContext decorated with an IndexSearcher
	protected FsnJoinerOperation(final String conceptId, final List<ExtendedLocale> locales, final DescriptionService descriptionService) {
		this.conceptId = conceptId;
		this.locales = locales;
		this.descriptionService = descriptionService;
	}

	public final List<T> run() {
		final Iterable<SnomedConcept> concepts = getConceptEntries(conceptId);
		if (Iterables.isEmpty(concepts)) {
			return ImmutableList.of();
		}
		
		Map<String, SnomedDescription> descriptionsByConcept = initDescriptionsByConcept(concepts);
		return convertConceptEntries(concepts, descriptionsByConcept);
	}

	private Map<String, SnomedDescription> initDescriptionsByConcept(Iterable<SnomedConcept> conceptEntries) {
		final Set<String> conceptIds = FluentIterable.from(conceptEntries).transform(IComponent::getId).toSet();
		return descriptionService.getFullySpecifiedNames(conceptIds, locales);
	}

	private List<T> convertConceptEntries(Iterable<SnomedConcept> concepts, Map<String, SnomedDescription> descriptionsByConcept) {
		final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();

		for (final SnomedConcept conceptEntry : concepts) {
			resultBuilder.add(convertConceptEntry(conceptEntry, getTerm(descriptionsByConcept, conceptEntry.getId())));
		}

		return resultBuilder.build();
	}

	private Optional<String> getTerm(Map<String, SnomedDescription> descriptionsByConcept, final String conceptId) {
		return Optional.fromNullable(descriptionsByConcept.get(conceptId))
				.transform(new Function<SnomedDescription, String>() {
					@Override
					public String apply(SnomedDescription input) {
						return input.getTerm();
					}
				});
	}

	protected abstract Iterable<SnomedConcept> getConceptEntries(String conceptId);

	protected abstract T convertConceptEntry(SnomedConcept concept, Optional<String> optionalFsn);
}
