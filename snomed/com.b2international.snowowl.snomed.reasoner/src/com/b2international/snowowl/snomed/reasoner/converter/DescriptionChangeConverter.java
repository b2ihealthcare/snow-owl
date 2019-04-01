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
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.DescriptionChange;
import com.b2international.snowowl.snomed.reasoner.domain.DescriptionChanges;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerDescription;
import com.b2international.snowowl.snomed.reasoner.index.DescriptionChangeDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.14
 */
public final class DescriptionChangeConverter 
extends BaseResourceConverter<DescriptionChangeDocument, DescriptionChange, DescriptionChanges> {

	public DescriptionChangeConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected DescriptionChanges createCollectionResource(final List<DescriptionChange> results, 
			final String scrollId, 
			final String searchAfter, 
			final int limit, 
			final int total) {

		return new DescriptionChanges(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected DescriptionChange toResource(final DescriptionChangeDocument entry) {
		final DescriptionChange resource = new DescriptionChange();
		resource.setClassificationId(entry.getClassificationId());
		resource.setChangeNature(entry.getNature());

		/*
		 * Inferred descriptions: ID refers to the "origin" description ID 
		 * Redundant descriptions: ID refers to the description that should be removed or deactivated
		 */
		final ReasonerDescription description = new ReasonerDescription(entry.getDescriptionId());
		description.setReleased(entry.isReleased());

		switch (entry.getNature()) {
			case NEW:
				// New descriptions are describing a different concept 
				description.setConceptId(entry.getConceptId());
				break;
	
			case REDUNDANT:
				// Redundant descriptions only need the ID and released flag populated to do the delete/inactivation
				break;
	
			default:
				throw new IllegalStateException(String.format("Unexpected description change '%s' found with SCTID '%s'.", 
						entry.getNature(), 
						entry.getDescriptionId()));
		}

		resource.setDescription(description);
		return resource;
	}

	@Override
	protected void expand(final List<DescriptionChange> results) {
		if (!expand().containsKey(DescriptionChange.Expand.DESCRIPTION)) {
			return;
		}

		/*
		 * Depending on the CD member change search request, we might need to issue
		 * SNOMED CT searches against multiple branches; find out which ones we have.
		 */
		final Multimap<String, DescriptionChange> itemsByBranch = getItemsByBranch(results);

		// Check if we only need to load inferred CD members in their entirety
		final Options expandOptions = expand().getOptions(DescriptionChange.Expand.DESCRIPTION);
		final boolean inferredOnly = expandOptions.getBoolean("inferredOnly");

		final Options descriptionExpandOptions = expandOptions.getOptions("expand");
		final Options conceptOptions = descriptionExpandOptions.getOptions("concept");
		final boolean needsConcept = descriptionExpandOptions.keySet().contains("concept");

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<DescriptionChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			/*
			 * Expand concept on "new" descriptions via a separate search request, they will
			 * be different from the concept on the "origin" description.
			 */
			if (needsConcept) {
				final List<ReasonerDescription> blankDescriptions = itemsForCurrentBranch.stream()
						.filter(c -> ChangeNature.NEW.equals(c.getChangeNature()))
						.map(DescriptionChange::getDescription)
						.collect(Collectors.toList());
	
				final Multimap<String, ReasonerDescription> descriptionsByConceptId = FluentIterable.from(blankDescriptions)
						.index(ReasonerDescription::getConceptId);
	
				final Set<String> conceptIds = descriptionsByConceptId.keySet();

				final Request<BranchContext, SnomedConcepts> conceptSearchRequest = SnomedRequests.prepareSearchConcept()
						.filterByIds(conceptIds)
						.setLimit(conceptIds.size())
						.setExpand(conceptOptions.get("expand", Options.class))
						.setLocales(locales())
						.build();
	
				final SnomedConcepts concepts = new BranchRequest<>(branch,
						new RevisionIndexReadRequest<>(conceptSearchRequest))
							.execute(context());
	
				for (final SnomedConcept concept : concepts) {
					final String conceptId = concept.getId();
					final Collection<ReasonerDescription> descriptionsForConcept = descriptionsByConceptId.get(conceptId);
	
					for (final ReasonerDescription description : descriptionsForConcept) {
						description.setConcept(concept);
					}
				}
			}
			
			/*
			 * Then fetch all the required descriptions. Note that the same "origin"
			 * description might be used for multiple eg. "new" counterparts.
			 */
			final Set<String> descriptionIds = itemsForCurrentBranch.stream()
					.filter(c -> !inferredOnly || ChangeNature.NEW.equals(c.getChangeNature()))
					.map(c -> c.getDescription().getOriginDescriptionId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedDescriptions> descriptionSearchRequest = SnomedRequests.prepareSearchDescription()
					.filterByIds(descriptionIds)
					.setLimit(descriptionIds.size())
					.setExpand(descriptionExpandOptions)
					.setLocales(locales())
					.build();

			final SnomedDescriptions descriptions = new BranchRequest<>(branch, 
					new RevisionIndexReadRequest<>(descriptionSearchRequest))
						.execute(context());

			final Map<String, SnomedDescription> descriptionsById = Maps.uniqueIndex(descriptions, SnomedDescription::getId);

			for (final DescriptionChange item : itemsForCurrentBranch) {
				final ReasonerDescription reasonerDescription = item.getDescription();
				final String descriptionId = reasonerDescription.getOriginDescriptionId();

				switch (item.getChangeNature()) {
					case NEW: {
						final SnomedDescription expandedDescription = descriptionsById.get(descriptionId);
	
						reasonerDescription.setAcceptabilityMap(expandedDescription.getAcceptabilityMap());
						reasonerDescription.setCaseSignificance(expandedDescription.getCaseSignificance());
						// reasonerDescription.setConcept(...) is already set earlier (or expanded)
						reasonerDescription.setLanguageCode(expandedDescription.getLanguageCode());
						// reasonerMember.setReleased(...) is already set
						reasonerDescription.setTerm(expandedDescription.getTerm());
						reasonerDescription.setType(expandedDescription.getType());
					}
					break;
	
					case REDUNDANT:
						if (!inferredOnly) {
							final SnomedDescription expandedDescription = descriptionsById.get(descriptionId);
	
							reasonerDescription.setAcceptabilityMap(expandedDescription.getAcceptabilityMap());
							reasonerDescription.setCaseSignificance(expandedDescription.getCaseSignificance());
							reasonerDescription.setConcept(expandedDescription.getConcept());
							reasonerDescription.setLanguageCode(expandedDescription.getLanguageCode());
							// reasonerMember.setReleased(...) is already set
							reasonerDescription.setTerm(expandedDescription.getTerm());
							reasonerDescription.setType(expandedDescription.getType());
						}
						break;
	
					default:
						throw new IllegalStateException(String.format("Unexpected description change '%s' found with SCTID '%s'.", 
								item.getChangeNature(), 
								item.getDescription().getOriginDescriptionId()));
				}
			}
		}
	}

	private Multimap<String, DescriptionChange> getItemsByBranch(final List<DescriptionChange> results) {
		final Set<String> classificationTaskIds = results.stream()
				.map(DescriptionChange::getClassificationId)
				.collect(Collectors.toSet());

		final Map<String, String> branchesByClassificationIdMap = ClassificationRequests.prepareSearchClassification()
				.filterByIds(classificationTaskIds)
				.all()
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(
						ClassificationTask::getId, 
						ClassificationTask::getBranch));

		final Multimap<String, DescriptionChange> itemsByBranch = Multimaps.index(results, 
				r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		
		return itemsByBranch;
	}
}
