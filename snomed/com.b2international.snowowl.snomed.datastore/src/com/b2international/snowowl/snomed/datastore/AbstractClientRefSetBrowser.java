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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.IClientTerminologyAndRefSetBrowser;
import com.b2international.snowowl.core.api.browser.IFilterClientTerminologyBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyAndRefSetBrowser;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * Abstract {@link IClientTerminologyAndRefSetBrowser} service that delegates to the branch aware 
 * {@link ITerminologyAndRefSetBrowser} service based on the currently specified active branch on the client side.
 * @param <R> - type of the reference set.
 * @param <C> - type of the components.
 * @param <K> - type of the unique component and reference set identifiers.
 * @see IClientTerminologyAndRefSetBrowser
 * @see ITerminologyAndRefSetBrowser
 * @see BranchPathUtils#createActivePath()
 */
public abstract class AbstractClientRefSetBrowser<R extends IComponent<K>, C extends IComponent<K>, K> 
	extends ActiveBranchPathAwareService 
	implements IClientTerminologyAndRefSetBrowser<R, C, K> {

	private final ITerminologyAndRefSetBrowser<R, C, K> wrapperService;

	public AbstractClientRefSetBrowser(final ITerminologyAndRefSetBrowser<R, C, K> wrapperService) {
		this.wrapperService = wrapperService;
	}
	
	/**
	 * Returns with the wrapped delegate service.
	 * @return the delegate service. 
	 */
	public ITerminologyAndRefSetBrowser<R, C, K> getWrapperService() {
		return wrapperService;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#getAllRefSetIds()
	 */
	@Override
	public Collection<K> getAllRefSetIds() {
		return wrapperService.getAllRefSetIds(getBranchPath());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getRootConcepts()
	 */
	@Override
	public Collection<C> getRootConcepts() {
		return wrapperService.getRootConcepts(getBranchPath());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getConcept(java.lang.Object)
	 */
	@Override
	public C getConcept(final K id) {
		return wrapperService.getConcept(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSuperTypes(java.lang.Object)
	 */
	@Override
	public Collection<C> getSuperTypes(final C concept) {
		return wrapperService.getSuperTypes(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSubTypes(java.lang.Object)
	 */
	@Override
	public Collection<C> getSubTypes(final C concept) {
		return wrapperService.getSubTypes(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSubTypesAsList(java.lang.Object)
	 */
	@Override
	public List<C> getSubTypesAsList(final C concept) {
		return wrapperService.getSubTypesAsList(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSuperTypesById(java.lang.Object)
	 */
	@Override
	public Collection<C> getSuperTypesById(final K id) {
		return wrapperService.getSuperTypesById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSubTypesById(java.lang.Object)
	 */
	@Override
	public Collection<C> getSubTypesById(final K id) {
		return wrapperService.getSubTypesById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSuperTypes(java.lang.Object)
	 */
	@Override
	public Collection<C> getAllSuperTypes(final C concept) {
		return wrapperService.getAllSuperTypes(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSuperTypesById(java.lang.Object)
	 */
	@Override
	public Collection<C> getAllSuperTypesById(final K id) {
		return wrapperService.getAllSuperTypesById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSubTypes(java.lang.Object)
	 */
	@Override
	public Collection<C> getAllSubTypes(final C concept) {
		return wrapperService.getAllSubTypes(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSubTypesById(java.lang.Object)
	 */
	@Override
	public Collection<C> getAllSubTypesById(final K id) {
		return wrapperService.getAllSubTypesById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSubTypeCount(java.lang.Object)
	 */
	@Override
	public int getAllSubTypeCount(final C concept) {
		return wrapperService.getAllSubTypeCount(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSubTypeCount(java.lang.Object)
	 */
	@Override
	public int getSubTypeCount(final C concept) {
		return wrapperService.getSubTypeCount(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSuperTypeCount(java.lang.Object)
	 */
	@Override
	public int getAllSuperTypeCount(final C concept) {
		return wrapperService.getAllSuperTypeCount(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSuperTypeCount(java.lang.Object)
	 */
	@Override
	public int getSuperTypeCount(final C concept) {
		return wrapperService.getSuperTypeCount(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSubTypeCountById(java.lang.Object)
	 */
	@Override
	public int getAllSubTypeCountById(final K id) {
		return wrapperService.getAllSubTypeCountById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSubTypeCountById(java.lang.Object)
	 */
	@Override
	public int getSubTypeCountById(final K id) {
		return wrapperService.getSubTypeCountById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getAllSuperTypeCountById(java.lang.Object)
	 */
	@Override
	public int getAllSuperTypeCountById(final K id) {
		return wrapperService.getAllSuperTypeCountById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSuperTypeCountById(java.lang.Object)
	 */
	@Override
	public int getSuperTypeCountById(final K id) {
		return wrapperService.getSuperTypeCountById(getBranchPath(), id);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getTopLevelConcept(java.lang.Object)
	 */
	@Override
	public C getTopLevelConcept(final C concept) {
		return wrapperService.getTopLevelConcept(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#isTerminologyAvailable()
	 */
	@Override
	public boolean isTerminologyAvailable() {
		return wrapperService.isTerminologyAvailable(getBranchPath());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#getMemberCount(java.lang.Object)
	 */
	@Override
	public int getMemberCount(final K refsetId) {
		return wrapperService.getMemberCount(getBranchPath(), refsetId);
	}

	public int getActiveMemberCount(final K refSetId) {
		return wrapperService.getActiveMemberCount(getBranchPath(), refSetId);
	};

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#getMemberConcepts(java.lang.Object)
	 */
	@Override
	public Collection<C> getMemberConcepts(final K refsetId) {
		return wrapperService.getMemberConcepts(getBranchPath(), refsetId);
	}

	@Override
	public java.util.Collection<K> getMemberConceptIds(final K refsetId) {
		return wrapperService.getMemberConceptIds(getBranchPath(), refsetId);
	};

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#getRefSet(java.lang.Object)
	 */
	@Override
	public R getRefSet(final K refSetId) {
		return wrapperService.getRefSet(getBranchPath(), refSetId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#getRefsSets()
	 */
	@Override
	public Iterable<R> getRefsSets() {
		return wrapperService.getRefsSets(getBranchPath());
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#getSuperTypeIds(java.lang.Object)
	 */
	@Override
	public java.util.Collection<K> getSuperTypeIds(final K conceptId) {
		return wrapperService.getSuperTypeIds(getBranchPath(), conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#isSuperTypeOf(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isSuperTypeOf(final C superType, final C subType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#isSuperTypeOfById(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isSuperTypeOfById(final String superTypeId, final String subTypeId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientRefSetBrowser#isReferenced(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReferenced(final K refSetId, final K componentId) {
		return wrapperService.isReferenced(getBranchPath(), refSetId, componentId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#filterTerminologyBrowser(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(final String expression, final IProgressMonitor monitor) {
		return wrapperService.filterTerminologyBrowser(getBranchPath(), expression, monitor);
	}

	@Override
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(final C concept) {
		return wrapperService.getSubTypesWithChildFlag(getBranchPath(), concept);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser#exists(java.lang.String)
	 */
	@Override
	public boolean exists(final String componentId) {
		return wrapperService.exists(getBranchPath(), componentId);
	}
}
