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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.api.browser.FilterTerminologyBrowserType;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;

/**
 * Empty implementation of the {@link IClientTerminologyBrowser} interface.
 * @see IClientTerminologyBrowser
 * @see #INSTANCE
 */
public final class EmptyTerminologyBrowser extends FilteredTerminologyBrowser<IComponent<Object>, Object> {

	private static final long serialVersionUID = 8800530445104572109L;

	/**The singleton instance implementation.*/
	private static final EmptyTerminologyBrowser INSTANCE = new EmptyTerminologyBrowser();
	
	/**
	 * Returns with the empty implementation of the terminology independent component browser.
	 * @return the {@link #INSTANCE empty implementation} singleton instance.
	 */
	@SuppressWarnings("unchecked")
	public static <C extends IComponent<K>, K> FilteredTerminologyBrowser<C, K> getInstance() {
		return (FilteredTerminologyBrowser<C, K>) INSTANCE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.FilteredTerminologyBrowser#setType(com.b2international.snowowl.core.api.FilteredTerminologyBrowser.Type)
	 */
	@Override
	public void setType(final FilterTerminologyBrowserType type) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.FilteredTerminologyBrowser#getType()
	 */
	@Override
	public FilterTerminologyBrowserType getType() {
		return FilterTerminologyBrowserType.FLAT;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.FilteredTerminologyBrowser#getRootConcepts()
	 */
	@Override
	public Collection<IComponent<Object>> getRootConcepts() {
		return Collections.emptyList();
	}
	
	@Override
	public Set<Object> getFilteredIds() {
		return Collections.emptySet();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.FilteredTerminologyBrowser#getConcept(java.lang.Object)
	 */
	@Override
	public IComponent<Object> getConcept(final Object key) {
		return NullComponent.getNullImplementation();
	}
	
	@Override
	public boolean contains(Object componentId) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	private EmptyTerminologyBrowser() {
		super();
	}
}