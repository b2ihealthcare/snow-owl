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
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 4.0
 */
final class SnomedDescriptionConverter extends BaseRevisionResourceConverter<SnomedDescriptionIndexEntry, SnomedDescription, SnomedDescriptions> {

	SnomedDescriptionConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedDescriptions createCollectionResource(List<SnomedDescription> results, String scrollId, int limit, int total) {
		return new SnomedDescriptions(results, scrollId, limit, total);
	}
	
	@Override
	protected SnomedDescription toResource(final SnomedDescriptionIndexEntry input) {
		final SnomedDescription result = new SnomedDescription();
		result.setStorageKey(input.getStorageKey());
		result.setAcceptabilityMap(input.getAcceptabilityMap());
		result.setActive(input.isActive());
		result.setCaseSignificance(toCaseSignificance(input.getCaseSignificanceId()));
		result.setConceptId(input.getConceptId());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTime()));
		result.setId(input.getId());
		result.setLanguageCode(input.getLanguageCode());
		result.setModuleId(input.getModuleId());
		result.setIconId(input.getIconId());
		result.setReleased(input.isReleased());
		result.setTerm(input.getTerm());
		result.setTypeId(input.getTypeId());
		result.setType(new SnomedConcept(input.getTypeId()));
		result.setScore(input.getScore());
		return result;
	}
	
	@Override
	protected void expand(List<SnomedDescription> results) {
		if (expand().isEmpty()) {
			return;
		}
		
		final Set<String> descriptionIds = FluentIterable.from(results).transform(IComponent.ID_FUNCTION).toSet();
		
		expandInactivationProperties(results, descriptionIds);
		new MembersExpander(context(), expand(), locales()).expand(results, descriptionIds);
		expandConcept(results, descriptionIds);
		expandType(results, descriptionIds);
	}

	private void expandConcept(List<SnomedDescription> results, final Set<String> descriptionIds) {
		if (expand().containsKey("concept")) {
			final Options expandOptions = expand().get("concept", Options.class);
			final Set<String> conceptIds = FluentIterable.from(results)
					.transform(new Function<SnomedDescription, String>() {
						@Override public String apply(SnomedDescription input) { return input.getConceptId(); }
					})
					.toSet();
			
			final Map<String, SnomedConcept> conceptsById = getConceptMap(expandOptions, conceptIds);
			
			for (SnomedDescription description : results) {
				final SnomedConcept concept = conceptsById.get(description.getConceptId());
				((SnomedDescription) description).setConcept(concept);
			}
		}
	}
	
	private void expandType(List<SnomedDescription> results, final Set<String> descriptionIds) {
		if (expand().containsKey("type")) {
			final Options expandOptions = expand().get("type", Options.class);
			final Set<String> conceptIds = FluentIterable.from(results)
					.transform(new Function<SnomedDescription, String>() {
						@Override public String apply(SnomedDescription input) { return input.getTypeId(); }
					})
					.toSet();
			
			final Map<String, SnomedConcept> conceptsById = getConceptMap(expandOptions, conceptIds);
			
			for (SnomedDescription description : results) {
				final SnomedConcept type = conceptsById.get(description.getTypeId());
				((SnomedDescription) description).setType(type);
			}
		}
	}

	private Map<String, SnomedConcept> getConceptMap(final Options expandOptions, final Set<String> conceptIds) {
		final SnomedConcepts types = SnomedRequests
			.prepareSearchConcept()
			.filterByIds(conceptIds)
			.setLimit(conceptIds.size())
			.setLocales(locales())
			.setExpand(expandOptions.get("expand", Options.class))
			.build()
			.execute(context());
		
		final Map<String, SnomedConcept> conceptsById = Maps.uniqueIndex(types, ID_FUNCTION);
		return conceptsById;
	}

	private void expandInactivationProperties(List<SnomedDescription> results, final Set<String> descriptionIds) {
		if (expand().containsKey("inactivationProperties")) {
			new InactivationExpander<SnomedDescription>(context(), Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR) {
				@Override
				protected void setAssociationTargets(SnomedDescription result,Multimap<AssociationType, String> associationTargets) {
					((SnomedDescription) result).setAssociationTargets(associationTargets);
				}
				
				@Override
				protected void setInactivationIndicator(SnomedDescription result, String valueId) {
					((SnomedDescription) result).setInactivationIndicator(DescriptionInactivationIndicator.getInactivationIndicatorByValueId(valueId));				
				}
			}.expand(results, descriptionIds);
		}
	}

	private CaseSignificance toCaseSignificance(final String caseSignificanceId) {
		return CaseSignificance.getByConceptId(caseSignificanceId);
	}
}
