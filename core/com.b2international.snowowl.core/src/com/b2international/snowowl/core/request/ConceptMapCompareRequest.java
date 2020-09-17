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
import java.util.Objects;
import java.util.Set;

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
* @since 7.8
*/
public final class ConceptMapCompareRequest extends ResourceRequest<BranchContext, ConceptMapCompareResult> {
	
	private static final long serialVersionUID = 1L;
	
	private final ComponentURI baseConceptMapURI;
	private final ComponentURI compareConceptMapURI;
	
	ConceptMapCompareRequest(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
	}

	@Override
	public ConceptMapCompareResult execute(BranchContext context) {

		ListMultimap<ComponentURI, ConceptMapMapping> baseMappings = ArrayListMultimap.create();
		ListMultimap<ComponentURI, ConceptMapMapping> compareMappings = ArrayListMultimap.create();
		Set<ComponentURI> baseURIs = Sets.newHashSet();
		Set<ComponentURI> compareURIs = Sets.newHashSet();

		final SearchResourceRequestIterator<ConceptMapMappingSearchRequestBuilder, ConceptMapMappings> baseIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(baseConceptMapURI.identifier())
				.setLocales(locales())
				.setLimit(10_000),
				r -> r.build().execute(context)
				);

		baseIterator.forEachRemaining(hits -> hits.forEach(hit -> {
			baseMappings.put(hit.getSourceComponentURI(), hit);
			baseURIs.add(hit.getSourceComponentURI());
		}));

		final SearchResourceRequestIterator<ConceptMapMappingSearchRequestBuilder, ConceptMapMappings> compareIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterByConceptMap(compareConceptMapURI.identifier())
				.setLocales(locales())
				.setLimit(10_000),
				r -> r.build().execute(context)
				);

		compareIterator.forEachRemaining(hits -> hits.forEach(hit -> {
			compareMappings.put(hit.getSourceComponentURI(), hit);
			compareURIs.add(hit.getSourceComponentURI());
		}));

		ConceptMapCompareResult result = compareDifferences(baseMappings, baseURIs, compareMappings, compareURIs);
		return result; 
	}
	
	private ConceptMapCompareResult compareDifferences(ListMultimap<ComponentURI, ConceptMapMapping> baseMappings, Set<ComponentURI> baseURIs, ListMultimap<ComponentURI, ConceptMapMapping> compareMappings, Set<ComponentURI> compareURIs) {
		ListMultimap<ConceptMapMapping, ConceptMapMapping> allChanged = ArrayListMultimap.create();
		List<ConceptMapMapping> allRemoved = Lists.newArrayList();
		List<ConceptMapMapping> allAdded = Lists.newArrayList();
		List<ConceptMapMapping> allUnchanged = Lists.newArrayList();
		
		SetView<ComponentURI> changedURIs = Sets.intersection(baseURIs, compareURIs);
		
		changedURIs.forEach(changedURI -> {
			ListMultimap<ConceptMapMapping, ConceptMapMapping> changed = ArrayListMultimap.create();
			for (ConceptMapMapping memberA : baseMappings.get(changedURI)) {
				compareMappings.get(changedURI).forEach(memberB -> {
					if (isChanged(memberA, memberB)) {
						changed.put(memberA, memberB);
					}
				});
			}
			allChanged.putAll(changed);
		});
		
		SetView<ComponentURI> removedURIs = Sets.difference(baseURIs, compareURIs);
		removedURIs.forEach(uri -> allRemoved.addAll(baseMappings.get(uri)));
		
		SetView<ComponentURI> addedURIs = Sets.difference(compareURIs, baseURIs);
		addedURIs.forEach(uri -> allAdded.addAll(compareMappings.get(uri)));
		
		SetView<ComponentURI> unchangedURIs = Sets.intersection(compareURIs, baseURIs);
		unchangedURIs.forEach(uri -> allUnchanged.addAll(compareMappings.get(uri)));
		
		return new ConceptMapCompareResult(allAdded, allRemoved, allChanged, allUnchanged);
	}

	private boolean isChanged(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return isSourceEqual(memberA, memberB) && !isTargetEqual(memberA, memberB);
	}

	private boolean isTargetEqual(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return Objects.equals(memberA.getTargetComponentURI(), memberB.getTargetComponentURI());
	}

	private boolean isSourceEqual(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return Objects.equals(memberA.getSourceComponentURI(), memberB.getSourceComponentURI());
	}

}
