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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.AlphaNumericComparator;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.EmptyTerminologyBrowser;
import com.b2international.snowowl.core.api.FilteredTerminologyBrowser;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.browser.AbstractClientTerminologyBrowser;
import com.b2international.snowowl.datastore.browser.ActiveBranchClientTerminologyBrowser;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.filteredrefset.FilteredRefSetMemberBrowser2;
import com.b2international.snowowl.snomed.datastore.filteredrefset.IRefSetMemberOperation;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntryWithChildFlag;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;

import bak.pcj.LongCollection;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.set.LongSet;

/**
 * Concept hierarchy browser service for the SNOMED&nbsp;CT ontology.
 * @see AbstractClientTerminologyBrowser
 */
@Client
public class SnomedClientTerminologyBrowser extends ActiveBranchClientTerminologyBrowser<SnomedConceptIndexEntry, String> {

	public static final List<ExtendedLocale> LOCALES = ImmutableList.of(new ExtendedLocale("en", "sg", Concepts.REFSET_LANGUAGE_TYPE_SG), new ExtendedLocale("en", "gb", Concepts.REFSET_LANGUAGE_TYPE_UK));
	
	private final IEventBus bus;

	/**
	 * Creates a new service instance based on the wrapped server side service.
	 * @param wrappedBrowser the wrapped server side service.
	 */
	public SnomedClientTerminologyBrowser(final SnomedTerminologyBrowser wrappedBrowser, final IEventBus bus) {
		super(wrappedBrowser);
		this.bus = checkNotNull(bus, "bus");
	}
	
	@Override
	public IFilterClientTerminologyBrowser<SnomedConceptIndexEntry, String> filterTerminologyBrowser(String expression, IProgressMonitor monitor) {
		final String branch = getBranchPath().getPath();
		final SnomedConcepts matches = SnomedRequests
			.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByTerm(expression)
			.filterByExtendedLocales(LOCALES)
			// expand parent and ancestorIds to get all possible treepaths to the top
			.setExpand("pt(),parentIds(),ancestorIds()")
			.build(branch)
			.executeSync(bus);
		
		if (matches.getItems().isEmpty()) {
			return EmptyTerminologyBrowser.getInstance();
		}
		
		final FluentIterable<SnomedConceptIndexEntry> matchingConcepts = FluentIterable.from(SnomedConceptIndexEntry.fromConcepts(matches));
		final Map<String, SnomedConceptIndexEntry> treeItemsById = newHashMap();
		
		// all matching concepts should be in the componentMap
		treeItemsById.putAll(matchingConcepts.uniqueIndex(ComponentUtils.<String>getIdFunction()));
		
		final Set<String> matchingConceptIds = ImmutableSet.copyOf(treeItemsById.keySet());

		// fetch required ROOT and TOP level concepts
		final List<SnomedConceptIndexEntry> requiredTopLevelConcepts = getRequiredTopLevelConcepts();
		final Collection<String> requiredTopLevelConceptIds = ComponentUtils.getIdSet(requiredTopLevelConcepts);
		
		// compute subType and superType maps for the tree
		final SetMultimap<String, String> superTypeMap = HashMultimap.create();
		final SetMultimap<String, String> subTypeMap = HashMultimap.create();
		
		for (SnomedConceptIndexEntry entry : matchingConcepts) {
			if (entry.getParents() != null) {
				final Collection<String> parents = LongSets.toStringSet(entry.getParents());
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
			} else if (entry.getAncestors() != null) {
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
		for (SnomedConceptIndexEntry entry : getComponents(allRequiredComponents)) {
			treeItemsById.put(entry.getId(), entry);
		}
		
		return new FilteredTerminologyBrowser<SnomedConceptIndexEntry, String>(treeItemsById, subTypeMap, superTypeMap, FilterTerminologyBrowserType.HIERARCHICAL, matchingConceptIds);
	}

	private void findParentInAncestors(final SnomedConceptIndexEntry entry, final Map<String, SnomedConceptIndexEntry> treeItemsById,
			final Collection<String> requiredTopLevelConceptIds, final SetMultimap<String, String> subTypeMap, final SetMultimap<String, String> superTypeMap) {
		// try to find a single matching ancestor and hook into that, otherwise we will require additional parentage info about the ancestors
		final Collection<String> ancestors = LongSets.toStringSet(entry.getAncestors());
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
				.setExpand("pt(),descendants(direct:true,expand(pt()))")
				.setLocales(LOCALES)
				.build(getBranchPath().getPath())
				.executeSync(bus);
		
		final Collection<ISnomedConcept> requiredTreeItemConcepts = newHashSet();
		requiredTreeItemConcepts.add(root);
		requiredTreeItemConcepts.addAll(root.getDescendants().getItems());
		return SnomedConceptIndexEntry.fromConcepts(requiredTreeItemConcepts);
	}
	
	@Override
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(SnomedConceptIndexEntry concept) {
		final SnomedConcepts concepts = SnomedRequests
			.prepareSearchConcept()
			.all()
			.filterByParent(concept.getId())
			.setExpand("pt(),descendants(direct:true,limit:0)")
			.setLocales(LOCALES)
			.build(getBranchPath().getPath())
			.executeSync(bus);
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
				.filterByParent(Long.toString(SnomedMappings.ROOT_ID))
				.setLocales(LOCALES)
				.setExpand("pt()")
				.build(getBranchPath().getPath())
				.executeSync(bus);
		return SnomedConceptIndexEntry.fromConcepts(roots);
	}
	
	@Override
	public SnomedConceptIndexEntry getConcept(String id) {
		try {
			final ISnomedConcept concept = SnomedRequests
					.prepareGetConcept()
					.setComponentId(id)
					.setExpand("pt()")
					.setLocales(LOCALES)
					.build(getBranchPath().getPath())
					.executeSync(bus);
			return SnomedConceptIndexEntry.builder(concept).label(concept.getPt().getTerm()).build();
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	@Override
	public Collection<SnomedConceptIndexEntry> getSubTypesById(String id) {
		final SnomedConcepts concepts = SnomedRequests
				.prepareSearchConcept()
				.all()
				.filterByParent(id)
				.setExpand("pt()")
				.setLocales(LOCALES)
				.build(getBranchPath().getPath())
				.executeSync(bus);
		return SnomedConceptIndexEntry.fromConcepts(concepts);
	}

	/**
	 * Returns with an iterable of all SNOMED&nbsp;CT concepts for the currently active branch. 
	 * @return an iterable of all SNOMED&nbsp;CT concepts.
	 */
	public Iterable<SnomedConceptIndexEntry> getConcepts() {
		return getWrappedService().getConcepts(getBranchPath());
	}

	/**
	 * Returns with the number of all SNOMED&nbsp;CT concepts for the currently active branch.
	 * @return the number of all SNOMED&nbsp;CT concepts.
	 */
	public int getConceptCount() {
		return getWrappedService().getConceptCount(getBranchPath());
	}

	/**
	 * Returns with a set of all active descendant concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active descendant concepts.
	 */
	public LongSet getAllSubTypeIds(final long conceptId) {
		return getWrappedService().getAllSubTypeIds(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a set of the active direct descendant concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of the active direct descendant concepts.
	 */
	public LongSet getSubTypeIds(final long conceptId) {
		return getWrappedService().getSubTypeIds(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a set of the active direct ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of the active direct ancestor concepts.
	 */
	public LongSet getSuperTypeIds(final long conceptId) {
		return getWrappedService().getSuperTypeIds(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a set of all active ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active ancestor concepts.
	 */
	public LongSet getAllSuperTypeIds(final long conceptId) {
		return getWrappedService().getAllSuperTypeIds(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a set of the active direct descendant concept storage keys of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept storage keys of the active direct descendant concepts.
	 */
	public LongSet getSubTypeStorageKeys(final String conceptId) {
		return getWrappedService().getSubTypeStorageKeys(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a set of all active descendant concept storage keys of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept storage keys of all active descendant concepts.
	 */
	public LongSet getAllSubTypeStorageKeys(final String conceptId) {
		return getWrappedService().getAllSubTypeStorageKeys(getBranchPath(), conceptId);
	}

	/**
	 * Returns with a 2D array of IDs and storage keys of all the active SNOMED&nbsp;CT concepts from the ontology.
	 * <p>The first dimension is the concept ID the second dimension is the storage key (CDO ID).
	 * @return a 2D array of concept IDs and storage keys.
	 */
	public long[][] getAllActiveConceptIdsStorageKeys() {
		return getWrappedService().getAllActiveConceptIdsStorageKeys(getBranchPath());
	}

	/**
	 * Maps all active SNOMED&nbsp;CT concept identifiers to their corresponding storage keys in the ontology.
	 * <p>Map keys are concept IDs, values are concept storage keys (CDO ID).
	 * @param branchPath the branch path.
	 * @return a map of concept IDs and storage keys.
	 */
	public LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath) {
		return getWrappedService().getConceptIdToStorageKeyMap(getBranchPath());
	}

	/**
	 * Returns all active SNOMED&nbsp;CT concept identifiers from the ontology.
	 * @param branchPath the branch path.
	 * @return a collection of concept IDs for all active concepts.;
	 */
	public LongCollection getAllActiveConceptIds() {
		return getWrappedService().getAllActiveConceptIds(getBranchPath());
	}

	/**
	 * Returns {@code true} only and if only the specified SNOMED&nbsp;CT <b>core</b> component ID does not
	 * exist in the store. Otherwise it returns with {@code false}.
	 * <p><b>NOTE:&nbsp;</b>this method is not aware of checking reference set and reference set members IDs.
	 * In case of checking *NON* core component IDs, this method returns {@code false}.
	 * @param componentId the SNOMED&nbsp;CT core component ID to check.
	 * @return {@code true} if the ID is unique, otherwise returns with {@code false}.
	 */
	public boolean isUniqueId(final String componentId) {
		return getWrappedService().isUniqueId(getBranchPath(), componentId);
	}

	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT concept exists with the given unique ID.
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	public boolean exists(final String conceptId) {
		return getWrappedService().exists(getBranchPath(), conceptId);
	}

	/**
	 * Returns {@code true} only and if only a SNOMED&nbsp;CT concept given by its ID is contained by the subset of SNOMED&nbsp;CT concepts specified
	 * as the query expression. Otherwise returns with {@code false}.
	 * @param expression the expression representing a bunch of concepts.
	 * @param conceptId the ID of the SNOMED&nbsp;CT concept to check. 
	 * @return {@code true} if the concept is in the subset of concepts represented as the expression. 
	 */
	public boolean contains(final String expression, final String conceptId) {
		return getWrappedService().contains(getBranchPath(), expression, conceptId);
	}

	/**
	 * Builds a taxonomy among the referenced components of a SNOMED&nbsp;CT reference set.
	 * @param refSetId the reference set identifier concept ID.
	 * @param filterExpression the query expression. Used for filtering reference set member referenced components based on their labels. Can be {@code null} if nothing to filter.
	 * @param includeInactiveMembers {@code false} if inactive reference set members should be excluded. Otherwise {@code true}.
	 * @param pendingOperations a list of uncommitted reference set member manipulating operations to take into account when filtering.
	 * @return a filtered taxonomy for referenced components of reference set members.
	 */
	public FilteredRefSetMemberBrowser2 createFilteredRefSetBrowser(final IBranchPath branchPath, 
			final long refSetId, 
			@Nullable final String filterExpression, 
			final boolean includeInactiveMembers, 
			final List<IRefSetMemberOperation> pendingOperations) {

		return getWrappedService().createFilteredRefSetBrowser(getBranchPath(), refSetId, filterExpression, includeInactiveMembers, pendingOperations);
	}

	/**
	 * Returns the sub types of the specified concept, with an additional boolean flag to indicate whether the concept has children or not.
	 * 
	 * @param concept the concept
	 * @return the sub types with additional child flag
	 */
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildInformation(final SnomedConceptIndexEntry concept) {
		return getWrappedService().getSubTypesWithChildFlag(getBranchPath(), concept);
	}

	/**
	 * Returns with a collection of concepts given with the concept unique IDs.
	 * <p>If the IDs argument references a non existing concept, then that concept will
	 * be omitted from the result set, instead of populating its value as {@code null}.  
	 * @param ids the unique IDs for the collection.
	 * @return a collection of concepts.
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getComponents(final Iterable<String> ids) {
		if (CompareUtils.isEmpty(ids)) {
			return Collections.emptySet();
		}
		final SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
				.all()
				.setComponentIds(ImmutableSet.copyOf(ids))
				.setLocales(LOCALES)
				.setExpand("pt(),parentIds()")
				.build(getBranchPath().getPath())
				.executeSync(bus);
		return SnomedConceptIndexEntry.fromConcepts(concepts);
	}
	
	/**
	 * Returns with the depth of the current concept from the taxonomy.
	 * The depth of a node is the number of edges from the node to the tree's root node.
	 * <br>A root node will have a depth of 0.
	 * @param conceptId the concept ID of the focus concept/node.
	 * @return the height of the node in the taxonomy.
	 */
	public int getDepth(final String conceptId) {
		return getWrappedService().getDepth(getBranchPath(), conceptId);
	}
	
	/**
	 * Returns with the height of the current concept from the taxonomy.
	 * The height of a node is the number of edges on the longest path from the node to a leaf.
	 * <br>A leaf node will have a height of 0.
	 * @param conceptId the concept ID of the focus concept/node.
	 * @return the height of the node in the taxonomy.
	 */
	public int getHeight(final String conceptId) {
		return getWrappedService().getHeight(getBranchPath(), conceptId);
	}
	
	private SnomedTerminologyBrowser getWrappedService() {
		return (SnomedTerminologyBrowser) getWrappedBrowser();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.BranchPathAwareService#getEPackage()
	 */
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}