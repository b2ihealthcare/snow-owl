/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.*;
import java.util.stream.Collectors;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.commons.AlphaNumericComparator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.*;

/**
 * @since 4.6
 */
abstract class TreeBuilderImpl implements TreeBuilder {

	private final List<ExtendedLocale> locales;
	private Collection<SnomedConcept> topLevelConcepts;
	
	TreeBuilderImpl(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	abstract String getForm();
	
	@Override
	public final TreeBuilder withTopLevelConcepts(final Collection<SnomedConcept> topLevelConcepts) {
		this.topLevelConcepts = topLevelConcepts;
		return this;
	}
	
	@Override
	public TerminologyTree build(final ResourceURI resource, final Iterable<SnomedConcept> nodes, final String snomedDescriptionExpand) {
		final Collection<SnomedConcept> topLevelConcepts = this.topLevelConcepts == null ? 
				getDefaultTopLevelConcepts(resource, snomedDescriptionExpand) : this.topLevelConcepts;
		
		final Map<String, SnomedConcept> treeItemsById = newHashMap();
		
		// all matching concepts should be in the componentMap
		treeItemsById.putAll(FluentIterable.from(nodes).uniqueIndex(IComponent::getId));
		
		final Collection<String> requiredTopLevelConceptIds = topLevelConcepts.stream().map(IComponent::getId).collect(Collectors.toSet());
		
		// compute subType and superType maps for the tree
		final SetMultimap<String, String> superTypeMap = HashMultimap.create();
		final SetMultimap<String, String> subTypeMap = HashMultimap.create();
		
		for (SnomedConcept entry : nodes) {
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
		for (SnomedConcept entry : topLevelConcepts) {
			if (!Concepts.ROOT_CONCEPT.equals(entry.getId()) && !treeItemsById.containsKey(entry.getId())) {
				if (subTypeMap.containsKey(entry.getId())) {
					treeItemsById.put(entry.getId(), entry);
				}
			}
		}
		
		
		for (SnomedConcept entry : topLevelConcepts) {
			if (Concepts.ROOT_CONCEPT.equals(entry.getId())) {
				// find all top level child and connect them with the root
				for (SnomedConcept tl : topLevelConcepts) {
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
		for (SnomedConcept entry : getComponents(resource, allRequiredComponents, snomedDescriptionExpand)) {
			treeItemsById.put(entry.getId(), entry);
		}
		
		return new TerminologyTree(treeItemsById, subTypeMap, superTypeMap);
	}
	
	private Iterable<SnomedConcept> getComponents(ResourceURI resource, Set<String> componentIds, final String snomedDescriptionExpand) {
		if (CompareUtils.isEmpty(componentIds)) {
			return Collections.emptySet();
		}
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(ImmutableSet.copyOf(componentIds))
				.setLocales(locales)
				.setExpand(snomedDescriptionExpand + ",parentIds(),ancestorIds()")
				.build(resource)
				.execute(getBus())
				.getSync();
	}

	private Collection<SnomedConcept> getDefaultTopLevelConcepts(final ResourceURI resource, final String snomedDescriptionExpand) {
		final SnomedConcept root = SnomedRequests.prepareGetConcept(Concepts.ROOT_CONCEPT)
				.setExpand(String.format("%2$s,%s(direct:true,expand(%2$s))", Trees.STATED_FORM.equals(getForm()) ? "statedDescendants" : "descendants", snomedDescriptionExpand))
				.setLocales(locales)
				.build(resource)
				.execute(getBus())
				.getSync();
	
		final Collection<SnomedConcept> requiredTreeItemConcepts = newHashSet();
		requiredTreeItemConcepts.add(root);
		requiredTreeItemConcepts.addAll(root.getDescendants().getItems());
	
		return requiredTreeItemConcepts;
	}
	
	private IEventBus getBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private LongCollection getParents(SnomedConcept entry) {
		switch (getForm()) {
		case Trees.INFERRED_FORM: return PrimitiveSets.newLongOpenHashSet(entry.getParentIds());
		case Trees.STATED_FORM: return PrimitiveSets.newLongOpenHashSet(entry.getStatedParentIds());
		default: return null;
		}
	}
	
	private LongCollection getAncestors(SnomedConcept entry) {
		switch (getForm()) {
		case Trees.INFERRED_FORM: return PrimitiveSets.newLongOpenHashSet(entry.getAncestorIds());
		case Trees.STATED_FORM: return PrimitiveSets.newLongOpenHashSet(entry.getStatedAncestorIds());
		default: return null;
		}
	}
	
	private void findParentInAncestors(final SnomedConcept entry, final Map<String, SnomedConcept> treeItemsById,
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
