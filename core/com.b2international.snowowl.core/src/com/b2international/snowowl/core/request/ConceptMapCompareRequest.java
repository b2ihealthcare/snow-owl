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

import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.b2international.snowowl.core.domain.SetMappings;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
* @since 7.8
*/
final class ConceptMapCompareRequest extends ResourceRequest<BranchContext, ConceptMapCompareResult> {
	
	private static final long serialVersionUID = 1L;
	
	private final ComponentURI baseConceptMapURI;
	private final ComponentURI compareConceptMapURI;
	
	ConceptMapCompareRequest(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
	}

	@Override
	public ConceptMapCompareResult execute(BranchContext context) {
		
		List<ConceptMapMapping> baseMappings = Lists.newArrayList();
		List<ConceptMapMapping> compareMappings = Lists.newArrayList();
		
		final SearchResourceRequestIterator<MappingSearchRequestBuilder, SetMappings> baseIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterBySet(baseConceptMapURI.identifier())
				.setLocales(locales())
				.setLimit(10_000),
				r -> r.build().execute(context)
		);
		
		baseIterator.forEachRemaining(hits -> hits.forEach(baseMappings::add));

		final SearchResourceRequestIterator<MappingSearchRequestBuilder, SetMappings> compareIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchConceptMapMappings()
				.filterBySet(compareConceptMapURI.identifier())
				.setLocales(locales())
				.setLimit(10_000),
				r -> r.build().execute(context)
		);
		
		compareIterator.forEachRemaining(hits -> hits.forEach(compareMappings::add));
		
		ConceptMapCompareResult result = compareDifferents(baseMappings, compareMappings);
		return result; 
	}
	
	private ConceptMapCompareResult compareDifferents(List<ConceptMapMapping> baseSet, List<ConceptMapMapping> compareSet) {
		ListMultimap<ConceptMapMapping, ConceptMapMapping> changes = ArrayListMultimap.create();
		List<ConceptMapMapping> remove = Lists.newArrayList();
		List<ConceptMapMapping> add = Lists.newArrayList();

		remove.addAll(baseSet);
		add.addAll(compareSet);

		for (ConceptMapMapping memberA : baseSet) {
			compareSet.forEach(memberB -> {
				if (isSame(memberA, memberB)) {
					remove.remove(memberA);
					add.remove(memberB);
				} else if (isChanged(memberA, memberB)) {
					remove.remove(memberA);
					add.remove(memberB);
					changes.put(memberA, memberB);
				}
			});
		}
		return new ConceptMapCompareResult (add, remove, changes);
	}

	private boolean isSame(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return isSourceEqual(memberA, memberB) && isTargetEqual(memberA, memberB);
	}

	private boolean isChanged(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return isSourceEqual(memberA, memberB) && !isTargetEqual(memberA, memberB);
	}

	private boolean isTargetEqual(ConceptMapMapping memberA, ConceptMapMapping memberB) {
		return  Objects.equals(memberA.getTargetComponentURI(),memberB.getTargetComponentURI());
	}

	private boolean isSourceEqual(ConceptMapMapping memberA, ConceptMapMapping memberB){
		return Objects.equals(memberA.getSourceComponentURI(), memberB.getSourceComponentURI());
	}

}
