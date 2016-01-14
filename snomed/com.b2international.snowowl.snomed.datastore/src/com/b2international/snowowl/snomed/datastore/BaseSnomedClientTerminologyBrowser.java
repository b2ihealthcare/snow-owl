/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.api.browser.TreeContentProvider;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.browser.ActiveBranchClientTerminologyBrowser;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.6
 */
public abstract class BaseSnomedClientTerminologyBrowser extends ActiveBranchClientTerminologyBrowser<SnomedConceptIndexEntry, String> implements TreeContentProvider<SnomedConceptIndexEntry> {

	public static final List<ExtendedLocale> LOCALES = ImmutableList.of(new ExtendedLocale("en", "sg", Concepts.REFSET_LANGUAGE_TYPE_SG), new ExtendedLocale("en", "gb", Concepts.REFSET_LANGUAGE_TYPE_UK));
	
	private final IEventBus bus;

	protected BaseSnomedClientTerminologyBrowser(ITerminologyBrowser<SnomedConceptIndexEntry, String> wrappedBrowser, IEventBus bus) {
		super(wrappedBrowser);
		this.bus = bus;
	}
	
	protected final IEventBus getBus() {
		return bus;
	}
	
	@Override
	protected final EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSuperTypes(SnomedConceptIndexEntry concept) {
		return getSuperTypesById(concept.getId());
	}
	
	@Override
	public SnomedConceptIndexEntry getConcept(String id) {
		try {
			final ISnomedConcept concept = SnomedRequests
					.prepareGetConcept()
					.setComponentId(id)
					.setExpand("pt(),parentIds()")
					.setLocales(LOCALES)
					.build(getBranchPath().getPath())
					.executeSync(getBus());
			return SnomedConceptIndexEntry.builder(concept).label(concept.getPt().getTerm()).build();
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Returns with a collection of concepts given with the concept unique IDs.
	 * <p>If the IDs argument references a non existing concept, then that concept will
	 * be omitted from the result set, instead of populating its value as {@code null}.  
	 * @param ids the unique IDs for the collection.
	 * @return a collection of concepts.
	 */
	@Override
	public final Collection<SnomedConceptIndexEntry> getComponents(final Iterable<String> ids) {
		if (CompareUtils.isEmpty(ids)) {
			return Collections.emptySet();
		}
		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setComponentIds(ImmutableSet.copyOf(ids))
				.setLocales(LOCALES)
				.setExpand("pt(),parentIds()")
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptIndexEntry.fromConcepts(concepts);
	}
	
}
