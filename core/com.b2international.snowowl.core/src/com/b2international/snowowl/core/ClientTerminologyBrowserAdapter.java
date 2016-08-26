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
package com.b2international.snowowl.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.EmptyTerminologyBrowser;
import com.b2international.snowowl.core.api.FilteredTerminologyBrowser;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;

/**
 * Allows selective implementation of {@link IClientTerminologyBrowser} methods.
 * 
 *
 * @param <C>
 * @param <K>
 */
public class ClientTerminologyBrowserAdapter<C, K> implements IClientTerminologyBrowser<C, K>{

	@Override
	public Collection<C> getRootConcepts() {
		return Collections.emptyList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public C getConcept(final K id) {
		return (C) NullComponent.<C>getNullImplementation();
	}
	
	@Override
	public Iterable<C> getComponents(Iterable<K> ids) {
		return Collections.emptyList();
	}

	@Override
	public Collection<K> getSuperTypeIds(final K conceptId) {
		return Collections.emptyList();
	}

	@Override
	public Collection<C> getSuperTypesById(final K id) {
		return Collections.emptyList();
	}

	@Override
	public Collection<C> getSubTypesById(final K id) {
		return Collections.emptyList();
	}

	@Override
	public Collection<C> getAllSuperTypesById(final K id) {
		return Collections.emptyList();
	}

	@Override
	public Collection<C> getAllSubTypesById(final K id) {
		return Collections.emptyList();
	}

	@Override
	public int getAllSubTypeCountById(final K id) {
		return 0;
	}

	@Override
	public int getSubTypeCountById(final K id) {
		return 0;
	}

	@Override
	public int getAllSuperTypeCountById(final K id) {
		return 0;
	}

	@Override
	public int getSuperTypeCountById(final K id) {
		return 0;
	}

	@Override
	public boolean isTerminologyAvailable() {
		return true;
	}

	@Override
	public boolean isSuperTypeOfById(final String superTypeId, final String subTypeId) {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(final String expression, final IProgressMonitor monitor) {
		final FilteredTerminologyBrowser<IComponent<Object>, Object> instance = EmptyTerminologyBrowser.getInstance();
		return (IFilterClientTerminologyBrowser<C, K>) instance;
	}

	@Override
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(final K componentId) {
		return Collections.emptyList();
	}

	@Override
	public boolean exists(final String componentId) {
		return false;
	}
	
	@Override
	public Map<String, Boolean> exist(Collection<String> componentIds) {
		throw new UnsupportedOperationException("Not implemented.");
	}
}
