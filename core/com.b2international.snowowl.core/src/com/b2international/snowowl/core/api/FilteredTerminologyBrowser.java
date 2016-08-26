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
package com.b2international.snowowl.core.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

public class FilteredTerminologyBrowser<C extends IComponent<K>, K> implements IFilterClientTerminologyBrowser<C, K>, Serializable {

	private static final long serialVersionUID = -8406487265076249122L;

	private Map<K, C> componentMap;
	private Multimap<K, K> subTypeMap;
	private Multimap<K, K> superTypeMap;
	private Set<K> filteredComponents;

	private FilterTerminologyBrowserType type = FilterTerminologyBrowserType.HIERARCHICAL;

	/**
	 * Only for NULL object subclasses.
	 */
	protected FilteredTerminologyBrowser() {
		
	}
	
	public FilteredTerminologyBrowser(final Map<K, C> componentMap, final Multimap<K, K> subTypeMap, 
			final Multimap<K, K> superTypeMap, final FilterTerminologyBrowserType type, final Set<K> filteredComponents) {
		
		
		this.componentMap = Preconditions.checkNotNull(componentMap, "Component map argument cannot be null.");
		this.subTypeMap = Preconditions.checkNotNull(subTypeMap, "Sub type map argument cannot be null.");
		this.superTypeMap = Preconditions.checkNotNull(superTypeMap, "Super type map argument cannot be null.");
		this.filteredComponents = Preconditions.checkNotNull(filteredComponents, "Filtered components argument cannot be null.");
	}

	@Override
	public Collection<C> getRootConcepts() {
		final Collection<K> selectedComponents = FilterTerminologyBrowserType.FLAT.equals(type) ? filteredComponents : subTypeMap.get(null);  
		return (Collection<C>)getComponents(selectedComponents);
	}

	@Override
	public C getConcept(final K key) {
		return componentMap.get(key);
	}
	
	@Override
	public Iterable<C> getComponents(Iterable<K> ids) {
		if (CompareUtils.isEmpty(ids)) {
			return Collections.emptySet();
		}
		return FluentIterable.from(ids).transform(new Function<K, C>() {
			@Override public C apply(final K componentId) {
				return getConcept(componentId);
			}
		}).toSet();
	}

	public boolean containsAncestors() {
		return !superTypeMap.isEmpty();
	}

	@Override
	public FilterTerminologyBrowserType getType() {
		return type;
	}

	@Override
	public void setType(final FilterTerminologyBrowserType type) {
		this.type = type;
	}

	@Override
	public boolean contains(final K componentId) {
		return filteredComponents.contains(componentId);
	}
	
	@Override
	public int size() {
		return filteredComponents.size();
	}

	@Override
	public Iterable<K> getFilteredIds() {
		return filteredComponents;
	}
	
	@Override
	public Collection<C> getSuperTypesById(final K id) {
		if (FilterTerminologyBrowserType.FLAT.equals(type)) {
			return Collections.emptyList();
		} else {
			return (Collection<C>) getComponents(superTypeMap.get(id));
		}
	}

	@Override
	public Collection<C> getSubTypesById(final K id) {
		if (FilterTerminologyBrowserType.FLAT.equals(type)) {
			return Collections.emptyList();
		} else {
			return (Collection<C>) getComponents(subTypeMap.get(id));
		}
	}

	@Override
	public Collection<C> getAllSuperTypesById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<C> getAllSubTypesById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSubTypeCountById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSubTypeCountById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getAllSuperTypeCountById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public int getSuperTypeCountById(final K id) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean isTerminologyAvailable() {
		return true;
	}

	@Override
	public boolean isSuperTypeOfById(final String superTypeId, final String subTypeId) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public java.util.Collection<K> getSuperTypeIds(final K conceptId) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(final String expression, final IProgressMonitor monitor) {
		return this;
	}

	@Override
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(K conceptId) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean exists(String componentId) {
		return filteredComponents.contains(componentId);
	}
	
	@Override
	public Map<String, Boolean> exist(Collection<String> componentIds) {
		throw new UnsupportedOperationException("Not implemented.");
	}
}
