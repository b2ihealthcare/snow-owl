/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 7.0
 */
public final class RelationshipChangeConverter 
		extends BaseResourceConverter<RelationshipChangeDocument, RelationshipChange, RelationshipChanges> {

	public RelationshipChangeConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected RelationshipChanges createCollectionResource(final List<RelationshipChange> results, 
			final String scrollId, 
			final Object[] searchAfter, 
			final int limit, 
			final int total) {

		return new RelationshipChanges(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected RelationshipChange toResource(final RelationshipChangeDocument entry) {
		final RelationshipChange resource = new RelationshipChange();
		resource.setClassificationId(entry.getClassificationId());
		resource.setChangeNature(entry.getNature());

		final SnomedRelationship relationship = new SnomedRelationship(entry.getRelationshipId());
		relationship.setSourceId(entry.getSourceId());
		
		if (ChangeNature.INFERRED.equals(entry.getNature())) {
			relationship.setGroup(entry.getGroup());
			relationship.setUnionGroup(entry.getUnionGroup());
		}

		resource.setRelationship(relationship);
		return resource;
	}

	@Override
	protected void expand(final List<RelationshipChange> results) {
		if (!expand().containsKey(RelationshipChange.Expand.RELATIONSHIP)) {
			return;
		}

		final Set<String> classificationTaskIds = results.stream()
				.map(RelationshipChange::getClassificationId)
				.collect(Collectors.toSet());

		final Map<String, String> branchesByClassificationIdMap = ClassificationRequests.prepareSearchClassification()
				.filterByIds(classificationTaskIds)
				.all()
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(ClassificationTask::getId, ClassificationTask::getBranch));

		final Multimap<String, RelationshipChange> itemsByBranch = Multimaps.index(results, r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		final Options expandOptions = expand().get(RelationshipChange.Expand.RELATIONSHIP, Options.class);
		final Options sourceOptions = expandOptions.getOptions(SnomedRelationship.Expand.SOURCE);
		final boolean needsSource = expandOptions.keySet().remove(SnomedRelationship.Expand.SOURCE);

		for (final String branch : itemsByBranch.keySet()) {
			final Collection<RelationshipChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			// Expand source concept on the initial, "blank" relationship of each item
			if (needsSource) {
				final List<SnomedRelationship> blankRelationships = itemsForCurrentBranch.stream()
						.map(RelationshipChange::getRelationship)
						.collect(Collectors.toList());

				final Multimap<String, SnomedRelationship> relationshipsBySource = Multimaps.index(blankRelationships, m -> m.getSourceId());
				final Set<String> sourceIds = relationshipsBySource.keySet();

				final Request<BranchContext, SnomedConcepts> sourceConceptSearchRequest = SnomedRequests.prepareSearchConcept()
						.filterByIds(sourceIds)
						.all()
						.setExpand(sourceOptions.get("expand", Options.class))
						.setLocales(locales())
						.build();

				final SnomedConcepts sourceConcepts = new BranchRequest<>(branch, sourceConceptSearchRequest).execute(context());

				for (final SnomedConcept concept : sourceConcepts) {
					for (final SnomedRelationship relationship : relationshipsBySource.get(concept.getId())) {
						relationship.setSource(concept);
					}
				}
			}

			// Then fetch all relationships (these will have a source ID that is no longer valid for inferred members)
			final Set<String> relationshipIds = itemsForCurrentBranch.stream()
					.map(c -> c.getRelationship().getId())
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedRelationships> relationshipSearchRequest = SnomedRequests.prepareSearchRelationship()
					.filterByIds(relationshipIds)
					.all()
					.setExpand(expandOptions.get("expand", Options.class))
					.setLocales(locales())
					.build();

			final SnomedRelationships relationships = new BranchRequest<>(branch, relationshipSearchRequest).execute(context());
			final Map<String, SnomedRelationship> relationshipsById = Maps.uniqueIndex(relationships, SnomedRelationship::getId);

			// Finally, set the relationship on the change item, but preserve the "adjusted" source concept that holds the inferred component target's ID
			for (final RelationshipChange item : itemsForCurrentBranch) {
				final SnomedRelationship blankRelationship = item.getRelationship();
				final String relationshipId = blankRelationship.getId();
				final SnomedConcept adjustedSource = blankRelationship.getSource();
				final SnomedRelationship expandedRelationship = relationshipsById.get(relationshipId);

				expandedRelationship.setSource(adjustedSource);
				
				if (ChangeNature.INFERRED.equals(item.getChangeNature())) {
					expandedRelationship.setGroup(blankRelationship.getGroup());
					expandedRelationship.setUnionGroup(blankRelationship.getUnionGroup());
				}

				item.setRelationship(expandedRelationship);
			}
		}
	}
}
