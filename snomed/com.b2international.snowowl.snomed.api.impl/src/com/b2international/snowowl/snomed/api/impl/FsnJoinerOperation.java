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
package com.b2international.snowowl.snomed.api.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Given a set of {@link SnomedConceptIndexEntry concept index entries}, collects the corresponding "preferred" fully
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
		Collection<SnomedConceptIndexEntry> conceptEntries = getConceptEntries(conceptId);
		if (conceptEntries.isEmpty()) {
			return ImmutableList.of();
		}
		
		Map<String, ISnomedDescription> descriptionsByConcept = initDescriptionsByConcept(conceptEntries);
		return convertConceptEntries(conceptEntries, descriptionsByConcept);
	}

	private Map<String, ISnomedDescription> initDescriptionsByConcept(Collection<SnomedConceptIndexEntry> conceptEntries) {
		final Set<String> conceptIds = ComponentUtils.getIdSet(conceptEntries);
		return descriptionService.getFullySpecifiedNames(conceptIds, locales);
	}

	private List<T> convertConceptEntries(Collection<SnomedConceptIndexEntry> conceptEntries, Map<String, ISnomedDescription> descriptionsByConcept) {
		final ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();

		for (final SnomedConceptIndexEntry conceptEntry : conceptEntries) {
			resultBuilder.add(convertConceptEntry(conceptEntry, getTerm(descriptionsByConcept, conceptEntry.getId())));
		}

		return resultBuilder.build();
	}

	private Optional<String> getTerm(Map<String, ISnomedDescription> descriptionsByConcept, final String conceptId) {
		return Optional.fromNullable(descriptionsByConcept.get(conceptId))
				.transform(new Function<ISnomedDescription, String>() {
					@Override
					public String apply(ISnomedDescription input) {
						return input.getTerm();
					}
				});
	}

	protected abstract Collection<SnomedConceptIndexEntry> getConceptEntries(String conceptId);

	protected abstract T convertConceptEntry(SnomedConceptIndexEntry conceptEntry, Optional<String> optionalFsn);
}
