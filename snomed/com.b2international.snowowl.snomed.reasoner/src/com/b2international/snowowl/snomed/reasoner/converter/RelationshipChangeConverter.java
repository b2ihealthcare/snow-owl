/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.converter.BaseResourceConverter;
import com.b2international.snowowl.datastore.request.BranchRequest;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationTask;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerRelationship;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChange;
import com.b2international.snowowl.snomed.reasoner.domain.RelationshipChanges;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 6.11 (originally introduced on 7.0)
 */
public final class RelationshipChangeConverter 
extends BaseResourceConverter<RelationshipChangeDocument, RelationshipChange, RelationshipChanges> {

	// TODO: these constants are moved to SnomedRelationship.Expand on 7.x
	private static final String SOURCE = "source";
	private static final String TYPE = "type";
	private static final String DESTINATION = "destination";
	private static final String MEMBERS = "members";

	public RelationshipChangeConverter(final RepositoryContext context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected RelationshipChanges createCollectionResource(final List<RelationshipChange> results, 
			final String scrollId, 
			final String searchAfter, 
			final int limit, 
			final int total) {

		return new RelationshipChanges(results, scrollId, searchAfter, limit, total);
	}

	@Override
	protected RelationshipChange toResource(final RelationshipChangeDocument entry) {
		final RelationshipChange resource = new RelationshipChange();
		resource.setClassificationId(entry.getClassificationId());
		resource.setChangeNature(entry.getNature());

		/*
		 * Inferred IS A relationships: ID is null (information is coming from the reasoner)
		 * Inferred non-IS A relationships: ID refers to the "origin" relationship's ID
		 * Updated relationships: ID refers to the relationship that should be updated in place 
		 * Redundant relationships: ID refers to the relationship that should be removed or deactivated
		 */
		final ReasonerRelationship relationship = new ReasonerRelationship(entry.getRelationshipId());
		
		// Released flag is the "origin" relationship's released state for updated and redundant relationships, false for new relationships
		relationship.setReleased(entry.isReleased());
		// All three change nature literals require the sourceId to be set, because we might need to update the moduleId based on the source concept
		relationship.setSourceId(entry.getSourceId());
		
		switch (entry.getNature()) {
			case NEW:
				/* 
				 * Inferences carry information about:
				 * - source
				 * - group
				 * - union group
				 * - characteristic type
				 * 
				 * The values will be different on the "origin" relationship, so make note of these here.
				 */
				relationship.setGroup(entry.getGroup());
				relationship.setUnionGroup(entry.getUnionGroup());
				relationship.setCharacteristicType(CharacteristicType.getByConceptId(entry.getCharacteristicTypeId()));
				
				/*
				 * Inferred IS A relationships have even more stored information, which we set on the response object.
				 */
				if (entry.getRelationshipId() == null) {
					relationship.setTypeId(entry.getTypeId());
					relationship.setDestinationId(entry.getDestinationId());
				}
				break;
				
			case UPDATED:
				// Updates change the group on an existing relationship
				relationship.setGroup(entry.getGroup());
				break;
				
			case REDUNDANT:
				// Redundant relationships only need the SCTID and released flag populated to do the delete/inactivation
				break;
				
			default:
				throw new IllegalStateException(String.format("Unexpected relationship change '%s' found with SCTID '%s'.", 
						entry.getNature(), 
						entry.getRelationshipId()));
		}

		resource.setRelationship(relationship);
		return resource;
	}

	@Override
	protected void expand(final List<RelationshipChange> results) {
		if (!expand().containsKey(RelationshipChange.Expand.RELATIONSHIP)) {
			return;
		}

		/*
		 * Depending on the relationship change search request, we might need to issue
		 * SNOMED CT searches against multiple branches; find out which ones we have.
		 */
		final Multimap<String, RelationshipChange> itemsByBranch = getItemsByBranch(results);

		// Check if we only need to load inferred relationships in their entirety
		final Options expandOptions = expand().getOptions(RelationshipChange.Expand.RELATIONSHIP);
		final boolean inferredOnly = expandOptions.getBoolean("inferredOnly");
		
		final Options relationshipExpandOptions = expandOptions.getOptions("expand");
		
		final Options sourceOptions = relationshipExpandOptions.getOptions(SOURCE);
		final Options typeOptions = relationshipExpandOptions.getOptions(TYPE);
		final Options destinationOptions = relationshipExpandOptions.getOptions(DESTINATION);

		final boolean needsSource = relationshipExpandOptions.keySet().contains(SOURCE);
		final boolean needsType = relationshipExpandOptions.keySet().contains(TYPE);
		final boolean needsDestination = relationshipExpandOptions.keySet().contains(DESTINATION);
		
		// Do not allow expansion of members
		final boolean needsMembers = relationshipExpandOptions.keySet().contains(MEMBERS);
		if (needsMembers) {
			throw new BadRequestException("Members can not be expanded on reasoner relationship changes.");
		}
		
		for (final String branch : itemsByBranch.keySet()) {
			final Collection<RelationshipChange> itemsForCurrentBranch = itemsByBranch.get(branch);

			/*
			 * Expand concepts on the relationship currently set on each item first, as they
			 * might have changed when compared to the "origin" relationship.
			 */
			if (needsSource) {
				expandConcepts(branch, 
						itemsForCurrentBranch, 
						sourceOptions,
						ReasonerRelationship::getSourceId,
						ReasonerRelationship::setSource);
			}

			if (needsType) {
				expandConcepts(branch, 
						itemsForCurrentBranch, 
						typeOptions, 
						ReasonerRelationship::getTypeId,
						ReasonerRelationship::setType);
			}

			if (needsDestination) {
				expandConcepts(branch, 
						itemsForCurrentBranch, 
						destinationOptions, 
						ReasonerRelationship::getDestinationId,
						ReasonerRelationship::setDestination);
			}

			// Now fetch the rest of the properties for the relationships (except IS As where no ID is recorded)
			final Set<String> relationshipIds = itemsForCurrentBranch.stream()
					.filter(c -> !inferredOnly || ChangeNature.NEW.equals(c.getChangeNature()))
					.map(c -> c.getRelationship().getOriginId())
					.filter(id -> id != null)
					.collect(Collectors.toSet());

			final Request<BranchContext, SnomedRelationships> relationshipSearchRequest = SnomedRequests.prepareSearchRelationship()
					.filterByIds(relationshipIds)
					.setLimit(relationshipIds.size())
					.setExpand(relationshipExpandOptions)
					.setLocales(locales())
					.build();

			final SnomedRelationships relationships = new BranchRequest<>(branch, 
					new RevisionIndexReadRequest<>(relationshipSearchRequest))
					.execute(context());

			final Map<String, SnomedRelationship> relationshipsById = Maps.uniqueIndex(relationships, 
					SnomedRelationship::getId);

			for (final RelationshipChange item : itemsForCurrentBranch) {
				final ReasonerRelationship reasonerRelationship = item.getRelationship();
				final String originId = reasonerRelationship.getOriginId();
				
				switch (item.getChangeNature()) {
					case NEW: 
						if (originId == null) {
							
							// reasonerRelationship.setCharacteristicType(...) is already set
							// reasonerRelationship.setDestination(...) is already set
							reasonerRelationship.setDestinationNegated(false);
							// reasonerRelationship.setGroup(...) is already set
							reasonerRelationship.setModifier(RelationshipModifier.EXISTENTIAL);
							// reasonerRelationship.setReleased(...) is already set
							// reasonerRelationship.setSource(...) is already set
							// reasonerRelationship.setType(...) is already set
							// reasonerRelationship.setUnionGroup(...) is already set
							
						} else {
						
							final SnomedRelationship expandedRelationship = relationshipsById.get(originId);
							
							// reasonerRelationship.setCharacteristicType(...) is already set
							reasonerRelationship.setDestination(expandedRelationship.getDestination());
							reasonerRelationship.setDestinationNegated(expandedRelationship.isDestinationNegated());
							// reasonerRelationship.setGroup(...) is already set
							reasonerRelationship.setModifier(expandedRelationship.getModifier());
							// reasonerRelationship.setReleased(...) is already set
							// reasonerRelationship.setSource(...) is already set
							reasonerRelationship.setType(expandedRelationship.getType());
							// reasonerRelationship.setUnionGroup(...) is already set
						}
						break;
						
					case UPDATED:
						if (!inferredOnly) {
							final SnomedRelationship expandedRelationship = relationshipsById.get(originId);

							reasonerRelationship.setCharacteristicType(expandedRelationship.getCharacteristicType());
							reasonerRelationship.setDestination(expandedRelationship.getDestination());
							reasonerRelationship.setDestinationNegated(expandedRelationship.isDestinationNegated());
							// reasonerRelationship.setGroup(...) is already set
							reasonerRelationship.setModifier(expandedRelationship.getModifier());
							// reasonerRelationship.setReleased(...) is already set
							reasonerRelationship.setSource(expandedRelationship.getSource());
							reasonerRelationship.setType(expandedRelationship.getType());
							reasonerRelationship.setUnionGroup(expandedRelationship.getUnionGroup());
						}
						break;
						
					case REDUNDANT:
						if (!inferredOnly) {
							final SnomedRelationship expandedRelationship = relationshipsById.get(originId);

							reasonerRelationship.setCharacteristicType(expandedRelationship.getCharacteristicType());
							reasonerRelationship.setDestination(expandedRelationship.getDestination());
							reasonerRelationship.setDestinationNegated(expandedRelationship.isDestinationNegated());
							reasonerRelationship.setGroup(expandedRelationship.getGroup());
							reasonerRelationship.setModifier(expandedRelationship.getModifier());
							// reasonerRelationship.setReleased(...) is already set
							reasonerRelationship.setSource(expandedRelationship.getSource());
							reasonerRelationship.setType(expandedRelationship.getType());
							reasonerRelationship.setUnionGroup(expandedRelationship.getUnionGroup());						}
						break;
						
					default:
						throw new IllegalStateException(String.format("Unexpected relationship change '%s' found with SCTID '%s'.", 
								item.getChangeNature(), 
								item.getRelationship().getOriginId()));
				}
			}
		}
	}

	private Multimap<String, RelationshipChange> getItemsByBranch(final List<RelationshipChange> results) {
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

		final Multimap<String, RelationshipChange> itemsByBranch = Multimaps.index(results, 
				r -> branchesByClassificationIdMap.get(r.getClassificationId()));
		
		return itemsByBranch;
	}

	private void expandConcepts(final String branch, 
			final Collection<RelationshipChange> relationshipChanges,
			final Options options,
			final Function<ReasonerRelationship, String> conceptIdFunction,
			final BiConsumer<ReasonerRelationship, SnomedConcept> conceptIdConsumer) {

		final List<ReasonerRelationship> blankRelationships = relationshipChanges.stream()
				.filter(c -> ChangeNature.NEW.equals(c.getChangeNature()))
				.map(RelationshipChange::getRelationship)
				.collect(Collectors.toList());

		final Multimap<String, ReasonerRelationship> relationshipsByConceptId = FluentIterable.from(blankRelationships)
				.filter(r -> conceptIdFunction.apply(r) != null)
				.index(conceptIdFunction);

		final Set<String> conceptIds = relationshipsByConceptId.keySet();

		final Request<BranchContext, SnomedConcepts> conceptSearchRequest = SnomedRequests.prepareSearchConcept()
				.filterByIds(conceptIds)
				.setLimit(conceptIds.size())
				.setExpand(options.get("expand", Options.class))
				.setLocales(locales())
				.build();

		final SnomedConcepts concepts = new BranchRequest<>(branch,
				new RevisionIndexReadRequest<>(conceptSearchRequest))
					.execute(context());

		for (final SnomedConcept concept : concepts) {
			final String conceptId = concept.getId();
			final Collection<ReasonerRelationship> relationshipsForConcept = relationshipsByConceptId.get(conceptId);

			for (final ReasonerRelationship relationship : relationshipsForConcept) {
				conceptIdConsumer.accept(relationship, concept);
			}
		}
	}
}
