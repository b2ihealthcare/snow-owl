/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.reasoner.converter;

import static com.google.common.collect.Lists.newArrayList;

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSet;
import com.b2international.snowowl.snomed.reasoner.domain.EquivalentConceptSets;
import com.b2international.snowowl.snomed.reasoner.index.EquivalentConceptSetDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 7.0
 */
public final class EquivalentConceptSetConverter 
	extends BaseResourceConverter<EquivalentConceptSetDocument, EquivalentConceptSet, EquivalentConceptSets> {

	public EquivalentConceptSetConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	@Override
	protected RepositoryContext context() {
		return (RepositoryContext) super.context();
	}

	@Override
	protected EquivalentConceptSets createCollectionResource(final List<EquivalentConceptSet> results, final String searchAfter, final int limit, final int total) {
		return new EquivalentConceptSets(results, searchAfter, limit, total);
	}

	@Override
	protected EquivalentConceptSet toResource(final EquivalentConceptSetDocument entry) {
		final EquivalentConceptSet resource = new EquivalentConceptSet();
		resource.setClassificationId(entry.getClassificationId());
		resource.setUnsatisfiable(entry.isUnsatisfiable());

		final List<SnomedConcept> items = newArrayList();
		for (final LongIterator itr = entry.getConceptIds().iterator(); itr.hasNext(); /* empty */) {
			items.add(new SnomedConcept(Long.toString(itr.next())));
		}

		final SnomedConcepts equivalentConcepts = new SnomedConcepts(items, null, items.size(), items.size());
		resource.setEquivalentConcepts(equivalentConcepts);
		return resource;
	}

	@Override
	public void expand(final List<EquivalentConceptSet> results) {
		if (!expand().containsKey(EquivalentConceptSet.Expand.EQUIVALENT_CONCEPTS)) {
			return;
		}

		final Set<String> classificationTaskIds = results.stream()
				.map(EquivalentConceptSet::getClassificationId)
				.collect(Collectors.toSet());

		final Map<String, String> branchesByClassificationIdMap = ClassificationRequests.prepareSearchClassification()
				.filterByIds(classificationTaskIds)
				.all()
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(ClassificationTask::getId, ClassificationTask::getBranch));

		final Multimap<String, EquivalentConceptSet> itemsByBranch = Multimaps.index(results, r -> branchesByClassificationIdMap.get(r.getClassificationId()));

		final Options expandOptions = expand().get(EquivalentConceptSet.Expand.EQUIVALENT_CONCEPTS, Options.class);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<EquivalentConceptSet> itemsForCurrentBranch = itemsByBranch.get(branch);

			final Set<String> conceptIds = itemsForCurrentBranch.stream()
					.flatMap(c -> c.getEquivalentConcepts().stream())
					.map(SnomedConcept::getId)
					.collect(Collectors.toSet());

			final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
					.filterByIds(conceptIds)
					.all()
					.setExpand(expandOptions.get("expand", Options.class))
					.setLocales(locales())
					.build(branch)
					.getRequest()
					.execute(context());

			final Map<String, SnomedConcept> conceptsById = Maps.uniqueIndex(concepts, SnomedConcept::getId);

			for (final EquivalentConceptSet item : itemsForCurrentBranch) {
				final List<SnomedConcept> equivalentConcepts = new ArrayList<>(item.getEquivalentConcepts().getItems());
				for (int i = 0; i < equivalentConcepts.size(); i++) {
					final SnomedConcept placeholderConcept = equivalentConcepts.get(i);
					final SnomedConcept expandedConcept = conceptsById.get(placeholderConcept.getId());
					equivalentConcepts.set(i, expandedConcept);
				}
				item.setEquivalentConcepts(new SnomedConcepts(equivalentConcepts, item.getEquivalentConcepts().getSearchAfter(), item.getEquivalentConcepts().getLimit(), item.getEquivalentConcepts().getTotal()));
			}
		}
	}
}
