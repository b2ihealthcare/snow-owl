/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ConceptChange;
import com.b2international.snowowl.snomed.reasoner.domain.ConceptChanges;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerConcept;
import com.b2international.snowowl.snomed.reasoner.index.ConceptChangeDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.14
 */
public final class ConceptChangeConverter 
		extends BaseResourceConverter<ConceptChangeDocument, ConceptChange, ConceptChanges> {

	private static final Collection<String> CONCEPT_EXPAND_OPTIONS = ImmutableSet.of(SnomedConcept.Expand.FULLY_SPECIFIED_NAME, 
			SnomedConcept.Expand.PREFERRED_TERM);

	public ConceptChangeConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected ConceptChanges createCollectionResource(final List<ConceptChange> results, 
			final String scrollId, 
			final String searchAfter, 
			final int limit, 
			final int total) {

		return new ConceptChanges(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected ConceptChange toResource(final ConceptChangeDocument entry) {
		final ConceptChange resource = new ConceptChange();
		resource.setClassificationId(entry.getClassificationId());
		resource.setChangeNature(entry.getNature());

		final ReasonerConcept concept = new ReasonerConcept(entry.getConceptId());
		
		// Released flag is the "origin" concept's released state
		concept.setReleased(entry.isReleased());

		if (!ChangeNature.REDUNDANT.equals(entry.getNature())) {
			throw new IllegalStateException(String.format("Unexpected concept change '%s' found with SCTID '%s'.", 
					entry.getNature(), 
					entry.getConceptId()));
		}
		
		resource.setConcept(concept);
		return resource;
	}

	@Override
	protected void expand(final List<ConceptChange> results) {
		if (!expand().containsKey(ConceptChange.Expand.CONCEPT)) {
			return;
		}

		/*
		 * Depending on the concept change search request, we might need to issue
		 * SNOMED CT searches against multiple branches; find out which ones we have.
		 */
		final Multimap<String, ConceptChange> itemsByBranch = getItemsByBranch(results);

		// Check if we only need to load inferred concepts in their entirety
		final Options expandOptions = expand().getOptions(ConceptChange.Expand.CONCEPT);

		// pt() and fsn() are the only useful options here
		final Options conceptExpandOptions = expandOptions.getOptions("expand");
		conceptExpandOptions.keySet().retainAll(CONCEPT_EXPAND_OPTIONS);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<ConceptChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			final Set<String> conceptIds = itemsForCurrentBranch.stream()
					.map(c -> c.getConcept().getOriginConceptId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedConcepts> conceptSearchRequest = SnomedRequests.prepareSearchConcept()
					.filterByIds(conceptIds)
					.setLimit(conceptIds.size())
					.setExpand(conceptExpandOptions)
					.setLocales(locales())
					.build();

			final SnomedConcepts concepts = new BranchRequest<>(branch, 
					new RevisionIndexReadRequest<>(conceptSearchRequest))
					.execute(context());

			final Map<String, SnomedConcept> conceptsById = Maps.uniqueIndex(concepts, SnomedConcept::getId);

			for (final ConceptChange item : itemsForCurrentBranch) {
				final ReasonerConcept reasonerConcept = item.getConcept();
				final String conceptId = reasonerConcept.getOriginConceptId();

				final SnomedConcept expandedConcept = conceptsById.get(conceptId);

				reasonerConcept.setDefinitionStatus(expandedConcept.getDefinitionStatus());
				reasonerConcept.setPt(expandedConcept.getPt());
				reasonerConcept.setFsn(expandedConcept.getFsn());
				// reasonerConcept.setReleased(...) is already set
			}
		}
	}

	private Multimap<String, ConceptChange> getItemsByBranch(final List<ConceptChange> results) {
		final Set<String> classificationTaskIds = results.stream()
				.map(ConceptChange::getClassificationId)
				.collect(Collectors.toSet());

		final Map<String, String> branchesByClassificationIdMap = ClassificationRequests.prepareSearchClassification()
				.filterByIds(classificationTaskIds)
				.setLimit(classificationTaskIds.size())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(
						ClassificationTask::getId, 
						ClassificationTask::getBranch));

		final Multimap<String, ConceptChange> itemsByBranch = Multimaps.index(results, 
				r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		
		return itemsByBranch;
	}
}
