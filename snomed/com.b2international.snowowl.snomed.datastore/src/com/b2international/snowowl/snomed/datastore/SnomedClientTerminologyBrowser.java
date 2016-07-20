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

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.core.tree.TerminologyTree;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntryWithChildFlag;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.inject.Provider;

/**
 * @since 1.0
 */
@Client
public class SnomedClientTerminologyBrowser extends BaseSnomedClientTerminologyBrowser {
	
	public SnomedClientTerminologyBrowser(final IEventBus bus, final Provider<LanguageSetting> languageSetting) {
		super(bus, languageSetting);
	}
	
	@Override
	public String getForm() {
		return Trees.INFERRED_FORM;
	}
	
	@Override
	public boolean hasChildren(SnomedConceptDocument element) {
		return getSubTypeCount(element) > 0;
	}
	
	@Override
	public boolean hasParents(SnomedConceptDocument element) {
		return !element.getParents().isEmpty();
	}

	@Override
	protected TerminologyTree newTree(String branch, Iterable<SnomedConceptDocument> concepts) {
		return Trees
				.newInferredTree()
				.build(branch, concepts);
	}
	
	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(SnomedConceptDocument concept) {
		final SnomedConcepts concepts = SnomedRequests
			.prepareSearchConcept()
			.all()
			.filterByParent(concept.getId())
			.setExpand("pt(),descendants(form:\"inferred\",direct:true,limit:0)")
			.setLocales(getLocales())
			.build(getBranchPath().getPath())
			.executeSync(getBus());
		return FluentIterable.from(concepts).transform(new Function<ISnomedConcept, IComponentWithChildFlag<String>>() {
			@Override
			public IComponentWithChildFlag<String> apply(ISnomedConcept input) {
				final SnomedConceptDocument entry = SnomedConceptDocument
					.builder(input)
					.label(input.getPt() == null ? input.getId() : input.getPt().getTerm())
					.build();
				return new SnomedConceptIndexEntryWithChildFlag(entry, input.getDescendants().getTotal() > 0);
			}
		}).toList();
	}
	
	@Override
	public Collection<SnomedConceptDocument> getRootConcepts() {
		final SnomedConcepts roots = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.filterByParent(Long.toString(SnomedConceptDocument.ROOT_ID))
				.setLocales(getLocales())
				.setExpand("pt(),parentIds()")
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptDocument.fromConcepts(roots);
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSubTypesById(String id) {
		final SnomedConcepts concepts = SnomedRequests
				.prepareSearchConcept()
				.all()
				.filterByParent(id)
				.setExpand("pt(),parentIds()")
				.setLocales(getLocales())
				.build(getBranchPath().getPath())
				.executeSync(getBus());
		return SnomedConceptDocument.fromConcepts(concepts);
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSuperTypesById(String id) {
		try {
			final ISnomedConcept concept = SnomedRequests
					.prepareGetConcept()
					.setComponentId(id)
					.setExpand("ancestors(form:\"inferred\",direct:true,expand(pt(),parentIds()))")
					.setLocales(getLocales())
					.build(getBranchPath().getPath())
					.executeSync(getBus());
			return SnomedConceptDocument.fromConcepts(concept.getAncestors());
		} catch (NotFoundException e) {
			return emptyList();
		}
	}
	
	@Override
	public Collection<SnomedConceptDocument> getAllSubTypesById(final String id) {
		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setLocales(getLocales())
				.setExpand("pt()")
				.filterByAncestor(id)
				.build(getBranchPath().getPath())
				.executeSync(getBus());

		return SnomedConceptDocument.fromConcepts(concepts);
	}
	
	/**
	 * Returns with a set of all active descendant concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active descendant concepts.
	 */
	public LongSet getAllSubTypeIds(final long conceptId) {
		throw new UnsupportedOperationException("TODO implement me");
	}

	/**
	 * Returns with a set of the active direct ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of the active direct ancestor concepts.
	 */
	public LongSet getSuperTypeIds(final long conceptId) {
		throw new UnsupportedOperationException("TODO implement me");
	}

	/**
	 * Returns with a set of all active ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active ancestor concepts.
	 */
	public LongSet getAllSuperTypeIds(final long conceptId) {
		throw new UnsupportedOperationException("TODO implement me");
	}

	/**
	 * Maps all active SNOMED&nbsp;CT concept identifiers to their corresponding storage keys in the ontology.
	 * <p>Map keys are concept IDs, values are concept storage keys (CDO ID).
	 * @param branchPath the branch path.
	 * @return a map of concept IDs and storage keys.
	 */
	public LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath) {
		throw new UnsupportedOperationException("TODO implement me");
	}

	/**
	 * Returns the sub types of the specified concept, with an additional boolean flag to indicate whether the concept has children or not.
	 * 
	 * @param concept the concept
	 * @return the sub types with additional child flag
	 */
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildInformation(final SnomedConceptDocument concept) {
		throw new UnsupportedOperationException("TODO implement me");
	}
	
}