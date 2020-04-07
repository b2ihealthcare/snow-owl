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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

final class SnomedRelationshipConverter extends BaseRevisionResourceConverter<SnomedRelationshipIndexEntry, SnomedRelationship, SnomedRelationships> {

	SnomedRelationshipConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedRelationships createCollectionResource(List<SnomedRelationship> results, String searchAfter, int limit, int total) {
		return new SnomedRelationships(results, searchAfter, limit, total);
	}
	
	@Override
	protected SnomedRelationship toResource(final SnomedRelationshipIndexEntry input) {
		final SnomedRelationship result = new SnomedRelationship();
		result.setActive(input.isActive());
		result.setCharacteristicTypeId(input.getCharacteristicTypeId());
		result.setDestinationNegated(input.isDestinationNegated());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTime()));
		result.setId(input.getId());
		result.setModifierId(toRelationshipModifier(input.isUniversal()));
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
		
		final Set<String> relationshipIds = FluentIterable.from(results).transform(SnomedRelationship::getId).toSet();
		new MembersExpander(context(), expand(), locales()).expand(results, relationshipIds);
		new ModuleExpander(context(), expand(), locales()).expand(results);
		expandSource(results);
		expandDestination(results);
		expandType(results);
		expandCharacteristicType(results);
		expandModifier(results);
	}

	private void expandCharacteristicType(List<SnomedRelationship> results) {
		if (expand().containsKey(SnomedRelationship.Expand.CHARACTERISTIC_TYPE)) {
			final Options characteristicTypeOptions = expand().get(SnomedRelationship.Expand.CHARACTERISTIC_TYPE, Options.class);
			final Set<String> characteristicTypeConceptIds = FluentIterable.from(results).transform(SnomedRelationship::getCharacteristicTypeId).toSet();
			final SnomedConcepts typeConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(characteristicTypeConceptIds)
				.setLimit(characteristicTypeConceptIds.size())
				.setExpand(characteristicTypeOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> characteristicTypesById = Maps.uniqueIndex(typeConcepts, SnomedConcept::getId);
			for (SnomedRelationship relationship : results) {
				((SnomedRelationship) relationship).setCharacteristicType(characteristicTypesById.get(relationship.getCharacteristicTypeId()));
			}
		}
	}
	
	private void expandModifier(List<SnomedRelationship> results) {
		if (expand().containsKey(SnomedRelationship.Expand.MODIFIER)) {
			final Options modifierOptions = expand().get(SnomedRelationship.Expand.MODIFIER, Options.class);
			final Set<String> modifierIds = FluentIterable.from(results).transform(SnomedRelationship::getModifierId).toSet();
			final SnomedConcepts typeConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(modifierIds)
				.setLimit(modifierIds.size())
				.setExpand(modifierOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> modifiersById = Maps.uniqueIndex(typeConcepts, SnomedConcept::getId);
			for (SnomedRelationship relationship : results) {
				((SnomedRelationship) relationship).setModifier(modifiersById.get(relationship.getModifierId()));
			}
		}
	}
	
	private void expandType(List<SnomedRelationship> results) {
		if (expand().containsKey(SnomedRelationship.Expand.TYPE)) {
			final Options typeOptions = expand().get(SnomedRelationship.Expand.TYPE, Options.class);
			final Set<String> typeConceptIds = FluentIterable.from(results).transform(SnomedRelationship::getTypeId).toSet();
			final SnomedConcepts typeConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(typeConceptIds)
				.setLimit(typeConceptIds.size())
				.setExpand(typeOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> typeConceptsById = Maps.uniqueIndex(typeConcepts, SnomedConcept::getId);
			for (SnomedRelationship relationship : results) {
				final String typeId = relationship.getTypeId();
				if (typeConceptsById.containsKey(typeId)) {
					final SnomedConcept typeConcept = typeConceptsById.get(typeId);
					((SnomedRelationship) relationship).setType(typeConcept);
				}
			}
		}
	}

	private void expandDestination(List<SnomedRelationship> results) {
		if (expand().containsKey(SnomedRelationship.Expand.DESTINATION)) {
			final Options destinationOptions = expand().get(SnomedRelationship.Expand.DESTINATION, Options.class);
			final Set<String> destinationConceptIds = FluentIterable.from(results).transform(SnomedRelationship::getDestinationId).toSet();
			final SnomedConcepts destinationConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(destinationConceptIds)
				.setLimit(destinationConceptIds.size())
				.setExpand(destinationOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> destinationConceptsById = Maps.uniqueIndex(destinationConcepts, SnomedConcept::getId);
			for (SnomedRelationship relationship : results) {
				final String destinationId = relationship.getDestinationId();
				if (destinationConceptsById.containsKey(destinationId)) {
					final SnomedConcept destinationConcept = destinationConceptsById.get(destinationId);
					((SnomedRelationship) relationship).setDestination(destinationConcept);
				}
			}
		}
	}

	private void expandSource(List<SnomedRelationship> results) {
		if (expand().containsKey(SnomedRelationship.Expand.SOURCE)) {
			final Options sourceOptions = expand().get(SnomedRelationship.Expand.SOURCE, Options.class);
			final Set<String> sourceConceptIds = FluentIterable.from(results).transform(SnomedRelationship::getSourceId).toSet();
			final SnomedConcepts sourceConcepts = SnomedRequests
				.prepareSearchConcept()
				.filterByIds(sourceConceptIds)
				.setLimit(sourceConceptIds.size())
				.setExpand(sourceOptions.get("expand", Options.class))
				.setLocales(locales())
				.build()
				.execute(context());
			final Map<String, SnomedConcept> sourceConceptsById = Maps.uniqueIndex(sourceConcepts, SnomedConcept::getId);
			for (SnomedRelationship relationship : results) {
				final String sourceId = relationship.getSourceId();
				if (sourceConceptsById.containsKey(sourceId)) {
					final SnomedConcept sourceConcept = sourceConceptsById.get(sourceId);
					((SnomedRelationship) relationship).setSource(sourceConcept);
				}
			}
		}
	}
	
	private String toRelationshipModifier(final boolean universal) {
		return universal ? Concepts.UNIVERSAL_RESTRICTION_MODIFIER : Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
	}
}
