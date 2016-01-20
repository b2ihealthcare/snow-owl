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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.datastore.BranchPathAwareService;

/**
 * Abstract implementation of {@link IClientTerminologyBrowser} that delegates to a branch-aware
 * {@link ITerminologyBrowser}.
 * 
 * 
 * @param <C>
 * @param <K>
 */
public abstract class AbstractClientTerminologyBrowser<C extends IComponent<K>, K> implements IClientTerminologyBrowser<C, K>, BranchPathAwareService {

	private final ITerminologyBrowser<C, K> wrappedBrowser;

	public AbstractClientTerminologyBrowser(final ITerminologyBrowser<C, K> wrappedBrowser) {
		this.wrappedBrowser = checkNotNull(wrappedBrowser, "wrappedBrowser");
	}

	@Override
	public Collection<C> getRootConcepts() {
		return wrappedBrowser.getRootConcepts(getBranchPath());
	}

	@Override
	public C getConcept(K id) {
		return wrappedBrowser.getConcept(getBranchPath(), id);
	}
	
	@Override
	public Iterable<C> getComponents(Iterable<K> ids) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Collection<C> getSuperTypes(C concept) {
		return wrappedBrowser.getSuperTypes(getBranchPath(), concept);
	}

	@Override
	public java.util.Collection<K> getSuperTypeIds(K conceptId) {
		return wrappedBrowser.getSuperTypeIds(getBranchPath(), conceptId);
	};
	
	@Override
	public Collection<C> getSubTypes(C concept) {
		return wrappedBrowser.getSubTypes(getBranchPath(), concept);
	}

	@Override
	public List<C> getSubTypesAsList(C concept) {
		return wrappedBrowser.getSubTypesAsList(getBranchPath(), concept);
	}

	@Override
	public Collection<C> getSuperTypesById(K id) {
		return wrappedBrowser.getSuperTypesById(getBranchPath(), id);
	}

	@Override
	public Collection<C> getSubTypesById(K id) {
		return wrappedBrowser.getSubTypesById(getBranchPath(), id);
	}

	@Override
	public Collection<C> getAllSuperTypes(C concept) {
		return wrappedBrowser.getAllSuperTypes(getBranchPath(), concept);
	}

	@Override
	public Collection<C> getAllSuperTypesById(K id) {
		return wrappedBrowser.getAllSuperTypesById(getBranchPath(), id);
	}

	@Override
	public Collection<C> getAllSubTypes(C concept) {
		return wrappedBrowser.getAllSubTypes(getBranchPath(), concept);
	}

	@Override
	public Collection<C> getAllSubTypesById(K id) {
		return wrappedBrowser.getAllSubTypesById(getBranchPath(), id);
	}

	@Override
	public int getAllSubTypeCount(C concept) {
		return wrappedBrowser.getAllSubTypeCount(getBranchPath(), concept);
	}

	@Override
	public int getSubTypeCount(C concept) {
		return wrappedBrowser.getSubTypeCount(getBranchPath(), concept);
	}

	@Override
	public int getAllSuperTypeCount(C concept) {
		return wrappedBrowser.getAllSuperTypeCount(getBranchPath(), concept);
	}

	@Override
	public int getSuperTypeCount(C concept) {
		return wrappedBrowser.getSuperTypeCount(getBranchPath(), concept);
	}

	@Override
	public int getAllSubTypeCountById(K id) {
		return wrappedBrowser.getAllSubTypeCountById(getBranchPath(), id);
	}

	@Override
	public int getSubTypeCountById(K id) {
		return wrappedBrowser.getSubTypeCountById(getBranchPath(), id);
	}

	@Override
	public int getAllSuperTypeCountById(K id) {
		return wrappedBrowser.getAllSuperTypeCountById(getBranchPath(), id);
	}

	@Override
	public int getSuperTypeCountById(K id) {
		return wrappedBrowser.getSuperTypeCountById(getBranchPath(), id);
	}

	@Override
	public C getTopLevelConcept(C concept) {
		return wrappedBrowser.getTopLevelConcept(getBranchPath(), concept);
	}
	
	@Override
	public boolean isTerminologyAvailable() {
		return wrappedBrowser.isTerminologyAvailable(getBranchPath());
	}

	@Override
	public boolean isSuperTypeOf(C superType, C subType) {
		return wrappedBrowser.isSuperTypeOf(getBranchPath(), superType, subType);
	}

	@Override
	public boolean isSuperTypeOfById(String superTypeId, String subTypeId) {
		return wrappedBrowser.isSuperTypeOfById(getBranchPath(), superTypeId, subTypeId);
	}

	@Override
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(String expression, IProgressMonitor monitor) {
		return wrappedBrowser.filterTerminologyBrowser(getBranchPath(), expression, monitor);
	}
	
	@Override
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(C concept) {
		return wrappedBrowser.getSubTypesWithChildFlag(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String componentId) {
		return wrappedBrowser.exists(getBranchPath(), componentId);
	}
	
	@Override
	public Map<String, Boolean> exist(final Collection<String> componentIds) {
		return wrappedBrowser.exist(getBranchPath(), componentIds);
	}
	
	/**
	 * @return the wrappedBrowser
	 */
	public ITerminologyBrowser<C, K> getWrappedBrowser() {
		return wrappedBrowser;
	} 
}