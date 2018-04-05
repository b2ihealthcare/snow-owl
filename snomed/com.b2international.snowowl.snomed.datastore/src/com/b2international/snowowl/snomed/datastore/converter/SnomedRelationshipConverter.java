/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import static com.b2international.snowowl.core.domain.IComponent.ID_FUNCTION;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

final class SnomedRelationshipConverter extends BaseRevisionResourceConverter<SnomedRelationshipIndexEntry, SnomedRelationship, SnomedRelationships> {

	SnomedRelationshipConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedRelationships createCollectionResource(List<SnomedRelationship> results, String scrollId, Object[] searchAfter, int limit, int total) {
		return new SnomedRelationships(results, scrollId, searchAfter, limit, total);
	}
	
	@Override
	protected SnomedRelationship toResource(final SnomedRelationshipIndexEntry input) {
		final SnomedRelationship result = new SnomedRelationship();
		result.setStorageKey(input.getStorageKey());
		result.setActive(input.isActive());
		result.setCharacteristicType(toCharacteristicType(input.getCharacteristicTypeId()));
		result.setDestinationNegated(input.isDestinationNegated());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTime()));
		result.setId(input.getId());
		result.setModifier(toRelationshipModifier(input.isUniversal()));
		result.setModuleId(input.getModuleId());
		result.setIconId(input.getIconId());
		result.setReleased(input.isReleased());
		result.setGroup(input.getGroup());
		result.setUnionGroup(input.getUnionGroup());
		result.setDestination(new SnomedConcept(input.getDestinationId()));
		result.setSource(new SnomedConcept(input.getSourceId()));
		result.setType(new SnomedConcept(input.getTypeId()));
		result.setScore(input.getScore());
		return result;
	}
	
	@Override
	protected void expand(List<SnomedRelationship> results) {
		if (expand().isEmpty()) {
			return;
		}
		
		final Set<String> relationshipIds = FluentIterable.from(results).transform(ID_FUNCTION).toSet();
		new MembersExpander(context(), expand(), locales()).expand(results, relationshipIds);
		if (expand().containsKey("source")) {
			final Options sourceOptions = expand().get("source", Options.class);
			final Set<String> sourceConceptIds = FluentIterable.from(results).transform(new Function<SnomedRelationship, String>() {
				@Override
				public String apply(SnomedRelationship input) {
					return input.getSourceId();
				}
			}).toSet();
			final SnomedConcepts sourceConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(sourceConceptIds)
				.setLimit(sourceConceptIds.size())
				.setExpand(sourceOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> sourceConceptsById = Maps.uniqueIndex(sourceConcepts, ID_FUNCTION);
			for (SnomedRelationship relationship : results) {
				final String sourceId = relationship.getSourceId();
				if (sourceConceptsById.containsKey(sourceId)) {
					final SnomedConcept sourceConcept = sourceConceptsById.get(sourceId);
					((SnomedRelationship) relationship).setSource(sourceConcept);
				}
			}
		}
		if (expand().containsKey("destination")) {
			final Options destinationOptions = expand().get("destination", Options.class);
			final Set<String> destinationConceptIds = FluentIterable.from(results).transform(new Function<SnomedRelationship, String>() {
				@Override
				public String apply(SnomedRelationship input) {
					return input.getDestinationId();
				}
			}).toSet();
			final SnomedConcepts destinationConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(destinationConceptIds)
				.setLimit(destinationConceptIds.size())
				.setExpand(destinationOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> destinationConceptsById = Maps.uniqueIndex(destinationConcepts, ID_FUNCTION);
			for (SnomedRelationship relationship : results) {
				final String destinationId = relationship.getDestinationId();
				if (destinationConceptsById.containsKey(destinationId)) {
					final SnomedConcept destinationConcept = destinationConceptsById.get(destinationId);
					((SnomedRelationship) relationship).setDestination(destinationConcept);
				}
			}
		}
		if (expand().containsKey("type")) {
			final Options typeOptions = expand().get("type", Options.class);
			final Set<String> typeConceptIds = FluentIterable.from(results).transform(new Function<SnomedRelationship, String>() {
				@Override
				public String apply(SnomedRelationship input) {
					return input.getTypeId();
				}
			}).toSet();
			final SnomedConcepts typeConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(typeConceptIds)
				.setLimit(typeConceptIds.size())
				.setExpand(typeOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> typeConceptsById = Maps.uniqueIndex(typeConcepts, ID_FUNCTION);
			for (SnomedRelationship relationship : results) {
				final String typeId = relationship.getTypeId();
				if (typeConceptsById.containsKey(typeId)) {
					final SnomedConcept typeConcept = typeConceptsById.get(typeId);
					((SnomedRelationship) relationship).setType(typeConcept);
				}
			}
		}
	}
	
	private CharacteristicType toCharacteristicType(final String characteristicTypeId) {
		if (Strings.isNullOrEmpty(characteristicTypeId)) {
			return null;
		} else {
			return CharacteristicType.getByConceptId(characteristicTypeId);
		}
	}

	private RelationshipModifier toRelationshipModifier(final boolean universal) {
		return universal ? RelationshipModifier.UNIVERSAL : RelationshipModifier.EXISTENTIAL;
	}

}
