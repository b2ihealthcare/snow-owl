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
package com.b2international.snowowl.snomed.core.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.datastore.BaseSnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;

import com.b2international.commons.collections.primitive.LongCollection;

/**
 * @since 4.6
 */
final class TreeBuilderImpl implements TreeBuilder {

	private final String branch;
	private final List<ExtendedLocale> locales;
	private final BaseSnomedClientTerminologyBrowser browser;
	private final IEventBus bus;
	private final String form;

	TreeBuilderImpl(String form, String branch, List<ExtendedLocale> locales, BaseSnomedClientTerminologyBrowser browser, IEventBus bus) {
		switch (form) {
		case Trees.INFERRED_FORM: 
		case Trees.STATED_FORM:
			break;
		default:
			throw new NotImplementedException("Tree Form is unsupported: %s", form);
		}
		this.form = form;
		this.bus = checkNotNull(bus);
		this.browser = checkNotNull(browser);
		this.branch = checkNotNull(branch);
		this.locales = checkNotNull(locales);
	}
	
	@Override
	public TerminologyTree build(Iterable<SnomedConceptIndexEntry> nodes) {
		final Map<String, SnomedConceptIndexEntry> treeItemsById = newHashMap();
		
		// all matching concepts should be in the componentMap
		treeItemsById.putAll(FluentIterable.from(nodes).uniqueIndex(ComponentUtils.<String>getIdFunction()));
		
		// fetch required ROOT and TOP level concepts
		final List<SnomedConceptIndexEntry> requiredTopLevelConcepts = getRequiredTopLevelConcepts();
		final Collection<String> requiredTopLevelConceptIds = ComponentUtils.getIdSet(requiredTopLevelConcepts);
		
		// compute subType and superType maps for the tree
		final SetMultimap<String, String> superTypeMap = HashMultimap.create();
		final SetMultimap<String, String> subTypeMap = HashMultimap.create();
		
		for (SnomedConceptIndexEntry entry : nodes) {
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
		for (SnomedConceptIndexEntry entry : requiredTopLevelConcepts) {
			if (!Concepts.ROOT_CONCEPT.equals(entry.getId()) && !treeItemsById.containsKey(entry.getId())) {
				if (subTypeMap.containsKey(entry.getId())) {
					treeItemsById.put(entry.getId(), entry);
				}
			}
		}
		
		
		for (SnomedConceptIndexEntry entry : requiredTopLevelConcepts) {
			if (Concepts.ROOT_CONCEPT.equals(entry.getId())) {
				// find all top level child and connect them with the root
				for (SnomedConceptIndexEntry tl : requiredTopLevelConcepts) {
					if (!Concepts.ROOT_CONCEPT.equals(tl.getId()) && treeItemsById.containsKey(tl.getId())) {
						subTypeMap.put(entry.getId(), tl.getId());
						superTypeMap.put(tl.getId(), entry.getId());
					}
				}
				treeItemsById.put(entry.getId(), entry);
				subTypeMap.put(null, entry.getId());
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
		for (SnomedConceptIndexEntry entry : browser.getComponents(allRequiredComponents)) {
			treeItemsById.put(entry.getId(), entry);
		}
		
		return new TerminologyTree(treeItemsById, subTypeMap, superTypeMap);
	}

	private LongCollection getParents(SnomedConceptIndexEntry entry) {
		switch (form) {
		case Trees.INFERRED_FORM: return entry.getParents();
		case Trees.STATED_FORM: return entry.getStatedParents();
		default: return null;
		}
	}
	
	private LongCollection getAncestors(SnomedConceptIndexEntry entry) {
		switch (form) {
		case Trees.INFERRED_FORM: return entry.getAncestors();
		case Trees.STATED_FORM: return entry.getStatedAncestors();
		default: return null;
		}
	}
	
	private void findParentInAncestors(final SnomedConceptIndexEntry entry, final Map<String, SnomedConceptIndexEntry> treeItemsById,
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

	private List<SnomedConceptIndexEntry> getRequiredTopLevelConcepts() {
		final ISnomedConcept root = SnomedRequests
				.prepareGetConcept()
				.setComponentId(Concepts.ROOT_CONCEPT)
				.setExpand("pt(),descendants(form:\"inferred\",direct:true,expand(pt()))")
				.setLocales(locales)
				.build(branch)
				.executeSync(bus);
		
		final Collection<ISnomedConcept> requiredTreeItemConcepts = newHashSet();
		requiredTreeItemConcepts.add(root);
		requiredTreeItemConcepts.addAll(root.getDescendants().getItems());
		return SnomedConceptIndexEntry.fromConcepts(requiredTreeItemConcepts);
	}
	
}
