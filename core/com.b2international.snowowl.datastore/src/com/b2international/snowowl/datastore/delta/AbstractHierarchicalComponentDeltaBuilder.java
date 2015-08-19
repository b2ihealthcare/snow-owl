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
package com.b2international.snowowl.datastore.delta;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.ChangeKind;
import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexEntry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

/**
 * Extended {@link AbstractComponentDeltaBuilder component delta builder} responsible for 
 * building and providing taxonomy information about the {@link HierarchicalComponentDelta component delta}s.
 * @see AbstractComponentDeltaBuilder
 * @see HierarchicalComponentDelta
 */
public abstract class AbstractHierarchicalComponentDeltaBuilder<C extends HierarchicalComponentDelta> extends AbstractComponentDeltaBuilder<C> {
	
	/**Supplier for the root component IDs in the terminology.*/
	private Supplier<Collection<String>> rootConceptIdSupplier = Suppliers.memoize(new Supplier<Collection<String>>() {
		@Override public Collection<String> get() {
			return getTerminologyBrowser().getRootConceptIds(getBranchPath());
		}
	});
	
	private boolean terminologyAvailable; 

	@Override
	protected void preProcess() {
		super.preProcess();
		terminologyAvailable = getTerminologyBrowser().isTerminologyAvailable(getBranchPath());
	}
	
	protected boolean isTerminologyAvailable() {
		return terminologyAvailable;
	}
	
	/**
	 * Builds the taxonomy among the component deltas.
	 */
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractComponentDeltaBuilder#postProcess()
	 */
	@Override
	protected void postProcess() {
		
		//process new and dirty objects first
		for (final Iterator<C> itr = getDeltaIterator(); itr.hasNext();  /* */) {
	
			boolean _stop = false;
			C component = itr.next();

			while (!_stop) { //recursively built taxonomy from bottom to top

				_stop = null != component.getParent();

				if (!_stop) { //already found and processed the ancestor

					_stop = isRoot(component);

					if (!_stop) { //root node

						// Try the terminology browser-based approach first
						String ancestorId = getParentIdFromTerminologyBrowser(component.getId());
						
						// Look in CDO directly if the approach fails
						if (null == ancestorId) {
							ancestorId = getParentId(component);
						}
						
						if (null != ancestorId) {
							
							//changed component cannot have detached parent
							C ancestor = null;
							
							//we do not make difference between parent, we just use the first
							//this part is required for mapping type reference set membership
							final Collection<C> ancestors = get(ancestorId);
							
							if (!CompareUtils.isEmpty(ancestors)) {
								
								ancestor = Iterables.get(ancestors, 0);
								
							}
							
							if (null == ancestor) { //if ancestor is not among the changed objects
									
								//lookup via index, put to the change set
								ancestor = buildDelta(getAncestorEntry(ancestorId));
								put(ancestor);
								
							}
								
							//hook up parentage
							ancestor.getChildren().add(component);
							component.setParent(ancestor);
							
							//keep building
							component = ancestor;
							_stop = false;
							
						} else {
							
							//break loop
							_stop = true; 
							
						}

					}

				}
				
			}
			
		}
		
		super.postProcess();
	}

	protected final AbstractIndexEntry getAncestorEntry(final String ancestorId) {
		
		final AbstractIndexEntry result = getTerminologyBrowser().getConcept(getBranchPath(), ancestorId);
		
		if (null != result) {
			return result;
		}
		
		if (isTerminologyAvailable()) {
			return null;
		}
		
		return createAncestorEntryFromCdoObject(ancestorId);
	}

	protected abstract AbstractIndexEntry createAncestorEntryFromCdoObject(final String ancestorId);

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractComponentDeltaBuilder#processNew(org.eclipse.emf.cdo.common.revision.CDOIDAndVersion)
	 */
	@Override
	protected void processNew(final CDOIDAndVersion idAndVersion) {
		processChange(idAndVersion, getCurrentView(), ChangeKind.ADDED);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractComponentDeltaBuilder#processDirty(org.eclipse.emf.cdo.common.revision.CDORevisionKey)
	 */
	@Override
	protected void processDirty(final CDORevisionKey revisionKey) {
		processChange(revisionKey, getCurrentView(), ChangeKind.UPDATED);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractComponentDeltaBuilder#processDetached(org.eclipse.emf.cdo.common.revision.CDOIDAndVersion)
	 */
	@Override
	protected void processDetached(final CDOIDAndVersion idAndVersion) {
		processChange(idAndVersion, getBaseView(), ChangeKind.DELETED);
	}
	
	/**Processes the changed object given by the CDO ID. Clients can make sure that object should be available in the specified view.*/
	protected abstract void processChange(final CDOIDAndVersion idAndVersion, final CDOView view, final ChangeKind change);
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.AbstractHierarchicalComponentDeltaBuilder#getParentId(com.b2international.snowowl.datastore.delta.HierarchicalComponentDelta)
	 */
	protected final String getParentId(final C delta) {
		
		if (delta.isDeleted()) {
			
			final CDOObject cdoObject = CDOUtils.getObjectIfExists(getBaseView(), CDOIDUtil.createLong(delta.getCdoId()));
			return getParentIdFromCdoObject(cdoObject);
		
		} else if (!isTerminologyAvailable()) {
			
			final CDOObject cdoObject = CDOUtils.getObjectIfExists(getCurrentView(), CDOIDUtil.createLong(delta.getCdoId()));
			return getParentIdFromCdoObject(cdoObject);
		
		} else {			
			
			return null;
		}
 	}

	protected abstract AbstractLookupService<String, ? extends CDOObject, CDOView> createLookupService();

	protected abstract String getParentIdFromCdoObject(final CDOObject cdoObject);

	protected String getParentIdFromTerminologyBrowser(String id) {
		final Collection<String> ancestorIds = getTerminologyBrowser().getSuperTypeIds(getBranchPath(), id);
		return CompareUtils.isEmpty(ancestorIds) ? null : ancestorIds.iterator().next();
	}
	
	/**Returns with the terminology specific terminology browser service.*/
	protected abstract ITerminologyBrowser<? extends AbstractIndexEntry, String> getTerminologyBrowser();

	/**Returns with the application specific terminology component ID for the concrete builder.*/
	protected abstract short getTerminologyComponentId();
	
	/**Builds the delta based on the specified component.*/
	@SuppressWarnings("unchecked")
	protected <CC extends AbstractIndexEntry> C buildDelta(final CC component) {
		
		short terminologyComponentId = getTerminologyComponentId();
		return (C) new HierarchicalComponentDelta(
				component.getId(), 
				component.getStorageKey(),
				getBranchPath(),
				component.getLabel(),
				component.getIconId(),
				terminologyComponentId,
				getCodeSystemOID(terminologyComponentId), //TODO: revisit!!!
				ChangeKind.UNCHANGED);
		
	}

	
	/**
	 * Returns {@code true} if the component is a root element in the terminology.
	 * @param component the terminology independent component.
	 * @return {@code true} if the component is a root element, otherwise {@code false}.
	 */
	protected boolean isRoot(final C component) {
		return rootConceptIdSupplier.get().contains(component.getId());
	}
}