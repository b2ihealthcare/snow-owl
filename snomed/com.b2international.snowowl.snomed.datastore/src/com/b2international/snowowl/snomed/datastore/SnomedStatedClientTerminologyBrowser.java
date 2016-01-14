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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;

import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntryWithChildFlag;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Client version of the SNOMED CT terminology browser using only stated IS_A relationships
 */
public final class SnomedStatedClientTerminologyBrowser extends BaseSnomedClientTerminologyBrowser {

	public SnomedStatedClientTerminologyBrowser(SnomedStatedTerminologyBrowser wrappedBrowser, IEventBus bus) {
		super(wrappedBrowser, bus);
	}
	
	@Override
	public boolean hasChildren(SnomedConceptIndexEntry element) {
		// TODO fix implementation, if required (this uses inferred tree instead of stated)
		return getSubTypeCount(element) > 0;
	}
	
	@Override
	public boolean hasParents(SnomedConceptIndexEntry element) {
		return !element.getStatedParents().isEmpty();
	}
	
	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(SnomedConceptIndexEntry concept) {
		final SnomedConcepts concepts = SnomedRequests
			.prepareSearchConcept()
			.all()
			.filterByStatedParent(concept.getId())
			.setExpand("pt(),descendants(form:\"stated\",direct:true,limit:0)")
			.setLocales(LOCALES)
			.build(getBranchPath().getPath())
			.executeSync(getBus());
		return FluentIterable.from(concepts).transform(new Function<ISnomedConcept, IComponentWithChildFlag<String>>() {
			@Override
			public IComponentWithChildFlag<String> apply(ISnomedConcept input) {
				final SnomedConceptIndexEntry entry = SnomedConceptIndexEntry
					.builder(input)
					.label(input.getPt().getTerm())
					.build();
				return new SnomedConceptIndexEntryWithChildFlag(entry, input.getDescendants().getTotal() > 0);
			}
		}).toList();
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getRootConcepts() {
		final SnomedConcepts roots = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.filterByStatedParent(Long.toString(SnomedMappings.ROOT_ID))
				.setLocales(LOCALES)
				.setExpand("pt(),parentIds()")
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptIndexEntry.fromConcepts(roots);
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesById(String id) {
		final SnomedConcepts concepts = SnomedRequests
				.prepareSearchConcept()
				.all()
				.filterByStatedParent(id)
				.setExpand("pt(),parentIds()")
				.setLocales(LOCALES)
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptIndexEntry.fromConcepts(concepts);
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypesById(String id) {
		final ISnomedConcept concept = SnomedRequests
				.prepareGetConcept()
				.setComponentId(id)
				.setExpand("ancestors(form:\"stated\",direct:true,expand(pt(),parentIds()))")
				.setLocales(LOCALES)
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptIndexEntry.fromConcepts(concept.getAncestors());
	}
	
}
