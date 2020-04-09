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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.BaseRevisionResourceConverter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AcceptabilityMembership;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @since 4.0
 */
final class SnomedDescriptionConverter extends BaseRevisionResourceConverter<SnomedDescriptionIndexEntry, SnomedDescription, SnomedDescriptions> {

	SnomedDescriptionConverter(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}

	@Override
	protected SnomedDescriptions createCollectionResource(List<SnomedDescription> results, String searchAfter, int limit, int total) {
		return new SnomedDescriptions(results, searchAfter, limit, total);
	}
	
	@Override
	protected SnomedDescription toResource(final SnomedDescriptionIndexEntry input) {
		final SnomedDescription result = new SnomedDescription();
		result.setAcceptabilityMap(input.getAcceptabilityMap());
		result.setActive(input.isActive());
		result.setCaseSignificanceId(input.getCaseSignificanceId());
		result.setConceptId(input.getConceptId());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTime()));
		result.setId(input.getId());
		result.setLanguageCode(input.getLanguageCode());
		result.setModuleId(input.getModuleId());
		result.setIconId(input.getIconId());
		result.setReleased(input.isReleased());
		result.setTerm(input.getTerm());
		result.setSemanticTag(input.getSemanticTag());
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
		
		final Set<String> descriptionIds = FluentIterable.from(results).transform(SnomedDescription::getId).toSet();
		
		new InactivationPropertiesExpander(context(), expand(), locales(), Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR).expand(results, descriptionIds);
		new MembersExpander(context(), expand(), locales()).expand(results, descriptionIds);
		new ModuleExpander(context(), expand(), locales()).expand(results);
		expandCaseSignificance(results);
		expandConcept(results, descriptionIds);
		expandType(results, descriptionIds);
		expandAcceptabilities(results);
	}
	
	private void expandCaseSignificance(List<SnomedDescription> results) {
		if (!expand().containsKey("caseSignificance")) {
			return;
		}
		
		final Set<String> caseSignificanceIds = results.stream().map(SnomedDescription::getCaseSignificanceId).collect(Collectors.toSet());
		final Options caseSignificanceExpand = expand().get("caseSignificance", Options.class);
		
		final Map<String, SnomedConcept> caseSignificancesById = SnomedRequests.prepareSearchConcept()
				.filterByIds(caseSignificanceIds)
				.setLimit(caseSignificanceIds.size())
				.setExpand(caseSignificanceExpand.getOptions("expand"))
				.setLocales(locales())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(SnomedConcept::getId, c -> c));
		
		for (SnomedDescription result : results) {
			result.setCaseSignificance(caseSignificancesById.get(result.getCaseSignificanceId()));
		}
	}

	private void expandAcceptabilities(List<SnomedDescription> results) {
		if (!expand().containsKey("acceptabilities")) {
			return;
		}
		
		// expand the acceptability objects first
		for (SnomedDescription result : results) {
			if (!CompareUtils.isEmpty(result.getAcceptabilityMap())) {
				List<AcceptabilityMembership> acceptabilities = result.getAcceptabilityMap().entrySet().stream()
						.map(entry -> new AcceptabilityMembership(entry.getKey(), entry.getValue().getConceptId()))
						.sorted()
						.collect(Collectors.toList());
				result.setAcceptabilities(acceptabilities);
			} else {
				result.setAcceptabilities(Collections.emptyList());
			}
		}

		// additional expansions supported inside the acceptabilities() expand
		final Options expandOptions = expand().get("acceptabilities", Options.class).getOptions("expand");
		
		if (expandOptions.containsKey("acceptability")) {
			final Options acceptabilityExpand = expandOptions.get("acceptability", Options.class);
			final Set<String> acceptabilityIds = results.stream().flatMap(d -> d.getAcceptabilities().stream().map(AcceptabilityMembership::getAcceptabilityId)).collect(Collectors.toSet());
			final Map<String, SnomedConcept> acceptabilitiesById = SnomedRequests.prepareSearchConcept()
				.filterByIds(acceptabilityIds)
				.setLimit(acceptabilityIds.size())
				.setExpand(acceptabilityExpand.getOptions("expand"))
				.setLocales(locales())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(SnomedConcept::getId, c -> c));
			for (SnomedDescription result : results) {
				result.getAcceptabilities().forEach(acceptabilityMembership -> {
					acceptabilityMembership.setAcceptability(acceptabilitiesById.get(acceptabilityMembership.getAcceptabilityId()));
				});
			}
		}
		
		if (expandOptions.containsKey("languageRefSet")) {
			final Options languageRefSetExpand = expandOptions.get("languageRefSet", Options.class);
			final Set<String> languageRefSetIds = results.stream().flatMap(d -> d.getAcceptabilities().stream().map(AcceptabilityMembership::getLanguageRefSetId)).collect(Collectors.toSet());
			final Map<String, SnomedConcept> languageRefSetsById = SnomedRequests.prepareSearchConcept()
				.filterByIds(languageRefSetIds)
				.setLimit(languageRefSetIds.size())
				.setExpand(languageRefSetExpand.getOptions("expand"))
				.setLocales(locales())
				.build()
				.execute(context())
				.stream()
				.collect(Collectors.toMap(SnomedConcept::getId, c -> c));
			for (SnomedDescription result : results) {
				result.getAcceptabilities().forEach(acceptabilityMembership -> {
					acceptabilityMembership.setLanguageRefSet(languageRefSetsById.get(acceptabilityMembership.getLanguageRefSetId()));
				});
			}
		}
		
		
	}

	private void expandConcept(List<SnomedDescription> results, final Set<String> descriptionIds) {
		if (expand().containsKey("concept")) {
			final Options expandOptions = expand().get("concept", Options.class);
			final Set<String> conceptIds = FluentIterable.from(results).transform(SnomedDescription::getConceptId).toSet();
			
			final Map<String, SnomedConcept> conceptsById = getConceptMap(expandOptions, conceptIds);
			
			for (SnomedDescription description : results) {
				((SnomedDescription) description).setConcept(conceptsById.get(description.getConceptId()));
			}
		}
	}
	
	private void expandType(List<SnomedDescription> results, final Set<String> descriptionIds) {
		if (expand().containsKey("type")) {
			final Options expandOptions = expand().get("type", Options.class);
			final Set<String> typeIds = FluentIterable.from(results).transform(SnomedDescription::getTypeId).toSet();
			
			final Map<String, SnomedConcept> typesById = getConceptMap(expandOptions, typeIds);
			
			for (SnomedDescription description : results) {
				((SnomedDescription) description).setType(typesById.get(description.getTypeId()));
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
		
		return Maps.uniqueIndex(types, SnomedConcept::getId);
	}

}
