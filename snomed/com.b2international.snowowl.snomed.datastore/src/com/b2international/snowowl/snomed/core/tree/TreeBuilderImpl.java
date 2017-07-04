/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.tree;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongCollection;
import com.b2international.commons.AlphaNumericComparator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;

/**
 * @since 4.6
 */
abstract class TreeBuilderImpl implements TreeBuilder {

	private Collection<SnomedConceptDocument> topLevelConcepts;
	
	TreeBuilderImpl() {}
	
	abstract String getForm();
	
	@Override
	public final TreeBuilder withTopLevelConcepts(final Collection<SnomedConceptDocument> topLevelConcepts) {
		this.topLevelConcepts = topLevelConcepts;
		return this;
	}
	
	@Override
	public TerminologyTree build(final String branch, final Iterable<SnomedConceptDocument> nodes) {
		final Collection<SnomedConceptDocument> topLevelConcepts = this.topLevelConcepts == null ? 
				getDefaultTopLevelConcepts(branch) : this.topLevelConcepts;
		
		final Map<String, SnomedConceptDocument> treeItemsById = newHashMap();
		
		// all matching concepts should be in the componentMap
		treeItemsById.putAll(FluentIterable.from(nodes).uniqueIndex(ComponentUtils.<String>getIdFunction()));
		
		final Collection<String> requiredTopLevelConceptIds = ComponentUtils.getIdSet(topLevelConcepts);
		
		// compute subType and superType maps for the tree
		final SetMultimap<String, String> superTypeMap = HashMultimap.create();
		final SetMultimap<String, String> subTypeMap = HashMultimap.create();
		
		for (SnomedConceptDocument entry : nodes) {
			final LongCollection parentIds = getParents(entry);
			final LongCollection ancestorIds = getAncestors(entry);
			if (parentIds != null) {
				final Collection<String> parents = LongSets.toStringSet(parentIds);
				final Collection<String> selectedParents = newHashSet();
				// if the parent is not a match or TOP level
				for (String parent : parents) {
					if (treeItemsById.containsKey(parent) || requiredTopLevelConceptIds.contains(parent)) {
						selectedParents.add(parent);
					}
				}
				if (selectedParents.isEmpty()) {
					findParentInAncestors(entry, treeItemsById, requiredTopLevelConceptIds, subTypeMap, superTypeMap);
				} else {
					for (String parent : selectedParents) {
						subTypeMap.put(parent, entry.getId());
						superTypeMap.put(entry.getId(), parent);
					}
				}
			} else if (ancestorIds != null) {
				findParentInAncestors(entry, treeItemsById, requiredTopLevelConceptIds, subTypeMap, superTypeMap);
			} else {
				// no parents or ancestors, root element
				subTypeMap.put(null, entry.getId());
			}
		}

		// add TOP levels
		for (SnomedConceptDocument entry : topLevelConcepts) {
			if (!Concepts.ROOT_CONCEPT.equals(entry.getId()) && !treeItemsById.containsKey(entry.getId())) {
				if (subTypeMap.containsKey(entry.getId())) {
					treeItemsById.put(entry.getId(), entry);
				}
			}
		}
		
		
		for (SnomedConceptDocument entry : topLevelConcepts) {
			if (Concepts.ROOT_CONCEPT.equals(entry.getId())) {
				// find all top level child and connect them with the root
				for (SnomedConceptDocument tl : topLevelConcepts) {
					if (!Concepts.ROOT_CONCEPT.equals(tl.getId()) && treeItemsById.containsKey(tl.getId())) {
						subTypeMap.put(entry.getId(), tl.getId());
						superTypeMap.put(tl.getId(), entry.getId());
					}
				}
				
				// only add root concept if the tree contains top level concepts
				if (subTypeMap.containsKey(Concepts.ROOT_CONCEPT)) {
					treeItemsById.put(entry.getId(), entry);
					subTypeMap.put(null, entry.getId());
				}
				
				break;
			}
		}
		
		// fetch all missing components to build the remaining part of the FULL tree
		final Set<String> allRequiredComponents = newHashSet();
		allRequiredComponents.addAll(superTypeMap.keySet());
		allRequiredComponents.addAll(subTypeMap.keySet());
		allRequiredComponents.removeAll(treeItemsById.keySet());
		allRequiredComponents.remove(null);
		
		// fetch required data for all unknown items
		for (SnomedConceptDocument entry : getComponents(branch, allRequiredComponents)) {
			treeItemsById.put(entry.getId(), entry);
		}
		
		return new TerminologyTree(treeItemsById, subTypeMap, superTypeMap);
	}
	
	private Collection<SnomedConceptDocument> getComponents(String branch, Set<String> componentIds) {
		if (CompareUtils.isEmpty(componentIds)) {
			return Collections.emptySet();
		}
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(ImmutableSet.copyOf(componentIds))
				.setLocales(getLocales())
				.setExpand("pt(),parentIds(),ancestorIds()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(getBus())
				.then(SnomedConcepts.TO_DOCS)
				.getSync();
	}

	private List<SnomedConceptDocument> getDefaultTopLevelConcepts(final String branch) {
		final SnomedConcept root = SnomedRequests.prepareGetConcept(Concepts.ROOT_CONCEPT)
				.setExpand(String.format("pt(),%s(direct:true,expand(pt()))", Trees.STATED_FORM.equals(getForm()) ? "statedDescendants" : "descendants"))
				.setLocales(getLocales())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(getBus())
				.getSync();
	
		final Collection<SnomedConcept> requiredTreeItemConcepts = newHashSet();
		requiredTreeItemConcepts.add(root);
		requiredTreeItemConcepts.addAll(root.getDescendants().getItems());
	
		return SnomedConceptDocument.fromConcepts(requiredTreeItemConcepts);
	}
	
	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}
	
	private IEventBus getBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private LongCollection getParents(SnomedConceptDocument entry) {
		switch (getForm()) {
		case Trees.INFERRED_FORM: return entry.getParents();
		case Trees.STATED_FORM: return entry.getStatedParents();
		default: return null;
		}
	}
	
	private LongCollection getAncestors(SnomedConceptDocument entry) {
		switch (getForm()) {
		case Trees.INFERRED_FORM: return entry.getAncestors();
		case Trees.STATED_FORM: return entry.getStatedAncestors();
		default: return null;
		}
	}
	
	private void findParentInAncestors(final SnomedConceptDocument entry, final Map<String, SnomedConceptDocument> treeItemsById,
			final Collection<String> requiredTopLevelConceptIds, final SetMultimap<String, String> subTypeMap, final SetMultimap<String, String> superTypeMap) {
		// try to find a single matching ancestor and hook into that, otherwise we will require additional parentage info about the ancestors
		final Collection<String> ancestors = LongSets.toStringSet(getAncestors(entry));
		final Collection<String> selectedAncestors = newHashSet();
		for (String ancestor : ancestors) {
			if (!requiredTopLevelConceptIds.contains(ancestor) && treeItemsById.containsKey(ancestor)) {
				selectedAncestors.add(ancestor);
			}
		}
		if (selectedAncestors.isEmpty()) {
			// no matching ancestor, try to find the TOP level and hook into that
			for (String ancestor : ancestors) {
				if (requiredTopLevelConceptIds.contains(ancestor) && !Concepts.ROOT_CONCEPT.equals(ancestor)) {
					selectedAncestors.add(ancestor);
				}
			}
			// still no matching ancestor hook into the ROOT if it's in the ancestor list
			if (selectedAncestors.isEmpty() && ancestors.contains(Concepts.ROOT_CONCEPT)) {
				selectedAncestors.add(Concepts.ROOT_CONCEPT);
			}
		}
		
		if (selectedAncestors.isEmpty()) {
			subTypeMap.put(null, entry.getId());
		} else if (selectedAncestors.size() == 1) {
			final String singleAncestor = selectedAncestors.iterator().next();
			subTypeMap.put(singleAncestor, entry.getId());
			superTypeMap.put(entry.getId(), singleAncestor);
		} else {
			final String firstAncestor = Ordering.from(new AlphaNumericComparator()).min(selectedAncestors);
			subTypeMap.put(firstAncestor, entry.getId());
			superTypeMap.put(entry.getId(), firstAncestor);
		}
	}
	
}
