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
import java.util.concurrent.TimeUnit;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.compare.CompareSets;
import com.b2international.snowowl.core.compare.ConceptMapCompareResult;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.SetMapping;
import com.b2international.snowowl.core.domain.SetMappings;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.Lists;

/**
* @since 7.8
*/
public class ConceptMapCompareRequest implements CompareSets, Request<BranchContext, ConceptMapCompareResult> {
	private static final long serialVersionUID = 1L;
	private final ComponentURI baseConceptMapURI;
	private final ComponentURI compareConceptMapURI;
	private final List<ExtendedLocale> locales;
	
	public ConceptMapCompareRequest(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI, List<ExtendedLocale> locales) {
		this.baseConceptMapURI = baseConceptMapURI;
		this.compareConceptMapURI = compareConceptMapURI;
		this.locales = locales;
	}

	public ConceptMapCompareResult execute(BranchContext context) {
		
		List<SetMapping> baseMappings = Lists.newArrayList();
		List<SetMapping> compareMappings = Lists.newArrayList();
		
		final SearchResourceRequestIterator<MappingSearchRequestBuilder, SetMappings> baseIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchMappings()
				.filterBySet(baseConceptMapURI.identifier())
				.setLocales(locales)
				.setLimit(10_000),
				// FIXME use proper CodeSystemURI instead of fixing it to HEAD of the currently selected
				r -> r.build(CodeSystemURI.head(baseConceptMapURI.codeSystem()))
				.execute(context.service(IEventBus.class))
				.getSync(5, TimeUnit.MINUTES)
				);
		
		baseIterator.forEachRemaining(hits -> {
			hits.forEach(member -> {	
				baseMappings.add(member);
			});
		});

		final SearchResourceRequestIterator<MappingSearchRequestBuilder, SetMappings> compareIterator = new SearchResourceRequestIterator<>(
				CodeSystemRequests.prepareSearchMappings()
				.filterBySet(compareConceptMapURI.identifier())
				.setLocales(locales)
				.setLimit(10_000),
				// FIXME use proper CodeSystemURI instead of fixing it to HEAD of the currently selected
				r -> r.build(CodeSystemURI.head(compareConceptMapURI.codeSystem()))
				.execute(context.service(IEventBus.class))
				.getSync(5, TimeUnit.MINUTES)
				);
		
		compareIterator.forEachRemaining(hits -> {
			hits.forEach(member -> {	
				compareMappings.add(member);
			});
		});
		
		ConceptMapCompareResult result = compareDifferents(baseMappings, compareMappings);
		return result; 
	}

}
