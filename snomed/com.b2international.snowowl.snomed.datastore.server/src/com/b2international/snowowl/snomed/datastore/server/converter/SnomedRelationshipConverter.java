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
package com.b2international.snowowl.snomed.datastore.server.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.RelationshipRefinability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

final class SnomedRelationshipConverter extends BaseSnomedComponentConverter<SnomedRelationshipIndexEntry, ISnomedRelationship, SnomedRelationships> {

	SnomedRelationshipConverter(BranchContext context, Options expand, List<ExtendedLocale> locales, final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		super(context, expand, locales, refSetMembershipLookupService);
	}

	@Override
	protected SnomedRelationships createCollectionResource(List<ISnomedRelationship> results, int offset, int limit, int total) {
		return new SnomedRelationships(results, offset, limit, total);
	}
	
	@Override
	protected ISnomedRelationship toResource(final SnomedRelationshipIndexEntry input) {
		final SnomedRelationship result = new SnomedRelationship();
		result.setActive(input.isActive());
		result.setCharacteristicType(toCharacteristicType(input.getCharacteristicTypeId()));
		result.setDestinationNegated(input.isDestinationNegated());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setGroup(input.getGroup());
		result.setId(input.getId());
		result.setModifier(toRelationshipModifier(input.isUniversal()));
		result.setModuleId(input.getModuleId());
		result.setRefinability(getRelationshipRefinability(input.getId()));
		result.setReleased(input.isReleased());
		result.setUnionGroup(input.getUnionGroup());
		result.setDestination(new SnomedConcept(input.getValueId()));
		result.setSource(new SnomedConcept(input.getObjectId()));
		result.setType(new SnomedConcept(input.getAttributeId()));
		return result;
	}
	
	@Override
	protected void expand(List<ISnomedRelationship> results) {
		if (expand().containsKey("source")) {
			final Options sourceOptions = expand().get("source", Options.class);
			final Set<String> sourceConceptIds = FluentIterable.from(results).transform(new Function<ISnomedRelationship, String>() {
				@Override
				public String apply(ISnomedRelationship input) {
					return input.getSourceId();
				}
			}).toSet();
			final SnomedConcepts sourceConcepts = SnomedRequests
				.prepareSearchConcept()
				.setLimit(sourceConceptIds.size())
				.setExpand(sourceOptions.get("expand", Options.class))
				.setComponentIds(sourceConceptIds)
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, ISnomedConcept> sourceConceptsById = Maps.uniqueIndex(sourceConcepts, ID_FUNCTION);
			for (ISnomedRelationship relationship : results) {
				final String sourceId = relationship.getSourceId();
				if (sourceConceptsById.containsKey(sourceId)) {
					final ISnomedConcept sourceConcept = sourceConceptsById.get(sourceId);
					((SnomedRelationship) relationship).setSource(sourceConcept);
				}
			}
		}
		if (expand().containsKey("destination")) {
			final Options destinationOptions = expand().get("destination", Options.class);
			final Set<String> destinationConceptIds = FluentIterable.from(results).transform(new Function<ISnomedRelationship, String>() {
				@Override
				public String apply(ISnomedRelationship input) {
					return input.getDestinationId();
				}
			}).toSet();
			final SnomedConcepts destinationConcepts = SnomedRequests
				.prepareSearchConcept()
				.setLimit(destinationConceptIds.size())
				.setExpand(destinationOptions.get("expand", Options.class))
				.setComponentIds(destinationConceptIds)
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, ISnomedConcept> destinationConceptsById = Maps.uniqueIndex(destinationConcepts, ID_FUNCTION);
			for (ISnomedRelationship relationship : results) {
				final String destinationId = relationship.getDestinationId();
				if (destinationConceptsById.containsKey(destinationId)) {
					final ISnomedConcept destinationConcept = destinationConceptsById.get(destinationId);
					((SnomedRelationship) relationship).setDestination(destinationConcept);
				}
			}
		}
		if (expand().containsKey("type")) {
			final Options typeOptions = expand().get("type", Options.class);
			final Set<String> typeConceptIds = FluentIterable.from(results).transform(new Function<ISnomedRelationship, String>() {
				@Override
				public String apply(ISnomedRelationship input) {
					return input.getTypeId();
				}
			}).toSet();
			final SnomedConcepts typeConcepts = SnomedRequests
				.prepareSearchConcept()
				.setLimit(typeConceptIds.size())
				.setExpand(typeOptions.get("expand", Options.class))
				.setComponentIds(typeConceptIds)
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, ISnomedConcept> typeConceptsById = Maps.uniqueIndex(typeConcepts, ID_FUNCTION);
			for (ISnomedRelationship relationship : results) {
				final String typeId = relationship.getTypeId();
				if (typeConceptsById.containsKey(typeId)) {
					final ISnomedConcept typeConcept = typeConceptsById.get(typeId);
					((SnomedRelationship) relationship).setType(typeConcept);
				}
			}
		}
	}
	
	private CharacteristicType toCharacteristicType(final String characteristicTypeId) {
		return CharacteristicType.getByConceptId(characteristicTypeId);
	}

	private RelationshipModifier toRelationshipModifier(final boolean universal) {
		return universal ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL;
	}

	private RelationshipRefinability getRelationshipRefinability(final String relationshipId) {
		final Collection<SnomedRefSetMemberIndexEntry> relationshipMembers = getRefSetMembershipLookupService().getRelationshipMembers(
				ImmutableSet.of(Concepts.REFSET_RELATIONSHIP_REFINABILITY),
				ImmutableSet.of(relationshipId));

		for (final SnomedRefSetMemberIndexEntry relationshipMember : relationshipMembers) {
			if (relationshipMember.isActive()) {
				return relationshipMember.getRefinability();
			}
		}

		// TODO: is this the proper fallback value?
		return RelationshipRefinability.NOT_REFINABLE;
	}
}
