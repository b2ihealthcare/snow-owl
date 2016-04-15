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
package com.b2international.snowowl.snomed.datastore.filteredrefset;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.collections.ints.IntIterator;
import com.b2international.collections.ints.IntKeyMap;
import com.b2international.collections.ints.IntSet;
import com.b2international.commons.arrays.BidiMapWithInternalId;
import com.b2international.snowowl.core.ClientTerminologyBrowserAdapter;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.google.common.collect.Lists;

/**
 * @deprecated unsupported, will be removed in 4.7
 */
public class FilteredRefSetMemberBrowser2 
		extends ClientTerminologyBrowserAdapter<IRefSetMemberNode, IRefSetMemberNode> 
		implements IFilterClientTerminologyBrowser<IRefSetMemberNode, IRefSetMemberNode>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> nodeSet;
	
	private final IntKeyMap subTypeMap;
	
	private final IntKeyMap superTypeMap;
	
	private FilterTerminologyBrowserType type = FilterTerminologyBrowserType.HIERARCHICAL;
	
	/**
	 * 
	 * @param nodeSet
	 * @param subTypeMap
	 * @param superTypeMap
	 */
	public FilteredRefSetMemberBrowser2(final BidiMapWithInternalId<IRefSetMemberNode, IRefSetMemberNode> nodeSet, final IntKeyMap subTypeMap, final IntKeyMap superTypeMap) {
		this.nodeSet = nodeSet;
		this.subTypeMap = subTypeMap;
		this.superTypeMap = superTypeMap;
	}

	@Override
	public boolean contains(final IRefSetMemberNode node) {
		return nodeSet.getInternalId(node) >= 0;
	}

	@Override
	public int size() {
		return nodeSet.size();
	}

	@Override
	public Iterable<IRefSetMemberNode> getFilteredIds() {
		return nodeSet.getElements();
	}

	@Override
	public void setType(final FilterTerminologyBrowserType type) {
		this.type = type;
	}

	@Override
	public FilterTerminologyBrowserType getType() {
		return type;
	}
	
	@Override
	public boolean isTerminologyAvailable() {
		return true;
	}
	
	@Override
	public Collection<IRefSetMemberNode> getSuperTypesById(final IRefSetMemberNode node) {
		return super.getSuperTypes(node);
	}
	
	@Override
	public Collection<IRefSetMemberNode> getSubTypesById(final IRefSetMemberNode node) {
		return super.getSubTypes(node);
	}
	
	@Override
	public List<IRefSetMemberNode> getSubTypesAsList(final IRefSetMemberNode node) {
		return getSubTypes(node);
	}
	
	@Override
	public List<IRefSetMemberNode> getSuperTypes(final IRefSetMemberNode node) {
		return getHierarchicalNodes(superTypeMap, node);
	}
	
	@Override
	public List<IRefSetMemberNode> getSubTypes(final IRefSetMemberNode node) {
		return getHierarchicalNodes(subTypeMap, node);
	}
	
	@Override
	public Collection<IRefSetMemberNode> getRootConcepts() {
		if(FilterTerminologyBrowserType.FLAT.equals(type)) {
			return Lists.newArrayList(nodeSet.getElements());
		} else {			
			return getHierarchicalNodes(subTypeMap, -1);
		}
	}

	private List<IRefSetMemberNode> getHierarchicalNodes(final IntKeyMap multimap, final IRefSetMemberNode node) {
		
		if (FilterTerminologyBrowserType.FLAT.equals(type)) {
			return Collections.emptyList();
		}
		
		final int internalId = nodeSet.getInternalId(node);
		
		if (internalId < 0) {
			return Collections.emptyList();
		}
		
		return getHierarchicalNodes(multimap, internalId);
	}

	private List<IRefSetMemberNode> getHierarchicalNodes(final IntKeyMap multimap, final int internalId) {
		
		final IntSet subTypeInternalIds = (IntSet) multimap.get(internalId);
		
		if (subTypeInternalIds == null) {
			return Collections.emptyList();
		}
		
		final List<IRefSetMemberNode> result = newArrayList();
		
		for (final IntIterator itr = subTypeInternalIds.iterator(); itr.hasNext(); /* empty */) {
			result.add(nodeSet.get(itr.next()));
		}
		
		return result;
	}
}