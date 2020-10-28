/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareChangeKind;
import com.b2international.snowowl.core.compare.ConceptMapCompareConfigurationProperties;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.compare.MapCompareSourceAndTargetEquivalence;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
* @since 7.8
*/
final class ConceptMapCompareRequest extends ResourceRequest<BranchContext, ConceptMapCompareResult> {
	
	private static final long serialVersionUID = 2L;
	
	private static final int DEFAULT_MEMBER_SCROLL_LIMIT = 10_000;
	
	@NotNull
	private final ComponentURI baseConceptMapURI;
	
	@NotNull
	private final ComponentURI compareConceptMapURI;
	
	@NotEmpty
	private final Set<ConceptMapCompareConfigurationProperties> selectedConfig;
	
	@Min(0)
	private int limit;
	
	ConceptMapCompareRequest(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI, int limit, Set<ConceptMapCompareConfigurationProperties> selectedConfig) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
		this.limit = limit;
		this.selectedConfig = selectedConfig;
	}

	@Override
	public ConceptMapCompareResult execute(BranchContext context) {
		List<ConceptMapMapping> baseMappings = fetchConceptMapMappings(context, baseConceptMapURI.identifier());
		List<ConceptMapMapping> compareMappings = fetchConceptMapMappings(context, compareConceptMapURI.identifier());
		return compareDifferences(baseMappings, compareMappings);
	}

	private List<ConceptMapMapping> fetchConceptMapMappings(BranchContext context, String conceptMapId) {
		List<ConceptMapMapping> baseMappings = Lists.newArrayList();
		new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(conceptMapId)
				.setLocales(locales())
				.setLimit(DEFAULT_MEMBER_SCROLL_LIMIT),
				r -> r.build().execute(context)
			).forEachRemaining(hits -> hits.forEach(baseMappings::add));
		return baseMappings;
	}
	
	private ConceptMapCompareResult compareDifferences(List<ConceptMapMapping> baseMappings, List<ConceptMapMapping> compareMappings) {

		MapCompareSourceAndTargetEquivalence mapCompareEquivalence = new MapCompareSourceAndTargetEquivalence(selectedConfig);
		Set<Wrapper<ConceptMapMapping>> baseWrappedMappings = baseMappings.stream()
				.map(mapping -> mapCompareEquivalence.wrap(mapping))
				.collect(Collectors.toSet());
		
		Set<Wrapper<ConceptMapMapping>> compareWrappedMappings = compareMappings.stream()
				.map(mapping -> mapCompareEquivalence.wrap(mapping))
				.collect(Collectors.toSet());
		
		//Unchanged elements are in the intersection
		Set<Wrapper<ConceptMapMapping>> allUnchangedWrappedMappings = Sets.intersection(baseWrappedMappings, compareWrappedMappings);
		
		Set<ConceptMapMapping> allUnchanged = Sets.newHashSet();
		allUnchangedWrappedMappings.forEach(wrappedMapping -> allUnchanged.add(wrappedMapping.get()));
		
		//Remove the unchanged from further comparison
		SetView<Wrapper<ConceptMapMapping>> onlyBaseWrappedMappings = Sets.difference(baseWrappedMappings, allUnchangedWrappedMappings);
		SetView<Wrapper<ConceptMapMapping>> onlyCompareWrappedMappings = Sets.difference(compareWrappedMappings, allUnchangedWrappedMappings);
		
		Set<Wrapper<ConceptMapMapping>> changedBase = extractChangedMappings(onlyBaseWrappedMappings, onlyCompareWrappedMappings);
		Set<Wrapper<ConceptMapMapping>> changedCompare = extractChangedMappings(onlyCompareWrappedMappings, onlyBaseWrappedMappings);
		
		// handle different map target case
		// TODO add more differences like map property based diffs, etc, see ConceptMapCompareChangeKind enum literals
		List<ConceptMapCompareResultItem> allChanged = Lists.newArrayList();
		changedBase.forEach(mapping -> allChanged.add(new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, mapping.get())));
		changedCompare.forEach(mapping -> allChanged.add(new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, mapping.get())));
		
		List<ConceptMapCompareResultItem> allRemoved = Sets.difference(onlyBaseWrappedMappings, changedBase).stream()
				.map(mapping -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.PRESENT, mapping.get()))
				.collect(Collectors.toList());
		
		List<ConceptMapCompareResultItem> allAdded = Sets.difference(onlyCompareWrappedMappings, changedCompare).stream()
				.map(mapping -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.MISSING, mapping.get()))
				.collect(Collectors.toList());
		
		List<ConceptMapCompareResultItem> items = ImmutableList.<ConceptMapCompareResultItem>builder()
			.addAll(allAdded)
			.addAll(allRemoved)
			.addAll(allChanged)
			.addAll(allUnchanged.stream().map(mapping -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.SAME, mapping)).collect(Collectors.toList()))
			.build()
			.stream()
			.sorted()
			.limit(limit)
			.collect(Collectors.toList());
		
		return new ConceptMapCompareResult(items, allAdded.size(), allRemoved.size(), allChanged.size(), allUnchanged.size(), limit);
	}

	private Set<Wrapper<ConceptMapMapping>> extractChangedMappings(SetView<Wrapper<ConceptMapMapping>> mappingsToChooseFrom,
			SetView<Wrapper<ConceptMapMapping>> mappingsToCompareTo) {
		return mappingsToChooseFrom.stream()
				.filter(wrappedMapping -> mappingsToCompareTo.stream()
						.anyMatch(compareWrappedMapping -> isSourceEqual(wrappedMapping.get(), compareWrappedMapping.get())))
				.collect(Collectors.toSet());
	}

	private boolean isSourceEqual(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		boolean isDifferent = selectedConfig.stream().anyMatch(config -> !config.isSourceEqual(memberA, memberB));
		return !isDifferent;
	}
	
}
