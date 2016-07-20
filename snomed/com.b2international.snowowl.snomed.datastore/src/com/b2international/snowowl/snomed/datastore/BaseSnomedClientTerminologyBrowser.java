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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.EmptyTerminologyBrowser;
import com.b2international.snowowl.core.api.FilteredTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.TreeContentProvider;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.core.tree.TerminologyTree;
import com.b2international.snowowl.snomed.core.tree.TreeBuilder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;

/**
 * @since 4.6
 */
public abstract class BaseSnomedClientTerminologyBrowser extends ActiveBranchPathAwareService implements IClientTerminologyBrowser<SnomedConceptDocument, String>, TreeContentProvider<SnomedConceptDocument> {

	private final IEventBus bus;
	private final Provider<LanguageSetting> languageSetting;

	protected BaseSnomedClientTerminologyBrowser(IEventBus bus, Provider<LanguageSetting> languageSetting) {
		this.bus = bus;
		this.languageSetting = languageSetting;
	}
	
	public abstract String getForm();
	
	protected final IEventBus getBus() {
		return bus;
	}
	
	protected final List<ExtendedLocale> getLocales() {
		return languageSetting.get().getLanguagePreference();
	}
	
	@Override
	protected final EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
	@Override
	public final IFilterClientTerminologyBrowser<SnomedConceptDocument, String> filterTerminologyBrowser(String expression, IProgressMonitor monitor) {
		final String branch = getBranchPath().getPath();
		final SnomedConcepts matches = SnomedRequests
			.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByTerm(expression)
			.filterByExtendedLocales(getLocales())
			// expand parent and ancestorIds to get all possible treepaths to the top
			.setExpand("pt(),parentIds(),ancestorIds()")
			.build(branch)
			.executeSync(getBus());
		
		if (matches.getItems().isEmpty()) {
			return EmptyTerminologyBrowser.getInstance();
		}
		
		final FluentIterable<SnomedConceptDocument> matchingConcepts = FluentIterable.from(SnomedConceptDocument.fromConcepts(matches));
		final Set<String> matchingConceptIds = matchingConcepts.transform(ComponentUtils.<String>getIdFunction()).toSet();
		final TerminologyTree tree = newTree(branch, matchingConcepts);
		return new FilteredTerminologyBrowser<SnomedConceptDocument, String>(tree.getItems(), tree.getSubTypes(), tree.getSuperTypes(), FilterTerminologyBrowserType.HIERARCHICAL, matchingConceptIds);
	}
	
	@Override
	public SnomedConceptDocument getConcept(String id) {
		try {
			final ISnomedConcept concept = SnomedRequests
					.prepareGetConcept()
					.setComponentId(id)
					.setExpand("pt(),parentIds()")
					.setLocales(getLocales())
					.build(getBranchPath().getPath())
					.executeSync(getBus());
			final ISnomedDescription pt = concept.getPt();
			final String label = pt != null ? pt.getTerm() : id;
			return SnomedConceptDocument.builder(concept).label(label).build();
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSubTypes(final SnomedConceptDocument concept) {
		final SnomedConcepts snomedConcepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setLocales(getLocales())
				.filterByAncestor(concept.getId())
				.setExpand("pt()")
				.build(getBranchPath().getPath())
				.executeSync(getBus());
				
		return SnomedConceptDocument.fromConcepts(snomedConcepts);
	}
	
	/**
	 * Create a {@link TreeBuilder} for a filtered tree.
	 * @param branch
	 * @param matchingConcepts 
	 * @param locales
	 * @return
	 */
	protected abstract TerminologyTree newTree(String branch, Iterable<SnomedConceptDocument> matchingConcepts);
	
	/**
	 * Returns with a collection of concepts given with the concept unique IDs.
	 * <p>If the IDs argument references a non existing concept, then that concept will
	 * be omitted from the result set, instead of populating its value as {@code null}.  
	 * @param ids the unique IDs for the collection.
	 * @return a collection of concepts.
	 */
	@Override
	public final Collection<SnomedConceptDocument> getComponents(final Iterable<String> ids) {
		if (CompareUtils.isEmpty(ids)) {
			return Collections.emptySet();
		}
		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setComponentIds(ImmutableSet.copyOf(ids))
				.setLocales(getLocales())
				.setExpand("pt(),parentIds(),ancestorIds()")
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptDocument.fromConcepts(concepts);
	}
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT concept exists with the given unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	@Override
	public boolean exists(final String conceptId) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Map<String, Boolean> exist(Collection<String> componentIds) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public boolean isTerminologyAvailable() {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getSuperTypeCount(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getSuperTypeCountById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getSubTypeCountById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getAllSubTypeCount(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getAllSuperTypeCount(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getAllSubTypeCountById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getAllSuperTypeCountById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<String> getSuperTypeIds(String conceptId) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSuperTypesById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSuperTypes(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSuperTypes(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSubTypesById(String id) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public boolean isSuperTypeOf(SnomedConceptDocument superType, SnomedConceptDocument subType) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public boolean isSuperTypeOfById(String superTypeId, String subTypeId) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public List<SnomedConceptDocument> getSubTypesAsList(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public int getSubTypeCount(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public SnomedConceptDocument getTopLevelConcept(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSubTypes(SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
}
