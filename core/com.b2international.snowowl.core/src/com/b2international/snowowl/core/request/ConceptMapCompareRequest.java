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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

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
	
	@NotNull
	private final String preferredDisplay;
	
	@NotEmpty
	private final Set<ConceptMapCompareConfigurationProperties> selectedConfig;
	
	private transient MapCompareSourceAndTargetEquivalence mapCompareEquivalence;
	
	@Min(0)
	private int limit;
	
	ConceptMapCompareRequest(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI, int limit, Set<ConceptMapCompareConfigurationProperties> selectedConfig, String preferredDisplay) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
		this.limit = limit;
		this.selectedConfig = selectedConfig;
		this.preferredDisplay = preferredDisplay;
	}

	@Override
	public ConceptMapCompareResult execute(BranchContext context) {
		List<ConceptMapMapping> baseMappings = fetchConceptMapMappings(context, baseConceptMapURI.identifier());
		List<ConceptMapMapping> compareMappings = fetchConceptMapMappings(context, compareConceptMapURI.identifier());

		mapCompareEquivalence = new MapCompareSourceAndTargetEquivalence(selectedConfig);
		
		return compareDifferences(baseMappings, compareMappings);
	}

	private List<ConceptMapMapping> fetchConceptMapMappings(BranchContext context, String conceptMapId) {
		List<ConceptMapMapping> baseMappings = Lists.newArrayList();
		new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(conceptMapId)
				.setLocales(locales())
				.setPreferredDisplay(preferredDisplay)
				.setLimit(DEFAULT_MEMBER_SCROLL_LIMIT),
				r -> r.build().execute(context)
			).forEachRemaining(hits -> hits.forEach(baseMappings::add));
		return baseMappings;
	}
	
	private ConceptMapCompareResult compareDifferences(List<ConceptMapMapping> baseMappings, List<ConceptMapMapping> compareMappings) {

		//Wrap the mappings to be compared and processed using set operations
		Set<Wrapper<ConceptMapMapping>> baseWrappedMappings = wrapMappings(baseMappings);
		Set<Wrapper<ConceptMapMapping>> compareWrappedMappings = wrapMappings(compareMappings);
		
		//Unchanged elements are in the intersection
		Set<Wrapper<ConceptMapMapping>> allUnchangedWrappedMappings = Sets.intersection(baseWrappedMappings, compareWrappedMappings);
		
		List<ConceptMapCompareResultItem> allUnchanged = allUnchangedWrappedMappings.stream()
			.map(w -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.SAME, w.get()))
			.collect(Collectors.toList());
		
		//Remove the unchanged from further comparison
		Set<Wrapper<ConceptMapMapping>> onlyBaseWrappedMappings = Sets.difference(baseWrappedMappings, allUnchangedWrappedMappings);
		Set<Wrapper<ConceptMapMapping>> onlyCompareWrappedMappings = Sets.difference(compareWrappedMappings, allUnchangedWrappedMappings);
		
		//Multimaps based on the configured equivalence properties
		Multimap<String, Wrapper<ConceptMapMapping>> baseMMap = collectMapEntries(onlyBaseWrappedMappings);
		Multimap<String, Wrapper<ConceptMapMapping>> compareMMap = collectMapEntries(onlyCompareWrappedMappings);
		
		Set<String> sourceCompoundKeys = baseMMap.keySet();
		Set<String> targetCompoundKeys = compareMMap.keySet();
		
		Set<String> presentKeys = Sets.difference(sourceCompoundKeys, targetCompoundKeys);
		Set<String> missingKeys = Sets.difference(targetCompoundKeys, sourceCompoundKeys);
		
		//Collect the 'Present' mappings
		Set<Wrapper<ConceptMapMapping>> presentMappings = Sets.newHashSet();
		presentKeys.stream().forEach(k -> presentMappings.addAll(baseMMap.get(k)));
		List<ConceptMapCompareResultItem> allRemoved = presentMappings.stream().map(w -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.PRESENT, w.get())).collect(Collectors.toList());
		
		//Collect the 'Missing' mappings
		Set<Wrapper<ConceptMapMapping>> missingMappings = Sets.newHashSet();
		missingKeys.stream().forEach(k -> missingMappings.addAll(compareMMap.get(k)));
		List<ConceptMapCompareResultItem> allAdded = missingMappings.stream().map(w -> new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.MISSING, w.get())).collect(Collectors.toList());
		
		//Remove the already processed missing and present mappings and the remaining items are the different mappings
		Set<Wrapper<ConceptMapMapping>> sourceDifferentMappings = Sets.difference(onlyBaseWrappedMappings, presentMappings);
		Set<Wrapper<ConceptMapMapping>> targetDifferentMappings = Sets.difference(onlyCompareWrappedMappings, missingMappings);
		
		List<ConceptMapCompareResultItem> allChanged = Lists.newArrayList();
		
		sourceDifferentMappings.forEach(w -> {
			allChanged.add(new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, w.get()));
		});
		
		targetDifferentMappings.forEach(w -> {
			allChanged.add(new ConceptMapCompareResultItem(ConceptMapCompareChangeKind.DIFFERENT_TARGET, w.get()));
		});
		
		List<ConceptMapCompareResultItem> items = ImmutableList.<ConceptMapCompareResultItem>builder()
			.addAll(allAdded)
			.addAll(allRemoved)
			.addAll(allChanged)
			.addAll(allUnchanged)
			.build()
			.stream()
			.sorted()
			.limit(limit)
			.collect(Collectors.toList());
		
		return new ConceptMapCompareResult(items, allAdded.size(), allRemoved.size(), allChanged.size(), allUnchanged.size(), limit);
	}

	/*
	 * Create a multimap for the passed in mappings with a compound key that is based on the selected configuration
	 */
	private Multimap<String, Wrapper<ConceptMapMapping>> collectMapEntries(Set<Wrapper<ConceptMapMapping>> wrappedMappings) {
		Multimap<String, Wrapper<ConceptMapMapping>> baseMMap = HashMultimap.create();
		wrappedMappings.forEach(w -> baseMMap.put(getCompoundKey(selectedConfig, w.get()), w));
		return baseMMap;
	}
	
	private String getCompoundKey(Set<ConceptMapCompareConfigurationProperties> configuration, ConceptMapMapping mapping) {
		
		StringBuilder sb = new StringBuilder();
		if (configuration.contains(ConceptMapCompareConfigurationProperties.CODE)) {
			sb.append(mapping.getSourceComponentURI().identifier());
		}
		
		if (configuration.contains(ConceptMapCompareConfigurationProperties.CODE_SYSTEM)) {
			sb.append(mapping.getSourceComponentURI().codeSystem());
		}
		
		if (configuration.contains(ConceptMapCompareConfigurationProperties.TERM)) {
			sb.append(mapping.getSourceTerm());
		}
		return sb.toString();
	}

	private Set<Wrapper<ConceptMapMapping>> wrapMappings(List<ConceptMapMapping> mappings) {
		return mappings.stream()
				.map(mapping -> mapCompareEquivalence.wrap(mapping))
				.collect(Collectors.toSet());
	}
	
}
