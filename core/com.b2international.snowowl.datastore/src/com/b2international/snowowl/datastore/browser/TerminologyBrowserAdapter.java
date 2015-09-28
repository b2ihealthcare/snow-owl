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
package com.b2international.snowowl.datastore.browser;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;


public class TerminologyBrowserAdapter<C, K> implements IClientTerminologyBrowser<C, K> {
	
	protected final IClientTerminologyBrowser<C, K> delegate;

	public TerminologyBrowserAdapter(final IClientTerminologyBrowser<C, K> delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Collection<C> getRootConcepts() {
		return delegate.getRootConcepts();
	}

	@Override
	public C getConcept(final K key) {
		return delegate.getConcept(key);
	}

	@Override
	public Collection<C> getSuperTypes(final C concept) {
		return delegate.getSuperTypes(concept);
	}

	@Override
	public Collection<C> getSubTypes(final C concept) {
		return delegate.getSubTypes(concept);
	}

	@Override
	public Collection<C> getSuperTypesById(final K id) {
		return delegate.getSuperTypesById(id);
	}

	@Override
	public Collection<C> getSubTypesById(final K id) {
		return delegate.getSubTypesById(id);
	}

	@Override
	public List<C> getSubTypesAsList(final C concept) {
		return delegate.getSubTypesAsList(concept);
	}

	public Collection<C> getAllSubTypes(final C concept) {
		return delegate.getAllSubTypes(concept);
	}

	public Collection<C> getAllSuperTypes(final C concept) {
		return delegate.getAllSuperTypes(concept);
	}

	public int getAllSubTypeCount(final C concept) {
		return delegate.getAllSubTypeCount(concept);
	}

	public int getSubTypeCount(final C concept) {
		return delegate.getSubTypeCount(concept);
	}

	public int getAllSuperTypeCount(final C concept) {
		return delegate.getAllSuperTypeCount(concept);
	}

	public int getSuperTypeCount(final C concept) {
		return delegate.getSuperTypeCount(concept);
	}

	@Override
	public Collection<C> getAllSuperTypesById(final K id) {
		return delegate.getAllSuperTypesById(id);
	}

	@Override
	public Collection<C> getAllSubTypesById(final K id) {
		return delegate.getAllSubTypesById(id);
	}

	@Override
	public int getAllSubTypeCountById(final K id) {
		return delegate.getAllSuperTypeCountById(id);
	}

	@Override
	public int getSubTypeCountById(final K id) {
		return delegate.getSubTypeCountById(id);
	}

	@Override
	public int getAllSuperTypeCountById(final K id) {
		return delegate.getAllSuperTypeCountById(id);
	}

	@Override
	public int getSuperTypeCountById(final K id) {
		return delegate.getSuperTypeCountById(id);
	}

	@Override
	public C getTopLevelConcept(final C concept) {
		return delegate.getTopLevelConcept(concept);
	}

	@Override
	public boolean isTerminologyAvailable() {
		return delegate.isTerminologyAvailable();
	}

	@Override
	public boolean isSuperTypeOf(C superType, C subType) {
		return delegate.isSuperTypeOf(superType, subType);
	}

	@Override
	public boolean isSuperTypeOfById(String superTypeId, String subTypeId) {
		return delegate.isSuperTypeOfById(superTypeId, subTypeId);
	}
	
	@Override
	public java.util.Collection<K> getSuperTypeIds(K conceptId) {
		return delegate.getSuperTypeIds(conceptId);
	}

	@Override
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(String expression, IProgressMonitor monitor) {
		return delegate.filterTerminologyBrowser(expression, monitor);
	}

	@Override
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(C concept) {
		return delegate.getSubTypesWithChildFlag(concept);
	}

	@Override
	public boolean exists(String componentId) {
		return delegate.exists(componentId);
	}
}
